# UML Enhancing Spring Boot Application

Application Spring Boot pour l'amélioration automatique de diagrammes UML avec génération d'abstractions via LLM.

## 🚀 Démarrage Rapide

```bash
# Démarrer l'application
./Devtools/run.sh

# Exécuter les tests fonctionnels
./scripts/run-functional-tests.sh
```

## 📂 Structure du Projet

```
.
├── docs/                    # 📚 Documentation complète
│   ├── QUICKSTART_TESTS.md
│   ├── FUNCTIONAL_TESTS_README.md
│   └── ...
├── scripts/                 # 🔧 Scripts d'automatisation
│   ├── run-functional-tests.sh
│   └── run-single-test.sh
├── test-samples/           # 🧪 Échantillons de fichiers PlantUML
│   ├── test-diagram.puml
│   └── ...
├── src/
│   ├── main/java/          # Code source
│   └── test/               # Tests (unitaires et fonctionnels)
│       ├── java/
│       │   └── .../functional/  # Tests Cucumber/Selenium
│       └── resources/
│           └── features/        # Scénarios Gherkin
├── output/                 # Diagrammes générés
├── Devtools/              # Outils de développement
└── pom.xml                # Configuration Maven
```

Voir [STRUCTURE.md](STRUCTURE.md) pour plus de détails.

## 📖 Documentation

### 📊 Rapports de Tests Récents
- 📈 **[RAPPORT FINAL DES TESTS](RAPPORT_FINAL_TESTS.md)** - Résultats complets de la dernière exécution
- 📊 **[TEST_RESULTS.md](TEST_RESULTS.md)** - Analyse détaillée des résultats

### Tests Fonctionnels
- 🚀 **[Guide de Démarrage Rapide](docs/QUICKSTART_TESTS.md)** (2 minutes)
- 📖 **[Guide Complet](docs/FUNCTIONAL_TESTS_README.md)** (15 minutes)
- 📋 **[Catalogue des Scénarios](docs/TEST_SCENARIOS_CATALOG.md)**
- ✍️ **[Guide de Contribution](docs/CONTRIBUTING_TESTS.md)**
- 🗺️ **[Index de Navigation](docs/TESTS_INDEX.md)**

### Tests Implémentés
- ✅ **20 scénarios** de test fonctionnels (Gherkin/Cucumber)
- ✅ **101 snapshots HTML** générés automatiquement
- ✅ Tests d'interface utilisateur
- ✅ Tests d'upload et traitement UML
- ✅ Tests de validation
- ✅ Tests de performance
- ✅ **Taux de réussite : 40% sans app, 90%+ avec app démarrée**

## 🛠️ Technologies

- **Backend** : Spring Boot 4.0.0, Java 17
- **Tests Fonctionnels** : Cucumber 7.18.1, Selenium 4.16.1
- **Tests Unitaires** : JUnit 5, Mockito
- **LLM** : OpenAI GPT
- **FCA** : FCA4J
- **UML** : PlantUML

## 🧪 Exécution des Tests

### Tests Fonctionnels (Cucumber + Selenium)

```bash
# Tous les tests
./scripts/run-functional-tests.sh

# Mode headless (sans interface)
./scripts/run-functional-tests.sh --headless

# Tests smoke uniquement
./scripts/run-functional-tests.sh --tags @smoke

# Un seul scénario
./scripts/run-single-test.sh "Affichage de la page d accueil"
```

### Tests Unitaires

```bash
# Tous les tests unitaires
mvn test

# Tests spécifiques
mvn test -Dtest=UMLEnhancerTest
```

### Rapports de Tests

Les rapports sont générés dans `target/cucumber-reports/` :
- `cucumber.html` - Rapport HTML interactif
- `cucumber.json` - Rapport JSON
- `cucumber.xml` - Rapport JUnit XML

```bash
# Ouvrir le rapport HTML
xdg-open target/cucumber-reports/cucumber.html
```

## 🏃 Exécution de l'Application

### En mode développement

```bash
# Avec le script
./Devtools/run.sh

# Avec Maven
mvn spring-boot:run
```

### Variables d'Environnement

```bash
# Définir la clé API OpenAI
export OPENAI_API_KEY="your-api-key"

# Ou utiliser le script fourni
source ./Devtools/set_token_env_var.sh
```

L'application sera accessible sur : http://localhost:8080

## 📝 Utilisation

1. Ouvrir http://localhost:8080 dans votre navigateur
2. Sélectionner un fichier PlantUML (.puml)
3. Définir le seuil de pertinence (0 à 1)
4. Cliquer sur "Traiter"
5. Télécharger le diagramme amélioré

## 🎯 Fonctionnalités

- ✅ Upload de fichiers PlantUML
- ✅ Analyse FCA (Formal Concept Analysis)
- ✅ Génération d'abstractions via LLM
- ✅ Validation des abstractions
- ✅ Génération de diagrammes améliorés
- ✅ Visualisation avant/après
- ✅ Téléchargement des résultats

## 🔧 Configuration

### Port du serveur
Modifier dans `src/main/resources/application.properties` :
```properties
server.port=8080
```

### Seuil de pertinence par défaut
Le seuil recommandé est **0.75** (75% de pertinence minimum).

## 📊 CI/CD

Le projet inclut un workflow GitHub Actions pour :
- ✅ Exécution automatique des tests fonctionnels
- ✅ Génération des rapports
- ✅ Upload des artifacts (rapports, screenshots)

Configuration : `.github/workflows/functional-tests.yml`

## 🤝 Contribution

Pour contribuer aux tests fonctionnels :
1. Lire [docs/CONTRIBUTING_TESTS.md](docs/CONTRIBUTING_TESTS.md)
2. Créer un scénario Gherkin dans `src/test/resources/features/`
3. Implémenter les step definitions
4. Tester localement
5. Soumettre une pull request

## 📄 Licence

[Votre licence ici]

## 👥 Auteurs

[Vos informations ici]

## 📞 Support

- 📖 Documentation : `docs/`
- 💬 Issues : GitHub Issues
- 📧 Email : [votre email]

---

**Pour plus d'informations sur les tests fonctionnels, consultez [docs/TESTS_INDEX.md](docs/TESTS_INDEX.md)**
