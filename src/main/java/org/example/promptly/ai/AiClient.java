package org.example.promptly.ai;

import lombok.RequiredArgsConstructor;
import org.example.promptly.model.ChatMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AiClient {

    private final RestClient restClient;

    @Value("${ai.model}")
    private String model;

    public String generateReply(List<ChatMessage> messages) {
        AiApiRequest request = new AiApiRequest(model, messages);

        AiApiResponse response = restClient.post()
                .uri("/chat/completions")
                .body(request)
                .retrieve()
                .body(AiApiResponse.class);

        return extractContent(response);
    }

    private String extractContent(AiApiResponse response) {
        if (response == null || response.choices() == null || response.choices().isEmpty()) {
            throw new RuntimeException("No choices in AI response");
        }
        AiApiResponse.Choice firstChoice = response.choices().getFirst();
        if (firstChoice.message() == null || firstChoice.message().content() == null) {
            throw new RuntimeException("Missing message or content in AI response");
        }
        if (firstChoice.message().content().isBlank()) {
            throw new RuntimeException("AI response content is blank");
        }
        return firstChoice.message().content();
    }
}
