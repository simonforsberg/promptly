package org.example.promptly.ai;

import com.github.tomakehurst.wiremock.stubbing.Scenario;
import org.example.promptly.model.ChatMessage;
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
}
