--
-- PostgreSQL database dump
--

-- Dumped from database version 9.5.2
-- Dumped by pg_dump version 9.5.2

SET statement_timeout = 0;
SET lock_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET row_security = off;

\connect postgres

DROP DATABASE redmine;
--
-- Name: redmine; Type: DATABASE; Schema: -; Owner: postgres
--

CREATE DATABASE redmine WITH TEMPLATE = template0 ENCODING = 'UTF8' LC_COLLATE = 'en_US.utf8' LC_CTYPE = 'en_US.utf8';


ALTER DATABASE redmine OWNER TO postgres;

\connect redmine

SET statement_timeout = 0;
SET lock_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: public; Type: SCHEMA; Schema: -; Owner: postgres
--



ALTER SCHEMA public OWNER TO postgres;

--
-- Name: SCHEMA public; Type: COMMENT; Schema: -; Owner: postgres
--

COMMENT ON SCHEMA public IS 'standard public schema';


--
-- Name: plpgsql; Type: EXTENSION; Schema: -; Owner: 
--

CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;


--
-- Name: EXTENSION plpgsql; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';


SET search_path = public, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: attachments; Type: TABLE; Schema: public; Owner: redmine
--

CREATE TABLE attachments (
    id integer NOT NULL,
    container_id integer,
    container_type character varying(30),
    filename character varying DEFAULT ''::character varying NOT NULL,
    disk_filename character varying DEFAULT ''::character varying NOT NULL,
    filesize bigint DEFAULT 0 NOT NULL,
    content_type character varying DEFAULT ''::character varying,
    digest character varying(40) DEFAULT ''::character varying NOT NULL,
    downloads integer DEFAULT 0 NOT NULL,
    author_id integer DEFAULT 0 NOT NULL,
    created_on timestamp without time zone,
    description character varying,
    disk_directory character varying
);


ALTER TABLE attachments OWNER TO redmine;

--
-- Name: attachments_id_seq; Type: SEQUENCE; Schema: public; Owner: redmine
--

CREATE SEQUENCE attachments_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE attachments_id_seq OWNER TO redmine;

--
-- Name: attachments_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: redmine
--

ALTER SEQUENCE attachments_id_seq OWNED BY attachments.id;


--
-- Name: auth_sources; Type: TABLE; Schema: public; Owner: redmine
--

CREATE TABLE auth_sources (
    id integer NOT NULL,
    type character varying(30) DEFAULT ''::character varying NOT NULL,
    name character varying(60) DEFAULT ''::character varying NOT NULL,
    host character varying(60),
    port integer,
    account character varying,
    account_password character varying DEFAULT ''::character varying,
    base_dn character varying(255),
    attr_login character varying(30),
    attr_firstname character varying(30),
    attr_lastname character varying(30),
    attr_mail character varying(30),
    onthefly_register boolean DEFAULT false NOT NULL,
    tls boolean DEFAULT false NOT NULL,
    filter text,
    timeout integer
);


ALTER TABLE auth_sources OWNER TO redmine;

--
-- Name: auth_sources_id_seq; Type: SEQUENCE; Schema: public; Owner: redmine
--

CREATE SEQUENCE auth_sources_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE auth_sources_id_seq OWNER TO redmine;

--
-- Name: auth_sources_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: redmine
--

ALTER SEQUENCE auth_sources_id_seq OWNED BY auth_sources.id;


--
-- Name: boards; Type: TABLE; Schema: public; Owner: redmine
--

CREATE TABLE boards (
    id integer NOT NULL,
    project_id integer NOT NULL,
    name character varying DEFAULT ''::character varying NOT NULL,
    description character varying,
    "position" integer DEFAULT 1,
    topics_count integer DEFAULT 0 NOT NULL,
    messages_count integer DEFAULT 0 NOT NULL,
    last_message_id integer,
    parent_id integer
);


ALTER TABLE boards OWNER TO redmine;

--
-- Name: boards_id_seq; Type: SEQUENCE; Schema: public; Owner: redmine
--

CREATE SEQUENCE boards_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE boards_id_seq OWNER TO redmine;

--
-- Name: boards_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: redmine
--

ALTER SEQUENCE boards_id_seq OWNED BY boards.id;


--
-- Name: changes; Type: TABLE; Schema: public; Owner: redmine
--

CREATE TABLE changes (
    id integer NOT NULL,
    changeset_id integer NOT NULL,
    action character varying(1) DEFAULT ''::character varying NOT NULL,
    path text NOT NULL,
    from_path text,
    from_revision character varying,
    revision character varying,
    branch character varying
);


ALTER TABLE changes OWNER TO redmine;

--
-- Name: changes_id_seq; Type: SEQUENCE; Schema: public; Owner: redmine
--

CREATE SEQUENCE changes_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE changes_id_seq OWNER TO redmine;

--
-- Name: changes_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: redmine
--

ALTER SEQUENCE changes_id_seq OWNED BY changes.id;


--
-- Name: changeset_parents; Type: TABLE; Schema: public; Owner: redmine
--

CREATE TABLE changeset_parents (
    changeset_id integer NOT NULL,
    parent_id integer NOT NULL
);


ALTER TABLE changeset_parents OWNER TO redmine;

--
-- Name: changesets; Type: TABLE; Schema: public; Owner: redmine
--

CREATE TABLE changesets (
    id integer NOT NULL,
    repository_id integer NOT NULL,
    revision character varying NOT NULL,
    committer character varying,
    committed_on timestamp without time zone NOT NULL,
    comments text,
    commit_date date,
    scmid character varying,
    user_id integer
);


ALTER TABLE changesets OWNER TO redmine;

--
-- Name: changesets_id_seq; Type: SEQUENCE; Schema: public; Owner: redmine
--

CREATE SEQUENCE changesets_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE changesets_id_seq OWNER TO redmine;

--
-- Name: changesets_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: redmine
--

ALTER SEQUENCE changesets_id_seq OWNED BY changesets.id;


--
-- Name: changesets_issues; Type: TABLE; Schema: public; Owner: redmine
--

CREATE TABLE changesets_issues (
    changeset_id integer NOT NULL,
    issue_id integer NOT NULL
);


ALTER TABLE changesets_issues OWNER TO redmine;

--
-- Name: comments; Type: TABLE; Schema: public; Owner: redmine
--

CREATE TABLE comments (
    id integer NOT NULL,
    commented_type character varying(30) DEFAULT ''::character varying NOT NULL,
    commented_id integer DEFAULT 0 NOT NULL,
    author_id integer DEFAULT 0 NOT NULL,
    comments text,
    created_on timestamp without time zone NOT NULL,
    updated_on timestamp without time zone NOT NULL
);


ALTER TABLE comments OWNER TO redmine;

--
-- Name: comments_id_seq; Type: SEQUENCE; Schema: public; Owner: redmine
--

CREATE SEQUENCE comments_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE comments_id_seq OWNER TO redmine;

--
-- Name: comments_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: redmine
--

ALTER SEQUENCE comments_id_seq OWNED BY comments.id;


--
-- Name: custom_field_enumerations; Type: TABLE; Schema: public; Owner: redmine
--

CREATE TABLE custom_field_enumerations (
    id integer NOT NULL,
    custom_field_id integer NOT NULL,
    name character varying NOT NULL,
    active boolean DEFAULT true NOT NULL,
    "position" integer DEFAULT 1 NOT NULL
);


ALTER TABLE custom_field_enumerations OWNER TO redmine;

--
-- Name: custom_field_enumerations_id_seq; Type: SEQUENCE; Schema: public; Owner: redmine
--

CREATE SEQUENCE custom_field_enumerations_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE custom_field_enumerations_id_seq OWNER TO redmine;

--
-- Name: custom_field_enumerations_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: redmine
--

ALTER SEQUENCE custom_field_enumerations_id_seq OWNED BY custom_field_enumerations.id;


--
-- Name: custom_fields; Type: TABLE; Schema: public; Owner: redmine
--

CREATE TABLE custom_fields (
    id integer NOT NULL,
    type character varying(30) DEFAULT ''::character varying NOT NULL,
    name character varying(30) DEFAULT ''::character varying NOT NULL,
    field_format character varying(30) DEFAULT ''::character varying NOT NULL,
    possible_values text,
    regexp character varying DEFAULT ''::character varying,
    min_length integer,
    max_length integer,
    is_required boolean DEFAULT false NOT NULL,
    is_for_all boolean DEFAULT false NOT NULL,
    is_filter boolean DEFAULT false NOT NULL,
    "position" integer DEFAULT 1,
    searchable boolean DEFAULT false,
    default_value text,
    editable boolean DEFAULT true,
    visible boolean DEFAULT true NOT NULL,
    multiple boolean DEFAULT false,
    format_store text,
    description text
);


ALTER TABLE custom_fields OWNER TO redmine;

--
-- Name: custom_fields_id_seq; Type: SEQUENCE; Schema: public; Owner: redmine
--

CREATE SEQUENCE custom_fields_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE custom_fields_id_seq OWNER TO redmine;

--
-- Name: custom_fields_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: redmine
--

ALTER SEQUENCE custom_fields_id_seq OWNED BY custom_fields.id;


--
-- Name: custom_fields_projects; Type: TABLE; Schema: public; Owner: redmine
--

CREATE TABLE custom_fields_projects (
    custom_field_id integer DEFAULT 0 NOT NULL,
    project_id integer DEFAULT 0 NOT NULL
);


ALTER TABLE custom_fields_projects OWNER TO redmine;

--
-- Name: custom_fields_roles; Type: TABLE; Schema: public; Owner: redmine
--

CREATE TABLE custom_fields_roles (
    custom_field_id integer NOT NULL,
    role_id integer NOT NULL
);


ALTER TABLE custom_fields_roles OWNER TO redmine;

--
-- Name: custom_fields_trackers; Type: TABLE; Schema: public; Owner: redmine
--

CREATE TABLE custom_fields_trackers (
    custom_field_id integer DEFAULT 0 NOT NULL,
    tracker_id integer DEFAULT 0 NOT NULL
);


ALTER TABLE custom_fields_trackers OWNER TO redmine;

--
-- Name: custom_values; Type: TABLE; Schema: public; Owner: redmine
--

CREATE TABLE custom_values (
    id integer NOT NULL,
    customized_type character varying(30) DEFAULT ''::character varying NOT NULL,
    customized_id integer DEFAULT 0 NOT NULL,
    custom_field_id integer DEFAULT 0 NOT NULL,
    value text
);


ALTER TABLE custom_values OWNER TO redmine;

--
-- Name: custom_values_id_seq; Type: SEQUENCE; Schema: public; Owner: redmine
--

CREATE SEQUENCE custom_values_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE custom_values_id_seq OWNER TO redmine;

--
-- Name: custom_values_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: redmine
--

ALTER SEQUENCE custom_values_id_seq OWNED BY custom_values.id;


--
-- Name: documents; Type: TABLE; Schema: public; Owner: redmine
--

CREATE TABLE documents (
    id integer NOT NULL,
    project_id integer DEFAULT 0 NOT NULL,
    category_id integer DEFAULT 0 NOT NULL,
    title character varying DEFAULT ''::character varying NOT NULL,
    description text,
    created_on timestamp without time zone
);


ALTER TABLE documents OWNER TO redmine;

--
-- Name: documents_id_seq; Type: SEQUENCE; Schema: public; Owner: redmine
--

CREATE SEQUENCE documents_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE documents_id_seq OWNER TO redmine;

--
-- Name: documents_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: redmine
--

ALTER SEQUENCE documents_id_seq OWNED BY documents.id;


--
-- Name: email_addresses; Type: TABLE; Schema: public; Owner: redmine
--

CREATE TABLE email_addresses (
    id integer NOT NULL,
    user_id integer NOT NULL,
    address character varying NOT NULL,
    is_default boolean DEFAULT false NOT NULL,
    notify boolean DEFAULT true NOT NULL,
    created_on timestamp without time zone NOT NULL,
    updated_on timestamp without time zone NOT NULL
);


ALTER TABLE email_addresses OWNER TO redmine;

--
-- Name: email_addresses_id_seq; Type: SEQUENCE; Schema: public; Owner: redmine
--

CREATE SEQUENCE email_addresses_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE email_addresses_id_seq OWNER TO redmine;

--
-- Name: email_addresses_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: redmine
--

ALTER SEQUENCE email_addresses_id_seq OWNED BY email_addresses.id;


--
-- Name: enabled_modules; Type: TABLE; Schema: public; Owner: redmine
--

CREATE TABLE enabled_modules (
    id integer NOT NULL,
    project_id integer,
    name character varying NOT NULL
);


ALTER TABLE enabled_modules OWNER TO redmine;

--
-- Name: enabled_modules_id_seq; Type: SEQUENCE; Schema: public; Owner: redmine
--

CREATE SEQUENCE enabled_modules_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE enabled_modules_id_seq OWNER TO redmine;

--
-- Name: enabled_modules_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: redmine
--

ALTER SEQUENCE enabled_modules_id_seq OWNED BY enabled_modules.id;


--
-- Name: enumerations; Type: TABLE; Schema: public; Owner: redmine
--

CREATE TABLE enumerations (
    id integer NOT NULL,
    name character varying(30) DEFAULT ''::character varying NOT NULL,
    "position" integer DEFAULT 1,
    is_default boolean DEFAULT false NOT NULL,
    type character varying,
    active boolean DEFAULT true NOT NULL,
    project_id integer,
    parent_id integer,
    position_name character varying(30)
);


ALTER TABLE enumerations OWNER TO redmine;

--
-- Name: enumerations_id_seq; Type: SEQUENCE; Schema: public; Owner: redmine
--

CREATE SEQUENCE enumerations_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE enumerations_id_seq OWNER TO redmine;

--
-- Name: enumerations_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: redmine
--

ALTER SEQUENCE enumerations_id_seq OWNED BY enumerations.id;


--
-- Name: groups_users; Type: TABLE; Schema: public; Owner: redmine
--

CREATE TABLE groups_users (
    group_id integer NOT NULL,
    user_id integer NOT NULL
);


ALTER TABLE groups_users OWNER TO redmine;

--
-- Name: import_items; Type: TABLE; Schema: public; Owner: redmine
--

CREATE TABLE import_items (
    id integer NOT NULL,
    import_id integer NOT NULL,
    "position" integer NOT NULL,
    obj_id integer,
    message text
);


ALTER TABLE import_items OWNER TO redmine;

--
-- Name: import_items_id_seq; Type: SEQUENCE; Schema: public; Owner: redmine
--

CREATE SEQUENCE import_items_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE import_items_id_seq OWNER TO redmine;

--
-- Name: import_items_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: redmine
--

ALTER SEQUENCE import_items_id_seq OWNED BY import_items.id;


--
-- Name: imports; Type: TABLE; Schema: public; Owner: redmine
--

CREATE TABLE imports (
    id integer NOT NULL,
    type character varying,
    user_id integer NOT NULL,
    filename character varying,
    settings text,
    total_items integer,
    finished boolean DEFAULT false NOT NULL,
    created_at timestamp without time zone NOT NULL,
    updated_at timestamp without time zone NOT NULL
);


ALTER TABLE imports OWNER TO redmine;

--
-- Name: imports_id_seq; Type: SEQUENCE; Schema: public; Owner: redmine
--

CREATE SEQUENCE imports_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE imports_id_seq OWNER TO redmine;

--
-- Name: imports_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: redmine
--

ALTER SEQUENCE imports_id_seq OWNED BY imports.id;


--
-- Name: issue_categories; Type: TABLE; Schema: public; Owner: redmine
--

CREATE TABLE issue_categories (
    id integer NOT NULL,
    project_id integer DEFAULT 0 NOT NULL,
    name character varying(60) DEFAULT ''::character varying NOT NULL,
    assigned_to_id integer
);


ALTER TABLE issue_categories OWNER TO redmine;

--
-- Name: issue_categories_id_seq; Type: SEQUENCE; Schema: public; Owner: redmine
--

CREATE SEQUENCE issue_categories_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE issue_categories_id_seq OWNER TO redmine;

--
-- Name: issue_categories_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: redmine
--

ALTER SEQUENCE issue_categories_id_seq OWNED BY issue_categories.id;


--
-- Name: issue_relations; Type: TABLE; Schema: public; Owner: redmine
--

CREATE TABLE issue_relations (
    id integer NOT NULL,
    issue_from_id integer NOT NULL,
    issue_to_id integer NOT NULL,
    relation_type character varying DEFAULT ''::character varying NOT NULL,
    delay integer
);


ALTER TABLE issue_relations OWNER TO redmine;

--
-- Name: issue_relations_id_seq; Type: SEQUENCE; Schema: public; Owner: redmine
--

CREATE SEQUENCE issue_relations_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE issue_relations_id_seq OWNER TO redmine;

--
-- Name: issue_relations_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: redmine
--

ALTER SEQUENCE issue_relations_id_seq OWNED BY issue_relations.id;


--
-- Name: issue_statuses; Type: TABLE; Schema: public; Owner: redmine
--

CREATE TABLE issue_statuses (
    id integer NOT NULL,
    name character varying(30) DEFAULT ''::character varying NOT NULL,
    is_closed boolean DEFAULT false NOT NULL,
    "position" integer DEFAULT 1,
    default_done_ratio integer
);


ALTER TABLE issue_statuses OWNER TO redmine;

--
-- Name: issue_statuses_id_seq; Type: SEQUENCE; Schema: public; Owner: redmine
--

CREATE SEQUENCE issue_statuses_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE issue_statuses_id_seq OWNER TO redmine;

--
-- Name: issue_statuses_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: redmine
--

ALTER SEQUENCE issue_statuses_id_seq OWNED BY issue_statuses.id;


--
-- Name: issues; Type: TABLE; Schema: public; Owner: redmine
--

CREATE TABLE issues (
    id integer NOT NULL,
    tracker_id integer NOT NULL,
    project_id integer NOT NULL,
    subject character varying DEFAULT ''::character varying NOT NULL,
    description text,
    due_date date,
    category_id integer,
    status_id integer NOT NULL,
    assigned_to_id integer,
    priority_id integer NOT NULL,
    fixed_version_id integer,
    author_id integer NOT NULL,
    lock_version integer DEFAULT 0 NOT NULL,
    created_on timestamp without time zone,
    updated_on timestamp without time zone,
    start_date date,
    done_ratio integer DEFAULT 0 NOT NULL,
    estimated_hours double precision,
    parent_id integer,
    root_id integer,
    lft integer,
    rgt integer,
    is_private boolean DEFAULT false NOT NULL,
    closed_on timestamp without time zone
);


ALTER TABLE issues OWNER TO redmine;

--
-- Name: issues_id_seq; Type: SEQUENCE; Schema: public; Owner: redmine
--

CREATE SEQUENCE issues_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE issues_id_seq OWNER TO redmine;

--
-- Name: issues_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: redmine
--

ALTER SEQUENCE issues_id_seq OWNED BY issues.id;


--
-- Name: journal_details; Type: TABLE; Schema: public; Owner: redmine
--

CREATE TABLE journal_details (
    id integer NOT NULL,
    journal_id integer DEFAULT 0 NOT NULL,
    property character varying(30) DEFAULT ''::character varying NOT NULL,
    prop_key character varying(30) DEFAULT ''::character varying NOT NULL,
    old_value text,
    value text
);


ALTER TABLE journal_details OWNER TO redmine;

--
-- Name: journal_details_id_seq; Type: SEQUENCE; Schema: public; Owner: redmine
--

CREATE SEQUENCE journal_details_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE journal_details_id_seq OWNER TO redmine;

--
-- Name: journal_details_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: redmine
--

ALTER SEQUENCE journal_details_id_seq OWNED BY journal_details.id;


--
-- Name: journals; Type: TABLE; Schema: public; Owner: redmine
--

CREATE TABLE journals (
    id integer NOT NULL,
    journalized_id integer DEFAULT 0 NOT NULL,
    journalized_type character varying(30) DEFAULT ''::character varying NOT NULL,
    user_id integer DEFAULT 0 NOT NULL,
    notes text,
    created_on timestamp without time zone NOT NULL,
    private_notes boolean DEFAULT false NOT NULL
);


ALTER TABLE journals OWNER TO redmine;

--
-- Name: journals_id_seq; Type: SEQUENCE; Schema: public; Owner: redmine
--

CREATE SEQUENCE journals_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE journals_id_seq OWNER TO redmine;

--
-- Name: journals_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: redmine
--

ALTER SEQUENCE journals_id_seq OWNED BY journals.id;


--
-- Name: member_roles; Type: TABLE; Schema: public; Owner: redmine
--

CREATE TABLE member_roles (
    id integer NOT NULL,
    member_id integer NOT NULL,
    role_id integer NOT NULL,
    inherited_from integer
);


ALTER TABLE member_roles OWNER TO redmine;

--
-- Name: member_roles_id_seq; Type: SEQUENCE; Schema: public; Owner: redmine
--

CREATE SEQUENCE member_roles_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE member_roles_id_seq OWNER TO redmine;

--
-- Name: member_roles_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: redmine
--

ALTER SEQUENCE member_roles_id_seq OWNED BY member_roles.id;


--
-- Name: members; Type: TABLE; Schema: public; Owner: redmine
--

CREATE TABLE members (
    id integer NOT NULL,
    user_id integer DEFAULT 0 NOT NULL,
    project_id integer DEFAULT 0 NOT NULL,
    created_on timestamp without time zone,
    mail_notification boolean DEFAULT false NOT NULL
);


ALTER TABLE members OWNER TO redmine;

--
-- Name: members_id_seq; Type: SEQUENCE; Schema: public; Owner: redmine
--

CREATE SEQUENCE members_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE members_id_seq OWNER TO redmine;

--
-- Name: members_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: redmine
--

ALTER SEQUENCE members_id_seq OWNED BY members.id;


--
-- Name: messages; Type: TABLE; Schema: public; Owner: redmine
--

CREATE TABLE messages (
    id integer NOT NULL,
    board_id integer NOT NULL,
    parent_id integer,
    subject character varying DEFAULT ''::character varying NOT NULL,
    content text,
    author_id integer,
    replies_count integer DEFAULT 0 NOT NULL,
    last_reply_id integer,
    created_on timestamp without time zone NOT NULL,
    updated_on timestamp without time zone NOT NULL,
    locked boolean DEFAULT false,
    sticky integer DEFAULT 0
);


ALTER TABLE messages OWNER TO redmine;

--
-- Name: messages_id_seq; Type: SEQUENCE; Schema: public; Owner: redmine
--

CREATE SEQUENCE messages_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE messages_id_seq OWNER TO redmine;

--
-- Name: messages_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: redmine
--

ALTER SEQUENCE messages_id_seq OWNED BY messages.id;


--
-- Name: news; Type: TABLE; Schema: public; Owner: redmine
--

CREATE TABLE news (
    id integer NOT NULL,
    project_id integer,
    title character varying(60) DEFAULT ''::character varying NOT NULL,
    summary character varying(255) DEFAULT ''::character varying,
    description text,
    author_id integer DEFAULT 0 NOT NULL,
    created_on timestamp without time zone,
    comments_count integer DEFAULT 0 NOT NULL
);


ALTER TABLE news OWNER TO redmine;

--
-- Name: news_id_seq; Type: SEQUENCE; Schema: public; Owner: redmine
--

CREATE SEQUENCE news_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE news_id_seq OWNER TO redmine;

--
-- Name: news_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: redmine
--

ALTER SEQUENCE news_id_seq OWNED BY news.id;


--
-- Name: open_id_authentication_associations; Type: TABLE; Schema: public; Owner: redmine
--

CREATE TABLE open_id_authentication_associations (
    id integer NOT NULL,
    issued integer,
    lifetime integer,
    handle character varying,
    assoc_type character varying,
    server_url bytea,
    secret bytea
);


ALTER TABLE open_id_authentication_associations OWNER TO redmine;

--
-- Name: open_id_authentication_associations_id_seq; Type: SEQUENCE; Schema: public; Owner: redmine
--

CREATE SEQUENCE open_id_authentication_associations_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE open_id_authentication_associations_id_seq OWNER TO redmine;

--
-- Name: open_id_authentication_associations_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: redmine
--

ALTER SEQUENCE open_id_authentication_associations_id_seq OWNED BY open_id_authentication_associations.id;


--
-- Name: open_id_authentication_nonces; Type: TABLE; Schema: public; Owner: redmine
--

CREATE TABLE open_id_authentication_nonces (
    id integer NOT NULL,
    "timestamp" integer NOT NULL,
    server_url character varying,
    salt character varying NOT NULL
);


ALTER TABLE open_id_authentication_nonces OWNER TO redmine;

--
-- Name: open_id_authentication_nonces_id_seq; Type: SEQUENCE; Schema: public; Owner: redmine
--

CREATE SEQUENCE open_id_authentication_nonces_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE open_id_authentication_nonces_id_seq OWNER TO redmine;

--
-- Name: open_id_authentication_nonces_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: redmine
--

ALTER SEQUENCE open_id_authentication_nonces_id_seq OWNED BY open_id_authentication_nonces.id;


--
-- Name: projects; Type: TABLE; Schema: public; Owner: redmine
--

CREATE TABLE projects (
    id integer NOT NULL,
    name character varying DEFAULT ''::character varying NOT NULL,
    description text,
    homepage character varying DEFAULT ''::character varying,
    is_public boolean DEFAULT true NOT NULL,
    parent_id integer,
    created_on timestamp without time zone,
    updated_on timestamp without time zone,
    identifier character varying,
    status integer DEFAULT 1 NOT NULL,
    lft integer,
    rgt integer,
    inherit_members boolean DEFAULT false NOT NULL,
    default_version_id integer
);


ALTER TABLE projects OWNER TO redmine;

--
-- Name: projects_id_seq; Type: SEQUENCE; Schema: public; Owner: redmine
--

CREATE SEQUENCE projects_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE projects_id_seq OWNER TO redmine;

--
-- Name: projects_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: redmine
--

ALTER SEQUENCE projects_id_seq OWNED BY projects.id;


--
-- Name: projects_trackers; Type: TABLE; Schema: public; Owner: redmine
--

CREATE TABLE projects_trackers (
    project_id integer DEFAULT 0 NOT NULL,
    tracker_id integer DEFAULT 0 NOT NULL
);


ALTER TABLE projects_trackers OWNER TO redmine;

--
-- Name: queries; Type: TABLE; Schema: public; Owner: redmine
--

CREATE TABLE queries (
    id integer NOT NULL,
    project_id integer,
    name character varying DEFAULT ''::character varying NOT NULL,
    filters text,
    user_id integer DEFAULT 0 NOT NULL,
    column_names text,
    sort_criteria text,
    group_by character varying,
    type character varying,
    visibility integer DEFAULT 0,
    options text
);


ALTER TABLE queries OWNER TO redmine;

--
-- Name: queries_id_seq; Type: SEQUENCE; Schema: public; Owner: redmine
--

CREATE SEQUENCE queries_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE queries_id_seq OWNER TO redmine;

--
-- Name: queries_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: redmine
--

ALTER SEQUENCE queries_id_seq OWNED BY queries.id;


--
-- Name: queries_roles; Type: TABLE; Schema: public; Owner: redmine
--

CREATE TABLE queries_roles (
    query_id integer NOT NULL,
    role_id integer NOT NULL
);


ALTER TABLE queries_roles OWNER TO redmine;

--
-- Name: repositories; Type: TABLE; Schema: public; Owner: redmine
--

CREATE TABLE repositories (
    id integer NOT NULL,
    project_id integer DEFAULT 0 NOT NULL,
    url character varying DEFAULT ''::character varying NOT NULL,
    login character varying(60) DEFAULT ''::character varying,
    password character varying DEFAULT ''::character varying,
    root_url character varying(255) DEFAULT ''::character varying,
    type character varying,
    path_encoding character varying(64),
    log_encoding character varying(64),
    extra_info text,
    identifier character varying,
    is_default boolean DEFAULT false,
    created_on timestamp without time zone
);


ALTER TABLE repositories OWNER TO redmine;

--
-- Name: repositories_id_seq; Type: SEQUENCE; Schema: public; Owner: redmine
--

CREATE SEQUENCE repositories_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE repositories_id_seq OWNER TO redmine;

--
-- Name: repositories_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: redmine
--

ALTER SEQUENCE repositories_id_seq OWNED BY repositories.id;


--
-- Name: roles; Type: TABLE; Schema: public; Owner: redmine
--

CREATE TABLE roles (
    id integer NOT NULL,
    name character varying(30) DEFAULT ''::character varying NOT NULL,
    "position" integer DEFAULT 1,
    assignable boolean DEFAULT true,
    builtin integer DEFAULT 0 NOT NULL,
    permissions text,
    issues_visibility character varying(30) DEFAULT 'default'::character varying NOT NULL,
    users_visibility character varying(30) DEFAULT 'all'::character varying NOT NULL,
    time_entries_visibility character varying(30) DEFAULT 'all'::character varying NOT NULL,
    all_roles_managed boolean DEFAULT true NOT NULL
);


ALTER TABLE roles OWNER TO redmine;

--
-- Name: roles_id_seq; Type: SEQUENCE; Schema: public; Owner: redmine
--

CREATE SEQUENCE roles_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE roles_id_seq OWNER TO redmine;

--
-- Name: roles_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: redmine
--

ALTER SEQUENCE roles_id_seq OWNED BY roles.id;


--
-- Name: roles_managed_roles; Type: TABLE; Schema: public; Owner: redmine
--

CREATE TABLE roles_managed_roles (
    role_id integer NOT NULL,
    managed_role_id integer NOT NULL
);


ALTER TABLE roles_managed_roles OWNER TO redmine;

--
-- Name: schema_migrations; Type: TABLE; Schema: public; Owner: redmine
--

CREATE TABLE schema_migrations (
    version character varying NOT NULL
);


ALTER TABLE schema_migrations OWNER TO redmine;

--
-- Name: settings; Type: TABLE; Schema: public; Owner: redmine
--

CREATE TABLE settings (
    id integer NOT NULL,
    name character varying(255) DEFAULT ''::character varying NOT NULL,
    value text,
    updated_on timestamp without time zone
);


ALTER TABLE settings OWNER TO redmine;

--
-- Name: settings_id_seq; Type: SEQUENCE; Schema: public; Owner: redmine
--

CREATE SEQUENCE settings_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE settings_id_seq OWNER TO redmine;

--
-- Name: settings_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: redmine
--

ALTER SEQUENCE settings_id_seq OWNED BY settings.id;


--
-- Name: time_entries; Type: TABLE; Schema: public; Owner: redmine
--

CREATE TABLE time_entries (
    id integer NOT NULL,
    project_id integer NOT NULL,
    user_id integer NOT NULL,
    issue_id integer,
    hours double precision NOT NULL,
    comments character varying(1024),
    activity_id integer NOT NULL,
    spent_on date NOT NULL,
    tyear integer NOT NULL,
    tmonth integer NOT NULL,
    tweek integer NOT NULL,
    created_on timestamp without time zone NOT NULL,
    updated_on timestamp without time zone NOT NULL
);


ALTER TABLE time_entries OWNER TO redmine;

--
-- Name: time_entries_id_seq; Type: SEQUENCE; Schema: public; Owner: redmine
--

CREATE SEQUENCE time_entries_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE time_entries_id_seq OWNER TO redmine;

--
-- Name: time_entries_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: redmine
--

ALTER SEQUENCE time_entries_id_seq OWNED BY time_entries.id;


--
-- Name: tokens; Type: TABLE; Schema: public; Owner: redmine
--

CREATE TABLE tokens (
    id integer NOT NULL,
    user_id integer DEFAULT 0 NOT NULL,
    action character varying(30) DEFAULT ''::character varying NOT NULL,
    value character varying(40) DEFAULT ''::character varying NOT NULL,
    created_on timestamp without time zone NOT NULL,
    updated_on timestamp without time zone
);


ALTER TABLE tokens OWNER TO redmine;

--
-- Name: tokens_id_seq; Type: SEQUENCE; Schema: public; Owner: redmine
--

CREATE SEQUENCE tokens_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE tokens_id_seq OWNER TO redmine;

--
-- Name: tokens_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: redmine
--

ALTER SEQUENCE tokens_id_seq OWNED BY tokens.id;


--
-- Name: trackers; Type: TABLE; Schema: public; Owner: redmine
--

CREATE TABLE trackers (
    id integer NOT NULL,
    name character varying(30) DEFAULT ''::character varying NOT NULL,
    is_in_chlog boolean DEFAULT false NOT NULL,
    "position" integer DEFAULT 1,
    is_in_roadmap boolean DEFAULT true NOT NULL,
    fields_bits integer DEFAULT 0,
    default_status_id integer
);


ALTER TABLE trackers OWNER TO redmine;

--
-- Name: trackers_id_seq; Type: SEQUENCE; Schema: public; Owner: redmine
--

CREATE SEQUENCE trackers_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE trackers_id_seq OWNER TO redmine;

--
-- Name: trackers_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: redmine
--

ALTER SEQUENCE trackers_id_seq OWNED BY trackers.id;


--
-- Name: user_preferences; Type: TABLE; Schema: public; Owner: redmine
--

CREATE TABLE user_preferences (
    id integer NOT NULL,
    user_id integer DEFAULT 0 NOT NULL,
    others text,
    hide_mail boolean DEFAULT true,
    time_zone character varying
);


ALTER TABLE user_preferences OWNER TO redmine;

--
-- Name: user_preferences_id_seq; Type: SEQUENCE; Schema: public; Owner: redmine
--

CREATE SEQUENCE user_preferences_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE user_preferences_id_seq OWNER TO redmine;

--
-- Name: user_preferences_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: redmine
--

ALTER SEQUENCE user_preferences_id_seq OWNED BY user_preferences.id;


--
-- Name: users; Type: TABLE; Schema: public; Owner: redmine
--

CREATE TABLE users (
    id integer NOT NULL,
    login character varying DEFAULT ''::character varying NOT NULL,
    hashed_password character varying(40) DEFAULT ''::character varying NOT NULL,
    firstname character varying(30) DEFAULT ''::character varying NOT NULL,
    lastname character varying(255) DEFAULT ''::character varying NOT NULL,
    admin boolean DEFAULT false NOT NULL,
    status integer DEFAULT 1 NOT NULL,
    last_login_on timestamp without time zone,
    language character varying(5) DEFAULT ''::character varying,
    auth_source_id integer,
    created_on timestamp without time zone,
    updated_on timestamp without time zone,
    type character varying,
    identity_url character varying,
    mail_notification character varying DEFAULT ''::character varying NOT NULL,
    salt character varying(64),
    must_change_passwd boolean DEFAULT false NOT NULL,
    passwd_changed_on timestamp without time zone
);


ALTER TABLE users OWNER TO redmine;

--
-- Name: users_id_seq; Type: SEQUENCE; Schema: public; Owner: redmine
--

CREATE SEQUENCE users_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE users_id_seq OWNER TO redmine;

--
-- Name: users_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: redmine
--

ALTER SEQUENCE users_id_seq OWNED BY users.id;


--
-- Name: versions; Type: TABLE; Schema: public; Owner: redmine
--

CREATE TABLE versions (
    id integer NOT NULL,
    project_id integer DEFAULT 0 NOT NULL,
    name character varying DEFAULT ''::character varying NOT NULL,
    description character varying DEFAULT ''::character varying,
    effective_date date,
    created_on timestamp without time zone,
    updated_on timestamp without time zone,
    wiki_page_title character varying,
    status character varying DEFAULT 'open'::character varying,
    sharing character varying DEFAULT 'none'::character varying NOT NULL
);


ALTER TABLE versions OWNER TO redmine;

--
-- Name: versions_id_seq; Type: SEQUENCE; Schema: public; Owner: redmine
--

CREATE SEQUENCE versions_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE versions_id_seq OWNER TO redmine;

--
-- Name: versions_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: redmine
--

ALTER SEQUENCE versions_id_seq OWNED BY versions.id;


--
-- Name: watchers; Type: TABLE; Schema: public; Owner: redmine
--

CREATE TABLE watchers (
    id integer NOT NULL,
    watchable_type character varying DEFAULT ''::character varying NOT NULL,
    watchable_id integer DEFAULT 0 NOT NULL,
    user_id integer
);


ALTER TABLE watchers OWNER TO redmine;

--
-- Name: watchers_id_seq; Type: SEQUENCE; Schema: public; Owner: redmine
--

CREATE SEQUENCE watchers_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE watchers_id_seq OWNER TO redmine;

--
-- Name: watchers_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: redmine
--

ALTER SEQUENCE watchers_id_seq OWNED BY watchers.id;


--
-- Name: wiki_content_versions; Type: TABLE; Schema: public; Owner: redmine
--

CREATE TABLE wiki_content_versions (
    id integer NOT NULL,
    wiki_content_id integer NOT NULL,
    page_id integer NOT NULL,
    author_id integer,
    data bytea,
    compression character varying(6) DEFAULT ''::character varying,
    comments character varying(1024) DEFAULT ''::character varying,
    updated_on timestamp without time zone NOT NULL,
    version integer NOT NULL
);


ALTER TABLE wiki_content_versions OWNER TO redmine;

--
-- Name: wiki_content_versions_id_seq; Type: SEQUENCE; Schema: public; Owner: redmine
--

CREATE SEQUENCE wiki_content_versions_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE wiki_content_versions_id_seq OWNER TO redmine;

--
-- Name: wiki_content_versions_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: redmine
--

ALTER SEQUENCE wiki_content_versions_id_seq OWNED BY wiki_content_versions.id;


--
-- Name: wiki_contents; Type: TABLE; Schema: public; Owner: redmine
--

CREATE TABLE wiki_contents (
    id integer NOT NULL,
    page_id integer NOT NULL,
    author_id integer,
    text text,
    comments character varying(1024) DEFAULT ''::character varying,
    updated_on timestamp without time zone NOT NULL,
    version integer NOT NULL
);


ALTER TABLE wiki_contents OWNER TO redmine;

--
-- Name: wiki_contents_id_seq; Type: SEQUENCE; Schema: public; Owner: redmine
--

CREATE SEQUENCE wiki_contents_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE wiki_contents_id_seq OWNER TO redmine;

--
-- Name: wiki_contents_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: redmine
--

ALTER SEQUENCE wiki_contents_id_seq OWNED BY wiki_contents.id;


--
-- Name: wiki_pages; Type: TABLE; Schema: public; Owner: redmine
--

CREATE TABLE wiki_pages (
    id integer NOT NULL,
    wiki_id integer NOT NULL,
    title character varying(255) NOT NULL,
    created_on timestamp without time zone NOT NULL,
    protected boolean DEFAULT false NOT NULL,
    parent_id integer
);


ALTER TABLE wiki_pages OWNER TO redmine;

--
-- Name: wiki_pages_id_seq; Type: SEQUENCE; Schema: public; Owner: redmine
--

CREATE SEQUENCE wiki_pages_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE wiki_pages_id_seq OWNER TO redmine;

--
-- Name: wiki_pages_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: redmine
--

ALTER SEQUENCE wiki_pages_id_seq OWNED BY wiki_pages.id;


--
-- Name: wiki_redirects; Type: TABLE; Schema: public; Owner: redmine
--

CREATE TABLE wiki_redirects (
    id integer NOT NULL,
    wiki_id integer NOT NULL,
    title character varying,
    redirects_to character varying,
    created_on timestamp without time zone NOT NULL,
    redirects_to_wiki_id integer NOT NULL
);


ALTER TABLE wiki_redirects OWNER TO redmine;

--
-- Name: wiki_redirects_id_seq; Type: SEQUENCE; Schema: public; Owner: redmine
--

CREATE SEQUENCE wiki_redirects_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE wiki_redirects_id_seq OWNER TO redmine;

--
-- Name: wiki_redirects_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: redmine
--

ALTER SEQUENCE wiki_redirects_id_seq OWNED BY wiki_redirects.id;


--
-- Name: wikis; Type: TABLE; Schema: public; Owner: redmine
--

CREATE TABLE wikis (
    id integer NOT NULL,
    project_id integer NOT NULL,
    start_page character varying(255) NOT NULL,
    status integer DEFAULT 1 NOT NULL
);


ALTER TABLE wikis OWNER TO redmine;

--
-- Name: wikis_id_seq; Type: SEQUENCE; Schema: public; Owner: redmine
--

CREATE SEQUENCE wikis_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE wikis_id_seq OWNER TO redmine;

--
-- Name: wikis_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: redmine
--

ALTER SEQUENCE wikis_id_seq OWNED BY wikis.id;


--
-- Name: workflows; Type: TABLE; Schema: public; Owner: redmine
--

CREATE TABLE workflows (
    id integer NOT NULL,
    tracker_id integer DEFAULT 0 NOT NULL,
    old_status_id integer DEFAULT 0 NOT NULL,
    new_status_id integer DEFAULT 0 NOT NULL,
    role_id integer DEFAULT 0 NOT NULL,
    assignee boolean DEFAULT false NOT NULL,
    author boolean DEFAULT false NOT NULL,
    type character varying(30),
    field_name character varying(30),
    rule character varying(30)
);


ALTER TABLE workflows OWNER TO redmine;

--
-- Name: workflows_id_seq; Type: SEQUENCE; Schema: public; Owner: redmine
--

CREATE SEQUENCE workflows_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE workflows_id_seq OWNER TO redmine;

--
-- Name: workflows_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: redmine
--

ALTER SEQUENCE workflows_id_seq OWNED BY workflows.id;


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: redmine
--

ALTER TABLE ONLY attachments ALTER COLUMN id SET DEFAULT nextval('attachments_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: redmine
--

ALTER TABLE ONLY auth_sources ALTER COLUMN id SET DEFAULT nextval('auth_sources_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: redmine
--

ALTER TABLE ONLY boards ALTER COLUMN id SET DEFAULT nextval('boards_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: redmine
--

ALTER TABLE ONLY changes ALTER COLUMN id SET DEFAULT nextval('changes_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: redmine
--

ALTER TABLE ONLY changesets ALTER COLUMN id SET DEFAULT nextval('changesets_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: redmine
--

ALTER TABLE ONLY comments ALTER COLUMN id SET DEFAULT nextval('comments_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: redmine
--

ALTER TABLE ONLY custom_field_enumerations ALTER COLUMN id SET DEFAULT nextval('custom_field_enumerations_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: redmine
--

ALTER TABLE ONLY custom_fields ALTER COLUMN id SET DEFAULT nextval('custom_fields_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: redmine
--

ALTER TABLE ONLY custom_values ALTER COLUMN id SET DEFAULT nextval('custom_values_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: redmine
--

ALTER TABLE ONLY documents ALTER COLUMN id SET DEFAULT nextval('documents_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: redmine
--

ALTER TABLE ONLY email_addresses ALTER COLUMN id SET DEFAULT nextval('email_addresses_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: redmine
--

ALTER TABLE ONLY enabled_modules ALTER COLUMN id SET DEFAULT nextval('enabled_modules_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: redmine
--

ALTER TABLE ONLY enumerations ALTER COLUMN id SET DEFAULT nextval('enumerations_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: redmine
--

ALTER TABLE ONLY import_items ALTER COLUMN id SET DEFAULT nextval('import_items_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: redmine
--

ALTER TABLE ONLY imports ALTER COLUMN id SET DEFAULT nextval('imports_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: redmine
--

ALTER TABLE ONLY issue_categories ALTER COLUMN id SET DEFAULT nextval('issue_categories_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: redmine
--

ALTER TABLE ONLY issue_relations ALTER COLUMN id SET DEFAULT nextval('issue_relations_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: redmine
--

ALTER TABLE ONLY issue_statuses ALTER COLUMN id SET DEFAULT nextval('issue_statuses_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: redmine
--

ALTER TABLE ONLY issues ALTER COLUMN id SET DEFAULT nextval('issues_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: redmine
--

ALTER TABLE ONLY journal_details ALTER COLUMN id SET DEFAULT nextval('journal_details_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: redmine
--

ALTER TABLE ONLY journals ALTER COLUMN id SET DEFAULT nextval('journals_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: redmine
--

ALTER TABLE ONLY member_roles ALTER COLUMN id SET DEFAULT nextval('member_roles_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: redmine
--

ALTER TABLE ONLY members ALTER COLUMN id SET DEFAULT nextval('members_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: redmine
--

ALTER TABLE ONLY messages ALTER COLUMN id SET DEFAULT nextval('messages_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: redmine
--

ALTER TABLE ONLY news ALTER COLUMN id SET DEFAULT nextval('news_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: redmine
--

ALTER TABLE ONLY open_id_authentication_associations ALTER COLUMN id SET DEFAULT nextval('open_id_authentication_associations_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: redmine
--

ALTER TABLE ONLY open_id_authentication_nonces ALTER COLUMN id SET DEFAULT nextval('open_id_authentication_nonces_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: redmine
--

ALTER TABLE ONLY projects ALTER COLUMN id SET DEFAULT nextval('projects_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: redmine
--

ALTER TABLE ONLY queries ALTER COLUMN id SET DEFAULT nextval('queries_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: redmine
--

ALTER TABLE ONLY repositories ALTER COLUMN id SET DEFAULT nextval('repositories_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: redmine
--

ALTER TABLE ONLY roles ALTER COLUMN id SET DEFAULT nextval('roles_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: redmine
--

ALTER TABLE ONLY settings ALTER COLUMN id SET DEFAULT nextval('settings_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: redmine
--

ALTER TABLE ONLY time_entries ALTER COLUMN id SET DEFAULT nextval('time_entries_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: redmine
--

ALTER TABLE ONLY tokens ALTER COLUMN id SET DEFAULT nextval('tokens_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: redmine
--

ALTER TABLE ONLY trackers ALTER COLUMN id SET DEFAULT nextval('trackers_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: redmine
--

ALTER TABLE ONLY user_preferences ALTER COLUMN id SET DEFAULT nextval('user_preferences_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: redmine
--

ALTER TABLE ONLY users ALTER COLUMN id SET DEFAULT nextval('users_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: redmine
--

ALTER TABLE ONLY versions ALTER COLUMN id SET DEFAULT nextval('versions_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: redmine
--

ALTER TABLE ONLY watchers ALTER COLUMN id SET DEFAULT nextval('watchers_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: redmine
--

ALTER TABLE ONLY wiki_content_versions ALTER COLUMN id SET DEFAULT nextval('wiki_content_versions_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: redmine
--

ALTER TABLE ONLY wiki_contents ALTER COLUMN id SET DEFAULT nextval('wiki_contents_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: redmine
--

ALTER TABLE ONLY wiki_pages ALTER COLUMN id SET DEFAULT nextval('wiki_pages_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: redmine
--

ALTER TABLE ONLY wiki_redirects ALTER COLUMN id SET DEFAULT nextval('wiki_redirects_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: redmine
--

ALTER TABLE ONLY wikis ALTER COLUMN id SET DEFAULT nextval('wikis_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: redmine
--

ALTER TABLE ONLY workflows ALTER COLUMN id SET DEFAULT nextval('workflows_id_seq'::regclass);


--
-- Data for Name: attachments; Type: TABLE DATA; Schema: public; Owner: redmine
--



--
-- Name: attachments_id_seq; Type: SEQUENCE SET; Schema: public; Owner: redmine
--

SELECT pg_catalog.setval('attachments_id_seq', 1, false);


--
-- Data for Name: auth_sources; Type: TABLE DATA; Schema: public; Owner: redmine
--



--
-- Name: auth_sources_id_seq; Type: SEQUENCE SET; Schema: public; Owner: redmine
--

SELECT pg_catalog.setval('auth_sources_id_seq', 1, false);


--
-- Data for Name: boards; Type: TABLE DATA; Schema: public; Owner: redmine
--



--
-- Name: boards_id_seq; Type: SEQUENCE SET; Schema: public; Owner: redmine
--

SELECT pg_catalog.setval('boards_id_seq', 1, false);


--
-- Data for Name: changes; Type: TABLE DATA; Schema: public; Owner: redmine
--



--
-- Name: changes_id_seq; Type: SEQUENCE SET; Schema: public; Owner: redmine
--

SELECT pg_catalog.setval('changes_id_seq', 1, false);


--
-- Data for Name: changeset_parents; Type: TABLE DATA; Schema: public; Owner: redmine
--



--
-- Data for Name: changesets; Type: TABLE DATA; Schema: public; Owner: redmine
--



--
-- Name: changesets_id_seq; Type: SEQUENCE SET; Schema: public; Owner: redmine
--

SELECT pg_catalog.setval('changesets_id_seq', 1, false);


--
-- Data for Name: changesets_issues; Type: TABLE DATA; Schema: public; Owner: redmine
--



--
-- Data for Name: comments; Type: TABLE DATA; Schema: public; Owner: redmine
--



--
-- Name: comments_id_seq; Type: SEQUENCE SET; Schema: public; Owner: redmine
--

SELECT pg_catalog.setval('comments_id_seq', 1, false);


--
-- Data for Name: custom_field_enumerations; Type: TABLE DATA; Schema: public; Owner: redmine
--



--
-- Name: custom_field_enumerations_id_seq; Type: SEQUENCE SET; Schema: public; Owner: redmine
--

SELECT pg_catalog.setval('custom_field_enumerations_id_seq', 1, false);


--
-- Data for Name: custom_fields; Type: TABLE DATA; Schema: public; Owner: redmine
--

INSERT INTO custom_fields (id, type, name, field_format, possible_values, regexp, min_length, max_length, is_required, is_for_all, is_filter, "position", searchable, default_value, editable, visible, multiple, format_store, description) VALUES (2, 'IssueCustomField', 'lat', 'float', NULL, '', NULL, NULL, true, false, false, 1, false, '', true, true, false, '--- !ruby/hash:ActiveSupport::HashWithIndifferentAccess
url_pattern: ''''
', 'latitude
-90 (south) .. 90 (north)');
INSERT INTO custom_fields (id, type, name, field_format, possible_values, regexp, min_length, max_length, is_required, is_for_all, is_filter, "position", searchable, default_value, editable, visible, multiple, format_store, description) VALUES (3, 'IssueCustomField', 'lon', 'float', NULL, '', NULL, NULL, true, false, false, 2, false, '', true, true, false, '--- !ruby/hash:ActiveSupport::HashWithIndifferentAccess
url_pattern: ''''
', 'longitude
-180 (west) .. 180 (east)');
INSERT INTO custom_fields (id, type, name, field_format, possible_values, regexp, min_length, max_length, is_required, is_for_all, is_filter, "position", searchable, default_value, editable, visible, multiple, format_store, description) VALUES (4, 'UserCustomField', 'icon', 'text', NULL, '', NULL, NULL, false, false, false, 1, false, '', true, true, false, '--- !ruby/hash:ActiveSupport::HashWithIndifferentAccess
text_formatting: ''''
', '');
INSERT INTO custom_fields (id, type, name, field_format, possible_values, regexp, min_length, max_length, is_required, is_for_all, is_filter, "position", searchable, default_value, editable, visible, multiple, format_store, description) VALUES (5, 'IssueCustomField', 'img', 'string', NULL, '', NULL, NULL, false, true, false, 3, false, '', true, true, false, '--- !ruby/hash:ActiveSupport::HashWithIndifferentAccess
text_formatting: ''''
url_pattern: ''''
', '');


--
-- Name: custom_fields_id_seq; Type: SEQUENCE SET; Schema: public; Owner: redmine
--

SELECT pg_catalog.setval('custom_fields_id_seq', 5, true);


--
-- Data for Name: custom_fields_projects; Type: TABLE DATA; Schema: public; Owner: redmine
--

INSERT INTO custom_fields_projects (custom_field_id, project_id) VALUES (2, 4);
INSERT INTO custom_fields_projects (custom_field_id, project_id) VALUES (3, 4);


--
-- Data for Name: custom_fields_roles; Type: TABLE DATA; Schema: public; Owner: redmine
--



--
-- Data for Name: custom_fields_trackers; Type: TABLE DATA; Schema: public; Owner: redmine
--

INSERT INTO custom_fields_trackers (custom_field_id, tracker_id) VALUES (2, 1);
INSERT INTO custom_fields_trackers (custom_field_id, tracker_id) VALUES (3, 1);
INSERT INTO custom_fields_trackers (custom_field_id, tracker_id) VALUES (5, 1);


--
-- Data for Name: custom_values; Type: TABLE DATA; Schema: public; Owner: redmine
--



--
-- Name: custom_values_id_seq; Type: SEQUENCE SET; Schema: public; Owner: redmine
--

SELECT pg_catalog.setval('custom_values_id_seq', 17, true);


--
-- Data for Name: documents; Type: TABLE DATA; Schema: public; Owner: redmine
--



--
-- Name: documents_id_seq; Type: SEQUENCE SET; Schema: public; Owner: redmine
--

SELECT pg_catalog.setval('documents_id_seq', 1, false);


--
-- Data for Name: email_addresses; Type: TABLE DATA; Schema: public; Owner: redmine
--

INSERT INTO email_addresses (id, user_id, address, is_default, notify, created_on, updated_on) VALUES (1, 1, 'admin@example.net', true, true, '2016-04-26 16:24:38.944985', '2016-04-26 16:24:38.944985');


--
-- Name: email_addresses_id_seq; Type: SEQUENCE SET; Schema: public; Owner: redmine
--

SELECT pg_catalog.setval('email_addresses_id_seq', 14, true);


--
-- Data for Name: enabled_modules; Type: TABLE DATA; Schema: public; Owner: redmine
--

INSERT INTO enabled_modules (id, project_id, name) VALUES (32, 4, 'issue_tracking');


--
-- Name: enabled_modules_id_seq; Type: SEQUENCE SET; Schema: public; Owner: redmine
--

SELECT pg_catalog.setval('enabled_modules_id_seq', 41, true);


--
-- Data for Name: enumerations; Type: TABLE DATA; Schema: public; Owner: redmine
--

INSERT INTO enumerations (id, name, "position", is_default, type, active, project_id, parent_id, position_name) VALUES (2, 'MustHave', 1, false, 'IssuePriority', true, NULL, NULL, 'lowest');
INSERT INTO enumerations (id, name, "position", is_default, type, active, project_id, parent_id, position_name) VALUES (3, 'ShouldHave', 2, false, 'IssuePriority', true, NULL, NULL, 'default');
INSERT INTO enumerations (id, name, "position", is_default, type, active, project_id, parent_id, position_name) VALUES (4, 'CouldHave', 3, false, 'IssuePriority', true, NULL, NULL, 'high2');
INSERT INTO enumerations (id, name, "position", is_default, type, active, project_id, parent_id, position_name) VALUES (5, 'WantToHave', 4, false, 'IssuePriority', true, NULL, NULL, 'highest');


--
-- Name: enumerations_id_seq; Type: SEQUENCE SET; Schema: public; Owner: redmine
--

SELECT pg_catalog.setval('enumerations_id_seq', 5, true);


--
-- Data for Name: groups_users; Type: TABLE DATA; Schema: public; Owner: redmine
--



--
-- Data for Name: import_items; Type: TABLE DATA; Schema: public; Owner: redmine
--



--
-- Name: import_items_id_seq; Type: SEQUENCE SET; Schema: public; Owner: redmine
--

SELECT pg_catalog.setval('import_items_id_seq', 1, false);


--
-- Data for Name: imports; Type: TABLE DATA; Schema: public; Owner: redmine
--



--
-- Name: imports_id_seq; Type: SEQUENCE SET; Schema: public; Owner: redmine
--

SELECT pg_catalog.setval('imports_id_seq', 1, false);


--
-- Data for Name: issue_categories; Type: TABLE DATA; Schema: public; Owner: redmine
--

INSERT INTO issue_categories (id, project_id, name, assigned_to_id) VALUES (1, 4, 'test', NULL);


--
-- Name: issue_categories_id_seq; Type: SEQUENCE SET; Schema: public; Owner: redmine
--

SELECT pg_catalog.setval('issue_categories_id_seq', 1, true);


--
-- Data for Name: issue_relations; Type: TABLE DATA; Schema: public; Owner: redmine
--



--
-- Name: issue_relations_id_seq; Type: SEQUENCE SET; Schema: public; Owner: redmine
--

SELECT pg_catalog.setval('issue_relations_id_seq', 1, false);


--
-- Data for Name: issue_statuses; Type: TABLE DATA; Schema: public; Owner: redmine
--

INSERT INTO issue_statuses (id, name, is_closed, "position", default_done_ratio) VALUES (1, 'awesome', false, 1, NULL);
INSERT INTO issue_statuses (id, name, is_closed, "position", default_done_ratio) VALUES (2, 'Reported', false, 2, NULL);
INSERT INTO issue_statuses (id, name, is_closed, "position", default_done_ratio) VALUES (3, 'Accepted', false, 3, NULL);
INSERT INTO issue_statuses (id, name, is_closed, "position", default_done_ratio) VALUES (4, 'Rejected', true, 4, NULL);
INSERT INTO issue_statuses (id, name, is_closed, "position", default_done_ratio) VALUES (5, 'Fixed', true, 5, NULL);


--
-- Name: issue_statuses_id_seq; Type: SEQUENCE SET; Schema: public; Owner: redmine
--

SELECT pg_catalog.setval('issue_statuses_id_seq', 5, true);


--
-- Data for Name: issues; Type: TABLE DATA; Schema: public; Owner: redmine
--



--
-- Name: issues_id_seq; Type: SEQUENCE SET; Schema: public; Owner: redmine
--

SELECT pg_catalog.setval('issues_id_seq', 2, true);


--
-- Data for Name: journal_details; Type: TABLE DATA; Schema: public; Owner: redmine
--



--
-- Name: journal_details_id_seq; Type: SEQUENCE SET; Schema: public; Owner: redmine
--

SELECT pg_catalog.setval('journal_details_id_seq', 5, true);


--
-- Data for Name: journals; Type: TABLE DATA; Schema: public; Owner: redmine
--



--
-- Name: journals_id_seq; Type: SEQUENCE SET; Schema: public; Owner: redmine
--

SELECT pg_catalog.setval('journals_id_seq', 2, true);


--
-- Data for Name: member_roles; Type: TABLE DATA; Schema: public; Owner: redmine
--

INSERT INTO member_roles (id, member_id, role_id, inherited_from) VALUES (1, 1, 3, NULL);


--
-- Name: member_roles_id_seq; Type: SEQUENCE SET; Schema: public; Owner: redmine
--

SELECT pg_catalog.setval('member_roles_id_seq', 1, true);


--
-- Data for Name: members; Type: TABLE DATA; Schema: public; Owner: redmine
--

INSERT INTO members (id, user_id, project_id, created_on, mail_notification) VALUES (1, 1, 4, '2016-04-27 00:37:27.462984', false);


--
-- Name: members_id_seq; Type: SEQUENCE SET; Schema: public; Owner: redmine
--

SELECT pg_catalog.setval('members_id_seq', 1, true);


--
-- Data for Name: messages; Type: TABLE DATA; Schema: public; Owner: redmine
--



--
-- Name: messages_id_seq; Type: SEQUENCE SET; Schema: public; Owner: redmine
--

SELECT pg_catalog.setval('messages_id_seq', 1, false);


--
-- Data for Name: news; Type: TABLE DATA; Schema: public; Owner: redmine
--



--
-- Name: news_id_seq; Type: SEQUENCE SET; Schema: public; Owner: redmine
--

SELECT pg_catalog.setval('news_id_seq', 1, false);


--
-- Data for Name: open_id_authentication_associations; Type: TABLE DATA; Schema: public; Owner: redmine
--



--
-- Name: open_id_authentication_associations_id_seq; Type: SEQUENCE SET; Schema: public; Owner: redmine
--

SELECT pg_catalog.setval('open_id_authentication_associations_id_seq', 1, false);


--
-- Data for Name: open_id_authentication_nonces; Type: TABLE DATA; Schema: public; Owner: redmine
--



--
-- Name: open_id_authentication_nonces_id_seq; Type: SEQUENCE SET; Schema: public; Owner: redmine
--

SELECT pg_catalog.setval('open_id_authentication_nonces_id_seq', 1, false);


--
-- Data for Name: projects; Type: TABLE DATA; Schema: public; Owner: redmine
--

INSERT INTO projects (id, name, description, homepage, is_public, parent_id, created_on, updated_on, identifier, status, lft, rgt, inherit_members, default_version_id) VALUES (4, 'SpatialProject', '', '', true, NULL, '2016-04-26 23:24:58.412119', '2016-05-18 05:38:19.082235', 'id', 1, 1, 2, false, NULL);


--
-- Name: projects_id_seq; Type: SEQUENCE SET; Schema: public; Owner: redmine
--

SELECT pg_catalog.setval('projects_id_seq', 4, true);


--
-- Data for Name: projects_trackers; Type: TABLE DATA; Schema: public; Owner: redmine
--

INSERT INTO projects_trackers (project_id, tracker_id) VALUES (4, 1);


--
-- Data for Name: queries; Type: TABLE DATA; Schema: public; Owner: redmine
--



--
-- Name: queries_id_seq; Type: SEQUENCE SET; Schema: public; Owner: redmine
--

SELECT pg_catalog.setval('queries_id_seq', 1, false);


--
-- Data for Name: queries_roles; Type: TABLE DATA; Schema: public; Owner: redmine
--



--
-- Data for Name: repositories; Type: TABLE DATA; Schema: public; Owner: redmine
--



--
-- Name: repositories_id_seq; Type: SEQUENCE SET; Schema: public; Owner: redmine
--

SELECT pg_catalog.setval('repositories_id_seq', 1, false);


--
-- Data for Name: roles; Type: TABLE DATA; Schema: public; Owner: redmine
--

INSERT INTO roles (id, name, "position", assignable, builtin, permissions, issues_visibility, users_visibility, time_entries_visibility, all_roles_managed) VALUES (1, 'Non member', 1, true, 1, '---
- :view_issues
', 'default', 'all', 'all', true);
INSERT INTO roles (id, name, "position", assignable, builtin, permissions, issues_visibility, users_visibility, time_entries_visibility, all_roles_managed) VALUES (2, 'Anonymous', 2, true, 2, '---
- :view_issues
', 'default', 'all', 'all', true);
INSERT INTO roles (id, name, "position", assignable, builtin, permissions, issues_visibility, users_visibility, time_entries_visibility, all_roles_managed) VALUES (3, 'deus ex machina', 3, true, 0, '---
- :add_project
- :edit_project
- :close_project
- :select_project_modules
- :manage_members
- :manage_versions
- :add_subprojects
- :manage_boards
- :add_messages
- :edit_messages
- :edit_own_messages
- :delete_messages
- :delete_own_messages
- :view_calendar
- :add_documents
- :edit_documents
- :delete_documents
- :view_documents
- :manage_files
- :view_files
- :view_gantt
- :manage_categories
- :view_issues
- :add_issues
- :edit_issues
- :copy_issues
- :manage_issue_relations
- :manage_subtasks
- :set_issues_private
- :set_own_issues_private
- :add_issue_notes
- :edit_issue_notes
- :edit_own_issue_notes
- :view_private_notes
- :set_notes_private
- :delete_issues
- :manage_public_queries
- :save_queries
- :view_issue_watchers
- :add_issue_watchers
- :delete_issue_watchers
- :import_issues
- :manage_news
- :comment_news
- :manage_repository
- :browse_repository
- :view_changesets
- :commit_access
- :manage_related_issues
- :log_time
- :view_time_entries
- :edit_time_entries
- :edit_own_time_entries
- :manage_project_activities
- :manage_wiki
- :rename_wiki_pages
- :delete_wiki_pages
- :view_wiki_pages
- :export_wiki_pages
- :view_wiki_edits
- :edit_wiki_pages
- :delete_wiki_pages_attachments
- :protect_wiki_pages
', 'all', 'all', 'all', true);


--
-- Name: roles_id_seq; Type: SEQUENCE SET; Schema: public; Owner: redmine
--

SELECT pg_catalog.setval('roles_id_seq', 4, true);


--
-- Data for Name: roles_managed_roles; Type: TABLE DATA; Schema: public; Owner: redmine
--



--
-- Data for Name: schema_migrations; Type: TABLE DATA; Schema: public; Owner: redmine
--

INSERT INTO schema_migrations (version) VALUES ('1');
INSERT INTO schema_migrations (version) VALUES ('2');
INSERT INTO schema_migrations (version) VALUES ('3');
INSERT INTO schema_migrations (version) VALUES ('4');
INSERT INTO schema_migrations (version) VALUES ('5');
INSERT INTO schema_migrations (version) VALUES ('6');
INSERT INTO schema_migrations (version) VALUES ('7');
INSERT INTO schema_migrations (version) VALUES ('8');
INSERT INTO schema_migrations (version) VALUES ('9');
INSERT INTO schema_migrations (version) VALUES ('10');
INSERT INTO schema_migrations (version) VALUES ('11');
INSERT INTO schema_migrations (version) VALUES ('12');
INSERT INTO schema_migrations (version) VALUES ('13');
INSERT INTO schema_migrations (version) VALUES ('14');
INSERT INTO schema_migrations (version) VALUES ('15');
INSERT INTO schema_migrations (version) VALUES ('16');
INSERT INTO schema_migrations (version) VALUES ('17');
INSERT INTO schema_migrations (version) VALUES ('18');
INSERT INTO schema_migrations (version) VALUES ('19');
INSERT INTO schema_migrations (version) VALUES ('20');
INSERT INTO schema_migrations (version) VALUES ('21');
INSERT INTO schema_migrations (version) VALUES ('22');
INSERT INTO schema_migrations (version) VALUES ('23');
INSERT INTO schema_migrations (version) VALUES ('24');
INSERT INTO schema_migrations (version) VALUES ('25');
INSERT INTO schema_migrations (version) VALUES ('26');
INSERT INTO schema_migrations (version) VALUES ('27');
INSERT INTO schema_migrations (version) VALUES ('28');
INSERT INTO schema_migrations (version) VALUES ('29');
INSERT INTO schema_migrations (version) VALUES ('30');
INSERT INTO schema_migrations (version) VALUES ('31');
INSERT INTO schema_migrations (version) VALUES ('32');
INSERT INTO schema_migrations (version) VALUES ('33');
INSERT INTO schema_migrations (version) VALUES ('34');
INSERT INTO schema_migrations (version) VALUES ('35');
INSERT INTO schema_migrations (version) VALUES ('36');
INSERT INTO schema_migrations (version) VALUES ('37');
INSERT INTO schema_migrations (version) VALUES ('38');
INSERT INTO schema_migrations (version) VALUES ('39');
INSERT INTO schema_migrations (version) VALUES ('40');
INSERT INTO schema_migrations (version) VALUES ('41');
INSERT INTO schema_migrations (version) VALUES ('42');
INSERT INTO schema_migrations (version) VALUES ('43');
INSERT INTO schema_migrations (version) VALUES ('44');
INSERT INTO schema_migrations (version) VALUES ('45');
INSERT INTO schema_migrations (version) VALUES ('46');
INSERT INTO schema_migrations (version) VALUES ('47');
INSERT INTO schema_migrations (version) VALUES ('48');
INSERT INTO schema_migrations (version) VALUES ('49');
INSERT INTO schema_migrations (version) VALUES ('50');
INSERT INTO schema_migrations (version) VALUES ('51');
INSERT INTO schema_migrations (version) VALUES ('52');
INSERT INTO schema_migrations (version) VALUES ('53');
INSERT INTO schema_migrations (version) VALUES ('54');
INSERT INTO schema_migrations (version) VALUES ('55');
INSERT INTO schema_migrations (version) VALUES ('56');
INSERT INTO schema_migrations (version) VALUES ('57');
INSERT INTO schema_migrations (version) VALUES ('58');
INSERT INTO schema_migrations (version) VALUES ('59');
INSERT INTO schema_migrations (version) VALUES ('60');
INSERT INTO schema_migrations (version) VALUES ('61');
INSERT INTO schema_migrations (version) VALUES ('62');
INSERT INTO schema_migrations (version) VALUES ('63');
INSERT INTO schema_migrations (version) VALUES ('64');
INSERT INTO schema_migrations (version) VALUES ('65');
INSERT INTO schema_migrations (version) VALUES ('66');
INSERT INTO schema_migrations (version) VALUES ('67');
INSERT INTO schema_migrations (version) VALUES ('68');
INSERT INTO schema_migrations (version) VALUES ('69');
INSERT INTO schema_migrations (version) VALUES ('70');
INSERT INTO schema_migrations (version) VALUES ('71');
INSERT INTO schema_migrations (version) VALUES ('72');
INSERT INTO schema_migrations (version) VALUES ('73');
INSERT INTO schema_migrations (version) VALUES ('74');
INSERT INTO schema_migrations (version) VALUES ('75');
INSERT INTO schema_migrations (version) VALUES ('76');
INSERT INTO schema_migrations (version) VALUES ('77');
INSERT INTO schema_migrations (version) VALUES ('78');
INSERT INTO schema_migrations (version) VALUES ('79');
INSERT INTO schema_migrations (version) VALUES ('80');
INSERT INTO schema_migrations (version) VALUES ('81');
INSERT INTO schema_migrations (version) VALUES ('82');
INSERT INTO schema_migrations (version) VALUES ('83');
INSERT INTO schema_migrations (version) VALUES ('84');
INSERT INTO schema_migrations (version) VALUES ('85');
INSERT INTO schema_migrations (version) VALUES ('86');
INSERT INTO schema_migrations (version) VALUES ('87');
INSERT INTO schema_migrations (version) VALUES ('88');
INSERT INTO schema_migrations (version) VALUES ('89');
INSERT INTO schema_migrations (version) VALUES ('90');
INSERT INTO schema_migrations (version) VALUES ('91');
INSERT INTO schema_migrations (version) VALUES ('92');
INSERT INTO schema_migrations (version) VALUES ('93');
INSERT INTO schema_migrations (version) VALUES ('94');
INSERT INTO schema_migrations (version) VALUES ('95');
INSERT INTO schema_migrations (version) VALUES ('96');
INSERT INTO schema_migrations (version) VALUES ('97');
INSERT INTO schema_migrations (version) VALUES ('98');
INSERT INTO schema_migrations (version) VALUES ('99');
INSERT INTO schema_migrations (version) VALUES ('100');
INSERT INTO schema_migrations (version) VALUES ('101');
INSERT INTO schema_migrations (version) VALUES ('102');
INSERT INTO schema_migrations (version) VALUES ('103');
INSERT INTO schema_migrations (version) VALUES ('104');
INSERT INTO schema_migrations (version) VALUES ('105');
INSERT INTO schema_migrations (version) VALUES ('106');
INSERT INTO schema_migrations (version) VALUES ('107');
INSERT INTO schema_migrations (version) VALUES ('108');
INSERT INTO schema_migrations (version) VALUES ('20090214190337');
INSERT INTO schema_migrations (version) VALUES ('20090312172426');
INSERT INTO schema_migrations (version) VALUES ('20090312194159');
INSERT INTO schema_migrations (version) VALUES ('20090318181151');
INSERT INTO schema_migrations (version) VALUES ('20090323224724');
INSERT INTO schema_migrations (version) VALUES ('20090401221305');
INSERT INTO schema_migrations (version) VALUES ('20090401231134');
INSERT INTO schema_migrations (version) VALUES ('20090403001910');
INSERT INTO schema_migrations (version) VALUES ('20090406161854');
INSERT INTO schema_migrations (version) VALUES ('20090425161243');
INSERT INTO schema_migrations (version) VALUES ('20090503121501');
INSERT INTO schema_migrations (version) VALUES ('20090503121505');
INSERT INTO schema_migrations (version) VALUES ('20090503121510');
INSERT INTO schema_migrations (version) VALUES ('20090614091200');
INSERT INTO schema_migrations (version) VALUES ('20090704172350');
INSERT INTO schema_migrations (version) VALUES ('20090704172355');
INSERT INTO schema_migrations (version) VALUES ('20090704172358');
INSERT INTO schema_migrations (version) VALUES ('20091010093521');
INSERT INTO schema_migrations (version) VALUES ('20091017212227');
INSERT INTO schema_migrations (version) VALUES ('20091017212457');
INSERT INTO schema_migrations (version) VALUES ('20091017212644');
INSERT INTO schema_migrations (version) VALUES ('20091017212938');
INSERT INTO schema_migrations (version) VALUES ('20091017213027');
INSERT INTO schema_migrations (version) VALUES ('20091017213113');
INSERT INTO schema_migrations (version) VALUES ('20091017213151');
INSERT INTO schema_migrations (version) VALUES ('20091017213228');
INSERT INTO schema_migrations (version) VALUES ('20091017213257');
INSERT INTO schema_migrations (version) VALUES ('20091017213332');
INSERT INTO schema_migrations (version) VALUES ('20091017213444');
INSERT INTO schema_migrations (version) VALUES ('20091017213536');
INSERT INTO schema_migrations (version) VALUES ('20091017213642');
INSERT INTO schema_migrations (version) VALUES ('20091017213716');
INSERT INTO schema_migrations (version) VALUES ('20091017213757');
INSERT INTO schema_migrations (version) VALUES ('20091017213835');
INSERT INTO schema_migrations (version) VALUES ('20091017213910');
INSERT INTO schema_migrations (version) VALUES ('20091017214015');
INSERT INTO schema_migrations (version) VALUES ('20091017214107');
INSERT INTO schema_migrations (version) VALUES ('20091017214136');
INSERT INTO schema_migrations (version) VALUES ('20091017214236');
INSERT INTO schema_migrations (version) VALUES ('20091017214308');
INSERT INTO schema_migrations (version) VALUES ('20091017214336');
INSERT INTO schema_migrations (version) VALUES ('20091017214406');
INSERT INTO schema_migrations (version) VALUES ('20091017214440');
INSERT INTO schema_migrations (version) VALUES ('20091017214519');
INSERT INTO schema_migrations (version) VALUES ('20091017214611');
INSERT INTO schema_migrations (version) VALUES ('20091017214644');
INSERT INTO schema_migrations (version) VALUES ('20091017214720');
INSERT INTO schema_migrations (version) VALUES ('20091017214750');
INSERT INTO schema_migrations (version) VALUES ('20091025163651');
INSERT INTO schema_migrations (version) VALUES ('20091108092559');
INSERT INTO schema_migrations (version) VALUES ('20091114105931');
INSERT INTO schema_migrations (version) VALUES ('20091123212029');
INSERT INTO schema_migrations (version) VALUES ('20091205124427');
INSERT INTO schema_migrations (version) VALUES ('20091220183509');
INSERT INTO schema_migrations (version) VALUES ('20091220183727');
INSERT INTO schema_migrations (version) VALUES ('20091220184736');
INSERT INTO schema_migrations (version) VALUES ('20091225164732');
INSERT INTO schema_migrations (version) VALUES ('20091227112908');
INSERT INTO schema_migrations (version) VALUES ('20100129193402');
INSERT INTO schema_migrations (version) VALUES ('20100129193813');
INSERT INTO schema_migrations (version) VALUES ('20100221100219');
INSERT INTO schema_migrations (version) VALUES ('20100313132032');
INSERT INTO schema_migrations (version) VALUES ('20100313171051');
INSERT INTO schema_migrations (version) VALUES ('20100705164950');
INSERT INTO schema_migrations (version) VALUES ('20100819172912');
INSERT INTO schema_migrations (version) VALUES ('20101104182107');
INSERT INTO schema_migrations (version) VALUES ('20101107130441');
INSERT INTO schema_migrations (version) VALUES ('20101114115114');
INSERT INTO schema_migrations (version) VALUES ('20101114115359');
INSERT INTO schema_migrations (version) VALUES ('20110220160626');
INSERT INTO schema_migrations (version) VALUES ('20110223180944');
INSERT INTO schema_migrations (version) VALUES ('20110223180953');
INSERT INTO schema_migrations (version) VALUES ('20110224000000');
INSERT INTO schema_migrations (version) VALUES ('20110226120112');
INSERT INTO schema_migrations (version) VALUES ('20110226120132');
INSERT INTO schema_migrations (version) VALUES ('20110227125750');
INSERT INTO schema_migrations (version) VALUES ('20110228000000');
INSERT INTO schema_migrations (version) VALUES ('20110228000100');
INSERT INTO schema_migrations (version) VALUES ('20110401192910');
INSERT INTO schema_migrations (version) VALUES ('20110408103312');
INSERT INTO schema_migrations (version) VALUES ('20110412065600');
INSERT INTO schema_migrations (version) VALUES ('20110511000000');
INSERT INTO schema_migrations (version) VALUES ('20110902000000');
INSERT INTO schema_migrations (version) VALUES ('20111201201315');
INSERT INTO schema_migrations (version) VALUES ('20120115143024');
INSERT INTO schema_migrations (version) VALUES ('20120115143100');
INSERT INTO schema_migrations (version) VALUES ('20120115143126');
INSERT INTO schema_migrations (version) VALUES ('20120127174243');
INSERT INTO schema_migrations (version) VALUES ('20120205111326');
INSERT INTO schema_migrations (version) VALUES ('20120223110929');
INSERT INTO schema_migrations (version) VALUES ('20120301153455');
INSERT INTO schema_migrations (version) VALUES ('20120422150750');
INSERT INTO schema_migrations (version) VALUES ('20120705074331');
INSERT INTO schema_migrations (version) VALUES ('20120707064544');
INSERT INTO schema_migrations (version) VALUES ('20120714122000');
INSERT INTO schema_migrations (version) VALUES ('20120714122100');
INSERT INTO schema_migrations (version) VALUES ('20120714122200');
INSERT INTO schema_migrations (version) VALUES ('20120731164049');
INSERT INTO schema_migrations (version) VALUES ('20120930112914');
INSERT INTO schema_migrations (version) VALUES ('20121026002032');
INSERT INTO schema_migrations (version) VALUES ('20121026003537');
INSERT INTO schema_migrations (version) VALUES ('20121209123234');
INSERT INTO schema_migrations (version) VALUES ('20121209123358');
INSERT INTO schema_migrations (version) VALUES ('20121213084931');
INSERT INTO schema_migrations (version) VALUES ('20130110122628');
INSERT INTO schema_migrations (version) VALUES ('20130201184705');
INSERT INTO schema_migrations (version) VALUES ('20130202090625');
INSERT INTO schema_migrations (version) VALUES ('20130207175206');
INSERT INTO schema_migrations (version) VALUES ('20130207181455');
INSERT INTO schema_migrations (version) VALUES ('20130215073721');
INSERT INTO schema_migrations (version) VALUES ('20130215111127');
INSERT INTO schema_migrations (version) VALUES ('20130215111141');
INSERT INTO schema_migrations (version) VALUES ('20130217094251');
INSERT INTO schema_migrations (version) VALUES ('20130602092539');
INSERT INTO schema_migrations (version) VALUES ('20130710182539');
INSERT INTO schema_migrations (version) VALUES ('20130713104233');
INSERT INTO schema_migrations (version) VALUES ('20130713111657');
INSERT INTO schema_migrations (version) VALUES ('20130729070143');
INSERT INTO schema_migrations (version) VALUES ('20130911193200');
INSERT INTO schema_migrations (version) VALUES ('20131004113137');
INSERT INTO schema_migrations (version) VALUES ('20131005100610');
INSERT INTO schema_migrations (version) VALUES ('20131124175346');
INSERT INTO schema_migrations (version) VALUES ('20131210180802');
INSERT INTO schema_migrations (version) VALUES ('20131214094309');
INSERT INTO schema_migrations (version) VALUES ('20131215104612');
INSERT INTO schema_migrations (version) VALUES ('20131218183023');
INSERT INTO schema_migrations (version) VALUES ('20140228130325');
INSERT INTO schema_migrations (version) VALUES ('20140903143914');
INSERT INTO schema_migrations (version) VALUES ('20140920094058');
INSERT INTO schema_migrations (version) VALUES ('20141029181752');
INSERT INTO schema_migrations (version) VALUES ('20141029181824');
INSERT INTO schema_migrations (version) VALUES ('20141109112308');
INSERT INTO schema_migrations (version) VALUES ('20141122124142');
INSERT INTO schema_migrations (version) VALUES ('20150113194759');
INSERT INTO schema_migrations (version) VALUES ('20150113211532');
INSERT INTO schema_migrations (version) VALUES ('20150113213922');
INSERT INTO schema_migrations (version) VALUES ('20150113213955');
INSERT INTO schema_migrations (version) VALUES ('20150208105930');
INSERT INTO schema_migrations (version) VALUES ('20150510083747');
INSERT INTO schema_migrations (version) VALUES ('20150525103953');
INSERT INTO schema_migrations (version) VALUES ('20150526183158');
INSERT INTO schema_migrations (version) VALUES ('20150528084820');
INSERT INTO schema_migrations (version) VALUES ('20150528092912');
INSERT INTO schema_migrations (version) VALUES ('20150528093249');
INSERT INTO schema_migrations (version) VALUES ('20150725112753');
INSERT INTO schema_migrations (version) VALUES ('20150730122707');
INSERT INTO schema_migrations (version) VALUES ('20150730122735');
INSERT INTO schema_migrations (version) VALUES ('20150921204850');
INSERT INTO schema_migrations (version) VALUES ('20150921210243');
INSERT INTO schema_migrations (version) VALUES ('20151020182334');
INSERT INTO schema_migrations (version) VALUES ('20151020182731');
INSERT INTO schema_migrations (version) VALUES ('20151021184614');
INSERT INTO schema_migrations (version) VALUES ('20151021185456');
INSERT INTO schema_migrations (version) VALUES ('20151021190616');
INSERT INTO schema_migrations (version) VALUES ('20151024082034');
INSERT INTO schema_migrations (version) VALUES ('20151025072118');
INSERT INTO schema_migrations (version) VALUES ('20151031095005');


--
-- Data for Name: settings; Type: TABLE DATA; Schema: public; Owner: redmine
--

INSERT INTO settings (id, name, value, updated_on) VALUES (1, 'rest_api_enabled', '1', '2016-04-26 17:11:41.937005');
INSERT INTO settings (id, name, value, updated_on) VALUES (2, 'jsonp_enabled', '0', '2016-04-26 17:11:41.945565');
INSERT INTO settings (id, name, value, updated_on) VALUES (4, 'default_language', 'en', '2016-05-11 09:28:43.22323');
INSERT INTO settings (id, name, value, updated_on) VALUES (5, 'force_default_language_for_anonymous', '0', '2016-05-11 09:28:43.228712');
INSERT INTO settings (id, name, value, updated_on) VALUES (6, 'force_default_language_for_loggedin', '0', '2016-05-11 09:28:43.23448');
INSERT INTO settings (id, name, value, updated_on) VALUES (7, 'start_of_week', '', '2016-05-11 09:28:43.239535');
INSERT INTO settings (id, name, value, updated_on) VALUES (8, 'date_format', '', '2016-05-11 09:28:43.245709');
INSERT INTO settings (id, name, value, updated_on) VALUES (9, 'time_format', '', '2016-05-11 09:28:43.252606');
INSERT INTO settings (id, name, value, updated_on) VALUES (10, 'user_format', 'firstname_lastname', '2016-05-11 09:28:43.257788');
INSERT INTO settings (id, name, value, updated_on) VALUES (11, 'gravatar_enabled', '0', '2016-05-11 09:28:43.263379');
INSERT INTO settings (id, name, value, updated_on) VALUES (12, 'gravatar_default', '', '2016-05-11 09:28:43.26956');
INSERT INTO settings (id, name, value, updated_on) VALUES (13, 'thumbnails_enabled', '0', '2016-05-11 09:28:43.274573');
INSERT INTO settings (id, name, value, updated_on) VALUES (14, 'thumbnails_size', '100', '2016-05-11 09:28:43.279119');
INSERT INTO settings (id, name, value, updated_on) VALUES (3, 'ui_theme', '', '2016-05-11 09:28:55.816158');
INSERT INTO settings (id, name, value, updated_on) VALUES (15, 'enabled_scm', '--- []
', '2016-05-11 09:30:07.825798');
INSERT INTO settings (id, name, value, updated_on) VALUES (16, 'autofetch_changesets', '0', '2016-05-11 09:30:07.832142');
INSERT INTO settings (id, name, value, updated_on) VALUES (17, 'sys_api_enabled', '0', '2016-05-11 09:30:07.837443');
INSERT INTO settings (id, name, value, updated_on) VALUES (18, 'repository_log_display_limit', '100', '2016-05-11 09:30:07.843375');
INSERT INTO settings (id, name, value, updated_on) VALUES (19, 'commit_ref_keywords', 'refs,references,IssueID', '2016-05-11 09:30:07.848469');
INSERT INTO settings (id, name, value, updated_on) VALUES (20, 'commit_cross_project_ref', '0', '2016-05-11 09:30:07.854119');
INSERT INTO settings (id, name, value, updated_on) VALUES (21, 'commit_logtime_enabled', '0', '2016-05-11 09:30:07.859298');
INSERT INTO settings (id, name, value, updated_on) VALUES (22, 'commit_update_keywords', '--- []
', '2016-05-11 09:30:07.864254');
INSERT INTO settings (id, name, value, updated_on) VALUES (24, 'autologin', '0', '2016-05-11 09:36:10.972112');
INSERT INTO settings (id, name, value, updated_on) VALUES (26, 'unsubscribe', '1', '2016-05-11 09:36:10.985196');
INSERT INTO settings (id, name, value, updated_on) VALUES (27, 'password_min_length', '3', '2016-05-11 09:36:10.9923');
INSERT INTO settings (id, name, value, updated_on) VALUES (28, 'password_max_age', '0', '2016-05-11 09:36:10.997665');
INSERT INTO settings (id, name, value, updated_on) VALUES (29, 'lost_password', '1', '2016-05-11 09:36:11.002868');
INSERT INTO settings (id, name, value, updated_on) VALUES (30, 'max_additional_emails', '5', '2016-05-11 09:36:11.007484');
INSERT INTO settings (id, name, value, updated_on) VALUES (31, 'openid', '0', '2016-05-11 09:36:11.013427');
INSERT INTO settings (id, name, value, updated_on) VALUES (32, 'session_lifetime', '0', '2016-05-11 09:36:11.019008');
INSERT INTO settings (id, name, value, updated_on) VALUES (33, 'session_timeout', '0', '2016-05-11 09:36:11.023859');
INSERT INTO settings (id, name, value, updated_on) VALUES (35, 'default_projects_public', '0', '2016-05-11 09:38:26.046877');
INSERT INTO settings (id, name, value, updated_on) VALUES (36, 'default_projects_modules', '--- []
', '2016-05-11 09:38:26.052864');
INSERT INTO settings (id, name, value, updated_on) VALUES (37, 'default_projects_tracker_ids', '--- []
', '2016-05-11 09:38:26.059162');
INSERT INTO settings (id, name, value, updated_on) VALUES (38, 'sequential_project_identifiers', '0', '2016-05-11 09:38:26.065162');
INSERT INTO settings (id, name, value, updated_on) VALUES (23, 'login_required', '1', '2016-05-11 12:22:52.277326');
INSERT INTO settings (id, name, value, updated_on) VALUES (25, 'self_registration', '3', '2016-05-11 12:22:52.284274');
INSERT INTO settings (id, name, value, updated_on) VALUES (34, 'default_users_hide_mail', '0', '2016-05-11 16:11:55.476903');


--
-- Name: settings_id_seq; Type: SEQUENCE SET; Schema: public; Owner: redmine
--

SELECT pg_catalog.setval('settings_id_seq', 38, true);


--
-- Data for Name: time_entries; Type: TABLE DATA; Schema: public; Owner: redmine
--



--
-- Name: time_entries_id_seq; Type: SEQUENCE SET; Schema: public; Owner: redmine
--

SELECT pg_catalog.setval('time_entries_id_seq', 1, false);


--
-- Data for Name: tokens; Type: TABLE DATA; Schema: public; Owner: redmine
--

INSERT INTO tokens (id, user_id, action, value, created_on, updated_on) VALUES (2, 1, 'api', '264acfed33b8af628991dda4de64d75390854d82', '2016-04-26 17:19:41.968964', '2016-04-26 17:19:41.968964');
INSERT INTO tokens (id, user_id, action, value, created_on, updated_on) VALUES (3, 1, 'feeds', '8ba49241a84bbdb910ff3687be5e8cbd1469efdf', '2016-04-26 19:33:34.145378', '2016-04-26 19:33:34.145378');
INSERT INTO tokens (id, user_id, action, value, created_on, updated_on) VALUES (20, 1, 'session', 'eaf3b0b8d923818f6bf5a4fb2c747bc2a8fd9c90', '2016-05-11 16:36:29.253037', '2016-05-11 19:04:58.592782');
INSERT INTO tokens (id, user_id, action, value, created_on, updated_on) VALUES (23, 1, 'session', 'aaa39976393ca91f5b29a66e9e5355a4cba7ca32', '2016-05-17 18:49:35.260384', '2016-05-17 18:49:54.253754');
INSERT INTO tokens (id, user_id, action, value, created_on, updated_on) VALUES (1, 1, 'session', 'e12be39487f79741edaa35c032dd85d551181734', '2016-04-26 17:09:29.014253', '2016-04-26 20:10:28.118163');
INSERT INTO tokens (id, user_id, action, value, created_on, updated_on) VALUES (5, 1, 'session', '0574f3e7f521f8a5052334667c48f916a4354e6b', '2016-04-26 20:27:46.680884', '2016-04-27 00:43:41.396684');
INSERT INTO tokens (id, user_id, action, value, created_on, updated_on) VALUES (26, 1, 'session', 'c950cfbd76597f0445c882a27ffef27873e49660', '2016-05-18 05:27:46.646671', '2016-05-18 05:38:48.870217');
INSERT INTO tokens (id, user_id, action, value, created_on, updated_on) VALUES (6, 1, 'session', '7b65e690305b053d7d61ab129b52fed0413a3aa3', '2016-04-27 19:37:24.845145', '2016-04-27 19:46:57.708756');
INSERT INTO tokens (id, user_id, action, value, created_on, updated_on) VALUES (7, 1, 'session', '21e48d04509493c19e43f4b8b3dd72ff9bba8eaf', '2016-05-02 15:55:57.207219', '2016-05-02 15:57:58.341');


--
-- Name: tokens_id_seq; Type: SEQUENCE SET; Schema: public; Owner: redmine
--

SELECT pg_catalog.setval('tokens_id_seq', 26, true);


--
-- Data for Name: trackers; Type: TABLE DATA; Schema: public; Owner: redmine
--

INSERT INTO trackers (id, name, is_in_chlog, "position", is_in_roadmap, fields_bits, default_status_id) VALUES (1, 'SpatialTracker', false, 1, true, 239, 2);


--
-- Name: trackers_id_seq; Type: SEQUENCE SET; Schema: public; Owner: redmine
--

SELECT pg_catalog.setval('trackers_id_seq', 1, true);


--
-- Data for Name: user_preferences; Type: TABLE DATA; Schema: public; Owner: redmine
--

INSERT INTO user_preferences (id, user_id, others, hide_mail, time_zone) VALUES (1, 1, '--- {}
', true, NULL);


--
-- Name: user_preferences_id_seq; Type: SEQUENCE SET; Schema: public; Owner: redmine
--

SELECT pg_catalog.setval('user_preferences_id_seq', 7, true);


--
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: redmine
--

INSERT INTO users (id, login, hashed_password, firstname, lastname, admin, status, last_login_on, language, auth_source_id, created_on, updated_on, type, identity_url, mail_notification, salt, must_change_passwd, passwd_changed_on) VALUES (2, '', '', '', 'Anonymous users', false, 1, NULL, '', NULL, '2016-04-26 16:24:38.841233', '2016-04-26 16:24:38.841233', 'GroupAnonymous', NULL, '', NULL, false, NULL);
INSERT INTO users (id, login, hashed_password, firstname, lastname, admin, status, last_login_on, language, auth_source_id, created_on, updated_on, type, identity_url, mail_notification, salt, must_change_passwd, passwd_changed_on) VALUES (3, '', '', '', 'Non member users', false, 1, NULL, '', NULL, '2016-04-26 16:24:38.870088', '2016-04-26 16:24:38.870088', 'GroupNonMember', NULL, '', NULL, false, NULL);
INSERT INTO users (id, login, hashed_password, firstname, lastname, admin, status, last_login_on, language, auth_source_id, created_on, updated_on, type, identity_url, mail_notification, salt, must_change_passwd, passwd_changed_on) VALUES (4, '', '', '', 'Anonymous', false, 0, NULL, '', NULL, '2016-04-26 16:27:45.044088', '2016-04-26 16:27:45.044088', 'AnonymousUser', NULL, 'only_my_events', NULL, false, NULL);
INSERT INTO users (id, login, hashed_password, firstname, lastname, admin, status, last_login_on, language, auth_source_id, created_on, updated_on, type, identity_url, mail_notification, salt, must_change_passwd, passwd_changed_on) VALUES (1, 'admin', 'dbc2ffb036f4988dc59d21150aabd6285a2ab409', 'Redmine', 'Admin', true, 1, '2016-05-18 05:27:46.632568', '', NULL, '2016-04-26 16:24:35.534265', '2016-04-26 16:24:35.534265', 'User', NULL, 'all', '0cb213aa9ddf50a09184ece6f00b3405', false, NULL);
INSERT INTO users (id, login, hashed_password, firstname, lastname, admin, status, last_login_on, language, auth_source_id, created_on, updated_on, type, identity_url, mail_notification, salt, must_change_passwd, passwd_changed_on) VALUES (6, '', '', '', 'user', false, 1, NULL, '', NULL, '2016-05-11 09:16:56.688478', '2016-05-11 09:16:56.688478', 'Group', NULL, '', NULL, false, NULL);
INSERT INTO users (id, login, hashed_password, firstname, lastname, admin, status, last_login_on, language, auth_source_id, created_on, updated_on, type, identity_url, mail_notification, salt, must_change_passwd, passwd_changed_on) VALUES (7, '', '', '', 'worker', false, 1, NULL, '', NULL, '2016-05-11 09:17:07.270485', '2016-05-11 09:17:07.270485', 'Group', NULL, '', NULL, false, NULL);
INSERT INTO users (id, login, hashed_password, firstname, lastname, admin, status, last_login_on, language, auth_source_id, created_on, updated_on, type, identity_url, mail_notification, salt, must_change_passwd, passwd_changed_on) VALUES (8, '', '', '', 'anonymous_user', false, 1, NULL, '', NULL, '2016-05-11 09:17:44.633114', '2016-05-11 09:17:44.633114', 'Group', NULL, '', NULL, false, NULL);


--
-- Name: users_id_seq; Type: SEQUENCE SET; Schema: public; Owner: redmine
--

SELECT pg_catalog.setval('users_id_seq', 20, true);


--
-- Data for Name: versions; Type: TABLE DATA; Schema: public; Owner: redmine
--



--
-- Name: versions_id_seq; Type: SEQUENCE SET; Schema: public; Owner: redmine
--

SELECT pg_catalog.setval('versions_id_seq', 1, false);


--
-- Data for Name: watchers; Type: TABLE DATA; Schema: public; Owner: redmine
--



--
-- Name: watchers_id_seq; Type: SEQUENCE SET; Schema: public; Owner: redmine
--

SELECT pg_catalog.setval('watchers_id_seq', 1, false);


--
-- Data for Name: wiki_content_versions; Type: TABLE DATA; Schema: public; Owner: redmine
--



--
-- Name: wiki_content_versions_id_seq; Type: SEQUENCE SET; Schema: public; Owner: redmine
--

SELECT pg_catalog.setval('wiki_content_versions_id_seq', 1, false);


--
-- Data for Name: wiki_contents; Type: TABLE DATA; Schema: public; Owner: redmine
--



--
-- Name: wiki_contents_id_seq; Type: SEQUENCE SET; Schema: public; Owner: redmine
--

SELECT pg_catalog.setval('wiki_contents_id_seq', 1, false);


--
-- Data for Name: wiki_pages; Type: TABLE DATA; Schema: public; Owner: redmine
--



--
-- Name: wiki_pages_id_seq; Type: SEQUENCE SET; Schema: public; Owner: redmine
--

SELECT pg_catalog.setval('wiki_pages_id_seq', 1, false);


--
-- Data for Name: wiki_redirects; Type: TABLE DATA; Schema: public; Owner: redmine
--



--
-- Name: wiki_redirects_id_seq; Type: SEQUENCE SET; Schema: public; Owner: redmine
--

SELECT pg_catalog.setval('wiki_redirects_id_seq', 1, false);


--
-- Data for Name: wikis; Type: TABLE DATA; Schema: public; Owner: redmine
--

INSERT INTO wikis (id, project_id, start_page, status) VALUES (4, 4, 'Wiki', 1);


--
-- Name: wikis_id_seq; Type: SEQUENCE SET; Schema: public; Owner: redmine
--

SELECT pg_catalog.setval('wikis_id_seq', 4, true);


--
-- Data for Name: workflows; Type: TABLE DATA; Schema: public; Owner: redmine
--

INSERT INTO workflows (id, tracker_id, old_status_id, new_status_id, role_id, assignee, author, type, field_name, rule) VALUES (1, 1, 0, 2, 1, false, false, 'WorkflowTransition', NULL, NULL);
INSERT INTO workflows (id, tracker_id, old_status_id, new_status_id, role_id, assignee, author, type, field_name, rule) VALUES (2, 1, 0, 2, 2, false, false, 'WorkflowTransition', NULL, NULL);
INSERT INTO workflows (id, tracker_id, old_status_id, new_status_id, role_id, assignee, author, type, field_name, rule) VALUES (3, 1, 0, 2, 3, false, false, 'WorkflowTransition', NULL, NULL);
INSERT INTO workflows (id, tracker_id, old_status_id, new_status_id, role_id, assignee, author, type, field_name, rule) VALUES (4, 1, 2, 3, 1, false, false, 'WorkflowTransition', NULL, NULL);
INSERT INTO workflows (id, tracker_id, old_status_id, new_status_id, role_id, assignee, author, type, field_name, rule) VALUES (5, 1, 2, 3, 2, false, false, 'WorkflowTransition', NULL, NULL);
INSERT INTO workflows (id, tracker_id, old_status_id, new_status_id, role_id, assignee, author, type, field_name, rule) VALUES (6, 1, 2, 3, 3, false, false, 'WorkflowTransition', NULL, NULL);
INSERT INTO workflows (id, tracker_id, old_status_id, new_status_id, role_id, assignee, author, type, field_name, rule) VALUES (7, 1, 2, 4, 1, false, false, 'WorkflowTransition', NULL, NULL);
INSERT INTO workflows (id, tracker_id, old_status_id, new_status_id, role_id, assignee, author, type, field_name, rule) VALUES (8, 1, 2, 4, 2, false, false, 'WorkflowTransition', NULL, NULL);
INSERT INTO workflows (id, tracker_id, old_status_id, new_status_id, role_id, assignee, author, type, field_name, rule) VALUES (9, 1, 2, 4, 3, false, false, 'WorkflowTransition', NULL, NULL);
INSERT INTO workflows (id, tracker_id, old_status_id, new_status_id, role_id, assignee, author, type, field_name, rule) VALUES (10, 1, 3, 5, 1, false, false, 'WorkflowTransition', NULL, NULL);
INSERT INTO workflows (id, tracker_id, old_status_id, new_status_id, role_id, assignee, author, type, field_name, rule) VALUES (11, 1, 3, 5, 2, false, false, 'WorkflowTransition', NULL, NULL);
INSERT INTO workflows (id, tracker_id, old_status_id, new_status_id, role_id, assignee, author, type, field_name, rule) VALUES (12, 1, 3, 5, 3, false, false, 'WorkflowTransition', NULL, NULL);


--
-- Name: workflows_id_seq; Type: SEQUENCE SET; Schema: public; Owner: redmine
--

SELECT pg_catalog.setval('workflows_id_seq', 12, true);


--
-- Name: attachments_pkey; Type: CONSTRAINT; Schema: public; Owner: redmine
--

ALTER TABLE ONLY attachments
    ADD CONSTRAINT attachments_pkey PRIMARY KEY (id);


--
-- Name: auth_sources_pkey; Type: CONSTRAINT; Schema: public; Owner: redmine
--

ALTER TABLE ONLY auth_sources
    ADD CONSTRAINT auth_sources_pkey PRIMARY KEY (id);


--
-- Name: boards_pkey; Type: CONSTRAINT; Schema: public; Owner: redmine
--

ALTER TABLE ONLY boards
    ADD CONSTRAINT boards_pkey PRIMARY KEY (id);


--
-- Name: changes_pkey; Type: CONSTRAINT; Schema: public; Owner: redmine
--

ALTER TABLE ONLY changes
    ADD CONSTRAINT changes_pkey PRIMARY KEY (id);


--
-- Name: changesets_pkey; Type: CONSTRAINT; Schema: public; Owner: redmine
--

ALTER TABLE ONLY changesets
    ADD CONSTRAINT changesets_pkey PRIMARY KEY (id);


--
-- Name: comments_pkey; Type: CONSTRAINT; Schema: public; Owner: redmine
--

ALTER TABLE ONLY comments
    ADD CONSTRAINT comments_pkey PRIMARY KEY (id);


--
-- Name: custom_field_enumerations_pkey; Type: CONSTRAINT; Schema: public; Owner: redmine
--

ALTER TABLE ONLY custom_field_enumerations
    ADD CONSTRAINT custom_field_enumerations_pkey PRIMARY KEY (id);


--
-- Name: custom_fields_pkey; Type: CONSTRAINT; Schema: public; Owner: redmine
--

ALTER TABLE ONLY custom_fields
    ADD CONSTRAINT custom_fields_pkey PRIMARY KEY (id);


--
-- Name: custom_values_pkey; Type: CONSTRAINT; Schema: public; Owner: redmine
--

ALTER TABLE ONLY custom_values
    ADD CONSTRAINT custom_values_pkey PRIMARY KEY (id);


--
-- Name: documents_pkey; Type: CONSTRAINT; Schema: public; Owner: redmine
--

ALTER TABLE ONLY documents
    ADD CONSTRAINT documents_pkey PRIMARY KEY (id);


--
-- Name: email_addresses_pkey; Type: CONSTRAINT; Schema: public; Owner: redmine
--

ALTER TABLE ONLY email_addresses
    ADD CONSTRAINT email_addresses_pkey PRIMARY KEY (id);


--
-- Name: enabled_modules_pkey; Type: CONSTRAINT; Schema: public; Owner: redmine
--

ALTER TABLE ONLY enabled_modules
    ADD CONSTRAINT enabled_modules_pkey PRIMARY KEY (id);


--
-- Name: enumerations_pkey; Type: CONSTRAINT; Schema: public; Owner: redmine
--

ALTER TABLE ONLY enumerations
    ADD CONSTRAINT enumerations_pkey PRIMARY KEY (id);


--
-- Name: import_items_pkey; Type: CONSTRAINT; Schema: public; Owner: redmine
--

ALTER TABLE ONLY import_items
    ADD CONSTRAINT import_items_pkey PRIMARY KEY (id);


--
-- Name: imports_pkey; Type: CONSTRAINT; Schema: public; Owner: redmine
--

ALTER TABLE ONLY imports
    ADD CONSTRAINT imports_pkey PRIMARY KEY (id);


--
-- Name: issue_categories_pkey; Type: CONSTRAINT; Schema: public; Owner: redmine
--

ALTER TABLE ONLY issue_categories
    ADD CONSTRAINT issue_categories_pkey PRIMARY KEY (id);


--
-- Name: issue_relations_pkey; Type: CONSTRAINT; Schema: public; Owner: redmine
--

ALTER TABLE ONLY issue_relations
    ADD CONSTRAINT issue_relations_pkey PRIMARY KEY (id);


--
-- Name: issue_statuses_pkey; Type: CONSTRAINT; Schema: public; Owner: redmine
--

ALTER TABLE ONLY issue_statuses
    ADD CONSTRAINT issue_statuses_pkey PRIMARY KEY (id);


--
-- Name: issues_pkey; Type: CONSTRAINT; Schema: public; Owner: redmine
--

ALTER TABLE ONLY issues
    ADD CONSTRAINT issues_pkey PRIMARY KEY (id);


--
-- Name: journal_details_pkey; Type: CONSTRAINT; Schema: public; Owner: redmine
--

ALTER TABLE ONLY journal_details
    ADD CONSTRAINT journal_details_pkey PRIMARY KEY (id);


--
-- Name: journals_pkey; Type: CONSTRAINT; Schema: public; Owner: redmine
--

ALTER TABLE ONLY journals
    ADD CONSTRAINT journals_pkey PRIMARY KEY (id);


--
-- Name: member_roles_pkey; Type: CONSTRAINT; Schema: public; Owner: redmine
--

ALTER TABLE ONLY member_roles
    ADD CONSTRAINT member_roles_pkey PRIMARY KEY (id);


--
-- Name: members_pkey; Type: CONSTRAINT; Schema: public; Owner: redmine
--

ALTER TABLE ONLY members
    ADD CONSTRAINT members_pkey PRIMARY KEY (id);


--
-- Name: messages_pkey; Type: CONSTRAINT; Schema: public; Owner: redmine
--

ALTER TABLE ONLY messages
    ADD CONSTRAINT messages_pkey PRIMARY KEY (id);


--
-- Name: news_pkey; Type: CONSTRAINT; Schema: public; Owner: redmine
--

ALTER TABLE ONLY news
    ADD CONSTRAINT news_pkey PRIMARY KEY (id);


--
-- Name: open_id_authentication_associations_pkey; Type: CONSTRAINT; Schema: public; Owner: redmine
--

ALTER TABLE ONLY open_id_authentication_associations
    ADD CONSTRAINT open_id_authentication_associations_pkey PRIMARY KEY (id);


--
-- Name: open_id_authentication_nonces_pkey; Type: CONSTRAINT; Schema: public; Owner: redmine
--

ALTER TABLE ONLY open_id_authentication_nonces
    ADD CONSTRAINT open_id_authentication_nonces_pkey PRIMARY KEY (id);


--
-- Name: projects_pkey; Type: CONSTRAINT; Schema: public; Owner: redmine
--

ALTER TABLE ONLY projects
    ADD CONSTRAINT projects_pkey PRIMARY KEY (id);


--
-- Name: queries_pkey; Type: CONSTRAINT; Schema: public; Owner: redmine
--

ALTER TABLE ONLY queries
    ADD CONSTRAINT queries_pkey PRIMARY KEY (id);


--
-- Name: repositories_pkey; Type: CONSTRAINT; Schema: public; Owner: redmine
--

ALTER TABLE ONLY repositories
    ADD CONSTRAINT repositories_pkey PRIMARY KEY (id);


--
-- Name: roles_pkey; Type: CONSTRAINT; Schema: public; Owner: redmine
--

ALTER TABLE ONLY roles
    ADD CONSTRAINT roles_pkey PRIMARY KEY (id);


--
-- Name: settings_pkey; Type: CONSTRAINT; Schema: public; Owner: redmine
--

ALTER TABLE ONLY settings
    ADD CONSTRAINT settings_pkey PRIMARY KEY (id);


--
-- Name: time_entries_pkey; Type: CONSTRAINT; Schema: public; Owner: redmine
--

ALTER TABLE ONLY time_entries
    ADD CONSTRAINT time_entries_pkey PRIMARY KEY (id);


--
-- Name: tokens_pkey; Type: CONSTRAINT; Schema: public; Owner: redmine
--

ALTER TABLE ONLY tokens
    ADD CONSTRAINT tokens_pkey PRIMARY KEY (id);


--
-- Name: trackers_pkey; Type: CONSTRAINT; Schema: public; Owner: redmine
--

ALTER TABLE ONLY trackers
    ADD CONSTRAINT trackers_pkey PRIMARY KEY (id);


--
-- Name: user_preferences_pkey; Type: CONSTRAINT; Schema: public; Owner: redmine
--

ALTER TABLE ONLY user_preferences
    ADD CONSTRAINT user_preferences_pkey PRIMARY KEY (id);


--
-- Name: users_pkey; Type: CONSTRAINT; Schema: public; Owner: redmine
--

ALTER TABLE ONLY users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);


--
-- Name: versions_pkey; Type: CONSTRAINT; Schema: public; Owner: redmine
--

ALTER TABLE ONLY versions
    ADD CONSTRAINT versions_pkey PRIMARY KEY (id);


--
-- Name: watchers_pkey; Type: CONSTRAINT; Schema: public; Owner: redmine
--

ALTER TABLE ONLY watchers
    ADD CONSTRAINT watchers_pkey PRIMARY KEY (id);


--
-- Name: wiki_content_versions_pkey; Type: CONSTRAINT; Schema: public; Owner: redmine
--

ALTER TABLE ONLY wiki_content_versions
    ADD CONSTRAINT wiki_content_versions_pkey PRIMARY KEY (id);


--
-- Name: wiki_contents_pkey; Type: CONSTRAINT; Schema: public; Owner: redmine
--

ALTER TABLE ONLY wiki_contents
    ADD CONSTRAINT wiki_contents_pkey PRIMARY KEY (id);


--
-- Name: wiki_pages_pkey; Type: CONSTRAINT; Schema: public; Owner: redmine
--

ALTER TABLE ONLY wiki_pages
    ADD CONSTRAINT wiki_pages_pkey PRIMARY KEY (id);


--
-- Name: wiki_redirects_pkey; Type: CONSTRAINT; Schema: public; Owner: redmine
--

ALTER TABLE ONLY wiki_redirects
    ADD CONSTRAINT wiki_redirects_pkey PRIMARY KEY (id);


--
-- Name: wikis_pkey; Type: CONSTRAINT; Schema: public; Owner: redmine
--

ALTER TABLE ONLY wikis
    ADD CONSTRAINT wikis_pkey PRIMARY KEY (id);


--
-- Name: workflows_pkey; Type: CONSTRAINT; Schema: public; Owner: redmine
--

ALTER TABLE ONLY workflows
    ADD CONSTRAINT workflows_pkey PRIMARY KEY (id);


--
-- Name: boards_project_id; Type: INDEX; Schema: public; Owner: redmine
--

CREATE INDEX boards_project_id ON boards USING btree (project_id);


--
-- Name: changeset_parents_changeset_ids; Type: INDEX; Schema: public; Owner: redmine
--

CREATE INDEX changeset_parents_changeset_ids ON changeset_parents USING btree (changeset_id);


--
-- Name: changeset_parents_parent_ids; Type: INDEX; Schema: public; Owner: redmine
--

CREATE INDEX changeset_parents_parent_ids ON changeset_parents USING btree (parent_id);


--
-- Name: changesets_changeset_id; Type: INDEX; Schema: public; Owner: redmine
--

CREATE INDEX changesets_changeset_id ON changes USING btree (changeset_id);


--
-- Name: changesets_issues_ids; Type: INDEX; Schema: public; Owner: redmine
--

CREATE UNIQUE INDEX changesets_issues_ids ON changesets_issues USING btree (changeset_id, issue_id);


--
-- Name: changesets_repos_rev; Type: INDEX; Schema: public; Owner: redmine
--

CREATE UNIQUE INDEX changesets_repos_rev ON changesets USING btree (repository_id, revision);


--
-- Name: changesets_repos_scmid; Type: INDEX; Schema: public; Owner: redmine
--

CREATE INDEX changesets_repos_scmid ON changesets USING btree (repository_id, scmid);


--
-- Name: custom_fields_roles_ids; Type: INDEX; Schema: public; Owner: redmine
--

CREATE UNIQUE INDEX custom_fields_roles_ids ON custom_fields_roles USING btree (custom_field_id, role_id);


--
-- Name: custom_values_customized; Type: INDEX; Schema: public; Owner: redmine
--

CREATE INDEX custom_values_customized ON custom_values USING btree (customized_type, customized_id);


--
-- Name: documents_project_id; Type: INDEX; Schema: public; Owner: redmine
--

CREATE INDEX documents_project_id ON documents USING btree (project_id);


--
-- Name: enabled_modules_project_id; Type: INDEX; Schema: public; Owner: redmine
--

CREATE INDEX enabled_modules_project_id ON enabled_modules USING btree (project_id);


--
-- Name: groups_users_ids; Type: INDEX; Schema: public; Owner: redmine
--

CREATE UNIQUE INDEX groups_users_ids ON groups_users USING btree (group_id, user_id);


--
-- Name: index_attachments_on_author_id; Type: INDEX; Schema: public; Owner: redmine
--

CREATE INDEX index_attachments_on_author_id ON attachments USING btree (author_id);


--
-- Name: index_attachments_on_container_id_and_container_type; Type: INDEX; Schema: public; Owner: redmine
--

CREATE INDEX index_attachments_on_container_id_and_container_type ON attachments USING btree (container_id, container_type);


--
-- Name: index_attachments_on_created_on; Type: INDEX; Schema: public; Owner: redmine
--

CREATE INDEX index_attachments_on_created_on ON attachments USING btree (created_on);


--
-- Name: index_auth_sources_on_id_and_type; Type: INDEX; Schema: public; Owner: redmine
--

CREATE INDEX index_auth_sources_on_id_and_type ON auth_sources USING btree (id, type);


--
-- Name: index_boards_on_last_message_id; Type: INDEX; Schema: public; Owner: redmine
--

CREATE INDEX index_boards_on_last_message_id ON boards USING btree (last_message_id);


--
-- Name: index_changesets_on_committed_on; Type: INDEX; Schema: public; Owner: redmine
--

CREATE INDEX index_changesets_on_committed_on ON changesets USING btree (committed_on);


--
-- Name: index_changesets_on_repository_id; Type: INDEX; Schema: public; Owner: redmine
--

CREATE INDEX index_changesets_on_repository_id ON changesets USING btree (repository_id);


--
-- Name: index_changesets_on_user_id; Type: INDEX; Schema: public; Owner: redmine
--

CREATE INDEX index_changesets_on_user_id ON changesets USING btree (user_id);


--
-- Name: index_comments_on_author_id; Type: INDEX; Schema: public; Owner: redmine
--

CREATE INDEX index_comments_on_author_id ON comments USING btree (author_id);


--
-- Name: index_comments_on_commented_id_and_commented_type; Type: INDEX; Schema: public; Owner: redmine
--

CREATE INDEX index_comments_on_commented_id_and_commented_type ON comments USING btree (commented_id, commented_type);


--
-- Name: index_custom_fields_on_id_and_type; Type: INDEX; Schema: public; Owner: redmine
--

CREATE INDEX index_custom_fields_on_id_and_type ON custom_fields USING btree (id, type);


--
-- Name: index_custom_fields_projects_on_custom_field_id_and_project_id; Type: INDEX; Schema: public; Owner: redmine
--

CREATE UNIQUE INDEX index_custom_fields_projects_on_custom_field_id_and_project_id ON custom_fields_projects USING btree (custom_field_id, project_id);


--
-- Name: index_custom_fields_trackers_on_custom_field_id_and_tracker_id; Type: INDEX; Schema: public; Owner: redmine
--

CREATE UNIQUE INDEX index_custom_fields_trackers_on_custom_field_id_and_tracker_id ON custom_fields_trackers USING btree (custom_field_id, tracker_id);


--
-- Name: index_custom_values_on_custom_field_id; Type: INDEX; Schema: public; Owner: redmine
--

CREATE INDEX index_custom_values_on_custom_field_id ON custom_values USING btree (custom_field_id);


--
-- Name: index_documents_on_category_id; Type: INDEX; Schema: public; Owner: redmine
--

CREATE INDEX index_documents_on_category_id ON documents USING btree (category_id);


--
-- Name: index_documents_on_created_on; Type: INDEX; Schema: public; Owner: redmine
--

CREATE INDEX index_documents_on_created_on ON documents USING btree (created_on);


--
-- Name: index_email_addresses_on_user_id; Type: INDEX; Schema: public; Owner: redmine
--

CREATE INDEX index_email_addresses_on_user_id ON email_addresses USING btree (user_id);


--
-- Name: index_enumerations_on_id_and_type; Type: INDEX; Schema: public; Owner: redmine
--

CREATE INDEX index_enumerations_on_id_and_type ON enumerations USING btree (id, type);


--
-- Name: index_enumerations_on_project_id; Type: INDEX; Schema: public; Owner: redmine
--

CREATE INDEX index_enumerations_on_project_id ON enumerations USING btree (project_id);


--
-- Name: index_issue_categories_on_assigned_to_id; Type: INDEX; Schema: public; Owner: redmine
--

CREATE INDEX index_issue_categories_on_assigned_to_id ON issue_categories USING btree (assigned_to_id);


--
-- Name: index_issue_relations_on_issue_from_id; Type: INDEX; Schema: public; Owner: redmine
--

CREATE INDEX index_issue_relations_on_issue_from_id ON issue_relations USING btree (issue_from_id);


--
-- Name: index_issue_relations_on_issue_from_id_and_issue_to_id; Type: INDEX; Schema: public; Owner: redmine
--

CREATE UNIQUE INDEX index_issue_relations_on_issue_from_id_and_issue_to_id ON issue_relations USING btree (issue_from_id, issue_to_id);


--
-- Name: index_issue_relations_on_issue_to_id; Type: INDEX; Schema: public; Owner: redmine
--

CREATE INDEX index_issue_relations_on_issue_to_id ON issue_relations USING btree (issue_to_id);


--
-- Name: index_issue_statuses_on_is_closed; Type: INDEX; Schema: public; Owner: redmine
--

CREATE INDEX index_issue_statuses_on_is_closed ON issue_statuses USING btree (is_closed);


--
-- Name: index_issue_statuses_on_position; Type: INDEX; Schema: public; Owner: redmine
--

CREATE INDEX index_issue_statuses_on_position ON issue_statuses USING btree ("position");


--
-- Name: index_issues_on_assigned_to_id; Type: INDEX; Schema: public; Owner: redmine
--

CREATE INDEX index_issues_on_assigned_to_id ON issues USING btree (assigned_to_id);


--
-- Name: index_issues_on_author_id; Type: INDEX; Schema: public; Owner: redmine
--

CREATE INDEX index_issues_on_author_id ON issues USING btree (author_id);


--
-- Name: index_issues_on_category_id; Type: INDEX; Schema: public; Owner: redmine
--

CREATE INDEX index_issues_on_category_id ON issues USING btree (category_id);


--
-- Name: index_issues_on_created_on; Type: INDEX; Schema: public; Owner: redmine
--

CREATE INDEX index_issues_on_created_on ON issues USING btree (created_on);


--
-- Name: index_issues_on_fixed_version_id; Type: INDEX; Schema: public; Owner: redmine
--

CREATE INDEX index_issues_on_fixed_version_id ON issues USING btree (fixed_version_id);


--
-- Name: index_issues_on_priority_id; Type: INDEX; Schema: public; Owner: redmine
--

CREATE INDEX index_issues_on_priority_id ON issues USING btree (priority_id);


--
-- Name: index_issues_on_root_id_and_lft_and_rgt; Type: INDEX; Schema: public; Owner: redmine
--

CREATE INDEX index_issues_on_root_id_and_lft_and_rgt ON issues USING btree (root_id, lft, rgt);


--
-- Name: index_issues_on_status_id; Type: INDEX; Schema: public; Owner: redmine
--

CREATE INDEX index_issues_on_status_id ON issues USING btree (status_id);


--
-- Name: index_issues_on_tracker_id; Type: INDEX; Schema: public; Owner: redmine
--

CREATE INDEX index_issues_on_tracker_id ON issues USING btree (tracker_id);


--
-- Name: index_journals_on_created_on; Type: INDEX; Schema: public; Owner: redmine
--

CREATE INDEX index_journals_on_created_on ON journals USING btree (created_on);


--
-- Name: index_journals_on_journalized_id; Type: INDEX; Schema: public; Owner: redmine
--

CREATE INDEX index_journals_on_journalized_id ON journals USING btree (journalized_id);


--
-- Name: index_journals_on_user_id; Type: INDEX; Schema: public; Owner: redmine
--

CREATE INDEX index_journals_on_user_id ON journals USING btree (user_id);


--
-- Name: index_member_roles_on_member_id; Type: INDEX; Schema: public; Owner: redmine
--

CREATE INDEX index_member_roles_on_member_id ON member_roles USING btree (member_id);


--
-- Name: index_member_roles_on_role_id; Type: INDEX; Schema: public; Owner: redmine
--

CREATE INDEX index_member_roles_on_role_id ON member_roles USING btree (role_id);


--
-- Name: index_members_on_project_id; Type: INDEX; Schema: public; Owner: redmine
--

CREATE INDEX index_members_on_project_id ON members USING btree (project_id);


--
-- Name: index_members_on_user_id; Type: INDEX; Schema: public; Owner: redmine
--

CREATE INDEX index_members_on_user_id ON members USING btree (user_id);


--
-- Name: index_members_on_user_id_and_project_id; Type: INDEX; Schema: public; Owner: redmine
--

CREATE UNIQUE INDEX index_members_on_user_id_and_project_id ON members USING btree (user_id, project_id);


--
-- Name: index_messages_on_author_id; Type: INDEX; Schema: public; Owner: redmine
--

CREATE INDEX index_messages_on_author_id ON messages USING btree (author_id);


--
-- Name: index_messages_on_created_on; Type: INDEX; Schema: public; Owner: redmine
--

CREATE INDEX index_messages_on_created_on ON messages USING btree (created_on);


--
-- Name: index_messages_on_last_reply_id; Type: INDEX; Schema: public; Owner: redmine
--

CREATE INDEX index_messages_on_last_reply_id ON messages USING btree (last_reply_id);


--
-- Name: index_news_on_author_id; Type: INDEX; Schema: public; Owner: redmine
--

CREATE INDEX index_news_on_author_id ON news USING btree (author_id);


--
-- Name: index_news_on_created_on; Type: INDEX; Schema: public; Owner: redmine
--

CREATE INDEX index_news_on_created_on ON news USING btree (created_on);


--
-- Name: index_projects_on_lft; Type: INDEX; Schema: public; Owner: redmine
--

CREATE INDEX index_projects_on_lft ON projects USING btree (lft);


--
-- Name: index_projects_on_rgt; Type: INDEX; Schema: public; Owner: redmine
--

CREATE INDEX index_projects_on_rgt ON projects USING btree (rgt);


--
-- Name: index_queries_on_project_id; Type: INDEX; Schema: public; Owner: redmine
--

CREATE INDEX index_queries_on_project_id ON queries USING btree (project_id);


--
-- Name: index_queries_on_user_id; Type: INDEX; Schema: public; Owner: redmine
--

CREATE INDEX index_queries_on_user_id ON queries USING btree (user_id);


--
-- Name: index_repositories_on_project_id; Type: INDEX; Schema: public; Owner: redmine
--

CREATE INDEX index_repositories_on_project_id ON repositories USING btree (project_id);


--
-- Name: index_roles_managed_roles_on_role_id_and_managed_role_id; Type: INDEX; Schema: public; Owner: redmine
--

CREATE UNIQUE INDEX index_roles_managed_roles_on_role_id_and_managed_role_id ON roles_managed_roles USING btree (role_id, managed_role_id);


--
-- Name: index_settings_on_name; Type: INDEX; Schema: public; Owner: redmine
--

CREATE INDEX index_settings_on_name ON settings USING btree (name);


--
-- Name: index_time_entries_on_activity_id; Type: INDEX; Schema: public; Owner: redmine
--

CREATE INDEX index_time_entries_on_activity_id ON time_entries USING btree (activity_id);


--
-- Name: index_time_entries_on_created_on; Type: INDEX; Schema: public; Owner: redmine
--

CREATE INDEX index_time_entries_on_created_on ON time_entries USING btree (created_on);


--
-- Name: index_time_entries_on_user_id; Type: INDEX; Schema: public; Owner: redmine
--

CREATE INDEX index_time_entries_on_user_id ON time_entries USING btree (user_id);


--
-- Name: index_tokens_on_user_id; Type: INDEX; Schema: public; Owner: redmine
--

CREATE INDEX index_tokens_on_user_id ON tokens USING btree (user_id);


--
-- Name: index_user_preferences_on_user_id; Type: INDEX; Schema: public; Owner: redmine
--

CREATE INDEX index_user_preferences_on_user_id ON user_preferences USING btree (user_id);


--
-- Name: index_users_on_auth_source_id; Type: INDEX; Schema: public; Owner: redmine
--

CREATE INDEX index_users_on_auth_source_id ON users USING btree (auth_source_id);


--
-- Name: index_users_on_id_and_type; Type: INDEX; Schema: public; Owner: redmine
--

CREATE INDEX index_users_on_id_and_type ON users USING btree (id, type);


--
-- Name: index_users_on_type; Type: INDEX; Schema: public; Owner: redmine
--

CREATE INDEX index_users_on_type ON users USING btree (type);


--
-- Name: index_versions_on_sharing; Type: INDEX; Schema: public; Owner: redmine
--

CREATE INDEX index_versions_on_sharing ON versions USING btree (sharing);


--
-- Name: index_watchers_on_user_id; Type: INDEX; Schema: public; Owner: redmine
--

CREATE INDEX index_watchers_on_user_id ON watchers USING btree (user_id);


--
-- Name: index_watchers_on_watchable_id_and_watchable_type; Type: INDEX; Schema: public; Owner: redmine
--

CREATE INDEX index_watchers_on_watchable_id_and_watchable_type ON watchers USING btree (watchable_id, watchable_type);


--
-- Name: index_wiki_content_versions_on_updated_on; Type: INDEX; Schema: public; Owner: redmine
--

CREATE INDEX index_wiki_content_versions_on_updated_on ON wiki_content_versions USING btree (updated_on);


--
-- Name: index_wiki_contents_on_author_id; Type: INDEX; Schema: public; Owner: redmine
--

CREATE INDEX index_wiki_contents_on_author_id ON wiki_contents USING btree (author_id);


--
-- Name: index_wiki_pages_on_parent_id; Type: INDEX; Schema: public; Owner: redmine
--

CREATE INDEX index_wiki_pages_on_parent_id ON wiki_pages USING btree (parent_id);


--
-- Name: index_wiki_pages_on_wiki_id; Type: INDEX; Schema: public; Owner: redmine
--

CREATE INDEX index_wiki_pages_on_wiki_id ON wiki_pages USING btree (wiki_id);


--
-- Name: index_wiki_redirects_on_wiki_id; Type: INDEX; Schema: public; Owner: redmine
--

CREATE INDEX index_wiki_redirects_on_wiki_id ON wiki_redirects USING btree (wiki_id);


--
-- Name: index_workflows_on_new_status_id; Type: INDEX; Schema: public; Owner: redmine
--

CREATE INDEX index_workflows_on_new_status_id ON workflows USING btree (new_status_id);


--
-- Name: index_workflows_on_old_status_id; Type: INDEX; Schema: public; Owner: redmine
--

CREATE INDEX index_workflows_on_old_status_id ON workflows USING btree (old_status_id);


--
-- Name: index_workflows_on_role_id; Type: INDEX; Schema: public; Owner: redmine
--

CREATE INDEX index_workflows_on_role_id ON workflows USING btree (role_id);


--
-- Name: issue_categories_project_id; Type: INDEX; Schema: public; Owner: redmine
--

CREATE INDEX issue_categories_project_id ON issue_categories USING btree (project_id);


--
-- Name: issues_project_id; Type: INDEX; Schema: public; Owner: redmine
--

CREATE INDEX issues_project_id ON issues USING btree (project_id);


--
-- Name: journal_details_journal_id; Type: INDEX; Schema: public; Owner: redmine
--

CREATE INDEX journal_details_journal_id ON journal_details USING btree (journal_id);


--
-- Name: journals_journalized_id; Type: INDEX; Schema: public; Owner: redmine
--

CREATE INDEX journals_journalized_id ON journals USING btree (journalized_id, journalized_type);


--
-- Name: messages_board_id; Type: INDEX; Schema: public; Owner: redmine
--

CREATE INDEX messages_board_id ON messages USING btree (board_id);


--
-- Name: messages_parent_id; Type: INDEX; Schema: public; Owner: redmine
--

CREATE INDEX messages_parent_id ON messages USING btree (parent_id);


--
-- Name: news_project_id; Type: INDEX; Schema: public; Owner: redmine
--

CREATE INDEX news_project_id ON news USING btree (project_id);


--
-- Name: projects_trackers_project_id; Type: INDEX; Schema: public; Owner: redmine
--

CREATE INDEX projects_trackers_project_id ON projects_trackers USING btree (project_id);


--
-- Name: projects_trackers_unique; Type: INDEX; Schema: public; Owner: redmine
--

CREATE UNIQUE INDEX projects_trackers_unique ON projects_trackers USING btree (project_id, tracker_id);


--
-- Name: queries_roles_ids; Type: INDEX; Schema: public; Owner: redmine
--

CREATE UNIQUE INDEX queries_roles_ids ON queries_roles USING btree (query_id, role_id);


--
-- Name: time_entries_issue_id; Type: INDEX; Schema: public; Owner: redmine
--

CREATE INDEX time_entries_issue_id ON time_entries USING btree (issue_id);


--
-- Name: time_entries_project_id; Type: INDEX; Schema: public; Owner: redmine
--

CREATE INDEX time_entries_project_id ON time_entries USING btree (project_id);


--
-- Name: tokens_value; Type: INDEX; Schema: public; Owner: redmine
--

CREATE UNIQUE INDEX tokens_value ON tokens USING btree (value);


--
-- Name: unique_schema_migrations; Type: INDEX; Schema: public; Owner: redmine
--

CREATE UNIQUE INDEX unique_schema_migrations ON schema_migrations USING btree (version);


--
-- Name: versions_project_id; Type: INDEX; Schema: public; Owner: redmine
--

CREATE INDEX versions_project_id ON versions USING btree (project_id);


--
-- Name: watchers_user_id_type; Type: INDEX; Schema: public; Owner: redmine
--

CREATE INDEX watchers_user_id_type ON watchers USING btree (user_id, watchable_type);


--
-- Name: wiki_content_versions_wcid; Type: INDEX; Schema: public; Owner: redmine
--

CREATE INDEX wiki_content_versions_wcid ON wiki_content_versions USING btree (wiki_content_id);


--
-- Name: wiki_contents_page_id; Type: INDEX; Schema: public; Owner: redmine
--

CREATE INDEX wiki_contents_page_id ON wiki_contents USING btree (page_id);


--
-- Name: wiki_pages_wiki_id_title; Type: INDEX; Schema: public; Owner: redmine
--

CREATE INDEX wiki_pages_wiki_id_title ON wiki_pages USING btree (wiki_id, title);


--
-- Name: wiki_redirects_wiki_id_title; Type: INDEX; Schema: public; Owner: redmine
--

CREATE INDEX wiki_redirects_wiki_id_title ON wiki_redirects USING btree (wiki_id, title);


--
-- Name: wikis_project_id; Type: INDEX; Schema: public; Owner: redmine
--

CREATE INDEX wikis_project_id ON wikis USING btree (project_id);


--
-- Name: wkfs_role_tracker_old_status; Type: INDEX; Schema: public; Owner: redmine
--

CREATE INDEX wkfs_role_tracker_old_status ON workflows USING btree (role_id, tracker_id, old_status_id);


--
-- Name: public; Type: ACL; Schema: -; Owner: postgres
--

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM postgres;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;


--
-- PostgreSQL database dump complete
--

