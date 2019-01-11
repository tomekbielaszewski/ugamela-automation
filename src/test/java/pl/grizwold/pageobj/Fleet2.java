package pl.grizwold.pageobj;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import pl.grizwold.pageobj.model.Address;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class Fleet2 extends Page {
    private static final String OWN_DESTINATION_DROP_DOWN_SELECTOR = "#fl_sel1";
    private static final String OWN_DESTINATION_OPTION_SELECTOR = "#fl_sel1 > option:nth-child(%d)";
    private static final String ADDRESS_GALAXY_SELECTOR = "#galaxy_selector";
    private static final String ADDRESS_SYSTEM_SELECTOR = "#system_selector";
    private static final String ADDRESS_PLANET_SELECTOR = "#select_planet";
    private static final String SUBMIT_BUTTON = "#thisForm > table > tbody:nth-child(3) > tr:nth-child(2) > th > input.SendButtom.lime";
    private static final Map<Integer, String> SPEED_SELECTORS = new HashMap<Integer, String>() {{
        put(100, "#defCursor > a:nth-child(1)");
        put(90, "#defCursor > a:nth-child(3)");
        put(80, "#defCursor > a:nth-child(5)");
        put(70, "#defCursor > a:nth-child(7)");
        put(60, "#defCursor > a:nth-child(9)");
        put(50, "#defCursor > a:nth-child(11)");
        put(40, "#defCursor > a:nth-child(13)");
        put(30, "#defCursor > a:nth-child(15)");
        put(20, "#defCursor > a:nth-child(17)");
        put(10, "#defCursor > a:nth-child(19)");
    }};

    public Fleet2(Fleet1 parent) {
        super(parent.$);
    }

    public Fleet2 selectDestinationColony(int index) {
        validateState();
        $.findElement(By.cssSelector(OWN_DESTINATION_DROP_DOWN_SELECTOR)).click();
        $.findElement(By.cssSelector(String.format(OWN_DESTINATION_OPTION_SELECTOR, index))).click();
        return this;
    }

    public Fleet2 selectSpeed(int speed) {
        validateState();
        String speedSelector = SPEED_SELECTORS.get(speed);
        assertNotNull("Incorrect speed specified: " + speed, speedSelector);
        $.findElement(By.cssSelector(speedSelector)).click();
        return this;
    }

    public Fleet3 next() {
        validateState();
        validateConsumption();
        $.findElement(By.cssSelector(SUBMIT_BUTTON)).click();
        return new Fleet3(this);
    }

    public Fleet2 selectDestination(Address address) {
        validateState();
        $.findElement(By.cssSelector(ADDRESS_GALAXY_SELECTOR)).sendKeys(address.getGalaxy());
        $.findElement(By.cssSelector(ADDRESS_SYSTEM_SELECTOR)).sendKeys(address.getSystem());
        $.findElement(By.cssSelector(ADDRESS_PLANET_SELECTOR)).sendKeys(address.getPlanet());
        return this;
    }

    private void validateConsumption() {
        assertNotEquals("Not enough deuterium", "red", $.findElement(By.cssSelector("#consumption > b")).getAttribute("class"));
    }

    private void validateState() {
        assertTrue($.getCurrentUrl().endsWith("fleet1.php"));
    }
}
