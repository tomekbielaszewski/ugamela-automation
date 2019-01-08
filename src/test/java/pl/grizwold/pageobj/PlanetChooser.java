package pl.grizwold.pageobj;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class PlanetChooser {
    private final WebDriver $;

    public PlanetChooser(WebDriver webDriver) {
        this.$ = webDriver;
    }

    public void overview() {
        $.findElement(By.cssSelector("body > div:nth-child(7) > div.pf.l0.fl.w2.z1.style > table > tbody > tr:nth-child(3) > td > a")).click();
    }

    public void openPlanet(int planetNumber) {
        $.findElement(By.cssSelector("#planet")).click();
        $.findElement(By.cssSelector("#planet > option:nth-child(" + planetNumber + ")")).click();
    }
}
