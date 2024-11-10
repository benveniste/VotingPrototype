--
-- PostgreSQL database dump
--

-- Dumped from database version 15.8 (Homebrew)
-- Dumped by pg_dump version 15.8 (Homebrew)

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
-- Name: candidates; Type: TABLE; Schema: public; Owner: mhb
--

DROP TABLE IF EXISTS public.candidates CASCADE;
CREATE TABLE public.candidates (
    id bigint NOT NULL,
    category_id bigint NOT NULL,
    description character varying(1000) NOT NULL
);
COMMENT ON TABLE public.candidates IS 'Known nominees.';
ALTER TABLE public.candidates OWNER TO mhb;

--
-- Name: categories; Type: TABLE; Schema: public; Owner: mhb
--
DROP TABLE IF EXISTS public.categories CASCADE;
CREATE TABLE public.categories (
    id bigint NOT NULL,
    election_id bigint NOT NULL,
    category character varying(250) NOT NULL
);
COMMENT ON TABLE public.categories IS 'Something to vote on, i.e. Best Skiffy Database';
ALTER TABLE public.categories OWNER TO mhb;

--
-- Name: elections; Type: TABLE; Schema: public; Owner: mhb
--
DROP TABLE IF EXISTS public.elections CASCADE;
CREATE TABLE public.elections (
    id bigint NOT NULL,
    event_id bigint NOT NULL,
    name character varying(40) NOT NULL,
    voting_opens date,
    voting_closes date,
    allow_writeins boolean
);
COMMENT ON TABLE public.elections IS 'A set of voting items.';
ALTER TABLE public.elections OWNER TO mhb;

--
-- Name: eligibilities; Type: TABLE; Schema: public; Owner: mhb
--
DROP TABLE IF EXISTS public.eligibilities CASCADE;
CREATE TABLE public.eligibilities (
    id bigint NOT NULL,
    member_id bigint NOT NULL,
    election_id bigint NOT NULL,
    status character varying(32) DEFAULT 'ELIGIBLE'::character varying NOT NULL
);
ALTER TABLE public.eligibilities OWNER TO mhb;

--
-- Name: events; Type: TABLE; Schema: public; Owner: mhb
--
DROP TABLE IF EXISTS public.events CASCADE;
CREATE TABLE public.events (
    id bigint NOT NULL,
    name character varying(100) NOT NULL,
    established date,
    start_date date,
    end_date date
);
COMMENT ON TABLE public.events IS 'The convention or event owning elections.';
ALTER TABLE public.events OWNER TO mhb;

--
-- Name: members; Type: TABLE; Schema: public; Owner: mhb
--
DROP TABLE IF EXISTS public.members CASCADE;
CREATE TABLE public.members (
    id bigint NOT NULL,
    event_id bigint NOT NULL,
    person_id bigint NOT NULL,
    badge_name character varying(40),
    status character varying(32),
    member_type character varying(32),
    member_number integer NOT NULL,
    uuid character varying(64) NOT NULL,
    joined_at timestamp with time zone NOT NULL
);
COMMENT ON COLUMN public.members.badge_name IS 'We don''t need no stinkin'' . . .';
COMMENT ON COLUMN public.members.status IS 'Free form status field.';
ALTER TABLE public.members OWNER TO mhb;

--
-- Name: persons; Type: TABLE; Schema: public; Owner: mhb
--
DROP TABLE IF EXISTS public.persons CASCADE;
CREATE TABLE public.persons (
    id bigint NOT NULL,
    prefix character varying(8),
    first_name character varying(40),
    middle_name character varying(40),
    surname character varying(40) NOT NULL,
    suffix character varying(8),
    password character varying(100),
    addr_line_1 character varying(46) NOT NULL,
    addr_line_2 character varying(46),
    city character varying(46),
    province character varying(46),
    country character(2) NOT NULL,
    postal_code character varying(16),
    email character varying(80),
    phone_number character varying(24)
);
ALTER TABLE public.persons OWNER TO mhb;

--
-- Name: votes; Type: TABLE; Schema: public; Owner: mhb
--
DROP TABLE IF EXISTS public.votes CASCADE;
CREATE TABLE public.votes (
    id bigint NOT NULL,
    category_id bigint NOT NULL,
    member_id bigint NOT NULL,
    ordinal integer DEFAULT 1 NOT NULL,
    cast_at timestamp with time zone NOT NULL,
    candidate_id bigint,
    description character varying(1000)
);
COMMENT ON TABLE public.votes IS 'Votes.  Note that ordinal can be ranked or allocation.  For simple votes, use the default of 1.';
COMMENT ON COLUMN public.votes.candidate_id IS 'Can be null for write ins.';
COMMENT ON COLUMN public.votes.description IS 'Need only be populated for write-ins and initial nominations.';
ALTER TABLE public.votes OWNER TO mhb;

--
-- Name: candidates_id_seq; Type: SEQUENCE; Schema: public; Owner: mhb
--
CREATE SEQUENCE public.candidates_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
ALTER TABLE public.candidates_id_seq OWNER TO mhb;
ALTER SEQUENCE public.candidates_id_seq OWNED BY public.candidates.id;

--
-- Name: category_id_seq; Type: SEQUENCE; Schema: public; Owner: mhb
--
CREATE SEQUENCE public.category_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
ALTER TABLE public.category_id_seq OWNER TO mhb;
ALTER SEQUENCE public.category_id_seq OWNED BY public.categories.id;

--
-- Name: elections_id_seq; Type: SEQUENCE; Schema: public; Owner: mhb
--
CREATE SEQUENCE public.elections_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
ALTER TABLE public.elections_id_seq OWNER TO mhb;
ALTER SEQUENCE public.elections_id_seq OWNED BY public.elections.id;

--
-- Name: eligibilities_id_seq; Type: SEQUENCE; Schema: public; Owner: mhb
--
CREATE SEQUENCE public.eligibilities_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
ALTER TABLE public.eligibilities_id_seq OWNER TO mhb;
ALTER SEQUENCE public.eligibilities_id_seq OWNED BY public.eligibilities.id;

--
-- Name: event_id_seq; Type: SEQUENCE; Schema: public; Owner: mhb
--
CREATE SEQUENCE public.event_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
ALTER TABLE public.event_id_seq OWNER TO mhb;
ALTER SEQUENCE public.event_id_seq OWNED BY public.events.id;

--
-- Name: members_id_seq; Type: SEQUENCE; Schema: public; Owner: mhb
--
CREATE SEQUENCE public.members_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
ALTER TABLE public.members_id_seq OWNER TO mhb;
ALTER SEQUENCE public.members_id_seq OWNED BY public.members.id;

--
-- Name: persons_id_seq; Type: SEQUENCE; Schema: public; Owner: mhb
--
CREATE SEQUENCE public.persons_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
ALTER TABLE public.persons_id_seq OWNER TO mhb;
ALTER SEQUENCE public.persons_id_seq OWNED BY public.persons.id;

--
-- Name: votes_id_seq; Type: SEQUENCE; Schema: public; Owner: mhb
--
CREATE SEQUENCE public.votes_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
ALTER TABLE public.votes_id_seq OWNER TO mhb;
ALTER SEQUENCE public.votes_id_seq OWNED BY public.votes.id;

--
-- Name: candidates id; Type: DEFAULT; Schema: public; Owner: mhb
--
ALTER TABLE ONLY public.candidates ALTER COLUMN id SET DEFAULT nextval('public.candidates_id_seq'::regclass);

--
-- Name: categories id; Type: DEFAULT; Schema: public; Owner: mhb
--
ALTER TABLE ONLY public.categories ALTER COLUMN id SET DEFAULT nextval('public.category_id_seq'::regclass);

--
-- Name: elections id; Type: DEFAULT; Schema: public; Owner: mhb
--
ALTER TABLE ONLY public.elections ALTER COLUMN id SET DEFAULT nextval('public.elections_id_seq'::regclass);

--
-- Name: eligibilities id; Type: DEFAULT; Schema: public; Owner: mhb
--
ALTER TABLE ONLY public.eligibilities ALTER COLUMN id SET DEFAULT nextval('public.eligibilities_id_seq'::regclass);

--
-- Name: events id; Type: DEFAULT; Schema: public; Owner: mhb
--
ALTER TABLE ONLY public.events ALTER COLUMN id SET DEFAULT nextval('public.event_id_seq'::regclass);

--
-- Name: members id; Type: DEFAULT; Schema: public; Owner: mhb
--
ALTER TABLE ONLY public.members ALTER COLUMN id SET DEFAULT nextval('public.members_id_seq'::regclass);

--
-- Name: persons id; Type: DEFAULT; Schema: public; Owner: mhb
--
ALTER TABLE ONLY public.persons ALTER COLUMN id SET DEFAULT nextval('public.persons_id_seq'::regclass);

--
-- Name: votes id; Type: DEFAULT; Schema: public; Owner: mhb
--
ALTER TABLE ONLY public.votes ALTER COLUMN id SET DEFAULT nextval('public.votes_id_seq'::regclass);

--
-- Name: candidates candidates_pkey; Type: CONSTRAINT; Schema: public; Owner: mhb
--
ALTER TABLE ONLY public.candidates
    ADD CONSTRAINT candidates_pkey PRIMARY KEY (id);

--
-- Name: categories category_pkey; Type: CONSTRAINT; Schema: public; Owner: mhb
--
ALTER TABLE ONLY public.categories
    ADD CONSTRAINT category_pkey PRIMARY KEY (id);

--
-- Name: elections elections_pkey; Type: CONSTRAINT; Schema: public; Owner: mhb
--
ALTER TABLE ONLY public.elections
    ADD CONSTRAINT elections_pkey PRIMARY KEY (id);

--
-- Name: eligibilities eligibilities_pkey; Type: CONSTRAINT; Schema: public; Owner: mhb
--
ALTER TABLE ONLY public.eligibilities
    ADD CONSTRAINT eligibilities_pkey PRIMARY KEY (id);

--
-- Name: events event_pkey; Type: CONSTRAINT; Schema: public; Owner: mhb
--
ALTER TABLE ONLY public.events
    ADD CONSTRAINT event_pkey PRIMARY KEY (id);

--
-- Name: members members_pkey; Type: CONSTRAINT; Schema: public; Owner: mhb
--
ALTER TABLE ONLY public.members
    ADD CONSTRAINT members_pkey PRIMARY KEY (id);

--
-- Name: persons persons_pkey; Type: CONSTRAINT; Schema: public; Owner: mhb
--
ALTER TABLE ONLY public.persons
    ADD CONSTRAINT persons_pkey PRIMARY KEY (id);

--
-- Name: votes votes_pkey; Type: CONSTRAINT; Schema: public; Owner: mhb
--
ALTER TABLE ONLY public.votes
    ADD CONSTRAINT votes_pkey PRIMARY KEY (id);

--
-- Name: idx_category; Type: INDEX; Schema: public; Owner: mhb
--
CREATE UNIQUE INDEX idx_category ON public.categories USING btree (election_id, category);

--
-- Name: idx_election_name; Type: INDEX; Schema: public; Owner: mhb
--
CREATE UNIQUE INDEX idx_election_name ON public.elections USING btree (event_id, name);

--
-- Name: idx_election_when; Type: INDEX; Schema: public; Owner: mhb
--
CREATE INDEX idx_election_when ON public.elections USING btree (voting_opens NULLS FIRST);

--
-- Name: idx_e_name; Type: INDEX; Schema: public; Owner: mhb
--
CREATE INDEX idx_e_name ON public.events USING btree (name);

--
-- Name: idx_e_when; Type: INDEX; Schema: public; Owner: mhb
--
CREATE INDEX idx_e_when ON public.events USING btree (start_date NULLS FIRST);

--
-- Name: idx_eligible_what; Type: INDEX; Schema: public; Owner: mhb
--
CREATE INDEX idx_eligible_what ON public.eligibilities USING btree (election_id, member_id);

--
-- Name: idx_eligible_who; Type: INDEX; Schema: public; Owner: mhb
--
CREATE UNIQUE INDEX idx_eligible_who ON public.eligibilities USING btree (member_id, election_id);

--
-- Name: idx_m_event; Type: INDEX; Schema: public; Owner: mhb
--
CREATE UNIQUE INDEX idx_m_event ON public.members USING btree (event_id, person_id);

--
-- Name: idx_m_person; Type: INDEX; Schema: public; Owner: mhb
--
CREATE INDEX idx_m_person ON public.members USING btree (person_id);
--
-- Name: idx_p_name; Type: INDEX; Schema: public; Owner: mhb
--
CREATE INDEX idx_p_name ON public.persons USING btree (surname, first_name);

--
-- Name: idx_onevote; Type: INDEX; Schema: public; Owner: mhb
--
CREATE UNIQUE INDEX idx_onevote ON public.votes USING btree (category_id, member_id, ordinal);

--
-- PostgreSQL database dump complete
--
