package pl.grizwold.ugamela;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class UgamelaSession {
    private static final String LOGIN_URL = "https://www.ugamela.pl/";

    private final WebDriver $;

    public UgamelaSession(WebDriver webDriver) {
        this.$ = webDriver;
    }

    public UgamelaSession openOgame() {
        $.get(LOGIN_URL);
        return this;
    }

    public boolean isLoggedIn() {
        return $.getCurrentUrl().startsWith("https://www.ugamela.pl/s1/");
    }

    public UgamelaSession login() {
        Credentials credentials = new Credentials();
        if (!this.isLoggedIn())
            this.login(credentials.login, credentials.password);
        return this;
    }

    public UgamelaSession login(String login, String password) {
        if (!$.getCurrentUrl().equalsIgnoreCase(LOGIN_URL))
            openOgame();
        $.findElement(By.cssSelector("#login_input > table > tbody > tr > td > form > input:nth-child(1)")).sendKeys(login);
        $.findElement(By.cssSelector("#login_input > table > tbody > tr > td > form > input:nth-child(2)")).sendKeys(password);
        $.findElement(By.cssSelector("#login_input > table > tbody > tr > td > form > input[type=submit]:nth-child(3)")).click();
        if (!isLoggedIn()) throw new IllegalStateException("Couldn't log in. Credentials valid?");
        return this;
    }

    public WebDriver getWebDriver() {
        return $;
    }

    public UgamelaSession selectUniversum(String uni) {
        $.findElements(By.xpath("//select[@name='Uni']/option[contains(text(), \"" + uni + "\")]"))
                .stream().findFirst()
                .ifPresent(WebElement::click);
        return this;
    }
}
