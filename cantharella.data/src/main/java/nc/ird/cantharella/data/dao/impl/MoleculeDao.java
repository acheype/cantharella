/*
 * #%L
 * Cantharella :: Data
 * $Id: MoleculeDao.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.data/src/main/java/nc/ird/cantharella/data/dao/impl/MoleculeDao.java $
 * %%
 * Copyright (C) 2013 IRD (Institut de Recherche pour le Developpement) and by respective authors (see below)
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
import nc.ird.cantharella.data.model.Molecule;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;

/**
 * DAO molecules.
 * 
 * @author Mickael Tricot
 * @author Adrien Cheype
 */
public final class MoleculeDao extends AbstractModelDao {

    /** Criteria : différents organismes des molécules déjà saisis */
    public static final DetachedCriteria CRITERIA_DISTINCT_MOLECULE_ORGANISMES = DetachedCriteria
            .forClass(Molecule.class).setProjection(Projections.distinct(Projections.property("identifieePar")))
            .addOrder(Order.asc("identifieePar"));

    /**
     * Constructor (prevents from instantiation)
     */
    private MoleculeDao() {
        //
    }
}
