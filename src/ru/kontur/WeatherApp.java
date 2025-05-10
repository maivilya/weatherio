package ru.kontur;

import org.json.simple.JSONObject;
import ru.kontur.model.service.weatherService.AbstractWeatherService;

public class WeatherApp {

    /**
     * Obtaining weather information for a specific city
     * that was received in a request from a user
     * @param service Weather service name
     * @param locationName Location(city) requested by the user
     * @return JSON object with information on a specific city
     */
    public static JSONObject getWeatherFromService(AbstractWeatherService service, String locationName) {
        return service.getWeather(locationName);
    }
}
