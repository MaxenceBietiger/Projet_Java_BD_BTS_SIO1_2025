package GestionDeStock.GUI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

import GestionDeStock.DB.DBVente;
import GestionDeStock.Modele.Vente;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.SQLException;

import GestionDeStock.DB.DatabaseConnection;

public class GUIVentes extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private TableRowSorter<DefaultTableModel> sorter;
    private DBVente venteDB;

    public GUIVentes() {
        setLayout(new BorderLayout());
        
        // Initialiser la connexion à la base de données
        try (@SuppressWarnings("unused")
        Connection conn = DatabaseConnection.getConnection()) {
            System.out.println("Connexion à la base de données réussie!");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur de connexion à la base de données.", "Erreur", JOptionPane.ERROR_MESSAGE);
        }

        venteDB = new DBVente();
        
        // Initialiser le tableau
        String[] columnNames = {"ID", "Date", "Montant", "ID Produit"};
        tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel);

        // Ajouter les ventes au modèle de tableau
        ArrayList<Vente> ventes = venteDB.consulterVentes();
        for (Vente vente : ventes) {
            tableModel.addRow(new Object[]{vente.getIdVente(), vente.getDateVente(), vente.getMontantVente(), vente.getIdProduit()});
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
        add(panel, BorderLayout.CENTER);

        // Ajouter des boutons pour ajouter, modifier et supprimer des ventes
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton addButton = new JButton("Ajouter");
        JButton editButton = new JButton("Modifier");
        JButton deleteButton = new JButton("Supprimer");

        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ajouterVente();
            }
        });

        editButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                modifierVente();
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                supprimerVente();
            }
        });

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        add(buttonPanel, BorderLayout.NORTH);
    }

    private void ajouterVente() {
        try {
            // Créer le panneau de formulaire
            JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
            panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            
            JTextField dateField = new JTextField(10);
            JTextField montantField = new JTextField(10);
            JTextField produitIdField = new JTextField(10);
            
            // Pré-remplir la date avec la date actuelle
            dateField.setText(new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date()));

            panel.add(new JLabel("Date (YYYY-MM-DD):"));
            panel.add(dateField);
            panel.add(new JLabel("Montant:"));
            panel.add(montantField);
            panel.add(new JLabel("ID Produit:"));
            panel.add(produitIdField);

            int result = JOptionPane.showConfirmDialog(
                this,
                panel,
                "Ajouter une vente",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
            );

            if (result == JOptionPane.OK_OPTION) {
                // Validation des entrées
                String date = dateField.getText().trim();
                if (!date.matches("\\d{4}-\\d{2}-\\d{2}")) {
                    throw new IllegalArgumentException("Format de date invalide. Utilisez YYYY-MM-DD");
                }

                double montant = Double.parseDouble(montantField.getText().trim());
                if (montant <= 0) {
                    throw new IllegalArgumentException("Le montant doit être supérieur à 0");
                }

                int produitId = Integer.parseInt(produitIdField.getText().trim());
                if (produitId <= 0) {
                    throw new IllegalArgumentException("L'ID du produit doit être un nombre positif");
                }

                // Créer et ajouter la vente
                Vente nouvelleVente = new Vente(0, date, montant, produitId);
                int newId = venteDB.ajouterVente(nouvelleVente);
                
                if (newId != -1) {
                    refreshTableData();
                    JOptionPane.showMessageDialog(
                        this,
                        "Vente ajoutée avec succès.",
                        "Succès",
                        JOptionPane.INFORMATION_MESSAGE
                    );
                }
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(
                this,
                "Veuillez entrer des valeurs numériques valides pour le montant et l'ID du produit.",
                "Erreur de saisie",
                JOptionPane.ERROR_MESSAGE
            );
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(
                this,
                e.getMessage(),
                "Erreur de validation",
                JOptionPane.ERROR_MESSAGE
            );
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                this,
                "Erreur lors de l'ajout de la vente : " + e.getMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE
            );
            e.printStackTrace();
        }
    }

    private void modifierVente() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            int id = (int) tableModel.getValueAt(selectedRow, 0);
            String date = JOptionPane.showInputDialog("Nouvelle date de la vente (YYYY-MM-DD) :", tableModel.getValueAt(selectedRow, 1));
            String montantStr = JOptionPane.showInputDialog("Nouveau montant de la vente :", tableModel.getValueAt(selectedRow, 2));
            String produitIdStr = JOptionPane.showInputDialog("Nouvel ID du produit :", tableModel.getValueAt(selectedRow, 3));

            if (date != null && !date.trim().isEmpty() && montantStr != null && !montantStr.trim().isEmpty() && produitIdStr != null && !produitIdStr.trim().isEmpty()) {
                try {
                    double montant = Double.parseDouble(montantStr);
                    int produitId = Integer.parseInt(produitIdStr);
                    Vente venteModifiee = new Vente(id, date, montant, produitId);
                    venteDB.modifierVente(venteModifiee);
                    tableModel.setValueAt(date, selectedRow, 1);
                    tableModel.setValueAt(montant, selectedRow, 2);
                    tableModel.setValueAt(produitId, selectedRow, 3);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Veuillez entrer des valeurs numériques valides.", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void supprimerVente() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Veuillez sélectionner une vente à supprimer.", 
                "Erreur", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Convertir l'index de la ligne sélectionnée du view vers le model
        int modelRow = table.convertRowIndexToModel(selectedRow);
        int idVente = (int) tableModel.getValueAt(modelRow, 0);
        String dateVente = (String) tableModel.getValueAt(modelRow, 1);
        double montantVente = (double) tableModel.getValueAt(modelRow, 2);

        int confirmation = JOptionPane.showConfirmDialog(this,
            "Êtes-vous sûr de vouloir supprimer la vente du " + dateVente + 
            " d'un montant de " + montantVente + " € ?",
            "Confirmation de suppression",
            JOptionPane.YES_NO_OPTION);

        if (confirmation == JOptionPane.YES_OPTION) {
            try {
                venteDB.supprimerVente(idVente);
                tableModel.removeRow(modelRow);
                JOptionPane.showMessageDialog(this, 
                    "Vente supprimée avec succès.", 
                    "Succès", 
                    JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                    "Erreur lors de la suppression de la vente : " + e.getMessage(), 
                    "Erreur", 
                    JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }

    // Méthode utilitaire pour rafraîchir les données du tableau
    private void refreshTableData() {
        tableModel.setRowCount(0);
        ArrayList<Vente> ventes = venteDB.consulterVentes();
        for (Vente vente : ventes) {
            tableModel.addRow(new Object[]{
                vente.getIdVente(),
                vente.getDateVente(),
                vente.getMontantVente(),
                vente.getIdProduit()
            });
        }
    }
}
