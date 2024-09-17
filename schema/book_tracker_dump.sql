--
-- PostgreSQL database dump
--

-- Dumped from database version 16.4
-- Dumped by pg_dump version 16.4

-- Started on 2024-09-17 14:48:31

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
-- TOC entry 216 (class 1259 OID 24607)
-- Name: books; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.books (
    id character varying(255) NOT NULL,
    title character varying(255) NOT NULL,
    authors jsonb,
    publisher character varying(255),
    publisheddate character varying(50),
    description text,
    pagecount integer,
    categories jsonb,
    imageurl text,
    language character varying(50)
);


ALTER TABLE public.books OWNER TO postgres;

--
-- TOC entry 217 (class 1259 OID 24668)
-- Name: current_reads; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.current_reads (
    username character varying(50) NOT NULL,
    book_id character varying(255) NOT NULL,
    added_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    current_page integer DEFAULT 0,
    start_date date,
    notes text
);


ALTER TABLE public.current_reads OWNER TO postgres;

--
-- TOC entry 219 (class 1259 OID 24713)
-- Name: future_reads; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.future_reads (
    username character varying(50) NOT NULL,
    book_id character varying(255) NOT NULL
);


ALTER TABLE public.future_reads OWNER TO postgres;

--
-- TOC entry 218 (class 1259 OID 24687)
-- Name: past_reads; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.past_reads (
    username character varying(50) NOT NULL,
    book_id character varying(255) NOT NULL,
    finished_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);


ALTER TABLE public.past_reads OWNER TO postgres;

--
-- TOC entry 215 (class 1259 OID 24593)
-- Name: users; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.users (
    username character varying(50) NOT NULL,
    email character varying(100) NOT NULL,
    hashed_password text NOT NULL,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    is_verified boolean DEFAULT true
);


ALTER TABLE public.users OWNER TO postgres;

--
-- TOC entry 4819 (class 0 OID 24607)
-- Dependencies: 216
-- Data for Name: books; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.books (id, title, authors, publisher, publisheddate, description, pagecount, categories, imageurl, language) FROM stdin;
\.


--
-- TOC entry 4820 (class 0 OID 24668)
-- Dependencies: 217
-- Data for Name: current_reads; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.current_reads (username, book_id, added_at, current_page, start_date, notes) FROM stdin;
\.


--
-- TOC entry 4822 (class 0 OID 24713)
-- Dependencies: 219
-- Data for Name: future_reads; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.future_reads (username, book_id) FROM stdin;
\.


--
-- TOC entry 4821 (class 0 OID 24687)
-- Dependencies: 218
-- Data for Name: past_reads; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.past_reads (username, book_id, finished_at) FROM stdin;
\.


--
-- TOC entry 4818 (class 0 OID 24593)
-- Dependencies: 215
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.users (username, email, hashed_password, created_at, updated_at, is_verified) FROM stdin;
\.


--
-- TOC entry 4662 (class 2606 OID 24613)
-- Name: books book_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.books
    ADD CONSTRAINT book_pkey PRIMARY KEY (id);


--
-- TOC entry 4664 (class 2606 OID 24676)
-- Name: current_reads current_reads_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.current_reads
    ADD CONSTRAINT current_reads_pkey PRIMARY KEY (username, book_id);


--
-- TOC entry 4668 (class 2606 OID 24717)
-- Name: future_reads future_reads_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.future_reads
    ADD CONSTRAINT future_reads_pkey PRIMARY KEY (username, book_id);


--
-- TOC entry 4666 (class 2606 OID 24692)
-- Name: past_reads past_reads_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.past_reads
    ADD CONSTRAINT past_reads_pkey PRIMARY KEY (username, book_id);


--
-- TOC entry 4656 (class 2606 OID 24606)
-- Name: users user_email_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT user_email_key UNIQUE (email);


--
-- TOC entry 4658 (class 2606 OID 24604)
-- Name: users user_username_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT user_username_key UNIQUE (username);


--
-- TOC entry 4660 (class 2606 OID 24667)
-- Name: users users_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (username);


--
-- TOC entry 4669 (class 2606 OID 24682)
-- Name: current_reads current_reads_book_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.current_reads
    ADD CONSTRAINT current_reads_book_id_fkey FOREIGN KEY (book_id) REFERENCES public.books(id) ON DELETE CASCADE;


--
-- TOC entry 4670 (class 2606 OID 24677)
-- Name: current_reads current_reads_username_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.current_reads
    ADD CONSTRAINT current_reads_username_fkey FOREIGN KEY (username) REFERENCES public.users(username) ON DELETE CASCADE;


--
-- TOC entry 4673 (class 2606 OID 24723)
-- Name: future_reads future_reads_book_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.future_reads
    ADD CONSTRAINT future_reads_book_id_fkey FOREIGN KEY (book_id) REFERENCES public.books(id) ON DELETE CASCADE;


--
-- TOC entry 4674 (class 2606 OID 24718)
-- Name: future_reads future_reads_username_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.future_reads
    ADD CONSTRAINT future_reads_username_fkey FOREIGN KEY (username) REFERENCES public.users(username) ON DELETE CASCADE;


--
-- TOC entry 4671 (class 2606 OID 24698)
-- Name: past_reads past_reads_book_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.past_reads
    ADD CONSTRAINT past_reads_book_id_fkey FOREIGN KEY (book_id) REFERENCES public.books(id) ON DELETE CASCADE;


--
-- TOC entry 4672 (class 2606 OID 24693)
-- Name: past_reads past_reads_username_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.past_reads
    ADD CONSTRAINT past_reads_username_fkey FOREIGN KEY (username) REFERENCES public.users(username) ON DELETE CASCADE;


-- Completed on 2024-09-17 14:48:31

--
-- PostgreSQL database dump complete
--

