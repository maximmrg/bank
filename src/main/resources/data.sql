INSERT INTO user (nom, prenom, birth_date, no_passeport, num_tel, email, password) VALUES
('Jaman', 'Gael', '1999-05-21','1234AAA', '0102030405', 'gael@mail.com',  'mdpdemerde'),
('Bristiel', 'Elouan',  '1999-08-25', '1234AAA', '0102030405', 'eloulou@mail.com',  'mdpdemerde');

INSERT INTO account (iban, pays, secret, solde, user_id) VALUES
('132', 'France', '1122', 100, 1),
('1323', 'Italie', '1122', 50, 1),
('11222', 'France', '1122', 1050, 2);

INSERT INTO carte (code, crypto, bloque, localisation, plafond, sans_contact, virtual, account_iban) VALUES
(1122, 231, false, false, 500, true, false, '132')
