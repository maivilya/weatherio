package ru.kontur.model.service.weatherService;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import ru.kontur.model.WeatherCodeConverter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class WeatherMeteoService extends AbstractWeatherService {

    private static final JSONParser PARSER = new JSONParser();
    private static final String WEATHER_METEO_API_URL = "https://geocoding-api.open-meteo.com/v1/search?name=%s&count=10&language=ru&format=json";

    /**
     * Obtaining weather information for a specific city
     * that was received in a request from a user
     *
     * @param locationName Location(city) requested by the user
     * @return JSON object with information on a specific city
     */
    @Override
    public JSONObject getWeather(String locationName) {
        JSONArray locationData = getLocationData(locationName);
        if (locationData == null) {
            showErrorMessage(CITY_NAME_ERROR_MESSAGE);
            System.out.println(CITY_NAME_ERROR_MESSAGE);
            return null;
        }
        String url = makeUrl(locationData);
        try {
            HttpURLConnection connection = fetchApiResponse(url);
            if (connection.getResponseCode() != 200) {
                showErrorMessage(CONNECTION_ERROR_MESSAGE);
                System.out.println(CONNECTION_ERROR_MESSAGE);
                return null;
            }
            StringBuilder resultJSON = getResultJSON(connection);
            return parseWeatherData(resultJSON.toString());
        } catch (IOException exception) {
            exception.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    private String makeUrl(JSONArray locationData) {
        JSONObject location = (JSONObject) locationData.get(0);
        return "https://api.open-meteo.com/v1/forecast?" +
                "latitude=" + location.get("latitude") +
                "&longitude=" + location.get("longitude") +
                "&hourly=temperature_2m," +
                "relative_humidity_2m," +
                "weather_code," +
                "wind_speed_10m" +
                "&timezone=America%2FLos_Angeles";
    }

    private JSONArray getLocationData(String locationName) {
        locationName = locationName.replaceAll(" ", "+");
        String url = String.format(WEATHER_METEO_API_URL, locationName);
        try {
            HttpURLConnection connection = fetchApiResponse(url);
            assert connection != null;
            if (connection.getResponseCode() != 200) {
                System.out.println(CONNECTION_ERROR_MESSAGE);
                return null;
            } else {
                StringBuilder resultJSON = getResultJSON(connection);
                JSONObject resultJSONObj = (JSONObject) PARSER.parse(String.valueOf(resultJSON));
                return (JSONArray) resultJSONObj.get("results");
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return null;
    }

    @Override
    protected JSONObject parseWeatherData(String stringJSON) throws Exception {
        JSONObject resultJSONObj = (JSONObject) PARSER.parse(String.valueOf(stringJSON));
        JSONObject hourly = (JSONObject) resultJSONObj.get("hourly");
        JSONArray timeData = (JSONArray) hourly.get("time");
        JSONArray temperatureData = (JSONArray) hourly.get("temperature_2m");
        JSONArray relativeHumidityData = (JSONArray) hourly.get("relative_humidity_2m");
        JSONArray weatherCodeData = (JSONArray) hourly.get("weather_code");
        JSONArray windSpeedData = (JSONArray) hourly.get("wind_speed_10m");

        int index = findIndexOfCurrentTime(timeData);
        double temperature = (double) temperatureData.get(index);
        String weatherCondition = WeatherCodeConverter.convert((long) weatherCodeData.get(index));
        long humidity = (long) relativeHumidityData.get(index);
        double windSpeed = (double) windSpeedData.get(index);

        JSONObject weatherData = new JSONObject();
        weatherData.put("temperature", temperature);
        weatherData.put("humidity", humidity);
        weatherData.put("weatherCondition", weatherCondition);
        weatherData.put("windSpeed", windSpeed);
        return weatherData;
    }

    /**
     * Getting the current date and time
     * in the format that is passed to the API
     *
     * @return current date and time in format "yyyy-MM-dd'T'HH':00'"
     */
    private static String getCurrentTime() {
        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH':00'");
        return currentDateTime.format(dateTimeFormatter);
    }

    /**
     * Getting the index of the current time
     * to get the rest of the data by this index
     *
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
}