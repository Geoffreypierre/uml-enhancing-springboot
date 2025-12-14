# language: fr
Fonctionnalité: Enhancement de diagrammes UML
  En tant qu'utilisateur
  Je veux pouvoir uploader un fichier PlantUML
  Afin d'obtenir un diagramme UML amélioré avec des abstractions

  Contexte:
    Étant donné que l'application est démarrée
    Et que je suis sur la page d'accueil

  Scénario: Affichage de la page d'accueil
    Alors je vois le titre "Uml enhancing"
    Et je vois le bouton de sélection de fichier
    Et je vois le champ de saisie du seuil de pertinence
    Et le bouton "Traiter" est désactivé

  Scénario: Sélection d'un fichier PlantUML
    Lorsque je sélectionne un fichier PlantUML valide "test-simple.puml"
    Alors le nom du fichier "test-simple.puml" est affiché
    Et le bouton "Traiter" est activé

  Scénario: Upload et traitement d'un fichier PlantUML simple
    Lorsque je sélectionne un fichier PlantUML valide "test-simple.puml"
    Et je saisis "0.75" comme seuil de pertinence
    Et je clique sur le bouton "Traiter"
    Alors je vois l'image du diagramme UML original
    Et je vois l'image du diagramme UML amélioré
    Et le bouton de téléchargement est affiché

  Scénario: Upload d'un fichier PlantUML complexe avec un seuil élevé
    Lorsque je sélectionne un fichier PlantUML valide "test-complex.puml"
    Et je saisis "0.9" comme seuil de pertinence
    Et je clique sur le bouton "Traiter"
    Alors je vois l'image du diagramme UML original
    Et je vois l'image du diagramme UML amélioré
    Et le bouton de téléchargement est affiché

  Scénario: Upload d'un fichier PlantUML avec un seuil faible
    Lorsque je sélectionne un fichier PlantUML valide "test-simple.puml"
    Et je saisis "0.3" comme seuil de pertinence
    Et je clique sur le bouton "Traiter"
    Alors je vois l'image du diagramme UML original
    Et je vois l'image du diagramme UML amélioré
    Et le bouton de téléchargement est affiché

  Scénario: Téléchargement du fichier UML amélioré
    Lorsque je sélectionne un fichier PlantUML valide "test-simple.puml"
    Et je saisis "0.75" comme seuil de pertinence
    Et je clique sur le bouton "Traiter"
    Et j'attends que le traitement soit terminé
    Et je clique sur le bouton de téléchargement
    Alors le fichier "enhanced-diagram.puml" est téléchargé

  Plan du scénario: Validation des différents seuils de pertinence
    Lorsque je sélectionne un fichier PlantUML valide "test-simple.puml"
    Et je saisis "<seuil>" comme seuil de pertinence
    Et je clique sur le bouton "Traiter"
    Alors je vois l'image du diagramme UML amélioré
    
    Exemples:
      | seuil |
      | 0.1   |
      | 0.5   |
      | 0.75  |
      | 0.9   |
      | 1.0   |

  Scénario: Tentative d'upload sans fichier sélectionné
    Lorsque je clique sur le bouton "Traiter"
    Alors le bouton "Traiter" reste désactivé
    Et aucune image n'est affichée
