package ru.kontur;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class WeatherApp {

    private static final String GEOLOCATION_URL = "https://geocoding-api.open-meteo.com/v1/search?name=%s&count=10&language=ru&format=json";

    public static JSONObject getWeatherData(String locationName) {
        JSONArray locationData = getLocationData(locationName);
        return null;
    }

    public static JSONArray getLocationData(String locationName) {
        locationName = locationName.replaceAll(" ", "+");
        String url = "https://geocoding-api.open-meteo.com/v1/search?name=" +
                locationName + "&count=10&language=ru&format=json";
        //String url2 = String.format(GEOLOCATION_URL, locationName);
        try {
            HttpURLConnection connection = fetchApiResponse(url);
            //assert connection != null;
            if (connection.getResponseCode() != 200) {
                System.out.println("Error: could not connect to the API");
                return null;
            } else {
                StringBuilder resultJSON = new StringBuilder();
                Scanner scanner = new Scanner(connection.getInputStream());
                while(scanner.hasNext()) {
                    resultJSON.append(scanner.nextLine());
                }
                scanner.close();
                connection.disconnect();

                JSONParser parser = new JSONParser();
                JSONObject resultJSONObj = (JSONObject) parser.parse(String.valueOf(resultJSON));

                JSONArray locationData = (JSONArray) resultJSONObj.get("results");
                return locationData;
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return null;
    }

    private static HttpURLConnection fetchApiResponse(String urlString) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            return connection;
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        return null;
    }
}
