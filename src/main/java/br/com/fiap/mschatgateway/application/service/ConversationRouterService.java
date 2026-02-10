package br.com.fiap.mschatgateway.application.service;

import br.com.fiap.mschatgateway.application.text.ChatTexts;
import br.com.fiap.mschatgateway.domain.model.ChatStep;
import br.com.fiap.mschatgateway.domain.model.ConversationState;
import br.com.fiap.mschatgateway.domain.ports.outbound.BackendValidationPort;
import br.com.fiap.mschatgateway.domain.ports.outbound.SendMessagePort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ConversationRouterService {

    private final SendMessagePort messagePort;
    private final BackendValidationPort backendPort;

    private final Map<String, ConversationState> sessions = new ConcurrentHashMap<>();

    public ConversationRouterService(
            SendMessagePort messagePort,
            BackendValidationPort backendPort
    ) {
        this.messagePort = messagePort;
        this.backendPort = backendPort;
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

        Integer idx = parseOptionIndexOrInvalid(state, input, null);
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

        List<String> states = backendPort.getStates();
        state.setCurrentOptions(states);

        messagePort.sendOptions(state.getUserId(), "Selecione o Estado", states);
        state.setStep(ChatStep.SELECT_STATE);
    }


    private void handleState(ConversationState state, String input) {

        Integer idx = parseOptionIndexOrInvalid(state, input, "Selecione o Estado");
        if (idx == null) return;

        state.setState(state.getCurrentOptions().get(idx));

        List<String> cities = backendPort.getCities(state.getState());
        state.setCurrentOptions(cities);

        messagePort.sendText(state.getUserId(), ChatTexts.SELECT_CITY);
        messagePort.sendOptions(state.getUserId(), "Selecione a Cidade", cities);

        state.setStep(ChatStep.SELECT_CITY);
    }

    private void handleCity(ConversationState state, String input) {

        Integer idx = parseOptionIndexOrInvalid(state, input, "Selecione a Cidade");
        if (idx == null) return;

        state.setCity(state.getCurrentOptions().get(idx));

        List<String> neighborhoods = backendPort.getNeighborhoods(state.getCity());
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

            messagePort.sendText(
                    state.getUserId(),
                    ChatTexts.FOUND_PHARMACIES_FOR_MEDICATION
            );

            List<String> types = backendPort.getMedicationTypes();
            state.setCurrentOptions(types);

            messagePort.sendText(state.getUserId(), ChatTexts.SELECT_MEDICATION_TYPE);
            messagePort.sendOptions(state.getUserId(), "Tipo de Medicamento", types);

            state.setStep(ChatStep.SELECT_MEDICATION_TYPE);
        }
    }

    private void handleShowPharmacies(ConversationState state) {

        String pharmacies = backendPort.getPharmacies(
                state.getState(),
                state.getCity(),
                state.getNeighborhood()
        );

        if (pharmacies == null || pharmacies.isBlank()) {

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

        messagePort.sendText(state.getUserId(), pharmacies);
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
            List<String> states = backendPort.getStates();
            state.setCurrentOptions(states);

            messagePort.sendOptions(state.getUserId(), "Selecione o Estado", states);
            state.setStep(ChatStep.SELECT_STATE);
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

        Integer idx = parseOptionIndexOrInvalid(state, input, null);
        if (idx == null) return;

        if (state.getCurrentOptions().get(idx).startsWith("Não")) {
            askFeedback(state);
            return;
        }

        List<String> types = backendPort.getMedicationTypes();
        state.setCurrentOptions(types);

        messagePort.sendText(state.getUserId(), ChatTexts.SELECT_MEDICATION_TYPE);
        messagePort.sendOptions(state.getUserId(), "Tipo de Medicamento", types);

        state.setStep(ChatStep.SELECT_MEDICATION_TYPE);
    }

    private void handleMedicationType(ConversationState state, String input) {

        Integer idx = parseOptionIndexOrInvalid(state, input, "Tipo de Medicamento");
        if (idx == null) return;

        List<String> meds = backendPort.getMedicationsByType(state.getCurrentOptions().get(idx));
        state.setCurrentOptions(meds);

        messagePort.sendText(state.getUserId(), ChatTexts.SELECT_MEDICATION);
        messagePort.sendOptions(state.getUserId(), "Selecione o Medicamento", meds);

        state.setStep(ChatStep.SELECT_MEDICATION);
    }

    private void handleMedication(ConversationState state, String input) {

        Integer idx = parseOptionIndexOrInvalid(state, input, "Selecione o Medicamento");
        if (idx == null) return;

        state.setMedication(state.getCurrentOptions().get(idx));
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

        messagePort.sendText(
                state.getUserId(),
                backendPort.getMedicationAvailability(
                        state.getMedication(),
                        state.getNeighborhood()
                )
        );

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

