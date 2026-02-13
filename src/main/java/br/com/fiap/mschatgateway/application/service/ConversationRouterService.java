package br.com.fiap.mschatgateway.application.service;

import br.com.fiap.mschatgateway.application.text.ChatTexts;
import br.com.fiap.mschatgateway.domain.model.conversation.ChatStep;
import br.com.fiap.mschatgateway.domain.model.conversation.ConversationState;
import br.com.fiap.mschatgateway.domain.model.pharmacy.Medication;
import br.com.fiap.mschatgateway.domain.model.pharmacy.MedicationType;
import br.com.fiap.mschatgateway.domain.model.pharmacy.PharmacyInfo;
import br.com.fiap.mschatgateway.domain.model.pharmacy.PharmacyStock;
import br.com.fiap.mschatgateway.domain.ports.outbound.PharmacyServicePort;
import br.com.fiap.mschatgateway.domain.ports.outbound.RegionCatalogPort;
import br.com.fiap.mschatgateway.domain.ports.outbound.SendMessagePort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static br.com.fiap.mschatgateway.domain.model.conversation.ChatStep.SELECT_STATE;

@Service
public class ConversationRouterService {

    private final SendMessagePort messagePort;
    private final PharmacyServicePort pharmacyPort;
    private final RegionCatalogPort regionPort;

    private final Map<String, ConversationState> sessions = new ConcurrentHashMap<>();

    public ConversationRouterService(
            SendMessagePort messagePort,
            PharmacyServicePort pharmacyPort,
            RegionCatalogPort regionPort
    ) {
        this.messagePort = messagePort;
        this.pharmacyPort = pharmacyPort;
        this.regionPort = regionPort;
    }

    public void handle(String userId, String input) {

        ConversationState state =
                sessions.computeIfAbsent(userId, ConversationState::new);

        String normalized = input == null ? "" : input.trim();

        switch (state.getStep()) {

            case START -> start(state);
            case MENU -> handleMenu(state, normalized);

            case SELECT_STATE -> handleState(state, normalized);
            case SELECT_CITY -> handleCity(state, normalized);
            case SELECT_NEIGHBORHOOD -> handleNeighborhood(state, normalized);

            case SHOW_PHARMACIES -> handleShowPharmacies(state);
            case NO_PHARMACIES -> handleNoPharmacies(state, normalized);


            case ASK_SEARCH_MEDICATION -> handleAskSearchMedication(state, normalized);
            case SELECT_MEDICATION_TYPE -> handleMedicationType(state, normalized);
            case SELECT_MEDICATION -> handleMedication(state, normalized);

            case SHOW_RESULT -> showResult(state);
            case ASK_VIEW_OTHER_PHARMACIES -> handleAskViewOtherPharmacies(state, normalized);

            case ASK_FEEDBACK -> handleFeedback(state, normalized);
            case ASK_END -> handleAskEnd(state, normalized);
            case END -> end(state);
        }
    }

    private Integer parseOptionIndexOrInvalid(ConversationState state, String input, String titleToRepeat) {
        List<String> options = state.getCurrentOptions();

        int index;
        try {
            index = Integer.parseInt(input) - 1;
        } catch (NumberFormatException e) {
            sendInvalidAndRepeat(state, titleToRepeat);
            return null;
        }

        if (options == null || index < 0 || index >= options.size()) {
            sendInvalidAndRepeat(state, titleToRepeat);
            return null;
        }

        return index;
    }

    private void sendInvalidAndRepeat(ConversationState state, String titleToRepeat) {
        messagePort.sendText(state.getUserId(), ChatTexts.INVALID_OPTION);
        messagePort.sendOptions(state.getUserId(), titleToRepeat, state.getCurrentOptions());
    }

    private void start(ConversationState state) {

        messagePort.sendText(state.getUserId(), ChatTexts.WELCOME);

        List<String> menu = List.of(
                "Encontrar farmácias do Farmácia Popular",
                "Buscar medicamento",
                "Sobre o programa"
        );

        state.setCurrentOptions(menu);
        messagePort.sendOptions(state.getUserId(), null, menu);
        state.setStep(ChatStep.MENU);
    }

    private void handleMenu(ConversationState state, String input) {

        Integer idx = parseOptionIndexOrInvalid(state, input, "Menu Principal");
        if (idx == null) return;

        String selected = state.getCurrentOptions().get(idx);

        if (selected.startsWith("Encontrar")) {
            state.setInitialFlow(ChatStep.SHOW_PHARMACIES);

            messagePort.sendText(
                    state.getUserId(),
                    ChatTexts.START_FIND_PHARMACIES
            );
        }

        else if (selected.startsWith("Buscar")) {
            state.setInitialFlow(ChatStep.SELECT_MEDICATION_TYPE);

            messagePort.sendText(
                    state.getUserId(),
                    ChatTexts.START_FIND_MEDICATION
            );
        }

        else if (selected.startsWith("Sobre")) {
            messagePort.sendText(state.getUserId(), ChatTexts.ABOUT_PROGRAM);
            askFeedback(state);
            return;
        }

        List<String> states = regionPort.getStates();
        state.setCurrentOptions(states);

        messagePort.sendOptions(state.getUserId(), "Selecione o Estado", states);
        state.setStep(SELECT_STATE);
    }


    private void handleState(ConversationState state, String input) {

        Integer idx = parseOptionIndexOrInvalid(state, input, "Selecione o Estado");
        if (idx == null) return;

        state.setState(state.getCurrentOptions().get(idx));

        List<String> cities = regionPort.getCities(state.getState());
        state.setCurrentOptions(cities);

        messagePort.sendText(state.getUserId(), ChatTexts.SELECT_CITY);
        messagePort.sendOptions(state.getUserId(), "Selecione a Cidade", cities);

        state.setStep(ChatStep.SELECT_CITY);
    }

    private void handleCity(ConversationState state, String input) {

        Integer idx = parseOptionIndexOrInvalid(state, input, "Selecione a Cidade");
        if (idx == null) return;

        state.setCity(state.getCurrentOptions().get(idx));

        List<String> neighborhoods =
                regionPort.getNeighborhoods(state.getState(), state.getCity());
        state.setCurrentOptions(neighborhoods);

        messagePort.sendText(state.getUserId(), ChatTexts.SELECT_NEIGHBORHOOD);
        messagePort.sendOptions(state.getUserId(), "Selecione o Bairro", neighborhoods);

        state.setStep(ChatStep.SELECT_NEIGHBORHOOD);
    }

    private void handleNeighborhood(ConversationState state, String input) {

        Integer idx = parseOptionIndexOrInvalid(state, input, "Selecione o Bairro");
        if (idx == null) return;

        state.setNeighborhood(state.getCurrentOptions().get(idx));

        if (state.getInitialFlow() == ChatStep.SHOW_PHARMACIES) {
            state.setStep(ChatStep.SHOW_PHARMACIES);
            handleShowPharmacies(state);
            return;
        }

        if (state.getInitialFlow() == ChatStep.SELECT_MEDICATION_TYPE) {

            messagePort.sendText(state.getUserId(), ChatTexts.FOUND_PHARMACIES_FOR_MEDICATION);

            List<MedicationType> types = pharmacyPort.getMedicationTypes();

            state.setMedicationTypes(types);

            List<String> options = types.stream()
                    .map(MedicationType::getDescription)
                    .toList();

            state.setCurrentOptions(options);

            messagePort.sendText(state.getUserId(), ChatTexts.SELECT_MEDICATION_TYPE);
            messagePort.sendOptions(state.getUserId(), "Tipo de Medicamento", options);

            state.setStep(ChatStep.SELECT_MEDICATION_TYPE);
        }
    }


    private void handleShowPharmacies(ConversationState state) {

        var pharmacies = pharmacyPort.getPharmacies(
                state.getState(),
                state.getCity(),
                state.getNeighborhood()
        );

        if (pharmacies == null || pharmacies.isEmpty()) {

            messagePort.sendText(state.getUserId(), ChatTexts.NO_PHARMACIES_FOUND);

            List<String> options = List.of(
                    "Informar outro endereço",
                    "Voltar ao menu principal",
                    "Encerrar conversa"
            );

            state.setCurrentOptions(options);
            messagePort.sendOptions(state.getUserId(), null, options);

            state.setStep(ChatStep.NO_PHARMACIES);
            return;
        }

        StringBuilder message = new StringBuilder(ChatTexts.FOUND_PHARMACIES);

        for (var pharmacy : pharmacies) {
            message.append("\n")
                    .append(pharmacy.getName()).append("\n")
                    .append("📍 ").append(pharmacy.getAddress()).append("\n");
        }

        messagePort.sendText(state.getUserId(), message.toString());
        messagePort.sendText(state.getUserId(), ChatTexts.ASK_SEARCH_MEDICATION);

        List<String> options = List.of("Sim, buscar medicamento", "Não, obrigado");
        state.setCurrentOptions(options);

        messagePort.sendOptions(state.getUserId(), null, options);
        state.setStep(ChatStep.ASK_SEARCH_MEDICATION);
    }

    private void handleNoPharmacies(ConversationState state, String input) {

        Integer idx = parseOptionIndexOrInvalid(state, input, null);
        if (idx == null) return;

        String selected = state.getCurrentOptions().get(idx);

        if (selected.startsWith("Informar")) {
            List<String> states = regionPort.getStates();
            state.setCurrentOptions(states);

            messagePort.sendOptions(state.getUserId(), "Selecione o Estado", states);
            state.setStep(SELECT_STATE);
            return;
        }

        if (selected.startsWith("Voltar")) {
            messagePort.sendText(state.getUserId(), ChatTexts.BACK_TO_MENU);
            state.setStep(ChatStep.START);
            start(state);
            return;
        }

        state.setStep(ChatStep.END);
        end(state);
    }

    private void handleAskSearchMedication(ConversationState state, String input) {

        Integer idx = parseOptionIndexOrInvalid(state, input, "Tipo de Medicamento");
        if (idx == null) return;

        if (state.getCurrentOptions().get(idx).startsWith("Não")) {
            askFeedback(state);
            return;
        }

        List<MedicationType> types = pharmacyPort.getMedicationTypes();

        state.setMedicationTypes(types);

        List<String> options = types.stream()
                .map(MedicationType::getDescription)
                .toList();

        state.setCurrentOptions(options);

        messagePort.sendText(state.getUserId(), ChatTexts.SELECT_MEDICATION_TYPE);
        messagePort.sendOptions(state.getUserId(), "Selecione tipo de Medicamento", options);

        state.setStep(ChatStep.SELECT_MEDICATION_TYPE);
    }

    private void handleMedicationType(ConversationState state, String input) {

        Integer idx = parseOptionIndexOrInvalid(state, input, "Tipo de Medicamento");
        if (idx == null) return;

        MedicationType selectedType = state.getMedicationTypes().get(idx);

        List<Medication> meds =
                pharmacyPort.getMedicationsByType(selectedType.getIdTypeMed());

        state.setMedications(meds);

        List<String> options = meds.stream()
                .map(Medication::getDescription)
                .toList();

        state.setCurrentOptions(options);

        messagePort.sendText(state.getUserId(), ChatTexts.SELECT_MEDICATION);
        messagePort.sendOptions(state.getUserId(), "Selecione o Medicamento", options);

        state.setStep(ChatStep.SELECT_MEDICATION);
    }


    private void handleMedication(ConversationState state, String input) {

        Integer idx = parseOptionIndexOrInvalid(state, input, "Selecione o Medicamento");
        if (idx == null) return;

        var selectedMed = state.getMedications().get(idx);

        state.setMedicationId(selectedMed.getIdMed());
        state.setMedication(selectedMed.getDescription());

        state.setStep(ChatStep.SHOW_RESULT);
        showResult(state);
    }

    private void handleAskEnd(ConversationState state, String input) {

        Integer idx = parseOptionIndexOrInvalid(state, input, null);
        if (idx == null) return;

        String selected = state.getCurrentOptions().get(idx);

        if (selected.startsWith("Encerrar")) {
            state.setStep(ChatStep.END);
            end(state);
            return;
        }

        messagePort.sendText(state.getUserId(), ChatTexts.BACK_TO_MENU);

        List<String> menu = List.of(
                "Encontrar farmácias do Farmácia Popular",
                "Buscar medicamento",
                "Sobre o programa"
        );

        state.setCurrentOptions(menu);
        messagePort.sendOptions(state.getUserId(), null, menu);
        state.setStep(ChatStep.MENU);
    }


    private void showResult(ConversationState state) {

        if (state.getInitialFlow() == ChatStep.SHOW_PHARMACIES) {
            showAllPharmaciesWithStockInfo(state);
            return;
        }

        var allPharmacies = pharmacyPort.getPharmacies(
                state.getState(),
                state.getCity(),
                state.getNeighborhood()
        );

        var stockResult = pharmacyPort.getMedicationAvailability(
                state.getState(),
                state.getCity(),
                state.getNeighborhood(),
                state.getMedicationId()
        );

        var confirmedPharmacies = filterConfirmedPharmacies(allPharmacies, stockResult);

        if (confirmedPharmacies.isEmpty()) {
            handleNoConfirmedStock(state);
            return;
        }

        sendMedicationResult(state, confirmedPharmacies, allPharmacies.size());
    }

    private void sendMedicationResult(
            ConversationState state,
            List<PharmacyStock> confirmedPharmacies,
            int totalPharmacies
    ) {

        StringBuilder message = new StringBuilder(
                ChatTexts.MEDICATION_RESULT_HEADER
                        .formatted(state.getMedication())
        );

        for (var stock : confirmedPharmacies) {

            if (state.getHistoryUuid() == null && stock.getHistoryUuid() != null) {
                state.setHistoryUuid(stock.getHistoryUuid());
            }

            message.append("\n")
                    .append(stock.getPharmacyName()).append("\n")
                    .append("📍 ").append(stock.getAddress()).append("\n")
                    .append("Disponibilidade: ")
                    .append(stock.getAvailability()).append("\n");
        }

        messagePort.sendText(state.getUserId(), message.toString());

        if (confirmedPharmacies.size() < totalPharmacies) {
            askViewOtherPharmacies(state);
            return;
        }

        askFeedback(state);
    }


    private List<PharmacyStock> filterConfirmedPharmacies(
            List<PharmacyInfo> allPharmacies,
            List<PharmacyStock> stockResult
    ) {
        return stockResult.stream()
                .filter(stock ->
                        allPharmacies.stream()
                                .anyMatch(p ->
                                        p.getName().equalsIgnoreCase(
                                                stock.getPharmacyName()
                                        )
                                )
                )
                .toList();
    }


    private void handleNoConfirmedStock(ConversationState state) {

        messagePort.sendText(
                state.getUserId(),
                ChatTexts.NO_CONFIRMED_STOCK
                        .formatted(state.getMedication())
        );

        askViewOtherPharmacies(state);
    }

    private void askViewOtherPharmacies(ConversationState state) {

        messagePort.sendText(
                state.getUserId(),
                ChatTexts.ASK_VIEW_OTHER_PHARMACIES
        );

        state.setCurrentOptions(List.of(
                "Sim, visualizar farmácias da região",
                "Não"
        ));

        messagePort.sendOptions(
                state.getUserId(),
                null,
                state.getCurrentOptions()
        );

        state.setStep(ChatStep.ASK_VIEW_OTHER_PHARMACIES);
    }


    private void showAllPharmaciesWithStockInfo(ConversationState state) {

        var allPharmacies = pharmacyPort.getPharmacies(
                state.getState(),
                state.getCity(),
                state.getNeighborhood()
        );

        var stockResult = pharmacyPort.getMedicationAvailability(
                state.getState(),
                state.getCity(),
                state.getNeighborhood(),
                state.getMedicationId()
        );

        StringBuilder message = new StringBuilder();
        message.append(ChatTexts.MEDICATION_RESULT_HEADER
                .formatted(state.getMedication()));

        for (var pharmacy : allPharmacies) {

            message.append("\n")
                    .append(pharmacy.getName()).append("\n")
                    .append("📍 ").append(pharmacy.getAddress()).append("\n");

            var stock = stockResult.stream()
                    .filter(s -> s.getPharmacyName()
                            .equalsIgnoreCase(pharmacy.getName()))
                    .findFirst();

            if (stock.isPresent()) {

                if (state.getHistoryUuid() == null &&
                        stock.get().getHistoryUuid() != null) {

                    state.setHistoryUuid(stock.get().getHistoryUuid());
                }

                message.append("Disponibilidade: ")
                        .append(stock.get().getAvailability())
                        .append("\n");

            } else {
                message.append(ChatTexts.STOCK_NOT_CONFIRMED);
            }

            message.append("\n");
        }

        messagePort.sendText(state.getUserId(), message.toString());
        askFeedback(state);
    }

    private void handleAskViewOtherPharmacies(ConversationState state, String input) {

        Integer idx = parseOptionIndexOrInvalid(state, input, null);
        if (idx == null) return;

        String selected = state.getCurrentOptions().get(idx);

        if (selected.startsWith("Não")) {
            askFeedback(state);
            return;
        }

        var allPharmacies = pharmacyPort.getPharmacies(
                state.getState(),
                state.getCity(),
                state.getNeighborhood()
        );

        StringBuilder message = new StringBuilder();

        for (var pharmacy : allPharmacies) {

            message.append("\n").append(pharmacy.getName()).append("\n").append("📍 ").append(pharmacy.getAddress()).append("\n");
        }

        messagePort.sendText(state.getUserId(), message.toString());

        askFeedback(state);
    }

    private void askFeedback(ConversationState state) {

        messagePort.sendText(state.getUserId(), ChatTexts.ASK_FEEDBACK);

        List<String> options = List.of("Sim", "Não");
        state.setCurrentOptions(options);

        messagePort.sendOptions(state.getUserId(), null, options);
        state.setStep(ChatStep.ASK_FEEDBACK);
    }

    private void handleFeedback(ConversationState state, String input) {

        Integer idx = parseOptionIndexOrInvalid(state, input, null);
        if (idx == null) return;

        boolean flagFeedback = idx == 0;

        if (state.getHistoryUuid() != null) {
            pharmacyPort.updateFeedback(state.getHistoryUuid(), flagFeedback);
        }

        askEnd(state);
    }

    private void askEnd(ConversationState state) {

        messagePort.sendText(
                state.getUserId(),
                "Deseja encerrar a conversa?"
        );

        List<String> options = List.of(
                "Encerrar conversa",
                "Voltar ao menu principal"
        );

        state.setCurrentOptions(options);
        messagePort.sendOptions(state.getUserId(), null, options);
        state.setStep(ChatStep.ASK_END);
    }

    private void end(ConversationState state) {
        messagePort.sendText(state.getUserId(), ChatTexts.END);
        sessions.remove(state.getUserId());
    }
}

