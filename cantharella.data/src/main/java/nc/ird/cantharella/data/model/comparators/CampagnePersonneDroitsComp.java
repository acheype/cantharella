/*
 * #%L
 * Cantharella :: Data
 * $Id: CampagnePersonneDroitsComp.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.data/src/main/java/nc/ird/cantharella/data/model/comparators/CampagnePersonneDroitsComp.java $
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
package nc.ird.cantharella.data.model.comparators;

import java.util.Comparator;

import nc.ird.cantharella.data.model.CampagnePersonneDroits;

import org.apache.commons.beanutils.BeanComparator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * CampagnePersonneDroits comparator. Compare on campagne.
 * 
 * @author Mickael Tricot
 * @author Adrien Cheype
 */
public class CampagnePersonneDroitsComp implements Comparator<CampagnePersonneDroits> {

    /** Logger */
    private static final Logger LOG = LoggerFactory.getLogger(CampagnePersonneDroitsComp.class);

    /** {@inheritDoc} */
    @Override
    public int compare(CampagnePersonneDroits obj1, CampagnePersonneDroits obj2) {
        LOG.debug("compare personnes droit");
        return new BeanComparator("id.pk1").compare(obj1, obj2);
    }
}
