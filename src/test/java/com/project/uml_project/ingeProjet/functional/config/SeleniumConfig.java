package com.project.uml_project.ingeProjet.functional.config;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * Configuration Selenium pour les tests fonctionnels
 * Utilise HtmlUnit pour les tests sans interface graphique
 */
@Configuration
public class SeleniumConfig {

    @Bean
    @Scope("cucumber-glue")
    public WebDriver webDriver() {
        // Utiliser HtmlUnit qui ne nécessite pas de navigateur installé
        HtmlUnitDriver driver = new HtmlUnitDriver(true); // true = JavaScript activé

        // Ne pas lancer d'exception sur les erreurs JavaScript
        driver.getWebClient().getOptions().setThrowExceptionOnScriptError(false);
        driver.getWebClient().getOptions().setThrowExceptionOnFailingStatusCode(false);
        driver.getWebClient().getOptions().setCssEnabled(false); // Désactiver CSS pour accélérer

        return driver;
    }
}
