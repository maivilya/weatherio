package ru.kontur;

import ru.kontur.view.WeatherIO2;

import javax.swing.*;

public class AppLauncher {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new WeatherIO2().setVisible(true);
        });
    }
}