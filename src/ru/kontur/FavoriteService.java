package ru.kontur;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FavoriteService {

    /**
     * Добавляет город в список избранных
     * @param cityName - название города или локации
     * @throws SQLException если город с указаным названием не найден в списке избранных
     */
    public void addCityToFavorites(String cityName) throws SQLException {
        String query = "INSERT INTO favorite (city_name) VALUES (?) ON CONFLICT (city_name) DO NOTHING";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, cityName);
            statement.executeUpdate();
        }
    }

    /**
     * Получает все города из списка избранных
     * @return список с избранными городами
     * @throws SQLException при ошибке подключения к базе данных
     */
    public List<String> getFavorites() throws SQLException {
        List<String> favorites = new ArrayList<>();
        String query = "SELECT city_name FROM favorite";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                favorites.add(resultSet.getString("city_name"));
            }
        }
        return favorites;
    }

    /**
     * Удаляет город из списка избранных
     * @param cityName - название города или локации
     * @throws SQLException если город с указаным названием не найден в списке избранных
     */
    public void removeCityFromFavorites(String cityName) throws SQLException {
        String query = "DELETE FROM favorite WHERE city_name = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, cityName);
            statement.executeUpdate();
        }
    }
}
