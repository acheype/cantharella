/*
 * #%L
 * Cantharella :: Data
 * $Id: CampagneDao.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.data/src/main/java/nc/ird/cantharella/data/dao/impl/CampagneDao.java $
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
import nc.ird.cantharella.data.model.Campagne;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;

/**
 * DAO campagne
 * 
 * @author Mickael Tricot
 * @author Adrien Cheype
 */
public final class CampagneDao extends AbstractModelDao {

    /** Criteria : différents programmes des campagnes déjà saisis */
    public static final DetachedCriteria CRITERIA_DISTINCT_CAMPAGNE_PROGRAMMES = DetachedCriteria
            .forClass(Campagne.class).setProjection(Projections.distinct(Projections.property("programme")))
            .addOrder(Order.asc("programme"));

    /**
     * Constructor (prevents from instantiation)
     */
    private CampagneDao() {
        //
    }
}
