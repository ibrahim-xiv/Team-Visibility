package com.TeamVisibility.App.service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Value("${resend.api.key}")
    private String apiKey;

    private final HttpClient httpClient = HttpClient.newHttpClient();

    public void sendVerificationCode(String toEmail, String code) {
        String body = """
            {
              "from": "onboarding@resend.dev",
              "to": ["%s"],
              "subject": "Dein Visibility Verifizierungscode",
              "html": "<h2>Dein Code: <strong>%s</strong></h2><p>Dieser Code ist 15 Minuten gültig.</p>"
            }
            """.formatted(toEmail, code);

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("https://api.resend.com/emails"))
            .header("Authorization", "Bearer " + apiKey)
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(body))
            .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 400) {
                throw new RuntimeException("Resend Fehler: " + response.body());
            }
        } catch (Exception e) {
            throw new RuntimeException("E-Mail Fehler: " + e.getMessage(), e);
        }
    }
}