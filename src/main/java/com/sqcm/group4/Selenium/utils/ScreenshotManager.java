package com.sqcm.group4.Selenium.utils;



import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

public class ScreenshotManager {
    private WebDriver driver;
    private String scenarioName;
    private Path screenshotDir;

    public ScreenshotManager(WebDriver driver, String scenarioName) {
        this.driver = driver;
        this.scenarioName = scenarioName;

        // Create directory for today's date: e.g., screenshots/20250325/ScenarioName/
        String dateFolder = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        screenshotDir = Paths.get("screenshots", dateFolder, scenarioName);

        try {
            Files.createDirectories(screenshotDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String takeScreenshot(String stepName) {
        if (driver == null) {
            return "";
        }
        
        try {
            File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            String filename = stepName.replaceAll("\\s+", "_") + "_" + 
                            LocalDateTime.now().format(DateTimeFormatter.ofPattern("HHmmss")) + ".png";
            Path destination = screenshotDir.resolve(filename);
            Files.copy(screenshot.toPath(), destination);
            return destination.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }
}