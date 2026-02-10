package br.com.fiap.mschatgateway.domain.ports.outbound;

import java.util.List;

public interface BackendValidationPort {

    List<String> getStates();
    List<String> getCities(String state);
    List<String> getNeighborhoods(String city);

    String getPharmacies(String state, String city, String neighborhood);

    List<String> getMedicationTypes();
    List<String> getMedicationsByType(String type);

    String getMedicationAvailability(String medication, String neighborhood);
}



