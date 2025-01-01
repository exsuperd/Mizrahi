import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public WebDriverWait wait;
    public Actions action;
    public WebDriver driver;
    public static String url = "https://www.mizrahi-tefahot.co.il/login/bv5x83ut/index.html#/auth-page-he";
    public static String userName = "5729008386";
    public static String password = "q5q5q5";
    public static int[] equivalentServers = {146, 19, 21, 22, 23, 117, 145}; // equivalent servers for servers 0, 2, 4, 5, 7, 8, 9 correspondingly

    public void initBrowser(String url) {
        try {
            WebDriverManager.chromedriver().setup();
            driver = new ChromeDriver();
            driver.manage().window().maximize();
            driver.get(url);
        } catch (Exception e) {
            System.out.println("Session not created - quiting. Try to manually define chrome driver path" + "\n" + e);
            driver.quit();
        }
        wait = new WebDriverWait(driver, Duration.ofSeconds((15)));
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(15));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(15));
        action = new Actions(driver);
    }

    public void login(String userName, String password) {
        try {
            WebElement userNameTextField = driver.findElement(By.cssSelector("input[id='emailDesktopHeb']"));
            WebElement passwordTextField = driver.findElement(By.cssSelector("input[id='passwordIDDesktopHEB']"));
            WebElement loginButton = driver.findElement(By.cssSelector("div.form-desktop button.btn"));
            wait.until(ExpectedConditions.elementToBeClickable(userNameTextField));
            userNameTextField.click();
            userNameTextField.clear();
            userNameTextField.sendKeys(userName);
            passwordTextField.clear();
            passwordTextField.click();
            passwordTextField.sendKeys(password);
            loginButton.click();
            wait.until(ExpectedConditions.visibilityOf(driver.findElement(By.cssSelector("a.user-greeting-message"))));
            wait.until(ExpectedConditions.elementToBeClickable(driver.findElement(By.cssSelector("a[aria-label='עובר ושב']"))));
        } catch (Exception e) {
            System.out.println("Login failed - closing session" + "\n" + e);
            driver.quit();
        }
    }

    public String getActiveServerID() throws InterruptedException {
        WebElement fluent = driver.findElement(By.cssSelector("a[aria-label='עובר ושב']"));
        fluent.click();
        Thread.sleep(15000);
        driver.switchTo().frame(driver.findElement(By.cssSelector("iframe[id='legacyIframe']"))).switchTo()
                .frame(driver.findElement(By.cssSelector("iframe[id='contentFrame']")));
        WebElement serverIDInfoHolder = driver.findElement(By.id("ctl00_lblDiagnosticInfo"));
        String serverIDText = serverIDInfoHolder.getAttribute("textContent");
        System.out.println("Server text is " + serverIDText);
        assert serverIDText != null;
        String[] info = serverIDText.split(" ");
        String serverIDFull = info[1];
        char serverNum = serverIDFull.charAt(0);
        String currentActiveServerID = String.valueOf(serverNum);
        int activeServer = Integer.parseInt(currentActiveServerID);
        System.out.println("Active server number is " + currentActiveServerID);
        driver.switchTo().defaultContent();
        switch (activeServer) {
            case 0:
                System.out.println("Equivalent active server number is " + equivalentServers[0]);
                break;
            case 2:
                System.out.println("Equivalent active server number is " + equivalentServers[1]);
                break;
            case 4:
                System.out.println("Equivalent active server number is " + equivalentServers[2]);
                break;
            case 5:
                System.out.println("Equivalent active server number is " + equivalentServers[3]);
                break;
            case 7:
                System.out.println("Equivalent active server number is " + equivalentServers[4]);
                break;
            case 8:
                System.out.println("Equivalent active server number is " + equivalentServers[5]);
                break;
            case 9:
                System.out.println("Equivalent active server number is " + equivalentServers[6]);
                break;
            default:
                System.out.println("The active server does NOT have an equivalent server number");
        }
        return currentActiveServerID;
    }

    public void verdict(int[] blackList) throws InterruptedException {
        String activeServerId = String.valueOf(getActiveServerID());
        List<String> blackListServerIds = new ArrayList<>();
        for (int i = 0; i < blackList.length; i++) {
            blackListServerIds.add(i, String.valueOf(blackList[i]));
        }
        if (blackListServerIds.contains(activeServerId)) {
            System.out.println("Current active driver is included in the blacklist - deleting cookies and starting over");
            driver.manage().deleteAllCookies();
            Thread.sleep(10000);
            driver.close();
            driver.quit();
            initBrowser(url);
            login(userName, password);
            getActiveServerID();
            verdict(blackList);
        } else {
            System.out.println("Server " + activeServerId + " should be tested. " + "Don't forget to close chrome window and this test " +
                    "when finishing");
        }
    }
}
