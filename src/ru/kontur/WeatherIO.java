package ru.kontur;

import javax.swing.*;

public class WeatherIO extends JFrame {

    private static final String TITLE = "Погода";
    private static final int WIDTH = 450;
    private static final int HEIGHT = 650;

    public WeatherIO() {
        setTitle(TITLE);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null);
        setLayout(null);
        setResizable(false);
    }
}
