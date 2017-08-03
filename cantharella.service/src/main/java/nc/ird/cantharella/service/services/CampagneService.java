/*
 * #%L
 * Cantharella :: Service
 * $Id: CampagneService.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.service/src/main/java/nc/ird/cantharella/service/services/CampagneService.java $
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
import nc.ird.cantharella.data.model.Campagne;
import nc.ird.cantharella.data.model.Utilisateur;
import nc.ird.cantharella.service.utils.normalizers.CampagneNormalizer;
import nc.ird.cantharella.service.utils.normalizers.UniqueFieldNormalizer;
import nc.ird.cantharella.service.utils.normalizers.utils.Normalize;

import org.springframework.transaction.annotation.Transactional;

/**
 * Service : campagnes
 * 
 * @author Mickael Tricot
 * @author Adrien Cheype
 */
public interface CampagneService {

    /**
     * Compte le nombre de campagnes
     * 
     * @return Nombre de campagnes
     */
    long countCampagnes();

    /**
     * Créé une campagne
     * 
     * @param campagne Campagne
     * @throws DataConstraintException En cas de doublons (champs uniques)
     */
    void createCampagne(@Normalize(CampagneNormalizer.class) Campagne campagne) throws DataConstraintException;

    /**
     * Supprime une campagne
     * 
     * @param campagne Campagne
     * @throws DataConstraintException Si des données liées empêchent la suppression
     */
    void deleteCampagne(Campagne campagne) throws DataConstraintException;

    /**
     * Liste les programmes des campagnes déjà saisis
     * 
     * @return Programmes des campagnes
     */
    @Transactional(readOnly = true)
    List<String> listCampagneProgrammes();

    /**
     * Liste les campagnes selon les droits d'un utilisateur (triés par nom)
     * 
     * @param utilisateur Utilisateur
     * @return Campagnes
     */
    @Transactional(readOnly = true)
    List<Campagne> listCampagnes(Utilisateur utilisateur);

    /**
     * Liste les campagnes selon les droits d'un utilisateur (triés par nom)
     * 
     * @param utilisateur Utilisateur
     * @return Campagnes
     */
    SortedSet<Campagne> listCampagnesForUser(Utilisateur utilisateur);

    /**
     * Charger une campagne
     * 
     * @param idCampagne ID campagne
     * @return Campagne
     * @throws DataNotFoundException Si non trouvée
     */
    Campagne loadCampagne(Integer idCampagne) throws DataNotFoundException;

    /**
     * Charger une campagne
     * 
     * @param nom Nom
     * @return Campagne
     * @throws DataNotFoundException Si non trouvée
     */
    Campagne loadCampagne(@Normalize(UniqueFieldNormalizer.class) String nom) throws DataNotFoundException;

    /**
     * Rafraichit une campagne (pour éviter des LazyLoadingException)
     * 
     * @param campagne Campagne
     */
    void refreshCampagne(Campagne campagne);

    /**
     * Met à jour une campagne
     * 
     * @param campagne Campagne
     * @throws DataConstraintException En cas de doublons (champs uniques)
     */
    void updateCampagne(@Normalize(CampagneNormalizer.class) Campagne campagne) throws DataConstraintException;

    /**
     * Détermine si un utilisateur peut modifier ou supprimer une campagne
     * 
     * @param campagne Campagne
     * @param utilisateur Utilisateur
     * @return TRUE si il a le droit
     */
    boolean updateOrdeleteCampagneEnabled(Campagne campagne, Utilisateur utilisateur);

}
