/*
 * #%L
 * Cantharella :: Service
 * $Id: SpecimenServiceImpl.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.service/src/main/java/nc/ird/cantharella/service/services/impl/SpecimenServiceImpl.java $
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
import nc.ird.cantharella.data.dao.impl.SpecimenDao;
import nc.ird.cantharella.data.exceptions.DataConstraintException;
import nc.ird.cantharella.data.exceptions.DataNotFoundException;
import nc.ird.cantharella.data.exceptions.UnexpectedException;
import nc.ird.cantharella.data.model.Campagne;
import nc.ird.cantharella.data.model.Lot;
import nc.ird.cantharella.data.model.Specimen;
import nc.ird.cantharella.data.model.Station;
import nc.ird.cantharella.data.model.Utilisateur;
import nc.ird.cantharella.data.model.Utilisateur.TypeDroit;
import nc.ird.cantharella.service.services.SpecimenService;
import nc.ird.cantharella.utils.AssertTools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Implémentation du service spécimens
 * 
 * @author Mickael Tricot
 * @author Adrien Cheype
 */
@Service
public final class SpecimenServiceImpl implements SpecimenService {

    /** Logger */
    private static final Logger LOG = LoggerFactory.getLogger(SpecimenServiceImpl.class);

    /** Accès aux données */
    @Autowired
    private GenericDao dao;

    /** {@inheritDoc} */
    @Override
    public long countSpecimens() {
        return dao.count(Specimen.class);
    }

    /** {@inheritDoc} */
    @Override
    public void createSpecimen(Specimen specimen) throws DataConstraintException {
        LOG.info("createSpecimen: " + specimen.getRef());
        dao.create(specimen);
    }

    /** {@inheritDoc} */
    @Override
    public void deleteSpecimen(Specimen specimen) throws DataConstraintException {
        AssertTools.assertNotNull(specimen);
        LOG.info("deleteSpecimen " + specimen.getRef());
        try {
            dao.delete(specimen);
        } catch (DataNotFoundException e) {
            LOG.error(e.getMessage(), e);
            throw new UnexpectedException(e);
        }
    }

    /** {@inheritDoc} */
    @Override
    public List<Specimen> listSpecimens(Utilisateur utilisateur) {
        AssertTools.assertNotNull(utilisateur);
        if (utilisateur.getTypeDroit() == TypeDroit.ADMINISTRATEUR) {
            return dao.readList(Specimen.class, "ref");
        }
        SortedSet<Specimen> specimens = listSpecimensForUser(utilisateur);

        return new ArrayList<Specimen>(specimens);
    }

    /** {@inheritDoc} */
    @Override
    public SortedSet<Specimen> listSpecimensForUser(Utilisateur utilisateur) {
        // gestion des droits en plus pour les utilisateurs
        SortedSet<Specimen> specimens = new TreeSet<Specimen>();

        // droits donnés pour les spécimens créés par l'utilisateur
        specimens.addAll(utilisateur.getSpecimensCrees());

        // droits donnés pour l'ensemble des spécimens qui a pour station une station prospectée par les campagnes dont
        // l'utilisateur a les droits
        List<Campagne> campagnes = new ArrayList<Campagne>();
        campagnes.addAll(utilisateur.getCampagnesCreees());
        campagnes.addAll(utilisateur.getCampagnesDroits().keySet());
        for (Campagne c : campagnes) {
            for (Station st : c.getStations()) {
                specimens.addAll(st.getSpecimensRattaches());
            }
        }
        // droits donnés également pour le spécimen de référence de chaque lot dont l'utilisateur a les droits
        List<Lot> lots = new ArrayList<Lot>();
        lots.addAll(utilisateur.getLotsCrees());
        lots.addAll(utilisateur.getLotsDroits().keySet());
        for (Lot l : lots) {
            // droit sur le spécimen de référence du lot
            if (l.getSpecimenRef().getStation() != null) {
                specimens.add(l.getSpecimenRef());
            }
        }
        return specimens;
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    @Override
    public List<String> listSpecimenEmbranchements() {
        return (List<String>) dao.list(SpecimenDao.CRITERIA_DISTINCT_SPECIMEN_EMBRANCHEMENTS);
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    @Override
    public List<String> listSpecimenFamilles() {
        return (List<String>) dao.list(SpecimenDao.CRITERIA_DISTINCT_SPECIMEN_FAMILLES);
    }

    /** {@inheritDoc} */
    @Override
    @SuppressWarnings("unchecked")
    public List<String> listSpecimenGenres() {
        return (List<String>) dao.list(SpecimenDao.CRITERIA_DISTINCT_SPECIMEN_GENRES);
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    @Override
    public List<String> listSpecimenEspeces() {
        return (List<String>) dao.list(SpecimenDao.CRITERIA_DISTINCT_SPECIMEN_ESPECES);
    }

    /** {@inheritDoc} */
    @Override
    @SuppressWarnings("unchecked")
    public List<String> listSpecimenSousEspeces() {
        return (List<String>) dao.list(SpecimenDao.CRITERIA_DISTINCT_SPECIMEN_SOUSESPECES);
    }

    /** {@inheritDoc} */
    @Override
    @SuppressWarnings("unchecked")
    public List<String> listSpecimenVarietes() {
        return (List<String>) dao.list(SpecimenDao.CRITERIA_DISTINCT_SPECIMEN_VARIETES);
    }

    /** {@inheritDoc} */
    @Override
    @SuppressWarnings("unchecked")
    public List<String> listLieuxDepot() {
        return (List<String>) dao.list(SpecimenDao.CRITERIA_DISTINCT_LIEUX_DEPOT);
    }

    /** {@inheritDoc} */
    @Override
    public Specimen loadSpecimen(Integer idSpecimen) throws DataNotFoundException {
        AssertTools.assertNotNull(idSpecimen);
        return dao.read(Specimen.class, idSpecimen);
    }

    /** {@inheritDoc} */
    @Override
    public Specimen loadSpecimen(String ref) throws DataNotFoundException {
        AssertTools.assertNotEmpty(ref);
        return dao.read(Specimen.class, "ref", ref);
    }

    /** {@inheritDoc} */
    @Override
    public boolean updateOrdeleteSpecimenEnabled(Specimen specimen, Utilisateur utilisateur) {
        return utilisateur.getTypeDroit() == TypeDroit.ADMINISTRATEUR
                || utilisateur.getIdPersonne() == specimen.getCreateur().getIdPersonne();
    }

    /** {@inheritDoc} */
    @Override
    public void updateSpecimen(Specimen specimen) throws DataConstraintException {
        LOG.info("updateSpecimen " + specimen.getIdSpecimen());
        try {
            dao.update(specimen);
        } catch (DataNotFoundException e) {
            LOG.error(e.getMessage(), e);
            throw new UnexpectedException(e);
        }
    }

}
