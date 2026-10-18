package com.notifyhub.aiservice.dto.request;

import java.util.List;


/*
 * Why List of Contents when a input has only one Content in a convo??
 * the API is designed for multi-turn conversations, like a chatbot, where you'd resend the
 * whole history each time, first user message, then the model's reply as role "model",
 * then the next user message, and so on, all in that same list, because these APIs are stateless.
 * The server doesn't remember your last call, so if you want context from earlier messages,
 * you have to send it all again yourself. That's exactly why it's a list and not a single object,
 */


/*
 * Why List of Parts, when each input is a text or sentence?
 * Each `Part` is one piece of input content, and a single message can be made of
 * several pieces glued together, so you could have one `Part` that's text and another `Part`
 * in the same content that's an image, and the model reads them together as one combined message.
 * It's not that each part represents a different possible response, it's that a message itself
 * can be multi-modal, made of multiple content pieces.
 */

public record GeminiRequest (
        // The instructions/rules we give to the AI eg: You are expert, answer it simply...
        SystemInstruction systemInstruction,

        // The conversation content between user and AI
        List<Content> contents,

        // The helper class to set custom properties like temperature (randomness) and max output tokens
        GenerationConfig generationConfig
){
    public record SystemInstruction(List<Part> parts){}
    public record Content(String role, List<Part> parts){}
    public record Part(String text){}
    public record GenerationConfig(double temperature, int maxOutputTokens){}
}
