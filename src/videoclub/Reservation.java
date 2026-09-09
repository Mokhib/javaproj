package videoclub;

import java.io.Serializable;
import java.util.ArrayList;

public class Reservation implements Serializable {

    private String numero;
    private String nomClient;
    private ArrayList<Place> places;

    Reservation(String numero, String nomClient, ArrayList<Place> places) {
        this.numero = numero;
        this.nomClient = nomClient;
        this.places = new ArrayList<Place>(places);
    }

    public String getNumero() {
        return numero;
    }

    public String getNomClient() {
        return nomClient;
    }

    public ArrayList<Place> getPlaces() {
        return new ArrayList<Place>(places);
    }

    public boolean contientPlace(Place place) {
        for (Place placeReservee : places) {
            if (placeReservee.getLigne() == place.getLigne()
                    && placeReservee.getNumero() == place.getNumero()) {
                return true;
            }
        }

        return false;
    }

    public int getNombrePlaces() {
        return places.size();
    }

    @Override
    public String toString() {
        return numero + " - " + nomClient + " - " + places.size() + " place(s)";
    }
}
