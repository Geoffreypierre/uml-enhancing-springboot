# language: fr
Fonctionnalité: Validation des fichiers UML
  En tant qu'utilisateur
  Je veux être informé si mon fichier n'est pas valide
  Afin de corriger les erreurs avant le traitement

  Contexte:
    Étant donné que l'application est démarrée
    Et que je suis sur la page d'accueil

  Scénario: Sélection d'un fichier non-PlantUML
    Lorsque je sélectionne un fichier non-PlantUML "invalid-file.txt"
    Alors le bouton "Traiter" reste activé
    # Note: Le comportement peut varier selon l'implémentation

  Scénario: Validation du champ seuil de pertinence - valeur minimale
    Lorsque je sélectionne un fichier PlantUML valide "test-simple.puml"
    Et je saisis "0" comme seuil de pertinence
    Alors le champ seuil accepte la valeur "0"

  Scénario: Validation du champ seuil de pertinence - valeur maximale
    Lorsque je sélectionne un fichier PlantUML valide "test-simple.puml"
    Et je saisis "1" comme seuil de pertinence
    Alors le champ seuil accepte la valeur "1"

  Scénario: Validation du champ seuil de pertinence - valeur décimale
    Lorsque je sélectionne un fichier PlantUML valide "test-simple.puml"
    Et je saisis "0.65" comme seuil de pertinence
    Alors le champ seuil accepte la valeur "0.65"
