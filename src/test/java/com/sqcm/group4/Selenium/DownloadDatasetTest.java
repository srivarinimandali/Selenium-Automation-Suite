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

@Epic("NEU Repository Automation")
@Feature("Dataset ZIP Download")
public class DownloadDatasetTest extends BaseTest {

    @Test
    public void downloadDatasetTest() {
        String testName = "Download DRS Dataset ZIP";
        ScreenshotManager screenshotManager = new ScreenshotManager(driver, testName);
        ReportManager.createTest(testName);

        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
            File downloadDir = new File(System.getProperty("user.dir") + "/downloads"); // project-level folder            // 🔄 Clean up old ZIPs before test
            for (File file : downloadDir.listFiles()) {
                if (file.getName().endsWith(".zip") || file.getName().endsWith(".crdownload")) {
                    file.delete();
                }
            }

            // Step 1: Visit library page
            driver.get("https://onesearch.library.northeastern.edu/discovery/search?vid=01NEU_INST:NU&lang=en");
            screenshotManager.takeScreenshot("Step_1_Library_Home");

            // Step 2: Click DRS in navbar
            WebElement drsLink = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//a[.//span[text()='digital repository service']]")));
            drsLink.click();
            screenshotManager.takeScreenshot("Step_2_Clicked_DRS");

            // Step 3: Switch to new tab
            String originalWindow = driver.getWindowHandle();
            wait.until(driver -> driver.getWindowHandles().size() > 1);
            for (String handle : driver.getWindowHandles()) {
                if (!handle.equals(originalWindow)) {
                    driver.switchTo().window(handle);
                    break;
                }
            }
            wait.until(ExpectedConditions.urlContains("repository.library.northeastern.edu"));
            screenshotManager.takeScreenshot("Step_3_Switched_To_DRS");

            // Step 4: Click Datasets
            WebElement datasetsLink = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//a[@href='/datasets']")));
            datasetsLink.click();
            screenshotManager.takeScreenshot("Step_4_Clicked_Datasets");

            // Step 5: Click ZIP download in first dataset
            WebElement zipLink = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//article[@class='drs-item']//a[contains(@title, 'Zip File')]")));
            zipLink.click();
            screenshotManager.takeScreenshot("Step_5_Clicked_Zip");

            // Step 6: Wait for ZIP to fully download
            long previousSize = -1;
            int maxWaitSecs = 600; // 7 minutes
            boolean zipDownloaded = false;

            for (int i = 1; i <= maxWaitSecs; i++) {
                File[] crdownloads = downloadDir.listFiles((dir, name) -> name.endsWith(".crdownload"));
                File[] zips = downloadDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".zip"));

                if ((crdownloads == null || crdownloads.length == 0) && zips != null && zips.length > 0) {
                    zipDownloaded = true;
                    System.out.println("✅ ZIP file download completed in " + i + " seconds.");
                    break;
                }

                if (crdownloads != null && crdownloads.length > 0) {
                    File downloadingFile = crdownloads[0];
                    long currentSize = downloadingFile.length();
                    System.out.println("⏳ Downloading... " + i + "s | Size: " + (currentSize / 1024) + " KB");

                    if (currentSize > 0 && currentSize == previousSize) {
                        File maybeZip = new File(downloadingFile.getAbsolutePath().replace(".crdownload", ""));
                        if (maybeZip.exists()) {
                            zipDownloaded = true;
                            System.out.println("✅ ZIP renamed and found in " + i + " seconds.");
                            break;
                        }
                    }

                    previousSize = currentSize;
                }

                Thread.sleep(1000); // wait 1 second
            }

            if (!zipDownloaded) {
                System.out.println("📂 Downloads folder content:");
                for (File file : downloadDir.listFiles()) {
                    System.out.println("• " + file.getName());
                }
            }

            // Step 7: Final Validation
            ReportManager.logFinalResult(testName, zipDownloaded,
                    "ZIP file should be downloaded in 'downloads' folder",
                    zipDownloaded ? "ZIP found" : "ZIP not found");
            Assert.assertTrue(zipDownloaded, "❌ ZIP file was not downloaded");

        } catch (Exception e) {
            String path = screenshotManager.takeScreenshot("Error");
            ReportManager.getTest(testName).log(Status.FAIL, "❌ Exception: " + e.getMessage());
            ReportManager.getTest(testName).addScreenCaptureFromPath(path);
            Assert.fail("❌ Scenario failed: " + e.getMessage());
        }
    }
}