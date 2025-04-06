package GestionDeStock.DB;

import java.sql.*;
import java.util.ArrayList;

import GestionDeStock.Modele.Vente;
import GestionDeStock.Utils.ValidationUtils;

public class DBVente {

    public int ajouterVente(Vente vente) {
        String sqlVente = "INSERT INTO vente (dateVente, montantVente, idProduit) VALUES (?, ?, ?)";
        int generatedId = -1;

        try (Connection conn = DatabaseConnection.getConnection()) {
            // Désactiver l'auto-commit pour la transaction
            conn.setAutoCommit(false);
            
            try {
                // Vérifier si le produit existe et a du stock
                String checkProduct = "SELECT quantite FROM produits WHERE idProduits = ?";
                try (PreparedStatement checkStmt = conn.prepareStatement(checkProduct)) {
                    checkStmt.setInt(1, vente.getIdProduit());
                    ResultSet rs = checkStmt.executeQuery();
                    
                    if (!rs.next()) {
                        throw new SQLException("Le produit avec l'ID " + vente.getIdProduit() + " n'existe pas.");
                    }
                    
                    int stockActuel = rs.getInt("quantite");
                    if (stockActuel <= 0) {
                        throw new SQLException("Stock insuffisant pour le produit " + vente.getIdProduit());
                    }
                }

                // Insérer la vente
                try (PreparedStatement pstmt = conn.prepareStatement(sqlVente, Statement.RETURN_GENERATED_KEYS)) {
                    pstmt.setString(1, vente.getDateVente());
                    pstmt.setDouble(2, vente.getMontantVente());
                    pstmt.setInt(3, vente.getIdProduit());
                    
                    int affectedRows = pstmt.executeUpdate();
                    
                    if (affectedRows == 0) {
                        throw new SQLException("La création de la vente a échoué, aucune ligne affectée.");
                    }

                    try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            generatedId = generatedKeys.getInt(1);
                        } else {
                            throw new SQLException("La création de la vente a échoué, aucun ID obtenu.");
                        }
                    }
                }

                // Mettre à jour le stock
                String updateStock = "UPDATE produits SET quantite = quantite - 1 WHERE idProduits = ?";
                try (PreparedStatement updateStmt = conn.prepareStatement(updateStock)) {
                    updateStmt.setInt(1, vente.getIdProduit());
                    updateStmt.executeUpdate();
                }

                // Valider la transaction
                conn.commit();
                System.out.println("Vente ajoutée avec succès, ID: " + generatedId);
                return generatedId;
                
            } catch (SQLException e) {
                // En cas d'erreur, annuler la transaction
                conn.rollback();
                System.err.println("Erreur lors de l'ajout de la vente: " + e.getMessage());
                throw new RuntimeException("Erreur lors de l'ajout de la vente: " + e.getMessage());
            }
        } catch (SQLException e) {
            System.err.println("Erreur de connexion: " + e.getMessage());
            throw new RuntimeException("Erreur de connexion à la base de données: " + e.getMessage());
        }
    }

    public void modifierVente(Vente vente) {
        if (!validerVente(vente)) {
            System.out.println("Données de la vente invalides.");
            return;
        }

        String sql = "UPDATE vente SET dateVente = ?, montantVente = ?, idProduit = ? WHERE idVente = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, vente.getDateVente());
            pstmt.setDouble(2, vente.getMontantVente());
            pstmt.setInt(3, vente.getIdProduit());
            pstmt.setInt(4, vente.getIdVente());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Vente> consulterVentes() {
        ArrayList<Vente> ventes = new ArrayList<>();
        String sql = "SELECT * FROM vente";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Vente vente = new Vente(
                    rs.getInt("idVente"),
                    rs.getString("dateVente"),
                    rs.getDouble("montantVente"),
                    rs.getInt("idProduit")
                );
                ventes.add(vente);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ventes;
    }

    public void supprimerVente(int idVente) {
        String checkVente = "SELECT COUNT(*) FROM vente WHERE idVente = ?";
        String sql = "DELETE FROM vente WHERE idVente = ?";

        try (Connection conn = DatabaseConnection.getConnection()) {
            // Vérifier si la vente existe
            try (PreparedStatement checkStmt = conn.prepareStatement(checkVente)) {
                checkStmt.setInt(1, idVente);
                ResultSet rs = checkStmt.executeQuery();
                if (rs.next() && rs.getInt(1) == 0) {
                    throw new SQLException("Aucune vente trouvée avec l'ID: " + idVente);
                }
            }

            // Supprimer la vente
            try (PreparedStatement deleteStmt = conn.prepareStatement(sql)) {
                deleteStmt.setInt(1, idVente);
                int rowsAffected = deleteStmt.executeUpdate();
                
                if (rowsAffected == 0) {
                    throw new SQLException("La suppression a échoué pour l'ID: " + idVente);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de la suppression de la vente: " + e.getMessage());
        }
    }
}
