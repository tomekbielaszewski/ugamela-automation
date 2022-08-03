# ugamela-automation
Ugamela automation project based on Selenium WebDriver.

# Getting started

In order to start using this project you need to either create a credentials file like so:

```json
{
  "login": "your_ugamela_login",
  "password": "your_ugamela_password"
}
```

and then login:

```java
UgamelaSession session = new UgamelaSession(webDriver).login();
```

or pass the credentials by yourself:

```java
UgamelaSession session = new UgamelaSession(webDriver).login(login, pass);
```
  
then finally you can start interacting with the game

```java
SpyReports.SpyReport spyReport = new SpyReports(session)
        .latest()
        .get();
System.out.println(spyReport.hasDefence());
System.out.println(spyReport.hasFleet());
```

or, if you are lazy, use preexisting routines

```java
Farming farming = new Farming();
farming.scanGalaxy(10, 30, galaxy);
farming.farmFromSpyReports(session);
```