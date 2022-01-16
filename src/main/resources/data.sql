INSERT INTO Role (id, name)
VALUES(1, 'ROLE_USER');
VALUES(2, 'ROLE_ADMIN');


INSERT INTO user (id, nom, prenom, birth_date, no_passeport, num_tel, email, password) VALUES
('419ee53c-5a8d-11ec-bf63-0242ac130002','Jaman', 'Gael', '1999-05-21','1234AAA', '0102030405', 'gael@mail.com',  '$argon2id$v=19$m=4096,t=3,p=1$UpZ1oVVeUXwGb4CeqqMeog$aViAk5njkRuPGqY3+gkUNdUCIivAcP2Omvnm/MRBj7U'),
('419ee53c-5a8d-11ec-bf63-0242ac130003','Bristiel', 'Elouan',  '1999-08-25', '1234AAA', '0102030405', 'eloulou@mail.com',  'mdp');

INSERT INTO User_Roles  (user_id, roles_id)
VALUES('419ee53c-5a8d-11ec-bf63-0242ac130002', 1);

INSERT INTO account (iban, pays, secret, solde, user_id) VALUES
('132', 'France', '1122', 100, '419ee53c-5a8d-11ec-bf63-0242ac130002'),
('1323', 'Italie', '1122', 50, '419ee53c-5a8d-11ec-bf63-0242ac130002'),
('11222', 'France', '1122', 1050, '419ee53c-5a8d-11ec-bf63-0242ac130003');

INSERT INTO carte (id, numero, code, crypto, bloque, localisation, plafond, sans_contact, virtual, account_iban, date_expiration, deleted) VALUES
('1', '1234567890123456',1122, '231', false, false, 500, true, false, '132', current_date, false ),
('2', '1234567890123455',1122, '231', false, false, 500, true, false, '132', current_date, false );

INSERT INTO operation (id, date, libelle, montant, taux, debitor_account_iban, creditor_account_iban, categ) VALUES
('1', current_timestamp, 'loyer', 400, 1, '132', '11222', 'personne' );