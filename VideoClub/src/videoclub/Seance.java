package videoclub;

import java.io.Serializable;
import java.util.ArrayList;

public class Seance implements Serializable {

    private Film film;
    private Salle salle;
    private String date;
    private String heure;
    private double tarif;
    private ArrayList<Reservation> reservations;

    public Seance(Film film, Salle salle, String date, String heure, double tarif) {
        if (film == null || salle == null || date == null || date.trim().isEmpty()
                || heure == null || heure.trim().isEmpty()) {
            throw new IllegalArgumentException("Il manque une information pour la séance.");
        }
        if (!(tarif >= 0 && tarif <= 1000)) {
            throw new IllegalArgumentException("Le tarif doit être compris entre 0 et 1000 euros.");
        }
        this.film = film;
        this.salle = salle;
        this.date = date.trim();
        this.heure = heure.trim();
        this.tarif = tarif;
        this.reservations = new ArrayList<Reservation>();
    }

    public Film getFilm() {
        return film;
    }

    public Salle getSalle() {
        return salle;
    }

    public String getDate() {
        return date;
    }

    public String getHeure() {
        return heure;
    }

    public double getTarif() {
        return tarif;
    }

    public ArrayList<Reservation> getReservations() {
        return new ArrayList<Reservation>(reservations);
    }

    // L'occupation d'une place est vérifiée uniquement pour cette séance.
    public boolean estPlaceOccupee(Place place) {
        if (!salle.contientPlace(place)) {
            throw new IllegalArgumentException("Choisis une place de cette salle.");
        }
        for (Reservation reservation : reservations) {
            if (reservation.contientPlace(place)) {
                return true;
            }
        }

        return false;
    }

    // Cinema fournit le numéro : l'interface passera par Cinema.reserver().
    Reservation reserver(String numero, String nomClient, ArrayList<Place> places)
            throws PlaceOccupeeException {
        if (numero == null || numero.trim().isEmpty() || rechercherReservation(numero) != null) {
            throw new IllegalArgumentException("Le numéro de réservation est invalide.");
        }
        if (nomClient == null || nomClient.trim().isEmpty()) {
            throw new IllegalArgumentException("Indique le nom du client.");
        }
        if (places == null || places.isEmpty()) {
            throw new IllegalArgumentException("Choisis au moins une place.");
        }

        // On vérifie toute la sélection avant de créer la réservation.
        for (int i = 0; i < places.size(); i++) {
            Place place = places.get(i);
            if (!salle.contientPlace(place)) {
                throw new IllegalArgumentException("Une place ne fait pas partie de cette salle.");
            }
            for (int j = 0; j < i; j++) {
                if (places.get(j) == place) {
                    throw new IllegalArgumentException("La place " + place + " est sélectionnée deux fois.");
                }
            }
            if (estPlaceOccupee(place)) {
                throw new PlaceOccupeeException("La place " + place + " est déjà réservée pour cette séance.");
            }
        }

        Reservation reservation = new Reservation(numero, nomClient.trim(), places);
        reservations.add(reservation);
        return reservation;
    }

    public Reservation rechercherReservation(String numero) {
        for (Reservation reservation : reservations) {
            if (reservation.getNumero().equals(numero)) {
                return reservation;
            }
        }
        return null;
    }

    public boolean annulerReservation(String numero) {
        for (int i = 0; i < reservations.size(); i++) {
            if (reservations.get(i).getNumero().equals(numero)) {
                reservations.remove(i);
                return true;
            }
        }
        return false;
    }

    public int getNombrePlacesReservees() {
        int total = 0;
        for (Reservation reservation : reservations) {
            total += reservation.getNombrePlaces();
        }
        return total;
    }

    public int getNombrePlacesLibres() {
        return salle.getCapacite() - getNombrePlacesReservees();
    }

    public double calculerRecette() {
        return getNombrePlacesReservees() * tarif;
    }

    public double calculerTauxRemplissage() {
        return 100.0 * getNombrePlacesReservees() / salle.getCapacite();
    }

    @Override
    public String toString() {
        return film.getTitre() + " - " + date + " à " + heure
                + " - salle " + salle.getNumero();
    }
}
