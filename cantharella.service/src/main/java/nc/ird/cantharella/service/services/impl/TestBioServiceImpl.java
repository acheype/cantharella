/*
 * #%L
 * Cantharella :: Service
 * $Id: TestBioServiceImpl.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.service/src/main/java/nc/ird/cantharella/service/services/impl/TestBioServiceImpl.java $
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
package nc.ird.cantharella.service.services.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import nc.ird.cantharella.data.dao.GenericDao;
import nc.ird.cantharella.data.dao.impl.TestBioDao;
import nc.ird.cantharella.data.exceptions.DataConstraintException;
import nc.ird.cantharella.data.exceptions.DataNotFoundException;
import nc.ird.cantharella.data.exceptions.UnexpectedException;
import nc.ird.cantharella.data.model.ErreurTestBio;
import nc.ird.cantharella.data.model.MethodeTestBio;
import nc.ird.cantharella.data.model.ResultatTestBio;
import nc.ird.cantharella.data.model.TestBio;
import nc.ird.cantharella.data.model.Utilisateur;
import nc.ird.cantharella.data.model.ResultatTestBio.TypeResultat;
import nc.ird.cantharella.data.model.Utilisateur.TypeDroit;
import nc.ird.cantharella.service.services.LotService;
import nc.ird.cantharella.service.services.TestBioService;
import nc.ird.cantharella.utils.AssertTools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Implémentation du service test
 * 
 * @author Adrien Cheype
 */
@Service
public final class TestBioServiceImpl implements TestBioService {

    /** Logger */
    private static final Logger LOG = LoggerFactory.getLogger(TestBioServiceImpl.class);

    /** Service : lots */
    @Autowired
    private LotService lotService;

    /** DAO */
    @Autowired
    private GenericDao dao;

    /** {@inheritDoc} */
    @Override
    public long countResultatsTestsBio() {
        return dao.count(TestBioDao.CRITERIA_COUNT_RESULTATS_TYPE_PRODUIT);
    }

    /** {@inheritDoc} */
    @Override
    public void createTestBio(TestBio testBio) throws DataConstraintException {
        LOG.info("createTestBio: " + testBio.getRef());
        dao.create(testBio);
    }

    /** {@inheritDoc} */
    @Override
    public void deleteTestBio(TestBio testBio) throws DataConstraintException {
        AssertTools.assertNotNull(testBio);
        LOG.info("deleteTestBio: " + testBio.getRef());
        try {
            dao.delete(testBio);
        } catch (DataNotFoundException e) {
            LOG.error(e.getMessage(), e);
            throw new UnexpectedException(e);
        }

    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    @Override
    public List<ResultatTestBio> listResultatsTestBio(Utilisateur utilisateur) {
        AssertTools.assertNotNull(utilisateur);
        if (utilisateur.getTypeDroit() == TypeDroit.ADMINISTRATEUR) {
            // si admin, on ajoute toutes les testBios de la base
            return (List<ResultatTestBio>) dao.list(TestBioDao.CRITERIA_LIST_RESULTATS_TYPE_PRODUIT);
        }
        // gestion des droits en plus pour les utilisateurs
        return new ArrayList<ResultatTestBio>(listResultatsTestBioForUser(utilisateur));
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    @Override
    public SortedSet<ResultatTestBio> listResultatsTestBioForUser(Utilisateur utilisateur) {
        SortedSet<ResultatTestBio> resultats = new TreeSet<ResultatTestBio>();

        // liste triée par produit afin d'optimiser
        List<ResultatTestBio> allResultTests = (List<ResultatTestBio>) dao
                .list(TestBioDao.CRITERIA_LIST_RESULTATS_TYPE_PRODUIT);
        for (ResultatTestBio curRes : allResultTests) {
            if (lotService.isLotAccessibleByUser(curRes.getLotSource(), utilisateur)) {
                resultats.add(curRes);
            }
        }

        return resultats;
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    @Override
    public List<String> listProduitsTemoins() {
        return (List<String>) dao.list(TestBioDao.CRITERIA_DISTINCT_PRODUITS_TEMOINS);
    }

    /** {@inheritDoc} */
    @Override
    public boolean isResultatTestBioAccessibleByUser(ResultatTestBio resultatTestBio, Utilisateur utilisateur) {
        return lotService.isLotAccessibleByUser(resultatTestBio.getLotSource(), utilisateur);
    }

    /** {@inheritDoc} */
    @Override
    public boolean isResultatTestBioUniqueInList(final ResultatTestBio resultatTestBio, final List<ResultatTestBio> list) {
        AssertTools.assertNotNull(resultatTestBio.getTypeResultat());
        AssertTools.assertNotNull(resultatTestBio.getRepere());
        // as resultatTestBio is already in list, detect if more of one elements satisfy these conditions
        int count = 0;
        for (ResultatTestBio curRes : list) {
            if (resultatTestBio.getTypeResultat() == TypeResultat.BLANC
                    && resultatTestBio.getRepere().equals(curRes.getRepere())) {
                count++;
            } else if (resultatTestBio.getTypeResultat() == TypeResultat.TEMOIN
                    && resultatTestBio.getRepere().equals(curRes.getRepere())
                    && resultatTestBio.getProduitTemoin().equals(curRes.getProduitTemoin())) {
                count++;
            } else if (resultatTestBio.getTypeResultat() == TypeResultat.PRODUIT
                    && resultatTestBio.getRepere().equals(curRes.getRepere())
                    && resultatTestBio.getProduit().getRef().equals(curRes.getProduit().getRef())) {
                count++;
            }
        }
        return count <= 1;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isTestBioUnique(TestBio testBio) {
        AssertTools.assertNotNull(testBio);

        // unique if it doesn't exist different value or if it exists but with the same id (so same row in the db)
        if (!dao.exists(TestBio.class, "ref", testBio.getRef())) {
            return true;
        }

        TestBio puriWithSameVal;
        try {
            puriWithSameVal = dao.read(TestBio.class, "ref", testBio.getRef());
            dao.evict(puriWithSameVal);
        } catch (DataNotFoundException e) {
            return true; // never call, cover by dao.exists...
        }
        // in case of new record, id is null
        return testBio.getIdTestBio() != null && testBio.getIdTestBio().equals(puriWithSameVal.getIdTestBio());
    }

    /** {@inheritDoc} */
    @Override
    public TestBio loadTestBio(Integer idTestBio) throws DataNotFoundException {
        return dao.read(TestBio.class, idTestBio);
    }

    /** {@inheritDoc} */
    @Override
    public TestBio loadTestBio(String nom) throws DataNotFoundException {
        return dao.read(TestBio.class, "nom", nom);
    }

    /** {@inheritDoc} */
    @Override
    public void updateTestBio(TestBio testBio) throws DataConstraintException {
        LOG.info("updateTestBio: " + testBio.getRef());
        try {
            dao.update(testBio);
        } catch (DataNotFoundException e) {
            LOG.error(e.getMessage(), e);
            throw new UnexpectedException(e);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void refreshTestBio(TestBio testBio) {
        dao.refresh(testBio);
    }

    /** {@inheritDoc} */
    @Override
    public boolean updateOrdeleteTestBioEnabled(TestBio testBio, Utilisateur utilisateur) {
        return utilisateur.getTypeDroit() == TypeDroit.ADMINISTRATEUR
                || utilisateur.getIdPersonne() == testBio.getCreateur().getIdPersonne();
    }

    /** {@inheritDoc} */
    @Override
    public void createMethodeTestBio(MethodeTestBio methode) throws DataConstraintException {
        LOG.info("createMethodeTest: " + methode.getNom());
        dao.create(methode);

    }

    /** {@inheritDoc} */
    @Override
    public void deleteMethodeTestBio(MethodeTestBio methode) throws DataConstraintException {
        AssertTools.assertNotNull(methode);
        LOG.info("deleteMethodeTest: " + methode.getNom());
        try {
            dao.delete(methode);
        } catch (DataNotFoundException e) {
            LOG.error(e.getMessage(), e);
            throw new UnexpectedException(e);
        }

    }

    /** {@inheritDoc} */
    @Override
    public List<MethodeTestBio> listMethodesTestBio() {
        return dao.readList(MethodeTestBio.class, "nom");
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    @Override
    public List<String> listDomainesMethodes() {
        return (List<String>) dao.list(TestBioDao.CRITERIA_DISTINCT_DOMAINES_METHODES);
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    @Override
    public List<String> listUnitesResultatMethodes() {
        return (List<String>) dao.list(TestBioDao.CRITERIA_DISTINCT_UNITES_RESULTAT_METHODES);
    }

    /** {@inheritDoc} */
    @Override
    public MethodeTestBio loadMethodeTest(Integer idMethode) throws DataNotFoundException {
        return dao.read(MethodeTestBio.class, idMethode);
    }

    /** {@inheritDoc} */
    @Override
    public MethodeTestBio loadMethodeTest(String nom) throws DataNotFoundException {
        return dao.read(MethodeTestBio.class, "nom", nom);
    }

    /** {@inheritDoc} */
    @Override
    public void updateMethodeTest(MethodeTestBio methode) throws DataConstraintException {
        LOG.info("updateMethodeTest: " + methode.getNom());
        try {
            dao.update(methode);
        } catch (DataNotFoundException e) {
            LOG.error(e.getMessage(), e);
            throw new UnexpectedException(e);
        }

    }

    /** {@inheritDoc} */
    @Override
    public void refreshMethodeTestBio(MethodeTestBio methode) {
        AssertTools.assertNotNull(methode);
        dao.refresh(methode);
    }

    /** {@inheritDoc} */
    @Override
    public void createErreurTest(ErreurTestBio erreurTest) throws DataConstraintException {
        LOG.info("createErreurTest: " + erreurTest.getNom());
        dao.create(erreurTest);
    }

    /** {@inheritDoc} */
    @Override
    public void deleteErreurTest(ErreurTestBio erreurTest) throws DataConstraintException {
        AssertTools.assertNotNull(erreurTest);
        LOG.info("deleteErreurTest: " + erreurTest.getNom());
        try {
            dao.delete(erreurTest);
        } catch (DataNotFoundException e) {
            LOG.error(e.getMessage(), e);
            throw new UnexpectedException(e);
        }
    }

    /** {@inheritDoc} */
    @Override
    public List<ErreurTestBio> listErreursTestBio() {
        return dao.readList(ErreurTestBio.class, "nom");
    }

    /** {@inheritDoc} */
    @Override
    public ErreurTestBio loadErreurTestBio(Integer idErreurTest) throws DataNotFoundException {
        return dao.read(ErreurTestBio.class, idErreurTest);
    }

    /** {@inheritDoc} */
    @Override
    public ErreurTestBio loadErreurTestBio(String nom) throws DataNotFoundException {
        return dao.read(ErreurTestBio.class, "nom", nom);
    }

    /** {@inheritDoc} */
    @Override
    public void updateErreurTestBio(ErreurTestBio erreurTest) throws DataConstraintException {
        LOG.info("updateErreurTest: " + erreurTest.getNom());
        try {
            dao.update(erreurTest);
        } catch (DataNotFoundException e) {
            LOG.error(e.getMessage(), e);
            throw new UnexpectedException(e);
        }
    }

}