package br.com.fiap.mschatgateway.adapters.outbound.backend;

import br.com.fiap.mschatgateway.adapters.outbound.backend.dto.MedicationResponse;
import br.com.fiap.mschatgateway.adapters.outbound.backend.dto.MedicationTypeResponse;
import br.com.fiap.mschatgateway.adapters.outbound.backend.dto.PharmacyMedicationResponse;
import br.com.fiap.mschatgateway.adapters.outbound.backend.dto.PharmacyResponse;
import br.com.fiap.mschatgateway.domain.model.pharmacy.Medication;
import br.com.fiap.mschatgateway.domain.model.pharmacy.MedicationType;
import br.com.fiap.mschatgateway.domain.model.pharmacy.PharmacyInfo;
import br.com.fiap.mschatgateway.domain.model.pharmacy.PharmacyStock;
import br.com.fiap.mschatgateway.domain.ports.outbound.PharmacyServicePort;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Component
@Profile("backend")
public class PharmacyServiceAdapter implements PharmacyServicePort {

    private final WebClient webClient;

    public PharmacyServiceAdapter(WebClient pharmacyWebClient) {
        this.webClient = pharmacyWebClient;
    }


    @Override
    public List<PharmacyInfo> getPharmacies(
            String state,
            String city,
            String neighborhood
    ) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/pharmacys")
                        .queryParam("state", state)
                        .queryParam("city", city)
                        .queryParam("neighb", neighborhood)
                        .build())
                .retrieve()
                .bodyToFlux(PharmacyResponse.class)
                .map(response ->
                        new PharmacyInfo(
                                response.getName(),
                                response.getAddress()
                        )
                )
                .collectList()
                .block();
    }


    @Override
    public List<MedicationType> getMedicationTypes() {

        return webClient.get()
                .uri("/medication")
                .retrieve()
                .bodyToFlux(MedicationTypeResponse.class)
                .map(resp ->
                        new MedicationType(
                                resp.getId(),
                                resp.getIndication()
                        )
                )
                .collectList()
                .block();
    }


    @Override
    public List<Medication> getMedicationsByType(String idTypeMed) {

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/medication")
                        .queryParam("idTypeMed", idTypeMed)
                        .build())
                .retrieve()
                .bodyToFlux(MedicationResponse.class)
                .map(resp ->
                        new Medication(
                                resp.getMedicineCode(),
                                resp.getMedicineName()
                        )
                )
                .collectList()
                .block();
    }


    @Override
    public List<PharmacyStock> getMedicationAvailability(
            String state,
            String city,
            String neighborhood,
            String medication
    ) {

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/medication/{code}/pharmacys")
                        .queryParam("state", state)
                        .queryParam("city", city)
                        .queryParam("neighb", neighborhood)
                        .build(medication))
                .retrieve()
                .bodyToFlux(PharmacyMedicationResponse.class)
                .map(response ->
                        new PharmacyStock(
                                response.getPharmacyUnit().getName(),
                                response.getPharmacyUnit().getAddress(),
                                response.getStockStatus(),
                                null
                        )
                )
                .collectList()
                .block();
    }

    @Override
    public void updateFeedback(String historyUuid, boolean flagFeedback) {

        webClient.put()
                .uri(uriBuilder -> uriBuilder
                        .path("/feedback/{uuid}")
                        .build(historyUuid))
                .bodyValue(Map.of("flagFeedback", flagFeedback))
                .retrieve()
                .toBodilessEntity()
                .block();
    }
}


