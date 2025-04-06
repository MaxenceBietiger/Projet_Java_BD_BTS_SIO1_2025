package GestionDeStock;

import java.util.ArrayList;
import java.util.Objects;

public class Fournisseurs {
    public String nomFournisseur;
    public String idFournisseur;
    ArrayList<Fournisseurs> listeFournisseurs = new ArrayList<Fournisseurs>();

    //Constructeur de la classe Fournisseurs
    public Fournisseurs(String idFournisseur, String nomFournisseur) {  // Ordre inversé ici
        this.nomFournisseur = nomFournisseur;
        this.idFournisseur = idFournisseur;
    }

    // Getters et Setters
    public String getIdFournisseur() {
        return idFournisseur;
    }

    public void setIdFournisseur(String idFournisseur) {
        this.idFournisseur = idFournisseur;
    }

    public String getNomFournisseur() {
        return nomFournisseur;
    }

    public void setNomFournisseur(String nomFournisseur) {
        this.nomFournisseur = nomFournisseur;
    }

    //Méthode pour afficher les fournisseurs dans la liste des fournisseurs
    public void afficherFournisseurs() {
        for (Fournisseurs f : listeFournisseurs) {
            System.out.println("Nom: " + f.nomFournisseur + " Numéro: " + f.idFournisseur);
        }
    }

    //Méthode pour modifier un fournisseur
    public void mofierFournisseur(Fournisseurs fournisseur) {
        for (Fournisseurs f : listeFournisseurs) {
            if (Objects.equals(f.nomFournisseur, fournisseur.nomFournisseur)) {
                f.idFournisseur = fournisseur.idFournisseur;
            }
        }
    }

    //Méthode pour ajouter un fournisseurs
    public void ajouterFournisseur(Fournisseurs fournisseur) {
        listeFournisseurs.add(fournisseur);
    }

    //Méthode pour supprimer un fournisseur
    public void supprimerFournisseur(Fournisseurs fournisseur) {
        listeFournisseurs.remove(fournisseur);
    }
}
