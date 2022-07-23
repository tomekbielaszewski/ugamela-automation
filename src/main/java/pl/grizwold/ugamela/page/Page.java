package pl.grizwold.ugamela.page;

import org.openqa.selenium.WebDriver;
import pl.grizwold.ugamela.UgamelaSession;

public abstract class Page {
    protected static final String BASE_URL = "https://www.ugamela.pl/s1/";
    protected final UgamelaSession session;

    public Page(UgamelaSession session) {
        if(!session.isLoggedIn()) throw new IllegalStateException("Log in first!");
        this.session = session;
    }

    protected void open(String subpage) {
        $().get(BASE_URL + subpage);
    }

    protected boolean isUrlLike(String subUrl) {
        return $().getCurrentUrl().contains(subUrl);
    }

    protected boolean isUrlEndingWith(String subUrl) {
        return $().getCurrentUrl().endsWith(subUrl);
    }

    protected WebDriver $() {
        return this.session.getWebDriver();
    }
}
