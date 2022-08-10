package pl.grizwold.ugamela.page;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import pl.grizwold.ugamela.page.model.Address;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Fleet2 extends Page {
    private static final String FLEET_2_PAGE = "fleet1.php";
    private static final String OWN_DESTINATION_DROP_DOWN_SELECTOR = "#fl_sel1";
    private static final String OWN_DESTINATION_OPTION_SELECTOR = "//*[@id=\"fl_sel1\"]/option[contains(text(), \"%s\")]";
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
        super(parent.session);
    }

    public Fleet2 selectDestinationColony(String planetNameSubstring) {
        validateState();
        $().findElement(By.cssSelector(OWN_DESTINATION_DROP_DOWN_SELECTOR)).click();
        List<WebElement> options = $().findElements(By.xpath(String.format(OWN_DESTINATION_OPTION_SELECTOR, planetNameSubstring)));
        if(options.isEmpty())
            throw new IllegalStateException("No matching planets found for: " + planetNameSubstring);
        if(options.size() > 1)
            throw new IllegalStateException("Too many planets found for: " + planetNameSubstring);
        options.get(0).click();
        return this;
    }

    public Fleet2 selectSpeed(int speed) {
        validateState();
        String speedSelector = SPEED_SELECTORS.get(speed);
        if(StringUtils.isEmpty(speedSelector))
            throw new IllegalStateException("Incorrect speed specified: " + speed);
        $().findElement(By.cssSelector(speedSelector)).click();
        return this;
    }

    public Fleet3 next() {
        validateState();
        validateConsumption();
        $().findElement(By.cssSelector(SUBMIT_BUTTON)).click();
        return new Fleet3(this);
    }

    public Fleet2 selectDestination(Address address) {
        validateState();
        $().findElement(By.cssSelector(ADDRESS_GALAXY_SELECTOR)).sendKeys(address.galaxy);
        $().findElement(By.cssSelector(ADDRESS_SYSTEM_SELECTOR)).sendKeys(address.system);
        $().findElement(By.cssSelector(ADDRESS_PLANET_SELECTOR)).sendKeys(address.planet);
        return this;
    }

    private void validateConsumption() {
        String insufficientGasMarker = $().findElement(By.cssSelector("#consumption > b")).getAttribute("class");
        if("red".equalsIgnoreCase(insufficientGasMarker))
            throw new IllegalStateException("Not enough deuterium");
    }

    private void validateState() {
        String currentUrl = $().getCurrentUrl();
        if(isUrlLike(FLEET_2_PAGE))
            return;
        throw new IllegalStateException("Not on fleet2 page.\nCurrent page: " + currentUrl + "\nExpected page: " + FLEET_2_PAGE);
    }
}
