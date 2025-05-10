package ru.kontur.view;

import org.json.simple.JSONObject;
import ru.kontur.CityAutoCompleteComponent;
import ru.kontur.database.FavoriteService;
import ru.kontur.WeatherApp;
import ru.kontur.model.service.CityAutoCompleteService;
import ru.kontur.model.service.TranslatorService;
import ru.kontur.model.service.weatherService.OpenWeatherMapService;
import ru.kontur.model.service.weatherService.WeatherApiService;
import ru.kontur.model.service.weatherService.WeatherMeteoService;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.List;

public class WeatherIO2 extends JFrame {

    private static final String TITLE = "Погода";
    private static final String FONT_FAMILY = "Roboto";
    private static final String DEFAULT_CITY = "Екатеринбург";
    private static final int WIDTH = 1300;
    private static final int HEIGHT = 750;

    private static final String SEARCH_RESOURCE_PATH = "assets/search.png";
    private static final String FAVORITES_ICON_PATH = "assets/favorite.png";
    private static final String CLEAR_RESOURCE_PATH = "assets/clear.png";
    private static final String CLOUDY_RESOURCE_PATH = "assets/cloudy.png";
    private static final String RAIN_RESOURCE_PATH = "assets/rain.png";
    private static final String SNOW_RESOURCE_PATH = "assets/snow.png";
    private static final String HUMIDITY_RESOURCE_PATH = "assets/humidity.png";
    private static final String WIND_SPEED_RESOURCE_PATH = "assets/windSpeed.png";

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

    private final WeatherMeteoService weatherMeteoService = new WeatherMeteoService();
    private final WeatherApiService weatherApiService = new WeatherApiService();
    private final OpenWeatherMapService openWeatherMapService = new OpenWeatherMapService();

    private static final TranslatorService translator = new TranslatorService();

    private CityAutoCompleteService cityAutoCompleteService = new CityAutoCompleteService();
    private JPopupMenu suggestionsPopup;
    private boolean isSuggestionClicked;

    public WeatherIO2() {
        favoriteService = new FavoriteService();
        listModel = new DefaultListModel<>();
        setTitle(TITLE);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null);
        setLayout(null);
        setResizable(false);
        addComponents();
        try {
            getWeather(translator.translateCityName(DEFAULT_CITY, "ru", "en"));
        } catch (Exception e) {
            e.printStackTrace();
        }
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
                try {
                    getWeather(translator.translateCityName(city, "ru", "en"));
                    searchTextField.setText(city);
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
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
     * Adding an average temperature text
     */
    private void addAverageTemperatureText() {
        averageTemperatureText = createLabel(450, 590, 450, 35, 24, true, SwingConstants.CENTER);
    }

    /**
     * Adding a wind speed description
     */
    private void addWindSpeedDescription() {
        //TODO: подумать над align
        apiWindSpeedDescription1 = createLabel(295, 490, 140, 65, 18, false, 0);
        apiWindSpeedDescription2 = createLabel(710, 490, 140, 65, 18, false, 0);
        apiWindSpeedDescription3 = createLabel(1125, 490, 140, 65, 18, false, 0);
    }

    /**
     * Adding a wind speed image
     */
    private void addWindSpeedImage() {
        createImageLabel(WIND_SPEED_RESOURCE_PATH, 205, 490, 75, 65);
        createImageLabel(WIND_SPEED_RESOURCE_PATH, 630, 490, 75, 65);
        createImageLabel(WIND_SPEED_RESOURCE_PATH, 1045, 490, 75, 65);
    }

    /**
     * Adding a humidity description
     */
    private void addHumidityDescription() {
        //TODO: подумать над align
        apiHumidityDescription1 = createLabel(90, 490, 110, 65, 18, false, 0);
        apiHumidityDescription2 = createLabel(505, 490, 110, 65, 18, false, 0);
        apiHumidityDescription3 = createLabel(920, 490, 110, 65, 18, false, 0);
    }

    /**
     * Adding a humidity image
     */
    private void addHumidityImage() {
        createImageLabel(HUMIDITY_RESOURCE_PATH, 15, 490, 75, 65);
        createImageLabel(HUMIDITY_RESOURCE_PATH, 430, 490, 75, 65);
        createImageLabel(HUMIDITY_RESOURCE_PATH, 845, 490, 75, 65);
    }

    /**
     * Adding a API description
     */
    private void addApiDescription() {
        JLabel apiDescription1 = createLabel(0, 110, 450, 35, 32, false, SwingConstants.CENTER);
        apiDescription1.setText("WeatherMeteo");
        JLabel apiDescription2 = createLabel(400, 110, 450, 35, 32, false, SwingConstants.CENTER);
        apiDescription2.setText("WeatherAPI");
        JLabel apiDescription3 = createLabel(800, 110, 450, 35, 32, false, SwingConstants.CENTER);
        apiDescription3.setText("OpenWeatherMap");
    }

    /**
     * Adding a weather description
     */
    private void addWeatherDescription() {
        apiWeatherDescription1 = createLabel(0, 440, 450, 35, 32, false, SwingConstants.CENTER);
        apiWeatherDescription2 = createLabel(400, 440, 450, 35, 32, false, SwingConstants.CENTER);
        apiWeatherDescription3 = createLabel(800, 440, 450, 35, 32, false, SwingConstants.CENTER);
    }

    /**
     * Adding a temperature text
     */
    private void addTemperatureText() {
        apiTemperatureText1 = createLabel(0, 380, 450, 55, 48, true, SwingConstants.CENTER);
        apiTemperatureText2 = createLabel(400, 380, 450, 55, 48, true, SwingConstants.CENTER);
        apiTemperatureText3 = createLabel(800, 380, 450, 55, 48, true, SwingConstants.CENTER);
    }

    /**
     * Adding a weather image
     */
    private void addWeatherImage() {
        apiWeatherConditionImage1 = createImageLabel(CLEAR_RESOURCE_PATH, 100, 165, 450, 220);
        apiWeatherConditionImage2 = createImageLabel(CLEAR_RESOURCE_PATH, 500, 165, 450, 220);
        apiWeatherConditionImage3 = createImageLabel(CLEAR_RESOURCE_PATH, 900, 165, 450, 220);
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
            try {
                getWeather(translator.translateCityName(userCityInput, "ru", "en"));
            } catch (Exception e) {
                e.printStackTrace();
            }
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
        //new CityAutoCompleteComponent(searchTextField, cityAutoCompleteService);
    }

    /**
     * Uploading a photo along the way. Otherwise, null is returned
     *
     * @param relativePath path to image resource
     * @return ImageIcon by image
     */
    private ImageIcon loadImage(String relativePath) {
        try {
            return new ImageIcon(ImageIO.read(getClass().getClassLoader().getResourceAsStream(relativePath)));
        } catch (IOException | NullPointerException e) {
            System.err.println("Не удалось загрузить изображение: " + relativePath);
            return null;
        }
    }

    private void getWeather(String cityName) {
        if (cityName.replaceAll("\\s", "").isEmpty()) return;

        JSONObject[] weatherData = fetchWeatherData(cityName);
        renderWeatherData(weatherData);
    }

    private JLabel createLabel(int x, int y, int width, int height, int fontSize, boolean bold, int align) {
        JLabel label = new JLabel();
        label.setBounds(x, y, width, height);
        label.setFont(new Font(FONT_FAMILY, bold ? Font.BOLD : Font.PLAIN, fontSize));
        label.setHorizontalAlignment(align);
        add(label);
        return label;
    }

    private JLabel createImageLabel(String imagePath, int x, int y, int width, int height) {
        JLabel label = new JLabel(loadImage(imagePath));
        label.setBounds(x, y, width, height);
        add(label);
        return label;
    }

    private JSONObject[] fetchWeatherData(String cityName) {
        JSONObject data1 = WeatherApp.getWeatherFromService(weatherMeteoService, cityName);
        JSONObject data2 = WeatherApp.getWeatherFromService(weatherApiService, cityName);
        JSONObject data3 = WeatherApp.getWeatherFromService(openWeatherMapService, cityName);

        if (data1 == null && data2 == null && data3 == null) {
            throw new IllegalArgumentException("Не удалось получить данные о погоде ни из одного источника.");
        }
        return new JSONObject[]{data1, data2, data3};
    }

    private void setWeatherIcons(String weatherCondition) {
        String resource = switch (weatherCondition) {
            case "Ясно" -> CLEAR_RESOURCE_PATH;
            case "Облачно" -> CLOUDY_RESOURCE_PATH;
            case "Дождь" -> RAIN_RESOURCE_PATH;
            case "Снег" -> SNOW_RESOURCE_PATH;
            default -> null;
        };

        if (resource != null) {
            ImageIcon icon = loadImage(resource);
            apiWeatherConditionImage1.setIcon(icon);
            apiWeatherConditionImage2.setIcon(icon);
            apiWeatherConditionImage3.setIcon(icon);
        }
    }

    private void renderWeatherData(JSONObject[] weatherData) {
        JSONObject d1 = weatherData[0];
        JSONObject d2 = weatherData[1];
        JSONObject d3 = weatherData[2];

        String condition = (String) d1.get("weatherCondition");
        setWeatherIcons(condition);
        apiWeatherDescription1.setText(condition);
        apiWeatherDescription2.setText(condition);
        apiWeatherDescription3.setText(condition);

        DecimalFormat df = new DecimalFormat("#.#");
        double t1 = (double) d1.get("temperature");
        double t2 = (double) d2.get("temperature");
        double t3 = (double) d3.get("temperature");

        apiTemperatureText1.setText(df.format(t1) + "º");
        apiTemperatureText2.setText(df.format(t2) + "º");
        apiTemperatureText3.setText(df.format(t3) + "º");

        apiHumidityDescription1.setText("<html><b>Влажность</b> " + d1.get("humidity") + "%</html>");
        apiHumidityDescription2.setText("<html><b>Влажность</b> " + d2.get("humidity") + "%</html>");
        apiHumidityDescription3.setText("<html><b>Влажность</b> " + d3.get("humidity") + "%</html>");

        apiWindSpeedDescription1.setText("<html><b>Скорость ветра</b> " + d1.get("windSpeed") + "км/ч</html>");
        apiWindSpeedDescription2.setText("<html><b>Скорость ветра</b> " + d2.get("windSpeed") + "км/ч</html>");
        apiWindSpeedDescription3.setText("<html><b>Скорость ветра</b> " + d3.get("windSpeed") + "км/ч</html>");

        double avgTemp = (t1 + t2 + t3) / 3;
        averageTemperatureText.setText("Средняя температура: " + df.format(avgTemp) + "º");
    }
}
