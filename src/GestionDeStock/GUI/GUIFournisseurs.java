package GestionDeStock.GUI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

import GestionDeStock.DB.DBFournisseurs;
import GestionDeStock.Fournisseurs;
import GestionDeStock.DB.DatabaseConnection;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

public class GUIFournisseurs extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private TableRowSorter<DefaultTableModel> sorter;
    private DBFournisseurs fournisseursBD;

    public GUIFournisseurs() {
        setLayout(new BorderLayout());
        setSize(600, 400);

        // Initialiser la connexion à la base de données
        try (@SuppressWarnings("unused")
        Connection conn = DatabaseConnection.getConnection()) {
            System.out.println("Connexion à la base de données réussie!");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur de connexion à la base de données.", "Erreur", JOptionPane.ERROR_MESSAGE);
        }

        // Initialiser la DB Fournisseurs
        fournisseursBD = new DBFournisseurs();

        // Créer le modèle de tableau
        tableModel = new DefaultTableModel(new Object[]{"ID", "Nom"}, 0);
        table = new JTable(tableModel);

        // Ajouter les fournisseurs au modèle de tableau
        ArrayList<Fournisseurs> fournisseurs = fournisseursBD.consulterFournisseurs();
        for (Fournisseurs fournisseur : fournisseurs) {
            tableModel.addRow(new Object[]{fournisseur.getIdFournisseur(), fournisseur.getNomFournisseur()});
        }

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

        // Ajouter le tableau et le champ de recherche à la fenêtre
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(searchField, BorderLayout.SOUTH);
        add(panel);

        // Ajouter des boutons pour ajouter, modifier et supprimer des fournisseurs
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton addButton = new JButton("Ajouter");
        JButton editButton = new JButton("Modifier");
        JButton deleteButton = new JButton("Supprimer");

        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ajouterFournisseur();
            }
        });

        editButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                modifierFournisseur();
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                supprimerFournisseur();
            }
        });

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        add(buttonPanel, BorderLayout.NORTH);
    }

    private void ajouterFournisseur() {
        String id = JOptionPane.showInputDialog("ID du fournisseur :");
        String nom = JOptionPane.showInputDialog("Nom du fournisseur :");
        if (id != null && !id.trim().isEmpty() && nom != null && !nom.trim().isEmpty()) {
            Fournisseurs nouveauFournisseur = new Fournisseurs(id, nom);
            fournisseursBD.ajouterFournisseur(nouveauFournisseur);
            tableModel.addRow(new Object[]{id, nom});
        }
    }

    private void modifierFournisseur() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            String id = (String) tableModel.getValueAt(selectedRow, 0);
            String nom = JOptionPane.showInputDialog("Nouveau nom du fournisseur :", tableModel.getValueAt(selectedRow, 1));
            if (nom != null && !nom.trim().isEmpty()) {
                Fournisseurs fournisseurModifie = new Fournisseurs(id, nom);
                fournisseursBD.modifierFournisseur(fournisseurModifie);
                tableModel.setValueAt(nom, selectedRow, 1);
            }
        }
    }

    private void supprimerFournisseur() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un fournisseur à supprimer.", "Erreur", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Convertir l'index de la ligne sélectionnée du view vers le model
        int modelRow = table.convertRowIndexToModel(selectedRow);
        String idFournisseur = (String) tableModel.getValueAt(modelRow, 0);
        String nomFournisseur = (String) tableModel.getValueAt(modelRow, 1);

        // Vérifier si l'ID est valide
        if (idFournisseur == null || idFournisseur.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "ID du fournisseur invalide.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        System.out.println("Tentative de suppression du fournisseur - ID: " + idFournisseur + ", Nom: " + nomFournisseur); // Debug

        int confirmation = JOptionPane.showConfirmDialog(this,
            "Êtes-vous sûr de vouloir supprimer le fournisseur " + nomFournisseur + " (ID: " + idFournisseur + ") ?",
            "Confirmation de suppression",
            JOptionPane.YES_NO_OPTION);

        if (confirmation == JOptionPane.YES_OPTION) {
            try {
                fournisseursBD.supprimerFournisseur(idFournisseur);
                refreshTableData();
                JOptionPane.showMessageDialog(this, "Fournisseur supprimé avec succès.", "Succès", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                String message = "Erreur lors de la suppression du fournisseur : " + e.getMessage();
                System.err.println(message); // Debug
                JOptionPane.showMessageDialog(this, message, "Erreur", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }

    private void refreshTableData() {
        tableModel.setRowCount(0);
        ArrayList<Fournisseurs> fournisseurs = fournisseursBD.consulterFournisseurs();
        for (Fournisseurs fournisseur : fournisseurs) {
            tableModel.addRow(new Object[]{
                fournisseur.getIdFournisseur(),
                fournisseur.getNomFournisseur()
            });
        }
    }

    // Supprimer la méthode main()
}
