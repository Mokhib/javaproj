package videoclub;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Sauvegarde {

    public static void enregistrer(Cinema cinema, File fichier) throws IOException {
        FileOutputStream sortie = new FileOutputStream(fichier);
        ObjectOutputStream objets = new ObjectOutputStream(sortie);
        objets.writeObject(cinema);
        objets.close();
    }

    public static Cinema charger(File fichier) throws IOException, ClassNotFoundException {
        FileInputStream entree = new FileInputStream(fichier);
        ObjectInputStream objets = new ObjectInputStream(entree);
        Object objet = objets.readObject();
        objets.close();
        if (!(objet instanceof Cinema)) {
            throw new IOException("Ce fichier ne contient pas un cinéma.");
        }
        return (Cinema) objet;
    }
}
