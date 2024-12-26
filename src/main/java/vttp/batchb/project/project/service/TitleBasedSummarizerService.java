package vttp.batchb.project.project.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import vttp.batchb.project.project.model.OpenAiChatRequest;
import vttp.batchb.project.project.model.OpenAiChatResponse;
import vttp.batchb.project.project.model.OpenAiMessage;
import java.util.List;

@Service
public class TitleBasedSummarizerService {

    @Value("${openai.api.key}")
    private String openAiApiKey;

    private static final String OPENAI_URL = "https://api.openai.com/v1/chat/completions";
    private final RestTemplate restTemplate = new RestTemplate();

    public String summarizeByTitle(String bookTitle) {
        OpenAiChatRequest requestBody = new OpenAiChatRequest();
        requestBody.setModel("gpt-4");
        requestBody.setTemperature(0.7);
        List<OpenAiMessage> messages = List.of(
            new OpenAiMessage("system",
               "You are a succinct, helpful summary bot with knowledge of classic literature."),
            new OpenAiMessage("user",
               "Please summarize the well-known book: '" + bookTitle + "'. Focus on main plot points, major themes, and key takeaways.")
        );
        requestBody.setMessages(messages);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(openAiApiKey);
        HttpEntity<OpenAiChatRequest> entity = new HttpEntity<>(requestBody, headers);
        try {
            ResponseEntity<OpenAiChatResponse> response =
                restTemplate.postForEntity(OPENAI_URL, entity, OpenAiChatResponse.class);
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                OpenAiChatResponse respBody = response.getBody();
                if (respBody.getChoices() != null && !respBody.getChoices().isEmpty()) {
                    return respBody.getChoices().get(0).getMessage().getContent();
                }
            }
        } catch (Exception e) {
            return "(Summary failed due to API error)";
        }
        return "(No summary returned)";
    }
}
