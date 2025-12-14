#!/bin/bash

# Script pour exécuter les tests fonctionnels Cucumber avec Selenium

echo "=========================================="
echo "Tests Fonctionnels UML Enhancing"
echo "=========================================="
echo ""

# Couleurs pour les messages
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Vérifier que Java est installé
if ! command -v java &> /dev/null; then
    echo -e "${RED}❌ Java n'est pas installé${NC}"
    exit 1
fi

# Vérifier que Maven est installé
if ! command -v mvn &> /dev/null; then
    echo -e "${RED}❌ Maven n'est pas installé${NC}"
    exit 1
fi

# Vérifier que Chrome est installé
if ! command -v google-chrome &> /dev/null && ! command -v chromium-browser &> /dev/null && ! command -v chrome &> /dev/null; then
    echo -e "${YELLOW}⚠️  Google Chrome ne semble pas installé. Les tests pourraient échouer.${NC}"
fi

echo -e "${GREEN}✓${NC} Java: $(java -version 2>&1 | head -n 1)"
echo -e "${GREEN}✓${NC} Maven: $(mvn -version | head -n 1)"
echo ""

# Options
MODE="normal"
TAGS=""

# Parser les arguments
while [[ $# -gt 0 ]]; do
    case $1 in
        --headless)
            MODE="headless"
            shift
            ;;
        --tags)
            TAGS="$2"
            shift 2
            ;;
        --help)
            echo "Usage: $0 [OPTIONS]"
            echo ""
            echo "Options:"
            echo "  --headless       Exécuter les tests en mode headless (sans interface graphique)"
            echo "  --tags TAG       Exécuter uniquement les tests avec un tag spécifique"
            echo "  --help           Afficher cette aide"
            echo ""
            echo "Exemples:"
            echo "  $0                      # Exécuter tous les tests"
            echo "  $0 --headless           # Exécuter en mode headless"
            echo "  $0 --tags @smoke        # Exécuter uniquement les tests @smoke"
            exit 0
            ;;
        *)
            echo -e "${RED}Option inconnue: $1${NC}"
            echo "Utilisez --help pour voir les options disponibles"
            exit 1
            ;;
    esac
done

# Configuration du mode headless
if [ "$MODE" = "headless" ]; then
    echo -e "${YELLOW}Mode headless activé${NC}"
    export HEADLESS=true
fi

# Nettoyer les anciens rapports
echo "Nettoyage des anciens rapports..."
rm -rf target/cucumber-reports/
echo ""

# Exécuter les tests
echo "=========================================="
echo "Exécution des tests fonctionnels..."
echo "=========================================="
echo ""

if [ -n "$TAGS" ]; then
    echo -e "${YELLOW}Exécution des tests avec le tag: $TAGS${NC}"
    mvn test -Dtest=CucumberTestRunner -Dcucumber.filter.tags="$TAGS"
else
    mvn test -Dtest=CucumberTestRunner
fi

# Capturer le code de sortie
TEST_EXIT_CODE=$?

echo ""
echo "=========================================="

# Afficher les résultats
if [ $TEST_EXIT_CODE -eq 0 ]; then
    echo -e "${GREEN}✓ Tests réussis!${NC}"
else
    echo -e "${RED}✗ Tests échoués${NC}"
fi

echo "=========================================="
echo ""

# Afficher le chemin du rapport
if [ -f "target/cucumber-reports/cucumber.html" ]; then
    echo -e "${GREEN}Rapport HTML généré:${NC} target/cucumber-reports/cucumber.html"
    echo ""
    echo "Pour ouvrir le rapport:"
    echo "  xdg-open target/cucumber-reports/cucumber.html     # Linux"
    echo "  open target/cucumber-reports/cucumber.html         # macOS"
    echo "  start target/cucumber-reports/cucumber.html        # Windows Git Bash"
fi

echo ""

exit $TEST_EXIT_CODE
