package pl.grizwold.ugamela.page;

import lombok.Getter;
import lombok.SneakyThrows;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.WebDriverWait;
import pl.grizwold.ugamela.SeleniumHelper;
import pl.grizwold.ugamela.UgamelaSession;
import pl.grizwold.ugamela.page.model.Address;

import java.time.Duration;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.Comparator.reverseOrder;

public class Galaxy extends Page {
    public static long GALAXY_WAIT_TIMEOUT = 10;

    private static final String GALAXY_PAGE = "galaxy.php";

    public final Navigator navigator;

    public Galaxy(UgamelaSession session) {
        super(session);
        this.navigator = new Navigator();
    }

    public Galaxy open() {
        if (!isUrlLike(GALAXY_PAGE))
            open(GALAXY_PAGE);
        return this;
    }

    @SneakyThrows
    public Galaxy goTo(Address address) {
        open();
        Thread.sleep(500);
        this.navigator.setAddress(address);
        return this;
    }

    public Planet getPlanet(Address address) {
        open().goTo(address);
        return getPlanet(Integer.parseInt(address.planet));
    }

    public Planet getPlanet(int position) {
        open();
        WebElement planet = $().findElement(By.xpath("//*[@id=\"galRows\"]/tr[" + position + "]"));
        return new Planet(planet, this.navigator.getAddress(String.valueOf(position)));
    }

    public Stream<Planet> getInhabitedPlanets() {
        open();
        return IntStream.range(1, 16)
                .boxed()
                .sorted(reverseOrder())
                .map(this::getPlanet)
                .filter(p -> p.isInhabited);
    }

    public Address address() {
        return this.navigator.getAddress();
    }

    public Galaxy looseFocus() {
        WebElement ajaxInfoBox = $().findElement(By.id("galaxy_form"));
        new Actions($()).moveToElement(ajaxInfoBox).perform();
        return this;
    }

    @SneakyThrows
    private MissionInfo getMissionStatus(Planet planet) {
        Thread.sleep(200);
        return new MissionInfo($().findElement(By.id("ajaxInfoBox")), planet);
    }

    @Getter
    public class Planet {
        public final String name;
        public final boolean isInhabited;
        public final boolean isEnemy;
        public final boolean isLongInactive;
        public final Address address;
        private final WebElement planet;

        public Planet(WebElement planet, Address address) {
            this.address = address;
            this.isInhabited = !planet.findElement(By.cssSelector("th:nth-child(7)")).getText().isBlank();
            this.name = planet.findElement(By.xpath("./child::th[3]")).getText();
            this.isEnemy = planet.findElements(By.xpath("./child::th[3]/a[@title=\"Napadaj\"]")).size() > 0;
            this.isLongInactive = planet.findElements(By.xpath("./child::th[6]/a/b[@class='longinactive']")).size() > 0;
            this.planet = planet;
        }

        public MissionInfo spy() {
            if (SeleniumHelper.isStale(planet)) {
                Galaxy.this.open().goTo(this.address);
            }

            planet.findElement(By.className("icoSpy")).click();
            return Galaxy.this.getMissionStatus(this);
        }
    }

    public class Navigator {
        public Address getAddress(String planet) {
            return new Address(
                    galaxyFormInput().getAttribute("value"),
                    systemFormInput().getAttribute("value"),
                    planet
            );
        }

        public Address getAddress() {
            return getAddress("1");
        }

        public Navigator nextSystem() {
            galaxyForm().findElement(By.xpath("//input[@name=\"systemRight\"]"))
                    .click();
            waitForAjax();
            return this;
        }

        public Navigator previousSystem() {
            galaxyForm().findElement(By.xpath("//input[@name=\"systemLeft\"]"))
                    .click();
            waitForAjax();
            return this;
        }

        public Navigator nextGalaxy() {
            galaxyForm().findElement(By.xpath("//input[@name=\"galaxyRight\"]"))
                    .click();
            waitForAjax();
            return this;
        }

        public Navigator previousGalaxy() {
            galaxyForm().findElement(By.xpath("//input[@name=\"galaxyLeft\"]"))
                    .click();
            waitForAjax();
            return this;
        }

        public Navigator submit() {
            galaxyFormSubmit().click();
            waitForAjax();
            return this;
        }

        public Navigator setGalaxy(String galaxy) {
            if (getAddress().galaxy.equals(galaxy))
                return this;
            WebElement galaxyFormInput = galaxyFormInput();
            galaxyFormInput.sendKeys(Keys.LEFT_SHIFT, "a");
            galaxyFormInput.sendKeys(galaxy);
            return this;
        }

        public Navigator setSystem(String system) {
            if (getAddress().system.equals(system))
                return this;
            WebElement galaxyFormInput = systemFormInput();
            galaxyFormInput.sendKeys(Keys.LEFT_SHIFT, "a");
            galaxyFormInput.sendKeys(system);
            return this;
        }

        public Navigator setAddress(Address address) {
            return setGalaxy(address.galaxy)
                    .setSystem(address.system)
                    .submit();
        }

        private WebElement galaxyForm() {
            return $().findElement(By.id("galaxy_form"));
        }

        private WebElement galaxyFormSubmit() {
            return galaxyForm().findElement(By.xpath("//input[@type=\"submit\"]"));
        }

        private WebElement galaxyFormInput() {
            return galaxyForm().findElement(By.xpath("//input[@name=\"galaxy\"]"));
        }

        private WebElement systemFormInput() {
            return galaxyForm().findElement(By.xpath("//input[@name=\"system\"]"));
        }

        private void waitForAjax() {
            new WebDriverWait($(), Duration.ofSeconds(GALAXY_WAIT_TIMEOUT))
                    .until(_$ -> _$.findElements(By.id("cover")).size() == 0);
        }
    }
}
