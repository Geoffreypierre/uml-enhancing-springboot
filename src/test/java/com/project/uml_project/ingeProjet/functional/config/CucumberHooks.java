package com.project.uml_project.ingeProjet.functional.config;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Hooks Cucumber pour la configuration et le nettoyage des tests
 */
public class CucumberHooks {

    @Autowired(required = false)
    private WebDriver driver;

    private String snapshotDir = "test-snapshots";

    @Before
    public void beforeScenario(Scenario scenario) {
        System.out.println("=== Démarrage du scénario: " + scenario.getName() + " ===");
        new File(snapshotDir).mkdirs();
    }

    @After
    public void afterScenario(Scenario scenario) {
        String timestamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
        String scenarioName = scenario.getName().replaceAll("[^a-zA-Z0-9]", "_");

        if (driver != null) {
            try {
                // Sauvegarder le snapshot HTML
                String pageSource = driver.getPageSource();
                String snapshotFile = snapshotDir + "/" + scenarioName + "_" + timestamp + ".html";
                try (FileWriter writer = new FileWriter(snapshotFile)) {
                    writer.write(pageSource);
                }
                System.out.println("Snapshot HTML sauvegardé: " + snapshotFile);
            } catch (Exception e) {
                System.err.println("Erreur snapshot: " + e.getMessage());
            }

            // Capture d'écran en cas d'échec
            if (scenario.isFailed()) {
                try {
                    byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
                    scenario.attach(screenshot, "image/png", "screenshot-" + scenario.getName());
                } catch (Exception e) {
                    System.err.println("Erreur lors de la capture d'écran: " + e.getMessage());
                }
            }

            // Fermeture du navigateur après chaque scénario
            try {
                driver.quit();
            } catch (Exception e) {
                System.err.println("Erreur lors de la fermeture du navigateur: " + e.getMessage());
            }
        }

        System.out.println("=== Fin du scénario: " + scenario.getName() +
                " - Statut: " + scenario.getStatus() + " ===");
    }
}