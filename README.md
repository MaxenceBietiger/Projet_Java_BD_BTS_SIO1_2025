# Projet_Java_BD_BTS_SIO1_2025

## Comment l'utiliser ?

Il suffit simplement de télécharger l'archive puis de la décompresser.

Il faut d'abord créer une base de données avec le script <strong>Script Création DB.sql</strong> via l'application MySQLWorkbench.

Ensuite, on se rend dans VSCode, on télécharge l'extension <strong>SQLTools MySQL/MariaDB/TiDB</strong>, et on renseigne le nom de la BD, l'ID de connexion ainsi que le mdp pour se connecter à la BD.

On retourne dans les extensions et on installe l'extension <strong>Extension Pack for Java</strong>.

Ensuite, on modifie le fichier <strong>/DB/DatabaseConnection</strong>, et on y modifie les variables URL, USER et PASSWORD en celles renseignées plus tôt lors de la création de la connection de notre BD sur MySQL Workbench.

Et enfin, on lance le fichier <strong>GUIGestionDeStock.java</strong>.

L'application est opérationelle.
