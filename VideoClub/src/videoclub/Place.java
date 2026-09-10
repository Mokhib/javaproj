package videoclub;

import java.io.Serializable;

public class Place implements Serializable {

    private int ligne;
    private int numero;

    public Place(int ligne, int numero) {
        this.ligne = ligne;
        this.numero = numero;
    }

    public int getLigne() {
        return ligne;
    }

    public int getNumero() {
        return numero;
    }

    public String getCode() {
        char lettre = (char) ('A' + ligne - 1);
        return String.valueOf(lettre) + numero;
    }

    @Override
    public String toString() {
        return getCode();
    }
}
