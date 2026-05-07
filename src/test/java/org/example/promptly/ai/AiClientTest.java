package org.example.promptly.ai;

import com.github.tomakehurst.wiremock.stubbing.Scenario;
import org.example.promptly.model.ChatMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

import org.wiremock.spring.EnableWireMock;

import java.util.List;

@SpringBootTest(properties = {
        "ai.api-key=test-key",
        "ai.base-url=${wiremock.server.baseUrl}"
})
@EnableWireMock
class AiClientTest {

    @Autowired
    private AiClient aiClient;

    private static final String SUCCESS_BODY = """
            {
              "choices": [{
                "message": {
                  "role": "assistant",
                  "content": "Stay classy, San Diego!"
                }
              }]
            }
            """;

    @Test
    void shouldRetryAndSucceedOnThirdAttempt() {
        stubFor(post(urlEqualTo("/chat/completions"))
                .inScenario("Retry Scenario")
                .whenScenarioStateIs(Scenario.STARTED)
                .willReturn(aResponse().withStatus(429))
                .willSetStateTo("First Failure"));

        stubFor(post(urlEqualTo("/chat/completions"))
                .inScenario("Retry Scenario")
                .whenScenarioStateIs("First Failure")
                .willReturn(aResponse().withStatus(429))
                .willSetStateTo("Second Failure"));

        stubFor(post(urlEqualTo("/chat/completions"))
                .inScenario("Retry Scenario")
                .whenScenarioStateIs("Second Failure")
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(SUCCESS_BODY)));

        List<ChatMessage> messages = List.of(new ChatMessage("user", "Stay classy!"));
        String result = aiClient.generateReply(messages);

        assertThat(result)
                .as("Should succeed after two retries")
                .isEqualTo("Stay classy, San Diego!");

        verify(3, postRequestedFor(urlEqualTo("/chat/completions")));
    }

    @Test
    void shouldOpenCircuitBreakerAfterRepeatedFailures() throws InterruptedException {
        stubFor(post(urlEqualTo("/chat/completions"))
                .willReturn(aResponse().withStatus(429)));

        List<ChatMessage> messages = List.of(new ChatMessage("user", "Stay classy!"));

        // Fyll fönstret med fel
        for (int i = 0; i < 10; i++) {
            try {
                aiClient.generateReply(messages);
            } catch (Exception _) {
            }
        }

        // Circuit Breaker bör vara OPEN
        resetAllRequests();
        try {
            aiClient.generateReply(messages);
        } catch (Exception _) {
        }

        verify(0, postRequestedFor(urlEqualTo("/chat/completions")));

        // Vänta tills Circuit Breaker blir HALF-OPEN
        Thread.sleep(6000);

        stubFor(post(urlEqualTo("/chat/completions"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(SUCCESS_BODY)));

        String result = aiClient.generateReply(messages);
        assertThat(result).isEqualTo("Stay classy, San Diego!");
    }
}
