package lair.moe.task7;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

public class App {
    private static final String PASSWORD_GENERATOR_URL = "https://www.calculator.net/password-generator.html";
    private static final By PASSWORD_RESULT = By.cssSelector("#resultid .verybigtext b");

    public static void main(String[] args) {
        WebDriver webDriver = ChromeDriverFactory.create(args);
        try {
            System.out.println("Задание №1");
            String password = generatePassword(webDriver);
            System.out.println("Сгенерированный пароль: " + password);
            System.out.println();

            System.out.println("Задание №2");
            String ipAddress = Task2.getIpAddress(webDriver);
            System.out.println("IPv4-адрес клиента: " + ipAddress);
            System.out.println();

            System.out.println("Задание №3");
            Task3.Forecast forecast = Task3.getForecast(webDriver);
            String table = Task3.formatForecastTable(forecast);
            System.out.println(table);
            Task3.writeForecast(forecast);
            System.out.println("Таблица сохранена в result/forecast.txt");
        } catch (Exception e) {
            System.out.println("Error");
            System.out.println(e.toString());
        } finally {
            webDriver.quit();
        }
    }

    static String generatePassword(WebDriver webDriver) {
        webDriver.get(PASSWORD_GENERATOR_URL);
        waitForDocument(webDriver);

        WebDriverWait wait = new WebDriverWait(webDriver, 15);
        return wait.until(driver -> {
            try {
                String password = driver.findElement(PASSWORD_RESULT).getText().trim();
                return password.isEmpty() ? null : password;
            } catch (NoSuchElementException | StaleElementReferenceException e) {
                return null;
            }
        });
    }

    private static void waitForDocument(WebDriver webDriver) {
        WebDriverWait wait = new WebDriverWait(webDriver, 15);
        wait.until(driver -> ((JavascriptExecutor) driver)
                .executeScript("return document.readyState")
                .toString()
                .equals("complete"));
    }
}
