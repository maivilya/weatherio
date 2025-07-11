package ru.kontur.model.service.weatherService;

import org.json.simple.JSONObject;

import javax.swing.*;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public abstract class AbstractWeatherService {

    protected static final String CONNECTION_ERROR_MESSAGE = "Ошибка: невозможно подключиться к API";
    protected static final String CITY_NAME_ERROR_MESSAGE = "Нет города с таким названием!";

    public abstract JSONObject getWeather(String locationName);

    /**
     * Abstract method for getting
     * a json object by url (url will be different for each api)
     * for weather data
     *
     * @param urlString URL
     * @return parsed JSON object
     */
    protected JSONObject fetchWeatherData(String urlString) {
        try {
            HttpURLConnection connection = fetchApiResponse(urlString);
            if (connection == null || connection.getResponseCode() != 200) {
                System.out.println(CONNECTION_ERROR_MESSAGE);
                return null;
            }

            StringBuilder resultJSON = getResultJSON(connection);
            return parseWeatherData(resultJSON.toString());
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return null;
    }

    /**
     * Abstract method for getting
     * a json object by url (url will be different for each api)
     * for weather hourly forecast data
     *
     * @param urlString URL
     * @return parsed JSON object
     */
    protected JSONObject fetchHourlyForecastData(String urlString) {
        try {
            HttpURLConnection connection = fetchApiResponse(urlString);
            if (connection == null || connection.getResponseCode() != 200) {
                System.out.println(CONNECTION_ERROR_MESSAGE);
                return null;
            }
            StringBuilder resultJSON = getResultJSON(connection);
            return parseHourlyForecast(resultJSON.toString());
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return null;
    }

    /**
     * Abstract method for parsing weather hourly forecast data
     *
     * @param jsonString json string that needs to be parsed
     * @return parsed json string
     */
    protected abstract JSONObject parseHourlyForecast(String jsonString) throws Exception;

    /**
     * Abstract method for parsing current weather data
     *
     * @param stringJSON json string that needs to be parsed
     * @return parsed json string
     */
    protected abstract JSONObject parseWeatherData(String stringJSON) throws Exception;

    /**
     * The method displays a panel with an error message
     *
     * @param message message that will be displayed
     */
    protected void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(new JFrame(), message, "Ошибка", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Getting all weather information in JSON format,
     * which will be parsed by specific entry
     *
     * @param connection current HttpURLConnection
     * @return full-information JSON object
     */
    protected StringBuilder getResultJSON(HttpURLConnection connection) throws IOException {
        Scanner scanner = new Scanner(connection.getInputStream());
        StringBuilder result = new StringBuilder();
        while (scanner.hasNext()) {
            result.append(scanner.nextLine());
        }
        scanner.close();
        connection.disconnect();
        return result;
    }

    /**
     * Getting HttpUrlConnection, sending a GET request to test the connection
     *
     * @param urlString API connection link(url)
     * @return HttpURLConnection, otherwise, null
     */
    protected HttpURLConnection fetchApiResponse(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.connect();
        return connection;
    }
}