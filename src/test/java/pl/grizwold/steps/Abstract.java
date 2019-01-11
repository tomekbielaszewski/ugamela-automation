package pl.grizwold.steps;

import org.hamcrest.core.Is;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import static org.junit.Assert.assertThat;

public abstract class Abstract {
    private static final String LOGIN_URL = "https://www.ugamela.pl/";

    protected WebDriver $;

    public Abstract() {
        System.setProperty("webdriver.chrome.driver", "chromedriver.exe");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.113 Safari/537.36");
        this.$ = new ChromeDriver(options);
        this.$.manage().window().maximize();
    }

    protected Abstract openOgame() {
        $.get(LOGIN_URL);
        return this;
    }

    protected Abstract login(String login, String password) {
        assertThat($.getCurrentUrl(), Is.is(LOGIN_URL));
        $.findElement(By.cssSelector("#login_input > table > tbody > tr > td > form > input:nth-child(4)")).sendKeys(login);
        $.findElement(By.cssSelector("#login_input > table > tbody > tr > td > form > input:nth-child(5)")).sendKeys(password);
        $.findElement(By.cssSelector("#login_input > table > tbody > tr > td > form > input[type=\"submit\"]:nth-child(6)")).click();
        return this;
    }
}
