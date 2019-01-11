package pl.grizwold.pageobj;

import org.openqa.selenium.By;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class Fleet3 extends Page {
    private static final String SUBMIT_BUTTON = "#gTb > tbody > tr:nth-child(5) > th > input.SendButtom.lime";
    private static final String LOAD_ALL_RESOURCES_SELECTOR = "#setMaxAll";
    private static final String METAL_AMOUNT_SELECTOR = "#gTb > tbody > tr:nth-child(2) > th:nth-child(2) > table > tbody:nth-child(1) > tr:nth-child(2) > th:nth-child(3) > input";
    private static final String CRISTAL_AMOUNT_SELECTOR = "#gTb > tbody > tr:nth-child(2) > th:nth-child(2) > table > tbody:nth-child(1) > tr:nth-child(3) > th:nth-child(3) > input";
    private static final String DEUTERIUM_AMOUNT_SELECTOR = "#gTb > tbody > tr:nth-child(2) > th:nth-child(2) > table > tbody:nth-child(1) > tr:nth-child(4) > th:nth-child(3) > input";
    private static final Map<String, String> MISSION_SELECTORS = new HashMap<String, String>(){{
        put("Attack", "#ms_1");
        put("Transport", "#ms_3");
        put("Station", "#ms_4");
        put("Spy", "#ms_6");
        put("Collect", "#ms_8");
        put("Destroy", "#ms_9");
    }};

    public Fleet3(Fleet2 parent) {
        super(parent.$);
    }

    public Fleet3 selectMission(String mission) {
        validateState();

        String missionSelector = MISSION_SELECTORS.get(mission);
        assertNotNull( "Incorrect mission specified: " + mission, missionSelector);
        $.findElement(By.cssSelector(missionSelector)).click();
        return this;
    }

    public Fleet3 loadAllResources() {
        validateState();
        $.findElement(By.cssSelector(LOAD_ALL_RESOURCES_SELECTOR)).click();
        return this;
    }

    public Fleet3 loadResources(int metal, int cristal, int deuterium) {
        validateState();
        $.findElement(By.cssSelector(METAL_AMOUNT_SELECTOR)).sendKeys(String.valueOf(metal));
        $.findElement(By.cssSelector(CRISTAL_AMOUNT_SELECTOR)).sendKeys(String.valueOf(cristal));
        $.findElement(By.cssSelector(DEUTERIUM_AMOUNT_SELECTOR)).sendKeys(String.valueOf(deuterium));
        return this;
    }

    public Fleet4 next() {
        validateState();
        $.findElement(By.cssSelector(SUBMIT_BUTTON)).click();
        return new Fleet4(this);
    }

    private void validateState() {
        assertTrue($.getCurrentUrl().endsWith("fleet2.php"));
    }
}
