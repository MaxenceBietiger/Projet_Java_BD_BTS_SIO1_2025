package GestionDeStock.DB;

import java.sql.*;
import java.util.ArrayList;

import GestionDeStock.Fournisseurs;

public class DBFournisseurs {
    
    public void ajouterFournisseur(Fournisseurs fournisseur) {
        String sql = "INSERT INTO fournisseurs (idFournisseur, nomFournisseur) VALUES (?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, fournisseur.idFournisseur);
            pstmt.setString(2, fournisseur.nomFournisseur);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void modifierFournisseur(Fournisseurs fournisseur) {
        String sql = "UPDATE fournisseurs SET nomFournisseur = ? WHERE idFournisseur = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, fournisseur.nomFournisseur);
            pstmt.setString(2, fournisseur.idFournisseur);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Fournisseurs> consulterFournisseurs() {
        ArrayList<Fournisseurs> fournisseurs = new ArrayList<>();
        String sql = "SELECT * FROM fournisseurs";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Fournisseurs fournisseur = new Fournisseurs(
                    rs.getString("idFournisseur"),  // Premier paramètre : ID
                    rs.getString("nomFournisseur")  // Second paramètre : Nom
                );
                fournisseurs.add(fournisseur);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return fournisseurs;
    }

    public void supprimerFournisseur(String idFournisseur) {
        // Vérifier si l'ID est valide
        if (idFournisseur == null || idFournisseur.trim().isEmpty()) {
            throw new RuntimeException("L'ID du fournisseur ne peut pas être vide");
        }

        // Vérifier d'abord si le fournisseur existe
        String checkFournisseur = "SELECT COUNT(*) FROM fournisseurs WHERE idFournisseur = ?";
        String checkProduits = "SELECT COUNT(*) FROM produits WHERE idFournisseur = ?";
        String sql = "DELETE FROM fournisseurs WHERE idFournisseur = ?";

        try (Connection conn = DatabaseConnection.getConnection()) {
            // Vérifier si le fournisseur existe
            try (PreparedStatement checkFournisseurStmt = conn.prepareStatement(checkFournisseur)) {
                checkFournisseurStmt.setString(1, idFournisseur);
                ResultSet rsFournisseur = checkFournisseurStmt.executeQuery();
                if (rsFournisseur.next() && rsFournisseur.getInt(1) == 0) {
                    throw new SQLException("Aucun fournisseur trouvé avec l'ID: " + idFournisseur);
                }
            }

            // Vérifier les produits associés
            try (PreparedStatement checkProduitsStmt = conn.prepareStatement(checkProduits)) {
                checkProduitsStmt.setString(1, idFournisseur);
                ResultSet rsProduits = checkProduitsStmt.executeQuery();
                if (rsProduits.next() && rsProduits.getInt(1) > 0) {
                    throw new SQLException("Impossible de supprimer le fournisseur car il a des produits associés.");
                }
            }

            // Supprimer le fournisseur
            try (PreparedStatement deleteStmt = conn.prepareStatement(sql)) {
                deleteStmt.setString(1, idFournisseur);
                System.out.println("Exécution de la requête de suppression pour l'ID: " + idFournisseur); // Debug
                int rowsAffected = deleteStmt.executeUpdate();
                System.out.println("Nombre de lignes affectées: " + rowsAffected); // Debug

                if (rowsAffected == 0) {
                    throw new SQLException("La suppression a échoué pour l'ID: " + idFournisseur);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur SQL: " + e.getMessage()); // Debug
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de la suppression du fournisseur: " + e.getMessage());
        }
    }
}
