/*
 * #%L
 * Cantharella :: Utils
 * $Id: PairTest.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.utils/src/test/java/nc/ird/cantharella/utils/PairTest.java $
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
package nc.ird.cantharella.utils;

import nc.ird.cantharella.utils.Pair;
import org.junit.Assert;
import org.junit.Test;

/**
 * Pair test
 * 
 * @author Mickael Tricot
 */
public final class PairTest {

    /**
     * pair test
     */
    @Test
    public void pair() {
        Pair<Integer, String> pair = new Pair<Integer, String>(1, "Toto");
        Assert.assertEquals((Integer) 1, pair.getKey());
        Assert.assertEquals("Toto", pair.getValue());
        pair.setKey(2);
        Assert.assertEquals((Integer) 2, pair.getKey());
        Assert.assertEquals("Toto", pair.setValue("Titi"));
        Assert.assertEquals("Titi", pair.getValue());
        Pair<Integer, String> pair2 = new Pair<Integer, String>(1, "Titi");
        Assert.assertNotSame(pair, pair2);
        Pair<Integer, String> pair3 = new Pair<Integer, String>(2, "Titi");
        Assert.assertEquals(pair, pair3);
    }
}
