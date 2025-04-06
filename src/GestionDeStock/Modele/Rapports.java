package GestionDeStock.Modele;


import java.util.ArrayList;
import java.util.Objects;

public class Rapports {

    int idRapport;
    String dateRapport;
    String contenuRapport;
    ArrayList<Rapports> listeRapports = new ArrayList<Rapports>();

    //Constructeur de la classe Rapports
    public Rapports(int idRapport, String dateRapport, String contenuRapport) {
        this.idRapport = idRapport;
        this.dateRapport = dateRapport;
        this.contenuRapport = contenuRapport;
    }

    // Getters et Setters
    public int getIdRapport() {
        return idRapport;
    }

    public void setIdRapport(int idRapport) {
        this.idRapport = idRapport;
    }

    public String getDateRapport() {
        return dateRapport;
    }

    public void setDateRapport(String dateRapport) {
        this.dateRapport = dateRapport;
    }

    public String getContenuRapport() {
        return contenuRapport;
    }

    public void setContenuRapport(String contenuRapport) {
        this.contenuRapport = contenuRapport;
    }


    //Méthode pour afficher les rapports dans la liste des rapports
    public void afficherRapports() {
        for (Rapports r : listeRapports) {
            System.out.println("ID: " + r.idRapport + " Date: " + r.dateRapport + " Contenu: " + r.contenuRapport);
        }
    }

    //Méthode pour ajouter un rapport
    public void ajouterRapport(Rapports rapport) {
            listeRapports.add(rapport);
        }

        //Méthode pour supprimer un rapport
        public void supprimerRapport(Rapports rapport) {
            listeRapports.remove(rapport);
        }

        //Méthode pour rechercher un rapport
        public void rechercherRapport(int idRapport) {
            for (Rapports r : listeRapports) {
                if (Objects.equals(r.idRapport, idRapport)) {
                    System.out.println("ID: " + r.idRapport + " Date: " + r.dateRapport + " Contenu: " + r.contenuRapport);
                }
            }
        }
}
