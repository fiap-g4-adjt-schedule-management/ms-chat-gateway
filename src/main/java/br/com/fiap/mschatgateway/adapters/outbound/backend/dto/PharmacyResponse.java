package br.com.fiap.mschatgateway.adapters.outbound.backend.dto;

public class PharmacyResponse {

    private String name;
    private String address;
    private String neighborhood;
    private String city;
    private String state;

    public PharmacyResponse() {
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getNeighborhood() {
        return neighborhood;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setNeighborhood(String neighborhood) {
        this.neighborhood = neighborhood;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setState(String state) {
        this.state = state;
    }
}

