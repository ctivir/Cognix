CREATE TABLE users (
    id serial NOT NULL,
    login text NOT NULL,
    password text NOT NULL,
    name text NOT NULL,
    permissions text,
    role text,
    deleted boolean DEFAULT false NOT NULL,
    PRIMARY KEY (id),
    UNIQUE (login)
);

CREATE TABLE subject (
    id serial NOT NULL,
    name text NOT NULL,    
    PRIMARY KEY (id)
);

CREATE TABLE documents (
    id serial NOT NULL,
    obaa_entry text NOT NULL,
    created timestamp DEFAULT now(),
    deleted boolean DEFAULT false NOT NULL,
    active boolean NOT NULL DEFAULT false,
    owner integer,
    obaaxml text,
    subject integer,
    PRIMARY KEY (id),
    FOREIGN KEY(owner) REFERENCES users(id) ON UPDATE NO ACTION ON DELETE SET NULL,
    FOREIGN KEY (subject) REFERENCES subject(id) ON UPDATE CASCADE ON DELETE SET NULL
);

CREATE TABLE files (
    id serial NOT NULL,
    name text NOT NULL,
    random_name text,
    content_type text,
    file_size bigint,
    location text NOT NULL,
    document integer,
    PRIMARY KEY (id),
    FOREIGN KEY (document) REFERENCES documents(id) ON UPDATE CASCADE ON DELETE CASCADE
);
