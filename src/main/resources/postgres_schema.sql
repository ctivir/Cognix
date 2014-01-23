ALTER TABLE users ADD UNIQUE (login);
ALTER TABLE documents ADD UNIQUE (obaa_entry);

-- Insert users
INSERT INTO users (login, password, name, permissions, role)
    VALUES ('admin', md5('admin'), 'Administrador Geral', 'PERM_CREATE_DOC,PERM_MANAGE_DOC,PERM_VIEW,PERM_MANAGE_USERS', 'root');

ALTER TABLE documents OWNER TO cognitiva;
ALTER TABLE files OWNER TO cognitiva;
ALTER TABLE users OWNER TO cognitiva;
ALTER TABLE subject OWNER TO cognitiva;