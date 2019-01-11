package pl.grizwold.pageobj;

import org.openqa.selenium.WebDriver;

public class Messages extends Page {
    private static final String MESSAGES_PAGE = "messages.php";

    public Messages(WebDriver $) {
        super($);
        if (!onUrlEndingWith(MESSAGES_PAGE)) {
            open(MESSAGES_PAGE);
        }
    }

    public SpyReports spyReports() {
        return new SpyReports(this.$);
    }
}
