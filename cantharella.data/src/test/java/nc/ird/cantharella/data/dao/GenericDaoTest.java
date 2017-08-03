/*
 * #%L
 * Cantharella :: Data
 * $Id: GenericDaoTest.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.data/src/test/java/nc/ird/cantharella/data/dao/GenericDaoTest.java $
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
package nc.ird.cantharella.data.dao;

import nc.ird.cantharella.data.AbstractDataTest;
import nc.ird.cantharella.data.exceptions.DataConstraintException;

import org.hibernate.exception.SQLGrammarException;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * GenericDaoTest
 * 
 * @author Mickael Tricot
 */
public final class GenericDaoTest extends AbstractDataTest {

    /** DAO */
    @Autowired
    private GenericDao dao;

    /**
     * Check that a wrong SQL request throws a DataAccessException
     * 
     * @throws DataConstraintException -
     */
    @Test(expected = SQLGrammarException.class)
    public void execute() throws DataConstraintException {
        dao.execute("SELECT * FROM Foo");
    }

    /**
     * Basic test
     */
    @Test
    public void test() {
        Assert.assertNotNull(dao);
    }
}
