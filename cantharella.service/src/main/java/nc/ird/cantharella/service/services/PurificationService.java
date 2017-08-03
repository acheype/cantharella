/*
 * #%L
 * Cantharella :: Service
 * $Id: PurificationService.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.service/src/main/java/nc/ird/cantharella/service/services/PurificationService.java $
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
import nc.ird.cantharella.data.model.Fraction;
import nc.ird.cantharella.data.model.MethodePurification;
import nc.ird.cantharella.data.model.ParamMethoPuri;
import nc.ird.cantharella.data.model.Purification;
import nc.ird.cantharella.data.model.Utilisateur;
import nc.ird.cantharella.service.utils.normalizers.MethodePurificationNormalizer;
import nc.ird.cantharella.service.utils.normalizers.PurificationNormalizer;
import nc.ird.cantharella.service.utils.normalizers.UniqueFieldNormalizer;
import nc.ird.cantharella.service.utils.normalizers.utils.Normalize;

import org.springframework.transaction.annotation.Transactional;

/**
 * Service : purification, méthode de purification, fractions
 * 
 * @author Adrien Cheype
 */
public interface PurificationService {

    /**
     * Compte le nombre de purifications
     * 
     * @return Nombre de purifications
     */
    long countPurifications();

    /**
     * Créée une manipulation de purification
     * 
     * @param purification La manipulation
     * @throws DataConstraintException Si la manipulation (réf) existe déjà
     */
    void createPurification(@Normalize(PurificationNormalizer.class) Purification purification)
            throws DataConstraintException;

    /**
     * Initialise les paramètres de la méthode avec des valeurs vides. Si certains paramètres existent déjà, aucune
     * modification sur eux
     * 
     * @param purification La purification
     */
    void initParamsMethoPuriEffectif(Purification purification);

    /**
     * Supprime une manipulation de purification
     * 
     * @param purification La manipulation
     * @throws DataConstraintException En cas de données liées
     */
    void deletePurification(Purification purification) throws DataConstraintException;

    /**
     * Liste les manipulations de purification selon les droits d'un utilisateur (triés par réf)
     * 
     * @param utilisateur L'utilisateur
     * @return la liste des manipulations
     */
    @Transactional(readOnly = true)
    List<Purification> listPurifications(Utilisateur utilisateur);

    /**
     * Liste les manipulations de purification selon les droits d'un utilisateur (triés par réf)
     * 
     * @param utilisateur L'utilisateur non admin
     * @return la liste des manipulations
     */
    SortedSet<Purification> listPurificationsForUser(Utilisateur utilisateur);

    /**
     * Vérifie si la purification de référence donnée existe est unique dans la base
     * 
     * @param purification La purification
     * @return TRUE si la purification est unique
     */
    @Transactional(readOnly = true)
    boolean isPurificationUnique(Purification purification);

    /**
     * Charge une manipulation de purification
     * 
     * @param idPurification ID de la manipulation
     * @return La manipulation
     * @throws DataNotFoundException Si non trouvée
     */
    Purification loadPurification(Integer idPurification) throws DataNotFoundException;

    /**
     * Charge une manipulation de purification
     * 
     * @param ref Référence de la manipulation
     * @return La manipulation correspondante
     * @throws DataNotFoundException Si non trouvée
     */
    Purification loadPurification(@Normalize(UniqueFieldNormalizer.class) String ref) throws DataNotFoundException;

    /**
     * Met à jour une manipulation de purification
     * 
     * @param purification La manipulation
     * @throws DataConstraintException En cas de doublons (champs uniques)
     */
    void updatePurification(@Normalize(PurificationNormalizer.class) Purification purification)
            throws DataConstraintException;

    /**
     * Rafraichit une purification (pour éviter des LazyLoadingException)
     * 
     * @param purification Purification
     */
    void refreshPurification(Purification purification);

    /**
     * Détermine si un utilisateur peut modifier ou supprimer une purification
     * 
     * @param purification La manipulation
     * @param utilisateur L'utilisateur
     * @return TRUE s'il a le droit
     */
    boolean updateOrdeletePurificationEnabled(Purification purification, Utilisateur utilisateur);

    /**
     * Créée une méthode pour une purification
     * 
     * @param methode La méthode
     * @throws DataConstraintException Si la méthode (nom) existe déjà
     */
    void createMethodePurification(@Normalize(MethodePurificationNormalizer.class) MethodePurification methode)
            throws DataConstraintException;

    /**
     * Supprime une méthode pour une purification
     * 
     * @param methode La méthode
     * @throws DataConstraintException En cas de données liées
     */
    void deleteMethodePurification(MethodePurification methode) throws DataConstraintException;

    /**
     * Liste les méthodes existantes pour une purification (triés par nom)
     * 
     * @return la liste des méthodes
     */
    @Transactional(readOnly = true)
    List<MethodePurification> listMethodesPurification();

    /**
     * Charge une méthode pour une purification
     * 
     * @param idMethode ID de la méthode
     * @return La méthode correspondante
     * @throws DataNotFoundException Si non trouvée
     */
    MethodePurification loadMethodePurification(Integer idMethode) throws DataNotFoundException;

    /**
     * Charge une méthode pour une purification
     * 
     * @param nom Nom de la méthode
     * @return La méthode correspondante
     * @throws DataNotFoundException Si non trouvée
     */
    MethodePurification loadMethodePurification(@Normalize(UniqueFieldNormalizer.class) String nom)
            throws DataNotFoundException;

    /**
     * Met à jour une méthode pour une purification
     * 
     * @param methode La méthode
     * @throws DataConstraintException En cas de doublons (champs uniques)
     */
    void updateMethodePurification(@Normalize(MethodePurificationNormalizer.class) MethodePurification methode)
            throws DataConstraintException;

    /**
     * Rafraichit une méthode de purification (pour éviter des LazyLoadingException)
     * 
     * @param methode La méthode
     */
    void refreshMethodePurification(MethodePurification methode);

    /**
     * Vérifie si au moins une purification référence le paramètre
     * 
     * @param paramMetho Le paramètre de la méthode
     * @return TRUE si le paramètre est référencé
     */
    @Transactional(readOnly = true)
    boolean isParamMethoPuriReferenced(ParamMethoPuri paramMetho);

    /**
     * Vérifie si la fraction de référence donnée est unique dans la base
     * 
     * @param fraction La fraction
     * @return TRUE si la fraction existe
     */
    @Transactional(readOnly = true)
    boolean isFractionUnique(Fraction fraction);

}
