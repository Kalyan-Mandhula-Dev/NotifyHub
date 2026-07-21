package com.notifyhub.notificationservice.service;

import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class TemplateEngine {

    public String populate(String template, Map<String, Object> payload) {
        String result = template;
        for (Map.Entry<String, Object> entry : payload.entrySet()) {
            result = result.replace(
                    "{{" + entry.getKey() + "}}",
                    String.valueOf(entry.getValue())
            );
        }
        return result;
    }

    public String buildFallbackTemplate(String eventType, Map<String, Object> payload) {
        StringBuilder html = new StringBuilder();
        html.append("<html><body>");
        html.append("<h2>Notification: ").append(eventType).append("</h2>");
        html.append("<table border='1' cellpadding='8'>");
        for (String key : payload.keySet()) {
            html.append("<tr>")
                    .append("<td><b>").append(key).append("</b></td>")
                    .append("<td>{{").append(key).append("}}</td>")
                    .append("</tr>");
        }
        html.append("</table>");
        html.append("</body></html>");
        return html.toString();
    }

}
