package GestionDeStock.DB;

import java.sql.*;
import java.util.ArrayList;

import GestionDeStock.Modele.Produits;

public class DBProduits {
    
    public void ajouterProduit(Produits produit) {
        if (!validerProduit(produit)) {
            System.out.println("Données du produit invalides.");
            return;
        }

        String sql = "INSERT INTO produits (nomProduit, descriptionProduit, prixProduit, quantite, idFournisseur) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, produit.nomProduit);
            pstmt.setString(2, produit.descriptionProduit);
            pstmt.setDouble(3, produit.prixProduit);
            pstmt.setInt(4, produit.quantite);
            pstmt.setString(5, produit.idFournisseur);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void modifierProduit(Produits produit) {
        if (!validerProduit(produit)) {
            System.out.println("Données du produit invalides.");
            return;
        }

        String sql = "UPDATE produits SET nomProduit = ?, descriptionProduit = ?, prixProduit = ?, quantite = ?, idFournisseur = ? WHERE idProduits = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, produit.nomProduit);
            pstmt.setString(2, produit.descriptionProduit);
            pstmt.setDouble(3, produit.prixProduit);
            pstmt.setInt(4, produit.quantite);
            pstmt.setString(5, produit.idFournisseur);
            pstmt.setInt(6, produit.idProduit);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Produits> consulterProduits() {
        ArrayList<Produits> produits = new ArrayList<>();
        String sql = "SELECT * FROM produits";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Produits produit = new Produits(
                    rs.getInt("idProduits"),
                    rs.getString("nomProduit"),
                    rs.getString("descriptionProduit"),
                    rs.getDouble("prixProduit"),
                    rs.getInt("quantite"),
                    rs.getString("idFournisseur")
                );
                produit.setDateAjout(rs.getDate("dateAjout"));
                produits.add(produit);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return produits;
    }

    private boolean validerProduit(Produits produit) {
        if (produit.nomProduit == null || produit.nomProduit.trim().isEmpty()) {
            System.out.println("Le nom du produit ne peut pas être vide.");
            return false;
        }
        if (produit.prixProduit <= 0) {
            System.out.println("Le prix du produit doit être un nombre positif.");
            return false;
        }
        if (produit.quantite < 0) {
            System.out.println("La quantité du produit ne peut pas être négative.");
            return false;
        }
        if (produit.idFournisseur == null || produit.idFournisseur.trim().isEmpty()) {
            System.out.println("L'ID du fournisseur ne peut pas être vide.");
            return false;
        }
        return true;
    }

    public void supprimerProduit(int idProduit) {
        // Vérifier d'abord si le produit existe et s'il n'est pas lié à des ventes
        String checkProduit = "SELECT COUNT(*) FROM produits WHERE idProduits = ?";
        String checkVentes = "SELECT COUNT(*) FROM vente WHERE idProduit = ?";
        String sql = "DELETE FROM produits WHERE idProduits = ?";

        try (Connection conn = DatabaseConnection.getConnection()) {
            // Vérifier si le produit existe
            try (PreparedStatement checkProduitStmt = conn.prepareStatement(checkProduit)) {
                checkProduitStmt.setInt(1, idProduit);
                ResultSet rsProduit = checkProduitStmt.executeQuery();
                if (rsProduit.next() && rsProduit.getInt(1) == 0) {
                    throw new SQLException("Aucun produit trouvé avec l'ID: " + idProduit);
                }
            }

            // Vérifier s'il y a des ventes associées
            try (PreparedStatement checkVentesStmt = conn.prepareStatement(checkVentes)) {
                checkVentesStmt.setInt(1, idProduit);
                ResultSet rsVentes = checkVentesStmt.executeQuery();
                if (rsVentes.next() && rsVentes.getInt(1) > 0) {
                    throw new SQLException("Impossible de supprimer le produit car il est associé à des ventes.");
                }
            }

            // Supprimer le produit
            try (PreparedStatement deleteStmt = conn.prepareStatement(sql)) {
                deleteStmt.setInt(1, idProduit);
                int rowsAffected = deleteStmt.executeUpdate();
                
                if (rowsAffected == 0) {
                    throw new SQLException("La suppression a échoué pour l'ID: " + idProduit);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la suppression du produit: " + e.getMessage());
        }
    }
}
