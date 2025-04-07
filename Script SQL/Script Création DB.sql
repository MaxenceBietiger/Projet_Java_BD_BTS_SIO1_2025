create database DB_Projet_Java;

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
