# VideoClub - phase 3 proche du cours

VideoClub permet de préparer le programme d'un petit cinéma et de réserver des places dans une fenêtre Swing. Il démarre avec un cinéma vide lorsqu'aucune sauvegarde n'existe.

## Ouvrir dans NetBeans

1. Décompresser le ZIP dans un nouveau dossier pour conserver la phase 2.
2. Ouvrir le dossier `VideoClub`, celui qui contient `build.xml` et `nbproject`.
3. Choisir un JDK 17 ou supérieur pour le projet. La cible de compilation est Java 17 et les sources sont en UTF-8.
4. Faire **Clean and Build**, puis **Run Project**. La classe principale est `videoclub.VideoClub`.

NetBeans peut lui-même demander un JDK plus récent : il faut distinguer le JDK de l'IDE de la cible du projet. Aucune bibliothèque externe n'est nécessaire à l'application.

Les réglages privés de chaque poste ne sont pas inclus. Ne pas copier les chemins Windows d'un ordinateur vers celui du binôme.

## Lancer le JAR

Depuis le dossier `VideoClub`, dans un terminal :

```sh
java -jar dist/VideoClub.jar
```

Une fenêtre doit s'ouvrir. Ce n'est plus la démonstration console de la phase 2. Utiliser un environnement de bureau avec Java 17 ou supérieur.

## Premier essai

Dans **Programme** :

1. Ajouter le film `Le Voyage`, genre `Aventure`, durée `110` minutes.
2. Ajouter la salle `1`, avec `4` rangées et `6` sièges par rangée.
3. Vérifier le film et la salle sélectionnés dans les listes.
4. Programmer le `12/09/2026` à `18:00`, au tarif de `9,50` euros.
5. Programmer aussi une séance à `21:00`, dans la même salle.

Dans **Réservations** :

1. Choisir la séance de 18 h, cliquer sur A1 et A2, saisir un nom et réserver.
2. Vérifier le numéro R1, la recette de 19,00 euros et les deux boutons grisés.
3. Passer à 21 h : A1 et A2 doivent être libres.
4. Rechercher la réservation avec `R1` ou le nom. La recherche porte sur toutes les séances.
5. Le bouton **Annuler cette réservation** annule immédiatement le résultat sélectionné, dont les détails sont affichés à droite.

Une place sélectionnée porte une étoile et une place réservée est désactivée. Un nouveau clic sur une place sélectionnée la retire de la sélection. Changer de séance ou revenir à l'écran Réservations remet la sélection à zéro.

## Sauvegarder et retrouver les données

Cliquer sur **Sauvegarder**. Films, salles, séances, réservations et compteur des numéros sont écrits ensemble dans `videoclub.dat`.

Le fichier est placé dans le dossier de travail du programme. Avec la commande ci-dessus, c'est le dossier `VideoClub`. Le chemin exact apparaît en bas de la fenêtre après la sauvegarde. Si tu lances le JAR d'un autre dossier, ce dossier devient l'emplacement de sa sauvegarde. Pour partager les données, transmettre aussi le fichier `videoclub.dat` et le placer dans le dossier de travail de l'autre poste.

Au lancement suivant depuis ce même dossier, la sauvegarde est chargée automatiquement. Le bouton **Recharger** relit la dernière sauvegarde ; il demande confirmation si des modifications validées n'ont pas encore été sauvegardées. Une lecture échouée conserve le cinéma actuellement ouvert.

À la fermeture, une question propose de sauvegarder les modifications. Annuler garde la fenêtre ouverte. Si la sauvegarde demandée échoue, la fenêtre reste ouverte aussi.

Les champs simplement remplis et les places sélectionnées ne sont pas des données enregistrées : cliquer sur les boutons d'ajout ou de réservation avant de sauvegarder. La sauvegarde manuelle remplace le fichier existant. Ne pas lancer deux instances depuis le même dossier : leur sauvegarde serait commune.

## Règles retenues

- La date et l'heure sont enregistrées comme du texte. Le programme ne contrôle pas le calendrier et ne détecte pas les chevauchements de séances.
- Le tarif est unique pour chaque séance ; point ou virgule sont acceptés. Les montants affichés sont arrondis au centime, mais un zéro final peut ne pas apparaître.
- Une séance avec des réservations ne peut pas être retirée. Un film encore programmé ne peut pas être retiré.
- Les réservations annulées disparaissent. Leur numéro n'est pas réutilisé.

## Vérifications fournies

Deux programmes de vérification sont dans `test/videoclub`. Dans NetBeans, ouvrir le fichier voulu puis utiliser **Run File**. Ils utilisent chacun un `main`, pas JUnit.

Pour les lancer depuis le terminal, après compilation du projet :

```sh
javac -encoding UTF-8 --release 17 -cp dist/VideoClub.jar -d build/verification test/videoclub/Verification.java test/videoclub/VerificationPhase3.java
```

Sous Windows :

```sh
java -cp "dist/VideoClub.jar;build/verification" videoclub.Verification
java -cp "dist/VideoClub.jar;build/verification" videoclub.VerificationPhase3
```

Sous macOS ou Linux, remplacer le `;` entre les deux chemins par `:`.

Résultats attendus : **39 vérifications réussies**, puis **15 vérifications phase 3 réussies**, soit 54 contrôles. Ces deux programmes vérifient le modèle et la sauvegarde en console. Les boutons et les dialogues se vérifient manuellement avec le parcours décrit dans le Markdown d'audit.

## Contenu et état de validation

- `src/videoclub` : 12 fichiers Java de l'application.
- `test/videoclub` : 2 fichiers Java de vérification.
- `dist/VideoClub.jar` : application compilée pour Java 17.
- `controle` : résultats des 54 vérifications en console sous Linux.
- `CONTEXTE_AUDIT_PHASE_3.md` : périmètre, choix et grille de relecture.

Cette livraison a été compilée avec `--release 17` sous OpenJDK 17, puis testée en utilisant les classes contenues dans le JAR. NetBeans, Ant et l'affichage d'une vraie JFrame ne sont pas disponibles dans cet environnement : **Clean and Build, lancement complet du JAR et dialogues restent à vérifier sur vos postes**.

Les fichiers Ant NetBeans sont conservés. Avec un JDK plus récent, un avertissement sur `-source 17` peut encore apparaître. Ne pas ajouter simplement `--release 17` à `javac.compilerargs` : il ne faut pas le combiner avec les options source et target déjà utilisées par cette configuration Ant.

Cette version ne propose pas de modification des films ou salles existants, de suppression de salle, de réductions, de paiement ni d'historique des annulations. Les sauvegardes d'anciennes phases ou de versions futures ne sont pas garanties compatibles.

La sauvegarde suit l'exemple simple du cours et écrit directement dans `videoclub.dat`. Une erreur pendant l'écriture peut donc abîmer le fichier précédent : il faut conserver une copie avant les essais importants.

La construction des formulaires et les actions de réservation sont découpées en méthodes courtes. Les quelques méthodes Swing complémentaires sont expliquées dans `CONTEXTE_AUDIT_PHASE_3.md`.

Le rapport demandé par le professeur n'est pas encore inclus. Cette archive sert à la validation de la phase 3, pas encore au dépôt final.
