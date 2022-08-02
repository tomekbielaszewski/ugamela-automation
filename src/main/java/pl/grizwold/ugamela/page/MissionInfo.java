package pl.grizwold.ugamela.page;

import org.openqa.selenium.WebElement;

import java.util.Objects;

public class MissionInfo {
    private final WebElement infoBox;
    private final Galaxy.Planet planet;

    public MissionInfo(WebElement infoBox, Galaxy.Planet planet) {
        this.infoBox = infoBox;
        this.planet = planet;
    }

    public boolean isVisible() {
        return infoBox.isDisplayed();
    }

    public boolean isSuccess() {
        return Objects.equals(infoBox.getCssValue("color"), "rgba(0, 255, 0, 1)");
    }

    public String getMessage() {
        return infoBox.getText();
    }

    public Galaxy.Planet getPlanet() {
        return planet;
    }
}
