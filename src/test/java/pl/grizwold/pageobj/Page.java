package pl.grizwold.pageobj;

import org.openqa.selenium.WebDriver;

public abstract class Page {
    protected static final String BASE_URL = "https://www.ugamela.pl/s1/";
    protected final WebDriver $;

    public Page(WebDriver $) {
        this.$ = $;
    }

    protected void open(String subpage) {
        $.get(BASE_URL + subpage);
    }

    protected boolean onUrlLike(String subUrl) {
        return $.getCurrentUrl().contains(subUrl);
    }

    protected boolean onUrlEndingWith(String subUrl) {
        return $.getCurrentUrl().endsWith(subUrl);
    }
}
