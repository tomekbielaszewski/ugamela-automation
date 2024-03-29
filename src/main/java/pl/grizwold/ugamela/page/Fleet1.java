package pl.grizwold.ugamela.page;

import lombok.SneakyThrows;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import pl.grizwold.ugamela.UgamelaSession;

import java.util.List;
import java.util.stream.Collectors;

public class Fleet1 extends Page {
    private static final String FLEET_PAGE = "fleet.php";

    private static final String STATIONED_FLEET_TABLE_SELECTOR = "#gameContent > center > form > table > tbody:nth-child(1)";
    private static final String NEXT_BUTTON_SELECTOR = "#gameContent > center > form > table > tbody:nth-child(4) > tr:nth-child(2) > th > input";

    public Fleet1(UgamelaSession session) {
        super(session);
        if (!isUrlLike(FLEET_PAGE)) {
            open(FLEET_PAGE);
        }
    }

    public List<AvailableFleet> availableShips() {
        validateState();
        return $().findElement(By.cssSelector(STATIONED_FLEET_TABLE_SELECTOR)).findElements(By.cssSelector("tr.addPad2"))
                .stream()
                .map(AvailableFleet::new)
                .collect(Collectors.toList());
    }

    public boolean canSendFleet() {
        List<WebElement> nextButtonList = $().findElements(By.cssSelector(NEXT_BUTTON_SELECTOR));
        return nextButtonList.size() > 0 &&
                nextButtonList.get(0).isDisplayed();
    }

    @SneakyThrows
    public Fleet2 next() {
        validateState();
        WebElement button = $().findElement(By.cssSelector(NEXT_BUTTON_SELECTOR));
        ((JavascriptExecutor) $()).executeScript("arguments[0].scrollIntoView(true);", button);
        Thread.sleep(500);
        button.click();
        return new Fleet2(this);
    }

    private void validateState() {
        String currentUrl = $().getCurrentUrl();
        if (currentUrl.contains(FLEET_PAGE))
            return;
        throw new IllegalStateException("Not on fleet page.\nCurrent page: " + currentUrl + "\nExpected page: " + FLEET_PAGE);
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

        public Fleet1 select(long amount) {
            validateState();
            this.shipRow.findElement(By.cssSelector("th > input")).sendKeys(String.valueOf(amount));
            return Fleet1.this;
        }
    }
}
