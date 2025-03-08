package ru.kontur;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.swing.*;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
    private static final String CITY_NAME_ERROR_MESSAGE = "Нет города с таким названием!";
    private static final String GEOLOCATION_API_URL = "https://geocoding-api.open-meteo.com/v1/search?name=%s&count=10&language=ru&format=json";
    private static final JSONParser PARSER = new JSONParser();
    private static Scanner scanner;

    /**
     * Obtaining weather information for a specific city
     * that was received in a request from a user
     * @param locationName Location(city) requested by the user
     * @return JSON object with information on a specific city
     */
    public static JSONObject getWeatherData(String locationName) {
        JSONArray locationData = getLocationData(locationName);
        if (locationData == null) {
            showErrorMessage(CITY_NAME_ERROR_MESSAGE);
            System.out.println(CITY_NAME_ERROR_MESSAGE);
            return null;
        }

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
                showErrorMessage(CONNECTION_ERROR_MESSAGE);
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

    private static void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(new JFrame(), message, "Ошибка", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Getting all weather information in JSON format,
     * which will be parsed by specific entry
     * @param connection current HttpURLConnection
     * @return full-information JSON object
     */
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

    /**
     * Obtaining data on weather condition
     * using weather code passed to JSON API
     * @param weatherCode weather code passed to JSON API
     * @return weather condition
     */
    private static String convertWeatherCode(long weatherCode) {
        String weatherCondition = "";
        if (weatherCode == 0L) {
            weatherCondition = "Ясно";
        } else if (weatherCode > 0L && weatherCode <= 3L) {
            weatherCondition = "Облачно";
        } else if ((weatherCode >= 51L && weatherCode <= 67L)
                || (weatherCode >= 80L && weatherCode <= 99L)) {
            weatherCondition = "Дождь";
        } else if (weatherCode >= 71L && weatherCode <= 77L) {
            weatherCondition = "Снег";
        }
        return weatherCondition;
    }

    /**
     * Getting the index of the current time
     * to get the rest of the data by this index
     * @param timeArray time array that is passed to the API
     * @return time array index, otherwise -1
     */
    private static int findIndexOfCurrentTime(JSONArray timeArray) {
        for (int i = 0; i < timeArray.size(); i++) {
            String time = (String) timeArray.get(i);
            if (time.equalsIgnoreCase(getCurrentTime())) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Getting the current date and time
     * in the format that is passed to the API
     * @return current date and time in format "yyyy-MM-dd'T'HH':00'"
     */
    private static String getCurrentTime() {
        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH':00'");
        return currentDateTime.format(dateTimeFormatter);
    }

    /**
     * Getting the value latitude
     * @param location Location(city) requested by the user
     * @return double latitude
     */
    private static double getLatitude(JSONObject location) {
        return (double) location.get("latitude");
    }

    /**
     * Getting the value longitude
     * @param location Location(city) requested by the user
     * @return double longitude
     */
    private static double getLongitude(JSONObject location) {
        return (double) location.get("longitude");
    }

    /**
     * Receive weather description information
     * for a specific city in JSON format
     * @param locationName City name by user input
     * @return JSONArray with entry 'results' by locationName
     */
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

    /**
     * Getting HttpUrlConnection, sending a GET request to test the connection
     * @param urlString API connection link(url)
     * @return HttpURLConnection, otherwise, null
     */
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
