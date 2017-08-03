/*
 * #%L
 * Cantharella :: Utils
 * $Id: NumberToolsTest.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.utils/src/test/java/nc/ird/cantharella/utils/NumberToolsTest.java $
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

import nc.ird.cantharella.utils.NumberTools;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.Assert;
import org.junit.Test;

/**
 * NumberTools test
 * 
 * @author Mickael Tricot
 */
public final class NumberToolsTest {

    /**
     * nullIfZero test
     */
    @Test
    public void nullIfZero() {
        // AtomicInteger
        AtomicInteger ai = null;
        Assert.assertNull(NumberTools.nullIfZero(ai));
        ai = new AtomicInteger(-1);
        Assert.assertEquals(ai, NumberTools.nullIfZero(ai));
        ai.set(0);
        Assert.assertNull(NumberTools.nullIfZero(ai));
        ai.set(1);
        Assert.assertEquals(ai, NumberTools.nullIfZero(ai));
        // AtomicLong
        AtomicLong al = null;
        Assert.assertNull(NumberTools.nullIfZero(al));
        al = new AtomicLong(-1L);
        Assert.assertEquals(al, NumberTools.nullIfZero(al));
        al.set(0L);
        Assert.assertNull(NumberTools.nullIfZero(al));
        al.set(1L);
        Assert.assertEquals(al, NumberTools.nullIfZero(al));
        // BigDecimal
        BigDecimal bd = null;
        Assert.assertNull(NumberTools.nullIfZero(bd));
        bd = new BigDecimal(-1.0D);
        Assert.assertEquals(bd, NumberTools.nullIfZero(bd));
        bd = new BigDecimal(-.5D);
        Assert.assertEquals(bd, NumberTools.nullIfZero(bd));
        bd = new BigDecimal(0.0D);
        Assert.assertNull(NumberTools.nullIfZero(bd));
        bd = new BigDecimal(.5D);
        Assert.assertEquals(bd, NumberTools.nullIfZero(bd));
        bd = new BigDecimal(1.0D);
        Assert.assertEquals(bd, NumberTools.nullIfZero(bd));
        // BigInteger
        BigInteger bi = null;
        Assert.assertNull(NumberTools.nullIfZero(bi));
        bi = new BigInteger("-1");
        Assert.assertEquals(bi, NumberTools.nullIfZero(bi));
        bi = new BigInteger("0");
        Assert.assertNull(NumberTools.nullIfZero(bi));
        bi = new BigInteger("1");
        Assert.assertEquals(bi, NumberTools.nullIfZero(bi));
        // Byte
        Byte b = null;
        Assert.assertNull(NumberTools.nullIfZero(b));
        b = 0;
        Assert.assertNull(NumberTools.nullIfZero(b));
        b = 1;
        Assert.assertEquals(b, NumberTools.nullIfZero(b));
        // Double
        Double d = null;
        Assert.assertNull(NumberTools.nullIfZero(d));
        d = -1.0D;
        Assert.assertEquals(d, NumberTools.nullIfZero(d));
        d = -.5D;
        Assert.assertEquals(d, NumberTools.nullIfZero(d));
        d = 0.0D;
        Assert.assertNull(NumberTools.nullIfZero(d));
        d = .5D;
        Assert.assertEquals(d, NumberTools.nullIfZero(d));
        d = 1.0D;
        Assert.assertEquals(d, NumberTools.nullIfZero(d));
        // Float
        Float f = null;
        Assert.assertNull(NumberTools.nullIfZero(f));
        f = -1.0F;
        Assert.assertEquals(f, NumberTools.nullIfZero(f));
        f = -.5F;
        Assert.assertEquals(f, NumberTools.nullIfZero(f));
        f = 0.0F;
        Assert.assertNull(NumberTools.nullIfZero(f));
        f = .5F;
        Assert.assertEquals(f, NumberTools.nullIfZero(f));
        f = 1.0F;
        Assert.assertEquals(f, NumberTools.nullIfZero(f));
        // Integer
        Integer i = null;
        Assert.assertNull(NumberTools.nullIfZero(i));
        i = -1;
        Assert.assertEquals(i, NumberTools.nullIfZero(i));
        i = 0;
        Assert.assertNull(NumberTools.nullIfZero(i));
        i = 1;
        Assert.assertEquals(i, NumberTools.nullIfZero(i));
        // Long
        Long l = null;
        Assert.assertNull(NumberTools.nullIfZero(l));
        l = -1L;
        Assert.assertEquals(l, NumberTools.nullIfZero(l));
        l = 0L;
        Assert.assertNull(NumberTools.nullIfZero(l));
        l = 1L;
        Assert.assertEquals(l, NumberTools.nullIfZero(l));
        // Short
        Short s = null;
        Assert.assertNull(NumberTools.nullIfZero(s));
        s = -1;
        Assert.assertEquals(s, NumberTools.nullIfZero(s));
        s = 0;
        Assert.assertNull(NumberTools.nullIfZero(s));
        s = 1;
    }
}
