package ru.kontur;

import org.json.simple.JSONObject;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class WeatherIO extends JFrame {

    private static final String TITLE = "Погода";
    private static final String FONT_FAMILY = "Roboto";
    private static final String SEARCH_RESOURCE_PATH = "src/ru/kontur/assets/search.png";
    private static final String CLEAR_RESOURCE_PATH = "src/ru/kontur/assets/clear.png";
    private static final String CLOUDY_RESOURCE_PATH = "src/ru/kontur/assets/cloudy.png";
    private static final String RAIN_RESOURCE_PATH = "src/ru/kontur/assets/rain.png";
    private static final String SNOW_RESOURCE_PATH = "src/ru/kontur/assets/snow.png";
    private static final String HUMIDITY_RESOURCE_PATH = "src/ru/kontur/assets/humidity.png";
    private static final String WIND_SPEED_RESOURCE_PATH = "src/ru/kontur/assets/windSpeed.png";
    private static final String FAVORITES_ICON_PATH = "src/ru/kontur/assets/favorite.png";
    private static JTextField searchTextField;
    private static JSONObject weatherData;
    private JLabel weatherConditionImage;
    private JLabel temperatureText;
    private JLabel weatherDescription;
    private JLabel humidityDescription;
    private JLabel windSpeedDescription;
    private static final int WIDTH = 450;
    private static final int HEIGHT = 650;

    // test list favorites
    private FavoriteService favoriteService;
    private JList<String> citiesList;
    private DefaultListModel<String> listModel;
    private JButton btnAddCity;
    private JButton btnRemoveCity;
    private JButton btnShowFavorites;

    public WeatherIO() {
        favoriteService = new FavoriteService();
        listModel = new DefaultListModel<>();
        citiesList = new JList<>(listModel);
        JScrollPane scrollPane = new JScrollPane(citiesList);
        setTitle(TITLE);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null);
        setLayout(null);
        setResizable(false);
        add(scrollPane);
        addComponents();



       updateFavoriteList();
    }

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
        addBtnAddCity();
        addBtnRemoveCity();
        addBtnFavorites();
    }

    private void addBtnFavorites() {
        JButton btnShowFavorites = new JButton("Избранные");
        btnShowFavorites.setBounds(340, 30, 90, 30);
        btnShowFavorites.addActionListener(e -> updateFavoriteList());
        add(btnShowFavorites);
    }

    private void showFavoritesList() {
        JPopupMenu popupMenu = new JPopupMenu();
        for (int i = 0; i < listModel.size(); i++) {
            String city = listModel.getElementAt(i);
            JMenuItem menuItem = new JMenuItem(city);
            menuItem.addActionListener(e -> {
                // Тут можно вызвать метод для отображения погоды для выбранного города
                // Например: showWeather(city);
                System.out.println("Погода для города: " + city);
            });
            JMenuItem removeItem = new JMenuItem("Удалить");
            removeItem.addActionListener(e -> removeCityFromFavorites(city));
            popupMenu.add(menuItem);
            popupMenu.add(removeItem);
        }
        popupMenu.show(btnShowFavorites, 0, btnShowFavorites.getHeight());
    }

    private void addBtnRemoveCity() {
        JButton btnRemoveCity = new JButton("Удалить город");
        btnRemoveCity.setBounds(320, 70, 120, 30);
        btnRemoveCity.addActionListener(e -> {
            String cityName = citiesList.getSelectedValue();
            if (cityName != null) {
                try {
                    favoriteService.removeCityFromFavorites(cityName);
                    updateFavoriteList();
                } catch (SQLException exception) {
                    exception.printStackTrace();
                }
            }
        });
        add(btnRemoveCity);
    }

    private void addBtnAddCity() {
        btnAddCity = new JButton("Добавить город");
        btnAddCity.setBounds(300, 110, 150, 30);
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
     * Adding a (plug) wind speed description
     */
    private void addWindSpeedDescription() {
        windSpeedDescription = new JLabel("<html><b>Скорость ветра</b>\n3 км/ч</html>");
        windSpeedDescription.setBounds(310, 500, 140, 65);
        windSpeedDescription.setFont(new Font(FONT_FAMILY, Font.PLAIN, 18));
        add(windSpeedDescription);
    }

    /**
     * Adding a (plug) wind speed image
     */
    private void addWindSpeedImage() {
        JLabel windSpeedImage = new JLabel(loadImage(WIND_SPEED_RESOURCE_PATH));
        windSpeedImage.setBounds(220, 500, 75, 65);
        add(windSpeedImage);
    }

    /**
     * Adding a (plug) humidity description
     */
    private void addHumidityDescription() {
        humidityDescription = new JLabel("<html><b>Влажность</b> 100%</html>");
        humidityDescription.setBounds(90, 500, 110, 65);
        humidityDescription.setFont(new Font(FONT_FAMILY, Font.PLAIN, 18));
        add(humidityDescription);
    }

    /**
     * Adding a (plug) humidity image
     */
    private void addHumidityImage() {
        JLabel humidityImage = new JLabel(loadImage(HUMIDITY_RESOURCE_PATH));
        humidityImage.setBounds(15, 500, 75, 65);
        add(humidityImage);
    }

    /**
     * Adding a (plug) weather description
     */
    private void addWeatherDescription() {
        weatherDescription = new JLabel("Облачно");
        weatherDescription.setBounds(0, 405, 450, 35);
        weatherDescription.setFont(new Font(FONT_FAMILY, Font.PLAIN, 32));
        weatherDescription.setHorizontalAlignment(SwingConstants.CENTER);
        add(weatherDescription);
    }

    /**
     * Adding a (plug) temperature text
     */
    private void addTemperatureText(){
        temperatureText = new JLabel("25º");
        temperatureText.setBounds(0, 350, 450, 55);
        temperatureText.setFont(new Font(FONT_FAMILY, Font.BOLD, 48));
        temperatureText.setHorizontalAlignment(SwingConstants.CENTER);
        add(temperatureText);
    }

    /**
     * Adding a (plug) weather image
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
            if (userCityInput.replaceAll("\\s", "").length() == 0) {
                return;
            }
            weatherData = WeatherApp.getWeatherData(userCityInput);
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
     * @param resourcePath Path to image resource
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
}
