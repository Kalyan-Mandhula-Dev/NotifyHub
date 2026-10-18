package com.notifyhub.aiservice.client;

public interface AiClient {

    AiGenerationResult generate(String systemPrompt, String userPrompt);

    String getModelName();

    record AiGenerationResult(String text, int promptTokens, int completionTokens) {}

}
