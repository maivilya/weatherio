package ru.kontur;

import org.json.simple.JSONObject;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.List;

public class WeatherIO extends JFrame {

    private static final String TITLE = "Погода";
    private static final String FONT_FAMILY = "Roboto";
    private static final String DEFAULT_CITY = "Yekaterinburg";
    private static final int WIDTH = 1300;
    private static final int HEIGHT = 750;

    private static final String SEARCH_RESOURCE_PATH = "src/ru/kontur/assets/search.png";
    private static final String FAVORITES_ICON_PATH = "src/ru/kontur/assets/favorite.png";
    private static final String CLEAR_RESOURCE_PATH = "src/ru/kontur/assets/clear.png";
    private static final String CLOUDY_RESOURCE_PATH = "src/ru/kontur/assets/cloudy.png";
    private static final String RAIN_RESOURCE_PATH = "src/ru/kontur/assets/rain.png";
    private static final String SNOW_RESOURCE_PATH = "src/ru/kontur/assets/snow.png";
    private static final String HUMIDITY_RESOURCE_PATH = "src/ru/kontur/assets/humidity.png";
    private static final String WIND_SPEED_RESOURCE_PATH = "src/ru/kontur/assets/windSpeed.png";

    private static JTextField searchTextField;
    private JLabel apiWeatherConditionImage1, apiWeatherConditionImage2, apiWeatherConditionImage3;
    private JLabel apiTemperatureText1, apiTemperatureText2, apiTemperatureText3;
    private JLabel apiWeatherDescription1, apiWeatherDescription2, apiWeatherDescription3;
    private JLabel apiHumidityDescription1, apiHumidityDescription2, apiHumidityDescription3;
    private JLabel apiWindSpeedDescription1, apiWindSpeedDescription2, apiWindSpeedDescription3;
    private JLabel averageTemperatureText;

    private final FavoriteService favoriteService;
    private final DefaultListModel<String> listModel;
    private JButton btnShowFavorites;

    private static final WeatherMeteoService weatherMeteoService = new WeatherMeteoService();
    private static final WeatherApiService weatherApiService = new WeatherApiService();
    private static final OpenWeatherMapService openWeatherMapService = new OpenWeatherMapService();

    public WeatherIO() {
        favoriteService = new FavoriteService();
        listModel = new DefaultListModel<>();
        setTitle(TITLE);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null);
        setLayout(null);
        setResizable(false);
        addComponents();
        getWeather(DEFAULT_CITY);
        searchTextField.setText(DEFAULT_CITY);
        updateFavoriteList();
    }

    /**
     * Adding all objects to the main window
     */
    private void addComponents() {
        addSearchField();
        addSearchButton();
        addWeatherImage();
        addTemperatureText();
        addWeatherDescription();
        addHumidityImage();
        addHumidityDescription();
        addWindSpeedImage();
        addWindSpeedDescription();
        addBtnShowFavorites();
        addBtnAddCity();
        addAverageTemperatureText();
        addApiDescription();
    }

    /**
     * Updating the list with favorite locations
     */
    private void updateFavoriteList() {
        try {
            List<String> favorites = favoriteService.getFavorites();
            listModel.clear();
            for (String city : favorites) {
                listModel.addElement(city);
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Adding a button to show favorites cities
     */
    private void addBtnShowFavorites() {
        btnShowFavorites = new JButton(loadImage(FAVORITES_ICON_PATH));
        btnShowFavorites.setBounds(950, 15, 45, 45);
        btnShowFavorites.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnShowFavorites.setBackground(Color.WHITE);
        btnShowFavorites.setBorder(BorderFactory.createEmptyBorder());
        btnShowFavorites.addActionListener(e -> showFavoritesList());
        add(btnShowFavorites);
    }

    /**
     * Display a list with favorite locations
     */
    private void showFavoritesList() {
        JPopupMenu popupMenu = new JPopupMenu();
        for (int i = 0; i < listModel.size(); i++) {
            String city = listModel.getElementAt(i);
            JMenuItem menuItem = new JMenuItem(city);
            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

            JLabel cityLabel = new JLabel(city);
            cityLabel.setPreferredSize(new Dimension(50, 30));
            panel.add(cityLabel);

            JButton removeButton = new JButton("X");
            removeButton.setPreferredSize(new Dimension(15, 15));
            removeButton.setMargin(new Insets(0, 0, 0, 0));
            removeButton.addActionListener(e -> removeCityFromFavorites(city));

            panel.add(Box.createHorizontalStrut(10));
            panel.add(removeButton);

            menuItem.addActionListener(e -> {
                getWeather(city);
                searchTextField.setText(city);
            });
            menuItem.setLayout(new BorderLayout());
            menuItem.add(panel, BorderLayout.CENTER);

            popupMenu.add(menuItem);
        }
        popupMenu.show(btnShowFavorites, 0, btnShowFavorites.getHeight());
    }

    /**
     * Deleting a city from the favorites list
     *
     * @param cityName the city you want to remove from the favorites list
     */
    private void removeCityFromFavorites(String cityName) {
        try {
            favoriteService.removeCityFromFavorites(cityName);
            updateFavoriteList();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Adding a button to add a city
     */
    private void addBtnAddCity() {
        JButton btnAddCity = new JButton("Добавить в избранное");
        btnAddCity.setBounds(200, 22, 200, 30);
        btnAddCity.addActionListener(e -> {
            String cityName = JOptionPane.showInputDialog("Введите название города: ");
            if (cityName != null && !cityName.trim().isEmpty()) {
                try {
                    favoriteService.addCityToFavorites(cityName);
                    updateFavoriteList();
                } catch (SQLException exception) {
                    exception.printStackTrace();
                }
            }
        });
        add(btnAddCity);
    }

    /**
     * Adding a average temperature text
     */
    private void addAverageTemperatureText() {
        averageTemperatureText = new JLabel("Средняя температура: 24º");
        averageTemperatureText.setBounds(450, 590, 450, 35);
        averageTemperatureText.setFont(new Font(FONT_FAMILY, Font.BOLD, 24));
        averageTemperatureText.setHorizontalAlignment(SwingConstants.CENTER);
        add(averageTemperatureText);
    }

    /**
     * Adding a wind speed description
     */
    private void addWindSpeedDescription() {
        apiWindSpeedDescription1 = new JLabel("<html><b>Скорость ветра</b>\n3 км/ч</html>");
        apiWindSpeedDescription1.setBounds(295, 490, 140, 65);
        apiWindSpeedDescription1.setFont(new Font(FONT_FAMILY, Font.PLAIN, 18));

        apiWindSpeedDescription2 = new JLabel("<html><b>Скорость ветра</b>\n3 км/ч</html>");
        apiWindSpeedDescription2.setBounds(710, 490, 140, 65);
        apiWindSpeedDescription2.setFont(new Font(FONT_FAMILY, Font.PLAIN, 18));

        apiWindSpeedDescription3 = new JLabel("<html><b>Скорость ветра</b>\n3 км/ч</html>");
        apiWindSpeedDescription3.setBounds(1125, 490, 140, 65);
        apiWindSpeedDescription3.setFont(new Font(FONT_FAMILY, Font.PLAIN, 18));
        add(apiWindSpeedDescription1);
        add(apiWindSpeedDescription2);
        add(apiWindSpeedDescription3);
    }

    /**
     * Adding a wind speed image
     */
    private void addWindSpeedImage() {
        JLabel windSpeedImage1 = new JLabel(loadImage(WIND_SPEED_RESOURCE_PATH));
        windSpeedImage1.setBounds(205, 490, 75, 65);

        JLabel windSpeedImage2 = new JLabel(loadImage(WIND_SPEED_RESOURCE_PATH));
        windSpeedImage2.setBounds(630, 490, 75, 65);

        JLabel windSpeedImage3 = new JLabel(loadImage(WIND_SPEED_RESOURCE_PATH));
        windSpeedImage3.setBounds(1045, 490, 75, 65);
        add(windSpeedImage1);
        add(windSpeedImage2);
        add(windSpeedImage3);
    }

    /**
     * Adding a humidity description
     */
    private void addHumidityDescription() {
        apiHumidityDescription1 = new JLabel("<html><b>Влажность</b> 100%</html>");
        apiHumidityDescription1.setBounds(90, 490, 110, 65);
        apiHumidityDescription1.setFont(new Font(FONT_FAMILY, Font.PLAIN, 18));

        apiHumidityDescription2 = new JLabel("<html><b>Влажность</b> 100%</html>");
        apiHumidityDescription2.setBounds(505, 490, 110, 65);
        apiHumidityDescription2.setFont(new Font(FONT_FAMILY, Font.PLAIN, 18));

        apiHumidityDescription3 = new JLabel("<html><b>Влажность</b> 100%</html>");
        apiHumidityDescription3.setBounds(920, 490, 110, 65);
        apiHumidityDescription3.setFont(new Font(FONT_FAMILY, Font.PLAIN, 18));

        add(apiHumidityDescription1);
        add(apiHumidityDescription2);
        add(apiHumidityDescription3);
    }

    /**
     * Adding a humidity image
     */
    private void addHumidityImage() {
        JLabel humidityImage1 = new JLabel(loadImage(HUMIDITY_RESOURCE_PATH));
        humidityImage1.setBounds(15, 490, 75, 65);

        JLabel humidityImage2 = new JLabel(loadImage(HUMIDITY_RESOURCE_PATH));
        humidityImage2.setBounds(430, 490, 75, 65);

        JLabel humidityImage3 = new JLabel(loadImage(HUMIDITY_RESOURCE_PATH));
        humidityImage3.setBounds(845, 490, 75, 65);
        add(humidityImage1);
        add(humidityImage2);
        add(humidityImage3);
    }

    /**
     * Adding a API description
     */
    private void addApiDescription() {
        JLabel apiDescription1 = new JLabel("WeatherMeteo");
        apiDescription1.setBounds(0, 110, 450, 35);
        apiDescription1.setFont(new Font(FONT_FAMILY, Font.PLAIN, 32));
        apiDescription1.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel apiDescription2 = new JLabel("WeatherAPI");
        apiDescription2.setBounds(400, 110, 450, 35);
        apiDescription2.setFont(new Font(FONT_FAMILY, Font.PLAIN, 32));
        apiDescription2.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel apiDescription3 = new JLabel("OpenWeatherMap");
        apiDescription3.setBounds(800, 110, 450, 35);
        apiDescription3.setFont(new Font(FONT_FAMILY, Font.PLAIN, 32));
        apiDescription3.setHorizontalAlignment(SwingConstants.CENTER);
        add(apiDescription1);
        add(apiDescription2);
        add(apiDescription3);
    }

    /**
     * Adding a weather description
     */
    private void addWeatherDescription() {
        apiWeatherDescription1 = new JLabel("Облачно");
        apiWeatherDescription1.setBounds(0, 440, 450, 35);
        apiWeatherDescription1.setFont(new Font(FONT_FAMILY, Font.PLAIN, 32));
        apiWeatherDescription1.setHorizontalAlignment(SwingConstants.CENTER);

        apiWeatherDescription2 = new JLabel("Облачно");
        apiWeatherDescription2.setBounds(400, 440, 450, 35);
        apiWeatherDescription2.setFont(new Font(FONT_FAMILY, Font.PLAIN, 32));
        apiWeatherDescription2.setHorizontalAlignment(SwingConstants.CENTER);

        apiWeatherDescription3 = new JLabel("Облачно");
        apiWeatherDescription3.setBounds(800, 440, 450, 35);
        apiWeatherDescription3.setFont(new Font(FONT_FAMILY, Font.PLAIN, 32));
        apiWeatherDescription3.setHorizontalAlignment(SwingConstants.CENTER);
        add(apiWeatherDescription1);
        add(apiWeatherDescription2);
        add(apiWeatherDescription3);
    }

    /**
     * Adding a temperature text
     */
    private void addTemperatureText() {
        apiTemperatureText1 = new JLabel("25º");
        apiTemperatureText1.setBounds(0, 380, 450, 55);
        apiTemperatureText1.setFont(new Font(FONT_FAMILY, Font.BOLD, 48));
        apiTemperatureText1.setHorizontalAlignment(SwingConstants.CENTER);

        apiTemperatureText2 = new JLabel("25º");
        apiTemperatureText2.setBounds(400, 380, 450, 55);
        apiTemperatureText2.setFont(new Font(FONT_FAMILY, Font.BOLD, 48));
        apiTemperatureText2.setHorizontalAlignment(SwingConstants.CENTER);

        apiTemperatureText3 = new JLabel("25º");
        apiTemperatureText3.setBounds(800, 380, 450, 55);
        apiTemperatureText3.setFont(new Font(FONT_FAMILY, Font.BOLD, 48));
        apiTemperatureText3.setHorizontalAlignment(SwingConstants.CENTER);
        add(apiTemperatureText1);
        add(apiTemperatureText2);
        add(apiTemperatureText3);
    }

    /**
     * Adding a weather image
     */
    private void addWeatherImage() {
        apiWeatherConditionImage1 = new JLabel(loadImage(CLOUDY_RESOURCE_PATH));
        apiWeatherConditionImage1.setBounds(0, 165, 450, 220);

        apiWeatherConditionImage2 = new JLabel(loadImage(CLOUDY_RESOURCE_PATH));
        apiWeatherConditionImage2.setBounds(400, 165, 450, 220);

        apiWeatherConditionImage3 = new JLabel(loadImage(CLOUDY_RESOURCE_PATH));
        apiWeatherConditionImage3.setBounds(800, 165, 450, 220);
        add(apiWeatherConditionImage1);
        add(apiWeatherConditionImage2);
        add(apiWeatherConditionImage3);
    }

    /**
     * Adding a search button
     * and assigning an event listener that will load data from APIs
     * and place it on labels
     */
    private void addSearchButton() {
        JButton searchButton = new JButton(loadImage(SEARCH_RESOURCE_PATH));
        searchButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        searchButton.setBounds(910, 15, 50, 45);
        searchButton.addActionListener(event -> {
            String userCityInput = searchTextField.getText();
            getWeather(userCityInput);
        });
        add(searchButton);
    }

    /**
     * Adding a text search field
     */
    private void addSearchField() {
        searchTextField = new JTextField();
        searchTextField.setBounds(400, 15, 500, 45);
        searchTextField.setFont(new Font(FONT_FAMILY, Font.PLAIN, 24));
        add(searchTextField);
    }

    /**
     * Uploading a photo along the way. Otherwise, null is returned
     *
     * @param resourcePath path to image resource
     * @return ImageIcon by image
     */
    private ImageIcon loadImage(String resourcePath) {
        try {
            BufferedImage image = ImageIO.read(new File(resourcePath));
            return new ImageIcon(image);
        } catch (IOException exception) {
            System.out.println(exception.getMessage());
        }
        System.out.println("Could not find resource");
        return null;
    }

    private void getWeather(String cityName) {
        if (cityName.replaceAll("\\s", "").length() == 0) {
            return;
        }
        JSONObject weatherData = WeatherApp.getWeatherFromService(weatherMeteoService, cityName);
        JSONObject weatherData2 = WeatherApp.getWeatherFromService(weatherApiService, cityName);
        JSONObject weatherData3 = WeatherApp.getWeatherFromService(openWeatherMapService, cityName);

        String weatherCondition1 = (String) weatherData.get("weatherCondition");
        switch (weatherCondition1) {
            case "Ясно" -> {
                apiWeatherConditionImage1.setIcon(loadImage(CLEAR_RESOURCE_PATH));
                apiWeatherConditionImage2.setIcon(loadImage(CLEAR_RESOURCE_PATH));
                apiWeatherConditionImage3.setIcon(loadImage(CLEAR_RESOURCE_PATH));
            }
            case "Облачно" -> {
                apiWeatherConditionImage1.setIcon(loadImage(CLOUDY_RESOURCE_PATH));
                apiWeatherConditionImage2.setIcon(loadImage(CLOUDY_RESOURCE_PATH));
                apiWeatherConditionImage3.setIcon(loadImage(CLOUDY_RESOURCE_PATH));
            }
            case "Дождь" -> {
                apiWeatherConditionImage1.setIcon(loadImage(RAIN_RESOURCE_PATH));
                apiWeatherConditionImage2.setIcon(loadImage(RAIN_RESOURCE_PATH));
                apiWeatherConditionImage3.setIcon(loadImage(RAIN_RESOURCE_PATH));
            }
            case "Снег" -> {
                apiWeatherConditionImage1.setIcon(loadImage(SNOW_RESOURCE_PATH));
                apiWeatherConditionImage2.setIcon(loadImage(SNOW_RESOURCE_PATH));
                apiWeatherConditionImage3.setIcon(loadImage(SNOW_RESOURCE_PATH));
            }
        }

        apiWeatherDescription1.setText(weatherCondition1);
        apiWeatherDescription2.setText(weatherCondition1);
        apiWeatherDescription3.setText(weatherCondition1);

        DecimalFormat decimalFormat = new DecimalFormat("#.#");
        double temperature1 = (double) weatherData.get("temperature");
        double temperature2 = (double) weatherData2.get("temperature");
        double temperature3 = (double) weatherData3.get("temperature");
        apiTemperatureText1.setText(decimalFormat.format(temperature1) + "º");
        apiTemperatureText2.setText(decimalFormat.format(temperature2) + "º");
        apiTemperatureText3.setText(decimalFormat.format(temperature3) + "º");

        apiHumidityDescription1.setText("<html><b>Влажность</b> " + weatherData.get("humidity") + "%</html>");
        apiHumidityDescription2.setText("<html><b>Влажность</b> " + weatherData2.get("humidity") + "%</html>");
        apiHumidityDescription3.setText("<html><b>Влажность</b> " + weatherData3.get("humidity") + "%</html>");

        apiWindSpeedDescription1.setText("<html><b>Скорость ветра</b> " + weatherData.get("windSpeed") + "км/ч</html>");
        apiWindSpeedDescription2.setText("<html><b>Скорость ветра</b> " + weatherData2.get("windSpeed") + "км/ч</html>");
        apiWindSpeedDescription3.setText("<html><b>Скорость ветра</b> " + weatherData2.get("windSpeed") + "км/ч</html>");

        double averageTemperature = (temperature1 + temperature2 + temperature3) / 3;
        averageTemperatureText.setText("Средняя температура: " + decimalFormat.format(averageTemperature) + "º");
    }
}
