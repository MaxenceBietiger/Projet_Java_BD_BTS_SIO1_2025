# Modification de la table produits pour que chaque produit ait un fournisseur
ALTER TABLE produits ADD COLUMN idFournisseur VARCHAR(255);

# Modification de la table vente pour que chaque vente ait un produit
ALTER TABLE vente ADD COLUMN idProduit INT;

# Modification de la table produits pour que chaque produit ait une quantité
ALTER TABLE produits ADD COLUMN quantite INT DEFAULT 0;

create database DB_Projet_Java;

DROP TABLE vente;

CREATE TABLE produits (
    idProduits INT PRIMARY KEY AUTO_INCREMENT,
    nomProduit varchar(50),
    descriptionProduit varchar(50),
    prixProduit float,
    quantite INT DEFAULT 0,
    idFournisseur VARCHAR(255),
    dateAjout DATE DEFAULT (CURRENT_DATE())
);

create table vente (
    idVente INT PRIMARY KEY AUTO_INCREMENT,
    dateVente DATE,
    montantVente DOUBLE NOT NULL,
    idProduit INT,
    FOREIGN KEY (idProduit) REFERENCES produits(idProduits)
);

CREATE TABLE fournisseurs (
    idFournisseur VARCHAR(255) PRIMARY KEY,
    nomFournisseur VARCHAR(255) NOT NULL
);

create table rapport (
		idRapport int,
        dateRapport varchar(50),
        contenuRapport varchar(50)
);