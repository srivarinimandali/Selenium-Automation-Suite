package com.sqcm.group4.Selenium;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.sqcm.group4.Selenium.pages.LoginPage;
import com.sqcm.group4.Selenium.pages.StudentHubPage;
import com.sqcm.group4.Selenium.utils.ExcelDataProvider;
import com.sqcm.group4.Selenium.utils.ReportManager;
import com.sqcm.group4.Selenium.utils.ScreenshotManager;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.File;
import java.time.Duration;
import java.util.Set;

@Epic("Student Hub Automation")
@Feature("Transcript Download")
public class TranscriptDownloadTest extends BaseTest {

    @Test
    public void downloadTranscriptTest() {
        String testName = "Download Latest Transcript";
        ScreenshotManager screenshotManager = new ScreenshotManager(driver, testName);
        ExtentTest test = ReportManager.createTest(testName);

        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(40));

            // 🔐 Get credentials from Excel
            String neuEmail = BaseTest.credentialsData.getCellData(1, "neu_email");
            String neuPassword = BaseTest.credentialsData.getCellData(1, "neu_password");
            String transcriptUsername = BaseTest.credentialsData.getCellData(1, "transcript_username");
            String transcriptPassword = BaseTest.credentialsData.getCellData(1, "transcript_password");
            driver.get("https://me.northeastern.edu");
            screenshotManager.takeScreenshot("Step_0_Navigated_to_NEU_Portal");

            // 🧠 Login to NEU portal
            LoginPage loginPage = new LoginPage(driver, screenshotManager, BaseTest.credentialsData);            loginPage.login();
            screenshotManager.takeScreenshot("Step_1_After_Login");

            // ✅ Handle "Yes, this is my device"
            try {
                WebElement yesButton = wait.until(ExpectedConditions.elementToBeClickable(
                        By.xpath("//button[contains(text(),'Yes, this is my device')]")));
                yesButton.click();
                screenshotManager.takeScreenshot("Step_2_Clicked_Yes_This_Is_My_Device");
            } catch (Exception e) {
                System.out.println("⚠ Duo prompt not detected.");
            }

            // ✅ Handle "Stay Signed In"
            try {
                WebElement staySignedInButton = wait.until(ExpectedConditions.elementToBeClickable(
                        By.xpath("//input[@id='idSIButton9' and @value='Yes']")));
                staySignedInButton.click();
                screenshotManager.takeScreenshot("Step_3_Clicked_Yes_Stay_Signed_In");
            } catch (Exception e) {
                System.out.println("⚠ Stay Signed In prompt not found.");
            }

            wait.until(ExpectedConditions.titleContains("Student Hub"));
            screenshotManager.takeScreenshot("Step_4_Student_Hub_Loaded");

            // 🧭 Navigate to transcripts
            StudentHubPage studentHubPage = new StudentHubPage(driver);
            Set<String> beforeClickHandles = driver.getWindowHandles();
            studentHubPage.navigateToTranscripts();
            screenshotManager.takeScreenshot("Step_5_Navigated_to_Transcripts");

            // 🪟 Switch to new tab
            wait.until(driver -> driver.getWindowHandles().size() > beforeClickHandles.size());
            Set<String> afterClickHandles = driver.getWindowHandles();
            for (String handle : afterClickHandles) {
                if (!beforeClickHandles.contains(handle)) {
                    driver.switchTo().window(handle);
                    break;
                }
            }
            wait.until(driver -> ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete"));
            screenshotManager.takeScreenshot("Step_6_Transcript_Page_Loaded");

            // 📥 Switch to iframe if present
            try {
                WebElement iframe = driver.findElement(By.tagName("iframe"));
                if (iframe.isDisplayed()) {
                    driver.switchTo().frame(iframe);
                    System.out.println("✅ Switched to transcript iframe.");
                }
            } catch (Exception e) {
                System.out.println("⚠ No iframe found.");
            }

            // 🔐 Transcript system login (JavaScript injection)
            try {
                Thread.sleep(2000);
                WebElement usernameField = wait.until(ExpectedConditions.elementToBeClickable(By.id("username")));
                WebElement passwordField = wait.until(ExpectedConditions.elementToBeClickable(By.id("password")));
                WebElement loginButton = wait.until(ExpectedConditions.elementToBeClickable(
                        By.xpath("//button[contains(text(),'Log In')]")));

                
                JavascriptExecutor js = (JavascriptExecutor) driver;
//                js.executeScript("arguments[0].value='mandali.sr';", usernameField);
//                js.executeScript("arguments[0].value='Myfavouriteband1d$';", passwordField);
                js.executeScript("arguments[0].value='" + transcriptUsername + "';", usernameField);
                js.executeScript("arguments[0].value='" + transcriptPassword + "';", passwordField);
                Thread.sleep(1000);
                loginButton.click();

                screenshotManager.takeScreenshot("Step_7_After_Transcript_Login");

                // ✅ Duo 2FA (if present)
                try {
                    WebElement duoIframe = wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("iframe")));
                    driver.switchTo().frame(duoIframe);

                    WebElement sendPushButton = wait.until(ExpectedConditions.elementToBeClickable(
                            By.xpath("//button[contains(text(),'Send Me a Push')]")));
                    sendPushButton.click();

                    Thread.sleep(3000); // wait for approval
                    driver.switchTo().defaultContent();
                } catch (Exception e) {
                    System.out.println("⚠ Duo Authentication prompt not detected, continuing.");
                }

            } catch (Exception e) {
                screenshotManager.takeScreenshot("Error_Transcript_Login");
                Assert.fail("❌ Transcript login failed: " + e.getMessage());
            }

            // 📄 Select transcript level/type and submit
            try {
                WebDriverWait wait1 = new WebDriverWait(driver, Duration.ofSeconds(20));

                WebElement transcriptLevelDropdown = wait1.until(ExpectedConditions.presenceOfElementLocated(By.id("levl_id")));
                new Select(transcriptLevelDropdown).selectByValue("GR");

                WebElement transcriptTypeDropdown = wait1.until(ExpectedConditions.presenceOfElementLocated(By.id("type_id")));
                new Select(transcriptTypeDropdown).selectByValue("AUDI");

                WebElement submitButton = wait1.until(ExpectedConditions.elementToBeClickable(By.xpath("//input[@type='submit' and @value='Submit']")));
                submitButton.click();

                screenshotManager.takeScreenshot("Step_8_Transcript_Options_Selected");
            } catch (Exception e) {
                screenshotManager.takeScreenshot("Error_Transcript_Selection");
                Assert.fail("❌ Failed to select transcript options: " + e.getMessage());
            }

            wait.until(ExpectedConditions.titleContains("Academic Transcript"));
            screenshotManager.takeScreenshot("Step_9_Transcript_Loaded");

            // 🖨️ Trigger print and save
            try {
                new Actions(driver).contextClick(driver.findElement(By.tagName("body"))).perform();
                screenshotManager.takeScreenshot("Step_10_Right_Clicked");

                Robot robot = new Robot();
                Thread.sleep(1000);

                robot.keyPress(KeyEvent.VK_META);
                robot.keyPress(KeyEvent.VK_TAB);
                robot.keyRelease(KeyEvent.VK_TAB);
                robot.keyRelease(KeyEvent.VK_META);
                Thread.sleep(2000);

                robot.keyPress(KeyEvent.VK_META);
                robot.keyPress(KeyEvent.VK_P);
                robot.keyRelease(KeyEvent.VK_P);
                robot.keyRelease(KeyEvent.VK_META);
                System.out.println("✅ Pressed 'Command + P' to open Print dialog.");
                Thread.sleep(2000);
                JavascriptExecutor js = (JavascriptExecutor) driver;
                js.executeScript(
                    "const controls = document.querySelector('div.controls');" +
                    "if (controls) { const saveButton = controls.querySelector('cr-button.action-button'); if (saveButton) { saveButton.click(); return true; } else { return false; } } else { return false; }"
                );
                System.out.println("✅ Attempted to click 'Save' button using JavaScript");
                Thread.sleep(2000);

                robot.keyPress(KeyEvent.VK_ENTER);
                robot.keyRelease(KeyEvent.VK_ENTER);
                System.out.println("✅ Pressed Enter to confirm save dialog");
                Thread.sleep(2000);

             // After opening the print dialog, set the filename and press Enter/Return
//                Thread.sleep(3000); // Wait for dialog to appear
           
                // Press Enter to click the Save button
                robot.keyPress(KeyEvent.VK_ENTER);
                robot.keyRelease(KeyEvent.VK_ENTER);
                System.out.println("✅ Clicked Save button using keyboard navigation");

            } catch (Exception e) {
                screenshotManager.takeScreenshot("Error_Print_Dialog");
                System.out.println("❌ Error during print/save: " + e.getMessage());
            }

            // 🧾 Check if PDF is downloaded
            File downloadsDir = new File(System.getProperty("user.home") + "/Downloads");
            boolean fileDownloaded = false;
            for (int i = 0; i < 30; i++) {
                File[] files = downloadsDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".pdf"));
                if (files != null && files.length > 0) {
                    fileDownloaded = true;
                    break;
                }
                Thread.sleep(1000);
            }
         // Expected vs Actual message
            String expected = "Transcript should be downloaded as PDF.";
            String actual = fileDownloaded ? "Transcript PDF found in Downloads folder." : "Transcript PDF NOT found.";

            // Log it in ExtentReport using ReportManager
            ReportManager.logFinalResult(testName, fileDownloaded, expected, actual);

            // Assert as usual
            Assert.assertTrue(fileDownloaded, "❌ Transcript PDF was not downloaded.");
            

        } catch (Exception e) {
            test.log(Status.FAIL, "❌ Test failed: " + e.getMessage());
            e.printStackTrace();
            String screenshotPath = screenshotManager.takeScreenshot("Error");
            test.addScreenCaptureFromPath(screenshotPath);
            Assert.fail("❌ Test failed: " + e.getMessage());
        }
    }
}