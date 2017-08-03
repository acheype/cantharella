/*
 * #%L
 * Cantharella :: Data
 * $Id: PersonneTest.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.data/src/test/java/nc/ird/cantharella/data/model/PersonneTest.java $
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

import org.junit.Test;

/**
 * Test: personnes
 * 
 * @author Mickael Tricot
 * @author Adrien Cheype
 */
public class PersonneTest extends AbstractDataTest {

    /** DAO */
    // @Autowired
    // private GenericDao dao;

    /**
     * Read personne fail //* @throws DataNotFoundException -
     */
    // test desactivated because of an
    // "no method exception found Check that an AOP invocation is in progress, and that the ExposeInvocationInterceptor is in the interceptor chain"
    // only in the test environment
    // @Test(expected = DataNotFoundException.class)
    @Test
    public void readFail() { // throws DataNotFoundException {
        // dao.read(Personne.class, 999999999);
    }

}