ALTER TABLE documents OWNER TO cognitiva;
ALTER TABLE files OWNER TO cognitiva;
ALTER TABLE users OWNER TO cognitiva;
ALTER TABLE subject OWNER TO cognitiva;

ALTER TABLE users ADD UNIQUE (login);
ALTER TABLE documents ADD UNIQUE (obaa_entry);

ALTER TABLE ONLY files
    ADD CONSTRAINT fk_documents FOREIGN KEY (document) REFERENCES documents(id) ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE documents 
    ADD CONSTRAINT owner_pkey FOREIGN KEY (owner) REFERENCES users (id) ON UPDATE NO ACTION ON DELETE SET NULL;
ALTER TABLE ONLY documents
    ADD CONSTRAINT fk_subject FOREIGN KEY (subject) REFERENCES subject(id) ON UPDATE CASCADE ON DELETE SET NULL;

-- Insert users
INSERT INTO users (login, password, name, permissions, role)
    VALUES ('admin', md5('admin'), 'Administrador Geral', 'PERM_CREATE_DOC,PERM_MANAGE_DOC,PERM_VIEW,PERM_MANAGE_USERS', 'root');