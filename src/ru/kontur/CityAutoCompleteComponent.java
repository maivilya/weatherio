package ru.kontur;

import ru.kontur.model.service.CityAutoCompleteService;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.Map;

public class CityAutoCompleteComponent {
    private final JTextField textField;
    private final CityAutoCompleteService autoCompleteService;
    private final JPopupMenu popupMenu;
    private boolean isSuggestionClicked = false;

    public CityAutoCompleteComponent(JTextField textField, CityAutoCompleteService autoCompleteService) {
        this.textField = textField;
        this.autoCompleteService = autoCompleteService;
        this.popupMenu = new JPopupMenu();
        installListener();
    }

    private void installListener() {
        textField.getDocument().addDocumentListener(new DocumentListener() {
            private String lastInput = "";

            public void insertUpdate(DocumentEvent e) { showSuggestions(); }
            public void removeUpdate(DocumentEvent e) { showSuggestions(); }
            public void changedUpdate(DocumentEvent e) { showSuggestions(); }

            private void maybeShowSuggestions() {
                String currentInput = textField.getText();
                if (isSuggestionClicked || currentInput.equalsIgnoreCase(lastInput)) {
                    isSuggestionClicked = false;
                    return;
                }
                lastInput = currentInput;
                showSuggestions();
            }
        });
    }

    private void showSuggestions() {
        if (isSuggestionClicked) {
            isSuggestionClicked = false;
            return;
        }

        String input = textField.getText();
        if (input.length() < 3) {
            popupMenu.setVisible(false);
            return;
        }

        new Thread(() -> {
            try {
                Map<String, String> cityMap = autoCompleteService.fetchCities(input);
                SwingUtilities.invokeLater(() -> {
                    popupMenu.removeAll();

                    for (Map.Entry<String, String> entry : cityMap.entrySet()) {
                        String cityName = entry.getKey();
                        String displayName = entry.getValue();

                        JMenuItem item = new JMenuItem(displayName);
                        item.setFont(new Font("Roboto", Font.PLAIN, 18));
                        item.addActionListener(e -> {
                            isSuggestionClicked = true;
                            textField.setText(cityName);
                            textField.requestFocusInWindow();
                            popupMenu.setVisible(false);
                        });
                        popupMenu.add(item);
                    }

                    if (!cityMap.isEmpty()) {
                        popupMenu.show(textField, 0, textField.getHeight());
                    } else {
                        popupMenu.setVisible(false);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
