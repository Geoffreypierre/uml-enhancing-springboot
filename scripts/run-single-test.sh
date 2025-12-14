#!/bin/bash

# Script pour exécuter un scénario spécifique de test fonctionnel

if [ -z "$1" ]; then
    echo "Usage: $0 <nom-du-scénario>"
    echo ""
    echo "Exemples:"
    echo "  $0 'Affichage de la page d accueil'"
    echo "  $0 'Upload et traitement d un fichier PlantUML simple'"
    echo ""
    echo "Pour voir tous les scénarios disponibles:"
    echo "  grep 'Scénario:' src/test/resources/features/*.feature"
    exit 1
fi

SCENARIO_NAME="$1"

echo "Exécution du scénario: $SCENARIO_NAME"
echo ""

mvn test -Dtest=CucumberTestRunner -Dcucumber.filter.name="$SCENARIO_NAME"
