package ru.kontur.view;

import org.json.simple.JSONObject;
import ru.kontur.database.FavoriteService;
import ru.kontur.WeatherApp;
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

public class WeatherIO extends JFrame {

    // Main window parameters
    private static final String TITLE = "Погода";
    private static final String FONT_FAMILY = "Roboto";
    private static final String DEFAULT_CITY = "Екатеринбург";
    private static final int WIDTH = 1300;
    private static final int HEIGHT = 750;

    // Paths to resources
    private static final String SEARCH_RESOURCE_PATH = "assets/search.png";
    private static final String FAVORITES_ICON_PATH = "assets/favorite.png";
    private static final String CLEAR_RESOURCE_PATH = "assets/clear.png";
    private static final String CLOUDY_RESOURCE_PATH = "assets/cloudy.png";
    private static final String RAIN_RESOURCE_PATH = "assets/rain.png";
    private static final String SNOW_RESOURCE_PATH = "assets/snow.png";
    private static final String JOURNAL_RESOURCE_PATH = "assets/journal.png";
    private static final String SUN_RESOURCE_PATH = "assets/sun.png";
    private static final String MOON_RESOURCE_PATH = "assets/moon.png";

    // Basic information fields
    private static JTextField searchTextField;
    private JLabel apiWeatherConditionImage1, apiWeatherConditionImage2, apiWeatherConditionImage3;
    private JLabel apiTemperatureText1, apiTemperatureText2, apiTemperatureText3;
    private JLabel apiWeatherDescription1, apiWeatherDescription2, apiWeatherDescription3;
    private JLabel apiHumidityDescription1, apiHumidityDescription2, apiHumidityDescription3;
    private JLabel apiWindSpeedDescription1, apiWindSpeedDescription2, apiWindSpeedDescription3;
    private JLabel averageTemperatureText;

    // To work with the database
    private final FavoriteService favoriteService;
    private final DefaultListModel<String> listModel;
    private JButton btnShowFavorites;

    // API
    private final WeatherMeteoService weatherMeteoService = new WeatherMeteoService();
    private final WeatherApiService weatherApiService = new WeatherApiService();
    private final OpenWeatherMapService openWeatherMapService = new OpenWeatherMapService();

    // Translator for city names
    private static final TranslatorService translator = new TranslatorService();

    // To change window theme
    private JButton themeToggleButton;
    private boolean isDayMode = true;
    private JPanel weatherBlock1, weatherBlock2, weatherBlock3;

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
        try {
            getWeather(translator.translateCityName(DEFAULT_CITY, "ru", "en"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        searchTextField.setText(DEFAULT_CITY);
        updateFavoriteList();
    }

    /**
     * The method adds all the main components to the window
     */
    private void addComponents() {
        addTopPanel();
        addApiLabels();
        addWeatherBlocks();
        addAverageTemperatureText();
    }

    /**
     * The method adds API names to the window
     */
    private void addApiLabels() {
        JPanel apiLabelsPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        apiLabelsPanel.setBounds(0, 80, WIDTH, 30);

        JLabel label1 = new JLabel("WeatherMeteo", SwingConstants.CENTER);
        JLabel label2 = new JLabel("WeatherAPI", SwingConstants.CENTER);
        JLabel label3 = new JLabel("OpenWeatherMap", SwingConstants.CENTER);

        label1.setFont(new Font(FONT_FAMILY, Font.PLAIN, 20));
        label2.setFont(new Font(FONT_FAMILY, Font.PLAIN, 20));
        label3.setFont(new Font(FONT_FAMILY, Font.PLAIN, 20));

        apiLabelsPanel.add(label1);
        apiLabelsPanel.add(label2);
        apiLabelsPanel.add(label3);
        add(apiLabelsPanel);
    }

    /**
     * The method adds weather information
     * (temperature, weather icon, humidity and wind speed)
     * using the API index
     *
     * @param index API index
     * @return JPanel for one API with prepared information
     */
    private JPanel createWeatherBlock(int index) {
        JPanel block = new JPanel();
        block.setLayout(new BoxLayout(block, BoxLayout.Y_AXIS));
        block.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel weatherIcon = new JLabel();
        JLabel temperatureText = new JLabel();
        JLabel weatherDescription = new JLabel();
        JLabel humidityDescription = new JLabel();
        JLabel windSpeedDescription = new JLabel();

        weatherIcon.setAlignmentX(Component.CENTER_ALIGNMENT);
        temperatureText.setAlignmentX(Component.CENTER_ALIGNMENT);
        weatherDescription.setAlignmentX(Component.CENTER_ALIGNMENT);
        humidityDescription.setAlignmentX(Component.CENTER_ALIGNMENT);
        windSpeedDescription.setAlignmentX(Component.CENTER_ALIGNMENT);

        temperatureText.setFont(new Font(FONT_FAMILY, Font.BOLD, 36));
        weatherDescription.setFont(new Font(FONT_FAMILY, Font.PLAIN, 20));
        humidityDescription.setFont(new Font(FONT_FAMILY, Font.PLAIN, 16));
        windSpeedDescription.setFont(new Font(FONT_FAMILY, Font.PLAIN, 16));

        switch (index) {
            case 1 -> {
                apiWeatherConditionImage1 = weatherIcon;
                apiTemperatureText1 = temperatureText;
                apiWeatherDescription1 = weatherDescription;
                apiHumidityDescription1 = humidityDescription;
                apiWindSpeedDescription1 = windSpeedDescription;
            }
            case 2 -> {
                apiWeatherConditionImage2 = weatherIcon;
                apiTemperatureText2 = temperatureText;
                apiWeatherDescription2 = weatherDescription;
                apiHumidityDescription2 = humidityDescription;
                apiWindSpeedDescription2 = windSpeedDescription;
            }
            case 3 -> {
                apiWeatherConditionImage3 = weatherIcon;
                apiTemperatureText3 = temperatureText;
                apiWeatherDescription3 = weatherDescription;
                apiHumidityDescription3 = humidityDescription;
                apiWindSpeedDescription3 = windSpeedDescription;
            }
        }

        block.add(weatherIcon);
        block.add(Box.createVerticalStrut(10));
        block.add(temperatureText);
        block.add(Box.createVerticalStrut(5));
        block.add(weatherDescription);
        block.add(Box.createVerticalStrut(10));
        block.add(humidityDescription);
        block.add(Box.createVerticalStrut(5));
        block.add(windSpeedDescription);
        return block;
    }

    /**
     * The method creates blocks with weather information from all three API
     */
    private void addWeatherBlocks() {
        JPanel weatherPanel = new JPanel();
        weatherPanel.setLayout(new GridLayout(1, 3, 10, 0));
        weatherPanel.setBounds(0, 120, WIDTH, 400);

        weatherBlock1 = createWeatherBlock(1);
        weatherBlock2 = createWeatherBlock(2);
        weatherBlock3 = createWeatherBlock(3);

        weatherPanel.add(weatherBlock1);
        weatherPanel.add(weatherBlock2);
        weatherPanel.add(weatherBlock3);
        add(weatherPanel);
    }

    /**
     * The method adds a top panel
     * (search field, button and favorite locations,
     * add to favorites button and forecast log)
     */
    private void addTopPanel() {
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));

        searchTextField = new JTextField();
        searchTextField.setFont(new Font(FONT_FAMILY, Font.PLAIN, 24));
        searchTextField.setColumns(30);
        topPanel.add(searchTextField);

        JButton searchButton = new JButton(loadImage(SEARCH_RESOURCE_PATH));
        searchButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        searchButton.addActionListener(event -> {
            String userCityInput = searchTextField.getText();
            try {
                getWeather(translator.translateCityName(userCityInput, "ru", "en"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        topPanel.add(searchButton);

        btnShowFavorites = new JButton(loadImage(FAVORITES_ICON_PATH));
        btnShowFavorites.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnShowFavorites.setBackground(Color.WHITE);
        btnShowFavorites.setBorder(BorderFactory.createEmptyBorder());
        btnShowFavorites.addActionListener(e -> showFavoritesList());
        topPanel.add(btnShowFavorites);

        JButton btnShowJournal = new JButton(loadImage(JOURNAL_RESOURCE_PATH));
        btnShowJournal.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnShowJournal.setBackground(Color.WHITE);
        btnShowJournal.setBorder(BorderFactory.createEmptyBorder());
        btnShowJournal.addActionListener(e -> showJournalList());
        topPanel.add(btnShowJournal);

        JButton btnAddCity = new JButton("Добавить в избранное");
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
        topPanel.add(btnAddCity);
        topPanel.setBounds(0, 0, WIDTH, 70);

        themeToggleButton = new JButton(loadImage(MOON_RESOURCE_PATH));
        themeToggleButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        themeToggleButton.setBackground(Color.WHITE);
        themeToggleButton.setBorder(BorderFactory.createEmptyBorder());
        themeToggleButton.addActionListener(e -> toggleTheme());
        topPanel.add(themeToggleButton);
        add(topPanel, BorderLayout.NORTH);
    }

    /**
     * The method changes the screen background and text color
     */
    private void toggleTheme() {
        isDayMode = !isDayMode;
        themeToggleButton.setIcon(loadImage(isDayMode ? MOON_RESOURCE_PATH : SUN_RESOURCE_PATH));

        Color bgColor = isDayMode ? Color.WHITE : Color.GRAY;
        Color textColor = isDayMode ? Color.BLACK : Color.WHITE;
        getContentPane().setBackground(bgColor);
        for (Component component : getContentPane().getComponents()) {
            if (component instanceof JPanel panel) {
                panel.setBackground(bgColor);
            }
        }

        if (weatherBlock1 != null) weatherBlock1.setBackground(bgColor);
        if (weatherBlock2 != null) weatherBlock2.setBackground(bgColor);
        if (weatherBlock3 != null) weatherBlock3.setBackground(bgColor);

        updateWeatherTextColor(weatherBlock1, textColor);
        updateWeatherTextColor(weatherBlock2, textColor);
        updateWeatherTextColor(weatherBlock3, textColor);
    }

    /**
     * The method changes the text color of the panel
     *
     * @param panel     panel whose color needs to be changed
     * @param textColor color to change to
     */
    private void updateWeatherTextColor(JPanel panel, Color textColor) {
        for (Component component : panel.getComponents()) {
            if (component instanceof JLabel label) {
                label.setForeground(textColor);
            }
        }
    }

    /**
     * The method updates the list from the database
     * to display current data from the list
     * with selected locations when adding or removing from this list
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
     * The method displays a list with selected locations
     * obtained from the database with the ability
     * to quickly access by name and delete from the list and database
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
     * The method allows you to open a new window with an hourly weather forecast
     * from the API for several days in advance, as well as plotting a graph for clarity
     */
    private void showJournalList() {

            ForecastIO forecastWindow = new ForecastIO();
            forecastWindow.setVisible(true);
            try {
                forecastWindow.loadForecasts(translator.translateCityName(searchTextField.getText(), "ru", "en"));
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        
    }

    /**
     * The method removes the location from the database
     * and updates the list rendering to display the latest data
     *
     * @param cityName city name to be removed from the list
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
     * The method adds a block with the average temperature for all three API
     */
    private void addAverageTemperatureText() {
        JPanel averagePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        averagePanel.setBounds(0, 530, WIDTH, 40); // Можно подвинуть выше/ниже по вкусу

        averageTemperatureText = new JLabel();
        averageTemperatureText.setFont(new Font(FONT_FAMILY, Font.BOLD, 24));

        averagePanel.add(averageTemperatureText);
        add(averagePanel);
    }

    /**
     * The method loads the required icon from the resource folder
     *
     * @param relativePath path to the required icon
     * @return required icon
     */
    private ImageIcon loadImage(String relativePath) {
        try {
            return new ImageIcon(ImageIO.read(getClass().getClassLoader().getResourceAsStream(relativePath)));
        } catch (IOException | NullPointerException e) {
            System.err.println("Не удалось загрузить изображение: " + relativePath);
            return null;
        }
    }

    /**
     * The method receives weather data from the API
     * and executes the renderWeatherData method
     *
     * @param cityName city name
     */
    private void getWeather(String cityName) {
        if (cityName.replaceAll("\\s", "").isEmpty()) return;
        JSONObject[] weatherData = fetchWeatherData(cityName);
        renderWeatherData(weatherData);
    }

    /**
     * The method receives data from three APIs into different JSONObjects
     * and returns an array of three components as a JSONObject
     *
     * @param cityName city name
     * @return array with three JSONObject
     */
    private JSONObject[] fetchWeatherData(String cityName) {
        JSONObject data1 = WeatherApp.getWeatherFromService(weatherMeteoService, cityName);
        JSONObject data2 = WeatherApp.getWeatherFromService(weatherApiService, cityName);
        JSONObject data3 = WeatherApp.getWeatherFromService(openWeatherMapService, cityName);
        if (data1 == null && data2 == null && data3 == null) {
            throw new IllegalArgumentException("Не удалось получить данные о погоде ни из одного источника.");
        }
        return new JSONObject[]{data1, data2, data3};
    }

    /**
     * The method updates weather icons based on currently received data
     *
     * @param weatherCondition weather condition
     */
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

    /**
     * Method for updating data in all blocks with weather conditions
     *
     * @param weatherData array with JSONObjects
     */
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

        apiHumidityDescription1.setText("<html><b>Влажность:</b> " + d1.get("humidity") + "%</html>");
        apiHumidityDescription2.setText("<html><b>Влажность:</b> " + d2.get("humidity") + "%</html>");
        apiHumidityDescription3.setText("<html><b>Влажность:</b> " + d3.get("humidity") + "%</html>");

        apiWindSpeedDescription1.setText("<html><b>Скорость ветра:</b> " + d1.get("windSpeed") + " м/с</html>");
        apiWindSpeedDescription2.setText("<html><b>Скорость ветра:</b> " + df.format(d2.get("windSpeed")) + " м/с</html>");
        apiWindSpeedDescription3.setText("<html><b>Скорость ветра:</b> " + d3.get("windSpeed") + " м/с</html>");

        double avgTemp = (t1 + t2 + t3) / 3;
        averageTemperatureText.setText("Средняя температура: " + df.format(avgTemp) + "º");
    }
}