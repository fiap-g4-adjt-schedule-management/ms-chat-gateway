package br.com.fiap.mschatgateway.adapters.outbound.backend;

import br.com.fiap.mschatgateway.adapters.outbound.backend.dto.*;
import br.com.fiap.mschatgateway.domain.model.pharmacy.Medication;
import br.com.fiap.mschatgateway.domain.model.pharmacy.MedicationType;
import br.com.fiap.mschatgateway.domain.model.pharmacy.PharmacyInfo;
import br.com.fiap.mschatgateway.domain.model.pharmacy.PharmacyStock;
import br.com.fiap.mschatgateway.domain.ports.outbound.PharmacyServicePort;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@Profile({"backend", "twilio"})
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
                        .path("/pharmacy")
                        .queryParam("state", state)
                        .queryParam("city", city)
                        .queryParam("neighb", neighborhood)
                        .build())
                .retrieve()
                .onStatus(
                        status -> status.value() == 404,
                        response -> Mono.empty()
                )
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
                        .path("/medication/{id}")
                        .build(idTypeMed))
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

        try {
            return webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/medication/{code}/pharmacys")
                            .queryParam("state", state)
                            .queryParam("city", city)
                            .queryParam("neighb", neighborhood)
                            .build(medication))
                    .retrieve()
                    .bodyToFlux(PharmacyMedicationResponse.class)
                    .filter(response -> response.getPharmacyUnit() != null)
                    .map(response ->
                            new PharmacyStock(
                                    response.getPharmacyUnit().getName(),
                                    response.getPharmacyUnit().getAddress(),
                                    response.getStockStatus()
                            )
                    )
                    .collectList()
                    .block();

        } catch (WebClientResponseException.NotFound e) {
            return List.of();
        }
    }
}


