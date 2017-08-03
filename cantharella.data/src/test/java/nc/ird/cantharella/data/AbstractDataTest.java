/*
 * #%L
 * Cantharella :: Data
 * $Id: AbstractDataTest.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.data/src/test/java/nc/ird/cantharella/data/AbstractDataTest.java $
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
package nc.ird.cantharella.data;

import nc.ird.cantharella.data.config.DataContext;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

/**
 * Test (to be extended) for the data layer
 * 
 * @author Mickael Tricot
 * @author Adrien Cheype
 */
@RunWith(SpringJUnit4ClassRunner.class)
// Load Spring ApplicationContext
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = DataContext.class)
@TransactionConfiguration
@Transactional
public abstract class AbstractDataTest extends AbstractTransactionalJUnit4SpringContextTests {

    /**
     * Initialization
     */
    @Before
    public void setUp() {
        applicationContext.getAutowireCapableBeanFactory().autowireBean(this);
    }
}
