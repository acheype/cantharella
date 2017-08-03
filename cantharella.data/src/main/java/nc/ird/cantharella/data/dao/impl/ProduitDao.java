/*
 * #%L
 * Cantharella :: Data
 * $Id: ProduitDao.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.data/src/main/java/nc/ird/cantharella/data/dao/impl/ProduitDao.java $
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
package nc.ird.cantharella.data.dao.impl;

import nc.ird.cantharella.data.dao.AbstractModelDao;
import nc.ird.cantharella.data.model.MoleculeProvenance;
import nc.ird.cantharella.data.model.Purification;
import nc.ird.cantharella.data.model.ResultatTestBio;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

/** DAO : produit */
public final class ProduitDao extends AbstractModelDao {

    /**
     * Constructor (empêche l'instantiation)
     */
    private ProduitDao() {
        //
    }

    /**
     * Rend le criteria qui rend le nombre de purifications qui référencent un produit
     * 
     * @param refProduit La référence du produit
     * @return Le criteria
     **/
    public static DetachedCriteria getCriteriaCountPurifFromProduit(String refProduit) {
        return DetachedCriteria.forClass(Purification.class).createAlias("produit", "prod")
                .add(Restrictions.eq("prod.ref", refProduit)).setProjection(Projections.rowCount());
    }

    /**
     * Rend le criteria qui rend le nombre de tests biologiques qui référencent un produit
     * 
     * @param refProduit La référence du produit
     * @return Le criteria
     **/
    public static DetachedCriteria getCriteriaCountTestBioFromProduit(String refProduit) {
        return DetachedCriteria.forClass(ResultatTestBio.class).createAlias("produit", "prod")
                .add(Restrictions.eq("prod.ref", refProduit)).setProjection(Projections.rowCount());
    }

    /**
     * Rend le criteria qui rend le nombre de molécule qui référencent un produit.
     * 
     * @param refProduit product reference
     * @return Le criteria
     **/
    public static DetachedCriteria getCriteriaCountMoleculeFromProduit(String refProduit) {
        return DetachedCriteria.forClass(MoleculeProvenance.class).createAlias("produit", "prod")
                .add(Restrictions.eq("prod.ref", refProduit)).setProjection(Projections.rowCount());
    }

}
