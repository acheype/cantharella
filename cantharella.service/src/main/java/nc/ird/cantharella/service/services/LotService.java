/*
 * #%L
 * Cantharella :: Service
 * $Id: LotService.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.service/src/main/java/nc/ird/cantharella/service/services/LotService.java $
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
import nc.ird.cantharella.data.model.Lot;
import nc.ird.cantharella.data.model.Partie;
import nc.ird.cantharella.data.model.Utilisateur;
import nc.ird.cantharella.service.utils.normalizers.LotNormalizer;
import nc.ird.cantharella.service.utils.normalizers.PartieNormalizer;
import nc.ird.cantharella.service.utils.normalizers.UniqueFieldNormalizer;
import nc.ird.cantharella.service.utils.normalizers.utils.Normalize;

import org.springframework.transaction.annotation.Transactional;

/**
 * Service : lots
 * 
 * @author Mickael Tricot
 * @author Adrien Cheype
 */
public interface LotService {

    /**
     * Compte le nombre de lots
     * 
     * @return Nombre de lots
     */
    long countLots();

    /**
     * Rafraichit un lot (pour éviter des LazyLoadingException)
     * 
     * @param lot Lot
     */
    void refreshLot(Lot lot);

    /**
     * Créée un lot
     * 
     * @param lot Lot
     * @throws DataConstraintException Si le lot (nom) existe déjà
     */
    void createLot(@Normalize(LotNormalizer.class) Lot lot) throws DataConstraintException;

    /**
     * Supprime un lot
     * 
     * @param lot Lot
     * @throws DataConstraintException Si le lot a des données liées
     */
    void deleteLot(Lot lot) throws DataConstraintException;

    /**
     * Liste les lots pour lesquelles l'utilisateur a les droits (triés par réf)
     * 
     * @param utilisateur Utilisateur
     * @return Lots
     */
    @Transactional(readOnly = true)
    List<Lot> listLots(Utilisateur utilisateur);

    /**
     * Liste les lots pour lesquelles l'utilisateur a les droits (triés par réf)
     * 
     * @param utilisateur Utilisateur non admin
     * @return Lots
     */
    SortedSet<Lot> listLotsForUser(Utilisateur utilisateur);

    /**
     * Détermine si un utilisateur peut modifier ou supprimer un lot
     * 
     * @param lot Lot
     * @param utilisateur Utilisateur
     * @return TRUE si il a le droit
     */
    boolean updateOrdeleteLotEnabled(Lot lot, Utilisateur utilisateur);

    /**
     * Détermine si un utilisateur peut accéder à un lot
     * 
     * @param lot Lot
     * @param utilisateur Utilisateur
     * @return TRUE si il a le droit
     */
    boolean isLotAccessibleByUser(Lot lot, Utilisateur utilisateur);

    /**
     * Liste les parties possibles pour un lot
     * 
     * @return Parties
     */
    @Transactional(readOnly = true)
    List<Partie> listParties();

    /**
     * Charge un lot
     * 
     * @param idLot ID
     * @return Lot
     * @throws DataNotFoundException Si le lot n'existe pas
     */
    Lot loadLot(Integer idLot) throws DataNotFoundException;

    /**
     * Charge un lot
     * 
     * @param ref Référence
     * @return Le lot correspondant
     * @throws DataNotFoundException Si le lot n'existe pas
     */
    Lot loadLot(@Normalize(UniqueFieldNormalizer.class) String ref) throws DataNotFoundException;

    /**
     * Met à jour un lot
     * 
     * @param lot Lot
     * @throws DataConstraintException Si le lot (nom) existe déjà
     */
    void updateLot(@Normalize(LotNormalizer.class) Lot lot) throws DataConstraintException;

    /**
     * Créé une partie
     * 
     * @param partie Partie
     * @throws DataConstraintException En cas de doublons (champs uniques)
     */
    void createPartie(@Normalize(PartieNormalizer.class) Partie partie) throws DataConstraintException;

    /**
     * Charger une partie
     * 
     * @param idPartie ID
     * @return La partie correspondante
     * @throws DataNotFoundException Si non trouvée
     */
    Partie loadPartie(Integer idPartie) throws DataNotFoundException;

    /**
     * Charger une partie
     * 
     * @param nom Nom
     * @return Partie
     * @throws DataNotFoundException Si non trouvée
     */
    Partie loadPartie(@Normalize(UniqueFieldNormalizer.class) String nom) throws DataNotFoundException;

    /**
     * Modifie une partie
     * 
     * @param partie Partie
     * @throws DataConstraintException En cas de doublons (champs uniques)
     */
    void updatePartie(@Normalize(PartieNormalizer.class) Partie partie) throws DataConstraintException;

    /**
     * Supprime une partie
     * 
     * @param partie Partie
     * @throws DataConstraintException En cas de données liées
     */
    void deletePartie(Partie partie) throws DataConstraintException;

}
