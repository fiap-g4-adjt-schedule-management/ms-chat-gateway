package br.com.fiap.mschatgateway.adapters.outbound.backend;

import br.com.fiap.mschatgateway.domain.ports.outbound.BackendValidationPort;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Profile({"local", "twilio"})
public class MockBackendValidationAdapter implements BackendValidationPort {

    @Override
    public List<String> getStates() {
        return List.of(
                "São Paulo",
                "Rio de Janeiro"
        );
    }

    @Override
    public List<String> getCities(String state) {
        return switch (state) {
            case "São Paulo" -> List.of("São Paulo", "Campinas", "Santos");
            case "Rio de Janeiro" -> List.of("Rio de Janeiro", "Niterói");
            default -> List.of("Cidade A", "Cidade B");
        };
    }

    @Override
    public List<String> getNeighborhoods(String city) {
        return switch (city) {
            case "São Paulo" -> List.of("Vila Mariana", "Pinheiros", "Moema");
            case "Rio de Janeiro" -> List.of("Copacabana", "Tijuca");
            default -> List.of("Centro");
        };
    }

    @Override
    public String getPharmacies(String state, String city, String neighborhood) {

        if (neighborhood == null) return "";

        return switch (neighborhood.trim().toLowerCase()) {
            case "vila mariana" -> """
            Farmácias encontradas em Vila Mariana:
            Drogaria São Paulo
            Drogasil
            """;

            case "pinheiros" -> """
            Farmácias encontradas em Pinheiros:
            Drogaria Pacheco
            Droga Raia
            """;

            default -> "";
        };
    }

    @Override
    public List<String> getMedicationTypes() {
        return List.of(
                "Hipertensão",
                "Diabetes",
                "Asma",
                "Colesterol"
        );
    }

    @Override
    public List<String> getMedicationsByType(String type) {
        return switch (type) {
            case "Hipertensão" -> List.of(
                    "Losartana 50mg",
                    "Enalapril 10mg",
                    "Captopril 25mg"
            );
            case "Diabetes" -> List.of(
                    "Metformina 500mg",
                    "Glibenclamida 5mg",
                    "Insulina NPH"
            );
            default -> List.of("Medicamento Genérico");
        };
    }

    @Override
    public String getMedicationAvailability(String medication, String neighborhood) {

        return """
        %s está disponível em %s:

        📍 Drogaria São Paulo
        ✓ Disponibilidade Baixa

        📍 Drogasil
        ✓ Disponibilidade Alta
        """.formatted(medication, neighborhood);
    }
}