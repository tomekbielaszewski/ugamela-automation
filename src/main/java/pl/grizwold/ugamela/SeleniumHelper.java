package pl.grizwold.ugamela;

import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;

public class SeleniumHelper {
    public static boolean isStale(WebElement el) {
        try {
            el.isEnabled();
            return false;
        } catch (StaleElementReferenceException e) {
            return true;
        }
    }
}
