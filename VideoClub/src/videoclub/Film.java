package videoclub;

import java.io.Serializable;

public class Film implements Serializable {

    private String titre;
    private int duree;
    private String genre;

    public Film(String titre, int duree, String genre) {
        setTitre(titre);
        setDuree(duree);
        setGenre(genre);
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        if (titre == null || titre.trim().isEmpty()) {
            throw new IllegalArgumentException("Le film doit avoir un titre.");
        }
        this.titre = titre;
    }

    public int getDuree() {
        return duree;
    }

    public void setDuree(int duree) {
        if (duree <= 0) {
            throw new IllegalArgumentException("La durée doit être positive.");
        }
        this.duree = duree;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        if (genre == null || genre.trim().isEmpty()) {
            throw new IllegalArgumentException("Indique le genre du film.");
        }
        this.genre = genre;
    }

    @Override
    public String toString() {
        return titre + " - " + genre + " (" + duree + " min)";
    }
}
