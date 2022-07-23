package pl.grizwold.ugamela.page;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import pl.grizwold.ugamela.UgamelaSession;

public class PlanetChooser extends Page {

    public PlanetChooser(UgamelaSession session) {
        super(session);
    }

    public void overview() {
        $().findElement(By.cssSelector("body > div:nth-child(7) > div.pf.l0.fl.w2.z1.style > table > tbody > tr:nth-child(3) > td > a")).click();
    }

    public void openPlanet(int planetNumber) {
        $().findElement(By.cssSelector("#planet")).click();
        $().findElement(By.cssSelector("#planet > option:nth-child(" + planetNumber + ")")).click();
    }
}
