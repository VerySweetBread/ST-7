package lair.moe.task7;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

final class JsonPageReader {
    private JsonPageReader() {
    }

    static String readJson(WebDriver webDriver, String url) {
        webDriver.get(url);

        WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(20));
        WebElement element = wait.until(driver -> {
            for (WebElement pre : driver.findElements(By.tagName("pre"))) {
                String text = pre.getText();
                if (text != null && !text.trim().isEmpty()) {
                    return pre;
                }
            }

            WebElement body = ExpectedConditions.presenceOfElementLocated(By.tagName("body")).apply(driver);
            if (body != null && body.getText() != null && !body.getText().trim().isEmpty()) {
                return body;
            }
            return null;
        });

        return element.getText().trim();
    }
}
