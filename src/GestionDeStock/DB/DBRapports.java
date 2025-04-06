package GestionDeStock.DB;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;

import GestionDeStock.Modele.Produits;
import GestionDeStock.Modele.Vente;

public class DBRapports {
    
    public ArrayList<Produits> genererRapportProduits(Date dateDebut, Date dateFin) {
        ArrayList<Produits> produits = new ArrayList<>();
        String sql = "SELECT p.*, f.nomFournisseur, " +
                    "(p.quantite * p.prixProduit) as valeur_stock " +
                    "FROM produits p " +
                    "LEFT JOIN fournisseurs f ON p.idFournisseur = f.idFournisseur " +
                    "WHERE p.dateAjout BETWEEN ? AND ? " +
                    "ORDER BY p.nomProduit";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setDate(1, new java.sql.Date(dateDebut.getTime()));
            pstmt.setDate(2, new java.sql.Date(dateFin.getTime()));

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Produits produit = new Produits(
                        rs.getInt("idProduits"),
                        rs.getString("nomProduit"),
                        rs.getString("descriptionProduit"),
                        rs.getDouble("prixProduit"),
                        rs.getInt("quantite"),
                        rs.getString("nomFournisseur")
                    );
                    produits.add(produit);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return produits;
    }

    public ArrayList<Vente> genererRapportVentes(Date dateDebut, Date dateFin) {
        ArrayList<Vente> ventes = new ArrayList<>();
        String sql = "SELECT v.*, p.nomProduit " +
                    "FROM vente v " +
                    "JOIN produits p ON v.idProduit = p.idProduits " +
                    "WHERE v.dateVente BETWEEN ? AND ? " +
                    "ORDER BY v.dateVente DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDate(1, new java.sql.Date(dateDebut.getTime()));
            pstmt.setDate(2, new java.sql.Date(dateFin.getTime()));

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Vente vente = new Vente(
                        rs.getInt("idVente"),
                        rs.getString("dateVente"),
                        rs.getDouble("montantVente"),
                        rs.getInt("idProduit")
                    );
                    ventes.add(vente);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ventes;
    }

    public ArrayList<Produits> genererRapportStock() {
        ArrayList<Produits> produits = new ArrayList<>();
        String sql = "SELECT p.*, f.nomFournisseur FROM produits p " +
                    "LEFT JOIN fournisseurs f ON p.idFournisseur = f.idFournisseur " +
                    "WHERE p.quantite <= 10 " +
                    "ORDER BY p.quantite ASC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Produits produit = new Produits(
                    rs.getInt("idProduits"),
                    rs.getString("nomProduit"),
                    rs.getString("descriptionProduit"),
                    rs.getDouble("prixProduit"),
                    rs.getInt("quantite"),
                    rs.getString("nomFournisseur")
                );
                produits.add(produit);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return produits;
    }
}
