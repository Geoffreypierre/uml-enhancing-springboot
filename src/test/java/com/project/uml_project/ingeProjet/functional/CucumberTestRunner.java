package com.project.uml_project.ingeProjet.functional;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

/**
 * Runner JUnit pour exécuter les tests Cucumber
 */
@RunWith(Cucumber.class)
@CucumberOptions(features = "src/test/resources/features", glue = {
        "com.project.uml_project.ingeProjet.functional"
}, plugin = {
        "pretty",
        "html:target/cucumber-reports/cucumber.html",
        "json:target/cucumber-reports/cucumber.json",
        "junit:target/cucumber-reports/cucumber.xml"
}, monochrome = true, snippets = CucumberOptions.SnippetType.CAMELCASE)
public class CucumberTestRunner {
    // Cette classe reste vide, elle sert uniquement de point d'entrée pour JUnit
}
