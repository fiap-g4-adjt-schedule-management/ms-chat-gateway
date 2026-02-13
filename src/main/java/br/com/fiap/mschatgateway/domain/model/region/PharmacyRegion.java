package br.com.fiap.mschatgateway.domain.model.region;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PharmacyRegion {

    private String state;
    private String city;
    private String neighborhood;

    public PharmacyRegion() {}

    public String getState() { return state; }
    public String getCity() { return city; }
    public String getNeighborhood() { return neighborhood; }
}


