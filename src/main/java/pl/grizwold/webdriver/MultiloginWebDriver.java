package pl.grizwold.webdriver;

import com.google.gson.Gson;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.IOException;
import java.net.*;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Optional;

/**
 * Multilogin project used here as a convenient way of obtaining the Selenium WebDriver with full browser fingerprint masking.
 * <a href="https://multilogin.com/download/">Multilogin download page</a>
 */
@Slf4j
public class MultiloginWebDriver {
    private final String profileId;
    public int port;
    public String url;

    public MultiloginWebDriver(String profileId) {
        this.profileId = profileId;
    }

    @SneakyThrows
    public WebDriver startAndConnect() {
        if (isActive(this.profileId)) {
            stopProfile(this.profileId);
        }
        Thread.sleep(5000);
        url = startProfile(profileId);
        port = Integer.parseInt(url.split(":")[2]);

        try {
            log.info("Connecting to Remote WebDriver at " + url);
            return new RemoteWebDriver(new URI(url).toURL(), new ChromeOptions());
        } catch (MalformedURLException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<WebDriver> connect(Integer port) {
        this.port = port;
        this.url = "http://127.0.0.1:" + port;

        try {
            return Optional.of(new RemoteWebDriver(new URI(this.url).toURL(), new ChromeOptions()));
        } catch (MalformedURLException | URISyntaxException | RuntimeException e) {
            log.error("Couldn't connect to cached WebDriver {}. Cause: {}", this.url, e.getMessage());
            return Optional.empty();
        }
    }

    private String startProfile(String profileId) throws IOException {
        log.info("Starting profile {}", profileId);
        String url = "http://127.0.0.1:32002/api/v1/profile/start?automation=true&profileId=" + profileId;

        String json = getJson(url);

        Gson gson = new Gson();
        HashMap hashMap = gson.fromJson(json, HashMap.class);
        return hashMap.get("value").toString();
    }

    private Boolean isActive(String profileId) throws IOException {
        String url = "http://127.0.0.1:32002/api/v1/profile/active?profileId=" + profileId;

        String json = getJson(url);

        Gson gson = new Gson();
        HashMap hashMap = gson.fromJson(json, HashMap.class);
        return Boolean.valueOf(hashMap.get("value").toString());
    }

    private void stopProfile(String profileId) throws IOException {
        log.info("Stopping profile {}", profileId);
        String url = "http://127.0.0.1:32002/api/v1/profile/stop?profileId=" + profileId;
        getJson(url);
    }

    private String getJson(String url) throws IOException {
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        con.setRequestMethod("GET");

        return IOUtils.toString(con.getInputStream(), Charset.defaultCharset());
    }
}
