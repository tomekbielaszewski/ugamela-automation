package pl.grizwold;

import gherkin.deps.com.google.gson.Gson;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

public class Credentials {
    public String login;
    public String password;

    public Credentials() {
        try {
            String credentialsJson = new String(Files.readAllBytes(Paths.get("pass.json")));
            HashMap credentials = new Gson().fromJson(credentialsJson, HashMap.class);
            this.login = (String) credentials.get("login");
            this.password = (String) credentials.get("password");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
