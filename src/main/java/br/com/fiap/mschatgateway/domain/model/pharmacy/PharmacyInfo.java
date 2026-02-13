package br.com.fiap.mschatgateway.domain.model.pharmacy;

public class PharmacyInfo {

    private final String name;
    private final String address;

    public PharmacyInfo(String name, String address) {
        this.name = name;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }
}


