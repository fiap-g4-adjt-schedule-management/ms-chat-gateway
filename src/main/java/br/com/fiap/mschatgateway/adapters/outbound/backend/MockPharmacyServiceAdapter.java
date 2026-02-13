package br.com.fiap.mschatgateway.adapters.outbound.backend;

import br.com.fiap.mschatgateway.domain.model.pharmacy.Medication;
import br.com.fiap.mschatgateway.domain.model.pharmacy.MedicationType;
import br.com.fiap.mschatgateway.domain.model.pharmacy.PharmacyInfo;
import br.com.fiap.mschatgateway.domain.model.pharmacy.PharmacyStock;
import br.com.fiap.mschatgateway.domain.ports.outbound.PharmacyServicePort;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Profile("local")
public class MockPharmacyServiceAdapter implements PharmacyServicePort {

    @Override
    public List<PharmacyInfo> getPharmacies(String state, String city, String neighborhood) {
        return List.of(
                new PharmacyInfo("RAIA DROGASIL S/A", "Av. Cassandoca, 84"),
                new PharmacyInfo("DROGARIA SÃO PAULO", "Rua Julio de Castilho, 950")
        );
    }

    @Override
    public List<MedicationType> getMedicationTypes() {
        return List.of(
                new MedicationType(1L, "Asma"),
                new MedicationType(2L, "Diabetes")
        );
    }

    @Override
    public List<Medication> getMedicationsByType(String idTypeMed) {

        if (idTypeMed.equals("1")) {
            return List.of(
                    new Medication("MED001", "Salbutamol"),
                    new Medication("MED002", "Aerolin")
            );
        }

        return List.of(
                new Medication("MED003", "Insulina")
        );
    }

    @Override
    public List<PharmacyStock> getMedicationAvailability(
            String state,
            String city,
            String neighborhood,
            String medicationId
    ) {
        return List.of(
                new PharmacyStock(
                        "RAIA DROGASIL S/A",
                        "Av. Cassandoca, 84",
                        "HIGH"
                ),
                new PharmacyStock(
                        "DROGARIA SÃO PAULO",
                        "Rua Julio de Castilho, 950",
                        "CRITICAL"
                )
        );
    }
}
