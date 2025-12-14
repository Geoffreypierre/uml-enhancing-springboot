package com.project.uml_project.ingeProjet.functional.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * Configuration des propriétés pour les tests fonctionnels
 */
@Configuration
@PropertySource("classpath:functional-test.properties")
public class TestPropertiesConfig {
}
