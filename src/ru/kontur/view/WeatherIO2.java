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

public class WeatherIO2 extends JFrame {

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

    // Basic information fields
    private static JTextField searchTextField;
    private JLabel apiWeatherConditionImage1, apiWeatherConditionImage2, apiWeatherConditionImage3;
    private JLabel apiTemperatureText1, apiTemperatureText2, apiTemperatureText3;
    private JLabel apiWeatherDescription1, apiWeatherDescription2, apiWeatherDescription3;
    private JLabel apiHumidityDescription1, apiHumidityDescription2, apiHumidityDescription3;
    private JLabel apiWindSpeedDescription1, apiWindSpeedDescription2, apiWindSpeedDescription3;
    private JLabel averageTemperatureText;
    private JPanel topPanel;
    private JButton btnShowJournal;

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

    private void addComponents() {
        addTopPanel();
        addApiLabels();
        addWeatherBlocks();
        addAverageTemperatureText();
    }

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

    private void addWeatherBlocks() {
        JPanel weatherPanel = new JPanel();
        weatherPanel.setLayout(new GridLayout(1, 3, 10, 0));
        weatherPanel.setBounds(0, 120, WIDTH, 400);

        JPanel block1 = createWeatherBlock(1);
        JPanel block2 = createWeatherBlock(2);
        JPanel block3 = createWeatherBlock(3);

        weatherPanel.add(block1);
        weatherPanel.add(block2);
        weatherPanel.add(block3);

        add(weatherPanel);
    }

    private void addTopPanel() {
        topPanel = new JPanel();
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

        btnShowJournal = new JButton(loadImage(JOURNAL_RESOURCE_PATH));
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
        add(topPanel, BorderLayout.NORTH);
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

    private void showJournalList() {
        JPopupMenu popupMenu = new JPopupMenu();

        JMenuItem journal1Item = new JMenuItem("История");
        journal1Item.addActionListener(e -> {
            HistoryIO historyWindow = new HistoryIO();
            historyWindow.setVisible(true);
        });
        popupMenu.add(journal1Item);

        JMenuItem journal2Item = new JMenuItem("Прогноз");
        journal2Item.addActionListener(e -> {
            ForecastIO forecastWindow = new ForecastIO();
            forecastWindow.setVisible(true);
        });
        popupMenu.add(journal2Item);

        popupMenu.show(btnShowFavorites, 100, btnShowFavorites.getHeight());
    }

    private void removeCityFromFavorites(String cityName) {
        try {
            favoriteService.removeCityFromFavorites(cityName);
            updateFavoriteList();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    private void addAverageTemperatureText() {
        JPanel averagePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        averagePanel.setBounds(0, 530, WIDTH, 40); // Можно подвинуть выше/ниже по вкусу

        averageTemperatureText = new JLabel();
        averageTemperatureText.setFont(new Font(FONT_FAMILY, Font.BOLD, 24));

        averagePanel.add(averageTemperatureText);
        add(averagePanel);
    }

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

        apiHumidityDescription1.setText("<html><b>Влажность:</b> " + d1.get("humidity") + "%</html>");
        apiHumidityDescription2.setText("<html><b>Влажность:</b> " + d2.get("humidity") + "%</html>");
        apiHumidityDescription3.setText("<html><b>Влажность:</b> " + d3.get("humidity") + "%</html>");

        apiWindSpeedDescription1.setText("<html><b>Скорость ветра:</b> " + d1.get("windSpeed") + "км/ч</html>");
        apiWindSpeedDescription2.setText("<html><b>Скорость ветра:</b> " + d2.get("windSpeed") + "км/ч</html>");
        apiWindSpeedDescription3.setText("<html><b>Скорость ветра:</b> " + d3.get("windSpeed") + "км/ч</html>");

        double avgTemp = (t1 + t2 + t3) / 3;
        averageTemperatureText.setText("Средняя температура: " + df.format(avgTemp) + "º");
    }
}
