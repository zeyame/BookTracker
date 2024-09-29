--
-- PostgreSQL database dump
--

-- Dumped from database version 16.4
-- Dumped by pg_dump version 16.4

-- Started on 2024-09-29 12:05:57

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- TOC entry 217 (class 1259 OID 24759)
-- Name: otp_verifications; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.otp_verifications (
    id integer NOT NULL,
    otp character varying(255) NOT NULL,
    expiration_time timestamp without time zone NOT NULL,
    used boolean DEFAULT false,
    email character varying(255),
    username character varying(255)
);


ALTER TABLE public.otp_verifications OWNER TO postgres;

--
-- TOC entry 216 (class 1259 OID 24758)
-- Name: otp_verifications_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.otp_verifications_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.otp_verifications_id_seq OWNER TO postgres;

--
-- TOC entry 4803 (class 0 OID 0)
-- Dependencies: 216
-- Name: otp_verifications_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.otp_verifications_id_seq OWNED BY public.otp_verifications.id;


--
-- TOC entry 215 (class 1259 OID 24593)
-- Name: users; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.users (
    username character varying(255) NOT NULL,
    email character varying(255) NOT NULL,
    hashed_password character varying(255) NOT NULL,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);


ALTER TABLE public.users OWNER TO postgres;

--
-- TOC entry 4640 (class 2604 OID 24762)
-- Name: otp_verifications id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.otp_verifications ALTER COLUMN id SET DEFAULT nextval('public.otp_verifications_id_seq'::regclass);


--
-- TOC entry 4797 (class 0 OID 24759)
-- Dependencies: 217
-- Data for Name: otp_verifications; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.otp_verifications (id, otp, expiration_time, used, email, username) FROM stdin;
\.


--
-- TOC entry 4795 (class 0 OID 24593)
-- Dependencies: 215
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.users (username, email, hashed_password, created_at, updated_at) FROM stdin;
\.


--
-- TOC entry 4804 (class 0 OID 0)
-- Dependencies: 216
-- Name: otp_verifications_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.otp_verifications_id_seq', 213, true);


--
-- TOC entry 4649 (class 2606 OID 24765)
-- Name: otp_verifications otp_verifications_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.otp_verifications
    ADD CONSTRAINT otp_verifications_pkey PRIMARY KEY (id);


--
-- TOC entry 4651 (class 2606 OID 24785)
-- Name: otp_verifications unique_email; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.otp_verifications
    ADD CONSTRAINT unique_email UNIQUE (email);


--
-- TOC entry 4643 (class 2606 OID 24749)
-- Name: users user_email_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT user_email_key UNIQUE (email);


--
-- TOC entry 4645 (class 2606 OID 24730)
-- Name: users user_username_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT user_username_key UNIQUE (username);


--
-- TOC entry 4647 (class 2606 OID 24776)
-- Name: users users_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (username);


-- Completed on 2024-09-29 12:05:57

--
-- PostgreSQL database dump complete
--

