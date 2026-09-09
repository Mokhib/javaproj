package videoclub;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class VerificationPhase3 {

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
        Film filmLibre = new Film("Le Retour", 95, "Comédie");
        Salle salle = new Salle(1, 2, 3);
        cinema.ajouterFilm(film);
        cinema.ajouterFilm(filmLibre);
        cinema.ajouterSalle(salle);

        Seance seance = new Seance(film, salle, "12/09/2026", "18:00", 9.50);
        Seance autreSeance = new Seance(film, salle, "12/09/2026", "21:00", 9.50);
        cinema.ajouterSeance(seance);
        cinema.ajouterSeance(autreSeance);
        verifier(cinema.getSeances().size() == 2, "ajout de deux séances");

        cinema.supprimerFilm(filmLibre);
        verifier(cinema.rechercherFilm("Le Retour") == null, "retrait d'un film non programmé");

        boolean refuse = false;
        try {
            cinema.supprimerFilm(film);
        } catch (IllegalArgumentException e) {
            refuse = true;
        }
        verifier(refuse, "film programmé protégé");

        ArrayList<Place> places = new ArrayList<Place>();
        places.add(salle.getPlace(1, 1));
        places.add(salle.getPlace(1, 2));
        Reservation reservation = cinema.reserver(seance, "Émilie", places);
        verifier(reservation.getNumero().equals("R1"), "réservation ajoutée");
        verifier(cinema.rechercherReservations("émilie").size() == 1,
                "recherche par nom sans tenir compte de la casse");
        verifier(cinema.rechercherReservations("r1").size() == 1, "recherche par numéro");
        verifier(cinema.rechercherReservations("").size() == 1, "affichage de toutes les réservations");

        refuse = false;
        try {
            cinema.supprimerSeance(seance);
        } catch (IllegalArgumentException e) {
            refuse = true;
        }
        verifier(refuse, "séance réservée protégée");

        verifier(cinema.annulerReservation("R1"), "annulation de la réservation");
        cinema.supprimerSeance(seance);
        verifier(cinema.getSeances().size() == 1, "retrait de la séance après annulation");

        File fichier = File.createTempFile("videoclub-", ".dat");
        fichier.deleteOnExit();
        places.clear();
        places.add(salle.getPlace(2, 2));
        cinema.reserver(autreSeance, "Yanis", places);
        Sauvegarde.enregistrer(cinema, fichier);
        Cinema copie = Sauvegarde.charger(fichier);
        verifier(copie.getFilms().size() == 1 && copie.getSeances().size() == 1,
                "films et séances rechargés");
        verifier(copie.rechercherReservation("R2").getNomClient().equals("Yanis"),
                "réservation rechargée");
        verifier(copie.getSeances().get(0).estPlaceOccupee(copie.getSalles().get(0).getPlace(2, 2)),
                "place occupée après rechargement");

        places.clear();
        places.add(copie.getSalles().get(0).getPlace(2, 3));
        Reservation suivante = copie.reserver(copie.getSeances().get(0), "Sam", places);
        verifier(suivante.getNumero().equals("R3"), "compteur conservé après rechargement");

        ObjectOutputStream sortie = new ObjectOutputStream(new FileOutputStream(fichier));
        sortie.writeObject("un texte");
        sortie.close();
        refuse = false;
        try {
            Sauvegarde.charger(fichier);
        } catch (IOException e) {
            refuse = true;
        }
        verifier(refuse, "fichier d'un autre type refusé");

        System.out.println(nombreTests + " vérifications phase 3 réussies.");
    }
}
