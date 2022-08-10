package pl.grizwold.ugamela.page;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import pl.grizwold.ugamela.UgamelaSession;

import java.util.List;

public class PlanetChooser extends Page {

    public PlanetChooser(UgamelaSession session) {
        super(session);
    }

    public void openPlanet(int planetNumber) {
        $().findElement(By.cssSelector("#planet")).click();
        $().findElement(By.cssSelector("#planet > option:nth-child(" + planetNumber + ")")).click();
    }

    public void openPlanet(String nameSubstring) {
        $().findElement(By.cssSelector("#planet")).click();
        List<WebElement> options = $().findElements(By.xpath("//*[@id=\"planet\"]/option[contains(text(), \"" + nameSubstring + "\")]"));
        if(options.isEmpty())
            throw new IllegalStateException("No matching planets found for: " + nameSubstring);
        if(options.size() > 1)
            throw new IllegalStateException("Too many planets found for: " + nameSubstring);
        options.get(0).click();
    }
}
