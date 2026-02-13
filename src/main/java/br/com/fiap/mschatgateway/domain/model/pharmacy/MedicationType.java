package br.com.fiap.mschatgateway.domain.model.pharmacy;

public class MedicationType {

    private final String idTypeMed;
    private final String description;

    public MedicationType(Long idTypeMed, String description) {
        this.idTypeMed = String.valueOf(idTypeMed);
        this.description = description;
    }

    public String getIdTypeMed() {
        return idTypeMed;
    }

    public String getDescription() {
        return description;
    }
}


