package br.com.fiap.mschatgateway.adapters.outbound.backend.dto;

public class MedicationResponse {

    private String medicineCode;
    private String medicineName;

    public String getMedicineCode() { return medicineCode; }
    public String getMedicineName() { return medicineName; }

    public void setMedicineCode(String medicineCode) { this.medicineCode = medicineCode; }
    public void setMedicineName(String medicineName) { this.medicineName = medicineName; }
}

