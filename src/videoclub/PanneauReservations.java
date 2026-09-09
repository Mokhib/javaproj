package videoclub;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class PanneauReservations extends JPanel implements ActionListener {

    private Cinema cinema;
    private boolean modifie;
    private boolean remplirListe;
    private JComboBox<Seance> seances = new JComboBox<Seance>();
    private Seance seance;
    private JPanel grille = new JPanel();
    private JButton[][] boutons = new JButton[0][0];
    private ArrayList<Place> selection = new ArrayList<Place>();
    private JTextField nom = new JTextField(18);
    private JButton reserver = new JButton("Réserver les places");
    private JLabel bilan = new JLabel(" ");
    private JLabel total = new JLabel(" ");
    private JLabel message = new JLabel("Choisis une séance et clique sur les places libres.");
    private JTextField recherche = new JTextField(14);
    private JButton rechercher = new JButton("Rechercher");
    private JComboBox<Reservation> reservations = new JComboBox<Reservation>();
    private JTextArea details = new JTextArea(5, 25);
    private JButton annuler = new JButton("Annuler cette réservation");

    public PanneauReservations(Cinema cinema) {
        this.cinema = cinema;
        setLayout(new BorderLayout(10, 10));
        JPanel haut = new JPanel(new GridLayout(0, 1, 5, 5));
        haut.add(seances);
        haut.add(bilan);
        add(haut, BorderLayout.NORTH);

        JPanel salle = new JPanel(new BorderLayout(5, 8));
        JLabel ecran = new JLabel("ÉCRAN", JLabel.CENTER);
        salle.add(ecran, BorderLayout.NORTH);
        JPanel centre = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 12));
        centre.add(grille);
        salle.add(new JScrollPane(centre), BorderLayout.CENTER);
        salle.add(new JLabel("* : sélectionnée   |   bouton désactivé : réservée"), BorderLayout.SOUTH);
        add(salle, BorderLayout.CENTER);

        JPanel historique = new JPanel(new BorderLayout(5, 10));
        historique.setPreferredSize(new Dimension(300, 350));
        JPanel filtres = new JPanel(new GridLayout(0, 1, 5, 8));
        filtres.add(new JLabel("Numéro ou nom (vide = tout)"));
        filtres.add(recherche);
        filtres.add(rechercher);
        filtres.add(reservations);
        historique.add(filtres, BorderLayout.NORTH);
        details.setEditable(false);
        historique.add(new JScrollPane(details), BorderLayout.CENTER);
        historique.add(annuler, BorderLayout.SOUTH);
        add(historique, BorderLayout.EAST);

        JPanel bas = new JPanel(new GridLayout(0, 1, 5, 5));
        JPanel saisie = new JPanel(new FlowLayout(FlowLayout.LEFT));
        saisie.add(new JLabel("Nom du client"));
        saisie.add(nom);
        saisie.add(reserver);
        bas.add(total);
        bas.add(saisie);
        bas.add(message);
        add(bas, BorderLayout.SOUTH);

        seances.addActionListener(this);
        reserver.addActionListener(this);
        rechercher.addActionListener(this);
        recherche.addActionListener(this);
        reservations.addActionListener(this);
        annuler.addActionListener(this);
        actualiser();
    }

    public void actualiser() {
        Seance ancienne = seance;
        remplirListe = true;
        seances.removeAllItems();
        for (Seance autre : cinema.getSeances()) {
            seances.addItem(autre);
        }
        if (cinema.getSeances().contains(ancienne)) {
            seances.setSelectedItem(ancienne);
        }
        remplirListe = false;
        changerSeance();
        chercher();
    }

    private void changerSeance() {
        seance = (Seance) seances.getSelectedItem();
        selection.clear();
        grille.removeAll();
        if (seance == null) {
            boutons = new JButton[0][0];
            bilan.setText("Aucune séance. Ouvre Programme pour en ajouter une.");
        } else {
            Salle salle = seance.getSalle();
            boutons = new JButton[salle.getNbLignes()][salle.getNbColonnes()];
            grille.setLayout(new GridLayout(salle.getNbLignes(), salle.getNbColonnes(), 6, 6));
            for (int i = 0; i < boutons.length; i++) {
                for (int j = 0; j < boutons[i].length; j++) {
                    JButton bouton = new JButton(salle.getPlace(i + 1, j + 1).getCode());
                    bouton.setPreferredSize(new Dimension(68, 40));
                    bouton.addActionListener(this);
                    boutons[i][j] = bouton;
                    grille.add(bouton);
                }
            }
        }
        afficherPlaces();
        grille.revalidate();
        grille.repaint();
    }

    private void afficherPlaces() {
        if (seance != null) {
            for (int i = 0; i < boutons.length; i++) {
                for (int j = 0; j < boutons[i].length; j++) {
                    Place place = seance.getSalle().getPlace(i + 1, j + 1);
                    JButton bouton = boutons[i][j];
                    boolean occupee = seance.estPlaceOccupee(place);
                    bouton.setEnabled(!occupee);
                    bouton.setText(place.getCode());
                    if (selection.contains(place)) {
                        bouton.setText(place.getCode() + " *");
                    }
                }
            }
            bilan.setText(seance.getNombrePlacesLibres() + " places libres / " + seance.getSalle().getCapacite()
                    + "   |   Tarif : " + montant(seance.getTarif()) + " EUR   |   Recette : "
                    + montant(seance.calculerRecette()) + " EUR   |   Remplissage : "
                    + arrondir(seance.calculerTauxRemplissage()) + " %");
        }
        reserver.setEnabled(seance != null && !selection.isEmpty());
        double prix = 0;
        if (seance != null) {
            prix = selection.size() * seance.getTarif();
        }
        total.setText(selection.size() + " place(s) sélectionnée(s) : " + montant(prix) + " EUR");
    }

    private String montant(double prix) {
        return String.valueOf(arrondir(prix)).replace('.', ',');
    }

    private double arrondir(double nombre) {
        return Math.round(nombre * 100.0) / 100.0;
    }

    private void chercher() {
        remplirListe = true;
        reservations.removeAllItems();
        for (Reservation reservation : cinema.rechercherReservations(recherche.getText())) {
            reservations.addItem(reservation);
        }
        remplirListe = false;
        afficherReservation();
    }

    private void afficherReservation() {
        Reservation reservation = (Reservation) reservations.getSelectedItem();
        annuler.setEnabled(reservation != null);
        if (reservation == null) {
            details.setText("Aucune réservation trouvée.");
            return;
        }
        Seance seanceReservee = cinema.rechercherSeanceReservation(reservation.getNumero());
        details.setText(reservation.getNumero() + " - " + reservation.getNomClient() + "\n\n"
                + seanceReservee + "\n\nPlaces : " + reservation.getPlaces() + "\nTotal : "
                + montant(reservation.getNombrePlaces() * seanceReservee.getTarif()) + " EUR");
    }

    @Override
    public void actionPerformed(ActionEvent evenement) {
        if (remplirListe) {
            return;
        }
        Object bouton = evenement.getSource();
        if (bouton == seances) {
            changerSeance();
            message.setText("La sélection de places a été remise à zéro.");
        } else if (bouton == rechercher || bouton == recherche) {
            chercher();
        } else if (bouton == reservations) {
            afficherReservation();
        } else if (bouton == reserver) {
            reserverPlaces();
        } else if (bouton == annuler) {
            annulerReservation();
        } else if (seance != null) {
            for (int i = 0; i < boutons.length; i++) {
                for (int j = 0; j < boutons[i].length; j++) {
                    if (bouton == boutons[i][j]) {
                        Place place = seance.getSalle().getPlace(i + 1, j + 1);
                        if (!selection.remove(place)) {
                            selection.add(place);
                        }
                        afficherPlaces();
                        return;
                    }
                }
            }
        }
    }

    private void reserverPlaces() {
        try {
            Reservation reservation = cinema.reserver(seance, nom.getText(), selection);
            modifie = true;
            selection.clear();
            afficherPlaces();
            recherche.setText(reservation.getNumero());
            chercher();
            message.setText("Réservation " + reservation.getNumero() + " enregistrée. Pense à sauvegarder.");
            nom.setText("");
        } catch (PlaceOccupeeException e) {
            selection.clear();
            afficherPlaces();
            message.setText(e.getMessage());
        } catch (IllegalArgumentException e) {
            message.setText(e.getMessage());
        }
    }

    private void annulerReservation() {
        Reservation reservation = (Reservation) reservations.getSelectedItem();
        if (reservation != null && cinema.annulerReservation(reservation.getNumero())) {
            modifie = true;
            afficherPlaces();
            chercher();
            message.setText("Réservation " + reservation.getNumero() + " annulée. Les places sont libres.");
        }
    }

    public boolean estModifie() {
        return modifie;
    }

    public void marquerEnregistre() {
        modifie = false;
    }
}
