package br.com.fiap.mschatgateway.domain.ports.outbound;


import java.util.List;

public interface RegionCatalogPort {

    List<String> getStates();

    List<String> getCities(String state);

    List<String> getNeighborhoods(String state, String city);

}

