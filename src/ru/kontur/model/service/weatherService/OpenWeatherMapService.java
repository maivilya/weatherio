package ru.kontur.model.service.weatherService;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class OpenWeatherMapService extends AbstractWeatherService {

    private static final String OPEN_WEATHER_MAP_URL = "http://api.openweathermap.org/data/2.5/weather?q=%s&appid=%s&units=metric&lang=ru";
    private static final String OPEN_WEATHER_MAP_API_KEY = "0fe00a1237c1788b7616dcf8ea456a26";
    private static final String OPEN_WEATHER_HOURLY_URL = "http://api.openweathermap.org/data/2.5/forecast?q=%s&appid=%s&units=metric&lang=ru";
    private static final JSONParser PARSER = new JSONParser();

    /**
     * The method generates a URL for sending a request
     * to the API (current weather) and makes this request
     *
     * @param locationName city name
     * @return JSON object for parsing
     */
    public JSONObject getWeather(String locationName) {
        String url = String.format(OPEN_WEATHER_MAP_URL, locationName, OPEN_WEATHER_MAP_API_KEY);
        return fetchWeatherData(url);
    }

    /**
     * The method generates a URL for sending a request
     * to the API (hourly forecast) and makes this request
     *
     * @param locationName city name
     * @return JSON object for parsing
     */
    public JSONObject getHourlyForecast(String locationName) {
        String url = String.format(OPEN_WEATHER_HOURLY_URL, locationName, OPEN_WEATHER_MAP_API_KEY);
        return fetchHourlyForecastData(url);
    }

    /**
     * Method for parsing weather hourly forecast data
     *
     * @param jsonString json string that needs to be parsed
     * @return parsed weather hourly forecast data in the form of an
     * JSON object that can be obtained by keys
     */
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
        JSONArray interpolated = interpolateHourlyData(hourlyForecast);
        JSONObject result = new JSONObject();
        result.put("hourly", interpolated);
        return result;
    }

    /**
     * Method for parsing current weather data
     *
     * @param stringJSON json string that needs to be parsed
     * @return parsed weather data in the form of an JSON object
     * that can be obtained by keys
     */
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

    /**
     * The method adds gaps in the data for OpenWeatherMap,
     * since the data from the API comes five days in advance,
     * but from the intervals from three hours (free version)
     *
     * @param rawForecast forecast to be interpolated
     * @return interpolated forecast
     */
    private JSONArray interpolateHourlyData(JSONArray rawForecast) throws Exception {
        JSONArray interpolated = new JSONArray();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar cal = Calendar.getInstance();

        for (int i = 0; i < rawForecast.size() - 1; i++) {
            JSONObject current = (JSONObject) rawForecast.get(i);
            JSONObject next = (JSONObject) rawForecast.get(i + 1);

            interpolated.add(current);

            String time1 = (String) current.get("time");
            String time2 = (String) next.get("time");

            Date date1 = formatter.parse(time1);
            Date date2 = formatter.parse(time2);

            long hoursBetween = (date2.getTime() - date1.getTime()) / (1000 * 60 * 60);
            if (hoursBetween > 1) {
                double temp1 = toDouble(current.get("temperature"));
                double temp2 = toDouble(next.get("temperature"));
                for (int h = 1; h < hoursBetween; h++) {
                    cal.setTime(date1);
                    cal.add(Calendar.HOUR_OF_DAY, h);
                    String interpolatedTime = formatter.format(cal.getTime());

                    double factor = (double) h / hoursBetween;
                    double interpolatedTemp = roundTwoDecimals(temp1 + (temp2 - temp1) * factor);

                    JSONObject interpolatedObj = new JSONObject();
                    interpolatedObj.put("time", interpolatedTime);
                    interpolatedObj.put("temperature", interpolatedTemp);
                    interpolatedObj.put("interpolated", true);
                    interpolated.add(interpolatedObj);
                }
            }
        }
        interpolated.add(rawForecast.get(rawForecast.size() - 1));
        return interpolated;
    }

    /**
     * The method converts an object (string, number)
     * to a real number
     *
     * @param value value to be converted
     * @return converted value
     */
    private double toDouble(Object value) {
        return value instanceof Number ? ((Number) value).doubleValue() : Double.parseDouble(value.toString());
    }

    /**
     * The method reduces a real number to two decimal places
     *
     * @param value value to be rounded
     * @return rounded value
     */
    private double roundTwoDecimals(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}