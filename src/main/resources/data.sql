INSERT INTO Role (id, name)
VALUES(1, 'ROLE_USER'),
(2, 'ROLE_ADMIN');


INSERT INTO user (id, nom, prenom, birth_date, no_passeport, num_tel, email, password) VALUES
('419ee53c-5a8d-11ec-bf63-0242ac130002','Jaman', 'Gael', '1999-05-21','12YH5456897', '0102030405', 'gael@mail.com',  '$argon2id$v=19$m=4096,t=3,p=1$UpZ1oVVeUXwGb4CeqqMeog$aViAk5njkRuPGqY3+gkUNdUCIivAcP2Omvnm/MRBj7U'),
('419ee53c-5a8d-11ec-bf63-0242ac130003','Bristiel', 'Elouan',  '1999-08-25', '12AH5136897', '0102030405', 'eloulou@mail.com', '$argon2id$v=19$m=4096,t=3,p=1$UpZ1oVVeUXwGb4CeqqMeog$aViAk5njkRuPGqY3+gkUNdUCIivAcP2Omvnm/MRBj7U'),
('419ee53c-5a8d-55ec-bf63-0242ac130253','Admin', 'Admin',  '1999-08-25', '12AO5132897', '0102030405', 'admin@mail.com', '$argon2id$v=19$m=4096,t=3,p=1$UpZ1oVVeUXwGb4CeqqMeog$aViAk5njkRuPGqY3+gkUNdUCIivAcP2Omvnm/MRBj7U');

INSERT INTO User_Roles  (user_id, roles_id)
VALUES('419ee53c-5a8d-11ec-bf63-0242ac130002', 1),
      ('419ee53c-5a8d-11ec-bf63-0242ac130003', 1),
       ('419ee53c-5a8d-55ec-bf63-0242ac130253', 2);

INSERT INTO account (iban, pays, secret, solde, user_id) VALUES
('FR8130003000509791649553M04', 'France', '123456', 100, '419ee53c-5a8d-11ec-bf63-0242ac130002'),
('IT69C0300203280436212222177', 'Italie', '1234567', 50, '419ee53c-5a8d-11ec-bf63-0242ac130002'),
('FR2710096000305544338445J02', 'France', '12345678', 1050, '419ee53c-5a8d-11ec-bf63-0242ac130003');

INSERT INTO carte (id, numero, code, crypto, bloque, localisation, plafond, sans_contact, virtual, account_iban, date_expiration, deleted) VALUES
('c1', '1234567890123456',1122, '231', false, false, 500, true, false, 'FR8130003000509791649553M04', current_date, false ),
('c2', '1234567890123455',1122, '231', false, false, 500, true, false, 'FR8130003000509791649553M04', current_date, false ),
('c3', '1234567890123488',1122, '221', false, false, 500, true, false, 'IT69C0300203280436212222177', current_date, false );

INSERT INTO operation (id, date, libelle, montant, taux, debitor_account_iban, creditor_account_iban, categ) VALUES
('ope1', current_timestamp, 'loyer', 400, 1, 'FR8130003000509791649553M04', 'FR2710096000305544338445J02', 'personne' );