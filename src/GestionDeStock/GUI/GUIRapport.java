package GestionDeStock.GUI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import GestionDeStock.DB.DBRapports;
import GestionDeStock.Modele.Produits;
import GestionDeStock.Modele.Vente;

import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class GUIRapport extends JPanel {
    private DBRapports rapportDB;
    private JTable table;
    private DefaultTableModel tableModel;
    private JComboBox<Integer> jourDebut, moisDebut, anneeDebut;
    private JComboBox<Integer> jourFin, moisFin, anneeFin;
    private JLabel totalLabel;

    @SuppressWarnings("unused")
    public GUIRapport() {
        setLayout(new BorderLayout());
        
        rapportDB = new DBRapports();
        
        // Panel pour les contrôles
        JPanel controlPanel = new JPanel(new FlowLayout());
        
        // Création des ComboBox pour la date
        jourDebut = new JComboBox<>(getJours());
        moisDebut = new JComboBox<>(getMois());
        anneeDebut = new JComboBox<>(getAnnees());
        
        jourFin = new JComboBox<>(getJours());
        moisFin = new JComboBox<>(getMois());
        anneeFin = new JComboBox<>(getAnnees());

        // Panel pour la date de début
        JPanel dateDebutPanel = new JPanel(new FlowLayout());
        dateDebutPanel.add(new JLabel("Date début:"));
        dateDebutPanel.add(jourDebut);
        dateDebutPanel.add(moisDebut);
        dateDebutPanel.add(anneeDebut);

        // Panel pour la date de fin
        JPanel dateFinPanel = new JPanel(new FlowLayout());
        dateFinPanel.add(new JLabel("Date fin:"));
        dateFinPanel.add(jourFin);
        dateFinPanel.add(moisFin);
        dateFinPanel.add(anneeFin);

        controlPanel.add(dateDebutPanel);
        controlPanel.add(dateFinPanel);

        // Boutons pour les différents types de rapports
        JButton stockButton = new JButton("Rapport de Stock");
        JButton ventesButton = new JButton("Rapport de Ventes");
        JButton stockCritiqueButton = new JButton("Stock Critique (<10)");
        JButton exportButton = new JButton("Exporter en CSV");

        controlPanel.add(stockButton);
        controlPanel.add(ventesButton);
        controlPanel.add(stockCritiqueButton);
        controlPanel.add(exportButton);

        // Panel pour le tableau
        tableModel = new DefaultTableModel();
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);

        // Panel pour le total
        totalLabel = new JLabel("Total: 0.00 €");
        totalLabel.setFont(new Font("Arial", Font.BOLD, 14));
        JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        totalPanel.add(totalLabel);

        // Ajout des listeners
        stockButton.addActionListener(e -> afficherRapportStock());
        ventesButton.addActionListener(e -> afficherRapportVentes());
        stockCritiqueButton.addActionListener(e -> afficherStockCritique());
        exportButton.addActionListener(e -> exporterEnCSV());

        // Assemblage de l'interface
        add(controlPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(totalPanel, BorderLayout.SOUTH);
    }

    private Integer[] getJours() {
        Integer[] jours = new Integer[31];
        for (int i = 0; i < 31; i++) {
            jours[i] = i + 1;
        }
        return jours;
    }

    private Integer[] getMois() {
        Integer[] mois = new Integer[12];
        for (int i = 0; i < 12; i++) {
            mois[i] = i + 1;
        }
        return mois;
    }

    private Integer[] getAnnees() {
        Integer[] annees = new Integer[10];
        int anneeActuelle = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = 0; i < 10; i++) {
            annees[i] = anneeActuelle - i;
        }
        return annees;
    }

    private Date getDateDebut() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, (Integer) anneeDebut.getSelectedItem());
        cal.set(Calendar.MONTH, (Integer) moisDebut.getSelectedItem() - 1);
        cal.set(Calendar.DAY_OF_MONTH, (Integer) jourDebut.getSelectedItem());
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        return cal.getTime();
    }

    private Date getDateFin() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, (Integer) anneeFin.getSelectedItem());
        cal.set(Calendar.MONTH, (Integer) moisFin.getSelectedItem() - 1);
        cal.set(Calendar.DAY_OF_MONTH, (Integer) jourFin.getSelectedItem());
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        return cal.getTime();
    }

    private void afficherRapportStock() {
        ArrayList<Produits> produits = rapportDB.genererRapportProduits(getDateDebut(), getDateFin());
        tableModel.setColumnIdentifiers(new Object[]{"ID", "Nom", "Description", "Prix", "Quantité", "Fournisseur"});
        tableModel.setRowCount(0);
        
        double valeurTotale = 0.0;
        for (Produits produit : produits) {
            tableModel.addRow(new Object[]{
                produit.getIdProduit(),
                produit.getNomProduit(),
                produit.getDescriptionProduit(),
                produit.getPrixProduit(),
                produit.getQuantite(),
                produit.getIdFournisseur()
            });
            valeurTotale += produit.getPrixProduit() * produit.getQuantite();
        }
        totalLabel.setText(String.format("Valeur totale du stock: %.2f €", valeurTotale));
    }

    private void afficherRapportVentes() {
        ArrayList<Vente> ventes = rapportDB.genererRapportVentes(getDateDebut(), getDateFin());
        tableModel.setColumnIdentifiers(new Object[]{"ID", "Date", "Montant", "Produit"});
        tableModel.setRowCount(0);
        
        double totalVentes = 0.0;
        for (Vente vente : ventes) {
            tableModel.addRow(new Object[]{
                vente.getIdVente(),
                vente.getDateVente(),
                vente.getMontantVente(),
                vente.getIdProduit()
            });
            totalVentes += vente.getMontantVente();
        }
        totalLabel.setText(String.format("Total des ventes: %.2f €", totalVentes));
    }

    private void afficherStockCritique() {
        // Le stock critique n'a pas besoin de filtre de date car c'est une situation actuelle
        ArrayList<Produits> produits = rapportDB.genererRapportStock();
        tableModel.setColumnIdentifiers(new Object[]{"ID", "Nom", "Description", "Prix", "Quantité", "Fournisseur"});
        tableModel.setRowCount(0);
        
        for (Produits produit : produits) {
            if (produit.getQuantite() < 10) {
                tableModel.addRow(new Object[]{
                    produit.getIdProduit(),
                    produit.getNomProduit(),
                    produit.getDescriptionProduit(),
                    produit.getPrixProduit(),
                    produit.getQuantite(),
                    produit.getIdFournisseur()
                });
            }
        }
        totalLabel.setText(String.format("Nombre de produits en stock critique: %d", tableModel.getRowCount()));
    }

    private void exporterEnCSV() {
        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Aucune donnée à exporter", "Erreur", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Exporter en CSV");
        fileChooser.setSelectedFile(new File("rapport.csv"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            try (FileWriter writer = new FileWriter(fileToSave)) {
                // En-têtes
                for (int i = 0; i < tableModel.getColumnCount(); i++) {
                    writer.write(tableModel.getColumnName(i));
                    writer.write(i < tableModel.getColumnCount() - 1 ? ";" : "\n");
                }

                // Données
                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    for (int j = 0; j < tableModel.getColumnCount(); j++) {
                        writer.write(String.valueOf(tableModel.getValueAt(i, j)));
                        writer.write(j < tableModel.getColumnCount() - 1 ? ";" : "\n");
                    }
                }
                JOptionPane.showMessageDialog(this, "Export réussi!", "Succès", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Erreur lors de l'export: " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GUIRapport gui = new GUIRapport();
            gui.setVisible(true);
        });
    }
}
