/*
 * #%L
 * Cantharella :: Service
 * $Id: StationService.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.service/src/main/java/nc/ird/cantharella/service/services/StationService.java $
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
package nc.ird.cantharella.service.services;

import java.util.List;
import java.util.SortedSet;

import nc.ird.cantharella.data.exceptions.DataConstraintException;
import nc.ird.cantharella.data.exceptions.DataNotFoundException;
import nc.ird.cantharella.data.model.Station;
import nc.ird.cantharella.data.model.Utilisateur;
import nc.ird.cantharella.service.utils.normalizers.StationNormalizer;
import nc.ird.cantharella.service.utils.normalizers.UniqueFieldNormalizer;
import nc.ird.cantharella.service.utils.normalizers.utils.Normalize;

import org.springframework.transaction.annotation.Transactional;

/**
 * Service : station
 * 
 * @author Mickael Tricot
 * @author Adrien Cheype
 */
public interface StationService {

    /**
     * Compte le nombre de stations
     * 
     * @return Nombre de stations
     */
    long countStations();

    /**
     * Créée une station
     * 
     * @param station Station
     * @throws DataConstraintException Si la station existe déjà
     */
    void createStation(@Normalize(StationNormalizer.class) Station station) throws DataConstraintException;

    /**
     * Supprime une station
     * 
     * @param station Station
     * @throws DataConstraintException Si des données liées empêchent la suppression
     */
    void deleteStation(Station station) throws DataConstraintException;

    /**
     * Liste les localités des stations
     * 
     * @return Localités
     */
    @Transactional(readOnly = true)
    List<String> listStationLocalites();

    /**
     * Liste les stations selon les droits d'un utilisateur (triés par nom)
     * 
     * @param utilisateur L'utilisateur
     * @return Stations
     */
    @Transactional(readOnly = true)
    List<Station> listStations(Utilisateur utilisateur);

    /**
     * Liste l'ensemble des stations selon les droits d'un utilisateur (triés par nom)
     * 
     * @param utilisateur L'utilisateur
     * @return la liste des stations
     */
    SortedSet<Station> listStationsForUser(Utilisateur utilisateur);

    /**
     * Charge une station
     * 
     * @param idStation ID
     * @return Station
     * @throws DataNotFoundException Si la station n'existe pas
     */
    Station loadStation(Integer idStation) throws DataNotFoundException;

    /**
     * Charge une station
     * 
     * @param nom Nom
     * @return Station
     * @throws DataNotFoundException Si la station n'existe pas
     */
    Station loadStation(@Normalize(UniqueFieldNormalizer.class) String nom) throws DataNotFoundException;

    /**
     * Détermine si un utilisateur peut modifier ou supprimer une station
     * 
     * @param station Station
     * @param utilisateur Utilisateur
     * @return TRUE si il a le droit
     */
    boolean updateOrdeleteStationEnabled(Station station, Utilisateur utilisateur);

    /**
     * Met à jour une station
     * 
     * @param station Station
     * @throws DataConstraintException Si la station existe déjà
     */
    void updateStation(@Normalize(StationNormalizer.class) Station station) throws DataConstraintException;
}
