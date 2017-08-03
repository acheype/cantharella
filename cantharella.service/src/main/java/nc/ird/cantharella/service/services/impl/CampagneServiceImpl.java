/*
 * #%L
 * Cantharella :: Service
 * $Id: CampagneServiceImpl.java 268 2014-05-06 15:45:40Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.service/src/main/java/nc/ird/cantharella/service/services/impl/CampagneServiceImpl.java $
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
import nc.ird.cantharella.data.dao.impl.CampagneDao;
import nc.ird.cantharella.data.exceptions.DataConstraintException;
import nc.ird.cantharella.data.exceptions.DataNotFoundException;
import nc.ird.cantharella.data.exceptions.UnexpectedException;
import nc.ird.cantharella.data.model.Campagne;
import nc.ird.cantharella.data.model.Lot;
import nc.ird.cantharella.data.model.Utilisateur;
import nc.ird.cantharella.data.model.Utilisateur.TypeDroit;
import nc.ird.cantharella.service.services.CampagneService;
import nc.ird.cantharella.utils.AssertTools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Implémentation du service campagne
 * 
 * @author Mickael Tricot
 * @author Adrien Cheype
 */
@Service
public final class CampagneServiceImpl implements CampagneService {

    /** Logger */
    private static final Logger LOG = LoggerFactory.getLogger(CampagneServiceImpl.class);

    /** Accès aux données */
    @Autowired
    private GenericDao dao;

    /** {@inheritDoc} */
    @Override
    public long countCampagnes() {
        return dao.count(Campagne.class);
    }

    /** {@inheritDoc} */
    @Override
    public void createCampagne(Campagne campagne) throws DataConstraintException {
        LOG.info("createCampagne " + campagne.getNom());
        dao.create(campagne);
    }

    /** {@inheritDoc} */
    @Override
    public void deleteCampagne(Campagne campagne) throws DataConstraintException {
        AssertTools.assertNotNull(campagne);
        LOG.info("deleteCampagne " + campagne.getIdCampagne());
        try {
            dao.delete(campagne);
        } catch (DataNotFoundException e) {
            LOG.error(e.getMessage(), e);
            throw new UnexpectedException(e);
        }
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    @Override
    public List<String> listCampagneProgrammes() {
        return (List<String>) dao.list(CampagneDao.CRITERIA_DISTINCT_CAMPAGNE_PROGRAMMES);
    }

    /** {@inheritDoc} */
    @Override
    public List<Campagne> listCampagnes(Utilisateur utilisateur) {
        AssertTools.assertNotNull(utilisateur);
        if (utilisateur.getTypeDroit() == TypeDroit.ADMINISTRATEUR) {
            return dao.readList(Campagne.class, "nom");
        }
        SortedSet<Campagne> campagnes = listCampagnesForUser(utilisateur);

        return new ArrayList<Campagne>(campagnes);
    }

    /** {@inheritDoc} */
    @Override
    public SortedSet<Campagne> listCampagnesForUser(Utilisateur utilisateur) {
        // gestion des droits en plus pour les utilisateurs
        SortedSet<Campagne> campagnes = new TreeSet<Campagne>();
        campagnes.addAll(utilisateur.getCampagnesCreees());
        campagnes.addAll(utilisateur.getCampagnesDroits().keySet());

        // accès aux campagnes des lots pour lesquels l'utilisateur a les droits
        for (Lot lot : utilisateur.getLotsCrees()) {
            campagnes.add(lot.getCampagne());
        }
        for (Lot lot : utilisateur.getLotsDroits().keySet()) {
            campagnes.add(lot.getCampagne());
        }
        return campagnes;
    }

    /** {@inheritDoc} */
    @Override
    public Campagne loadCampagne(Integer idCampagne) throws DataNotFoundException {
        AssertTools.assertNotNull(idCampagne);
        return dao.read(Campagne.class, idCampagne);
    }

    /** {@inheritDoc} */
    @Override
    public Campagne loadCampagne(String nom) throws DataNotFoundException {
        AssertTools.assertNotEmpty(nom);
        return dao.read(Campagne.class, "nom", nom);
    }

    /** {@inheritDoc} */
    @Override
    public void refreshCampagne(Campagne campagne) {
        AssertTools.assertNotNull(campagne);
        dao.refresh(campagne);
    }

    /** {@inheritDoc} */
    @Override
    public void updateCampagne(Campagne campagne) throws DataConstraintException {
        LOG.info("updateCampagne " + campagne.getIdCampagne());
        try {
            dao.update(campagne);
        } catch (DataNotFoundException e) {
            LOG.error(e.getMessage(), e);
            throw new UnexpectedException(e);
        }
    }

    /** {@inheritDoc} */
    @Override
    public boolean updateOrdeleteCampagneEnabled(Campagne campagne, Utilisateur utilisateur) {
        return utilisateur.getTypeDroit() == TypeDroit.ADMINISTRATEUR
                || utilisateur.getIdPersonne() == campagne.getCreateur().getIdPersonne();
    }
}
