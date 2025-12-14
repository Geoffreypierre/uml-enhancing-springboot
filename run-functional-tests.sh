#!/bin/bash

# Script pour exécuter les tests fonctionnels
# Usage: ./run-functional-tests.sh [OPTIONS]

set -e  # Arrêter en cas d'erreur

# Couleurs pour l'affichage
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Fonction pour afficher les messages
log_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

log_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

log_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Fonction pour vérifier si l'application est démarrée
check_app_running() {
    if curl -s http://localhost:8080 > /dev/null 2>&1; then
        return 0
    else
        return 1
    fi
}

# Fonction pour démarrer l'application
start_app() {
    log_info "Démarrage de l'application Spring Boot..."
    mvn spring-boot:run > /tmp/springboot.log 2>&1 &
    APP_PID=$!
    echo $APP_PID > /tmp/springboot.pid
    
    log_info "Attente du démarrage de l'application (PID: $APP_PID)..."
    for i in {1..30}; do
        if check_app_running; then
            log_success "Application démarrée avec succès!"
            return 0
        fi
        echo -n "."
        sleep 2
    done
    
    log_error "L'application n'a pas démarré après 60 secondes"
    return 1
}

# Fonction pour arrêter l'application
stop_app() {
    if [ -f /tmp/springboot.pid ]; then
        APP_PID=$(cat /tmp/springboot.pid)
        if ps -p $APP_PID > /dev/null 2>&1; then
            log_info "Arrêt de l'application (PID: $APP_PID)..."
            kill $APP_PID
            rm /tmp/springboot.pid
            log_success "Application arrêtée"
        fi
    fi
}

# Fonction pour exécuter les tests
run_tests() {
    local tags="$1"
    
    if [ -z "$tags" ]; then
        log_info "Exécution de tous les tests..."
        mvn test -Dtest=CucumberTestRunner
    else
        log_info "Exécution des tests avec tags: $tags"
        mvn test -Dtest=CucumberTestRunner -Dcucumber.filter.tags="$tags"
    fi
}

# Fonction pour afficher les rapports
show_reports() {
    log_info "Rapports générés:"
    echo ""
    echo "  📊 Rapport HTML:   target/cucumber-reports/cucumber.html"
    echo "  📊 Rapport JSON:   target/cucumber-reports/cucumber.json"
    echo "  📊 Rapport XML:    target/cucumber-reports/cucumber.xml"
    echo "  📸 Snapshots:      test-snapshots/"
    echo ""
    
    if [ -f target/cucumber-reports/cucumber.html ]; then
        read -p "Voulez-vous ouvrir le rapport HTML? (y/n) " -n 1 -r
        echo
        if [[ $REPLY =~ ^[Yy]$ ]]; then
            xdg-open target/cucumber-reports/cucumber.html 2>/dev/null || \
            open target/cucumber-reports/cucumber.html 2>/dev/null || \
            log_warning "Impossible d'ouvrir le navigateur automatiquement"
        fi
    fi
}

# Fonction pour nettoyer les anciens rapports
clean_reports() {
    log_info "Nettoyage des anciens rapports..."
    rm -rf target/cucumber-reports/* 2>/dev/null
    rm -rf target/surefire-reports/* 2>/dev/null
    rm -rf test-snapshots/*.html 2>/dev/null
    log_success "Rapports nettoyés"
}

# Fonction pour afficher l'aide
show_help() {
    cat << EOF
Usage: ./run-functional-tests.sh [OPTIONS]

Options:
  -h, --help              Afficher cette aide
  -c, --clean             Nettoyer les anciens rapports avant l'exécution
  -s, --start-app         Démarrer l'application automatiquement
  -t, --tags TAGS         Exécuter uniquement les tests avec les tags spécifiés
  -r, --report            Afficher les rapports après l'exécution
  --smoke                 Exécuter uniquement les tests @smoke
  --regression            Exécuter uniquement les tests @regression
  --ui                    Exécuter uniquement les tests @ui
  --no-slow               Exclure les tests @slow

Exemples:
  ./run-functional-tests.sh                           # Exécuter tous les tests
  ./run-functional-tests.sh --clean --start-app       # Nettoyer, démarrer l'app, tester
  ./run-functional-tests.sh --smoke                   # Tests smoke uniquement
  ./run-functional-tests.sh -t "@smoke or @regression" # Tests smoke ou regression
  ./run-functional-tests.sh --no-slow                 # Exclure les tests lents

Tags disponibles:
  @smoke       - Tests de vérification rapide
  @regression  - Tests de régression
  @ui          - Tests d'interface utilisateur
  @slow        - Tests longs/lents

EOF
}

# Variables
START_APP=false
CLEAN=false
SHOW_REPORT=false
TAGS=""

# Traitement des arguments
while [[ $# -gt 0 ]]; do
    case $1 in
        -h|--help)
            show_help
            exit 0
            ;;
        -c|--clean)
            CLEAN=true
            shift
            ;;
        -s|--start-app)
            START_APP=true
            shift
            ;;
        -t|--tags)
            TAGS="$2"
            shift 2
            ;;
        -r|--report)
            SHOW_REPORT=true
            shift
            ;;
        --smoke)
            TAGS="@smoke"
            shift
            ;;
        --regression)
            TAGS="@regression"
            shift
            ;;
        --ui)
            TAGS="@ui"
            shift
            ;;
        --no-slow)
            TAGS="not @slow"
            shift
            ;;
        *)
            log_error "Option inconnue: $1"
            show_help
            exit 1
            ;;
    esac
done

# Bannière
echo ""
echo "╔═══════════════════════════════════════════════════════╗"
echo "║      Tests Fonctionnels UML Enhancing                 ║"
echo "║      Cucumber + Selenium + HtmlUnit                   ║"
echo "╚═══════════════════════════════════════════════════════╝"
echo ""

# Nettoyage si demandé
if [ "$CLEAN" = true ]; then
    clean_reports
fi

# Vérifier si l'application est déjà en cours d'exécution
if check_app_running; then
    log_success "L'application est déjà en cours d'exécution"
else
    log_warning "L'application n'est pas en cours d'exécution"
    
    if [ "$START_APP" = true ]; then
        if ! start_app; then
            log_error "Impossible de démarrer l'application"
            exit 1
        fi
        STOP_APP_AT_END=true
    else
        log_warning "Les tests nécessitent l'application démarrée sur http://localhost:8080"
        log_info "Démarrez l'application avec: mvn spring-boot:run"
        log_info "Ou utilisez l'option --start-app pour la démarrer automatiquement"
        echo ""
        read -p "Continuer quand même? (y/n) " -n 1 -r
        echo
        if [[ ! $REPLY =~ ^[Yy]$ ]]; then
            log_info "Exécution annulée"
            exit 0
        fi
    fi
fi

# Exécuter les tests
log_info "╔═══════════════════════════════════════╗"
log_info "║  Exécution des tests en cours...      ║"
log_info "╚═══════════════════════════════════════╝"
echo ""

if run_tests "$TAGS"; then
    TEST_STATUS=0
    log_success "Tests terminés avec succès!"
else
    TEST_STATUS=1
    log_warning "Certains tests ont échoué"
fi

# Arrêter l'application si elle a été démarrée par le script
if [ "$STOP_APP_AT_END" = true ]; then
    stop_app
fi

# Afficher les statistiques
echo ""
log_info "═══════════════════════════════════════"
log_info "Statistiques:"
if [ -f target/cucumber-reports/cucumber.json ]; then
    python3 -c "
import json, sys
try:
    with open('target/cucumber-reports/cucumber.json') as f:
        data = json.load(f)
    passed = failed = 0
    for feature in data:
        for scenario in feature.get('elements', []):
            steps_passed = all(s.get('result', {}).get('status') == 'passed' for s in scenario.get('steps', []))
            if steps_passed:
                passed += 1
            else:
                failed += 1
    total = passed + failed
    print(f'  ✓ Tests réussis: {passed}/{total}')
    print(f'  ✗ Tests échoués: {failed}/{total}')
    if total > 0:
        percentage = (passed / total) * 100
        print(f'  📊 Taux de réussite: {percentage:.1f}%')
except:
    pass
" 2>/dev/null || log_info "  Impossible de lire les statistiques"
fi

# Compter les snapshots
SNAPSHOT_COUNT=$(ls -1 test-snapshots/*.html 2>/dev/null | wc -l)
log_info "  📸 Snapshots générés: $SNAPSHOT_COUNT"
log_info "═══════════════════════════════════════"
echo ""

# Afficher les rapports si demandé
if [ "$SHOW_REPORT" = true ]; then
    show_reports
fi

# Message final
echo ""
if [ $TEST_STATUS -eq 0 ]; then
    log_success "╔═══════════════════════════════════════╗"
    log_success "║  Tous les tests ont réussi! 🎉        ║"
    log_success "╚═══════════════════════════════════════╝"
else
    log_warning "╔═══════════════════════════════════════╗"
    log_warning "║  Certains tests ont échoué            ║"
    log_warning "║  Consultez les rapports pour details  ║"
    log_warning "╚═══════════════════════════════════════╝"
fi
echo ""

exit $TEST_STATUS
