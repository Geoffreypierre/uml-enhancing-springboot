package com.project.uml_project.ingeProjet.functional.config;

import com.project.uml_project.ingeProjet.functional.pages.UmlEnhancementPage;
import org.openqa.selenium.WebDriver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * Configuration des Page Objects
 */
@Configuration
public class PageObjectConfig {

    @Bean
    @Scope("cucumber-glue")
    public UmlEnhancementPage umlEnhancementPage(WebDriver driver) {
        return new UmlEnhancementPage(driver);
    }
}
