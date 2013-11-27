INSERT INTO users (id,name,login,role,permissions,password) VALUES (1, 'Administrador','admin','admin','PERM_CREATE_DOC,PERM_MANAGE_DOC,PERM_VIEW,PERM_MANAGE_USERS','698dc19d489c4e4db73e28a713eab07b');
INSERT INTO users (id,name,login,role,permissions,password) VALUES (2, 'Marcos Nunes', 'marcos','admin','PERM_CREATE_DOC,PERM_MANAGE_DOC,PERM_VIEW,PERM_MANAGE_USERS','698dc19d489c4e4db73e28a713eab07b');
INSERT INTO users (id,name,login,role,permissions,password) VALUES (3, 'Noob', 'autor','author','PERM_CREATE_DOC','698dc19d489c4e4db73e28a713eab07b');

INSERT INTO documents (id, obaa_entry,created, owner, obaaxml) VALUES (1, 'entry1', '2013-05-08 03:00:00', 2, '<obaa:obaa xsi:schemaLocation="http://ltsc.ieee.org/xsd/LOM http://ltsc.ieee.org/xsd/obaav1.0/lom.xsd" xmlns:obaa="http://ltsc.ieee.org/xsd/LOM" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
   <obaa:general><obaa:title>Ataque a o TCP - Mitnick</obaa:title><obaa:keyword>TCP</obaa:keyword><obaa:structure>atomic</obaa:structure></obaa:general></obaa:obaa>');

INSERT INTO documents (id, obaa_entry,created,deleted) VALUES (2, 'entry2', '2013-05-08',true);

INSERT INTO documents (id, obaa_entry,created) VALUES (3, 'entry3', '2999-07-10 21:41:00');

INSERT INTO documents (id, obaa_entry,created) VALUES (4, 'entry4', '2013-07-10 21:41:10');

INSERT INTO documents (id, obaa_entry,created, owner, obaaxml) VALUES (5, 'entry5', '2013-05-08 03:10:00', 3, '<obaa:obaa xsi:schemaLocation="http://ltsc.ieee.org/xsd/LOM http://ltsc.ieee.org/xsd/obaav1.0/lom.xsd" xmlns:obaa="http://ltsc.ieee.org/xsd/LOM" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
   <obaa:general><obaa:title>Ataque a o TCP - Mitnick</obaa:title><obaa:keyword>TCP</obaa:keyword><obaa:structure>atomic</obaa:structure></obaa:general></obaa:obaa>');

INSERT INTO files (id, name,content_type, file_size, location, document) VALUES (2,'teste2.txt','text/plain',42,'diretorio-nao-existe',1);
INSERT INTO files (id, name,content_type, file_size, location, document) VALUES (1,'teste.txt','text/plain',42,'diretorio-nao-existe',1);
INSERT INTO files (id, name,content_type, file_size, location, document) VALUES (3,'teste3.txt','text/plain',42,'/temp/1fde1am3.pdf',2);
INSERT INTO files (id, name,content_type, file_size, location, document) VALUES (4,'file.test','text/plain',5,'./src/test/resources/files/file.test',5);
