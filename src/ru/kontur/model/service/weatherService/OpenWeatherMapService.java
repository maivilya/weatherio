package ru.kontur.model.service.weatherService;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class OpenWeatherMapService extends AbstractWeatherService {

    private static final String OPEN_WEATHER_MAP_URL = "http://api.openweathermap.org/data/2.5/weather?q=%s&appid=%s&units=metric&lang=ru";
    private static final String OPEN_WEATHER_MAP_API_KEY = "0fe00a1237c1788b7616dcf8ea456a26";
    private static final String OPEN_WEATHER_HOURLY_URL = "http://api.openweathermap.org/data/2.5/forecast?q=%s&appid=%s&units=metric&lang=ru";
    private static final JSONParser PARSER = new JSONParser();

    public JSONObject getWeather(String locationName) {
        String url = String.format(OPEN_WEATHER_MAP_URL, locationName, OPEN_WEATHER_MAP_API_KEY);
        return fetchWeatherData(url);
    }

    public JSONObject getHourlyForecast(String locationName) {
        String url = String.format(OPEN_WEATHER_HOURLY_URL, locationName, OPEN_WEATHER_MAP_API_KEY);
        return fetchHourlyForecastData(url);
    }

    @Override
    protected JSONObject parseHourlyForecast(String jsonString) throws Exception {
        JSONObject resultJSONObj = (JSONObject) PARSER.parse(jsonString);
        JSONArray list = (JSONArray) resultJSONObj.get("list");

        JSONArray hourlyForecast = new JSONArray();
        for (Object obj : list) {
            JSONObject forecastEntry = (JSONObject) obj;
            JSONObject main = (JSONObject) forecastEntry.get("main");
            JSONObject wind = (JSONObject) forecastEntry.get("wind");
            JSONArray weatherArray = (JSONArray) forecastEntry.get("weather");
            JSONObject weather = (JSONObject) weatherArray.get(0);

            JSONObject hourData = new JSONObject();
            hourData.put("time", forecastEntry.get("dt_txt"));
            hourData.put("temperature", main.get("temp"));
            hourData.put("weatherCondition", weather.get("description"));
            hourData.put("humidity", main.get("humidity"));
            hourData.put("windSpeed", wind.get("speed"));
            hourlyForecast.add(hourData);
        }
        JSONObject result = new JSONObject();
        result.put("hourly", hourlyForecast);
        return result;
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