CREATE TABLE public.employee (
    id character varying(255) NOT NULL,
    login character varying(255) NOT NULL,
    name character varying(255) NOT NULL,
    salary numeric(20,4) NOT NULL,
    start_date date NOT NULL,
    created_datetime timestamp without time zone NOT NULL,
    last_updated_datetime timestamp without time zone NOT NULL,
    version integer NOT NULL
);
ALTER TABLE ONLY public.employee ADD CONSTRAINT employee_pkey PRIMARY KEY (id);
ALTER TABLE ONLY public.employee ADD CONSTRAINT uniquelogin UNIQUE (login);
