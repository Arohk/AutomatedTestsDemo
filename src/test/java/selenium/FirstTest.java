package selenium;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static io.restassured.RestAssured.given;

public class FirstTest {

    ChromeDriver driver;
    WebDriverWait wait;

    @BeforeMethod
    public void setUp() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();

        // Headless implementation
//        ChromeOptions options = new ChromeOptions();
//        options.addArguments("--headless", "--disable-gpu", "--ignore-certificate-errors");
//        driver = new ChromeDriver(options);

        // Different wait types
        // Implicit
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(20));
        // Explicit
        wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        // Page Load
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(50));

        // driver settings
        driver.manage().window().maximize();


    }

    @Test
    public void loginTest() {

        driver.get("http://training.skillo-bg.com/posts/all");
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
    }

    @Test
    public void dropDownTest() {
        driver.get("https://www.mobile.bg/pcgi/mobile.cgi");
        WebElement cookieConsentButton = driver.findElement(By.xpath("//div[@class='fc-dialog-container']//button[@class='fc-button fc-cta-consent fc-primary-button']//p[@class='fc-button-label']"));
        cookieConsentButton.click();
        Select dropDownMarka = new Select(driver.findElement(By.xpath("//select[@name='marka']")));
        dropDownMarka.selectByVisibleText("Mitsubishi");

        Select dropDownModel = new Select(driver.findElement(By.xpath("//select[@name='model']")));
        dropDownModel.selectByVisibleText("Lancer");

        WebElement searchButton = driver.findElement(By.xpath("//*[@id='button2']"));
        wait.until(ExpectedConditions.elementToBeClickable(searchButton)).click();

        WebElement resultContainer = driver.findElement(By.xpath("//table[@class='tablereset'][2]"));
        Assert.assertTrue(resultContainer.isDisplayed());
    }

    @Test
    public void getPosts() {
        given()
                .when()
                .get("http://training.skillo-bg.com:3100/posts/public?take=1&skip=0")
                .then()
                .log()
                .all();
    }

    @AfterMethod
    public void tearDown(){
        driver.close();
    }

}
