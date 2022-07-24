package pl.grizwold.ugamela;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import pl.grizwold.ugamela.page.Buildings;
import pl.grizwold.ugamela.page.SpyReports;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.function.Predicate.not;

public class Tester {

    public static void main(String[] args) throws MalformedURLException, URISyntaxException {
        WebDriver $;
        System.setProperty("webdriver.chrome.driver", "chromedriver.exe");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/97.0.4692.99 Safari/537.36");
        $ = new ChromeDriver(options);
//        $ = new RemoteWebDriver(new URI("http://localhost:32003").toURL(), new ChromeOptions());
        $.manage().window().maximize();

        Credentials credentials = new Credentials();
        UgamelaSession session = new UgamelaSession($);
        session.login(credentials.login, credentials.password);

//        List<SpyReports.SpyReport> spyReports = new SpyReports(session).all()
//                .stream()
//                .filter(SpyReports.SpyReport::defenceRowVisible)
//                .filter(SpyReports.SpyReport::fleetRowVisible)
//                .filter(not(SpyReports.SpyReport::hasDefence))
//                .filter(not(SpyReports.SpyReport::hasFleet))
//                .collect(Collectors.toList());
//
//        System.out.println(spyReports.size());

        Buildings buildings = new Buildings(session);
        boolean isUpgradable;
        do {
            int plantLevel = buildings.getTotalLevel(Buildings.Building.BUILDING_SOLAR_PLANT);
            int metalLevel = buildings.getTotalLevel(Buildings.Building.BUILDING_METAL_MINE);
            int crystalLevel = buildings.getTotalLevel(Buildings.Building.BUILDING_CRYSTAL_MINE);

            if (plantLevel >= metalLevel) isUpgradable = buildings.upgrade(Buildings.Building.BUILDING_METAL_MINE);
            else if (plantLevel >= crystalLevel) isUpgradable = buildings.upgrade(Buildings.Building.BUILDING_CRYSTAL_MINE);
            else isUpgradable = buildings.upgrade(Buildings.Building.BUILDING_SOLAR_PLANT);
        } while (isUpgradable);
    }
}
