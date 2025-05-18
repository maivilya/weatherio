package ru.kontur;

import ru.kontur.view.WeatherIO;

import javax.swing.*;

public class AppLauncher {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new WeatherIO().setVisible(true));
    }
}