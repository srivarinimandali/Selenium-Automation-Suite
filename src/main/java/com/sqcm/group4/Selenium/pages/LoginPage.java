package com.sqcm.group4.Selenium.pages;

import com.sqcm.group4.Selenium.utils.ExcelDataProvider;
import com.sqcm.group4.Selenium.utils.ScreenshotManager;
import io.qameta.allure.Allure;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class LoginPage extends BasePage {

    private WebDriverWait wait;
    private ScreenshotManager screenshotManager;
    private ExcelDataProvider dataProvider;

    // Locators based on the login page (adjust as needed)
    @FindBy(id = "i0116")
    private WebElement usernameField;

    @FindBy(id = "idSIButton9")
    private WebElement nextButton;

    @FindBy(id = "i0118")
    private WebElement passwordField;

    @FindBy(id = "idSIButton9")
    private WebElement signInButton;

    public LoginPage(WebDriver driver, ScreenshotManager screenshotManager, ExcelDataProvider dataProvider) {
        super(driver);
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        this.screenshotManager = screenshotManager;
        this.dataProvider = dataProvider;
        PageFactory.initElements(driver, this);
    }

    public void enterUsername(String username) {
        wait.until(ExpectedConditions.visibilityOf(usernameField)).sendKeys(username);
        screenshotManager.takeScreenshot("Step_Entered_Username");
        Allure.step("Entered username: " + username);
    }

    public void clickNext() {
        wait.until(ExpectedConditions.elementToBeClickable(nextButton)).click();
        screenshotManager.takeScreenshot("Step_Clicked_Next");
        Allure.step("Clicked Next");
    }

    public void enterPassword(String password) {
        wait.until(ExpectedConditions.visibilityOf(passwordField)).sendKeys(password);
        screenshotManager.takeScreenshot("Step_Entered_Password");
        Allure.step("Entered password");
    }

    public void clickSignIn() {
        wait.until(ExpectedConditions.elementToBeClickable(signInButton)).click();
        screenshotManager.takeScreenshot("Step_Clicked_SignIn");
        Allure.step("Clicked Sign In");
    }
    private String decodePassword(String encoded) {
        return new String(java.util.Base64.getDecoder().decode(encoded));
    }

    public void login() {
        // Read test data from Excel (row index 1; adjust as needed)
    	String username = dataProvider.getCellData(1, "neu_email");
    	String password = dataProvider.getCellData(1, "neu_password");

        enterUsername(username);
        clickNext();
        enterPassword(password);
        clickSignIn();
    }
}