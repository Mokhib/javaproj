package videoclub;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class FenetreCinema extends JFrame implements ActionListener, WindowListener {

    private Cinema cinema;
    private File fichier = new File("videoclub.dat");
    private PanneauProgramme programme;
    private PanneauReservations reservations;
    private JPanel contenu = new JPanel(new BorderLayout());
    private JButton voirProgramme = new JButton("Programme");
    private JButton voirReservations = new JButton("Réservations");
    private JButton enregistrer = new JButton("Sauvegarder");
    private JButton charger = new JButton("Recharger");
    private JLabel message = new JLabel(" ");

    public FenetreCinema() {
        super("VideoClub");
        cinema = new Cinema();
        if (fichier.exists()) {
            try {
                cinema = Sauvegarde.charger(fichier);
                message.setText("La dernière sauvegarde a été chargée.");
            } catch (IOException e) {
                message.setText("Sauvegarde illisible. Le cinéma est vide, le fichier est conservé.");
            } catch (ClassNotFoundException e) {
                message.setText("Sauvegarde incompatible avec cette version de VideoClub.");
            }
        } else {
            message.setText("Bienvenue dans VideoClub. Crée ton programme pour commencer.");
        }
        setLayout(new BorderLayout(8, 8));
        JPanel barre = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        barre.add(new JLabel("VideoClub"));
        barre.add(voirProgramme);
        barre.add(voirReservations);
        barre.add(enregistrer);
        barre.add(charger);
        add(barre, BorderLayout.NORTH);
        add(contenu, BorderLayout.CENTER);
        add(message, BorderLayout.SOUTH);
        creerPanneaux();
        afficher(programme);

        voirProgramme.addActionListener(this);
        voirReservations.addActionListener(this);
        enregistrer.addActionListener(this);
        charger.addActionListener(this);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(this);
        setSize(1100, 680);
    }

    private void creerPanneaux() {
        programme = new PanneauProgramme(cinema);
        reservations = new PanneauReservations(cinema);
    }

    private void afficher(JPanel panneau) {
        contenu.removeAll();
        contenu.add(panneau, BorderLayout.CENTER);
        contenu.revalidate();
        contenu.repaint();
    }

    private boolean estModifie() {
        return programme.estModifie() || reservations.estModifie();
    }

    private boolean sauvegarder() {
        try {
            Sauvegarde.enregistrer(cinema, fichier);
            programme.marquerEnregistre();
            reservations.marquerEnregistre();
            message.setText("Sauvegarde effectuée dans " + fichier.getAbsolutePath());
            return true;
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Impossible de sauvegarder : " + e.getMessage(),
                    "Sauvegarde", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    private void recharger() {
        if (estModifie()) {
            int reponse = JOptionPane.showConfirmDialog(this,
                    "Recharger remplacera les modifications non sauvegardées. Continuer ?",
                    "Recharger", JOptionPane.YES_NO_OPTION);
            if (reponse != JOptionPane.YES_OPTION) {
                return;
            }
        }
        try {
            // Le cinéma actuel reste en place si la lecture échoue.
            Cinema copie = Sauvegarde.charger(fichier);
            cinema = copie;
            creerPanneaux();
            afficher(programme);
            message.setText("Sauvegarde rechargée.");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Impossible de recharger : " + e.getMessage(),
                    "Recharger", JOptionPane.ERROR_MESSAGE);
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(this, "Ce fichier vient d'une version incompatible.",
                    "Recharger", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void actionPerformed(ActionEvent evenement) {
        Object bouton = evenement.getSource();
        if (bouton == voirProgramme) {
            programme.actualiser();
            afficher(programme);
        } else if (bouton == voirReservations) {
            reservations.actualiser();
            afficher(reservations);
        } else if (bouton == enregistrer) {
            sauvegarder();
        } else if (bouton == charger) {
            recharger();
        }
    }

    @Override
    public void windowClosing(WindowEvent evenement) {
        if (estModifie()) {
            int reponse = JOptionPane.showConfirmDialog(this,
                    "Sauvegarder les modifications avant de quitter ?", "Quitter VideoClub",
                    JOptionPane.YES_NO_CANCEL_OPTION);
            if (reponse == JOptionPane.CANCEL_OPTION || reponse == JOptionPane.CLOSED_OPTION) {
                return;
            }
            if (reponse == JOptionPane.YES_OPTION && !sauvegarder()) {
                return;
            }
        }
        dispose();
    }

    @Override
    public void windowOpened(WindowEvent evenement) { }
    @Override
    public void windowClosed(WindowEvent evenement) { }
    @Override
    public void windowIconified(WindowEvent evenement) { }
    @Override
    public void windowDeiconified(WindowEvent evenement) { }
    @Override
    public void windowActivated(WindowEvent evenement) { }
    @Override
    public void windowDeactivated(WindowEvent evenement) { }
}
