package ru.kontur.view;

import javax.swing.*;

public class HistoryIO extends JFrame {

    private static final String TITLE = "История";
    private static final String FONT_FAMILY = "Roboto";
    private static final int WIDTH = 1300;
    private static final int HEIGHT = 750;

    public HistoryIO() {
        setTitle(TITLE);
        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
    }
}
