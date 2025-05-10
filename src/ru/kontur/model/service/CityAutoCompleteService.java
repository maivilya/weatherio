package ru.kontur.model.service;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;

public class CityAutoCompleteService {

    private final String API_KEY = "0fe00a1237c1788b7616dcf8ea456a26";
    private static final TranslatorService translator = new TranslatorService();

    public Map<String, String> fetchCities(String query) throws Exception {
        String encodedQuery = URLEncoder.encode(query, "UTF-8");
        String urlStr = "https://api.openweathermap.org/geo/1.0/direct?q=" + encodedQuery + "&limit=5&appid=" + API_KEY;

        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();

        JSONArray jsonArray = new JSONArray(response.toString());
        Map<String, String> cities = new LinkedHashMap<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject obj = jsonArray.getJSONObject(i);
            String name = translator.translateCityName(obj.getString("name"), "en", "ru");
            String country = translator.translateCityName(obj.getString("country"), "en", "ru");
            cities.put(name, name + " (" + country + ")");
        }
        return cities;
    }
}
