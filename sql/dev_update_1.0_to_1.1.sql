---
-- #%L
-- Cantharella :: Data
-- $Id: dev_update_1.0_to_1.1.sql 211 2013-04-30 07:31:44Z acheype $
-- $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.data/src/main/sql/dev_update_1.0_to_1.1.sql $
-- %%
-- Copyright (C) 2013 IRD (Institut de Recherche pour le Developpement) and by respective authors (see below)
-- %%
-- This program is free software: you can redistribute it and/or modify
-- it under the terms of the GNU Affero General Public License as published by
-- the Free Software Foundation, either version 3 of the License, or
-- (at your option) any later version.
-- 
-- This program is distributed in the hope that it will be useful,
-- but WITHOUT ANY WARRANTY; without even the implied warranty of
-- MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
-- GNU General Public License for more details.
-- 
-- You should have received a copy of the GNU Affero General Public License
-- along with this program.  If not, see <http://www.gnu.org/licenses/>.
-- #L%
---
-- Mise à jour à appliquer à la base de données
-- pour les modifications effectuées pendant le développement


BEGIN;
-- Documents (16/11/2012) - update (25/03/2013)
    create table TypeDocument (
        idTypeDocument int4 not null,
        description text not null,
        domaine varchar(60) not null,
        nom varchar(60) not null unique,
        primary key (idTypeDocument),
        unique (nom)
    );

-- Molecules (07/01/2013)
    create table Molecule (
        idMolecule int4 not null,
        complement text,
        familleChimique varchar(60),
        formuleBrute varchar(60) not null,
        formuleDevMol text,
        identifieePar varchar(60),
        masseMolaire numeric(9, 4) check (masseMolaire<=99999 AND masseMolaire>=0),
        nomCommun varchar(100),
        nomIupca varchar(255),
        nouvMolecul boolean not null,
        publiOrigine text,
        campagne_idCampagne int4,
        createur_idPersonne int4 not null,
        primary key (idMolecule)
    );

    create table MoleculeProvenance (
        id int4 not null,
        pourcentage numeric(9, 4) check (pourcentage>=0 AND pourcentage<=100),
        molecule_idMolecule int4 not null,
        produit_id int4 not null,
        primary key (id)
    );
    
    alter table Molecule 
        add constraint FKEC979EA61AA103F8 
        foreign key (campagne_idCampagne) 
        references Campagne;

    alter table Molecule 
        add constraint FKEC979EA6822055B9 
        foreign key (createur_idPersonne) 
        references Personne;

    alter table MoleculeProvenance 
        add constraint FK8B39E567F44F1B20 
        foreign key (molecule_idMolecule) 
        references Molecule;

    alter table MoleculeProvenance 
        add constraint FK8B39E567FE19187A 
        foreign key (produit_id) 
        references Produit;

    create sequence molecule_sequence;

-- Document (12/02/2013)
    create table Document (
        idDocument int4 not null,
        contrainteLegale varchar(100),
        dateCreation date not null,
        description text,
        editeur varchar(100) not null,
        fileContent bytea,
        fileContentThumb bytea,
        fileMimetype varchar(60),
        fileName varchar(60) not null,
        langue varchar(2),
        titre varchar(100) not null,
        ajoutePar_idPersonne int4 not null,
        createur_idPersonne int4 not null,
        typeDocument_idTypeDocument int4 not null,
        specimen int4,
        campagne int4,
        personne int4,
        extraction int4,
        station int4,
        purification int4,
        lot int4,
        resultatTestBio int4,
        molecule int4,
        primary key (idDocument)
    );

    alter table Document 
        add constraint FK3737353B2F46DB31 
        foreign key (ajoutePar_idPersonne) 
        references Personne;

    alter table Document 
        add constraint FK3737353B822055B9 
        foreign key (createur_idPersonne) 
        references Personne;

    alter table Document 
        add constraint FK3737353BBECBA92F 
        foreign key (typeDocument_idTypeDocument) 
        references TypeDocument;

    alter table Document 
        add constraint FK3737353B51427EB0 
        foreign key (specimen) 
        references Specimen;

    alter table Document 
        add constraint FK3737353B3ED66D5C 
        foreign key (campagne) 
        references Campagne;

    alter table Document 
        add constraint FK3737353B8458C838 
        foreign key (personne) 
        references Personne;

    alter table Document 
        add constraint FK3737353BD9E0906E 
        foreign key (extraction) 
        references Extraction;

    alter table Document 
        add constraint FK3737353BC591F5C8 
        foreign key (station) 
        references Station;

    alter table Document 
        add constraint FK3737353B20A56C82 
        foreign key (purification) 
        references Purification;

    alter table Document 
        add constraint FK3737353B767DEA02 
        foreign key (lot) 
        references Lot;

    alter table Document 
        add constraint FK3737353B8923169C 
        foreign key (resultatTestBio) 
        references TestBio;

    alter table Document 
        add constraint FK3737353B20FEEDAC 
        foreign key (molecule) 
        references Molecule;

COMMIT;