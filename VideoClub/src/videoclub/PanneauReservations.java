/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package videoclub;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.DefaultComboBoxModel;
/**
 *
 * @author Admin
 */
public class PanneauReservations extends javax.swing.JPanel
        implements ActionListener {
    private Cinema cinema;
    private boolean modifie;
    private boolean remplirListe;
    private Seance seance;
    private JButton[][] boutons = new JButton[0][0];
    private ArrayList<Place> selection
            = new ArrayList<Place>();
    private DefaultComboBoxModel<Seance> modeleSeances
            = new DefaultComboBoxModel<Seance>();
    private DefaultComboBoxModel<Reservation> modeleReservations
            = new DefaultComboBoxModel<Reservation>();
    
    /**
     * Creates new form PanneauReservations
     */
    public PanneauReservations() {
        initComponents();
    }
    public PanneauReservations(Cinema cinema) {
        initComponents();
        this.cinema = cinema;

        remplirListe = true;
        seances.setModel(modeleSeances);
        reservations.setModel(modeleReservations);
        remplirListe = false;

        actualiser();
    }
    public void actualiser() {
        Seance ancienne = seance;

        remplirListe = true;
        modeleSeances.removeAllElements();

        for (Seance autre : cinema.getSeances()) {
            modeleSeances.addElement(autre);
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
            bilan.setText(
                    "Aucune séance. Ouvre Programme pour en ajouter une.");

        } else {
            Salle salleChoisie = seance.getSalle();

            boutons = new JButton[
                    salleChoisie.getNbLignes()
            ][
                    salleChoisie.getNbColonnes()
            ];

            grille.setLayout(new GridLayout(
                    salleChoisie.getNbLignes(),
                    salleChoisie.getNbColonnes(),
                    6,
                    6));

            for (int i = 0; i < boutons.length; i++) {
                for (int j = 0; j < boutons[i].length; j++) {
                    JButton bouton = new JButton(
                            salleChoisie
                                    .getPlace(i + 1, j + 1)
                                    .getCode());

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
                    Place place = seance.getSalle()
                            .getPlace(i + 1, j + 1);

                    JButton bouton = boutons[i][j];

                    boolean occupee
                            = seance.estPlaceOccupee(place);

                    bouton.setEnabled(!occupee);
                    bouton.setText(place.getCode());

                    if (selection.contains(place)) {
                        bouton.setText(place.getCode() + " *");
                    }
                }
            }

            bilan.setText(
                    seance.getNombrePlacesLibres()
                    + " places libres / "
                    + seance.getSalle().getCapacite()
                    + " | Tarif : "
                    + montant(seance.getTarif())
                    + " EUR | Recette : "
                    + montant(seance.calculerRecette())
                    + " EUR | Remplissage : "
                    + arrondir(seance.calculerTauxRemplissage())
                    + " %");
        }

        reserver.setEnabled(
                seance != null && !selection.isEmpty());

        double prix = 0;

        if (seance != null) {
            prix = selection.size() * seance.getTarif();
        }

        total.setText(
                selection.size()
                + " place(s) sélectionnée(s) : "
                + montant(prix)
                + " EUR");
    }
    private String montant(double prix) {
        return String.valueOf(arrondir(prix))
                .replace('.', ',');
    }

    private double arrondir(double nombre) {
        return Math.round(nombre * 100.0) / 100.0;
    }
    private void chercher() {
        remplirListe = true;
        modeleReservations.removeAllElements();

        for (Reservation reservation
                : cinema.rechercherReservations(
                        recherche.getText())) {

            modeleReservations.addElement(reservation);
        }

        remplirListe = false;
        afficherReservation();
    }
    private void afficherReservation() {
        Reservation reservation
                = (Reservation) reservations.getSelectedItem();

        annuler.setEnabled(reservation != null);

        if (reservation == null) {
            details.setText("Aucune réservation trouvée.");
            return;
        }

        Seance seanceReservee
                = cinema.rechercherSeanceReservation(
                        reservation.getNumero());

        details.setText(
                reservation.getNumero()
                + " - "
                + reservation.getNomClient()
                + "\n\n"
                + seanceReservee
                + "\n\nPlaces : "
                + reservation.getPlaces()
                + "\nTotal : "
                + montant(
                        reservation.getNombrePlaces()
                        * seanceReservee.getTarif())
                + " EUR");
    }
    private void reserverPlaces() {
        try {
            Reservation reservation = cinema.reserver(
                    seance,
                    nom.getText(),
                    selection);

            modifie = true;
            selection.clear();

            afficherPlaces();

            recherche.setText(reservation.getNumero());
            chercher();

            message.setText(
                    "Réservation "
                    + reservation.getNumero()
                    + " enregistrée. Pense à sauvegarder.");

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
        Reservation reservation
                = (Reservation) reservations.getSelectedItem();

        if (reservation != null
                && cinema.annulerReservation(
                        reservation.getNumero())) {

            modifie = true;

            afficherPlaces();
            chercher();

            message.setText(
                    "Réservation "
                    + reservation.getNumero()
                    + " annulée. Les places sont libres.");
        }
    }
        /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        haut = new javax.swing.JPanel();
        seances = new javax.swing.JComboBox<>();
        bilan = new javax.swing.JLabel();
        salle = new javax.swing.JPanel();
        label_ecran = new javax.swing.JLabel();
        defilementSalle = new javax.swing.JScrollPane();
        centreSalle = new javax.swing.JPanel();
        grille = new javax.swing.JPanel();
        légende = new javax.swing.JLabel();
        historique = new javax.swing.JPanel();
        filtres = new javax.swing.JPanel();
        label_recherche = new javax.swing.JLabel();
        recherche = new javax.swing.JTextField();
        rechercher = new javax.swing.JButton();
        reservations = new javax.swing.JComboBox<>();
        defilementDetails = new javax.swing.JScrollPane();
        details = new javax.swing.JTextArea();
        annuler = new javax.swing.JButton();
        bas = new javax.swing.JPanel();
        total = new javax.swing.JLabel();
        saisie = new javax.swing.JPanel();
        label_nomclient = new javax.swing.JLabel();
        nom = new javax.swing.JTextField();
        reserver = new javax.swing.JButton();
        message = new javax.swing.JLabel();

        setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setLayout(new java.awt.BorderLayout(10, 10));

        haut.setLayout(new java.awt.GridLayout(2, 1, 5, 5));

        seances.addActionListener(this::seancesActionPerformed);
        haut.add(seances);

        bilan.setText(" ");
        haut.add(bilan);

        add(haut, java.awt.BorderLayout.NORTH);

        salle.setLayout(new java.awt.BorderLayout(5, 8));

        label_ecran.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        label_ecran.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        label_ecran.setText("Ecran");
        salle.add(label_ecran, java.awt.BorderLayout.NORTH);

        centreSalle.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 12, 12));
        centreSalle.add(grille);

        defilementSalle.setViewportView(centreSalle);

        salle.add(defilementSalle, java.awt.BorderLayout.CENTER);

        légende.setText("* : sélectionnée | bouton désactivé : réservée");
        salle.add(légende, java.awt.BorderLayout.SOUTH);

        add(salle, java.awt.BorderLayout.CENTER);

        historique.setPreferredSize(new java.awt.Dimension(300, 350));
        historique.setLayout(new java.awt.BorderLayout(5, 10));

        filtres.setLayout(new java.awt.GridLayout(4, 1, 5, 8));

        label_recherche.setText("Numéro ou nom (vide = tout)");
        filtres.add(label_recherche);

        recherche.setColumns(14);
        recherche.addActionListener(this::rechercheActionPerformed);
        filtres.add(recherche);

        rechercher.setText("Rechercher");
        rechercher.addActionListener(this::rechercherActionPerformed);
        filtres.add(rechercher);

        reservations.addActionListener(this::reservationsActionPerformed);
        filtres.add(reservations);

        historique.add(filtres, java.awt.BorderLayout.NORTH);

        details.setEditable(false);
        details.setColumns(25);
        details.setRows(5);
        defilementDetails.setViewportView(details);

        historique.add(defilementDetails, java.awt.BorderLayout.CENTER);

        annuler.setText("Annuler cette réservation");
        annuler.addActionListener(this::annulerActionPerformed);
        historique.add(annuler, java.awt.BorderLayout.SOUTH);

        add(historique, java.awt.BorderLayout.EAST);

        bas.setDoubleBuffered(false);
        bas.setLayout(new java.awt.GridLayout(3, 1, 5, 5));

        total.setText(" ");
        bas.add(total);

        saisie.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        label_nomclient.setText("Nom du client");
        saisie.add(label_nomclient);

        nom.setColumns(18);
        saisie.add(nom);

        reserver.setText("Réserver les places");
        reserver.addActionListener(this::reserverActionPerformed);
        saisie.add(reserver);

        bas.add(saisie);

        message.setText("Choisis une séance et clique sur les places libres");
        bas.add(message);

        add(bas, java.awt.BorderLayout.SOUTH);
    }// </editor-fold>//GEN-END:initComponents

    private void seancesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_seancesActionPerformed
        if (!remplirListe) {
            changerSeance();
            message.setText(
                    "La sélection de places a été remise à zéro.");
        }
    }//GEN-LAST:event_seancesActionPerformed
    private void reserverActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reserverActionPerformed
        reserverPlaces();
    }//GEN-LAST:event_reserverActionPerformed
    private void rechercherActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rechercherActionPerformed
        chercher();
    }//GEN-LAST:event_rechercherActionPerformed
    private void rechercheActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rechercheActionPerformed
        chercher();
    }//GEN-LAST:event_rechercheActionPerformed
    private void reservationsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reservationsActionPerformed
        if (!remplirListe) {
            afficherReservation();
        }
    }//GEN-LAST:event_reservationsActionPerformed
    private void annulerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_annulerActionPerformed
        annulerReservation();
    }//GEN-LAST:event_annulerActionPerformed
    @Override
    public void actionPerformed(ActionEvent evenement) {
        Object source = evenement.getSource();

        if (seance != null) {
            for (int i = 0; i < boutons.length; i++) {
                for (int j = 0; j < boutons[i].length; j++) {
                    if (source == boutons[i][j]) {
                        Place place = seance.getSalle()
                                .getPlace(i + 1, j + 1);

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
    public boolean estModifie() {
        return modifie;
    }

    public void marquerEnregistre() {
        modifie = false;
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton annuler;
    private javax.swing.JPanel bas;
    private javax.swing.JLabel bilan;
    private javax.swing.JPanel centreSalle;
    private javax.swing.JScrollPane defilementDetails;
    private javax.swing.JScrollPane defilementSalle;
    private javax.swing.JTextArea details;
    private javax.swing.JPanel filtres;
    private javax.swing.JPanel grille;
    private javax.swing.JPanel haut;
    private javax.swing.JPanel historique;
    private javax.swing.JLabel label_ecran;
    private javax.swing.JLabel label_nomclient;
    private javax.swing.JLabel label_recherche;
    private javax.swing.JLabel légende;
    private javax.swing.JLabel message;
    private javax.swing.JTextField nom;
    private javax.swing.JTextField recherche;
    private javax.swing.JButton rechercher;
    private javax.swing.JComboBox<Reservation> reservations;
    private javax.swing.JButton reserver;
    private javax.swing.JPanel saisie;
    private javax.swing.JPanel salle;
    private javax.swing.JComboBox<Seance> seances;
    private javax.swing.JLabel total;
    // End of variables declaration//GEN-END:variables
}
