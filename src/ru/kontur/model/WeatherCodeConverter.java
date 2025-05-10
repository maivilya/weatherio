package ru.kontur.model;

public class WeatherCodeConverter {

    /**
     * Obtaining data on weather condition
     * using weather code passed to JSON API
     * @param weatherCode weather code passed to JSON API
     * @return weather condition
     */
    public static String convert(long weatherCode) {
        if (weatherCode == 0L) {
            return "Ясно";
        } else if (weatherCode > 0L && weatherCode <= 3L) {
            return "Облачно";
        } else if ((weatherCode >= 51L && weatherCode <= 67L)
                || (weatherCode >= 80L && weatherCode <= 99L)) {
            return "Дождь";
        } else if (weatherCode >= 71L && weatherCode <= 77L) {
            return "Снег";
        } else {
            return "Неизвестно";
        }
    }
}
