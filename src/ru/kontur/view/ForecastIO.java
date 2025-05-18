package ru.kontur.view;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.time.Hour;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import ru.kontur.model.service.weatherService.OpenWeatherMapService;
import ru.kontur.model.service.weatherService.WeatherMeteoService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class ForecastIO extends JFrame {

    private static final String TITLE = "Прогноз";
    private static final int WIDTH = 1300;
    private static final int HEIGHT = 750;

    private DefaultTableModel openWeatherModel;
    private DefaultTableModel weatherMeteoModel;
    private final JPanel graphPanel;

    public ForecastIO() {
        setTitle(TITLE);
        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(650);

        JPanel tablePanel = new JPanel();
        tablePanel.setLayout(new BoxLayout(tablePanel, BoxLayout.Y_AXIS));
        tablePanel.add(createApiTableFor("OpenWeatherMap"));
        tablePanel.add(Box.createVerticalStrut(10));
        tablePanel.add(createApiTableFor("WeatherMeteo"));

        graphPanel = new JPanel();
        graphPanel.setBackground(Color.WHITE);
        splitPane.setLeftComponent(new JScrollPane(tablePanel));
        splitPane.setRightComponent(graphPanel);
        add(splitPane);
    }

    private JPanel createApiTableFor(String apiName) {
        DefaultTableModel model = new DefaultTableModel();
        JTable table = new JTable(model);
        model.addColumn("Час");

        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM");
        for (int i = 0; i < 5; i++) {
            LocalDate date = today.plusDays(i);
            model.addColumn(date.format(formatter));
        }
        for (int hour = 0; hour < 24; hour++) {
            String hourStr = String.format("%02d:00", hour);
            Object[] row = new Object[6];
            row[0] = hourStr;
            for (int j = 1; j < 6; j++) {
                row[j] = "-";
            }
            model.addRow(row);
        }
        switch (apiName) {
            case "OpenWeatherMap" -> openWeatherModel = model;
            case "WeatherMeteo" -> weatherMeteoModel = model;
        }
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(new JLabel(apiName), BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    public void loadForecasts(String location) {
        OpenWeatherMapService openWeather = new OpenWeatherMapService();
        WeatherMeteoService weatherMeteo = new WeatherMeteoService();

        System.out.println("Запрос к OpenWeather...");
        JSONObject openForecast = openWeather.getHourlyForecast(location);
        System.out.println("OpenWeather JSON: " + openForecast);

        System.out.println("Запрос к WeatherMeteo...");
        JSONObject meteoForecast = weatherMeteo.getHourlyForecast(location);
        System.out.println("WeatherMeteo JSON: " + meteoForecast);

        fillHourlyForecast(openWeatherModel, openForecast);
        fillHourlyForecast(weatherMeteoModel, meteoForecast);

        graphPanel.removeAll();
        graphPanel.setLayout(new GridLayout(2, 1));
        ChartPanel openWeatherChart = createTemperatureChart("OpenWeatherMap", openForecast);
        ChartPanel meteoChart = createTemperatureChart("WeatherMeteo", meteoForecast);
        graphPanel.add(openWeatherChart);
        graphPanel.add(meteoChart);
        graphPanel.revalidate();
        graphPanel.repaint();
    }

    private void fillHourlyForecast(DefaultTableModel model, JSONObject forecastData) {
        if (forecastData == null || !forecastData.containsKey("hourly")) return;
        JSONArray hourlyArray = (JSONArray) forecastData.get("hourly");

        Map<String, Map<String, String>> structuredForecast = new HashMap<>();

        DateTimeFormatter outputDate = DateTimeFormatter.ofPattern("dd MMM");
        DateTimeFormatter outputHour = DateTimeFormatter.ofPattern("HH:00");

        for (Object obj : hourlyArray) {
            JSONObject hourData = (JSONObject) obj;
            String timeStr = (String) hourData.get("time");
            String temperature = hourData.get("temperature").toString();

            LocalDateTime time;
            if (timeStr.contains("T")) {
                time = LocalDateTime.parse(timeStr.replace("T", " "), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            } else {
                time = LocalDateTime.parse(timeStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            }

            String dateKey = time.format(outputDate);
            String hourKey = time.format(outputHour);
            structuredForecast
                    .computeIfAbsent(dateKey, k -> new HashMap<>())
                    .put(hourKey, temperature + "°C");
        }
        for (int row = 0; row < model.getRowCount(); row++) {
            String hour = (String) model.getValueAt(row, 0);
            for (int col = 1; col < model.getColumnCount(); col++) {
                String date = model.getColumnName(col);
                Map<String, String> hourMap = structuredForecast.get(date);
                if (hourMap != null && hourMap.containsKey(hour)) {
                    model.setValueAt(hourMap.get(hour), row, col);
                }
            }
        }
    }

    private ChartPanel createTemperatureChart(String apiName, JSONObject forecastData) {
        TimeSeries series = new TimeSeries(apiName + " Temperature");

        JSONArray hourlyArray = (JSONArray) forecastData.get("hourly");
        for (Object obj : hourlyArray) {
            JSONObject hourData = (JSONObject) obj;
            String timeStr = (String) hourData.get("time");
            Number temperature = (Number) hourData.get("temperature");

            LocalDateTime time;
            if (timeStr.contains("T")) {
                time = LocalDateTime.parse(timeStr.replace("T", " "), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            } else {
                time = LocalDateTime.parse(timeStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            }
            series.add(new Hour(time.getHour(), time.getDayOfMonth(), time.getMonthValue(), time.getYear()), temperature);
        }
        TimeSeriesCollection dataset = new TimeSeriesCollection();
        dataset.addSeries(series);
        JFreeChart chart = ChartFactory.createTimeSeriesChart(
                apiName,
                "Время (T)",
                "Температура (°C)",
                dataset,
                false,
                true,
                false
        );
        return new ChartPanel(chart);
    }
}