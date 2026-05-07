package org.example.promptly.ai;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import org.example.promptly.exception.ChatServiceException;
import org.example.promptly.exception.NonRetryableHttpException;
import org.example.promptly.exception.RetryableHttpException;
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

    @CircuitBreaker(name = "aiClient", fallbackMethod = "fallbackReply")
    @Retry(name = "aiClient")
    public String generateReply(List<ChatMessage> messages) {
        AiApiRequest request = new AiApiRequest(model, messages);

        AiApiResponse response = restClient.post()
                .uri("/chat/completions")
                .body(request)
                .retrieve()
                .onStatus(s -> s.value() == 429 || s.value() == 503,
                        (_, resp) -> {
                            throw new RetryableHttpException("Retryable HTTP error: " + resp.getStatusCode());
                        })
                .onStatus(s -> s.value() == 401 || s.value() == 400,
                        (_, resp) -> {
                            throw new NonRetryableHttpException("Non-retryable HTTP error: " + resp.getStatusCode());
                        })
                .body(AiApiResponse.class);

        return extractContent(response);
    }

    private String fallbackReply(List<ChatMessage> messages, Exception ex) {
        throw new ChatServiceException("AI service is currently unavailable. Please try again later.");
    }

    private String extractContent(AiApiResponse response) {
        if (response == null || response.choices() == null || response.choices().isEmpty()) {
            throw new ChatServiceException("No response from AI service");
        }
        AiApiResponse.Choice firstChoice = response.choices().getFirst();
        if (firstChoice.message() == null || firstChoice.message().content() == null) {
            throw new ChatServiceException("Invalid AI response structure");
        }
        if (firstChoice.message().content().isBlank()) {
            throw new ChatServiceException("AI returned empty response");
        }
        return firstChoice.message().content();
    }
}
