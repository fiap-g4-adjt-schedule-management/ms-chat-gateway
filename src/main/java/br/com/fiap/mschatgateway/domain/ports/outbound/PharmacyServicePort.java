package br.com.fiap.mschatgateway.domain.ports.outbound;

import br.com.fiap.mschatgateway.domain.model.pharmacy.Medication;
import br.com.fiap.mschatgateway.domain.model.pharmacy.MedicationType;
import br.com.fiap.mschatgateway.domain.model.pharmacy.PharmacyInfo;
import br.com.fiap.mschatgateway.domain.model.pharmacy.PharmacyStock;
import java.util.List;

public interface PharmacyServicePort {

    List<PharmacyInfo> getPharmacies(
            String state,
            String city,
            String neighborhood
    );

    List<PharmacyStock> getMedicationAvailability(
            String state,
            String city,
            String neighborhood,
            String medicationCode
    );

    List<MedicationType> getMedicationTypes();

    List<Medication> getMedicationsByType(String idTypeMed);

    void updateFeedback(String historyUuid, boolean flagFeedback);
}

