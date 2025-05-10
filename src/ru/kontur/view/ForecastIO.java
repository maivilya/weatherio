package ru.kontur.view;

import javax.swing.*;

public class ForecastIO extends JFrame {

    private static final String TITLE = "Прогноз";
    private static final String FONT_FAMILY = "Roboto";
    private static final int WIDTH = 1300;
    private static final int HEIGHT = 750;

    public ForecastIO() {
        setTitle(TITLE);
        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
    }
}
