package pl.grizwold.webdriver;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.function.Supplier;

public class LocalWebDriver implements Supplier<WebDriver> {
    private final String chromedriverPath;

    public LocalWebDriver(String chromedriverPath) {
        this.chromedriverPath = chromedriverPath;
    }

    @Override
    public WebDriver get() {
        System.setProperty("webdriver.chrome.driver", chromedriverPath);
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-debugging-port=32003");
        options.addArguments("--user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/97.0.4692.99 Safari/537.36");
        return new ChromeDriver(options);
    }
}
