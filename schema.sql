
CREATE TABLE ponencia (
                id_ponencia INTEGER NOT NULL,
                titulo VARCHAR(250) NOT NULL,
                categoria SMALLINT NOT NULL,
                tipo SMALLINT NOT NULL,
                nivel SMALLINT NOT NULL,
                resumen VARCHAR(2000) NOT NULL,
                duracion SMALLINT NOT NULL,
                datashow SMALLINT NOT NULL,
                laptop SMALLINT NOT NULL,
                otros VARCHAR(200) NOT NULL,
                CONSTRAINT id_ponencia PRIMARY KEY (id_ponencia)
);


CREATE TABLE ponente (
                id_ponente INTEGER NOT NULL,
                id_ponencia INTEGER NOT NULL,
                nombres VARCHAR(250) NOT NULL,
                apellidos VARCHAR(250) NOT NULL,
                email VARCHAR(50) NOT NULL,
                biografia VARCHAR(2000) NOT NULL,
                observaciones VARCHAR(2000) NOT NULL,
                CONSTRAINT id_ponente PRIMARY KEY (id_ponente, id_ponencia)
);


ALTER TABLE ponente ADD CONSTRAINT ponencia_ponente_fk
FOREIGN KEY (id_ponencia)
REFERENCES ponencia (id_ponencia)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;