# language: fr
Fonctionnalité: Tests de performance et stabilité
  En tant que testeur
  Je veux vérifier la performance et la stabilité de l'application
  Afin de garantir une bonne expérience utilisateur

  @smoke
  Scénario: Test de smoke - Vérification rapide
    Étant donné que l'application est démarrée
    Et que je suis sur la page d'accueil
    Lorsque je sélectionne un fichier PlantUML valide "test-simple.puml"
    Et je saisis "0.75" comme seuil de pertinence
    Et je clique sur le bouton "Traiter"
    Alors je vois l'image du diagramme UML amélioré

  @regression
  Scénario: Test de régression - Upload multiple
    Étant donné que l'application est démarrée
    Et que je suis sur la page d'accueil
    Lorsque je sélectionne un fichier PlantUML valide "test-simple.puml"
    Et je saisis "0.75" comme seuil de pertinence
    Et je clique sur le bouton "Traiter"
    Et j'attends que le traitement soit terminé
    Alors je vois l'image du diagramme UML amélioré
    # Note: Dans un scénario réel, on rafraîchirait la page et on recommencerait

  @slow
  Scénario: Test de performance - Fichier complexe
    Étant donné que l'application est démarrée
    Et que je suis sur la page d'accueil
    Lorsque je sélectionne un fichier PlantUML valide "test-complex.puml"
    Et je saisis "0.75" comme seuil de pertinence
    Et je clique sur le bouton "Traiter"
    Alors je vois l'image du diagramme UML amélioré
    Et le bouton de téléchargement est affiché

  @ui
  Scénario: Test d'interface - Responsive design
    Étant donné que l'application est démarrée
    Et que je suis sur la page d'accueil
    Alors je vois le titre "Uml enhancing"
    Et je vois le bouton de sélection de fichier
    Et je vois le champ de saisie du seuil de pertinence
    # Note: Dans un test réel, on pourrait redimensionner la fenêtre
    # et vérifier l'affichage responsive
