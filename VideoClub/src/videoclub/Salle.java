package videoclub;

import java.io.Serializable;

public class Salle implements Serializable {

    private int numero;
    private int nbLignes;
    private int nbColonnes;
    private Place[][] places;

    public Salle(int numero, int nbLignes, int nbColonnes) {
        if (numero <= 0 || nbLignes < 1 || nbLignes > 26 || nbColonnes < 1 || nbColonnes > 50) {
            throw new IllegalArgumentException("La salle doit avoir un numéro positif, 1 à 26 lignes et 1 à 50 places par ligne.");
        }
        this.numero = numero;
        this.nbLignes = nbLignes;
        this.nbColonnes = nbColonnes;
        this.places = new Place[nbLignes][nbColonnes];

        creerPlaces();
    }

    // Remplit le tableau avec les places de la salle.
    private void creerPlaces() {
        for (int i = 0; i < nbLignes; i++) {
            for (int j = 0; j < nbColonnes; j++) {
                places[i][j] = new Place(i + 1, j + 1);
            }
        }
    }

    public int getNumero() {
        return numero;
    }

    public int getNbLignes() {
        return nbLignes;
    }

    public int getNbColonnes() {
        return nbColonnes;
    }

    public int getCapacite() {
        return nbLignes * nbColonnes;
    }

    public Place getPlace(int ligne, int numeroPlace) {
        if (ligne < 1 || ligne > nbLignes || numeroPlace < 1 || numeroPlace > nbColonnes) {
            throw new IllegalArgumentException("Cette place n'existe pas dans la salle.");
        }
        return places[ligne - 1][numeroPlace - 1];
    }

    public boolean contientPlace(Place place) {
        if (place == null || place.getLigne() < 1 || place.getLigne() > nbLignes
                || place.getNumero() < 1 || place.getNumero() > nbColonnes) {
            return false;
        }
        return getPlace(place.getLigne(), place.getNumero()) == place;
    }

    @Override
    public String toString() {
        return "Salle " + numero + " - " + getCapacite() + " places";
    }
}
