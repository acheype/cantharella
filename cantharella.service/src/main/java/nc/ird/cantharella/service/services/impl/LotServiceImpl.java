/*
 * #%L
 * Cantharella :: Service
 * $Id: LotServiceImpl.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.service/src/main/java/nc/ird/cantharella/service/services/impl/LotServiceImpl.java $
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

import javax.annotation.Resource;

import nc.ird.cantharella.data.dao.GenericDao;
import nc.ird.cantharella.data.exceptions.DataConstraintException;
import nc.ird.cantharella.data.exceptions.DataNotFoundException;
import nc.ird.cantharella.data.exceptions.UnexpectedException;
import nc.ird.cantharella.data.model.Campagne;
import nc.ird.cantharella.data.model.Lot;
import nc.ird.cantharella.data.model.Partie;
import nc.ird.cantharella.data.model.Utilisateur;
import nc.ird.cantharella.data.model.Utilisateur.TypeDroit;
import nc.ird.cantharella.service.services.LotService;
import nc.ird.cantharella.utils.AssertTools;
import nc.ird.cantharella.utils.BeanTools;
import nc.ird.cantharella.utils.CollectionTools;

import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Implémentation du service lot
 * 
 * @author Mickael Tricot
 * @author Adrien Cheype
 */
@Service
public final class LotServiceImpl implements LotService {

    /** Logger */
    private static final Logger LOG = LoggerFactory.getLogger(LotServiceImpl.class);

    /** DAO */
    @Autowired
    private GenericDao dao;

    /** Hibernate session factory. */
    @Resource
    private SessionFactory sessionFactory;

    /** {@inheritDoc} */
    @Override
    public long countLots() {
        return dao.count(Lot.class);
    }

    /** {@inheritDoc} */
    @Override
    public void refreshLot(Lot lot) {
        AssertTools.assertNotNull(lot);
        dao.refresh(lot);
    }

    /** {@inheritDoc} */
    @Override
    public void createLot(Lot lot) throws DataConstraintException {
        LOG.info("createLot " + lot.getRef());
        dao.create(lot);
    }

    /** {@inheritDoc} */
    @Override
    public void createPartie(Partie partie) throws DataConstraintException {
        LOG.info("createPartie: " + partie.getNom());
        dao.create(partie);
    }

    /** {@inheritDoc} */
    @Override
    public void deleteLot(Lot lot) throws DataConstraintException {
        AssertTools.assertNotNull(lot);
        LOG.info("deleteLot " + lot.getRef());
        try {
            dao.delete(lot);
        } catch (DataNotFoundException e) {
            LOG.error(e.getMessage(), e);
            throw new UnexpectedException(e);
        }

    }

    /** {@inheritDoc} */
    @Override
    public void deletePartie(Partie partie) throws DataConstraintException {
        AssertTools.assertNotNull(partie);
        LOG.info("deletePartie: " + partie.getNom());
        try {
            dao.delete(partie);
        } catch (DataNotFoundException e) {
            LOG.error(e.getMessage(), e);
            throw new UnexpectedException(e);
        }
    }

    /** {@inheritDoc} */
    @Override
    public List<Lot> listLots(Utilisateur utilisateur) {
        AssertTools.assertNotNull(utilisateur);
        if (utilisateur.getTypeDroit() == TypeDroit.ADMINISTRATEUR) {
            return dao.readList(Lot.class, "ref");
        }
        // gestion des droits en plus pour les utilisateurs
        return new ArrayList<Lot>(listLotsForUser(utilisateur));
    }

    /** {@inheritDoc} */
    @Override
    public SortedSet<Lot> listLotsForUser(Utilisateur utilisateur) {
        AssertTools.assertNotNull(utilisateur);
        SortedSet<Lot> lots = new TreeSet<Lot>();
        for (Campagne c : utilisateur.getCampagnesCreees()) {
            lots.addAll(c.getLots());
        }
        for (Campagne c : utilisateur.getCampagnesDroits().keySet()) {
            lots.addAll(c.getLots());
        }
        lots.addAll(utilisateur.getLotsCrees());
        lots.addAll(utilisateur.getLotsDroits().keySet());
        return lots;
    }

    /** {@inheritDoc} */
    @Override
    public boolean updateOrdeleteLotEnabled(Lot lot, Utilisateur utilisateur) {
        return utilisateur.getTypeDroit() == TypeDroit.ADMINISTRATEUR
                || utilisateur.getIdPersonne() == lot.getCreateur().getIdPersonne();
    }

    /** {@inheritDoc} */
    @Override
    public boolean isLotAccessibleByUser(Lot lot, Utilisateur utilisateur) {
        // si administrateur ou créateur, accès ok
        if (utilisateur.getTypeDroit() == TypeDroit.ADMINISTRATEUR || utilisateur.equals(lot.getCreateur())) {
            return true;
        }
        // accessible si l'utilisateur
        if (utilisateur.getCampagnesCreees().contains(lot.getCampagne())) {
            return true;
        }

        // accessible si l'utilisateur a le droit à la campagne
        // FIXME echatellier 20130502 ne fonctionne pas car les clés composites
        // ne sont pas les mêmes instances que les clés
        /*if (utilisateur.getCampagnesDroits().containsKey(lot.getCampagne())) {
            return true;
        }
        // accessible si l'utilisateur a le droit au lot
        if (utilisateur.getLotsDroits().containsKey(lot)) {
            return true;
        }*/

        // FIXME echatellier 20130502 code temporaire pour pallier au problème
        // des clés composites
        if (CollectionTools.containsWithValue(utilisateur.getCampagnesDroits().keySet(), "idCampagne",
                BeanTools.AccessType.GETTER, lot.getCampagne().getIdCampagne())) {
            return true;
        }
        if (CollectionTools.containsWithValue(utilisateur.getLotsDroits().keySet(), "idLot",
                BeanTools.AccessType.GETTER, lot.getIdLot())) {
            return true;
        }

        // pas d'accès sinon
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public List<Partie> listParties() {
        return dao.readList(Partie.class, "nom");
    }

    /** {@inheritDoc} */
    @Override
    public Lot loadLot(Integer idLot) throws DataNotFoundException {
        return dao.read(Lot.class, idLot);
    }

    /** {@inheritDoc} */
    @Override
    public Lot loadLot(String ref) throws DataNotFoundException {
        AssertTools.assertNotEmpty(ref);
        return dao.read(Lot.class, "ref", ref);
    }

    /** {@inheritDoc} */
    @Override
    public Partie loadPartie(Integer idPartie) throws DataNotFoundException {
        return dao.read(Partie.class, idPartie);
    }

    /** {@inheritDoc} */
    @Override
    public Partie loadPartie(String nom) throws DataNotFoundException {
        return dao.read(Partie.class, "nom", nom);
    }

    /** {@inheritDoc} */
    @Override
    public void updateLot(Lot lot) throws DataConstraintException {
        LOG.info("updateLot " + lot.getRef());
        try {
            dao.update(lot);
        } catch (DataNotFoundException e) {
            LOG.error(e.getMessage(), e);
            throw new UnexpectedException(e);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void updatePartie(Partie partie) throws DataConstraintException {
        LOG.info("updatePartie: " + partie.getNom());
        try {
            dao.update(partie);
        } catch (DataNotFoundException e) {
            LOG.error(e.getMessage(), e);
            throw new UnexpectedException(e);
        }
    }
}
