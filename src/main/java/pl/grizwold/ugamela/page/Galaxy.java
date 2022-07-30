package pl.grizwold.ugamela.page;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import pl.grizwold.ugamela.UgamelaSession;
import pl.grizwold.ugamela.page.model.Address;
import pl.grizwold.ugamela.page.model.MissionInfo;

public class Galaxy extends Page {
    private static final String GALAXY_PAGE = "galaxy.php";

    public Galaxy(UgamelaSession session) {
        super(session);
    }

    public Galaxy open() {
        if (!isUrlLike(GALAXY_PAGE))
            open(GALAXY_PAGE);
        return this;
    }

    public MissionInfo spy(Address address) {
        open();
        goTo(address);
        throw new RuntimeException("Not implemented");
//        return null;
    }

    public Galaxy nextSystem() {
        getGalaxyForm().findElement(By.xpath("//input[@name=\"systemRight\"]"))
                .click();
        return this;
    }

    public Galaxy previousSystem() {
        getGalaxyForm().findElement(By.xpath("//input[@name=\"systemLeft\"]"))
                .click();
        return this;
    }

    public Galaxy nextGalaxy() {
        getGalaxyForm().findElement(By.xpath("//input[@name=\"galaxyRight\"]"))
                .click();
        return this;
    }

    public Galaxy previousGalaxy() {
        getGalaxyForm().findElement(By.xpath("//input[@name=\"galaxyLeft\"]"))
                .click();
        return this;
    }

    public Galaxy goTo(Address address) {
        if (!isUrlEndingWith(urlSuffix(address))) {
            WebElement galaxyForm = getGalaxyForm();
            WebElement galaxyFormElement = galaxyForm.findElement(By.xpath("//input[@name=\"galaxy\"]"));
            WebElement systemFormElement = galaxyForm.findElement(By.xpath("//input[@name=\"system\"]"));
            WebElement submit = galaxyForm.findElement(By.xpath("//input[@type=\"submit\"]"));

            galaxyFormElement.sendKeys(Keys.CONTROL, "a");
            galaxyFormElement.sendKeys(address.getGalaxy());

            systemFormElement.sendKeys(Keys.CONTROL, "a");
            systemFormElement.sendKeys(address.getSystem());

            submit.click();
        }

        return this;
    }

    private String urlSuffix(Address address) {
        return String.format("#%s:%s", address.getGalaxy(), address.getSystem());
    }

    private WebElement getGalaxyForm() {
        return $().findElement(By.id("galaxy_form"));
    }

    public Planet getPlanet(int position) {
        open();
        WebElement planet = $().findElement(By.xpath("//*[@id=\"galRows\"]/tr[" + position + "]"));
        return new Planet(planet);
    }

    public Planet getPlanet(Address address) {
        open().goTo(address);
        return getPlanet(Integer.parseInt(address.getPlanet()));
    }

    public class Planet {
        private final WebElement planet;

        public Planet(WebElement planet) {
            this.planet = planet;
        }
    }
}
