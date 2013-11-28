
CREATE TABLE documents (
    id serial NOT NULL,
    obaa_entry text NOT NULL,
    created timestamp DEFAULT now(),
    deleted boolean DEFAULT false NOT NULL,
    owner integer,
    obaaxml text,
    subject integer,
    PRIMARY KEY (id)
);


CREATE TABLE files (
    id serial NOT NULL,
    name text NOT NULL,
    random_name text,
    content_type text,
    file_size bigint,
    location text NOT NULL,
    document integer,
    PRIMARY KEY (id)
);


CREATE TABLE users (
    id serial NOT NULL,
    login text NOT NULL,
    password text NOT NULL,
    name text NOT NULL,
    permissions text,
    role text,
    PRIMARY KEY (id)
);

CREATE TABLE subject (
    id serial NOT NULL,
    name text NOT NULL,    
    PRIMARY KEY (id)
);