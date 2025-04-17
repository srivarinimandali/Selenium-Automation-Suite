package com.sqcm.group4.Selenium;

import com.aventstack.extentreports.Status;
import com.sqcm.group4.Selenium.pages.LoginPage;
import com.sqcm.group4.Selenium.utils.ReportManager;
import com.sqcm.group4.Selenium.utils.ScreenshotManager;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.Duration;

@Epic("Canvas Calendar Automation")
@Feature("Calendar Verification")
public class CanvasCalendarTest extends BaseTest {

    @Test
    public void verifyCanvasCalendarScenario() {
        String testName = "Verify Canvas Calendar";
        ScreenshotManager screenshotManager = new ScreenshotManager(driver, testName);
        ReportManager.createTest(testName);

        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));

            // Step 1: Navigate to Canvas
            driver.get("https://canvas.northeastern.edu/");
            screenshotManager.takeScreenshot("Step_1_Navigated_to_Canvas");

            String neuEmail = BaseTest.credentialsData.getCellData(1, "neu_email");
            String neuPassword = BaseTest.credentialsData.getCellData(1, "neu_password");

            // Step 2: Wait for the login button and click
            WebElement loginLink = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//a[contains(@title,'Log in to Canvas')]")));
            String loginUrl = loginLink.getAttribute("href");
            driver.get(loginUrl);
            screenshotManager.takeScreenshot("Step_2_Navigated_to_Canvas_Login");

            LoginPage loginPage = new LoginPage(driver, screenshotManager, credentialsData);
            loginPage.login();
            screenshotManager.takeScreenshot("Step_3_After_Login");

            // Handle Duo prompts
            try {
                WebElement yesButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(text(),'Yes, this is my device')]")));
                yesButton.click();
                screenshotManager.takeScreenshot("Step_4_Clicked_Yes_This_Is_My_Device");
            } catch (Exception e) {
                System.out.println("⚠ Duo prompt not detected.");
            }

            try {
                WebElement staySignedInButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//input[@id='idSIButton9' and @value='Yes']")));
                staySignedInButton.click();
                screenshotManager.takeScreenshot("Step_5_Clicked_Yes_Stay_Signed_In");
            } catch (Exception e) {
                System.out.println("⚠ Stay Signed In prompt not found.");
            }

            // Step 4: Wait for Dashboard
            wait.until(ExpectedConditions.titleContains("Dashboard"));
            screenshotManager.takeScreenshot("Step_6_Logged_in_to_Canvas");

            // Step 5: Navigate to Calendar
            WebElement calendarLink = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[@href='/calendar']")));
            calendarLink.click();
            screenshotManager.takeScreenshot("Step_7_Clicked_Calendar");

            WebDriverWait wait1 = new WebDriverWait(driver, Duration.ofSeconds(30));
            WebElement createEventBtn = wait1.until(ExpectedConditions.elementToBeClickable(By.id("create_new_event_link")));
            createEventBtn.click();
            screenshotManager.takeScreenshot("Step_8_Clicked_Create_New_Event");

            int rowCount = calendarData.getRowCount();
            System.out.println("📘 Total calendar events: " + rowCount);

            for (int i = 1; i <= rowCount; i++) {
                String title = calendarData.getCellData(i, "Title");
                String time = calendarData.getCellData(i, "Time");
                String details = calendarData.getCellData(i, "Details");

                WebElement titleField = wait1.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input[data-testid='edit-calendar-event-form-title']")));
                titleField.clear();
                titleField.sendKeys(title);

                WebElement timeField = driver.findElement(By.cssSelector("input[data-testid='event-form-start-time']"));
                timeField.clear();
                timeField.sendKeys(time);

                WebElement locationField = driver.findElement(By.cssSelector("input[data-testid='edit-calendar-event-form-location']"));
                locationField.clear();
                locationField.sendKeys(details);

                WebElement submitBtn = wait1.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[text()='Submit']/ancestor::button")));
                submitBtn.click();
                screenshotManager.takeScreenshot("Step_9_Submitted_Event_" + i);

                if (i < rowCount) {
                    WebElement createNewEvent = wait1.until(ExpectedConditions.elementToBeClickable(By.id("create_new_event_link")));
                    createNewEvent.click();
                }
            }

            // Final Validation
            WebElement calendarBtn = wait1.until(ExpectedConditions.visibilityOfElementLocated(By.id("create_new_event_link")));
            boolean condition = calendarBtn.isDisplayed();
            String actual = condition ? "Create New Event button is visible" : "Create New Event button not found";
            String expected = "Create New Event button should be visible";

            ReportManager.logFinalResult(testName, condition, expected, actual);
            Assert.assertTrue(condition, "❌ Calendar page not visible or failed to load.");

        } catch (Exception e) {
            String screenshotPath = screenshotManager.takeScreenshot("Error");
            ReportManager.getTest(testName).log(Status.FAIL, "❌ Canvas calendar test failed: " + e.getMessage());
            ReportManager.getTest(testName).addScreenCaptureFromPath(screenshotPath);
            Assert.fail("❌ Canvas calendar test failed: " + e.getMessage());
        }
    }
}
