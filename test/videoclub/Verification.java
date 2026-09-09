package videoclub;

import java.util.ArrayList;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Verification {

    private static int nombreTests = 0;

    private static void verifier(boolean resultat, String message) throws Exception {
        if (!resultat) {
            throw new Exception("ÉCHEC : " + message);
        }
        nombreTests++;
        System.out.println("OK : " + message);
    }

    public static void main(String[] args) throws Exception {
        Cinema cinema = new Cinema();
        Film film = new Film("Le Voyage", 110, "Aventure");
        Salle salle = new Salle(1, 2, 3);
        Seance seance = new Seance(film, salle, "12/09/2026", "18:00", 9.50);
        Seance autreSeance = new Seance(film, salle, "12/09/2026", "21:00", 9.50);
        cinema.ajouterFilm(film);
        cinema.ajouterSalle(salle);
        cinema.ajouterSeance(seance);
        cinema.ajouterSeance(autreSeance);
        verifier(salle.getCapacite() == 6 && salle.getPlace(2, 3).getCode().equals("B3"), "plan de salle");
        verifier(cinema.rechercherFilm("le voyage") == film && cinema.rechercherFilm("Inconnu") == null, "recherche d'un film");
        verifier(cinema.rechercherSalle(1) == salle && cinema.rechercherSalle(99) == null, "recherche d'une salle");
        verifier(seance.calculerRecette() == 0 && seance.calculerTauxRemplissage() == 0, "séance vide");

        ArrayList<Place> choix = new ArrayList<Place>();
        choix.add(salle.getPlace(1, 1));
        choix.add(salle.getPlace(1, 2));
        Reservation reservation = cinema.reserver(seance, "  Yanis  ", choix);
        verifier(reservation.getNumero().equals("R1") && reservation.getNomClient().equals("Yanis"), "première réservation");
        verifier(seance.getNombrePlacesReservees() == 2 && seance.getNombrePlacesLibres() == 4, "compte des places");
        verifier(seance.calculerRecette() == 19.0, "recette de deux billets");
        verifier(Math.abs(seance.calculerTauxRemplissage() - 100.0 / 3) < 0.000001, "pourcentage avec décimales");

        // Une sélection partiellement occupée doit être refusée en entier.
        choix.clear();
        choix.add(salle.getPlace(2, 1));
        choix.add(salle.getPlace(1, 1));
        boolean refusee = false;
        try {
            cinema.reserver(seance, "Camille", choix);
        } catch (PlaceOccupeeException e) {
            refusee = true;
        }
        verifier(refusee && !seance.estPlaceOccupee(salle.getPlace(2, 1))
                && seance.getNombrePlacesReservees() == 2, "refus sans réservation partielle");
        choix.clear();
        choix.add(salle.getPlace(1, 1));
        Reservation deuxieme = cinema.reserver(autreSeance, "Camille", choix);
        verifier(deuxieme.getNumero().equals("R2"), "numéro global non consommé par un refus");
        verifier(autreSeance.estPlaceOccupee(salle.getPlace(1, 1)), "même place à une autre séance");
        verifier(cinema.rechercherReservation("R2") == deuxieme
                && cinema.rechercherSeanceReservation("R2") == autreSeance, "recherche dans plusieurs séances");
        verifier(cinema.annulerReservation("R1"), "annulation existante");
        verifier(!seance.estPlaceOccupee(salle.getPlace(1, 1)) && seance.calculerRecette() == 0,
                "places et recette mises à jour après annulation");
        verifier(autreSeance.estPlaceOccupee(salle.getPlace(1, 1)), "autre séance inchangée après annulation");
        verifier(!cinema.annulerReservation("R1") && cinema.rechercherReservation("R1") == null, "double annulation et recherche absente");

        choix.add(salle.getPlace(1, 1));
        refuserSelection(cinema, seance, "Yanis", choix, "place sélectionnée deux fois");
        choix.clear();
        refuserSelection(cinema, seance, "Yanis", choix, "sélection vide");
        refuserSelection(cinema, seance, "Yanis", null, "sélection null");
        choix.add(salle.getPlace(1, 1));
        refuserSelection(cinema, seance, "   ", choix, "nom vide");
        refuserSelection(cinema, seance, null, choix, "nom null");
        Seance inconnue = new Seance(film, salle, "13/09/2026", "18:00", 9.50);
        refuserSelection(cinema, inconnue, "Yanis", choix, "séance non enregistrée");
        refuserSelection(cinema, null, "Yanis", choix, "séance null");
        choix.clear();
        choix.add(new Salle(2, 2, 3).getPlace(1, 1));
        refuserSelection(cinema, seance, "Yanis", choix, "siège A1 d'une autre salle");
        choix.clear();
        choix.add(new Place(1, 1));
        refuserSelection(cinema, seance, "Yanis", choix, "place fabriquée hors du tableau");
        choix.clear();
        choix.add(null);
        refuserSelection(cinema, seance, "Yanis", choix, "place null");
        verifier(seance.getNombrePlacesReservees() == 0, "aucun ajout après les saisies refusées");

        choix.clear();
        for (int ligne = 1; ligne <= 2; ligne++) {
            for (int numero = 1; numero <= 3; numero++) {
                choix.add(salle.getPlace(ligne, numero));
            }
        }
        Reservation complete = cinema.reserver(seance, "Alex", choix);
        verifier(complete.getNumero().equals("R3"), "numéro non réutilisé après annulation");
        verifier(seance.getNombrePlacesLibres() == 0 && seance.calculerTauxRemplissage() == 100
                && seance.calculerRecette() == 57.0, "salle complète");
        refusee = false;
        try {
            cinema.reserver(seance, "Sam", choix);
        } catch (PlaceOccupeeException e) {
            refusee = true;
        }
        verifier(refusee && seance.getNombrePlacesReservees() == 6, "pas de surréservation");
        choix.clear();
        complete.getPlaces().clear();
        seance.getReservations().clear();
        cinema.getSeances().clear();
        cinema.getFilms().clear();
        cinema.getSalles().clear();
        verifier(seance.getNombrePlacesReservees() == 6 && cinema.getSeances().size() == 2
                && cinema.getFilms().size() == 1 && cinema.getSalles().size() == 1,
                "les listes externes ne modifient pas le modèle");
        refusee = false;
        try {
            salle.getPlace(0, 1);
        } catch (IllegalArgumentException e) {
            refusee = true;
        }
        verifier(refusee, "coordonnées invalides refusées");
        refusee = false;
        try {
            new Salle(3, 0, 3);
        } catch (IllegalArgumentException e) {
            refusee = true;
        }
        verifier(refusee, "salle de capacité nulle refusée");
        refusee = false;
        try {
            new Seance(film, salle, "12/09/2026", "18:00", -1);
        } catch (IllegalArgumentException e) {
            refusee = true;
        }
        verifier(refusee, "tarif négatif refusé");
        refusee = false;
        try {
            cinema.ajouterSalle(new Salle(1, 1, 1));
        } catch (IllegalArgumentException e) {
            refusee = true;
        }
        verifier(refusee, "numéro de salle déjà utilisé refusé");

        // Test technique du rechargement, avant l'ajout de la sauvegarde dans l'application.
        File fichier = File.createTempFile("videoclub-test-", ".dat");
        fichier.deleteOnExit();
        ObjectOutputStream sortie = new ObjectOutputStream(new FileOutputStream(fichier));
        sortie.writeObject(cinema);
        sortie.close();
        ObjectInputStream entree = new ObjectInputStream(new FileInputStream(fichier));
        Cinema copie = (Cinema) entree.readObject();
        entree.close();
        Seance seanceChargee = copie.rechercherSeanceReservation("R2");
        Salle salleChargee = copie.rechercherSalle(1);
        verifier(seanceChargee.estPlaceOccupee(salleChargee.getPlace(1, 1)), "occupation après rechargement");
        verifier(copie.rechercherSeanceReservation("R3").getSalle() == salleChargee, "salle partagée après rechargement");
        choix.add(salleChargee.getPlace(2, 2));
        Reservation suivante = copie.reserver(seanceChargee, "Sam", choix);
        verifier(suivante.getNumero().equals("R4"), "compteur conservé après rechargement");
        verifier(seanceChargee.calculerRecette() == 19.0, "réservation après rechargement");
        System.out.println(nombreTests + " vérifications réussies.");
    }

    private static void refuserSelection(Cinema cinema, Seance seance, String nom,
            ArrayList<Place> places, String message) throws Exception {
        boolean refusee = false;
        try {
            cinema.reserver(seance, nom, places);
        } catch (IllegalArgumentException e) {
            refusee = true;
        }
        verifier(refusee, message);
    }
}
