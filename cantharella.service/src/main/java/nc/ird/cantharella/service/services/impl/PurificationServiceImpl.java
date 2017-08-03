/*
 * #%L
 * Cantharella :: Service
 * $Id: PurificationServiceImpl.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.service/src/main/java/nc/ird/cantharella/service/services/impl/PurificationServiceImpl.java $
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
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import nc.ird.cantharella.data.dao.GenericDao;
import nc.ird.cantharella.data.dao.impl.PurificationDao;
import nc.ird.cantharella.data.exceptions.DataConstraintException;
import nc.ird.cantharella.data.exceptions.DataNotFoundException;
import nc.ird.cantharella.data.exceptions.UnexpectedException;
import nc.ird.cantharella.data.model.Extraction;
import nc.ird.cantharella.data.model.Extrait;
import nc.ird.cantharella.data.model.Fraction;
import nc.ird.cantharella.data.model.MethodePurification;
import nc.ird.cantharella.data.model.ParamMethoPuri;
import nc.ird.cantharella.data.model.ParamMethoPuriEffectif;
import nc.ird.cantharella.data.model.Purification;
import nc.ird.cantharella.data.model.Utilisateur;
import nc.ird.cantharella.data.model.Utilisateur.TypeDroit;
import nc.ird.cantharella.service.services.ExtractionService;
import nc.ird.cantharella.service.services.PurificationService;
import nc.ird.cantharella.utils.AssertTools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Implémentation du service purification
 * 
 * @author Adrien Cheype
 */
@Service
public final class PurificationServiceImpl implements PurificationService {

    /** Logger */
    private static final Logger LOG = LoggerFactory.getLogger(PurificationServiceImpl.class);

    /** DAO */
    @Autowired
    private GenericDao dao;

    /** Service : extractions */
    @Autowired
    private ExtractionService extractionService;

    /** {@inheritDoc} */
    @Override
    public long countPurifications() {
        return dao.count(Purification.class);
    }

    /** {@inheritDoc} */
    @Override
    public void createPurification(Purification purification) throws DataConstraintException {
        LOG.info("createPurification: " + purification.getRef());
        dao.create(purification);
    }

    /** {@inheritDoc} */
    @Override
    public void initParamsMethoPuriEffectif(Purification purification) {
        AssertTools.assertNotNull(purification);
        if (purification.getMethode() != null) {
            // rafraichi la méthode pour pouvoir accéder aux paramètres accédés en LAZY
            refreshMethodePurification(purification.getMethode());

            refreshMethodePurification(purification.getMethode());
            purification.getParamsMetho().clear();
            for (int paramInd = 0; paramInd < purification.getMethode().getParametres().size(); paramInd++) {
                ParamMethoPuri paramMetho = purification.getMethode().getParametres().get(paramInd);

                ParamMethoPuriEffectif effectifParam = new ParamMethoPuriEffectif();
                effectifParam.setParam(paramMetho);
                effectifParam.setPurification(purification);

                // effectifParam.valeur is null
                purification.getParamsMetho().add(effectifParam);
                // LOG.debug("ajout param : " + paramMetho.getNom());
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public void deletePurification(Purification purification) throws DataConstraintException {
        AssertTools.assertNotNull(purification);
        LOG.info("deletePurification: " + purification.getRef());
        try {
            dao.delete(purification);
        } catch (DataNotFoundException e) {
            LOG.error(e.getMessage(), e);
            throw new UnexpectedException(e);
        }

    }

    /** {@inheritDoc} */
    @Override
    public List<Purification> listPurifications(Utilisateur utilisateur) {
        AssertTools.assertNotNull(utilisateur);
        if (utilisateur.getTypeDroit() == TypeDroit.ADMINISTRATEUR) {
            // si admin, on ajoute toutes les purifications de la base
            return dao.readList(Purification.class, "produit", "ref");
        }
        // gestion des droits en plus pour les utilisateurs
        return new ArrayList<Purification>(listPurificationsForUser(utilisateur));
    }

    /** {@inheritDoc} */
    @Override
    public SortedSet<Purification> listPurificationsForUser(Utilisateur utilisateur) {
        SortedSet<Purification> purifications = new TreeSet<Purification>();
        purifications.addAll(utilisateur.getPurificationsCrees());
        addAllPurificationsOfExtractions(purifications, extractionService.listExtractionsForUser(utilisateur));

        return purifications;
    }

    /**
     * Ajoute tous les purifications contenues dans un ensemble d'extraction à un ensemble de purifications
     * 
     * @param purifications l'ensemble de purifications destination
     * @param extractions l'ensemble des extractions sources
     */
    private void addAllPurificationsOfExtractions(Set<Purification> purifications, Set<Extraction> extractions) {
        for (Extraction curExtraction : extractions) {
            for (Extrait curExtrait : curExtraction.getExtraits()) {
                for (Purification curPurification : curExtrait.getPurificationsSuivantes()) {
                    purifications.add(curPurification);

                    // version itérative (dérécursification)
                    LinkedList<Fraction> fractionsQueue = new LinkedList<Fraction>(curPurification.getFractions());
                    while (!fractionsQueue.isEmpty()) {
                        Fraction curFraction = fractionsQueue.remove();
                        for (Purification curPuriFromFraction : curFraction.getPurificationsSuivantes()) {
                            purifications.add(curPuriFromFraction);
                            fractionsQueue.addAll(curPuriFromFraction.getFractions());
                        }
                    }
                }
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public boolean isPurificationUnique(Purification purification) {
        AssertTools.assertNotNull(purification);

        // unique if it doesn't exist different value or if it exists but with the same id (so same row in the db)
        if (!dao.exists(Purification.class, "ref", purification.getRef())) {
            return true;
        }

        Purification puriWithSameVal;
        try {
            puriWithSameVal = dao.read(Purification.class, "ref", purification.getRef());
            dao.evict(puriWithSameVal);
        } catch (DataNotFoundException e) {
            return true; // never call, cover by dao.exists...
        }
        // in case of new record, id is null
        return purification.getIdPurification() != null
                && purification.getIdPurification().equals(puriWithSameVal.getIdPurification());
    }

    /** {@inheritDoc} */
    @Override
    public Purification loadPurification(Integer idPurification) throws DataNotFoundException {
        return dao.read(Purification.class, idPurification);
    }

    /** {@inheritDoc} */
    @Override
    public Purification loadPurification(String nom) throws DataNotFoundException {
        return dao.read(Purification.class, "nom", nom);
    }

    /** {@inheritDoc} */
    @Override
    public void updatePurification(Purification purification) throws DataConstraintException {
        LOG.info("updatePurification: " + purification.getRef());
        try {
            dao.update(purification);
        } catch (DataNotFoundException e) {
            LOG.error(e.getMessage(), e);
            throw new UnexpectedException(e);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void refreshPurification(Purification purification) {
        dao.refresh(purification);
    }

    /** {@inheritDoc} */
    @Override
    public boolean updateOrdeletePurificationEnabled(Purification purification, Utilisateur utilisateur) {
        return utilisateur.getTypeDroit() == TypeDroit.ADMINISTRATEUR
                || utilisateur.getIdPersonne() == purification.getCreateur().getIdPersonne();
    }

    /** {@inheritDoc} */
    @Override
    public void createMethodePurification(MethodePurification methode) throws DataConstraintException {
        LOG.info("createMethodePurification: " + methode.getNom());
        dao.create(methode);
    }

    /** {@inheritDoc} */
    @Override
    public void deleteMethodePurification(MethodePurification methode) throws DataConstraintException {
        AssertTools.assertNotNull(methode);
        LOG.info("deleteMethodePurification: " + methode.getNom());
        try {
            dao.delete(methode);
        } catch (DataNotFoundException e) {
            LOG.error(e.getMessage(), e);
            throw new UnexpectedException(e);
        }

    }

    /** {@inheritDoc} */
    @Override
    public List<MethodePurification> listMethodesPurification() {
        return dao.readList(MethodePurification.class, "nom");
    }

    /** {@inheritDoc} */
    @Override
    public MethodePurification loadMethodePurification(Integer idMethode) throws DataNotFoundException {
        return dao.read(MethodePurification.class, idMethode);
    }

    /** {@inheritDoc} */
    @Override
    public MethodePurification loadMethodePurification(String nom) throws DataNotFoundException {
        return dao.read(MethodePurification.class, "nom", nom);
    }

    /** {@inheritDoc} */
    @Override
    public void updateMethodePurification(MethodePurification methode) throws DataConstraintException {
        LOG.info("updateMethodePurification: " + methode.getNom());
        try {
            dao.update(methode);
        } catch (DataNotFoundException e) {
            LOG.error(e.getMessage(), e);
            throw new UnexpectedException(e);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void refreshMethodePurification(MethodePurification methode) {
        AssertTools.assertNotNull(methode);
        dao.refresh(methode);
    }

    /** {@inheritDoc} */
    @Override
    public boolean isParamMethoPuriReferenced(ParamMethoPuri paramMetho) {
        AssertTools.assertNotNull(paramMetho);
        AssertTools.assertNotNull(paramMetho.getIdParamMethoPuri());
        return dao.count(PurificationDao.COUNT_PURIF_WITH_PARAM_METHO, paramMetho.getIdParamMethoPuri()) > 0;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isFractionUnique(Fraction fraction) {
        AssertTools.assertNotNull(fraction);

        // unique if it doesn't exist different value or if it exists with the same id (so same row in the db)
        if (!dao.exists(Fraction.class, "ref", fraction.getRef())) {
            return true;
        }
        Fraction fractWithSameVal;
        try {
            fractWithSameVal = dao.read(Fraction.class, "ref", fraction.getRef());
            dao.evict(fractWithSameVal);
        } catch (DataNotFoundException e) {
            return true; // never call, covers by dao.exists...
        }
        // in case of new record, id is null
        return fraction.getId() != null && fraction.getId().equals(fractWithSameVal.getId());
    }

}