package pl.grizwold.ugamela.page;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import pl.grizwold.ugamela.UgamelaSession;

import java.util.Scanner;

public class Resources extends Page {
    public Resources(UgamelaSession session) {
        super(session);
    }

    public long metal() {
        return getResource("metal");
    }

    public long crystal() {
        return getResource("crystal");
    }

    public long deuterium() {
        return getResource("deut");
    }

    public long metalCapacity() {
        return getResource("metalmax");
    }

    public long crystalCapacity() {
        return getResource("crystalmax");
    }

    public long deuteriumCapacity() {
        return getResource("deuteriummax");
    }

    private Long getResource(String resourceId) {
        return $().findElements(By.id(resourceId)).stream()
                .findFirst()
                .map(WebElement::getText)
                .map(this::toNumber)
                .orElse(0L);
    }

    private Long toNumber(String text) {
        String formatted = text.replaceAll("\\.", "")
                .replaceAll("\\(-", "")
                .replaceAll("\\)", "");
        Scanner scanner = new Scanner(formatted);
        if (scanner.hasNextLong())
            return scanner.nextLong();
        throw new IllegalStateException("Cannot convert " + text + " to resource amount");
    }
}
