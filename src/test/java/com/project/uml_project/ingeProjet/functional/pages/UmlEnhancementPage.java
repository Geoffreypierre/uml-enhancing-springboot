package com.project.uml_project.ingeProjet.functional.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.time.Duration;

/**
 * Page Object pour la page d'accueil de l'application UML Enhancement
 */
public class UmlEnhancementPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    // Locators
    private final By titleLocator = By.cssSelector("h1.title");
    private final By fileInputLocator = By.cssSelector("input.file-input");
    private final By fileNameDisplayLocator = By.id("file-name");
    private final By relevanceInputLocator = By.id("relevanceValue");
    private final By submitButtonLocator = By.cssSelector("button.submit-button");
    private final By beforeImageLocator = By.id("uml-image");
    private final By afterImageLocator = By.id("uml-image-after");
    private final By downloadButtonLocator = By.id("download-button");
    private final By fileButtonLocator = By.cssSelector(".file-button");

    public UmlEnhancementPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(30));
    }

    public void navigateTo(String url) {
        driver.get(url);
    }

    public String getPageTitle() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(titleLocator)).getText();
    }

    public boolean isFileInputDisplayed() {
        try {
            return driver.findElement(fileInputLocator).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isRelevanceInputDisplayed() {
        return driver.findElement(relevanceInputLocator).isDisplayed();
    }

    public boolean isSubmitButtonEnabled() {
        return driver.findElement(submitButtonLocator).isEnabled();
    }

    public void uploadFile(String filePath) {
        File file = new File(filePath);
        WebElement fileInput = driver.findElement(fileInputLocator);

        // Pour HtmlUnit, on doit directement envoyer le chemin sans vérifier la
        // visibilité
        // car l'input file est caché par CSS
        try {
            fileInput.sendKeys(file.getAbsolutePath());
        } catch (org.openqa.selenium.ElementNotInteractableException e) {
            // Fallback: utiliser JavaScript executor si l'élément n'est pas interactif
            org.openqa.selenium.JavascriptExecutor js = (org.openqa.selenium.JavascriptExecutor) driver;
            js.executeScript("arguments[0].style.display='block'; arguments[0].style.visibility='visible';", fileInput);
            fileInput.sendKeys(file.getAbsolutePath());
        }
    }

    public String getDisplayedFileName() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(fileNameDisplayLocator)).getText();
    }

    public void setRelevanceThreshold(String threshold) {
        WebElement input = driver.findElement(relevanceInputLocator);
        input.clear();
        input.sendKeys(threshold);
    }

    public String getRelevanceThreshold() {
        return driver.findElement(relevanceInputLocator).getAttribute("value");
    }

    public void clickSubmitButton() {
        WebElement button = wait.until(ExpectedConditions.elementToBeClickable(submitButtonLocator));
        button.click();
    }

    public boolean isBeforeImageDisplayed() {
        try {
            WebElement img = wait.until(ExpectedConditions.visibilityOfElementLocated(beforeImageLocator));
            String src = img.getAttribute("src");
            return src != null && !src.isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isAfterImageDisplayed() {
        try {
            WebElement img = wait.until(ExpectedConditions.visibilityOfElementLocated(afterImageLocator));
            String src = img.getAttribute("src");
            return src != null && !src.isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isDownloadButtonDisplayed() {
        try {
            WebElement button = wait.until(ExpectedConditions.visibilityOfElementLocated(downloadButtonLocator));
            String display = button.getCssValue("display");
            return !"none".equals(display);
        } catch (Exception e) {
            return false;
        }
    }

    public void clickDownloadButton() {
        WebElement button = wait.until(ExpectedConditions.elementToBeClickable(downloadButtonLocator));
        button.click();
    }

    public void waitForProcessingComplete() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(afterImageLocator));
        wait.until(ExpectedConditions.attributeToBeNotEmpty(
                driver.findElement(afterImageLocator), "src"));
    }

    public boolean hasNoImagesDisplayed() {
        try {
            WebElement beforeImg = driver.findElement(beforeImageLocator);
            WebElement afterImg = driver.findElement(afterImageLocator);
            String beforeSrc = beforeImg.getAttribute("src");
            String afterSrc = afterImg.getAttribute("src");
            return (beforeSrc == null || beforeSrc.isEmpty()) &&
                    (afterSrc == null || afterSrc.isEmpty());
        } catch (Exception e) {
            return true;
        }
    }

    public boolean isFileButtonDisplayed() {
        return driver.findElement(fileButtonLocator).isDisplayed();
    }
}
