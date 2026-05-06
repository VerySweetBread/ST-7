package lair.moe.task7;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.openqa.selenium.WebDriver;

public class Task2 {
    private static final String IPIFY_URL = "https://api.ipify.org/?format=json";

    public static void main(String[] args) {
        WebDriver webDriver = ChromeDriverFactory.create(args);
        try {
            System.out.println("IPv4-адрес клиента: " + getIpAddress(webDriver));
        } catch (Exception e) {
            System.out.println("Error");
            System.out.println(e.toString());
        } finally {
            webDriver.quit();
        }
    }

    public static String getIpAddress(WebDriver webDriver) throws Exception {
        String json = JsonPageReader.readJson(webDriver, IPIFY_URL);
        JSONObject obj = (JSONObject) new JSONParser().parse(json);
        Object ip = obj.get("ip");
        if (ip == null) {
            throw new IllegalStateException("В JSON-ответе не найдено поле ip: " + json);
        }
        return ip.toString();
    }
}
