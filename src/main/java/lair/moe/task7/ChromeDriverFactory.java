package lair.moe.task7;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

/**
 * Creates ChromeDriver instances for all tasks.
 *
 * The driver path can be passed as the first command line argument,
 * by -Dwebdriver.chrome.driver=/path/to/chromedriver, or by CHROME_DRIVER_PATH.
 * If it is not set, Selenium Manager will try to find a compatible driver.
 */
final class ChromeDriverFactory {
    private static final String DRIVER_PROPERTY = "webdriver.chrome.driver";
    private static final String DRIVER_ENV = "CHROME_DRIVER_PATH";
    private static final String BROWSER_ENV = "CHROME_BINARY_PATH";

    private ChromeDriverFactory() {
    }

    static WebDriver create(String[] args) {
        configureDriverPath(args);

        ChromeOptions options = new ChromeOptions();
        String browserBinary = System.getenv(BROWSER_ENV);
        if (browserBinary != null && !browserBinary.trim().isEmpty()) {
            options.setBinary(browserBinary.trim());
        }
        if (Boolean.parseBoolean(System.getenv().getOrDefault("HEADLESS", "false"))) {
            options.addArguments("--headless=new");
        }
        options.addArguments("--remote-allow-origins=*");

        return new ChromeDriver(options);
    }

    private static void configureDriverPath(String[] args) {
        String currentValue = System.getProperty(DRIVER_PROPERTY);
        if (currentValue != null && !currentValue.trim().isEmpty()) {
            return;
        }

        String driverPath = null;
        if (args != null && args.length > 0 && args[0] != null && !args[0].trim().isEmpty()) {
            driverPath = args[0].trim();
        } else {
            driverPath = System.getenv(DRIVER_ENV);
        }

        if (driverPath != null && !driverPath.trim().isEmpty()) {
            System.setProperty(DRIVER_PROPERTY, driverPath.trim());
        }
    }
}
