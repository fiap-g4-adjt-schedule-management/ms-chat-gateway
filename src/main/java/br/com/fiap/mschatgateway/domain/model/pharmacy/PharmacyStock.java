package br.com.fiap.mschatgateway.domain.model.pharmacy;

public class PharmacyStock {

    private final String pharmacyName;
    private final String address;
    private final String availability;

    public PharmacyStock(String pharmacyName,
                         String address,
                         String availability) {
        this.pharmacyName = pharmacyName;
        this.address = address;
        this.availability = availability;
    }

    public String getPharmacyName() {
        return pharmacyName;
    }

    public String getAddress() {
        return address;
    }

    public String getAvailability() {
        return availability;
    }

}




