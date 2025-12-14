package com.project.uml_project.ingeProjet.functional;

import com.project.uml_project.UmlProjectApplication;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

/**
 * Configuration Spring Boot pour les tests Cucumber
 */
@CucumberContextConfiguration
@SpringBootTest(classes = UmlProjectApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestPropertySource(properties = {
        "server.port=8080"
})
public class CucumberSpringConfiguration {
    // Cette classe configure le contexte Spring pour Cucumber
}
