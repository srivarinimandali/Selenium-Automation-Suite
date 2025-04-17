package com.sqcm.group4.Selenium;

import com.aventstack.extentreports.Status;
import com.sqcm.group4.Selenium.utils.ReportManager;
import com.sqcm.group4.Selenium.utils.ScreenshotManager;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.time.Duration;

@Epic("Snell Library Automation")
@Feature("Study Room Reservation")
public class SpotInSnellTest extends BaseTest {

    @Test
    public void runScenario() {
        String testName = "Reserve Study Room - Snell Library";
        ScreenshotManager screenshotManager = new ScreenshotManager(driver, testName);
        ReportManager.createTest(testName);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        JavascriptExecutor js = (JavascriptExecutor) driver;

        try {
            // Step 1: Open NEU Library Website
            driver.get("https://library.northeastern.edu/");
            screenshotManager.takeScreenshot("Step_1_Open_NEU_Library");
            Assert.assertTrue(driver.getTitle().contains("Library"), "Page title should contain 'Library'");

            // Step 2: Click "Reserve A Study Room"
            WebElement reserveLink = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//a[contains(text(),'Reserve A Study Room')]")));
            js.executeScript("arguments[0].click();", reserveLink);
            wait.until(ExpectedConditions.urlContains("library-rooms-spaces"));
            screenshotManager.takeScreenshot("Step_2_Clicked_Reserve_A_Study_Room");

            // Step 3: Click on Boston image
            WebElement bostonImage = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//img[contains(@src, 'Boston.png')]")));
            js.executeScript("arguments[0].click();", bostonImage);
            wait.until(ExpectedConditions.urlContains("ideas/rooms-spaces"));
            screenshotManager.takeScreenshot("Step_3_Clicked_Boston_Image");

            // Step 4: Click "Book a Room" button
            WebElement bookRoomBtn = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//a[contains(text(),'Book a Room')]")));
            js.executeScript("arguments[0].click();", bookRoomBtn);

            // Step 5: Wait for LibCal page
            wait.until(ExpectedConditions.or(
                    ExpectedConditions.urlContains("northeastern.libcal.com"),
                    ExpectedConditions.urlContains("reserve/spaces/studyspace")
            ));
            screenshotManager.takeScreenshot("Step_4_LibCal_Page_Loaded");

            // Step 6: Select 'Individual Study'
            WebElement seatStyleDropdown = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("gid")));
            new Select(seatStyleDropdown).selectByVisibleText("Individual Study");
            screenshotManager.takeScreenshot("Step_5_Selected_Individual_Study");

            // Step 7: Select 'Space For 1-4 people'
            WebElement capacityDropdown = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("capacity")));
            new Select(capacityDropdown).selectByVisibleText("Space For 1-4 people");
            screenshotManager.takeScreenshot("Step_6_Selected_Capacity");

            // Step 8: Scroll down
            js.executeScript("window.scrollTo(0, document.body.scrollHeight);");
            screenshotManager.takeScreenshot("Step_7_Scrolled_Bottom");

            // ✅ Final URL Validation
         // ✅ Final URL Validation - More Flexible
            boolean urlValid = driver.getCurrentUrl().contains("libcal.com/spaces");
            String actual = driver.getCurrentUrl();
            String expected = "URL should contain 'libcal.com/spaces'";
            ReportManager.logFinalResult(testName, urlValid, expected, actual);
            Assert.assertTrue(urlValid, "❌ Final URL didn't match expected path.");
            
        } catch (Exception e) {
            String path = screenshotManager.takeScreenshot("Error");
            ReportManager.getTest(testName).log(Status.FAIL, "❌ Scenario failed: " + e.getMessage());
            ReportManager.getTest(testName).addScreenCaptureFromPath(path);
            Assert.fail("❌ Scenario failed due to Exception: " + e.getMessage());
        }
    }
}