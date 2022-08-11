package pl.grizwold.webdriver;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.IOException;
import java.net.*;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Multilogin project used here as a convenient way of obtaining the Selenium WebDriver with full browser fingerprint masking.
 * <a href="https://multilogin.com/download/">Multilogin download page</a>
 */
@Slf4j
public class MultiloginWebDriver implements Supplier<WebDriver>, Function<Integer, WebDriver> {
    private final String profileId;

    public MultiloginWebDriver(String profileId) {
        this.profileId = profileId;
    }

    @Override
    public WebDriver get() {
        String runningProfileAutomationUrl;
        try {
            runningProfileAutomationUrl = getRunningProfileAutomationUrl(this.profileId);
            if (runningProfileAutomationUrl.equals("Profile " + profileId + " is not running or not automated"))
                runningProfileAutomationUrl = startProfile(profileId);
        } catch (IOException e) {
            try {
                runningProfileAutomationUrl = startProfile(profileId);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
        try {
            log.info("Connecting to Remote WebDriver at " + runningProfileAutomationUrl);
            return new RemoteWebDriver(new URI(runningProfileAutomationUrl).toURL(), new ChromeOptions());
        } catch (MalformedURLException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public WebDriver apply(Integer port) {
        String runningAutomationUrl = "http://127.0.0.1:" + port;
        try {
            return new RemoteWebDriver(new URI(runningAutomationUrl).toURL(), new ChromeOptions());
        } catch (MalformedURLException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public String getRunningProfileAutomationUrl(String profileId) throws IOException {
        String url = "http://127.0.0.1:32002/api/v1/profile/selenium?profileId=" + profileId;

        String json = getJson(url);

        Gson gson = new Gson();
        HashMap hashMap = gson.fromJson(json, HashMap.class);
        return hashMap.get("value").toString();
    }

    public String startProfile(String profileId) throws IOException {
        String url = "http://127.0.0.1:32002/api/v1/profile/start?automation=true&profileId=" + profileId;

        String json = getJson(url);

        Gson gson = new Gson();
        HashMap hashMap = gson.fromJson(json, HashMap.class);
        return hashMap.get("value").toString();
    }

    private String getJson(String url) throws IOException {
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        con.setRequestMethod("GET");

        return IOUtils.toString(con.getInputStream(), Charset.defaultCharset());
    }
}
