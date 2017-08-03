/*
 * #%L
 * Cantharella :: Data
 * $Id: GroupeTransactionTest.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.data/src/test/java/nc/ird/cantharella/data/model/GroupeTransactionTest.java $
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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import nc.ird.cantharella.data.dao.impl.HibernateTemplateDao;
import nc.ird.cantharella.data.dao.utils.SpringTransactionTestAppender;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.transaction.annotation.Transactional;

/**
 * Test if transaction works with groups
 * 
 * @author adri
 */
@Transactional
public class GroupeTransactionTest extends GroupeTest {

    /** For transaction logging */
    SpringTransactionTestAppender transactionTestAppender;

    /**
     * Initialization before tests
     */
    @Before
    public void initLogger() {
        transactionTestAppender = new SpringTransactionTestAppender();
    }

    /**
     * Release after tests
     */
    @After
    public void closeLogger() {
        transactionTestAppender.close();
    }

    /**
     * Test if transaction works when reading groups
     * 
     * @throws Exception -
     */
    @Test
    public void testReadGroupesTransaction() throws Exception {
        // again we run our integration test
        super.read();

        // and now we verify, that opening and closing transaction operations were made by the TransactionInterceptor
        String methodName = "readList";
        assertTrue(transactionTestAppender.isTransactionOpened(HibernateTemplateDao.class, methodName));
        assertTrue(transactionTestAppender.isTransactionCompleted(HibernateTemplateDao.class, methodName));
        assertFalse(transactionTestAppender.isTransactionRollbacked(HibernateTemplateDao.class, methodName));
    }

}