package ru.kontur;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class WeatherApiService extends AbstractWeatherService {

    private static final String WEATHER_API_URL = "http://api.weatherapi.com/v1/current.json?key=%s&q=%s&lang=ru";
    private static final String WEATHER_API_KEY = "41582deb5d84437183f81730251303";
    private static final JSONParser PARSER = new JSONParser();

    @Override
    public JSONObject getWeather(String locationName) {
        String url = String.format(WEATHER_API_URL, WEATHER_API_KEY, locationName);
        return fetchWeatherData(url);
    }

    @Override
    protected JSONObject parseWeatherData(String stringJSON) throws Exception {
        JSONObject resultJSONObj = (JSONObject) PARSER.parse(stringJSON);
        JSONObject current = (JSONObject) resultJSONObj.get("current");
        JSONObject condition = (JSONObject) current.get("condition");

        JSONObject weatherData = new JSONObject();
        weatherData.put("temperature", current.get("temp_c"));
        weatherData.put("weatherCondition", condition.get("text"));
        weatherData.put("humidity", current.get("humidity"));
        weatherData.put("windSpeed", current.get("windSpeed"));
        return weatherData;
    }
}
