---
-- #%L
-- Cantharella :: Data
-- $Id: dev_update_1.1_to_1.2.sql 211 2013-04-30 07:31:44Z acheype $
-- $HeadURL: http://svn.forge.codelutin.com/svn/cantharella/trunk/cantharella.data/src/main/sql/dev_update_1.0_to_1.1.sql $
-- %%
-- Copyright (C) 2014 IRD (Institut de Recherche pour le Developpement) and by respective authors (see below)
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

-- insert once connected to cantharella database with the user cantharella
BEGIN;

    -- SCHEMA 1.1

    create table Campagne (
        idCampagne int4 not null,
        codePays varchar(255) not null,
        complement text,
        dateDeb date not null,
        dateFin date not null,
        mentionLegale text,
        nom varchar(255) unique not null,
        programme varchar(255),
        createur_idPersonne int4 not null,
        primary key (idCampagne)
    );

    create table CampagneGroupeDroits (
        droitExtrait bool,
        droitPuri bool,
        droitRecolte bool,
        droitTestBio bool,
        pk1_idCampagne int4 not null,
        pk2_idGroupe int4 not null,
        primary key (pk1_idCampagne, pk2_idGroupe)
    );

    create table CampagnePersonneDroits (
        droitExtrait bool,
        droitPuri bool,
        droitRecolte bool,
        droitTestBio bool,
        pk1_idCampagne int4 not null,
        pk2_idPersonne int4 not null,
        primary key (pk1_idCampagne, pk2_idPersonne)
    );

    create table CampagnePersonneParticipant (
        complement text,
        pk1_idCampagne int4 not null,
        pk2_idPersonne int4 not null,
        primary key (pk1_idCampagne, pk2_idPersonne)
    );

    create table Campagne_Station (
        campagnes_idCampagne int4 not null,
        stations_idStation int4 not null
    );

    create table ErreurTestBio (
        idErreurTest int4 not null,
        description text not null,
        nom varchar(255) not null,
        primary key (idErreurTest),
        unique (nom)
    );

    create table Extraction (
        idExtraction int4 not null,
        complement text,
        date date not null,
        masseDepart numeric(9, 4),
        ref varchar(255) unique not null,
        createur_idPersonne int4 not null,
        lot_idLot int4 not null,
        manipulateur_idPersonne int4 not null,
        methode_idMethodeExtraction int4 not null,
        primary key (idExtraction)
    );

    create table Extrait (
        id int4 not null,
        extraction_idExtraction int4 not null,
        typeExtrait_idTypeExtrait int4 not null,
        primary key (id)
    );

    create table Fraction (
        indice varchar(255) not null,
        id int4 not null,
        purification_idPurification int4 not null,
        primary key (id)
    );

    create table Groupe (
        idGroupe int4 not null,
        description text not null,
        nom varchar(255) unique not null,
        primary key (idGroupe)
    );

    create table Lot (
        idLot int4 not null,
        complement text,
        dateRecolte date not null,
        echantillonColl bool not null,
        echantillonIdent bool not null,
        echantillonPhylo bool not null,
        masseFraiche numeric(9, 4),
        masseSeche numeric(9, 4),
        ref varchar(255) unique not null,
        campagne_idCampagne int4 not null,
        createur_idPersonne int4 not null,
        partie_idPartie int4,
        specimenRef_idSpecimen int4 not null,
        station_idStation int4 not null,
        primary key (idLot)
    );

    create table LotGroupeDroits (
        droitExtrait bool,
        droitPuri bool,
        droitRecolte bool,
        droitTestBio bool,
        pk1_idLot int4 not null,
        pk2_idGroupe int4 not null,
        primary key (pk1_idLot, pk2_idGroupe)
    );

    create table LotPersonneDroits (
        droitExtrait bool,
        droitPuri bool,
        droitRecolte bool,
        droitTestBio bool,
        pk2_idPersonne int4 not null,
        pk1_idLot int4 not null,
        primary key (pk1_idLot, pk2_idPersonne)
    );

    create table MethodeExtraction (
        idMethodeExtraction int4 not null,
        description text not null,
        nom varchar(255) unique not null,
        primary key (idMethodeExtraction)
    );

    create table MethodePurification (
        idMethodePurification int4 not null,
        description text not null,
        nom varchar(255) unique not null,
        primary key (idMethodePurification)
    );

    create table MethodeTestBio (
        idMethodeTest int4 not null,
        cible varchar(255) unique  not null,
        critereActivite varchar(255) not null,
        description text not null,
        domaine varchar(255) not null,
        nom varchar(255) unique not null,
        uniteResultat varchar(255) not null,
        valeurMesuree varchar(255) not null,
        primary key (idMethodeTest)
    );

    create table ParamMethoPuri (
        idParamMethoPuri int4 not null,
        description text not null,
        index int4 not null,
        nom varchar(255) not null,
        methodePurification_idMethodePurification int4 not null,
        primary key (idParamMethoPuri)
    );

    create table ParamMethoPuriEffectif (
        idParamMethoPuriEffectif int4 not null,
        valeur varchar(255),
        param_idParamMethoPuri int4 not null,
        purification_idPurification int4 not null,
        primary key (idParamMethoPuriEffectif)
    );

    create table Partie (
        idPartie int4 not null,
        nom varchar(255) unique not null,
        primary key (idPartie)
    );

    create table Personne (
        idPersonne int4 not null,
        adressePostale text not null,
        codePays varchar(255) not null,
        codePostal varchar(255) not null,
        courriel varchar(255) unique not null,
        fax varchar(255),
        fonction varchar(255),
        nom varchar(255) not null,
        organisme varchar(255) not null,
        prenom varchar(255) not null,
        tel varchar(255),
        ville varchar(255) not null,
        primary key (idPersonne),
        unique (nom, prenom)
    );

    create table Produit (
        id int4 not null,
        masseObtenue numeric(9, 4),
        ref varchar(255) unique not null,
        primary key (id)
    );

    create table Purification (
        idPurification int4 not null,
        complement text,
        confidentiel bool not null,
        date date not null,
        dateConfidentialite date,
        masseDepart numeric(9, 4),
        ref varchar(255) unique not null,
        createur_idPersonne int4 not null,
        lotSource_idLot int4 not null,
        manipulateur_idPersonne int4 not null,
        methode_idMethodePurification int4 not null,
        produit_id int4 not null,
        primary key (idPurification)
    );

    create table ResultatTestBio (
        id int4 not null,
        concMasse numeric(9, 4),
        estActif bool,
        produitTemoin varchar(255),
        repere varchar(255) not null,
        stade int4,
        typeResultat int4 not null,
        uniteConcMasse int4,
        valeur numeric(9, 4),
        erreur_idErreurTest int4,
        produit_id int4,
        testBio_idTestBio int4 not null,
        typeExtraitSource_idTypeExtrait int4,
        primary key (id)
    );

    create table Specimen (
        idSpecimen int4 not null,
        complement text,
        dateDepot date,
        embranchement varchar(255) not null,
        espece varchar(255),
        famille varchar(255),
        genre varchar(255),
        lieuDepot varchar(255),
        numDepot varchar(255),
        ref varchar(255) unique not null,
        sousEspece varchar(255),
        typeOrganisme int4,
        variete varchar(255),
        createur_idPersonne int4 not null,
        identificateur_idPersonne int4,
        station_idStation int4,
        primary key (idSpecimen)
    );

    create table Station (
        idStation int4 not null,
        codePays varchar(255) not null,
        complement text,
        latitude varchar(255),
        localite varchar(255),
        longitude varchar(255),
        nom varchar(255) unique not null,
        referentiel int4,
        createur_idPersonne int4 not null,
        primary key (idStation)
    );

    create table TestBio (
        idTestBio int4 not null,
        complement text,
        concMasseDefaut numeric(9, 4),
        confidentiel bool not null,
        date date not null,
        dateConfidentialite date,
        organismeTesteur varchar(255) not null,
        ref varchar(255) unique not null,
        stadeDefaut int4,
        uniteConcMasseDefaut int4,
        createur_idPersonne int4 not null,
        manipulateur_idPersonne int4 not null,
        methode_idMethodeTest int4 not null,
        primary key (idTestBio)
    );

    create table TypeExtrait (
        idTypeExtrait int4 not null,
        description text not null,
        initiales varchar(255) unique not null,
        methodeExtraction_idMethodeExtraction int4 not null,
        primary key (idTypeExtrait)
    );

    create table Utilisateur (
        dateValiditeCompte date,
        estValide bool not null,
        passwordHash varchar(255) not null,
        typeDroit int4 not null,
        idPersonne int4 not null,
        groupe_idGroupe int4,
        primary key (idPersonne)
    );

    alter table Campagne
        add constraint FKFB835E7E822055B9
        foreign key (createur_idPersonne)
        references Personne;

    alter table CampagneGroupeDroits
        add constraint FK3AFBE26B905CBAAF
        foreign key (pk2_idGroupe)
        references Groupe;

    alter table CampagneGroupeDroits
        add constraint FK3AFBE26B975C19C0
        foreign key (pk1_idCampagne)
        references Campagne;

    alter table CampagnePersonneDroits
        add constraint FK81782471975C19C0
        foreign key (pk1_idCampagne)
        references Campagne;

    alter table CampagnePersonneDroits
        add constraint FK81782471E49019FB
        foreign key (pk2_idPersonne)
        references Personne;

    alter table CampagnePersonneParticipant
        add constraint FK72EE0469975C19C0
        foreign key (pk1_idCampagne)
        references Campagne;

    alter table CampagnePersonneParticipant
        add constraint FK72EE0469E49019FB
        foreign key (pk2_idPersonne)
        references Personne;

    alter table Campagne_Station
        add constraint FK61A84053557B2C21
        foreign key (campagnes_idCampagne)
        references Campagne;

    alter table Campagne_Station
        add constraint FK61A840531AE541CD
        foreign key (stations_idStation)
        references Station;

    alter table Extraction
        add constraint FKA39DBC077106663B
        foreign key (manipulateur_idPersonne)
        references Personne;

    alter table Extraction
        add constraint FKA39DBC07822055B9
        foreign key (createur_idPersonne)
        references Personne;

    alter table Extraction
        add constraint FKA39DBC07F7C7FFC0
        foreign key (methode_idMethodeExtraction)
        references MethodeExtraction;

    alter table Extraction
        add constraint FKA39DBC07AE638899
        foreign key (lot_idLot)
        references Lot;

    alter table Extrait
        add constraint FK156B751B9547CE24
        foreign key (id)
        references Produit;

    alter table Extrait
        add constraint FK156B751B252FD5C9
        foreign key (typeExtrait_idTypeExtrait)
        references TypeExtrait;

    alter table Extrait
        add constraint FK156B751BE48A1121
        foreign key (extraction_idExtraction)
        references Extraction;

    alter table Fraction
        add constraint FKA14826229547CE24
        foreign key (id)
        references Produit;

    alter table Fraction
        add constraint FKA1482622859CB52B
        foreign key (purification_idPurification)
        references Purification;

    alter table Lot
        add constraint FK12B311AA103F8
        foreign key (campagne_idCampagne)
        references Campagne;

    alter table Lot
        add constraint FK12B3168370809
        foreign key (partie_idPartie)
        references Partie;

    alter table Lot
        add constraint FK12B31822055B9
        foreign key (createur_idPersonne)
        references Personne;

    alter table Lot
        add constraint FK12B31DDF1E8FF
        foreign key (specimenRef_idSpecimen)
        references Specimen;

    alter table Lot
        add constraint FK12B31CBED0F02
        foreign key (station_idStation)
        references Station;

    alter table LotGroupeDroits
        add constraint FK9AC53A9E905CBAAF
        foreign key (pk2_idGroupe)
        references Groupe;

    alter table LotGroupeDroits
        add constraint FK9AC53A9E87AF0F1E
        foreign key (pk1_idLot)
        references Lot;

    alter table LotPersonneDroits
        add constraint FK144C3BE4E49019FB
        foreign key (pk2_idPersonne)
        references Personne;

    alter table LotPersonneDroits
        add constraint FK144C3BE487AF0F1E
        foreign key (pk1_idLot)
        references Lot;

    alter table ParamMethoPuri
        add constraint FK1476479225A42085
        foreign key (methodePurification_idMethodePurification)
        references MethodePurification;

    alter table ParamMethoPuriEffectif
        add constraint FK8F7FA020B4F23AD1
        foreign key (param_idParamMethoPuri)
        references ParamMethoPuri;

    alter table ParamMethoPuriEffectif
        add constraint FK8F7FA020859CB52B
        foreign key (purification_idPurification)
        references Purification;

    create index courriel on Personne (courriel);

    alter table Purification
        add constraint FKD1727611FE19187A
        foreign key (produit_id)
        references Produit;

    alter table Purification
        add constraint FKD17276117106663B
        foreign key (manipulateur_idPersonne)
        references Personne;

    alter table Purification
        add constraint FKD1727611822055B9
        foreign key (createur_idPersonne)
        references Personne;

    alter table Purification
        add constraint FKD1727611626E4F54
        foreign key (methode_idMethodePurification)
        references MethodePurification;

    alter table Purification
        add constraint FKD1727611C4943FB4
        foreign key (lotSource_idLot)
        references Lot;

    create index typeResultat on ResultatTestBio (typeResultat);

    alter table ResultatTestBio
        add constraint FK5DFD5BC6FE19187A
        foreign key (produit_id)
        references Produit;

    alter table ResultatTestBio
        add constraint FK5DFD5BC65033BE68
        foreign key (testBio_idTestBio)
        references TestBio;

    alter table ResultatTestBio
        add constraint FK5DFD5BC64B1883E5
        foreign key (erreur_idErreurTest)
        references ErreurTestBio;

    alter table ResultatTestBio
        add constraint FK5DFD5BC6ABE26DE4
        foreign key (typeExtraitSource_idTypeExtrait)
        references TypeExtrait;

    alter table Specimen
        add constraint FK84B96728822055B9
        foreign key (createur_idPersonne)
        references Personne;

    alter table Specimen
        add constraint FK84B96728D2CE02CA
        foreign key (identificateur_idPersonne)
        references Personne;

    alter table Specimen
        add constraint FK84B96728CBED0F02
        foreign key (station_idStation)
        references Station;

    alter table Station
        add constraint FKF2249914822055B9
        foreign key (createur_idPersonne)
        references Personne;

    alter table TestBio
        add constraint FKE6F36F67106663B
        foreign key (manipulateur_idPersonne)
        references Personne;

    alter table TestBio
        add constraint FKE6F36F6822055B9
        foreign key (createur_idPersonne)
        references Personne;

    alter table TestBio
        add constraint FKE6F36F67DBA1C52
        foreign key (methode_idMethodeTest)
        references MethodeTestBio;

    alter table TypeExtrait
        add constraint FK40F3D8811D52927
        foreign key (methodeExtraction_idMethodeExtraction)
        references MethodeExtraction;

    create index estValide on Utilisateur (estValide);

    create index typeDroit on Utilisateur (typeDroit);

    alter table Utilisateur
        add constraint FK407FDB6370C95760
        foreign key (groupe_idGroupe)
        references Groupe;

    alter table Utilisateur
        add constraint FK407FDB63A1338A53
        foreign key (idPersonne)
        references Personne;

    create sequence hibernate_sequence;

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

    -- END OF SCHEMA 1.1

    -- MIGRATION FROM SCHEMA 1.1 TO 1.2

    -- DocumentContent (05/05/2014)
    create table DocumentContent (
        idDocumentContent int4 not null,
        fileContent bytea not null,
        tmpDocument int4,
        tmpDocumentThumb int4,
        primary key (idDocumentContent)
    );

    -- move all file content to new table
    insert into DocumentContent (idDocumentContent, fileContent, tmpDocument)
        SELECT nextval('hibernate_sequence'), fileContent, idDocument from document;
    -- move all thumbnail content to new table
    insert into DocumentContent (idDocumentContent, fileContent, tmpDocumentThumb)
        SELECT nextval('hibernate_sequence'), fileContentThumb, idDocument FROM document where fileContentThumb is not NULL;

    -- link documentcontent to document
    alter table Document add column fileContent_idDocumentContent int4;
    alter table Document add column fileContentThumb_idDocumentContent int4;

    update Document SET fileContent_idDocumentContent = subquery.idDocumentContent
        FROM (SELECT idDocumentContent, tmpDocument FROM DocumentContent) AS subquery
        WHERE subquery.tmpDocument = Document.idDocument;
    update Document SET fileContentThumb_idDocumentContent = subquery.idDocumentContent
        FROM (SELECT idDocumentContent, tmpDocumentThumb FROM DocumentContent) AS subquery
        WHERE subquery.tmpDocumentThumb = Document.idDocument;

    alter table Document 
        alter column fileContent_idDocumentContent set not null;

    alter table Document 
        add constraint FK_3l4n85rsmw88bewfx57o7gg9g 
        foreign key (fileContent_idDocumentContent) 
        references DocumentContent;

    alter table Document 
        add constraint FK_mphot3xs24tg4u5na1bgo1h85 
        foreign key (fileContentThumb_idDocumentContent) 
        references DocumentContent;

    -- clean
    alter table Document drop column fileContent;
    alter table Document drop column fileContentThumb;
    alter table DocumentContent drop column tmpDocument;
    alter table DocumentContent drop column tmpDocumentThumb;

    -- END OF MIGRATION FROM SCHEMA 1.1 TO 1.2
COMMIT;