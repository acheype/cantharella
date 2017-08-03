/*
 * #%L
 * Cantharella :: Service
 * $Id: SpecimenService.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.service/src/main/java/nc/ird/cantharella/service/services/SpecimenService.java $
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
import nc.ird.cantharella.data.model.Specimen;
import nc.ird.cantharella.data.model.Utilisateur;
import nc.ird.cantharella.service.utils.normalizers.SpecimenNormalizer;
import nc.ird.cantharella.service.utils.normalizers.UniqueFieldNormalizer;
import nc.ird.cantharella.service.utils.normalizers.utils.Normalize;

import org.springframework.transaction.annotation.Transactional;

/**
 * Service : spécimens
 * 
 * @author Mickael Tricot
 * @author Adrien Cheype
 */
public interface SpecimenService {

    /**
     * Compte le nombre de specimens
     * 
     * @return Nombre de specimens
     */
    long countSpecimens();

    /**
     * Créée un spécimen
     * 
     * @param specimen Spécimen
     * @throws DataConstraintException Si le spécimen (nom) existe déjà
     */
    void createSpecimen(@Normalize(SpecimenNormalizer.class) Specimen specimen) throws DataConstraintException;

    /**
     * Supprime un spécimen
     * 
     * @param specimen Spécimen
     * @throws DataConstraintException Si le spécimen a des données liées
     */
    void deleteSpecimen(Specimen specimen) throws DataConstraintException;

    /**
     * Liste les spécimens selon les droits d'un utilisateur (triés par réf)
     * 
     * @param utilisateur L'utilisateur
     * @return La liste des spécimens
     */
    @Transactional(readOnly = true)
    List<Specimen> listSpecimens(Utilisateur utilisateur);

    /**
     * Liste l'ensemble des specimens selon les droits d'un utilisateur (triés par réf)
     * 
     * @param utilisateur L'utilisateur
     * @return La liste des spécimens
     */
    SortedSet<Specimen> listSpecimensForUser(Utilisateur utilisateur);

    /**
     * Liste les embranchements existants pour les spécimens
     * 
     * @return Les embranchements
     */
    @Transactional(readOnly = true)
    List<String> listSpecimenEmbranchements();

    /**
     * Liste les embranchements existants pour les spécimens
     * 
     * @return Les familles
     */
    @Transactional(readOnly = true)
    List<String> listSpecimenFamilles();

    /**
     * Liste les genres existants pour les spécimens
     * 
     * @return Les genres
     */
    @Transactional(readOnly = true)
    List<String> listSpecimenGenres();

    /**
     * Liste les espèces existants pour les spécimens
     * 
     * @return Les espèces
     */
    @Transactional(readOnly = true)
    List<String> listSpecimenEspeces();

    /**
     * Liste les sous-espèces existants pour les spécimens
     * 
     * @return Les sous-espèces
     */
    @Transactional(readOnly = true)
    List<String> listSpecimenSousEspeces();

    /**
     * Liste les variétés existants pour les spécimens
     * 
     * @return Les variétés
     */
    @Transactional(readOnly = true)
    List<String> listSpecimenVarietes();

    /**
     * Liste les lieux de dépots existants pour les spécimens
     * 
     * @return Les lieux de dépôt
     */
    @Transactional(readOnly = true)
    List<String> listLieuxDepot();

    /**
     * Charge un spécimen
     * 
     * @param idSpecimen ID
     * @return Spécimen
     * @throws DataNotFoundException Si le spécimen n'existe pas
     */
    Specimen loadSpecimen(Integer idSpecimen) throws DataNotFoundException;

    /**
     * Charge un pécimen
     * 
     * @param ref Référence
     * @return Spécimen
     * @throws DataNotFoundException Si le spécimen n'existe pas
     */
    Specimen loadSpecimen(@Normalize(UniqueFieldNormalizer.class) String ref) throws DataNotFoundException;

    /**
     * Détermine si un utilisateur peut modifier ou supprimer un specimen
     * 
     * @param specimen Specimen
     * @param utilisateur Utilisateur
     * @return TRUE si il a le droit
     */
    boolean updateOrdeleteSpecimenEnabled(Specimen specimen, Utilisateur utilisateur);

    /**
     * Met à jour un spécimen
     * 
     * @param specimen Spécimen
     * @throws DataConstraintException Si le pécimen (nom) existe déjà
     */
    void updateSpecimen(@Normalize(SpecimenNormalizer.class) Specimen specimen) throws DataConstraintException;

}
