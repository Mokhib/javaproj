package videoclub;

import java.io.Serializable;
import java.util.ArrayList;

public class Cinema implements Serializable {

    private ArrayList<Film> films;
    private ArrayList<Salle> salles;
    private ArrayList<Seance> seances;
    private int prochainNumeroReservation;

    public Cinema() {
        films = new ArrayList<Film>();
        salles = new ArrayList<Salle>();
        seances = new ArrayList<Seance>();
        prochainNumeroReservation = 1;
    }

    public void ajouterFilm(Film film) {
        if (film == null || films.contains(film)) {
            throw new IllegalArgumentException("Ce film est absent ou déjà ajouté.");
        }
        films.add(film);
    }

    public void ajouterSalle(Salle salle) {
        if (salle == null || rechercherSalle(salle.getNumero()) != null) {
            throw new IllegalArgumentException("Cette salle est absente ou son numéro est déjà utilisé.");
        }
        salles.add(salle);
    }

    public void ajouterSeance(Seance seance) {
        if (seance == null || seances.contains(seance)) {
            throw new IllegalArgumentException("Cette séance est absente ou déjà ajoutée.");
        }
        if (!films.contains(seance.getFilm()) || !salles.contains(seance.getSalle())) {
            throw new IllegalArgumentException("Ajoute d'abord le film et la salle au cinéma.");
        }
        seances.add(seance);
    }

    public void supprimerFilm(Film film) {
        for (Seance seance : seances) {
            if (seance.getFilm() == film) {
                throw new IllegalArgumentException("Retire d'abord les séances de ce film.");
            }
        }
        if (!films.remove(film)) {
            throw new IllegalArgumentException("Choisis un film à retirer.");
        }
    }

    public void supprimerSeance(Seance seance) {
        if (!seances.contains(seance)) {
            throw new IllegalArgumentException("Choisis une séance à retirer.");
        }
        if (seance.getNombrePlacesReservees() > 0) {
            throw new IllegalArgumentException("Annule d'abord les réservations de cette séance.");
        }
        seances.remove(seance);
    }

    public ArrayList<Reservation> rechercherReservations(String recherche) {
        ArrayList<Reservation> resultat = new ArrayList<Reservation>();
        if (recherche == null) {
            return resultat;
        }
        String texte = recherche.trim();
        for (Seance seance : seances) {
            for (Reservation reservation : seance.getReservations()) {
                if (texte.isEmpty() || reservation.getNumero().equalsIgnoreCase(texte)
                        || reservation.getNomClient().toLowerCase().contains(texte.toLowerCase())) {
                    resultat.add(reservation);
                }
            }
        }
        return resultat;
    }

    public Reservation reserver(Seance seance, String nomClient, ArrayList<Place> places)
            throws PlaceOccupeeException {
        if (!seances.contains(seance)) {
            throw new IllegalArgumentException("Cette séance n'est pas dans le programme.");
        }
        String numero = "R" + prochainNumeroReservation;
        Reservation reservation = seance.reserver(numero, nomClient, places);
        // Une tentative refusée ne consomme pas de numéro.
        prochainNumeroReservation++;
        return reservation;
    }

    public ArrayList<Film> getFilms() {
        return new ArrayList<Film>(films);
    }

    public ArrayList<Salle> getSalles() {
        return new ArrayList<Salle>(salles);
    }

    public ArrayList<Seance> getSeances() {
        return new ArrayList<Seance>(seances);
    }

    public Film rechercherFilm(String titre) {
        if (titre == null) {
            return null;
        }
        for (Film film : films) {
            if (film.getTitre().equalsIgnoreCase(titre.trim())) {
                return film;
            }
        }
        return null;
    }

    public Salle rechercherSalle(int numero) {
        for (Salle salle : salles) {
            if (salle.getNumero() == numero) {
                return salle;
            }
        }
        return null;
    }

    public Reservation rechercherReservation(String numero) {
        Seance seance = rechercherSeanceReservation(numero);
        if (seance == null) {
            return null;
        }
        return seance.rechercherReservation(numero);
    }

    public Seance rechercherSeanceReservation(String numero) {
        for (Seance seance : seances) {
            if (seance.rechercherReservation(numero) != null) {
                return seance;
            }
        }
        return null;
    }

    public boolean annulerReservation(String numero) {
        Seance seance = rechercherSeanceReservation(numero);
        if (seance == null) {
            return false;
        }
        return seance.annulerReservation(numero);
    }
}
