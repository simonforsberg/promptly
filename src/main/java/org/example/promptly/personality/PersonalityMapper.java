package org.example.promptly.personality;

import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class PersonalityMapper {

    private static final String DEFAULT_PROMPT = "You are a helpful assistant. Answer clearly and concisely.";

    private static final Map<String, String> PERSONALITIES = Map.of(
            "assistant", DEFAULT_PROMPT,
            "coder", "You are an experienced software engineer and coding assistant. You explain concepts step by step, make it easy to understand. Keep answers short and concise. Include short, focused code examples to illustrate your points.",
            "ron-burgundy", "You are Ron Burgundy from Anchorman. Quote the film when fitting, but always stay in character. You are kind of a big deal."
    );

    public String getSystemPrompt(String personality) {
        if (personality == null) {
            return DEFAULT_PROMPT;
        }
        return PERSONALITIES.getOrDefault(personality.toLowerCase(), DEFAULT_PROMPT);
    }
}
