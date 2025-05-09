package ru.kontur;

import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class TranslatorService {

    public String translateCityName(String cityName, String sourceLang, String targetLang) throws Exception {
        String encodedCIty = URLEncoder.encode(cityName, StandardCharsets.UTF_8);
        String url = String.format(
                "https://api.mymemory.translated.net/get?q=%s&langpair=%s%%7C%s",
                encodedCIty, sourceLang, targetLang
        );

        HttpRequest request = HttpRequest.newBuilder()
                .uri(java.net.URI.create(url))
                .GET()
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        String body = response.body();

        int start = body.indexOf("\"translatedText\":\"") + 18;
        int end = body.indexOf("\"", start);
        return decodeUnicode(body.substring(start, end));
    }

    private String decodeUnicode(String input) {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        while (i < input.length()) {
            char c = input.charAt(i++);
            if (c == '\\' && i < input.length() && input.charAt(i) == 'u') {
                i++; // пропускаем 'u'
                int code = Integer.parseInt(input.substring(i, i + 4), 16);
                sb.append((char) code);
                i += 4;
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}
