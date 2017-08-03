/*
 * #%L
 * Cantharella :: Service
 * $Id: SearchServiceTest.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.service/src/test/java/nc/ird/cantharella/service/services/SearchServiceTest.java $
 * %%
 * Copyright (C) 2009 - 2013 IRD (Institut de Recherche pour le Developpement) and by respective authors (see below)
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
package nc.ird.cantharella.service.services;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import nc.ird.cantharella.data.exceptions.DataConstraintException;
import nc.ird.cantharella.data.model.Campagne;
import nc.ird.cantharella.data.model.Extraction;
import nc.ird.cantharella.data.model.Extrait;
import nc.ird.cantharella.data.model.Lot;
import nc.ird.cantharella.data.model.MethodeExtraction;
import nc.ird.cantharella.data.model.MethodeTestBio;
import nc.ird.cantharella.data.model.ResultatTestBio;
import nc.ird.cantharella.data.model.ResultatTestBio.TypeResultat;
import nc.ird.cantharella.data.model.Specimen;
import nc.ird.cantharella.data.model.Specimen.TypeOrganisme;
import nc.ird.cantharella.data.model.Station;
import nc.ird.cantharella.data.model.TestBio;
import nc.ird.cantharella.data.model.TypeExtrait;
import nc.ird.cantharella.data.model.Utilisateur;
import nc.ird.cantharella.service.AbstractServiceTest;
import nc.ird.cantharella.service.model.SearchBean;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Search service tests.
 * 
 * @author echatellier
 */
public class SearchServiceTest extends AbstractServiceTest {

    /** Administrateur par défaut */
    @Autowired
    private Utilisateur defaultAdmin;

    @Autowired
    private PersonneService personneService;

    @Autowired
    private CampagneService campagneService;

    @Autowired
    private StationService stationService;

    @Autowired
    private SpecimenService specimenService;

    @Autowired
    private LotService lotService;

    @Autowired
    private SearchService searchService;

    @Autowired
    private TestBioService testBioService;

    @Autowired
    private ExtractionService extractionService;

    /**
     * L'index devrait normalement etre rollback par spring comme la transaction. Mais visiblement cela ne fonctionne
     * pas.
     */
    @Before
    public void clearIndex() {
        searchService.reIndex();
    }

    /**
     * Test to create some specimen and search for it.
     * 
     * @throws DataConstraintException
     */
    @Test
    public void testSearchSpecimen() throws DataConstraintException {

        // fix user creation for test
        defaultAdmin.setValide(Boolean.TRUE);
        personneService.createPersonne(defaultAdmin);

        // create some specimens
        Specimen specimen1 = new Specimen();
        specimen1.setRef("P175");
        specimen1.setEmbranchement("embranchement 175");
        specimen1.setTypeOrganisme(TypeOrganisme.PLANTE);
        specimen1.setCreateur(defaultAdmin);
        specimenService.createSpecimen(specimen1);

        Specimen specimen2 = new Specimen();
        specimen2.setRef("P174");
        specimen2.setEmbranchement("embranchement 174");
        specimen2.setTypeOrganisme(TypeOrganisme.CHAMPIGNON);
        specimen2.setCreateur(defaultAdmin);
        specimenService.createSpecimen(specimen2);

        // test to create some entity
        Assert.assertEquals(2, specimenService.countSpecimens());

        // search into lucene
        List<Specimen> specimens = searchService.search(new SearchBean("P175"), defaultAdmin).getSpecimens();
        Assert.assertEquals(specimen1, specimens.get(0));
        specimens = searchService.search(new SearchBean("P174"), defaultAdmin).getSpecimens();
        Assert.assertEquals(specimen2, specimens.get(0));
        specimens = searchService.search(new SearchBean("P17?"), defaultAdmin).getSpecimens();
        Assert.assertEquals(2, specimens.size());
    }

    /**
     * Test to create some lot and search for it.
     * 
     * @throws DataConstraintException
     */
    @Test
    public void testSearchLot() throws DataConstraintException {

        // fix user creation for test
        defaultAdmin.setValide(Boolean.TRUE);
        personneService.createPersonne(defaultAdmin);

        // station
        Station station = new Station();
        station.setNom("MT1");
        station.setCodePays("FR");
        station.setCreateur(defaultAdmin);
        stationService.createStation(station);

        // campagne
        Campagne campagne = new Campagne();
        campagne.setNom("BSM-PF1");
        campagne.setDateDeb(new Date());
        campagne.setDateFin(new Date());
        campagne.setCreateur(defaultAdmin);
        campagne.setCodePays("FR");
        campagneService.createCampagne(campagne);

        // create some specimens
        Specimen specimen1 = new Specimen();
        specimen1.setRef("P175");
        specimen1.setEmbranchement("Porifera");
        specimen1.setTypeOrganisme(TypeOrganisme.PLANTE);
        specimen1.setCreateur(defaultAdmin);
        specimenService.createSpecimen(specimen1);

        // create some lots
        Lot lot1 = new Lot();
        lot1.setRef("P164-MHO4");
        lot1.setStation(station);
        lot1.setEchantillonPhylo(false);
        lot1.setEchantillonColl(false);
        lot1.setEchantillonIdent(true);
        lot1.setCampagne(campagne);
        lot1.setDateRecolte(new Date());
        lot1.setSpecimenRef(specimen1);
        lot1.setCreateur(defaultAdmin);
        lotService.createLot(lot1);

        Assert.assertEquals(1, lotService.countLots()); // force sync

        // search into lucene
        List<Lot> lots = searchService.search(new SearchBean("MHO4"), defaultAdmin).getLots();
        Assert.assertEquals(lot1, lots.get(0));
        lots = searchService.search(new SearchBean("Foo"), defaultAdmin).getLots();
        Assert.assertTrue(lots.isEmpty());

        // test de recherche sur les entités liées
        lots = searchService.search(new SearchBean("porifera"), defaultAdmin).getLots();
        Assert.assertEquals(lot1, lots.get(0));

        // update associated property
        specimen1.setEmbranchement("Niphatidae");
        specimenService.updateSpecimen(specimen1);
        Assert.assertEquals(1, lotService.countLots()); // force sync
        lots = searchService.search(new SearchBean("porifera"), defaultAdmin).getLots();
        //Assert.assertTrue(lots.isEmpty());
        lots = searchService.search(new SearchBean("niphatidae"), defaultAdmin).getLots();
        //Assert.assertEquals(lot1, lots.get(0));

        // update direct entity
        lot1.setRef("P175-MT4+5");
        lotService.updateLot(lot1);
        Assert.assertEquals(1, lotService.countLots()); // force sync
        lots = searchService.search(new SearchBean("porifera"), defaultAdmin).getLots();
        Assert.assertTrue(lots.isEmpty());
        lots = searchService.search(new SearchBean("niphatidae"), defaultAdmin).getLots();
        Assert.assertEquals(lot1, lots.get(0));
    }

    /**
     * Test to create some resultat test bio and search for it.
     * 
     * @throws DataConstraintException
     */
    @Test
    public void testSearchResultatTestBio() throws DataConstraintException {
        // fix user creation for test
        defaultAdmin.setValide(Boolean.TRUE);
        personneService.createPersonne(defaultAdmin);

        // station
        Station station = new Station();
        station.setNom("MT1");
        station.setCodePays("FR");
        station.setCreateur(defaultAdmin);
        stationService.createStation(station);

        // campagne
        Campagne campagne = new Campagne();
        campagne.setNom("BSM-PF1");
        campagne.setDateDeb(new Date());
        campagne.setDateFin(new Date());
        campagne.setCreateur(defaultAdmin);
        campagne.setCodePays("FR");
        campagneService.createCampagne(campagne);

        // create some specimens
        Specimen specimen1 = new Specimen();
        specimen1.setRef("P175");
        specimen1.setEmbranchement("Porifera");
        specimen1.setTypeOrganisme(TypeOrganisme.PLANTE);
        specimen1.setCreateur(defaultAdmin);
        specimenService.createSpecimen(specimen1);

        // create some lots
        Lot lot1 = new Lot();
        lot1.setRef("P164-MHO4");
        lot1.setStation(station);
        lot1.setEchantillonPhylo(false);
        lot1.setEchantillonColl(false);
        lot1.setEchantillonIdent(true);
        lot1.setCampagne(campagne);
        lot1.setDateRecolte(new Date());
        lot1.setSpecimenRef(specimen1);
        lot1.setCreateur(defaultAdmin);
        lotService.createLot(lot1);

        // type extrait
        TypeExtrait typeExtrait1 = new TypeExtrait();
        typeExtrait1.setDescription("Type extrait");
        typeExtrait1.setInitiales("TE");

        // methode extraction
        MethodeExtraction methodeExtraction1 = new MethodeExtraction();
        typeExtrait1.setMethodeExtraction(methodeExtraction1);
        methodeExtraction1.setNom("MET-01");
        methodeExtraction1.setDescription("Desc");
        methodeExtraction1.setTypesEnSortie(Collections.singletonList(typeExtrait1));
        extractionService.createMethodeExtraction(methodeExtraction1);

        // produit
        Extrait extrait1 = new Extrait();
        extrait1.setRef("P175-MT1D");
        extrait1.setTypeExtrait(typeExtrait1);
        Extraction extraction1 = new Extraction();
        extraction1.setRef("EXT-01");
        extraction1.setExtraits(Collections.singletonList(extrait1));
        extrait1.setExtraction(extraction1);
        extraction1.setDate(new Date());
        extraction1.setCreateur(defaultAdmin);
        extraction1.setManipulateur(defaultAdmin);
        extraction1.setLot(lot1);
        extraction1.setMethode(methodeExtraction1);
        extractionService.createExtraction(extraction1);

        // methode
        MethodeTestBio methodeTestBio1 = new MethodeTestBio();
        methodeTestBio1.setNom("Test method");
        methodeTestBio1.setCible("KB");
        methodeTestBio1.setValeurMesuree("Temparature");
        methodeTestBio1.setDomaine("domain");
        methodeTestBio1.setDescription("Temperature effective");
        methodeTestBio1.setUniteResultat("°C");
        testBioService.createMethodeTestBio(methodeTestBio1);

        // test bio
        TestBio testBio1 = new TestBio();
        testBio1.setRef("TC-T2");
        testBio1.setMethode(methodeTestBio1);
        testBio1.setCreateur(defaultAdmin);
        testBio1.setManipulateur(defaultAdmin);
        testBio1.setDate(new Date());
        testBio1.setOrganismeTesteur("IRD");

        // resultatTestBio
        ResultatTestBio resultatTestBio1 = new ResultatTestBio();
        resultatTestBio1.setRepere("T2-E2");
        resultatTestBio1.setTypeResultat(TypeResultat.PRODUIT);
        resultatTestBio1.setActif(true);
        resultatTestBio1.setTestBio(testBio1);
        resultatTestBio1.setProduit(extrait1);
        testBio1.setResultats(Collections.singletonList(resultatTestBio1));
        testBioService.createTestBio(testBio1);

        Assert.assertEquals(1, testBioService.countResultatsTestsBio()); // force sync

        // test search resultatbio
        List<ResultatTestBio> resultatTestBios = searchService.search(new SearchBean("BSM-PF1"), defaultAdmin)
                .getResultatTestBios();
        Assert.assertFalse(resultatTestBios.isEmpty());
    }
}
