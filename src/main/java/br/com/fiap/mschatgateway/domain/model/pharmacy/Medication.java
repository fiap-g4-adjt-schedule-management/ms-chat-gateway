package br.com.fiap.mschatgateway.domain.model.pharmacy;

public class Medication {

    private final String idMed;
    private final String description;

    public Medication(String idMed, String description) {
        this.idMed = idMed;
        this.description = description;
    }

    public String getIdMed() {
        return idMed;
    }

    public String getDescription() {
        return description;
    }
}

