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
import java.util.Scanner;

public class WeatherApp {

    private static final String CONNECTION_ERROR_MESSAGE = "ERROR: Could not connect to the API";
    private static final String GEOLOCATION_API_URL = "https://geocoding-api.open-meteo.com/v1/search?name=%s&count=10&language=ru&format=json";

    public static JSONObject getWeatherData(String locationName) {
        JSONArray locationData = getLocationData(locationName);
        JSONObject location = (JSONObject) locationData.get(0);
        String url = "https://api.open-meteo.com/v1/forecast?" +
                        "latitude=" + getLatitude(location) +
                        "&longitude=" + getLongitude(location) +
                        "&hourly=temperature_2m,relative_humidity_2m,weather_code,wind_speed_10m&timezone=America%2FLos_Angeles";

        try {
            HttpURLConnection connection = fetchApiResponse(url);
            if (connection.getResponseCode() != 200) {
                System.out.println(CONNECTION_ERROR_MESSAGE);
                return null;
            }

            // TODO: метод для получения полного JSON
            StringBuilder resultJSON = new StringBuilder();
            Scanner scanner = new Scanner(connection.getInputStream());
            while(scanner.hasNext()) {
                resultJSON.append(scanner.nextLine());
            }
            scanner.close();
            connection.disconnect();

            // TODO: parser для получения определенного результата. e.g: "results", "hourly" e.t.c
            JSONParser parser = new JSONParser();
            JSONObject resultJSONObj = (JSONObject) parser.parse(String.valueOf(resultJSON));

            // TODO: метод для получения текущих временных данных
            JSONObject hourly = (JSONObject) resultJSONObj.get("hourly");
            JSONArray timeData = (JSONArray) hourly.get("time");
            int index = findIndexOfCurrentTime(timeData);

            // TODO: метод для получения данных о температуре
            JSONArray temperatureData = (JSONArray) hourly.get("temperature_2m");
            double temperature = (double) temperatureData.get(index);

            // TODO: метод для получения данных о состоянии погоды
            JSONArray weatherCodeData = (JSONArray) hourly.get("weather_code");
            String weatherCondition = convertWeatherCode((long) weatherCodeData.get(index));
            switch (weatherCondition) {
                case "Cloudy" -> weatherCondition = "Облачно";
                case "Rain" -> weatherCondition = "Дождь";
                case "Clear" -> weatherCondition = "Ясно";
                case "Snow" -> weatherCondition = "Снег";
            }

            // TODO: метод для получения данных влажности воздуха
            JSONArray relativeHumidityData = (JSONArray) hourly.get("relative_humidity_2m");
            long humidity = (long) relativeHumidityData.get(index);

            // TODO: метод для получения данных скорости ветра
            JSONArray windSpeedData = (JSONArray) hourly.get("wind_speed_10m");
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
                // TODO: метод для получения полного JSON
                StringBuilder resultJSON = new StringBuilder();
                Scanner scanner = new Scanner(connection.getInputStream());
                while(scanner.hasNext()) {
                    resultJSON.append(scanner.nextLine());
                }
                scanner.close();
                connection.disconnect();

                // TODO: parser для получения определенного результата. e.g: "results", "hourly" e.t.c
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
