/*
 * #%L
 * Cantharella :: Data
 * $Id: GroupeTest.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.data/src/test/java/nc/ird/cantharella/data/model/GroupeTest.java $
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
package nc.ird.cantharella.data.model;

import nc.ird.cantharella.data.AbstractDataTest;
import nc.ird.cantharella.data.dao.GenericDao;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Test: groupe
 * 
 * @author Mickael Tricot
 */
public class GroupeTest extends AbstractDataTest {

    /** DAO */
    @Autowired
    private GenericDao dao;

    /**
     * Read groupes
     */
    @Test
    public void read() {
        Assert.assertNotNull(dao);
        Assert.assertNotNull(dao.readList(Groupe.class));
    }
}
