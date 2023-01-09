CREATE DATABASE systemmanager ENCODING 'UTF8';
\c systemmanager

SET statement_timeout = 0;
SET xmloption = content;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

--
-- Name: sysmanager; Type: SCHEMA; Schema: -; Owner: sysmanager
--
CREATE USER sysmanager with SYSADMIN PASSWORD 'sysmanager@123';

SET search_path = sysmanager;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: database; Type: TABLE; Schema: sysmanager; Owner: sysmanager; Tablespace: 
--

CREATE TABLE database (
    name character varying(100) NOT NULL,
    ip character varying(100),
    userid character varying(100),
    port integer,
    dbclass tinyint,
    password character varying(100),
    username character varying(100),
    database_info text,
    useforstu boolean
)
WITH (orientation=row, compression=no);


ALTER TABLE sysmanager.database OWNER TO sysmanager;

--
-- Name: exercise; Type: TABLE; Schema: sysmanager; Owner: sysmanager; Tablespace: 
--

CREATE TABLE exercise (
    id integer NOT NULL,
    name character varying(100),
    describe text,
    teacherid character varying(100) NOT NULL,
    ispublic boolean,
    deleteflag boolean
)
WITH (orientation=row, compression=no);


ALTER TABLE sysmanager.exercise OWNER TO sysmanager;

--
-- Name: exerinclass; Type: TABLE; Schema: sysmanager; Owner: sysmanager; Tablespace: 
--

CREATE TABLE exerinclass (
    stuclassid character varying(100) NOT NULL,
    exerciseid integer NOT NULL,
    istest boolean,
    starttime timestamp with time zone,
    endtime timestamp with time zone
)
WITH (orientation=row, compression=no);


ALTER TABLE sysmanager.exerinclass OWNER TO sysmanager;

--
-- Name: judgedatabase; Type: TABLE; Schema: sysmanager; Owner: sysmanager; Tablespace: 
--

CREATE TABLE judgedatabase (
    name character varying(100) NOT NULL,
    describe text
)
WITH (orientation=row, compression=no);


ALTER TABLE sysmanager.judgedatabase OWNER TO sysmanager;

--
-- Name: questinexer; Type: TABLE; Schema: sysmanager; Owner: sysmanager; Tablespace: 
--

CREATE TABLE questinexer (
    exerciseid integer NOT NULL,
    questionid integer NOT NULL,
    orderid integer,
    score integer
)
WITH (orientation=row, compression=no);


ALTER TABLE sysmanager.questinexer OWNER TO sysmanager;

--
-- Name: question; Type: TABLE; Schema: sysmanager; Owner: sysmanager; Tablespace: 
--

CREATE TABLE question (
    id integer NOT NULL,
    questionclass tinyint,
    content text,
    answer text,
    dbname character varying(100) NOT NULL,
    title character varying(100),
    analysis text,
    deleteflag boolean,
    teacherid character varying(100) NOT NULL,
    targetname character varying(100),
    initsql text
)
WITH (orientation=row, compression=no);


ALTER TABLE sysmanager.question OWNER TO sysmanager;

--
-- Name: stuanswer; Type: TABLE; Schema: sysmanager; Owner: sysmanager; Tablespace: 
--

CREATE TABLE stuanswer (
    answer text,
    isright boolean,
    idea text,
    score integer,
    questionid integer NOT NULL,
    studentid character varying(100) NOT NULL,
    exerciseid integer NOT NULL
)
WITH (orientation=row, compression=no);


ALTER TABLE sysmanager.stuanswer OWNER TO sysmanager;

--
-- Name: stuclass; Type: TABLE; Schema: sysmanager; Owner: sysmanager; Tablespace: 
--

CREATE TABLE stuclass (
    id character varying(100) NOT NULL,
    teacherid character varying(100) NOT NULL,
    semester character varying(100)
)
WITH (orientation=row, compression=no);


ALTER TABLE sysmanager.stuclass OWNER TO sysmanager;

--
-- Name: student; Type: TABLE; Schema: sysmanager; Owner: sysmanager; Tablespace: 
--

CREATE TABLE student (
    name character varying(100),
    grade character varying(100),
    id character varying(100) NOT NULL,
    classid character varying(100),
    username character varying(100),
    password character varying(100),
    dbname character varying(100),
    isactive boolean
)
WITH (orientation=row, compression=no);


ALTER TABLE sysmanager.student OWNER TO sysmanager;

--
-- Name: sysuser; Type: TABLE; Schema: sysmanager; Owner: sysmanager; Tablespace: 
--

CREATE TABLE sysuser (
    id character varying(100) NOT NULL,
    pwd character varying(100),
    userclass tinyint
)
WITH (orientation=row, compression=no);


ALTER TABLE sysmanager.sysuser OWNER TO sysmanager;

--
-- Name: teacher; Type: TABLE; Schema: sysmanager; Owner: sysmanager; Tablespace: 
--

CREATE TABLE teacher (
    name character varying(100),
    id character varying(100) NOT NULL,
    password character varying(100),
    username character varying(100)
)
WITH (orientation=row, compression=no);


ALTER TABLE sysmanager.teacher OWNER TO sysmanager;

--
-- Name: exercise_id_seq; Type: SEQUENCE; Schema: sysmanager; Owner: sysmanager
--

CREATE SEQUENCE exercise_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE sysmanager.exercise_id_seq OWNER TO sysmanager;

--
-- Name: exercise_id_seq; Type: SEQUENCE OWNED BY; Schema: sysmanager; Owner: sysmanager
--

ALTER SEQUENCE exercise_id_seq OWNED BY exercise.id;


--
-- Name: question_id_seq; Type: SEQUENCE; Schema: sysmanager; Owner: sysmanager
--

CREATE SEQUENCE question_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE sysmanager.question_id_seq OWNER TO sysmanager;

--
-- Name: question_id_seq; Type: SEQUENCE OWNED BY; Schema: sysmanager; Owner: sysmanager
--

ALTER SEQUENCE question_id_seq OWNED BY question.id;


--
-- Name: id; Type: DEFAULT; Schema: sysmanager; Owner: sysmanager
--

ALTER TABLE exercise ALTER COLUMN id SET DEFAULT nextval('exercise_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: sysmanager; Owner: sysmanager
--

ALTER TABLE question ALTER COLUMN id SET DEFAULT nextval('question_id_seq'::regclass);


--
-- Name: teacher_pkey; Type: CONSTRAINT; Schema: sysmanager; Owner: sysmanager; Tablespace: 
--

ALTER TABLE teacher
    ADD CONSTRAINT teacher_pkey PRIMARY KEY (id);


--
-- Name: database_useid_fkey; Type: FK CONSTRAINT; Schema: sysmanager; Owner: sysmanager
--

ALTER TABLE database
    ADD CONSTRAINT database_useid_fkey FOREIGN KEY (userid) REFERENCES teacher(id);


--
-- Name: exercise_teacherid_fkey; Type: FK CONSTRAINT; Schema: sysmanager; Owner: sysmanager
--

ALTER TABLE exercise
    ADD CONSTRAINT exercise_teacherid_fkey FOREIGN KEY (teacherid) REFERENCES teacher(id);


--
-- Name: exercise_pkey; Type: CONSTRAINT; Schema: sysmanager; Owner: sysmanager; Tablespace: 
--

ALTER TABLE exercise
    ADD CONSTRAINT exercise_pkey PRIMARY KEY (id);


--
-- Name: exerinclass_exerciseid_fkey; Type: FK CONSTRAINT; Schema: sysmanager; Owner: sysmanager
--

ALTER TABLE exerinclass
    ADD CONSTRAINT exerinclass_exerciseid_fkey FOREIGN KEY (exerciseid) REFERENCES exercise(id);


--
-- Name: stuclass_pkey; Type: CONSTRAINT; Schema: sysmanager; Owner: sysmanager; Tablespace: 
--

ALTER TABLE stuclass
    ADD CONSTRAINT stuclass_pkey PRIMARY KEY (id);


--
-- Name: exerinclass_stuclassid_fkey; Type: FK CONSTRAINT; Schema: sysmanager; Owner: sysmanager
--

ALTER TABLE exerinclass
    ADD CONSTRAINT exerinclass_stuclassid_fkey FOREIGN KEY (stuclassid) REFERENCES stuclass(id);


--
-- Name: database_pkey; Type: CONSTRAINT; Schema: sysmanager; Owner: sysmanager; Tablespace: 
--

ALTER TABLE database
    ADD CONSTRAINT database_pkey PRIMARY KEY (name);


--
-- Name: judgedatabase_name_fkey; Type: FK CONSTRAINT; Schema: sysmanager; Owner: sysmanager
--

ALTER TABLE judgedatabase
    ADD CONSTRAINT judgedatabase_name_fkey FOREIGN KEY (name) REFERENCES database(name);


--
-- Name: questinexer_exerciseid_fkey; Type: FK CONSTRAINT; Schema: sysmanager; Owner: sysmanager
--

ALTER TABLE questinexer
    ADD CONSTRAINT questinexer_exerciseid_fkey FOREIGN KEY (exerciseid) REFERENCES exercise(id);


--
-- Name: question_pkey; Type: CONSTRAINT; Schema: sysmanager; Owner: sysmanager; Tablespace: 
--

ALTER TABLE question
    ADD CONSTRAINT question_pkey PRIMARY KEY (id);


--
-- Name: questinexer_questionid_fkey; Type: FK CONSTRAINT; Schema: sysmanager; Owner: sysmanager
--

ALTER TABLE questinexer
    ADD CONSTRAINT questinexer_questionid_fkey FOREIGN KEY (questionid) REFERENCES question(id);


--
-- Name: judgedatabase_pkey; Type: CONSTRAINT; Schema: sysmanager; Owner: sysmanager; Tablespace: 
--

ALTER TABLE judgedatabase
    ADD CONSTRAINT judgedatabase_pkey PRIMARY KEY (name);


--
-- Name: question_dbname_fkey; Type: FK CONSTRAINT; Schema: sysmanager; Owner: sysmanager
--

ALTER TABLE question
    ADD CONSTRAINT question_dbname_fkey FOREIGN KEY (dbname) REFERENCES judgedatabase(name);


--
-- Name: question_teacherid_fkey; Type: FK CONSTRAINT; Schema: sysmanager; Owner: sysmanager
--

ALTER TABLE question
    ADD CONSTRAINT question_teacherid_fkey FOREIGN KEY (teacherid) REFERENCES teacher(id);


--
-- Name: stuclass_teacherid_fkey; Type: FK CONSTRAINT; Schema: sysmanager; Owner: sysmanager
--

ALTER TABLE stuclass
    ADD CONSTRAINT stuclass_teacherid_fkey FOREIGN KEY (teacherid) REFERENCES teacher(id);


--
-- Name: student_classid_fkey; Type: FK CONSTRAINT; Schema: sysmanager; Owner: sysmanager
--

ALTER TABLE student
    ADD CONSTRAINT student_classid_fkey FOREIGN KEY (classid) REFERENCES stuclass(id);


--
-- Name: student_dbname_fkey; Type: FK CONSTRAINT; Schema: sysmanager; Owner: sysmanager
--

ALTER TABLE student
    ADD CONSTRAINT student_dbname_fkey FOREIGN KEY (dbname) REFERENCES database(name);


--
-- Name: sysuser_pkey; Type: CONSTRAINT; Schema: sysmanager; Owner: sysmanager; Tablespace: 
--

ALTER TABLE sysuser
    ADD CONSTRAINT sysuser_pkey PRIMARY KEY (id);


--
-- Name: student_id_fkey; Type: FK CONSTRAINT; Schema: sysmanager; Owner: sysmanager
--

ALTER TABLE student
    ADD CONSTRAINT student_id_fkey FOREIGN KEY (id) REFERENCES sysuser(id);


--
-- Name: teacher_id_fkey; Type: FK CONSTRAINT; Schema: sysmanager; Owner: sysmanager
--

ALTER TABLE teacher
    ADD CONSTRAINT teacher_id_fkey FOREIGN KEY (id) REFERENCES sysuser(id);


--
-- Name: exerinclass_pkey; Type: CONSTRAINT; Schema: sysmanager; Owner: sysmanager; Tablespace: 
--

ALTER TABLE exerinclass
    ADD CONSTRAINT exerinclass_pkey PRIMARY KEY (stuclassid, exerciseid);


--
-- Name: questinexer_pkey; Type: CONSTRAINT; Schema: sysmanager; Owner: sysmanager; Tablespace: 
--

ALTER TABLE questinexer
    ADD CONSTRAINT questinexer_pkey PRIMARY KEY (exerciseid, questionid);


--
-- Name: stuanswer_pkey; Type: CONSTRAINT; Schema: sysmanager; Owner: sysmanager; Tablespace: 
--

ALTER TABLE stuanswer
    ADD CONSTRAINT stuanswer_pkey PRIMARY KEY (exerciseid, questionid, studentid);


--
-- Name: student_pkey; Type: CONSTRAINT; Schema: sysmanager; Owner: sysmanager; Tablespace: 
--

ALTER TABLE student
    ADD CONSTRAINT student_pkey PRIMARY KEY (id);

insert into sysuser(id, pwd, userclass) values ('sysmanager', 'sysmanager@123', 1);
insert into teacher(id, name, username, password) values('sysmanager','管理员','sysmanager','sysmanager@123');
create database practicedb1 OWNER sysmanager;
insert into database(name,userid,dbclass,useforstu) values ('practicedb1','sysmanager',1,true);
