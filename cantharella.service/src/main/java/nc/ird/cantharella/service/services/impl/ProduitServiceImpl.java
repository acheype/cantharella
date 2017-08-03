/*
 * #%L
 * Cantharella :: Service
 * $Id: ProduitServiceImpl.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.service/src/main/java/nc/ird/cantharella/service/services/impl/ProduitServiceImpl.java $
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
import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import nc.ird.cantharella.data.dao.GenericDao;
import nc.ird.cantharella.data.dao.impl.ProduitDao;
import nc.ird.cantharella.data.model.Extraction;
import nc.ird.cantharella.data.model.Extrait;
import nc.ird.cantharella.data.model.Fraction;
import nc.ird.cantharella.data.model.Produit;
import nc.ird.cantharella.data.model.Purification;
import nc.ird.cantharella.data.model.Utilisateur;
import nc.ird.cantharella.data.model.Utilisateur.TypeDroit;
import nc.ird.cantharella.service.services.ExtractionService;
import nc.ird.cantharella.service.services.ProduitService;
import nc.ird.cantharella.service.services.PurificationService;
import nc.ird.cantharella.utils.AssertTools;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Implémentation du service produit
 * 
 * @author Adrien Cheype
 */
@Service
public final class ProduitServiceImpl implements ProduitService {

    /** DAO */
    @Autowired
    private GenericDao dao;

    /** Service pour les extractions **/
    @Autowired
    private ExtractionService extractionService;

    /** Service pour les purifications **/
    @Autowired
    private PurificationService purificationService;

    /** {@inheritDoc} */
    @Override
    public List<Produit> listProduits(Utilisateur utilisateur) {
        AssertTools.assertNotNull(utilisateur);

        if (utilisateur.getTypeDroit() == TypeDroit.ADMINISTRATEUR) {
            SortedSet<Produit> produits = new TreeSet<Produit>();
            produits.addAll(dao.readList(Extrait.class, "ref"));
            produits.addAll(dao.readList(Fraction.class, "ref"));
            return new ArrayList<Produit>(produits);
        }
        // gestion des droits en plus pour les utilisateurs
        return new ArrayList<Produit>(listProduitsForUser(utilisateur));
    }

    /** {@inheritDoc} */
    @Override
    public SortedSet<Produit> listProduitsForUser(Utilisateur utilisateur) {
        AssertTools.assertNotNull(utilisateur);
        SortedSet<Produit> produits = new TreeSet<Produit>();

        for (Extraction curExtraction : extractionService.listExtractionsForUser(utilisateur)) {
            produits.addAll(curExtraction.getExtraits());
        }
        for (Purification curPurification : purificationService.listPurificationsForUser(utilisateur)) {
            produits.addAll(curPurification.getFractions());
        }

        return produits;
    }

    /** {@inheritDoc} */
    @Override
    public List<Produit> listProduitsWithoutChildrenOfPuri(Utilisateur utilisateur, Purification purification) {
        AssertTools.assertNotNull(utilisateur);
        AssertTools.assertNotNull(purification);

        List<Produit> produits = listProduits(utilisateur);

        // version itérative (dérécursification)
        LinkedList<Fraction> fractionsQueue = new LinkedList<Fraction>(purification.getFractions());
        while (!fractionsQueue.isEmpty()) {
            Fraction curFraction = fractionsQueue.remove();
            produits.remove(curFraction);
            // LOG.debug("-removeFraction : " + curFraction);

            for (Purification curPuriFromFraction : curFraction.getPurificationsSuivantes()) {
                fractionsQueue.addAll(curPuriFromFraction.getFractions());
            }
        }
        return produits;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isProduitReferenced(Produit produit) {
        return dao.count(ProduitDao.getCriteriaCountPurifFromProduit(produit.getRef())) > 0
                || dao.count(ProduitDao.getCriteriaCountTestBioFromProduit(produit.getRef())) > 0
                || dao.count(ProduitDao.getCriteriaCountMoleculeFromProduit(produit.getRef())) > 0;
    }
}
