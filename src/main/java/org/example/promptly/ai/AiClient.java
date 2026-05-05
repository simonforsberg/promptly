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
            throw new RuntimeException("Invalid AI response");
        }
        return response.choices().getFirst().message().content();
    }
}
