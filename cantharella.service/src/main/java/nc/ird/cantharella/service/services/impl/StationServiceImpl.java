/*
 * #%L
 * Cantharella :: Service
 * $Id: StationServiceImpl.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.service/src/main/java/nc/ird/cantharella/service/services/impl/StationServiceImpl.java $
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
import nc.ird.cantharella.data.dao.impl.StationDao;
import nc.ird.cantharella.data.exceptions.DataConstraintException;
import nc.ird.cantharella.data.exceptions.DataNotFoundException;
import nc.ird.cantharella.data.exceptions.UnexpectedException;
import nc.ird.cantharella.data.model.Campagne;
import nc.ird.cantharella.data.model.Lot;
import nc.ird.cantharella.data.model.Station;
import nc.ird.cantharella.data.model.Utilisateur;
import nc.ird.cantharella.data.model.Utilisateur.TypeDroit;
import nc.ird.cantharella.service.services.StationService;
import nc.ird.cantharella.utils.AssertTools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service station
 * 
 * @author Mickael Tricot
 * @author Adrien Cheype
 */
@Service
public final class StationServiceImpl implements StationService {

    /** Logger */
    private static final Logger LOG = LoggerFactory.getLogger(StationServiceImpl.class);

    /** DAO */
    @Autowired
    private GenericDao dao;

    /** {@inheritDoc} */
    @Override
    public long countStations() {
        return dao.count(Station.class);
    }

    /** {@inheritDoc} */
    @Override
    public void createStation(Station station) throws DataConstraintException {
        LOG.info("createStation " + station.getNom());
        dao.create(station);
    }

    /** {@inheritDoc} */
    @Override
    public void deleteStation(Station station) throws DataConstraintException {
        LOG.info("deleteStation " + station.getIdStation());
        try {
            dao.delete(station);
        } catch (DataNotFoundException e) {
            LOG.error(e.getMessage(), e);
            throw new UnexpectedException(e);
        }
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    @Override
    public List<String> listStationLocalites() {
        return (List<String>) dao.list(StationDao.CRITERIA_DISTINCT_STATION_LOCALITES);
    }

    /** {@inheritDoc} */
    @Override
    public List<Station> listStations(Utilisateur utilisateur) {
        AssertTools.assertNotNull(utilisateur);
        if (utilisateur.getTypeDroit() == TypeDroit.ADMINISTRATEUR) {
            return dao.readList(Station.class, "nom");
        }
        SortedSet<Station> stations = listStationsForUser(utilisateur);

        return new ArrayList<Station>(stations);
    }

    /** {@inheritDoc} */
    @Override
    public SortedSet<Station> listStationsForUser(Utilisateur utilisateur) {
        // gestion des droits en plus pour les utilisateurs
        SortedSet<Station> stations = new TreeSet<Station>();

        // droits donnés pour les stations crées par l'utilisateur
        stations.addAll(utilisateur.getStationsCrees());

        // droits donnés pour l'ensemble des stations prospectées des campagnes où l'utilisateur a les droits sur la
        // campagne complète
        List<Campagne> campagnes = new ArrayList<Campagne>();
        campagnes.addAll(utilisateur.getCampagnesCreees());
        campagnes.addAll(utilisateur.getCampagnesDroits().keySet());
        for (Campagne c : campagnes) {
            stations.addAll(c.getStations());
        }
        // droits donnés également pour chaque lot qui a des droits particuliers
        List<Lot> lots = new ArrayList<Lot>();
        lots.addAll(utilisateur.getLotsCrees());
        lots.addAll(utilisateur.getLotsDroits().keySet());
        for (Lot l : lots) {
            // droit sur la station du lot
            stations.add(l.getStation());
            // droit sur la station de provenance du spécimen rattaché
            if (l.getSpecimenRef().getStation() != null) {
                stations.add(l.getSpecimenRef().getStation());
            }
        }
        return stations;
    }

    /** {@inheritDoc} */
    @Override
    public Station loadStation(Integer idStation) throws DataNotFoundException {
        AssertTools.assertNotNull(idStation);
        return dao.read(Station.class, idStation);
    }

    /** {@inheritDoc} */
    @Override
    public Station loadStation(String nom) throws DataNotFoundException {
        AssertTools.assertNotEmpty(nom);
        return dao.read(Station.class, "nom", nom);
    }

    /** {@inheritDoc} */
    @Override
    public boolean updateOrdeleteStationEnabled(Station station, Utilisateur utilisateur) {
        return utilisateur.getTypeDroit() == TypeDroit.ADMINISTRATEUR
                || utilisateur.getIdPersonne() == station.getCreateur().getIdPersonne();
    }

    /** {@inheritDoc} */
    @Override
    public void updateStation(Station station) throws DataConstraintException {
        LOG.info("updateStation " + station.getNom());
        try {
            dao.update(station);
        } catch (DataNotFoundException e) {
            LOG.error(e.getMessage(), e);
            throw new UnexpectedException(e);
        }
    }
}
