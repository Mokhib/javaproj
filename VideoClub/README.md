# VideoClub

Application de bureau pour gérer un petit cinéma : on y compose le programme (films, salles, séances) et on réserve des places directement sur un plan de salle. Le tout tient dans une fenêtre Swing, sans base de données ni bibliothèque externe.

[![Architecture diagram of mokhib/javaproj](https://gitdiagram.com/mokhib/javaproj/diagram.png)](https://gitdiagram.com/mokhib/javaproj?utm_source=readme&utm_medium=picture)

## Ce que fait l'application

**Programme**
- ajouter des films (titre, genre, durée) et des salles (nombre de rangées et de sièges par rangée) ;
- programmer des séances avec une date, une heure et un tarif ;
- retirer une séance ou un film, tant qu'aucune réservation ou séance n'en dépend.

**Réservations**
- choisir une séance et cliquer sur les places voulues : une étoile marque la sélection, une place déjà prise est grisée ;
- réserver au nom d'un client, ce qui génère un numéro (`R1`, `R2`…) et met à jour la recette de la séance ;
- retrouver une réservation par son numéro ou par le nom du client, sur l'ensemble des séances, puis l'annuler.

**Sauvegarde**
- tout (films, salles, séances, réservations, compteur de numéros) est écrit dans un seul fichier, `videoclub.dat` ;
- la sauvegarde est rechargée automatiquement au lancement suivant ;
- à la fermeture, l'application propose d'enregistrer s'il reste des modifications, et demande confirmation avant un rechargement qui écraserait un travail non sauvegardé.

## Lancer le projet

Il faut un JDK 17 ou supérieur.

**Avec NetBeans**
1. Ouvrir le dossier du projet (celui qui contient `build.xml` et `nbproject`).
2. Faire *Clean and Build*, puis *Run Project*. La classe principale est `videoclub.VideoClub`.

**En ligne de commande**

```sh
javac -encoding UTF-8 -d build src/videoclub/*.java
java -cp build videoclub.VideoClub
```

Si vous avez le JAR généré par NetBeans : `java -jar dist/VideoClub.jar`.

`videoclub.dat` est créé dans le dossier depuis lequel on lance le programme. Lancer l'application depuis un autre dossier revient donc à repartir d'un cinéma vide.

## Un premier parcours

1. Dans **Programme**, ajouter le film *Le Voyage* (Aventure, 110 min) et la salle 1 (4 rangées, 6 sièges).
2. Programmer le 12/09/2026 à 18:00 au tarif de 9,50 €, puis une seconde séance à 21:00.
3. Dans **Réservations**, choisir la séance de 18 h, cliquer sur A1 et A2, saisir un nom et valider : la réservation `R1` apparaît avec une recette de 19,00 €.
4. Passer à la séance de 21 h : A1 et A2 y sont toujours libres, l'occupation se gère séance par séance.

## Organisation du code

Le code est séparé en un modèle métier, une couche de sauvegarde et une interface.

| Classe | Rôle |
|---|---|
| `Film`, `Salle`, `Place` | Les données de base. Une salle crée elle-même sa grille de places. |
| `Seance` | Un film dans une salle à une date et une heure, avec son tarif et ses réservations. Calcule places libres, recette et taux de remplissage. |
| `Reservation` | Un numéro, un client et la liste de ses places. |
| `Cinema` | Point d'entrée du modèle : il détient films, salles et séances, attribue les numéros de réservation et gère les recherches. |
| `PlaceOccupeeException` | Levée quand une place demandée est déjà prise. |
| `Sauvegarde` | Lecture et écriture du cinéma par sérialisation Java. |
| `FenetreCinema`, `PanneauProgramme`, `PanneauReservations` | L'interface Swing : une fenêtre et deux panneaux. |
| `VideoClub` | Le `main`. |

Quelques choix que j'ai faits en le construisant :

- **Les objets se valident eux-mêmes.** Les constructeurs refusent les valeurs absurdes (salle de 0 rangée, tarif négatif, titre vide) avec des messages clairs, ce qui évite de disperser les contrôles dans l'interface.
- **Une réservation est tout ou rien.** Toute la sélection est vérifiée avant qu'une réservation soit créée : si une seule place pose problème, rien n'est enregistré.
- **Un refus ne consomme pas de numéro.** Le compteur n'avance qu'une fois la réservation réussie, et un numéro annulé n'est jamais réattribué.
- **Les getters renvoient des copies** des listes, pour que le modèle ne soit modifié que par ses propres méthodes.
- **Les écrans sont des `JPanel` distincts**, recréés à partir du même `Cinema` lors d'un rechargement.

## Limites connues

Ce projet reste volontairement simple, et certaines choses ne sont pas gérées :

- la date et l'heure sont stockées comme du texte : le calendrier n'est pas contrôlé et deux séances qui se chevauchent dans la même salle ne sont pas détectées ;
- on ne peut pas modifier un film ou une salle une fois créés, ni supprimer une salle ;
- pas de tarifs réduits, de paiement ni d'historique des annulations ;
- la sauvegarde écrit directement dans `videoclub.dat` : une erreur pendant l'écriture peut abîmer l'ancien fichier, mieux vaut en garder une copie ;
- deux instances lancées depuis le même dossier partagent le même fichier de sauvegarde.

## Technologies

Java 17, Swing, sérialisation Java, projet NetBeans (Ant). Aucune dépendance externe.
