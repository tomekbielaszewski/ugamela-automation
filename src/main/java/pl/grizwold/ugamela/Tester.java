package pl.grizwold.ugamela;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import pl.grizwold.ugamela.page.SpyReports;

import java.util.List;

import static java.util.function.Predicate.not;

public class Tester {

    public static void main(String[] args) {
        WebDriver $;
        System.setProperty("webdriver.chrome.driver", "chromedriver.exe");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/97.0.4692.99 Safari/537.36");
        $ = new ChromeDriver(options);
        $.manage().window().maximize();

        Credentials credentials = new Credentials();
        UgamelaSession session = new UgamelaSession($);
        session.login(credentials.login, credentials.password);

        List<SpyReports.SpyReport> spyReports = new SpyReports(session).all()
                .stream()
                .filter(SpyReports.SpyReport::defenceRowVisible)
                .filter(SpyReports.SpyReport::fleetRowVisible)
                .filter(not(SpyReports.SpyReport::hasDefence))
                .filter(not(SpyReports.SpyReport::hasFleet))
                .toList();

        System.out.println(spyReports.size());
    }
}
