package br.com.fiap.mschatgateway.domain.model;

import java.util.List;

public class ConversationState {

    private final String userId;
    private ChatStep step;

    private String state;
    private String city;
    private String neighborhood;
    private String medication;

    private ChatStep initialFlow;

    private List<String> currentOptions;

    public ConversationState(String userId) {
        this.userId = userId;
        this.step = ChatStep.START;
    }

    public String getUserId() {
        return userId;
    }

    public ChatStep getStep() {
        return step;
    }

    public void setStep(ChatStep step) {
        this.step = step;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getNeighborhood() {
        return neighborhood;
    }

    public void setNeighborhood(String neighborhood) {
        this.neighborhood = neighborhood;
    }

    public String getMedication() {
        return medication;
    }

    public void setMedication(String medication) {
        this.medication = medication;
    }

    public ChatStep getInitialFlow() {
        return initialFlow;
    }

    public void setInitialFlow(ChatStep initialFlow) {
        this.initialFlow = initialFlow;
    }

    public List<String> getCurrentOptions() {
        return currentOptions;
    }

    public void setCurrentOptions(List<String> currentOptions) {
        this.currentOptions = currentOptions;
    }
}



