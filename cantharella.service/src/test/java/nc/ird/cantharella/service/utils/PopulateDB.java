/*
 * #%L
 * Cantharella :: Service
 * $Id: PopulateDB.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.service/src/test/java/nc/ird/cantharella/service/utils/PopulateDB.java $
 * %%
 * Copyright (C) 2009 - 2012 IRD (Institut de Recherche pour le Developpement) and by respective authors (see below)
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */
package nc.ird.cantharella.service.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import nc.ird.cantharella.data.config.DataContext;
import nc.ird.cantharella.data.exceptions.DataConstraintException;
import nc.ird.cantharella.data.exceptions.DataNotFoundException;
import nc.ird.cantharella.data.model.Campagne;
import nc.ird.cantharella.data.model.CampagnePersonneDroits;
import nc.ird.cantharella.data.model.CampagnePersonneParticipant;
import nc.ird.cantharella.data.model.Lot;
import nc.ird.cantharella.data.model.LotPersonneDroits;
import nc.ird.cantharella.data.model.Partie;
import nc.ird.cantharella.data.model.Personne;
import nc.ird.cantharella.data.model.Specimen;
import nc.ird.cantharella.data.model.Station;
import nc.ird.cantharella.data.model.Utilisateur;
import nc.ird.cantharella.data.model.Utilisateur.TypeDroit;
import nc.ird.cantharella.service.config.ServiceContext;
import nc.ird.cantharella.service.exceptions.ExcelImportException;
import nc.ird.cantharella.service.services.CampagneService;
import nc.ird.cantharella.service.services.LotService;
import nc.ird.cantharella.service.services.PersonneService;
import nc.ird.cantharella.service.services.SpecimenService;
import nc.ird.cantharella.service.services.StationService;
import nc.ird.cantharella.service.utils.ExcelColumnStructure.ExcelColumnType;
import nc.ird.cantharella.utils.CoordTools;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.transaction.annotation.Transactional;

/**
 * Populate DB for importation. JUST A DRAFT, NOT WORKS FOR THE MOMENT
 * 
 * @author acheype
 */
public final class PopulateDB {

    /** Logger */
    private static final Logger LOG = LoggerFactory.getLogger(PopulateDB.class);

    /** Date format */
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("d/M/y");

    /** Excel file structure used to import datas */
    private List<ExcelSheetStructure> excelFileStructure;

    /** Service : campagne */
    @Autowired
    private CampagneService campagneService;

    /** Service : lot */
    @Autowired
    private LotService lotService;

    /** Service : personne */
    @Autowired
    private PersonneService personneService;

    /** Service : spécimen */
    private SpecimenService specimenService;

    /** Service : station */
    @Autowired
    private StationService stationService;

    /**
     * Main
     * 
     * @param args Arguments
     * @throws DataConstraintException -
     * @throws IOException -
     * @throws ExcelImportException -
     * @throws DataNotFoundException -
     */
    public static void main(String[] args) throws DataConstraintException, ExcelImportException, IOException,
            DataNotFoundException {
        new PopulateDB();
    }

    /**
     * Constructor
     * 
     * @throws DataConstraintException -
     * @throws IOException -
     * @throws ExcelImportException -
     * @throws DataNotFoundException -
     */
    public PopulateDB() throws DataConstraintException, IOException, ExcelImportException, DataNotFoundException {
        ApplicationContext ctx = new AnnotationConfigApplicationContext(ServiceContext.class);
        ctx.getAutowireCapableBeanFactory().autowireBean(this);

        ServiceContext.setMailActivated(false);

        initExcelFileStructure();

        InputStream excelStream = FileUtils.openInputStream(new File(
                "/home/adri/workspace/echange_doc/equipe-is/cantharella/jeu_de_donnees.xls"));

        ExcelColumnsReader excelReader = new ExcelColumnsReader(excelStream, excelFileStructure);
        populateAll(excelReader);

        excelStream.close();
    }

    /**
     * @param excelReader -
     * @throws DataConstraintException -
     * @throws ExcelImportException -
     * @throws DataNotFoundException -
     */
    @Transactional
    public void populateAll(ExcelColumnsReader excelReader) throws ExcelImportException, DataConstraintException,
            DataNotFoundException {
        populatePersonneUtilisateur(excelReader);
        populateCampagne(excelReader);
        populateCampagnePersonneDroits(excelReader);
        // populateCampagnePersonneParticipants(excelReader);
        populateStation(excelReader);
    }

    /**
     * Initialise the structure asked for the Excel file
     */
    private void initExcelFileStructure() {

        excelFileStructure = new ArrayList<ExcelSheetStructure>();

        // structure for the entities 'personne'
        ExcelColumnStructure[] personneColsArray = new ExcelColumnStructure[] {
                new ExcelColumnStructure("nom", ExcelColumnType.STRING, true),
                new ExcelColumnStructure("prenom", ExcelColumnType.STRING, true),
                new ExcelColumnStructure("organisme", ExcelColumnType.STRING, true),
                new ExcelColumnStructure("fonction", ExcelColumnType.STRING, false),
                new ExcelColumnStructure("tel", ExcelColumnType.STRING, false),
                new ExcelColumnStructure("fax", ExcelColumnType.STRING, false),
                new ExcelColumnStructure("courriel", ExcelColumnType.STRING, true),
                new ExcelColumnStructure("adressePostale", ExcelColumnType.STRING, true),
                new ExcelColumnStructure("codePostal", ExcelColumnType.STRING, true),
                new ExcelColumnStructure("ville", ExcelColumnType.STRING, true),
                new ExcelColumnStructure("codePays", ExcelColumnType.STRING, true),
                new ExcelColumnStructure("typeDroit", ExcelColumnType.STRING, false) };
        ArrayList<ExcelColumnStructure> personneColsList = new ArrayList<ExcelColumnStructure>(
                Arrays.asList(personneColsArray));
        excelFileStructure.add(new ExcelSheetStructure("personne", personneColsList));

        // structure for the entities 'personne'
        ExcelColumnStructure[] campagneColsArray = new ExcelColumnStructure[] {
                new ExcelColumnStructure("nom", ExcelColumnType.STRING, true),
                new ExcelColumnStructure("codePays", ExcelColumnType.STRING, true),
                new ExcelColumnStructure("programme", ExcelColumnType.STRING, false),
                new ExcelColumnStructure("complement", ExcelColumnType.STRING, false),
                new ExcelColumnStructure("createur", ExcelColumnType.STRING, true),
                new ExcelColumnStructure("dateDeb", ExcelColumnType.DATE, true),
                new ExcelColumnStructure("dateFin", ExcelColumnType.DATE, true) };
        ArrayList<ExcelColumnStructure> campagneColsList = new ArrayList<ExcelColumnStructure>(
                Arrays.asList(campagneColsArray));
        excelFileStructure.add(new ExcelSheetStructure("campagne", campagneColsList));

        // structure for the entities 'campagnePersonneDroit'
        ExcelColumnStructure[] campagnePersonneDroitColsArray = new ExcelColumnStructure[] {
                new ExcelColumnStructure("campagne", ExcelColumnType.STRING, true),
                new ExcelColumnStructure("personne", ExcelColumnType.STRING, true),
                new ExcelColumnStructure("droitRecolte", ExcelColumnType.BOOLEAN, true),
                new ExcelColumnStructure("droitExtrait", ExcelColumnType.BOOLEAN, true),
                new ExcelColumnStructure("droitPuri", ExcelColumnType.BOOLEAN, true),
                new ExcelColumnStructure("droitTest", ExcelColumnType.BOOLEAN, true) };
        ArrayList<ExcelColumnStructure> campagnePersonneDroitColsList = new ArrayList<ExcelColumnStructure>(
                Arrays.asList(campagnePersonneDroitColsArray));
        excelFileStructure.add(new ExcelSheetStructure("campagnePersonneDroits", campagnePersonneDroitColsList));

        // structure for the entities 'campagnePersonneParticipants'
        ExcelColumnStructure[] campagnePersonneParticipantsColsArray = new ExcelColumnStructure[] {
                new ExcelColumnStructure("campagne", ExcelColumnType.STRING, true),
                new ExcelColumnStructure("personne", ExcelColumnType.STRING, true),
                new ExcelColumnStructure("complement", ExcelColumnType.STRING, false) };
        ArrayList<ExcelColumnStructure> campagnePersonneParticipantsColsList = new ArrayList<ExcelColumnStructure>(
                Arrays.asList(campagnePersonneParticipantsColsArray));
        excelFileStructure.add(new ExcelSheetStructure("campagnePersonneParticipants",
                campagnePersonneParticipantsColsList));

        // structure for the entities 'station'
        ExcelColumnStructure[] stationColsArray = new ExcelColumnStructure[] {
                new ExcelColumnStructure("nom", ExcelColumnType.STRING, true),
                new ExcelColumnStructure("codePays", ExcelColumnType.STRING, true),
                new ExcelColumnStructure("localite", ExcelColumnType.STRING, true),
                new ExcelColumnStructure("latitude", ExcelColumnType.STRING, false),
                new ExcelColumnStructure("longitude", ExcelColumnType.STRING, false),
                new ExcelColumnStructure("referentiel", ExcelColumnType.STRING, false),
                new ExcelColumnStructure("complement", ExcelColumnType.STRING, false),
                new ExcelColumnStructure("createur", ExcelColumnType.STRING, true) };
        ArrayList<ExcelColumnStructure> stationColsList = new ArrayList<ExcelColumnStructure>(
                Arrays.asList(stationColsArray));
        excelFileStructure.add(new ExcelSheetStructure("station", stationColsList));

    }

    /**
     * Populate the personne and utilisateur entities
     * 
     * @param excelReader The Excel reader
     * @throws ExcelImportException -
     * @throws DataConstraintException -
     */
    private void populatePersonneUtilisateur(ExcelColumnsReader excelReader) throws ExcelImportException,
            DataConstraintException {
        excelReader.selectSheet("personne");

        Map<String, Object> line = excelReader.readLine();
        while (!line.isEmpty()) {
            String nom = (String) line.get("nom");
            String prenom = (String) line.get("prenom");
            String organisme = (String) line.get("organisme");
            String fonction = (String) line.get("fonction");
            String tel = (String) line.get("tel");
            String fax = (String) line.get("fax");
            String courriel = (String) line.get("courriel");
            String adressePostale = (String) line.get("adressePostale");
            String codePostal = (String) line.get("codePostal");
            String ville = (String) line.get("ville");
            String codePays = (String) line.get("codePays");
            String typeDroit = (String) line.get("typeDroit");
            String password = "password";
            Boolean estValide = true;
            LOG.info("adding personne (xls line " + excelReader.getCurrentNumLine() + ") with values :");
            LOG.info("[nom : '" + nom + "', prenom : '" + prenom + "', organisme : '" + organisme + "', fonction : '"
                    + fonction + "', tel : '" + tel + "', fax : '" + fax + "', courriel : '" + courriel
                    + "', adressePostale : '" + adressePostale + "', codePays : '" + codePays + "', typeDroit : '"
                    + typeDroit + "', password : '" + password + "', estValide : '" + estValide + "']");
            addUtilisateurOrPersonne(nom, prenom, organisme, fonction, tel, fax, courriel, adressePostale, codePostal,
                    ville, codePays, typeDroit, password, estValide);
            line = excelReader.readLine();
        }
    }

    /**
     * Populate the campagne entities
     * 
     * @param excelReader The Excel reader
     * @throws ExcelImportException -
     * @throws DataConstraintException -
     * @throws DataNotFoundException -
     */
    private void populateCampagne(ExcelColumnsReader excelReader) throws ExcelImportException, DataConstraintException,
            DataNotFoundException {
        excelReader.selectSheet("campagne");

        Map<String, Object> line = excelReader.readLine();
        while (!line.isEmpty()) {
            String nom = (String) line.get("nom");
            String codePays = (String) line.get("codePays");
            String programme = (String) line.get("programme");
            String complement = (String) line.get("complement");
            String createur = (String) line.get("createur");
            Date dateDeb = (Date) line.get("dateDeb");
            Date dateFin = (Date) line.get("dateFin");

            LOG.debug("adding campagne (xls line " + excelReader.getCurrentNumLine() + ") with values :");
            LOG.debug("[nom : '" + nom + "', codePays : '" + codePays + "', programme : '" + programme
                    + "', complement : '" + complement + "', createur : '" + createur + "', dateDeb : '" + dateDeb
                    + "', dateFin : '" + dateFin + "']");
            addCampagne(nom, codePays, programme, complement, createur, dateDeb, dateFin);

            line = excelReader.readLine();
        }
    }

    /**
     * Populate the campagnePersonneDroits entities
     * 
     * @param excelReader The Excel reader
     * @throws ExcelImportException -
     * @throws DataConstraintException -
     * @throws DataNotFoundException -
     */
    private void populateCampagnePersonneDroits(ExcelColumnsReader excelReader) throws ExcelImportException,
            DataConstraintException, DataNotFoundException {
        excelReader.selectSheet("campagnePersonneDroits");

        Map<String, Object> line = excelReader.readLine();
        while (!line.isEmpty()) {
            String campagne = (String) line.get("campagne");
            String personne = (String) line.get("personne");
            Boolean droitRecolte = (Boolean) line.get("droitRecolte");
            Boolean droitExtrait = (Boolean) line.get("droitExtrait");
            Boolean droitPuri = (Boolean) line.get("droitPuri");
            Boolean droitTest = (Boolean) line.get("droitTest");

            LOG.debug("adding campagnePersonneDroits (xls line " + excelReader.getCurrentNumLine() + ") with values :");
            LOG.debug("[campagne : '" + campagne + "', personne : '" + personne + "', droitRecolte : '" + droitRecolte
                    + "', droitExtrait : '" + droitExtrait + "', droitPuri : '" + droitPuri + "', droitTest : '"
                    + droitTest + "']");
            addCampagnePersonneDroits(campagne, personne, droitExtrait, droitPuri, droitRecolte, droitTest);

            line = excelReader.readLine();
        }
    }

    /**
     * Populate the campagnePersonneParticipants entities
     * 
     * @param excelReader The Excel reader
     * @throws ExcelImportException -
     * @throws DataConstraintException -
     * @throws DataNotFoundException -
     */
    @SuppressWarnings("unused")
    private void populateCampagnePersonneParticipants(ExcelColumnsReader excelReader) throws ExcelImportException,
            DataConstraintException, DataNotFoundException {
        excelReader.selectSheet("campagnePersonneParticipants");

        Map<String, Object> line = excelReader.readLine();
        while (!line.isEmpty()) {
            String campagne = (String) line.get("campagne");
            String personne = (String) line.get("personne");
            String complement = (String) line.get("complement");

            LOG.debug("adding campagnePersonneParticipants (xls line " + excelReader.getCurrentNumLine()
                    + ") with values :");
            LOG.debug("[campagne : '" + campagne + "', personne : '" + personne + "', complement : '" + complement
                    + "']");
            addCampagnePersonneParticipants(campagne, personne, complement);

            line = excelReader.readLine();
        }
    }

    /**
     * Populate the station entities
     * 
     * @param excelReader The Excel reader
     * @throws ExcelImportException -
     * @throws DataConstraintException -
     * @throws DataNotFoundException -
     */
    private void populateStation(ExcelColumnsReader excelReader) throws ExcelImportException, DataConstraintException,
            DataNotFoundException {
        excelReader.selectSheet("station");

        Map<String, Object> line = excelReader.readLine();
        while (!line.isEmpty()) {
            String nom = (String) line.get("nom");
            String codePays = (String) line.get("codePays");
            String localite = (String) line.get("localite");
            String latitude = (String) line.get("latitude");
            String longitude = (String) line.get("longitude");
            String referentiel = (String) line.get("referentiel");
            String complement = (String) line.get("complement");
            String createur = (String) line.get("createur");

            LOG.debug("adding campagnePersonneParticipants (xls line " + excelReader.getCurrentNumLine()
                    + ") with values :");
            LOG.debug("[nom :'" + nom + "', codePays : '" + codePays + "', localite : '" + localite + "', latitude : '"
                    + latitude + "', longitude : '" + longitude + "', referentiel : '" + referentiel
                    + "', complement : '" + complement + "', createur : '" + createur + "']");
            addStation(nom, codePays, localite, latitude, longitude, complement, createur, referentiel);

            line = excelReader.readLine();
        }
    }

    /**
     * Ajouter une campagne
     * 
     * @param nom -
     * @param codePays -
     * @param programme -
     * @param complement -
     * @param createur -
     * @param dateDeb -
     * @param dateFin -
     * @throws DataConstraintException -
     * @throws DataNotFoundException -
     */
    private void addCampagne(String nom, String codePays, String programme, String complement, String createur,
            Date dateDeb, Date dateFin) throws DataConstraintException, DataNotFoundException {
        Campagne c = new Campagne();
        c.setNom(nom);
        c.setCodePays(codePays);
        c.setProgramme(programme);
        c.setComplement(complement);
        c.setCreateur(personneService.loadPersonne(createur));
        c.setDateDeb(dateDeb);
        c.setDateFin(dateFin);
        campagneService.createCampagne(c);
    }

    /**
     * Ajouter des droits pour une personne sur une campagne
     * 
     * @param campagne -
     * @param personne -
     * @param droitExtrait -
     * @param droitPuri -
     * @param droitRecolte -
     * @param droitTest -
     * @throws DataNotFoundException -
     * @throws DataConstraintException -
     */
    private void addCampagnePersonneDroits(String campagne, String personne, Boolean droitExtrait, Boolean droitPuri,
            Boolean droitRecolte, Boolean droitTest) throws DataNotFoundException, DataConstraintException {
        CampagnePersonneDroits d = new CampagnePersonneDroits();
        d.getId().setPk1(campagneService.loadCampagne(campagne));
        d.getId().setPk2(personneService.loadPersonne(personne));
        d.getDroits().setDroitExtrait(droitExtrait);
        d.getDroits().setDroitPuri(droitPuri);
        d.getDroits().setDroitRecolte(droitRecolte);
        d.getDroits().setDroitTestBio(droitTest);
        // d.id.pk1.personnesDroits.add(d);
        // d.id.pk2.campagnesDroits.put(d.id.pk1, d);
        personneService.updatePersonne(d.getId().getPk2());
    }

    /**
     * Ajouter un participant à une campagne
     * 
     * @param campagne -
     * @param personne -
     * @param complement -
     * @throws DataNotFoundException -
     * @throws DataConstraintException -
     */
    private void addCampagnePersonneParticipants(String campagne, String personne, String complement)
            throws DataNotFoundException, DataConstraintException {
        CampagnePersonneParticipant p = new CampagnePersonneParticipant();
        p.getId().setPk1(campagneService.loadCampagne(campagne));
        p.getId().setPk2(personneService.loadPersonne(personne));
        p.setComplement(complement);
        p.getId().getPk1().getParticipants().add(p);
        p.getId().getPk2().getCampagnesParticipees().add(p);
        campagneService.updateCampagne(p.getId().getPk1());
    }

    /**
     * Ajouter un lot
     * 
     * @param campagne -
     * @param complement -
     * @param createur -
     * @param date -
     * @param echantillonColl -
     * @param echantillonIdent -
     * @param echantillonPhylo -
     * @param masseFraiche -
     * @param masseSeche -
     * @param partie -
     * @param ref -
     * @param specimenRef -
     * @param station -
     * @throws DataNotFoundException -
     * @throws ParseException -
     */
    @SuppressWarnings("unused")
    private void addLot(String campagne, String complement, String createur, String date, String echantillonColl,
            String echantillonIdent, String echantillonPhylo, String masseFraiche, String masseSeche, String partie,
            String ref, String specimenRef, String station) throws DataNotFoundException, ParseException {
        Lot l = new Lot();
        l.setCampagne(campagneService.loadCampagne(campagne));
        l.setComplement(complement);
        l.setCreateur(personneService.loadPersonne(createur));
        l.setDateRecolte(DATE_FORMAT.parse(date));
        l.setEchantillonColl(Boolean.valueOf(echantillonColl));
        l.setEchantillonIdent(Boolean.valueOf(echantillonIdent));
        l.setEchantillonPhylo(Boolean.valueOf(echantillonPhylo));
        DecimalFormat fmt = (DecimalFormat) NumberFormat.getNumberInstance(Locale.FRENCH);
        fmt.setMaximumFractionDigits(DataContext.DECIMAL_SCALE);
        fmt.setMaximumIntegerDigits(DataContext.DECIMAL_PRECISION - DataContext.DECIMAL_SCALE);
        fmt.setParseBigDecimal(true);
        l.setMasseFraiche((BigDecimal) fmt.parse(masseFraiche));
        l.setMasseSeche((BigDecimal) fmt.parse(masseSeche));
        l.setPartie(lotService.loadPartie(partie));
        l.setRef(ref);
        l.setSpecimenRef(specimenService.loadSpecimen(specimenRef));
        l.setStation(stationService.loadStation(station));
    }

    /**
     * Ajouter des droits pour une personne sur un lot
     * 
     * @param lot -
     * @param personne -
     * @param droitExtrait -
     * @param droitPuri -
     * @param droitRécolte -
     * @param droitTest -
     * @throws DataNotFoundException -
     * @throws DataConstraintException -
     */
    @SuppressWarnings("unused")
    private void addLotPersonneDroits(String lot, String personne, String droitExtrait, String droitPuri,
            String droitRécolte, String droitTest) throws DataNotFoundException, DataConstraintException {
        LotPersonneDroits d = new LotPersonneDroits();
        d.getId().setPk1(lotService.loadLot(lot));
        d.getId().setPk2(personneService.loadPersonne(personne));
        d.getDroits().setDroitExtrait(Boolean.valueOf(droitExtrait));
        d.getDroits().setDroitPuri(Boolean.valueOf(droitPuri));
        d.getDroits().setDroitRecolte(Boolean.valueOf(droitRécolte));
        d.getDroits().setDroitTestBio(Boolean.valueOf(droitTest));
        d.getId().getPk1().getPersonnesDroits().add(d);
        d.getId().getPk2().getLotsDroits().put(d.getId().getPk1(), d);
        personneService.updatePersonne(d.getId().getPk2());
    }

    /**
     * Ajouter une partie
     * 
     * @param nom -
     * @throws DataConstraintException -
     */
    @SuppressWarnings("unused")
    private void addPartie(String nom) throws DataConstraintException {
        Partie p = new Partie();
        p.setNom(nom);
        lotService.createPartie(p);
    }

    /**
     * Ajouter un spécimen
     * 
     * @param ref -
     * @param embranchement -
     * @throws DataConstraintException -
     */
    @SuppressWarnings("unused")
    private void addSpecimen(String ref, String embranchement) throws DataConstraintException {
        Specimen s = new Specimen();
        s.setRef(ref);
        s.setEmbranchement(embranchement);
        specimenService.createSpecimen(s);
    }

    /**
     * Ajouter une campagne
     * 
     * @param nom -
     * @param codePays -
     * @param localite -
     * @param latitude -
     * @param longitude -
     * @param complement -
     * @param createur -
     * @param referentiel -
     * @throws DataNotFoundException -
     * @throws DataConstraintException -
     * @throws ExcelImportException -
     */
    private void addStation(String nom, String codePays, String localite, String latitude, String longitude,
            String complement, String createur, String referentiel) throws DataNotFoundException,
            DataConstraintException, ExcelImportException {
        Station s = new Station();
        s.setNom(nom);
        s.setCodePays(codePays);
        s.setComplement(complement);
        s.setCreateur(personneService.loadPersonne(createur));
        s.setLatitude(StringUtils.leftPad(StringUtils.deleteWhitespace(latitude), CoordTools.LATITUDE_LENGTH));
        s.setLongitude(StringUtils.leftPad(StringUtils.deleteWhitespace(longitude), CoordTools.LONGITUDE_LENGTH));
        s.setReferentiel(getReferentialNumber(referentiel));
        s.setLocalite(localite);
        stationService.createStation(s);
    }

    /**
     * Get the referentiel number associated to the given referential
     * 
     * @param referentialString referential name
     * @return the corresponding number
     * @throws ExcelImportException -
     */
    private Integer getReferentialNumber(String referentialString) throws ExcelImportException {
        for (Map.Entry<Integer, String> curEntry : DataContext.REFERENTIELS.entrySet()) {
            if (curEntry.getValue().equals(referentialString)) {
                return curEntry.getKey();
            }
        }
        throw new ExcelImportException("'" + referentialString + "' is not a allowed referential");
    }

    /**
     * Ajoute une personne ou un utilisateur
     * 
     * @param nom -
     * @param prenom -
     * @param organisme -
     * @param fonction -
     * @param tel -
     * @param fax -
     * @param courriel -
     * @param adressePostale -
     * @param codePostal -
     * @param ville -
     * @param codePays -
     * @param typeDroit -
     * @param password -
     * @param estValide -
     * @throws DataConstraintException -
     */
    private void addUtilisateurOrPersonne(String nom, String prenom, String organisme, String fonction, String tel,
            String fax, String courriel, String adressePostale, String codePostal, String ville, String codePays,
            String typeDroit, String password, Boolean estValide) throws DataConstraintException {
        if (StringUtils.isEmpty(typeDroit)) {
            Personne p = new Personne();
            p.setNom(nom);
            p.setPrenom(prenom);
            p.setOrganisme(organisme);
            p.setFonction(fonction);
            p.setTel(tel);
            p.setFax(fax);
            p.setCourriel(courriel);
            p.setAdressePostale(adressePostale);
            p.setCodePostal(codePostal);
            p.setVille(ville);
            p.setCodePays(codePays);
            personneService.createPersonne(p);
        } else {
            Utilisateur u = new Utilisateur();
            u.setNom(nom);
            u.setPrenom(prenom);
            u.setOrganisme(organisme);
            u.setFonction(fonction);
            u.setTel(tel);
            u.setFax(fax);
            u.setCourriel(courriel);
            u.setAdressePostale(adressePostale);
            u.setCodePostal(codePostal);
            u.setVille(ville);
            u.setCodePays(codePays);
            u.setTypeDroit(TypeDroit.valueOf(typeDroit));
            u.setPasswordHash(personneService.hashPassword(password));
            u.setValide(estValide);
            personneService.createUtilisateur(u);
        }
    }
}