package lair.moe.task7;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.openqa.selenium.WebDriver;

public class Task3 {
    private static final String FORECAST_URL = "https://api.open-meteo.com/v1/forecast"
            + "?latitude=56"
            + "&longitude=44"
            + "&hourly=temperature_2m,rain"
            + "&current=cloud_cover"
            + "&timezone=Europe%2FMoscow"
            + "&forecast_days=1"
            + "&wind_speed_unit=ms";
    private static final Path RESULT_PATH = Paths.get("result", "forecast.txt");

    public static void main(String[] args) {
        WebDriver webDriver = ChromeDriverFactory.create(args);
        try {
            Forecast forecast = getForecast(webDriver);
            String table = formatForecastTable(forecast);
            System.out.println(table);
            writeForecast(forecast);
            System.out.println("Таблица сохранена в " + RESULT_PATH);
        } catch (Exception e) {
            System.out.println("Error");
            System.out.println(e.toString());
        } finally {
            webDriver.quit();
        }
    }

    public static Forecast getForecast(WebDriver webDriver) throws Exception {
        String json = JsonPageReader.readJson(webDriver, FORECAST_URL);
        return parseForecast(json);
    }

    static Forecast parseForecast(String json) throws Exception {
        JSONObject root = (JSONObject) new JSONParser().parse(json);
        JSONObject hourly = (JSONObject) root.get("hourly");
        if (hourly == null) {
            throw new IllegalStateException("В JSON-ответе не найден блок hourly: " + json);
        }

        JSONArray times = (JSONArray) hourly.get("time");
        JSONArray temperatures = (JSONArray) hourly.get("temperature_2m");
        JSONArray rains = (JSONArray) hourly.get("rain");
        if (times == null || temperatures == null || rains == null) {
            throw new IllegalStateException("В JSON-ответе не найдены hourly.time, hourly.temperature_2m или hourly.rain");
        }

        int rowsCount = Math.min(times.size(), Math.min(temperatures.size(), rains.size()));
        List<ForecastRow> rows = new ArrayList<>();
        for (int i = 0; i < rowsCount; i++) {
            rows.add(new ForecastRow(
                    i + 1,
                    String.valueOf(times.get(i)),
                    toDouble(temperatures.get(i)),
                    toDouble(rains.get(i))));
        }

        return new Forecast(rows);
    }

    public static String formatForecastTable(Forecast forecast) {
        StringBuilder table = new StringBuilder();
        table.append(String.format("%-3s | %-16s | %-11s | %-10s%n", "№", "Дата/время", "Температура", "Осадки (мм)"));
        table.append("----|------------------|-------------|------------").append(System.lineSeparator());
        for (ForecastRow row : forecast.getRows()) {
            table.append(String.format(Locale.US, "%-3d | %-16s | %8.1f °C | %10.2f%n",
                    row.getNumber(),
                    row.getDateTime(),
                    row.getTemperature(),
                    row.getRain()));
        }
        return table.toString();
    }

    public static void writeForecast(Forecast forecast) throws IOException {
        Files.createDirectories(RESULT_PATH.getParent());
        Files.write(RESULT_PATH, formatForecastTable(forecast).getBytes(StandardCharsets.UTF_8));
    }

    private static double toDouble(Object value) {
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        return Double.parseDouble(String.valueOf(value));
    }

    public static final class Forecast {
        private final List<ForecastRow> rows;

        Forecast(List<ForecastRow> rows) {
            this.rows = Collections.unmodifiableList(new ArrayList<>(rows));
        }

        public List<ForecastRow> getRows() {
            return rows;
        }
    }

    public static final class ForecastRow {
        private final int number;
        private final String dateTime;
        private final double temperature;
        private final double rain;

        ForecastRow(int number, String dateTime, double temperature, double rain) {
            this.number = number;
            this.dateTime = dateTime;
            this.temperature = temperature;
            this.rain = rain;
        }

        public int getNumber() {
            return number;
        }

        public String getDateTime() {
            return dateTime;
        }

        public double getTemperature() {
            return temperature;
        }

        public double getRain() {
            return rain;
        }
    }
}
