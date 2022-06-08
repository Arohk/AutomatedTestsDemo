package selenium;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.*;

import java.time.Duration;
import java.util.List;

public class HerokuAppTests {

    WebDriver driver;
    WebDriverWait wait;
    Actions actions;

    @BeforeMethod
    public void setUp() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(20));
        actions = new Actions(driver);
    }

    @AfterMethod
    public void cleanUp() {
//        driver.quit();
        driver.close();
    }

    @Test
    public void addRemoveElements() throws InterruptedException {
        driver.get("https://the-internet.herokuapp.com/add_remove_elements/");

        // Assert empty list of delete buttons
        List<WebElement> elementsContainerChildren = driver.findElements(By.xpath("//div[@id='elements']/descendant::*"));
        Assert.assertTrue(elementsContainerChildren.isEmpty());

        // Click the add elements button to create delete buttons
        WebElement addElementButton = driver.findElement(By.xpath("//button[@onclick='addElement()']"));
        for (int i = 0; i < 3; i++) {
            addElementButton.click();
        }

        // Map created delete elements buttons and assert them
        elementsContainerChildren = driver.findElements(By.xpath("//div[@id='elements']/descendant::*"));

//        WebElement deleteButton = driver.findElement(By.cssSelector(".added-manually"));
        Assert.assertEquals(elementsContainerChildren.size(), 3);

        Thread.sleep(1500);

    }

    @Test
    public void basicAuth() throws InterruptedException {
        driver.get("https://admin:admin@the-internet.herokuapp.com/basic_auth");
        Thread.sleep(1500);
        WebElement text = driver.findElement(By.xpath("//div[@class='example']/p"));
        Assert.assertEquals(text.getText(), "Congratulations! You must have the proper credentials.");
    }

    @Test
    public void contextMenu() throws InterruptedException {
        driver.get("https://the-internet.herokuapp.com/context_menu");

        WebElement contextBox = driver.findElement(By.id("hot-spot"));
        actions.contextClick(contextBox).perform();
        Alert alert = driver.switchTo().alert();

        String alertText = alert.getText();
        Assert.assertEquals(alertText, "You selected a context menu");
        alert.dismiss();

        Thread.sleep(1500);
    }

    @Test
    public void checkboxes()  throws  InterruptedException{
        Thread.sleep(1500);
        driver.get("https://the-internet.herokuapp.com/checkboxes");
        WebElement checkbox1 = driver.findElement(By.xpath("//form[@id='checkboxes']/input[1]"));
        WebElement checkbox2 = driver.findElement(By.xpath("//form[@id='checkboxes']/input[2]"));

        boolean checkboxState1 = checkbox1.isSelected();
        boolean checkboxState2 = checkbox2.isSelected();

        if (checkboxState1) {
            checkbox1.click();
            Assert.assertFalse(checkbox1.isSelected());
        }else{
            checkbox1.click();
            Assert.assertTrue(checkbox1.isSelected());
        }


        if (checkboxState2) {
            checkbox2.click();
        }


        Thread.sleep(1500);
    }



}
