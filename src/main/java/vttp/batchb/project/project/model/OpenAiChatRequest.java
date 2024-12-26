package vttp.batchb.project.project.model;

import java.util.List;

public class OpenAiChatRequest {
    private String model;
    private List<OpenAiMessage> messages;
    private Double temperature;

    public String getModel() {
        return model;
    }
    public void setModel(String model) {
        this.model = model;
    }

    public List<OpenAiMessage> getMessages() {
        return messages;
    }
    public void setMessages(List<OpenAiMessage> messages) {
        this.messages = messages;
    }

    public Double getTemperature() {
        return temperature;
    }
    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }
}