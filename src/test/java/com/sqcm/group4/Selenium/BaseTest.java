package com.sqcm.group4.Selenium;

import com.sqcm.group4.Selenium.utils.ExcelDataProvider;
import com.sqcm.group4.Selenium.utils.ReportManager;
import com.sqcm.group4.Selenium.utils.ScreenshotManager;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.ITestResult;
import org.testng.annotations.*;

import java.io.File;
import java.time.Duration;
import java.util.HashMap;

public class BaseTest {
    protected WebDriver driver;
    protected ScreenshotManager screenshotManager;

    // ✅ Shared ExcelDataProviders for both tests
    public static ExcelDataProvider credentialsData;
    public static ExcelDataProvider calendarData;

    @BeforeSuite
    public void setupSuite() {
        try {
            String excelPath = System.getProperty("user.dir") + "/src/test/resources/test_data.xlsx";
            File file = new File(excelPath);

            if (!file.exists()) {
                System.out.println("❌ Excel file not found at: " + excelPath);
                throw new RuntimeException("Excel file missing");
            }

            // ✅ Initialize both sheets
            credentialsData = new ExcelDataProvider(excelPath, "Credentials");
            calendarData = new ExcelDataProvider(excelPath, "calendar_events");
            System.out.println("✅ Excel sheets loaded successfully");

            ReportManager.initReport();

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Suite Setup Failed");
        }
    }

    @BeforeMethod
    public void setup(ITestResult result) {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        HashMap<String, Object> prefs = new HashMap<>();
        prefs.put("download.default_directory", System.getProperty("user.dir") + "/downloads");
        options.setExperimentalOption("prefs", prefs);

        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        screenshotManager = new ScreenshotManager(driver, result.getMethod().getMethodName());
    }

    @AfterMethod
    public void tearDown(ITestResult result) {
        if (driver != null) {
            driver.quit();
        }
    }

    @AfterSuite
    public void tearDownSuite() {
        ReportManager.flushReport();
    }
}