package ru.kontur;

import javax.swing.*;

public class AppLauncher {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->
                //new WeatherIO().setVisible(true)
                System.out.println(WeatherApp.getLocationData("Yekaterinburg"))
        );
    }
}