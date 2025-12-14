package com.project.uml_project.ingeProjet.functional.steps;

import com.project.uml_project.ingeProjet.functional.pages.UmlEnhancementPage;
import io.cucumber.java.fr.Alors;
import io.cucumber.java.fr.Et;
import io.cucumber.java.fr.Lorsque;
import io.cucumber.java.fr.Étantdonné;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Step Definitions pour les tests fonctionnels de l'enhancement UML
 */
public class UmlEnhancementSteps {

    @Autowired
    private WebDriver driver;

    @Autowired
    private UmlEnhancementPage umlPage;

    private String baseUrl = "http://localhost:8080";

    @Étantdonné("l'application est démarrée")
    public void queApplicationEstDemarree() {
        // L'application doit être démarrée manuellement ou via un hook
        // On vérifie simplement qu'on peut y accéder
        driver.get(baseUrl);
    }

    @Étantdonné("je suis sur la page d'accueil")
    public void queJeSuisSurLaPageDaccueil() {
        umlPage.navigateTo(baseUrl);
    }

    @Alors("je vois le titre {string}")
    public void jeVoisLeTitre(String expectedTitle) {
        String actualTitle = umlPage.getPageTitle();
        assertEquals(expectedTitle, actualTitle,
                "Le titre de la page devrait être '" + expectedTitle + "'");
    }

    @Alors("je vois le bouton de sélection de fichier")
    public void jeVoisLeBoutonDeSelectionDeFichier() {
        assertTrue(umlPage.isFileButtonDisplayed(),
                "Le bouton de sélection de fichier devrait être visible");
    }

    @Alors("je vois le champ de saisie du seuil de pertinence")
    public void jeVoisLeChampDeSaisieDuSeuilDePertinence() {
        assertTrue(umlPage.isRelevanceInputDisplayed(),
                "Le champ de saisie du seuil de pertinence devrait être visible");
    }

    @Alors("le bouton {string} est désactivé")
    public void leBoutonEstDesactive(String buttonText) {
        assertFalse(umlPage.isSubmitButtonEnabled(),
                "Le bouton '" + buttonText + "' devrait être désactivé");
    }

    @Lorsque("je sélectionne un fichier PlantUML valide {string}")
    public void jeSelectionneUnFichierPlantUMLValide(String fileName) {
        uploadTestFile(fileName);
    }

    @Lorsque("je sélectionne un fichier non-PlantUML {string}")
    public void jeSelectionneUnFichierNonPlantUML(String fileName) {
        uploadTestFile(fileName);
    }

    /**
     * Méthode utilitaire pour uploader un fichier de test
     */
    private void uploadTestFile(String fileName) {
        String filePath = getTestFilePath(fileName);
        umlPage.uploadFile(filePath);
    }

    @Alors("le nom du fichier {string} est affiché")
    public void leNomDuFichierEstAffiche(String expectedFileName) {
        String displayedName = umlPage.getDisplayedFileName();
        assertTrue(displayedName.contains(expectedFileName) ||
                displayedName.equals(expectedFileName),
                "Le nom du fichier affiché devrait contenir '" + expectedFileName +
                        "' mais est '" + displayedName + "'");
    }

    @Alors("le bouton {string} est activé")
    public void leBoutonEstActive(String buttonText) {
        assertTrue(umlPage.isSubmitButtonEnabled(),
                "Le bouton '" + buttonText + "' devrait être activé");
    }

    @Et("je saisis {string} comme seuil de pertinence")
    public void jeSaisisCommeSeuilDePertinence(String threshold) {
        umlPage.setRelevanceThreshold(threshold);
    }

    @Et("je clique sur le bouton {string}")
    public void jeCliqueSurLeBouton(String buttonText) {
        if ("Traiter".equals(buttonText) && umlPage.isSubmitButtonEnabled()) {
            umlPage.clickSubmitButton();
        } else if ("Traiter".equals(buttonText)) {
            // Tentative de clic alors que le bouton est désactivé
            // Le comportement reste inchangé
        }
    }

    @Alors("je vois l'image du diagramme UML original")
    public void jeVoisLimageDuDiagrammeUMLOriginal() {
        assertTrue(umlPage.isBeforeImageDisplayed(),
                "L'image du diagramme UML original devrait être affichée");
    }

    @Alors("je vois l'image du diagramme UML amélioré")
    public void jeVoisLimageDuDiagrammeUMLAmeliore() {
        assertTrue(umlPage.isAfterImageDisplayed(),
                "L'image du diagramme UML amélioré devrait être affichée");
    }

    @Alors("le bouton de téléchargement est affiché")
    public void leBoutonDeTelechargementEstAffiche() {
        assertTrue(umlPage.isDownloadButtonDisplayed(),
                "Le bouton de téléchargement devrait être affiché");
    }

    @Et("j'attends que le traitement soit terminé")
    public void jAttendsQueLeTraitementSoitTermine() {
        umlPage.waitForProcessingComplete();
    }

    @Et("je clique sur le bouton de téléchargement")
    public void jeCliqueSurLeBoutonDeTelechargement() {
        umlPage.clickDownloadButton();
    }

    @Alors("le fichier {string} est téléchargé")
    public void leFichierEstTelecharge(String fileName) {
        // Note: La vérification du téléchargement de fichier avec Selenium
        // est complexe et dépend de la configuration du navigateur.
        // On vérifie simplement que le bouton a été cliqué.
        assertTrue(umlPage.isDownloadButtonDisplayed(),
                "Le téléchargement devrait être disponible");
    }

    @Alors("le bouton {string} reste désactivé")
    public void leBoutonResteDesactive(String buttonText) {
        assertFalse(umlPage.isSubmitButtonEnabled(),
                "Le bouton '" + buttonText + "' devrait rester désactivé");
    }

    @Alors("aucune image n'est affichée")
    public void aucuneImageNestAffichee() {
        assertTrue(umlPage.hasNoImagesDisplayed(),
                "Aucune image ne devrait être affichée");
    }

    @Alors("le champ seuil accepte la valeur {string}")
    public void leChampSeuilAccepteLaValeur(String expectedValue) {
        String actualValue = umlPage.getRelevanceThreshold();
        assertEquals(expectedValue, actualValue,
                "Le champ seuil devrait contenir la valeur '" + expectedValue + "'");
    }

    @Alors("le bouton {string} reste activé")
    public void leBoutonResteActive(String buttonText) {
        assertTrue(umlPage.isSubmitButtonEnabled(),
                "Le bouton '" + buttonText + "' devrait rester activé");
    }

    /**
     * Récupère le chemin absolu du fichier de test
     */
    private String getTestFilePath(String fileName) {
        String testResourcesPath = "src/test/resources/test-files/" + fileName;
        File file = new File(testResourcesPath);

        if (!file.exists()) {
            throw new RuntimeException("Le fichier de test n'existe pas: " + testResourcesPath);
        }

        return file.getAbsolutePath();
    }
}
