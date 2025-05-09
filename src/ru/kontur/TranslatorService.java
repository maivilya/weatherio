package ru.kontur;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class TranslatorService {

    public String translateCityName(String cityName, String sourceLang, String targetLang) throws Exception {
        String requestBody = String.format(
                "q=%s&source=%s&target=%s&format=text",
                cityName, sourceLang, targetLang
        );

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("https://libretranslate.com/translate"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        String body = response.body();

        int start = body.indexOf(":\"") + 2;
        int end = body.indexOf("\"", start);
        return body.substring(start, end);
    }
}
