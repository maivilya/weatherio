package ru.kontur;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FavoriteService {

    /**
     * Adding a city to the list of favorite locations
     * @param cityName city or location
     * @throws SQLException if the city with the specified name is not found in the list of favorites
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
     * Retrieve all cities from the favorites list
     * @return favorites list
     * @throws SQLException in case of database connection error
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
     * Deleting a city from the favorites list
     * @param cityName city or location
     * @throws SQLException if the city with the specified name is not found in the list of favorites
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
