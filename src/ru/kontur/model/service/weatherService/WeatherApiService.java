package ru.kontur.model.service.weatherService;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class WeatherApiService extends AbstractWeatherService {

    private static final String WEATHER_API_URL = "http://api.weatherapi.com/v1/current.json?key=%s&q=%s&lang=ru";
    private static final String WEATHER_API_KEY = "41582deb5d84437183f81730251303";
    private static final String WEATHER_API_HOURLY_URL = "http://api.weatherapi.com/v1/forecast.json?key=%s&q=%s&lang=ru&hours=72";
    private static final JSONParser PARSER = new JSONParser();

    @Override
    public JSONObject getWeather(String locationName) {
        String url = String.format(WEATHER_API_URL, WEATHER_API_KEY, locationName);
        return fetchWeatherData(url);
    }

    public JSONObject getHourlyForecast(String locationName) {
        String url = String.format(WEATHER_API_HOURLY_URL, WEATHER_API_KEY, locationName);
        return fetchHourlyForecastData(url);
    }

    @Override
    protected JSONObject parseHourlyForecast(String jsonString) throws Exception {
        JSONObject resultJSONObj = (JSONObject) PARSER.parse(jsonString);
        JSONObject forecast = (JSONObject) resultJSONObj.get("forecast");
        if (forecast == null) throw new ParseException(ParseException.ERROR_UNEXPECTED_EXCEPTION, "Missing 'forecast'");
        JSONArray forecastday = (JSONArray) forecast.get("forecastday");
        if (forecastday == null) throw new ParseException(ParseException.ERROR_UNEXPECTED_EXCEPTION, "Missing 'forecastday'");

        JSONArray hourlyForecast = new JSONArray();

        for (Object dayObj : forecastday) {
            JSONObject day = (JSONObject) dayObj;
            JSONArray hourArray = (JSONArray) day.get("hour");

            if (hourArray == null) continue;

            for (Object hourObj : hourArray) {
                JSONObject hour = (JSONObject) hourObj;
                JSONObject condition = (JSONObject) hour.get("condition");

                JSONObject hourData = new JSONObject();
                hourData.put("time", hour.get("time"));
                hourData.put("temperature", hour.get("temp_c"));
                hourData.put("weatherCondition", condition != null ? condition.get("text") : null);
                hourData.put("humidity", hour.get("humidity"));
                hourData.put("windSpeed", hour.get("wind_kph"));  // или "wind_mph" по выбору
                hourlyForecast.add(hourData);
            }
        }
        JSONObject result = new JSONObject();
        result.put("hourly", hourlyForecast);
        return result;
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
        weatherData.put("windSpeed", (double) current.get("wind_kph") / 3.6);
        return weatherData;
    }
}