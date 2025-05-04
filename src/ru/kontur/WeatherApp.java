package ru.kontur;

import org.json.simple.JSONObject;

public class WeatherApp {

    /**
     * Obtaining weather information for a specific city
     * that was received in a request from a user
     * @param locationName Location(city) requested by the user
     * @return JSON object with information on a specific city
     */
    public static JSONObject getWeatherDataFromOpenWeatherMap(String locationName) {
        //return OpenWeatherMapService.getWeather(locationName);
        return new JSONObject();
        /*String url = String.format(OPEN_WEATHER_MAP_URL, locationName, OPEN_WEATHER_MAP_API_KEY);
        try {
            HttpURLConnection connection = fetchApiResponse(url);
            if (connection == null || connection.getResponseCode() != 200) {
                showErrorMessage(CONNECTION_ERROR_MESSAGE);
                System.out.println(CONNECTION_ERROR_MESSAGE);
                return null;
            }
            StringBuilder resultJSON = getResultJSON(connection);
            JSONObject resultJSONObj = (JSONObject) PARSER.parse(String.valueOf(resultJSON));

            JSONObject main = (JSONObject) resultJSONObj.get("main");
            double temperature = (double) main.get("temp");

            JSONObject weather = (JSONObject) ((JSONArray) resultJSONObj.get("weather")).get(0);
            String weatherCondition = (String) weather.get("description");

            long humidity = (long) main.get("humidity");

            JSONObject weatherData = new JSONObject();
            weatherData.put("temperature", temperature);
            weatherData.put("weatherCondition", weatherCondition);
            weatherData.put("humidity", humidity);
            return weatherData;
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return null;*/
    }

    /**
     * Obtaining weather information for a specific city
     * that was received in a request from a user
     * @param locationName Location(city) requested by the user
     * @return JSON object with information on a specific city
     */
    public static JSONObject getWeatherDataFromWeatherApi(String locationName) {
        //return WeatherApiService.getWeather(locationName);
        return new JSONObject();
        /*String url = String.format(WEATHER_API_URL, WEATHER_API_KEY, locationName);
        try {
            HttpURLConnection connection = fetchApiResponse(url);
            if (connection == null || connection.getResponseCode() != 200) {
                showErrorMessage(CONNECTION_ERROR_MESSAGE);
                System.out.println(CONNECTION_ERROR_MESSAGE);
                return null;
            }
            StringBuilder resultJSON = getResultJSON(connection);
            JSONObject resultJSONObj = (JSONObject) PARSER.parse(String.valueOf(resultJSON));

            JSONObject current = (JSONObject) resultJSONObj.get("current");

            double temperature = (double) current.get("temp_c");

            JSONObject condition = (JSONObject) current.get("condition");
            String weatherCondition = (String) condition.get("text");

            long humidity = (long) current.get("humidity");

            double windSpeed = (double) current.get("wind_kph");

            JSONObject weatherData = new JSONObject();
            weatherData.put("temperature", temperature);
            weatherData.put("weatherCondition", weatherCondition);
            weatherData.put("humidity", humidity);
            weatherData.put("windSpeed", windSpeed);
            return weatherData;
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return null;*/
    }

    /**
     * Obtaining weather information for a specific city
     * that was received in a request from a user
     * @param locationName Location(city) requested by the user
     * @return JSON object with information on a specific city
     */
    public static JSONObject getWeatherDataFromWeatherMeteo(String locationName) {
        //return WeatherMeteoService.getWeather(locationName);
        return new JSONObject();
        /*JSONArray locationData = getLocationData(locationName);
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
        return null;*/
    }

    public static JSONObject getWeatherFromService(AbstractWeatherService service, String locationName) {
        return service.getWeather(locationName);
    }
}
