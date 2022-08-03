# ugamela-automation
Ugamela automation project based on Selenium WebDriver.

# Getting started
1. Create the `pass.json` file:
```json
{
  "login": "your_ugamela_login",
  "password": "your_ugamela_password"
}
```
2. Read the credentials by creating new Credentials object:
```java
Credentials credentials = new Credentials();
```
3. Login to the game by creating new UgamelaSession object using your WebDriver instance:
```java
public UgamelaSession login(WebDriver $) {
    Credentials credentials = new Credentials();
    UgamelaSession session = new UgamelaSession($);
    if (!session.isLoggedIn())
        session.login(credentials.login, credentials.password);
    return session;
}
```
I won't cover the WebDriver part here. There is plenty of Selenium WebDriver docs out there.  
4. Interact with the game:
```java
SpyReports.SpyReport spyReport = new SpyReports(session)
        .latest()
        .get();
System.out.println(spyReport.hasDefence());
System.out.println(spyReport.hasFleet());
```