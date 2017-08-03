/*
 * #%L
 * Cantharella :: Data
 * $Id: ExtractionDao.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.data/src/main/java/nc/ird/cantharella/data/dao/impl/ExtractionDao.java $
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

import nc.ird.cantharella.data.model.Extrait;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

/**
 * DAO : extraction
 * 
 * @author Adrien Cheype
 */
public class ExtractionDao {

    /**
     * Constructor (empêche l'instantiation)
     */
    private ExtractionDao() {
        //
    }

    /**
     * Rend le criteria qui rend le nombre d'extrait qui référencent un type d'extrait
     * 
     * @param idTypeExtrait L'id du type extrait
     * @return Le criteria
     **/
    public static DetachedCriteria getCriteriaCountExtraitOfTypeExtrait(Integer idTypeExtrait) {
        return DetachedCriteria.forClass(Extrait.class).createAlias("typeExtrait", "type")
                .add(Restrictions.eq("type.idTypeExtrait", idTypeExtrait)).setProjection(Projections.rowCount());
    }

}
