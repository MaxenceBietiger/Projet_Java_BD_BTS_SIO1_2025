package GestionDeStock.GUI;

import javax.swing.*;
import javax.swing.table.*;

import GestionDeStock.DB.DBProduits;
import GestionDeStock.Modele.Produits;

import java.awt.*;
import java.util.ArrayList;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class GUIProduit extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private TableRowSorter<DefaultTableModel> sorter;
    private DBProduits produitDB;

    @SuppressWarnings("unused")
    public GUIProduit() {
        setLayout(new BorderLayout());
        produitDB = new DBProduits();
        
        // Initialiser le tableau avec la colonne de date
        String[] columnNames = {"ID", "Nom", "Description", "Prix", "Quantité", "ID Fournisseur", "Date d'ajout"};
        tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel);

        // Ajouter les produits au modèle de tableau
        refreshTableData();

        // Ajouter la fonctionnalité de tri
        sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);

        // Ajouter la fonctionnalité de recherche
        searchField = new JTextField(20);
        searchField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String text = searchField.getText();
                if (text.trim().length() == 0) {
                    sorter.setRowFilter(null);
                } else {
                    sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
                }
            }
        });

        // Créer le panel des boutons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton addButton = new JButton("Ajouter");
        JButton editButton = new JButton("Modifier");
        JButton deleteButton = new JButton("Supprimer");

        // Ajouter les listeners pour les boutons
        addButton.addActionListener(e -> ajouterProduit());
        editButton.addActionListener(e -> modifierProduit());
        deleteButton.addActionListener(e -> supprimerProduit());

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);

        // Ajouter les composants au panel principal
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(searchField, BorderLayout.SOUTH);
        
        add(buttonPanel, BorderLayout.NORTH);
        add(panel, BorderLayout.CENTER);
    }

    private void refreshTableData() {
        tableModel.setRowCount(0);
        ArrayList<Produits> produits = produitDB.consulterProduits();
        for (Produits produit : produits) {
            tableModel.addRow(new Object[]{
                produit.getIdProduit(),
                produit.getNomProduit(),
                produit.getDescriptionProduit(),
                produit.getPrixProduit(),
                produit.getQuantite(),
                produit.getIdFournisseur(),
                produit.getDateAjout()
            });
        }
    }

    private void ajouterProduit() {
        JTextField nomField = new JTextField();
        JTextField descriptionField = new JTextField();
        JTextField prixField = new JTextField();
        JTextField quantiteField = new JTextField();
        JTextField fournisseurField = new JTextField();

        Object[] message = {
            "Nom:", nomField,
            "Description:", descriptionField,
            "Prix:", prixField,
            "Quantité:", quantiteField,
            "ID Fournisseur:", fournisseurField
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Ajouter un produit", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                String nom = nomField.getText();
                String description = descriptionField.getText();
                double prix = Double.parseDouble(prixField.getText());
                int quantite = Integer.parseInt(quantiteField.getText());
                String idFournisseur = fournisseurField.getText();

                Produits nouveauProduit = new Produits(0, nom, description, prix, quantite, idFournisseur);
                produitDB.ajouterProduit(nouveauProduit);
                refreshTableData();
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Veuillez entrer des valeurs numériques valides pour le prix et la quantité.", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void modifierProduit() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un produit à modifier.", "Erreur", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idProduit = (int) tableModel.getValueAt(selectedRow, 0);
        
        JTextField nomField = new JTextField(tableModel.getValueAt(selectedRow, 1).toString());
        JTextField descriptionField = new JTextField(tableModel.getValueAt(selectedRow, 2).toString());
        JTextField prixField = new JTextField(tableModel.getValueAt(selectedRow, 3).toString());
        JTextField quantiteField = new JTextField(tableModel.getValueAt(selectedRow, 4).toString());
        JTextField fournisseurField = new JTextField(tableModel.getValueAt(selectedRow, 5).toString());

        Object[] message = {
            "Nom:", nomField,
            "Description:", descriptionField,
            "Prix:", prixField,
            "Quantité:", quantiteField,
            "ID Fournisseur:", fournisseurField
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Modifier un produit", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                String nom = nomField.getText();
                String description = descriptionField.getText();
                double prix = Double.parseDouble(prixField.getText());
                int quantite = Integer.parseInt(quantiteField.getText());
                String idFournisseur = fournisseurField.getText();

                Produits produitModifie = new Produits(idProduit, nom, description, prix, quantite, idFournisseur);
                produitDB.modifierProduit(produitModifie);
                refreshTableData();
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Veuillez entrer des valeurs numériques valides pour le prix et la quantité.", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void supprimerProduit() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un produit à supprimer.", "Erreur", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Convertir l'index de la ligne sélectionnée du view vers le model
        int modelRow = table.convertRowIndexToModel(selectedRow);
        
        // Récupérer l'ID et vérifier sa valeur
        Object idObj = tableModel.getValueAt(modelRow, 0);
        if (idObj == null) {
            JOptionPane.showMessageDialog(this, "ID du produit invalide.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int idProduit = Integer.parseInt(idObj.toString());
        if (idProduit <= 0) {
            JOptionPane.showMessageDialog(this, "ID du produit invalide: " + idProduit, "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirmation = JOptionPane.showConfirmDialog(this,
            "Êtes-vous sûr de vouloir supprimer le produit avec l'ID " + idProduit + " ?",
            "Confirmation de suppression",
            JOptionPane.YES_NO_OPTION);

        if (confirmation == JOptionPane.YES_OPTION) {
            try {
                produitDB.supprimerProduit(idProduit);
                refreshTableData();
                JOptionPane.showMessageDialog(this, "Produit supprimé avec succès.", "Succès", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                    "Erreur lors de la suppression du produit : " + e.getMessage(), 
                    "Erreur", 
                    JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }
}
