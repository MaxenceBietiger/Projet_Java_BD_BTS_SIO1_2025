package GestionDeStock;

import javax.swing.*;

import GestionDeStock.GUI.GUIFournisseurs;
import GestionDeStock.GUI.GUIProduit;
import GestionDeStock.GUI.GUIRapport;
import GestionDeStock.GUI.GUIVentes;

import java.awt.*;
import java.sql.SQLException;

public class GUIGestionDeStock extends JFrame {
    private JTabbedPane tabbedPane;

    public GUIGestionDeStock() throws SQLException {
        setTitle("Gestion de Stock");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Créer les onglets pour chaque fonctionnalité
        tabbedPane = new JTabbedPane();

        // Ajouter les interfaces pour chaque fonctionnalité
        tabbedPane.addTab("Produits", new GUIProduit());
        tabbedPane.addTab("Fournisseurs", new GUIFournisseurs());
        tabbedPane.addTab("Ventes", new GUIVentes());
        tabbedPane.addTab("Rapports", new GUIRapport());

        add(tabbedPane, BorderLayout.CENTER);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GUIGestionDeStock gui = null;
			try {
				gui = new GUIGestionDeStock();
			} catch (SQLException e) {
				e.printStackTrace();
			}
            gui.setVisible(true);
        });
    }
}
