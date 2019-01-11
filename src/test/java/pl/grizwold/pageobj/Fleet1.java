package pl.grizwold.pageobj;

import lombok.SneakyThrows;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertTrue;

public class Fleet1 extends Page {
    private static final String FLEET_PAGE = "fleet.php";

    private static final String STATIONED_FLEET_TABLE_SELECTOR = "#gameContent > center > form > table > tbody:nth-child(1)";

    public Fleet1(WebDriver $) {
        super($);
        if(!onUrlLike(FLEET_PAGE)) {
            open(FLEET_PAGE);
        }
    }

    public List<AvailableFleet> availableShips() {
        validateState();
        return $.findElement(By.cssSelector(STATIONED_FLEET_TABLE_SELECTOR)).findElements(By.cssSelector("tr.addPad2"))
                .stream()
                .map(AvailableFleet::new)
                .collect(Collectors.toList());
    }

    @SneakyThrows
    public Fleet2 next() {
        validateState();
        WebElement button = $.findElement(By.cssSelector("#gameContent > center > form > table > tbody:nth-child(4) > tr:nth-child(2) > th > input"));
        ((JavascriptExecutor) $).executeScript("arguments[0].scrollIntoView(true);", button);
        Thread.sleep(500);
        button.click();
        return new Fleet2(this);
    }

    private void validateState() {
        assertTrue($.getCurrentUrl().endsWith("fleet.php"));
    }

    public class AvailableFleet {
        private WebElement shipRow;

        AvailableFleet(WebElement shipRow) {
            this.shipRow = shipRow;
        }

        public String shipName() {
            return this.shipRow.findElement(By.cssSelector("a.Speed")).getText();
        }

        public int shipAmount() {
            validateState();
            String amountText = this.shipRow.findElement(By.cssSelector("th:nth-child(2)")).getText().replace(".", "");
            return Integer.parseInt(amountText);
        }

        public Fleet1 selectAll() {
            validateState();
            this.shipRow.findElement(By.cssSelector("a.maxShip")).click();
            return Fleet1.this;
        }

        public Fleet1 selectNone() {
            validateState();
            this.shipRow.findElement(By.cssSelector("a.noShip")).click();
            return Fleet1.this;
        }

        public Fleet1 select(int amount) {
            validateState();
            this.shipRow.findElement(By.cssSelector("th > input")).sendKeys(String.valueOf(amount));
            return Fleet1.this;
        }
    }
}
