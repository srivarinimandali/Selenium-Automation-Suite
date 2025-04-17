package com.sqcm.group4.Selenium;

import com.aventstack.extentreports.Status;
import com.sqcm.group4.Selenium.pages.LoginPage;
import com.sqcm.group4.Selenium.utils.ReportManager;
import com.sqcm.group4.Selenium.utils.ScreenshotManager;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Epic("NEU Student Hub Automation")
@Feature("Academic Calendar Interaction")
public class AcademicCalendarTest extends BaseTest {

    @Test
    public void academicCalendarScenarioTest() {
        String testName = "Verify and Interact with Academic Calendar";
        ScreenshotManager screenshotManager = new ScreenshotManager(driver, testName);
        ReportManager.createTest(testName);

        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(40));

            driver.get("https://me.northeastern.edu");
            screenshotManager.takeScreenshot("Step_0_Navigated_to_NEU_Portal");

            LoginPage loginPage = new LoginPage(driver, screenshotManager, BaseTest.credentialsData);
            loginPage.login();
            screenshotManager.takeScreenshot("Step_1_After_Login");

            try {
                WebElement yesButton = wait.until(ExpectedConditions.elementToBeClickable(
                        By.xpath("//button[contains(text(),'Yes, this is my device')]")));
                yesButton.click();
                screenshotManager.takeScreenshot("Step_2_Clicked_Yes_This_Is_My_Device");
            } catch (Exception e) {
                System.out.println("⚠ Duo prompt not detected.");
            }

            try {
                WebElement staySignedInButton = wait.until(ExpectedConditions.elementToBeClickable(
                        By.xpath("//input[@id='idSIButton9' and @value='Yes']")));
                staySignedInButton.click();
                screenshotManager.takeScreenshot("Step_3_Clicked_Yes_Stay_Signed_In");
            } catch (Exception e) {
                System.out.println("⚠ Stay Signed In prompt not found.");
            }

            wait.until(ExpectedConditions.titleContains("Student Hub"));
            screenshotManager.takeScreenshot("Step_3_Student_Hub_Loaded");

            WebElement resourcesLink = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("a[data-testid='link-resources']")));
            resourcesLink.click();
            screenshotManager.takeScreenshot("Step_4_Clicked_Resources");

            WebElement academicsTab = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//span[contains(text(), 'Academics, Classes & Registration')]")));
            academicsTab.click();
            screenshotManager.takeScreenshot("Step_5_Clicked_Academics_Classes_Registration");

            WebElement acadCalendar = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//a[contains(text(),'Academic Calendar')]")));
            acadCalendar.click();
            screenshotManager.takeScreenshot("Step_6_Clicked_Academic_Calendar");

            ArrayList<String> tabs = new ArrayList<>(driver.getWindowHandles());
            driver.switchTo().window(tabs.get(1));
            screenshotManager.takeScreenshot("Step_7_Switched_To_Registrar_Tab");

            WebElement currentAcadCal = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//a[contains(@href, 'academic-calendar') and contains(text(),'Academic Calendar')]")));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", currentAcadCal);
//            Thread.sleep(500);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", currentAcadCal);
            screenshotManager.takeScreenshot("Step_8_Clicked_Current_Academic_Calendar");

            WebElement academicCalendarLink = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//a[contains(@href, '/article/academic-calendar') and contains(., 'Academic Calendar')]")));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", academicCalendarLink);
//            Thread.sleep(500);
            academicCalendarLink.click();
            screenshotManager.takeScreenshot("Step_9_Clicked_Final_Calendar_Link");

//            wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("iframe")));

            List<WebElement> iframes = driver.findElements(By.tagName("iframe"));
            boolean frameFound = false;

            for (WebElement iframe : iframes) {
                try {
                    driver.switchTo().frame(iframe);

                    // Only try to find the checkbox for 1 seconds inside each iframe
                    WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(2));
                    WebElement ugCheckbox = shortWait.until(ExpectedConditions.presenceOfElementLocated(By.id("mixItem0")));

                    if (ugCheckbox != null && ugCheckbox.isDisplayed()) {
                        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", ugCheckbox);
                        shortWait.until(ExpectedConditions.elementToBeClickable(ugCheckbox));

                        if (ugCheckbox.isSelected()) {
                            ugCheckbox.click();
                            System.out.println("✅ Unchecked 'Undergraduate (UG)' checkbox");
                        } else {
                            System.out.println("☑️ UG checkbox was already unchecked");
                        }

                        screenshotManager.takeScreenshot("Step_10_Unchecked_UG_Checkbox");
                        frameFound = true;
                        break;
                    }
                } catch (Exception e) {
                    // If checkbox not found in current iframe, go back to main document
                    driver.switchTo().defaultContent();
                }
            }

            if (!frameFound) {
                throw new NoSuchElementException("❌ 'Undergraduate (UG)' checkbox not found in any iframe.");
            }
            
            // Final scroll and screenshot
            ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight);");
            screenshotManager.takeScreenshot("Step_11_Scrolled_Bottom_Iframe");

//            ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight);");
//            wait.until(driver1 -> ((JavascriptExecutor) driver1)
//                    .executeScript("return document.readyState").equals("complete"));
//            screenshotManager.takeScreenshot("Step_11_Scrolled_Bottom_Iframe");

            ReportManager.logFinalResult(testName, true,
                    "Academic Calendar checkbox toggled and verified",
                    "Success");

        } catch (Exception e) {
            String path = screenshotManager.takeScreenshot("Error");
            ReportManager.getTest(testName).log(Status.FAIL, "❌ Exception: " + e.getMessage());
            ReportManager.getTest(testName).addScreenCaptureFromPath(path);
            Assert.fail("❌ Scenario failed: " + e.getMessage());
        }
    }
}