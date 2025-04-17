package com.sqcm.group4.Selenium.pages;

import com.sqcm.group4.Selenium.pages.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.Set;

public class StudentHubPage extends BasePage {

    // ✅ Updated locators
    private By resourcesTab = By.xpath("//a[@data-testid='link-resources']");
    private By academicsText = By.xpath("//span[contains(text(),'Academics, Classes')]");
    private By transcriptsLink = By.xpath("//a[contains(text(),'My Transcript')]");

    public StudentHubPage(WebDriver driver) {
        super(driver);
    }
    
    public void navigateToTranscripts() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        // ✅ Click Resources tab
        WebElement resources = wait.until(ExpectedConditions.elementToBeClickable(resourcesTab));
        resources.click();
        System.out.println("✅ Clicked on Resources tab");

        // ✅ Click Academics, Classes & Registration
        WebElement academics = wait.until(ExpectedConditions.visibilityOfElementLocated(academicsText));
        academics.click();
        System.out.println("✅ Clicked on Academics, Classes & Registration");

        // ✅ Click My Transcript (opens in a new tab)
        WebElement transcripts = wait.until(ExpectedConditions.elementToBeClickable(transcriptsLink));
        transcripts.click();
        System.out.println("✅ Clicked on My Transcript link");

        // ✅ Handle new tab
        Set<String> windowHandles = driver.getWindowHandles();
        for (String handle : windowHandles) {
            driver.switchTo().window(handle);
        }
        System.out.println("✅ Switched to Transcript page");
    }
}