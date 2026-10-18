package com.notifyhub.aiservice.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

// Represents the complete response we receive from AI, extra fields AI adds later won't break parsing.
@JsonIgnoreProperties(ignoreUnknown= true)
public record GeminiResponse(

        // List of possible responses given by AI
        List<Candidate> candidates,

        // Usage metrics like how many tokens for input, how many for response
        UsageMetaData usageMetadata
) {

    // A possible response for a input prompt, finishReason = Why AI stopped generating
    @JsonIgnoreProperties(ignoreUnknown= true)
    public record Candidate(Content content, String finishReason){}

    @JsonIgnoreProperties(ignoreUnknown= true)
    public record Content(List<Part> parts){}

    @JsonIgnoreProperties(ignoreUnknown= true)
    public record Part(String text){}

    // promptTokenCount = token for input, candidatesTokenCount = tokens for response, totalTokenCount = input + output
    @JsonIgnoreProperties(ignoreUnknown= true)
    public record UsageMetaData(int promptTokenCount, int candidatesTokenCount, int totalTokenCount, int thoughtsTokenCount){}

    public String getFirstText(){
        if(candidates == null || candidates.isEmpty()
                || candidates.get(0).content == null
                || candidates.get(0).content.parts == null
                || candidates.get(0).content.parts.isEmpty()){
            return null;
        }

        return candidates.get(0).content.parts.get(0).text;
    }
}
