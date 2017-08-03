/*
 * #%L
 * Cantharella :: Service
 * $Id: ExtractionService.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.service/src/main/java/nc/ird/cantharella/service/services/ExtractionService.java $
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
import nc.ird.cantharella.data.model.Extraction;
import nc.ird.cantharella.data.model.Extrait;
import nc.ird.cantharella.data.model.MethodeExtraction;
import nc.ird.cantharella.data.model.TypeExtrait;
import nc.ird.cantharella.data.model.Utilisateur;
import nc.ird.cantharella.service.utils.normalizers.ExtractionNormalizer;
import nc.ird.cantharella.service.utils.normalizers.MethodeExtractionNormalizer;
import nc.ird.cantharella.service.utils.normalizers.UniqueFieldNormalizer;
import nc.ird.cantharella.service.utils.normalizers.utils.Normalize;

import org.springframework.transaction.annotation.Transactional;

/**
 * Service : extraction, méthode d'extraction, extraits
 * 
 * @author Adrien Cheype
 */
public interface ExtractionService {

    /**
     * Compte le nombre d'extractions
     * 
     * @return Nombre d'extractions
     */
    long countExtractions();

    /**
     * Créée une manipulation d'extraction
     * 
     * @param extraction La manipulation
     * @throws DataConstraintException Si la manipulation (réf) existe déjà
     */
    void createExtraction(@Normalize(ExtractionNormalizer.class) Extraction extraction) throws DataConstraintException;

    /**
     * Supprime une manipulation d'extraction
     * 
     * @param extraction L'extraction
     * @throws DataConstraintException En cas de données liées
     */
    void deleteExtraction(Extraction extraction) throws DataConstraintException;

    /**
     * Liste les manipulations d'extraction selon les droits d'un utilisateur (triés par réf)
     * 
     * @param utilisateur L'utilisateur
     * @return la liste des extractions
     */
    @Transactional(readOnly = true)
    List<Extraction> listExtractions(Utilisateur utilisateur);

    /**
     * Liste les manipulations d'extraction selon les droits d'un utilisateur (triés par réf)
     * 
     * @param utilisateur L'utilisateur non admin
     * @return la liste des manipulations
     */
    SortedSet<Extraction> listExtractionsForUser(Utilisateur utilisateur);

    /**
     * Vérifie si l'extraction de référence donnée existe est unique dans la base
     * 
     * @param extraction L'extraction
     * @return TRUE si l'extraction est unique
     */
    boolean isExtractionUnique(Extraction extraction);

    /**
     * Charge une manipulation d'extraction
     * 
     * @param idExtraction ID de la manipulation
     * @return La manipulation correspondante
     * @throws DataNotFoundException Si non trouvée
     */
    Extraction loadExtraction(Integer idExtraction) throws DataNotFoundException;

    /**
     * Charge une manipulation d'extraction
     * 
     * @param ref Référence de la manipulation
     * @return La manipulation correspondante
     * @throws DataNotFoundException Si non trouvée
     */
    Extraction loadExtraction(@Normalize(UniqueFieldNormalizer.class) String ref) throws DataNotFoundException;

    /**
     * Met à jour une manipulation d'extraction
     * 
     * @param extraction La manipulation
     * @throws DataConstraintException En cas de doublons (champs uniques)
     */
    void updateExtraction(@Normalize(ExtractionNormalizer.class) Extraction extraction) throws DataConstraintException;

    /**
     * Détermine si un utilisateur peut modifier ou supprimer une extraction
     * 
     * @param extraction L'extraction
     * @param utilisateur L'utilisateur
     * @return TRUE s'il a le droit
     */
    boolean updateOrdeleteExtractionEnabled(Extraction extraction, Utilisateur utilisateur);

    /**
     * Créée une méthode pour une extraction
     * 
     * @param methode La méthode
     * @throws DataConstraintException Si la méthode (nom) existe déjà
     */
    void createMethodeExtraction(@Normalize(MethodeExtractionNormalizer.class) MethodeExtraction methode)
            throws DataConstraintException;

    /**
     * Supprime une méthode pour une extraction
     * 
     * @param methode La méthode
     * @throws DataConstraintException En cas de données liées
     */
    void deleteMethodeExtraction(MethodeExtraction methode) throws DataConstraintException;

    /**
     * Liste les méthodes existantes pour une extraction (triés par nom)
     * 
     * @return la liste des méthodes
     */
    List<MethodeExtraction> listMethodesExtraction();

    /**
     * Charge une méthode pour une extraction
     * 
     * @param idMethode ID de la méthode
     * @return La méthode correspondante
     * @throws DataNotFoundException Si non trouvée
     */
    MethodeExtraction loadMethodeExtraction(Integer idMethode) throws DataNotFoundException;

    /**
     * Charge une méthode pour une extraction
     * 
     * @param nom Nom de la méthode
     * @return La méthode correspondante
     * @throws DataNotFoundException Si non trouvée
     */
    MethodeExtraction loadMethodeExtraction(@Normalize(UniqueFieldNormalizer.class) String nom)
            throws DataNotFoundException;

    /**
     * Met à jour une méthode pour une extraction
     * 
     * @param methode La méthode
     * @throws DataConstraintException En cas de doublons (champs uniques)
     */
    void updateMethodeExtraction(@Normalize(MethodeExtractionNormalizer.class) MethodeExtraction methode)
            throws DataConstraintException;

    /**
     * Rafraichit une méthode d'extraction (pour éviter des LazyLoadingException)
     * 
     * @param methode La méthode
     */
    void refreshMethodeExtraction(MethodeExtraction methode);

    /**
     * Vérifie si au moins un extrait référence ce type d'extrait
     * 
     * @param typeExtrait Le type d'extrait
     * @return TRUE si le type d'extrait est référencé
     */
    boolean isTypeExtraitReferenced(TypeExtrait typeExtrait);

    /**
     * Vérifie si l'extrait de référence donnée est unique dans la base
     * 
     * @param extrait L'extrait
     * @return TRUE si l'extrait est unique
     */
    boolean isExtraitUnique(Extrait extrait);

}
