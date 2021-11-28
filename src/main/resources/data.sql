INSERT INTO account (nom, prenom, no_passeport) VALUES
('Gobillard', 'Tom', '2');

INSERT INTO carte (id, code, crypto, bloque, localisation, plafond, sans_Contact, virtual, account_id) VALUES
(1234, 1222, 144, false, false, 500, true, false, '1');

INSERT INTO operation (date, heure, libelle, montant, taux, IBAN_crediteur, nom_Crediteur, categ, pays, compte_owner_id) VALUES
(CURRENT_DATE(), Current_Timestamp(), 'payement', 30, 1, '123', 'Georges', 'commerces', 'France', '1');