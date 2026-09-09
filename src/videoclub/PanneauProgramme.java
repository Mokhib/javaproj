package videoclub;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class PanneauProgramme extends JPanel implements ActionListener {

    private Cinema cinema;
    private boolean modifie;
    private JTextField titre = new JTextField();
    private JTextField genre = new JTextField();
    private JTextField duree = new JTextField();
    private JTextField numeroSalle = new JTextField();
    private JTextField lignes = new JTextField();
    private JTextField colonnes = new JTextField();
    private JTextField date = new JTextField();
    private JTextField heure = new JTextField();
    private JTextField tarif = new JTextField("9,50");
    private JComboBox<Film> films = new JComboBox<Film>();
    private JComboBox<Salle> salles = new JComboBox<Salle>();
    private JComboBox<Seance> seances = new JComboBox<Seance>();
    private JButton ajouterFilm = new JButton("Ajouter le film");
    private JButton supprimerFilm = new JButton("Retirer le film choisi");
    private JButton ajouterSalle = new JButton("Ajouter la salle");
    private JButton ajouterSeance = new JButton("Programmer la séance");
    private JButton supprimerSeance = new JButton("Retirer la séance choisie");
    private JLabel message = new JLabel("Commence par un film et une salle, puis programme une séance.");

    public PanneauProgramme(Cinema cinema) {
        this.cinema = cinema;
        setLayout(new BorderLayout(10, 10));
        JPanel formulaires = new JPanel(new GridLayout(1, 3, 12, 0));

        formulaires.add(creerFormulaireFilm());
        formulaires.add(creerFormulaireSalle());
        formulaires.add(creerFormulaireSeance());

        JPanel programme = new JPanel(new BorderLayout(8, 8));
        programme.add(seances, BorderLayout.CENTER);
        programme.add(supprimerSeance, BorderLayout.EAST);
        JPanel bas = new JPanel(new BorderLayout(8, 16));
        bas.add(programme, BorderLayout.NORTH);
        bas.add(message, BorderLayout.SOUTH);
        add(formulaires, BorderLayout.CENTER);
        add(bas, BorderLayout.SOUTH);

        ajouterFilm.addActionListener(this);
        supprimerFilm.addActionListener(this);
        ajouterSalle.addActionListener(this);
        ajouterSeance.addActionListener(this);
        supprimerSeance.addActionListener(this);
        actualiser();
    }

    private JPanel creerFormulaireFilm() {
        JPanel film = new JPanel(new BorderLayout(5, 10));
        JPanel champsFilm = new JPanel(new GridLayout(0, 2, 5, 10));
        champsFilm.add(new JLabel("Titre"));
        champsFilm.add(titre);
        champsFilm.add(new JLabel("Genre"));
        champsFilm.add(genre);
        champsFilm.add(new JLabel("Durée (minutes)"));
        champsFilm.add(duree);
        JPanel listeFilms = new JPanel(new GridLayout(0, 1, 5, 10));
        listeFilms.add(ajouterFilm);
        listeFilms.add(new JLabel("Film choisi pour la séance"));
        listeFilms.add(films);
        listeFilms.add(supprimerFilm);
        film.add(champsFilm, BorderLayout.NORTH);
        film.add(listeFilms, BorderLayout.SOUTH);
        return film;
    }

    private JPanel creerFormulaireSalle() {
        JPanel salle = new JPanel(new BorderLayout(5, 10));
        JPanel champsSalle = new JPanel(new GridLayout(0, 2, 5, 10));
        champsSalle.add(new JLabel("Numéro"));
        champsSalle.add(numeroSalle);
        champsSalle.add(new JLabel("Rangées (1 à 26)"));
        champsSalle.add(lignes);
        champsSalle.add(new JLabel("Sièges/rangée (1 à 50)"));
        champsSalle.add(colonnes);
        JPanel listeSalles = new JPanel(new GridLayout(0, 1, 5, 10));
        listeSalles.add(ajouterSalle);
        listeSalles.add(new JLabel("Salle choisie pour la séance"));
        listeSalles.add(salles);
        salle.add(champsSalle, BorderLayout.NORTH);
        salle.add(listeSalles, BorderLayout.SOUTH);
        return salle;
    }

    private JPanel creerFormulaireSeance() {
        JPanel seance = new JPanel(new BorderLayout(5, 10));
        JPanel champsSeance = new JPanel(new GridLayout(0, 2, 5, 10));
        champsSeance.add(new JLabel("Date (JJ/MM/AAAA)"));
        champsSeance.add(date);
        champsSeance.add(new JLabel("Heure (HH:MM)"));
        champsSeance.add(heure);
        champsSeance.add(new JLabel("Prix par place (EUR)"));
        champsSeance.add(tarif);
        JPanel boutonsSeance = new JPanel(new GridLayout(0, 1, 5, 10));
        boutonsSeance.add(new JLabel("Date et heure de la séance"));
        boutonsSeance.add(ajouterSeance);
        seance.add(champsSeance, BorderLayout.NORTH);
        seance.add(boutonsSeance, BorderLayout.SOUTH);
        return seance;
    }

    public void actualiser() {
        Film filmChoisi = (Film) films.getSelectedItem();
        Salle salleChoisie = (Salle) salles.getSelectedItem();
        films.removeAllItems();
        for (Film film : cinema.getFilms()) {
            films.addItem(film);
        }
        if (cinema.getFilms().contains(filmChoisi)) {
            films.setSelectedItem(filmChoisi);
        }
        salles.removeAllItems();
        for (Salle salle : cinema.getSalles()) {
            salles.addItem(salle);
        }
        if (cinema.getSalles().contains(salleChoisie)) {
            salles.setSelectedItem(salleChoisie);
        }
        seances.removeAllItems();
        for (Seance seance : cinema.getSeances()) {
            seances.addItem(seance);
        }
    }

    @Override
    public void actionPerformed(ActionEvent evenement) {
        Object bouton = evenement.getSource();
        try {
            if (bouton == ajouterFilm) {
                Film film = new Film(titre.getText().trim(), Integer.parseInt(duree.getText().trim()),
                        genre.getText().trim());
                cinema.ajouterFilm(film);
                actualiser();
                films.setSelectedItem(film);
                titre.setText("");
                genre.setText("");
                duree.setText("");
                message.setText("Film ajouté : " + film.getTitre());
            } else if (bouton == supprimerFilm) {
                cinema.supprimerFilm((Film) films.getSelectedItem());
                actualiser();
                message.setText("Le film a été retiré.");
            } else if (bouton == ajouterSalle) {
                Salle salle = new Salle(Integer.parseInt(numeroSalle.getText().trim()),
                        Integer.parseInt(lignes.getText().trim()), Integer.parseInt(colonnes.getText().trim()));
                cinema.ajouterSalle(salle);
                actualiser();
                salles.setSelectedItem(salle);
                message.setText("La salle " + salle.getNumero() + " est prête.");
            } else if (bouton == ajouterSeance) {
                double prix = Double.parseDouble(tarif.getText().trim().replace(',', '.'));
                Seance seance = new Seance((Film) films.getSelectedItem(), (Salle) salles.getSelectedItem(),
                        date.getText(), heure.getText(), prix);
                cinema.ajouterSeance(seance);
                actualiser();
                seances.setSelectedItem(seance);
                message.setText("Séance ajoutée. Tu peux maintenant réserver des places.");
            } else if (bouton == supprimerSeance) {
                cinema.supprimerSeance((Seance) seances.getSelectedItem());
                actualiser();
                message.setText("La séance a été retirée.");
            }
            modifie = true;
        } catch (NumberFormatException e) {
            message.setText("Vérifie les nombres saisis. Exemple de tarif : 9,50.");
        } catch (IllegalArgumentException e) {
            message.setText(e.getMessage());
        }
    }

    public boolean estModifie() {
        return modifie;
    }

    public void marquerEnregistre() {
        modifie = false;
    }
}
