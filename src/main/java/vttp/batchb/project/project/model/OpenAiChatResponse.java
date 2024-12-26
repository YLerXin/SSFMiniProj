package vttp.batchb.project.project.model;

import java.util.List;

public class OpenAiChatResponse {
    private List<Choice> choices;

    public List<Choice> getChoices() {
        return choices;
    }
    public void setChoices(List<Choice> choices) {
        this.choices = choices;
    }

    public static class Choice {
        private int index;
        private OpenAiMessage message;
        private String finish_reason;

        public int getIndex() {
            return index;
        }
        public void setIndex(int index) {
            this.index = index;
        }
        public OpenAiMessage getMessage() {
            return message;
        }
        public void setMessage(OpenAiMessage message) {
            this.message = message;
        }
        public String getFinish_reason() {
            return finish_reason;
        }
        public void setFinish_reason(String finish_reason) {
            this.finish_reason = finish_reason;
        }
    }
}