package GestionDeStock.Modele;

import java.util.ArrayList;

public class Vente {
    private int idVente;
    private String dateVente;
    private double montantVente;
    private int idProduit;

    public Vente(int idVente, String dateVente, double montantVente, int idProduit) {
        this.idVente = idVente;
        this.dateVente = dateVente;
        this.montantVente = montantVente;
        this.idProduit = idProduit;
    }

    // Getters et Setters
    public int getIdVente() {
        return idVente;
    }

    public void setIdVente(int idVente) {
        this.idVente = idVente;
    }

    public String getDateVente() {
        return dateVente;
    }

    public void setDateVente(String dateVente) {
        this.dateVente = dateVente;
    }

    public double getMontantVente() {
        return montantVente;
    }

    public void setMontantVente(double montantVente) {
        this.montantVente = montantVente;
    }

    public int getIdProduit() {
        return idProduit;
    }

    public void setIdProduit(int idProduit) {
        this.idProduit = idProduit;
    }
}
