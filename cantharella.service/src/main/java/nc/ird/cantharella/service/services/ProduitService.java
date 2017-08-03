/*
 * #%L
 * Cantharella :: Service
 * $Id: ProduitService.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.service/src/main/java/nc/ird/cantharella/service/services/ProduitService.java $
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

import org.springframework.transaction.annotation.Transactional;

import nc.ird.cantharella.data.model.Produit;
import nc.ird.cantharella.data.model.Purification;
import nc.ird.cantharella.data.model.Utilisateur;

/**
 * Service : produits (ensemble des extraits et fractions)
 * 
 * @author Adrien Cheype
 */
public interface ProduitService {

    /**
     * Liste les produits disponibles selon les droits d'un utilisateur (trié par réf)
     * 
     * @param utilisateur L'utilisateur
     * @return la liste des produits
     */
    @Transactional(readOnly = true)
    List<Produit> listProduits(Utilisateur utilisateur);

    /**
     * Liste les produits disponibles selon les droits d'un utilisateur (trié par réf)
     * 
     * @param utilisateur L'utilisateur non admin
     * @return la liste des produits
     */
    SortedSet<Produit> listProduitsForUser(Utilisateur utilisateur);

    /**
     * Liste les produits disponibles selon les droits d'un utilisateur. Enlève en plus tous les produits resultants de
     * la purification donnée
     * 
     * @param utilisateur L'utilisateur
     * @param purification La purification
     * @return la liste des produits
     */
    @Transactional(readOnly = true)
    List<Produit> listProduitsWithoutChildrenOfPuri(Utilisateur utilisateur, Purification purification);

    /**
     * Vérifie si des données référence le produit (purification ou test biologique)
     * 
     * @param produit Le produit
     * @return TRUE si le produit est référencé au moins une fois
     */
    @Transactional(readOnly = true)
    boolean isProduitReferenced(Produit produit);

}
