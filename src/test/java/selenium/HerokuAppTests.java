package selenium;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.*;

import java.time.Duration;
import java.util.List;
import java.util.function.Function;

public class HerokuAppTests {

    WebDriver driver;
    WebDriverWait wait;
    Actions actions;
    JavascriptExecutor js;

    @BeforeMethod
    public void setUp() {

        // 1st approach - setting chrome driver manually.
//        System.setProperty("webdriver.chrome.driver", "drivers/chromedriver");

        // 2nd approach - use WebDriverManager library.
        WebDriverManager.chromedriver().setup();

        driver = new ChromeDriver();
        // implicit wait - wait certain time before throwing no such element exception.
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(20));
        // explicit wait - add custom conditions, which should be met.
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        actions = new Actions(driver);
        js = (JavascriptExecutor) driver;
    }

    @AfterMethod
    public void cleanUp() {
        driver.close();
    }

    @Test
    public void addRemoveElements() throws InterruptedException {
        driver.get("https://the-internet.herokuapp.com/add_remove_elements/");

        // Assert empty list of delete buttons -> Size = 0
        List<WebElement> elementsContainerChildren = driver.findElements(By.xpath("//div[@id='elements']/descendant::*"));
        Assert.assertTrue(elementsContainerChildren.isEmpty());

        // Click the add elements button to create delete buttons
        WebElement addElementButton = driver.findElement(By.xpath("//button[@onclick='addElement()']"));
        for (int i = 0; i < 3; i++) {
            addElementButton.click();
        }

        // Same list is now not 0 but 3.
        elementsContainerChildren = driver.findElements(By.xpath("//div[@id='elements']/descendant::*"));


        // Click the delete elements button to create delete buttons
        List<WebElement> deleteButtons = driver.findElements(By.xpath("//button[@onclick='deleteElement()']"));

        for (WebElement element : deleteButtons
        ) {
            element.click();
        }

        deleteButtons = driver.findElements(By.xpath("//button[@onclick='deleteElement()']"));
        Assert.assertEquals(deleteButtons.size(), 0);


//        WebElement deleteButton = driver.findElement(By.cssSelector(".added-manually"));
        Assert.assertEquals(elementsContainerChildren.size(), 3);

    }


    @Test
        //not working
    void dragAndDrop() throws InterruptedException {
        driver.get("http://the-internet.herokuapp.com/drag_and_drop");

        WebElement divA = driver.findElement(By.id("column-a"));
        WebElement divB = driver.findElement(By.id("column-b"));

        Actions builder = new Actions(driver);
        builder.dragAndDrop(divB, divA).perform();

    }

    @Test
    public void basicAuth() throws InterruptedException {
        driver.get("https://admin:admin@the-internet.herokuapp.com/basic_auth");
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

    }

    @Test
    public void checkboxes() throws InterruptedException {
        driver.get("https://the-internet.herokuapp.com/checkboxes");
        WebElement checkbox1 = driver.findElement(By.xpath("//form[@id='checkboxes']/input[1]"));
        WebElement checkbox2 = driver.findElement(By.xpath("//form[@id='checkboxes']/input[2]"));

        boolean checkboxState1 = checkbox1.isSelected();
        boolean checkboxState2 = checkbox2.isSelected();

        if (checkboxState1) {
            checkbox1.click();
            Assert.assertFalse(checkbox1.isSelected());
        } else {
            checkbox1.click();
            Assert.assertTrue(checkbox1.isSelected());
        }


        if (checkboxState2) {
            checkbox2.click();
        }
    }

    @Test
    public void floatingMenu() throws InterruptedException {
        driver.get("http://the-internet.herokuapp.com/floating_menu");
        // assert floating element is there when opening the page
        WebElement homeButton = driver.findElement(By.xpath("//*[@id='menu']//a[text()='Home']"));
        Assert.assertTrue(homeButton.isDisplayed());

        // scroll the page down
        js.executeScript("window.scrollBy(0,2000)");

        // assert floating element is still displayed after the scroll
        Assert.assertTrue(homeButton.isDisplayed());

        // scroll the page up
        js.executeScript("window.scrollBy(0,-1000)");

        js.executeScript("arguments[0].click();", homeButton);

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id='menu']//a[text()='Home']")));
        Assert.assertTrue(homeButton.isDisplayed());

    }

    @Test
    public void dynamicControls() {
        driver.get("http://the-internet.herokuapp.com/dynamic_controls");

        // assert the dynamic checkbox is present after loading the page
        WebElement checkbox = driver.findElement(By.id("checkbox"));
        Assert.assertTrue(checkbox.isDisplayed());

        // click the remove button and wait until the loading animation is gone
        WebElement removeButton = driver.findElement(By.xpath("//button[text()='Remove']"));
        removeButton.click();

//        // wait until the animation for removing the checkbox is gone
//        WebElement loadingAnimation = driver.findElement(By.xpath("//div[@id='loading']"));
//        wait.until(ExpectedConditions.invisibilityOf(loadingAnimation));


        // fluent wait example
        Wait<WebDriver> wait = new FluentWait<WebDriver>(driver)
                .withTimeout(Duration.ofSeconds(30))
                .pollingEvery(Duration.ofSeconds(1))
                .ignoring(NoSuchElementException.class);

        WebElement loadingBar = wait.until(new Function<WebDriver, WebElement>() {
            public WebElement apply(WebDriver driver) {
                return driver.findElement(By.xpath("//div[@id='loading']"));
            }
        });

        // Assert that the checkbox is no longer displayed after clicking the remove button. Assert the text.
        wait.until(ExpectedConditions.invisibilityOf(checkbox));
        Assert.assertEquals(driver.findElement(By.id("message")).getText(), "It's gone!");

    }

    @Test
    public void dynamicLoading() throws InterruptedException {
        driver.get("http://the-internet.herokuapp.com/dynamic_loading/2");

        By startButton = By.xpath("//div[@id='start']/button");
        By helloWorldText = By.xpath("//div[@id='finish']");

        WebElement startButtonWebElement = driver.findElement(startButton);
        startButtonWebElement.click();
        WebElement helloWorldTextWebElement = driver.findElement(helloWorldText);

        Thread.sleep(1500);

    }

    @Test
    public void switchWindows() throws InterruptedException {
        driver.get("http://the-internet.herokuapp.com/windows");
        String firstWindowHandle = driver.getWindowHandle();
        WebElement clickHereLink = driver.findElement(By.linkText("Click Here"));
        clickHereLink.click();

        for (String winHandle : driver.getWindowHandles()
        ) {
            driver.switchTo().window(winHandle);
        }

        Thread.sleep(1500);

    }

    @Test
    public void iFrames() {
        driver.get("http://the-internet.herokuapp.com/iframe");

        // step into the frame in which the web element is located
        driver.switchTo().frame("mce_0_ifr");
        // now that we are in the frame we are now able to save the locator to a web element
        WebElement textElement = driver.findElement(By.xpath("//*[@id='tinymce']//p"));
        textElement.clear();
        textElement.sendKeys("some text");

        // switch back to the main document or first frame
        driver.switchTo().defaultContent();
        // now that we are in the main document we are able to find the element which is out of the iframes
        WebElement headerText = driver.findElement(By.xpath("//div[@class='example']/h3"));

    }

    @Test
    public void nestedFrames() throws InterruptedException {
        driver.get("http://the-internet.herokuapp.com/nested_frames");

        // step into the frame in which the web element is located
        driver.switchTo().frame("frame-top").switchTo().frame("frame-left");

        WebElement leftFrameBodyText = driver.findElement(By.xpath("//body"));
        Assert.assertEquals(leftFrameBodyText.getText(), "LEFT");

        // switching to parent frame so child frames are accessible
//        driver.switchTo().parentFrame();
        driver.switchTo().defaultContent();
        driver.switchTo().frame("frame-top").switchTo().frame("frame-middle");
        // switch to child frame
//        driver.switchTo().frame("frame-middle");

        WebElement middleFrameBodyText = driver.findElement(By.xpath("//body"));
        Assert.assertEquals(middleFrameBodyText.getText(), "MIDDLE");

        Thread.sleep(1500);

    }


}
