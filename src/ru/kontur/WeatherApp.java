package ru.kontur;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Scanner;

public class WeatherApp {

    private static final String HOURLY_ENTRY = "hourly";
    private static final String TIME_ENTRY = "time";
    private static final String TEMPERATURE_ENTRY = "temperature_2m";
    private static final String WEATHER_CODE_ENTRY = "weather_code";
    private static final String HUMIDITY_ENTRY = "relative_humidity_2m";
    private static final String WIND_SPEED_ENTRY = "wind_speed_10m";
    private static final String RESULTS_ENTRY = "results";
    private static final String CONNECTION_ERROR_MESSAGE = "ERROR: Could not connect to the API";
    private static final String GEOLOCATION_API_URL = "https://geocoding-api.open-meteo.com/v1/search?name=%s&count=10&language=ru&format=json";
    private static final JSONParser PARSER = new JSONParser();
    private static Scanner scanner;

    public static JSONObject getWeatherData(String locationName) {
        JSONArray locationData = getLocationData(locationName);
        assert locationData != null;
        JSONObject location = (JSONObject) locationData.get(0);
        String url = "https://api.open-meteo.com/v1/forecast?" +
                        "latitude=" + getLatitude(location) +
                        "&longitude=" + getLongitude(location) +
                        "&hourly=temperature_2m," +
                        "relative_humidity_2m," +
                        "weather_code," +
                        "wind_speed_10m" +
                        "&timezone=America%2FLos_Angeles";

        try {
            HttpURLConnection connection = fetchApiResponse(url);
            assert connection != null;
            if (connection.getResponseCode() != 200) {
                System.out.println(CONNECTION_ERROR_MESSAGE);
                return null;
            }
            StringBuilder resultJSON = getResultJSON(connection);

            JSONObject resultJSONObj = (JSONObject) PARSER.parse(String.valueOf(resultJSON));

            JSONObject hourly = (JSONObject) resultJSONObj.get(HOURLY_ENTRY);
            JSONArray timeData = (JSONArray) hourly.get(TIME_ENTRY);
            int index = findIndexOfCurrentTime(timeData);

            JSONArray temperatureData = (JSONArray) hourly.get(TEMPERATURE_ENTRY);
            double temperature = (double) temperatureData.get(index);

            JSONArray weatherCodeData = (JSONArray) hourly.get(WEATHER_CODE_ENTRY);
            String weatherCondition = convertWeatherCode((long) weatherCodeData.get(index));
            switch (weatherCondition) {
                case "Cloudy" -> weatherCondition = "Облачно";
                case "Rain" -> weatherCondition = "Дождь";
                case "Clear" -> weatherCondition = "Ясно";
                case "Snow" -> weatherCondition = "Снег";
            }

            JSONArray relativeHumidityData = (JSONArray) hourly.get(HUMIDITY_ENTRY);
            long humidity = (long) relativeHumidityData.get(index);

            JSONArray windSpeedData = (JSONArray) hourly.get(WIND_SPEED_ENTRY);
            double windSpeed = (double) windSpeedData.get(index);

            JSONObject resultWeatherData = new JSONObject();
            resultWeatherData.put("temperature", temperature);
            resultWeatherData.put("weatherCondition", weatherCondition);
            resultWeatherData.put("humidity", humidity);
            resultWeatherData.put("windSpeed", windSpeed);
            return resultWeatherData;
        } catch (IOException exception) {
            exception.printStackTrace();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    private static StringBuilder getResultJSON(HttpURLConnection connection) {
        StringBuilder resultJSON = new StringBuilder();
        try {
            scanner = new Scanner(connection.getInputStream());
            while(scanner.hasNext()) {
                resultJSON.append(scanner.nextLine());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            scanner.close();
            connection.disconnect();
        }
        return resultJSON;
    }

    private static String convertWeatherCode(long weatherCode) {
        String weatherCondition = "";
        if (weatherCode == 0L) {
            weatherCondition = "Clear";
        } else if (weatherCode > 0L && weatherCode <= 3L) {
            weatherCondition = "Cloudy";
        } else if ((weatherCode >= 51L && weatherCode <= 67L)
                || (weatherCode >= 80L && weatherCode <= 99L)) {
            weatherCondition = "Rain";
        } else if (weatherCode >= 71L && weatherCode <= 77L) {
            weatherCondition = "Snow";
        }
        return weatherCondition;
    }

    private static int findIndexOfCurrentTime(JSONArray timeArray) {
        for (int i = 0; i < timeArray.size(); i++) {
            String time = (String) timeArray.get(i);
            if (time.equalsIgnoreCase(getCurrentTime())) {
                return i;
            }
        }
        return -1;
    }

    private static String getCurrentTime() {
        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH':00'");
        return currentDateTime.format(dateTimeFormatter);
    }

    private static double getLatitude(JSONObject location) {
        return (double) location.get("latitude");
    }

    private static double getLongitude(JSONObject location) {
        return (double) location.get("longitude");
    }

    private static JSONArray getLocationData(String locationName) {
        locationName = locationName.replaceAll(" ", "+");
        String url = String.format(GEOLOCATION_API_URL, locationName);
        try {
            HttpURLConnection connection = fetchApiResponse(url);
            assert connection != null;
            if (connection.getResponseCode() != 200) {
                System.out.println(CONNECTION_ERROR_MESSAGE);
                return null;
            } else {
                StringBuilder resultJSON = getResultJSON(connection);
                JSONObject resultJSONObj = (JSONObject) PARSER.parse(String.valueOf(resultJSON));
                return (JSONArray) resultJSONObj.get(RESULTS_ENTRY);
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
