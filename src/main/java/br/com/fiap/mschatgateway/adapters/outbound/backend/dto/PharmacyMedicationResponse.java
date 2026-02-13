package br.com.fiap.mschatgateway.adapters.outbound.backend.dto;

public class PharmacyMedicationResponse {

    private String medicineCode;
    private String medicineName;
    private Integer quantity;
    private String stockStatus; // se vier como enum no backend pode mapear como String mesmo

    private PharmacyUnitResponse pharmacyUnit;

    public String getMedicineCode() { return medicineCode; }
    public void setMedicineCode(String medicineCode) { this.medicineCode = medicineCode; }

    public String getMedicineName() { return medicineName; }
    public void setMedicineName(String medicineName) { this.medicineName = medicineName; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public String getStockStatus() { return stockStatus; }
    public void setStockStatus(String stockStatus) { this.stockStatus = stockStatus; }

    public PharmacyUnitResponse getPharmacyUnit() { return pharmacyUnit; }
    public void setPharmacyUnit(PharmacyUnitResponse pharmacyUnit) {
        this.pharmacyUnit = pharmacyUnit;
    }
}


