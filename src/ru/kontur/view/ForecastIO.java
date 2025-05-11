package ru.kontur.view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ForecastIO extends JFrame {

    private static final String TITLE = "Прогноз";
    private static final int WIDTH = 1300;
    private static final int HEIGHT = 750;

    private JTable openWeatherTable;
    private JTable weatherApiTable;
    private JTable weatherMeteoTable;
    private DefaultTableModel openWeatherModel;
    private DefaultTableModel weatherApiModel;
    private DefaultTableModel weatherMeteoModel;

    public ForecastIO() {
        setTitle(TITLE);
        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(650);

        // Левая панель с таблицами
        JPanel tablePanel = new JPanel();
        tablePanel.setLayout(new BoxLayout(tablePanel, BoxLayout.Y_AXIS));

        JLabel openWeatherMapLabel = new JLabel("OpenWeatherMap");
        JLabel weatherApiLabel = new JLabel("WeatherAPI");
        JLabel weatherMeteoLabel = new JLabel("WeatherMeteo");

        createApiTable("OpenWeatherMap");
        createApiTable("WeatherApi");
        createApiTable("WeatherMeteo");

        tablePanel.add(openWeatherMapLabel);
        tablePanel.add(new JScrollPane(openWeatherTable));
        tablePanel.add(weatherApiLabel);
        tablePanel.add(new JScrollPane(weatherApiTable));
        tablePanel.add(weatherMeteoLabel);
        tablePanel.add(new JScrollPane(weatherMeteoTable));

        // Правая панель — графики (будет позже)
        JPanel graphPanel = new JPanel();
        graphPanel.setBackground(Color.WHITE);

        splitPane.setLeftComponent(new JScrollPane(tablePanel));
        splitPane.setRightComponent(graphPanel);
        add(splitPane);
    }

    private void createApiTable(String apiName) {
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
            case "OpenWeatherMap" -> {
                openWeatherModel = model;
                openWeatherTable = table;
            }
            case "WeatherApi" -> {
                weatherApiModel = model;
                weatherApiTable = table;
            }
            case "WeatherMeteo" -> {
                weatherMeteoModel = model;
                weatherMeteoTable = table;
            }
        }
    }
}