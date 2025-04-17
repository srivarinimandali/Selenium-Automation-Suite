package com.sqcm.group4.Selenium.pages;

import com.sqcm.group4.Selenium.pages.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class TranscriptPage extends BasePage {

    // ✅ Locators based on transcript form
    private By transcriptLevelDropdown = By.id("transcriptLevel");
    private By transcriptTypeDropdown = By.id("transcriptType");
    private By submitButton = By.xpath("//input[@value='Submit']");
    private By printButton = By.xpath("//button[contains(text(),'Print')]");

    public TranscriptPage(WebDriver driver) {
        super(driver);
    }
    
    public void configureAndPrintTranscript(String level, String type) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        // ✅ Select Transcript Level
        wait.until(ExpectedConditions.visibilityOfElementLocated(transcriptLevelDropdown)).sendKeys(level);
        System.out.println("✅ Selected transcript level: " + level);

        // ✅ Select Transcript Type
        wait.until(ExpectedConditions.visibilityOfElementLocated(transcriptTypeDropdown)).sendKeys(type);
        System.out.println("✅ Selected transcript type: " + type);

        // ✅ Click Submit
        wait.until(ExpectedConditions.elementToBeClickable(submitButton)).click();
        System.out.println("✅ Clicked Submit");

        // ✅ Click Print button
        wait.until(ExpectedConditions.elementToBeClickable(printButton)).click();
        System.out.println("✅ Clicked Print button");
    }
}