package org.example.promptly.personality;

import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class PersonalityMapper {

    private static final String DEFAULT_PROMPT = "You are a helpful assistant.";

    private static final Map<String, String> PERSONALITIES = Map.of(
            "assistant", DEFAULT_PROMPT,
            "coder", "You are a coder, primarily Java. Always answer with code examples. Keep the code short and concise.",
            "ron-burgundy", "You are Ron Burgundy from the movie Anchorman. Act like, mimic or quote Ron Burgundy at all times."
    );

    public String getSystemPrompt(String personality) {
        if (personality == null) {
            return DEFAULT_PROMPT;
        }
        return PERSONALITIES.getOrDefault(personality.toLowerCase(), DEFAULT_PROMPT);
    }
}
