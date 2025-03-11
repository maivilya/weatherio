package ru.kontur;

import org.json.simple.JSONObject;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class WeatherIO extends JFrame {

    private static final String TITLE = "Погода";
    private static final String FONT_FAMILY = "Roboto";
    private static final String SEARCH_RESOURCE_PATH = "src/ru/kontur/assets/search.png";
    private static final String FAVORITES_ICON_PATH = "src/ru/kontur/assets/favorite.png";
    private static final String CLEAR_RESOURCE_PATH = "src/ru/kontur/assets/clear.png";
    private static final String CLOUDY_RESOURCE_PATH = "src/ru/kontur/assets/cloudy.png";
    private static final String RAIN_RESOURCE_PATH = "src/ru/kontur/assets/rain.png";
    private static final String SNOW_RESOURCE_PATH = "src/ru/kontur/assets/snow.png";
    private static final String HUMIDITY_RESOURCE_PATH = "src/ru/kontur/assets/humidity.png";
    private static final String WIND_SPEED_RESOURCE_PATH = "src/ru/kontur/assets/windSpeed.png";
    private static JTextField searchTextField;
    private static JSONObject weatherData;
    private JLabel weatherConditionImage;
    private JLabel temperatureText;
    private JLabel weatherDescription;
    private JLabel humidityDescription;
    private JLabel windSpeedDescription;
    private static final int WIDTH = 450;
    private static final int HEIGHT = 650;

    private final FavoriteService favoriteService;
    private final DefaultListModel<String> listModel;
    private JButton btnShowFavorites;

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
        btnShowFavorites = new JButton(loadImage(FAVORITES_ICON_PATH)); // Иконка для избранных
        btnShowFavorites.setBounds(375, 70, 45, 45);
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
        JButton btnAddCity = new JButton("Добавить город");
        btnAddCity.setBounds(220, 70, 150, 30);
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
     * Adding a wind speed description
     */
    private void addWindSpeedDescription() {
        windSpeedDescription = new JLabel("<html><b>Скорость ветра</b>\n3 км/ч</html>");
        windSpeedDescription.setBounds(310, 500, 140, 65);
        windSpeedDescription.setFont(new Font(FONT_FAMILY, Font.PLAIN, 18));
        add(windSpeedDescription);
    }

    /**
     * Adding a wind speed image
     */
    private void addWindSpeedImage() {
        JLabel windSpeedImage = new JLabel(loadImage(WIND_SPEED_RESOURCE_PATH));
        windSpeedImage.setBounds(220, 500, 75, 65);
        add(windSpeedImage);
    }

    /**
     * Adding a humidity description
     */
    private void addHumidityDescription() {
        humidityDescription = new JLabel("<html><b>Влажность</b> 100%</html>");
        humidityDescription.setBounds(90, 500, 110, 65);
        humidityDescription.setFont(new Font(FONT_FAMILY, Font.PLAIN, 18));
        add(humidityDescription);
    }

    /**
     * Adding a humidity image
     */
    private void addHumidityImage() {
        JLabel humidityImage = new JLabel(loadImage(HUMIDITY_RESOURCE_PATH));
        humidityImage.setBounds(15, 500, 75, 65);
        add(humidityImage);
    }

    /**
     * Adding a weather description
     */
    private void addWeatherDescription() {
        weatherDescription = new JLabel("Облачно");
        weatherDescription.setBounds(0, 405, 450, 35);
        weatherDescription.setFont(new Font(FONT_FAMILY, Font.PLAIN, 32));
        weatherDescription.setHorizontalAlignment(SwingConstants.CENTER);
        add(weatherDescription);
    }

    /**
     * Adding a temperature text
     */
    private void addTemperatureText() {
        temperatureText = new JLabel("25º");
        temperatureText.setBounds(0, 350, 450, 55);
        temperatureText.setFont(new Font(FONT_FAMILY, Font.BOLD, 48));
        temperatureText.setHorizontalAlignment(SwingConstants.CENTER);
        add(temperatureText);
    }

    /**
     * Adding a weather image
     */
    private void addWeatherImage() {
        weatherConditionImage = new JLabel(loadImage(CLOUDY_RESOURCE_PATH));
        weatherConditionImage.setBounds(0, 125, 450, 220);
        add(weatherConditionImage);
    }

    /**
     * Adding a search button
     * and assigning an event listener that will load data from APIs
     * and place it on labels
     */
    private void addSearchButton() {
        JButton searchButton = new JButton(loadImage(SEARCH_RESOURCE_PATH));
        searchButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        searchButton.setBounds(375, 15, 50, 45);
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
        searchTextField.setBounds(15, 15, 350, 45);
        searchTextField.setFont(new Font(FONT_FAMILY, Font.PLAIN, 24));
        add(searchTextField);
    }

    /**
     * Uploading a photo along the way. Otherwise, null is returned
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
        weatherData = WeatherApp.getWeatherData(cityName);
        assert weatherData != null;
        String weatherCondition = (String) weatherData.get("weatherCondition");
        switch (weatherCondition) {
            case "Ясно" -> weatherConditionImage.setIcon(loadImage(CLEAR_RESOURCE_PATH));
            case "Облачно" -> weatherConditionImage.setIcon(loadImage(CLOUDY_RESOURCE_PATH));
            case "Дождь" -> weatherConditionImage.setIcon(loadImage(RAIN_RESOURCE_PATH));
            case "Снег" -> weatherConditionImage.setIcon(loadImage(SNOW_RESOURCE_PATH));
        }
        weatherDescription.setText(weatherCondition);

        double temperature = (double) weatherData.get("temperature");
        temperatureText.setText(temperature + "º");

        long humidity = (long) weatherData.get("humidity");
        humidityDescription.setText("<html><b>Влажность</b> " + humidity + "%</html>");

        double windSpeed = (double) weatherData.get("windSpeed");
        windSpeedDescription.setText("<html><b>Скорость ветра</b> " + windSpeed + "км/ч</html>");
    }
}
