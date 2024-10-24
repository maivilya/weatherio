package ru.kontur;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class WeatherIO extends JFrame {

    private static final String TITLE = "Погода";
    private static final String FONT_FAMILY = "Roboto";
    private static final String SEARCH_RESOURCE_PATH = "src/ru/kontur/assets/search.png";
    private static final String CLOUDY_RESOURCE_PATH = "src/ru/kontur/assets/cloudy.png";
    private static final int WIDTH = 450;
    private static final int HEIGHT = 650;

    public WeatherIO() {
        setTitle(TITLE);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null);
        setLayout(null);
        setResizable(false);
        addComponents();
    }

    private void addComponents() {
        addSearchField();
        addSearchButton();
        addWeatherImage();
        addTemperatureText();
        addWeatherDescription();
    }

    private void addWeatherDescription() {
        JLabel weatherDescription = new JLabel("Cloudy");
        weatherDescription.setBounds(0, 405, 450, 35);
        weatherDescription.setFont(new Font(FONT_FAMILY, Font.PLAIN, 32));
        weatherDescription.setHorizontalAlignment(SwingConstants.CENTER);
        add(weatherDescription);
    }

    private void addTemperatureText(){
        JLabel temperature = new JLabel("25º");
        temperature.setBounds(0, 350, 450, 55);
        temperature.setFont(new Font(FONT_FAMILY, Font.BOLD, 48));
        temperature.setHorizontalAlignment(SwingConstants.CENTER);
        add(temperature);
    }

    private void addWeatherImage() {
        JLabel weatherConditionImage = new JLabel(loadImage(CLOUDY_RESOURCE_PATH));
        weatherConditionImage.setBounds(0, 125, 450, 220);
        add(weatherConditionImage);
    }

    private void addSearchButton() {
        JButton searchButton = new JButton(loadImage(SEARCH_RESOURCE_PATH));
        searchButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        searchButton.setBounds(375, 15, 50, 45);
        add(searchButton);
    }

    private void addSearchField() {
        JTextField searchTextField = new JTextField();
        searchTextField.setBounds(15, 15, 350, 45);
        searchTextField.setFont(new Font(FONT_FAMILY, Font.PLAIN, 24));
        add(searchTextField);
    }

    private ImageIcon loadImage(String resourcePath) {
        try {
            BufferedImage image = ImageIO.read(new File(resourcePath));
            return new ImageIcon(image);
        } catch (IOException exception) {
            System.out.println(exception.getMessage());
        }
        System.out.println("Could not find resource");
        return null;
    }
}
