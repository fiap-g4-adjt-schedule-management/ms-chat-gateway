package br.com.fiap.mschatgateway.domain.model.pharmacy;

public class PharmacyStock {

    private final String pharmacyName;
    private final String address;
    private final String availability;
    private final String historyUuid;

    public PharmacyStock(String pharmacyName,
                         String address,
                         String availability,
                         String historyUuid) {
        this.pharmacyName = pharmacyName;
        this.address = address;
        this.availability = availability;
        this.historyUuid = historyUuid;
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

    public String getHistoryUuid() {
        return historyUuid;
    }
}




