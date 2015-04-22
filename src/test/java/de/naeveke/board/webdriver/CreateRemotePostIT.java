package de.naeveke.board.webdriver;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

public class CreateRemotePostIT {
    static final Logger logger = Logger.getLogger(CreateRemotePostIT.class.getName());

    private WebDriver driver;

    @Before
    public void setUp() {
        driver = new FirefoxDriver();
    }

    @After
    public void tearDown() {
        try {
            File screenshotFile = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
            FileUtils.copyFile(screenshotFile,new File("./target/surefire-reports/createRemotePostIT.jpg"));
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
        
        driver.quit();
    }

    @Ignore
    @Test
    public void seramisLogin() {
        driver.get("http://localhost:9090/board/");
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        
        WebElement username = driver.findElement(By.name("user"));
        username.sendKeys("seramis@naeveke.de");

        WebElement password = driver.findElement(By.name("pass"));
        password.sendKeys("");

        password.submit();

        // Sleep until the div we want is visible or 5 seconds is over
        long end = System.currentTimeMillis() + 5000;
        while (System.currentTimeMillis() < end) {

            if (driver.getPageSource().contains("Näveke")) {
                return;
            }
        }

        Assert.fail("Name not found");
    }
}
