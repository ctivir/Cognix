--
-- PostgreSQL database dump
--

-- Dumped from database version 9.1.3
-- Dumped by pg_dump version 9.1.3
-- Started on 2012-05-22 14:59:23 BRT

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

--
-- TOC entry 1926 (class 1262 OID 20310)
-- Name: repositorio; Type: DATABASE; Schema: -; Owner: cognitiva
--

CREATE DATABASE repositorio WITH TEMPLATE = template0 ENCODING = 'UTF8' LC_COLLATE = 'pt_BR.UTF-8' LC_CTYPE = 'pt_BR.UTF-8';


ALTER DATABASE repositorio OWNER TO cognitiva;

\connect repositorio

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

--
-- TOC entry 167 (class 3079 OID 11685)
-- Name: plpgsql; Type: EXTENSION; Schema: -; Owner: 
--

CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;


--
-- TOC entry 1929 (class 0 OID 0)
-- Dependencies: 167
-- Name: EXTENSION plpgsql; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';


SET search_path = public, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- TOC entry 164 (class 1259 OID 20337)
-- Dependencies: 1909 1910 5
-- Name: documents; Type: TABLE; Schema: public; Owner: cognitiva; Tablespace: 
--

CREATE TABLE documents (
    id integer NOT NULL,
    obaa_entry character varying NOT NULL,
    "timestamp" timestamp without time zone DEFAULT now(),
    deleted boolean DEFAULT false NOT NULL,
    owner integer,
    obaaxml character varying
);


ALTER TABLE public.documents OWNER TO cognitiva;

--
-- TOC entry 163 (class 1259 OID 20335)
-- Dependencies: 164 5
-- Name: documentos_id_seq; Type: SEQUENCE; Schema: public; Owner: cognitiva
--

CREATE SEQUENCE documents_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
  CACHE 1;


ALTER TABLE public.documents_id_seq OWNER TO cognitiva;

--
-- TOC entry 1930 (class 0 OID 0)
-- Dependencies: 163
-- Name: documentos_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: cognitiva
--

ALTER SEQUENCE documents_id_seq OWNED BY documents.id;


--
-- TOC entry 1931 (class 0 OID 0)
-- Dependencies: 163
-- Name: documentos_id_seq; Type: SEQUENCE SET; Schema: public; Owner: cognitiva
--

SELECT pg_catalog.setval('documents_id_seq', 100, false);


--
-- TOC entry 166 (class 1259 OID 20395)
-- Dependencies: 5
-- Name: files; Type: TABLE; Schema: public; Owner: cognitiva; Tablespace: 
--

CREATE TABLE files (
    id integer NOT NULL,
    name character varying NOT NULL,
    random_name character varying,
    content_type character varying,
    size bigint,
    location character varying NOT NULL,
    document integer NOT NULL
);


ALTER TABLE public.files OWNER TO cognitiva;

--
-- TOC entry 1932 (class 0 OID 0)
-- Dependencies: 166
-- Name: TABLE files; Type: COMMENT; Schema: public; Owner: cognitiva
--

COMMENT ON TABLE files IS 'files relating to documents';


--
-- TOC entry 165 (class 1259 OID 20393)
-- Dependencies: 5 166
-- Name: files_id_seq; Type: SEQUENCE; Schema: public; Owner: cognitiva
--

CREATE SEQUENCE files_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.files_id_seq OWNER TO cognitiva;

--
-- TOC entry 1933 (class 0 OID 0)
-- Dependencies: 165
-- Name: files_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: cognitiva
--

ALTER SEQUENCE files_id_seq OWNED BY files.id;


--
-- TOC entry 1934 (class 0 OID 0)
-- Dependencies: 165
-- Name: files_id_seq; Type: SEQUENCE SET; Schema: public; Owner: cognitiva
--

SELECT pg_catalog.setval('files_id_seq', 100, false);


--
-- TOC entry 162 (class 1259 OID 20329)
-- Dependencies: 5
-- Name: users; Type: TABLE; Schema: public; Owner: cognitiva; Tablespace: 
--

CREATE TABLE users (
    id integer NOT NULL,
    login character varying(45) NOT NULL,
    password character varying NOT NULL,
    name character varying(100) NOT NULL,
    permissions character varying(200),
    role character varying(20),
  CONSTRAINT users_login_key UNIQUE (login )
);


ALTER TABLE public.users OWNER TO cognitiva;

--
-- TOC entry 161 (class 1259 OID 20327)
-- Dependencies: 5 162
-- Name: usuarios_id_seq; Type: SEQUENCE; Schema: public; Owner: cognitiva
--

CREATE SEQUENCE users_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.users_id_seq OWNER TO cognitiva;

--
-- TOC entry 1935 (class 0 OID 0)
-- Dependencies: 161
-- Name: usuarios_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: cognitiva
--

ALTER SEQUENCE users_id_seq OWNED BY users.id;


--
-- TOC entry 1936 (class 0 OID 0)
-- Dependencies: 161
-- Name: usuarios_id_seq; Type: SEQUENCE SET; Schema: public; Owner: cognitiva
--

SELECT pg_catalog.setval('users_id_seq', 100, false);


--
-- TOC entry 1908 (class 2604 OID 20340)
-- Dependencies: 163 164 164
-- Name: id; Type: DEFAULT; Schema: public; Owner: cognitiva
--

ALTER TABLE ONLY documents ALTER COLUMN id SET DEFAULT nextval('documents_id_seq'::regclass);


--
-- TOC entry 1911 (class 2604 OID 20398)
-- Dependencies: 166 165 166
-- Name: id; Type: DEFAULT; Schema: public; Owner: cognitiva
--

ALTER TABLE ONLY files ALTER COLUMN id SET DEFAULT nextval('files_id_seq'::regclass);


--
-- TOC entry 1907 (class 2604 OID 20332)
-- Dependencies: 162 161 162
-- Name: id; Type: DEFAULT; Schema: public; Owner: cognitiva
--

ALTER TABLE ONLY users ALTER COLUMN id SET DEFAULT nextval('users_id_seq'::regclass);


--
-- TOC entry 1922 (class 0 OID 20337)
-- Dependencies: 164
-- Data for Name: documents; Type: TABLE DATA; Schema: public; Owner: cognitiva
--



--
-- TOC entry 1923 (class 0 OID 20395)
-- Dependencies: 166
-- Data for Name: files; Type: TABLE DATA; Schema: public; Owner: cognitiva
--



--
-- TOC entry 1921 (class 0 OID 20329)
-- Dependencies: 162
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: cognitiva
--



--
-- TOC entry 1915 (class 2606 OID 20347)
-- Dependencies: 164 164
-- Name: documentos_pkey; Type: CONSTRAINT; Schema: public; Owner: cognitiva; Tablespace: 
--

ALTER TABLE ONLY documents
    ADD CONSTRAINT documents_pkey PRIMARY KEY (id);


--
-- TOC entry 1919 (class 2606 OID 20403)
-- Dependencies: 166 166
-- Name: files_pkey; Type: CONSTRAINT; Schema: public; Owner: cognitiva; Tablespace: 
--

ALTER TABLE ONLY files
    ADD CONSTRAINT files_pkey PRIMARY KEY (id);


--
-- TOC entry 1917 (class 2606 OID 20365)
-- Dependencies: 164 164
-- Name: uni_obaaentry; Type: CONSTRAINT; Schema: public; Owner: cognitiva; Tablespace: 
--

ALTER TABLE ONLY documents
    ADD CONSTRAINT uni_obaaentry UNIQUE (obaa_entry);


--
-- TOC entry 1913 (class 2606 OID 20334)
-- Dependencies: 162 162
-- Name: usuarios_pkey; Type: CONSTRAINT; Schema: public; Owner: cognitiva; Tablespace: 
--

ALTER TABLE ONLY users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);


--
-- TOC entry 1920 (class 2606 OID 20404)
-- Dependencies: 1914 166 164
-- Name: fk_documents; Type: FK CONSTRAINT; Schema: public; Owner: cognitiva
--

ALTER TABLE ONLY files
    ADD CONSTRAINT fk_documents FOREIGN KEY (document) REFERENCES documents(id) ON UPDATE CASCADE ON DELETE CASCADE;


ALTER TABLE documents 
    ADD CONSTRAINT owner_pkey FOREIGN KEY (owner) REFERENCES users (id) ON UPDATE NO ACTION ON DELETE SET NULL;
--
-- TOC entry 1928 (class 0 OID 0)
-- Dependencies: 5
-- Name: public; Type: ACL; Schema: -; Owner: postgres
--

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM postgres;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;


-- Insert users

INSERT INTO users (login, password, name, permissions, role)
    VALUES ('admin', md5('admin'), 'Administrador Geral', 'PERM_CREATE_DOC,PERM_MANAGE_DOC,PERM_VIEW,PERM_MANAGE_USERS', 'root');

