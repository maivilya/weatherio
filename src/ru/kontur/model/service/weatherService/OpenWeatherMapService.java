package ru.kontur.model.service.weatherService;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class OpenWeatherMapService extends AbstractWeatherService {

    private static final String OPEN_WEATHER_MAP_URL = "http://api.openweathermap.org/data/2.5/weather?q=%s&appid=%s&units=metric&lang=ru";
    private static final String OPEN_WEATHER_MAP_API_KEY = "0fe00a1237c1788b7616dcf8ea456a26";
    private static final JSONParser PARSER = new JSONParser();

    public JSONObject getWeather(String locationName) {
        String url = String.format(OPEN_WEATHER_MAP_URL, locationName, OPEN_WEATHER_MAP_API_KEY);
        return fetchWeatherData(url);
    }

    @Override
    protected JSONObject parseWeatherData(String stringJSON) throws Exception {
        JSONObject resultJSONObj = (JSONObject) PARSER.parse(stringJSON);
        JSONObject main = (JSONObject) resultJSONObj.get("main");
        JSONObject wind = (JSONObject) resultJSONObj.get("wind");
        JSONArray weatherArray = (JSONArray) resultJSONObj.get("weather");
        JSONObject weather = (JSONObject) weatherArray.get(0);

        JSONObject weatherData = new JSONObject();
        weatherData.put("temperature", main.get("temp"));
        weatherData.put("weatherCondition", weather.get("description"));
        weatherData.put("humidity", main.get("humidity"));
        weatherData.put("windSpeed", wind.get("speed"));
        return weatherData;
    }
}
