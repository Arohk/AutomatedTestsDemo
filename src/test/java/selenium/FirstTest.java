package selenium;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.testng.Assert;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class FirstTest {


    public static void main(String[] args) throws InterruptedException {


        // 1st approach - setting chrome driver manually.
//        System.setProperty("webdriver.chrome.driver", "drivers/chromedriver");

        // 2nd approach - use WebDriverManager library.
        WebDriverManager.chromedriver().setup();
        ChromeDriver driver = new ChromeDriver();
        driver.get("http://training.skillo-bg.com/posts/all");
        driver.manage().window().maximize();

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(20));

        // test
        WebElement loginButton = driver.findElement(By.id("nav-link-login"));
        loginButton.click();
        WebElement userNameField = driver.findElement(By.id("defaultLoginFormUsername"));
        userNameField.sendKeys("test51");
        WebElement passwordField = driver.findElement(By.id("defaultLoginFormPassword"));
        passwordField.sendKeys("test51");
        WebElement signInButton = driver.findElement(By.id("sign-in-button"));
        signInButton.click();
        WebElement newPostButton = driver.findElement(By.id("nav-link-new-post"));

        Assert.assertTrue(newPostButton.isDisplayed());

        driver.close();
    }

}
