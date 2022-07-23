package pl.grizwold.ugamela;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class UgamelaSession {
    private static final String LOGIN_URL = "https://www.ugamela.pl/";

    private final WebDriver $;

    private boolean loggedIn;

    public UgamelaSession(WebDriver webDriver) {
        this.$ = webDriver;
    }

    public UgamelaSession openOgame() {
        $.get(LOGIN_URL);
        return this;
    }

    public UgamelaSession login(String login, String password) {
        if (!$.getCurrentUrl().equalsIgnoreCase(LOGIN_URL))
            openOgame();
        $.findElement(By.cssSelector("#login_input > table > tbody > tr > td > form > input:nth-child(4)")).sendKeys(login);
        $.findElement(By.cssSelector("#login_input > table > tbody > tr > td > form > input:nth-child(5)")).sendKeys(password);
        $.findElement(By.cssSelector("#login_input > table > tbody > tr > td > form > input[type=\"submit\"]:nth-child(6)")).click();
        //TODO: assert if really logged in
        loggedIn = true;
        return this;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public WebDriver getWebDriver() {
        return $;
    }
}
