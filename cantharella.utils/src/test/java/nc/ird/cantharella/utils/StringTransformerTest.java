/*
 * #%L
 * Cantharella :: Utils
 * $Id: StringTransformerTest.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.utils/src/test/java/nc/ird/cantharella/utils/StringTransformerTest.java $
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

import nc.ird.cantharella.utils.StringTransformer;
import org.junit.Assert;
import org.junit.Test;

/**
 * StringTransformer test
 * 
 * @author Mickael Tricot
 * @author Adrien Cheype
 */
public class StringTransformerTest {

    /**
     * Capitalize test
     */
    @Test
    public void capitalize() {
        Assert.assertNull(new StringTransformer(null).capitalize().toString());
        Assert.assertEquals("", new StringTransformer("").capitalize().toString());
        Assert.assertEquals("AAa", new StringTransformer("AAa").capitalize().toString());
        Assert.assertEquals("AAa BBb", new StringTransformer("aAa bBb").capitalize().toString());
        Assert.assertEquals("AAa-bBb", new StringTransformer("aAa-bBb").capitalize().toString());
    }

    /**
     * Capitalize fully test
     */
    @Test
    public void capitalizeFully() {
        Assert.assertNull(new StringTransformer(null).capitalizeFully().toString());
        Assert.assertEquals("", new StringTransformer("").capitalizeFully().toString());
        Assert.assertEquals("Aaa", new StringTransformer("AAa").capitalizeFully().toString());
        Assert.assertEquals("Aaa Bbb", new StringTransformer("aAa bBb").capitalizeFully().toString());
        Assert.assertEquals("Aaa-bbb", new StringTransformer("aAa-bBb").capitalizeFully().toString());
    }

    /**
     * Init test
     */
    @Test
    public void init() {
        Assert.assertNull(new StringTransformer(null).toString());
        Assert.assertEquals("", new StringTransformer("").toString());
        Assert.assertEquals("toto", new StringTransformer("toto").toString());

        StringTransformer st = new StringTransformer(null);
        Assert.assertEquals(st, st.trim());
    }

    /**
     * Replace accents test
     */
    @Test
    public void replaceAccents() {
        Assert.assertNull(new StringTransformer(null).replaceAccents().toString());
        Assert.assertEquals("", new StringTransformer("").replaceAccents().toString());
        Assert.assertEquals("AbCdEfGhIjKlMnOpQrStUvWxYz", new StringTransformer("AbCdEfGhIjKlMnOpQrStUvWxYz")
                .replaceAccents().toString());
        Assert.assertEquals("&e'(-e_ca)=u%*µ^¨$£}¤]@^`|[{#~", new StringTransformer("&é'(-è_çà)=ù%*µ^¨$£}¤]@^`|[{#~")
                .replaceAccents().toString());
    }

    /**
     * Replace consecutive whitespaces test
     */
    @Test
    public void replaceConsecutiveWhitespaces() {
        Assert.assertNull(new StringTransformer(null).replaceConsecutiveWhitespaces().toString());
        Assert.assertEquals("", new StringTransformer("").replaceConsecutiveWhitespaces().toString());
        Assert.assertEquals(" ", new StringTransformer(" ").replaceConsecutiveWhitespaces().toString());
        Assert.assertEquals(" ", new StringTransformer("  ").replaceConsecutiveWhitespaces().toString());
        Assert.assertEquals(" toto titi ", new StringTransformer("  toto    titi  ").replaceConsecutiveWhitespaces()
                .toString());
    }

    /**
     * Replace non alpha test
     */
    @Test
    public void replaceNonAlpha() {
        Assert.assertNull(new StringTransformer(null).toString());
        Assert.assertEquals("", new StringTransformer("").replaceNonAlpha().toString());
        Assert.assertEquals(" ", new StringTransformer(" ").replaceNonAlpha().toString());
        Assert.assertEquals("e", new StringTransformer("e").replaceNonAlpha().toString());
        Assert.assertEquals(" ", new StringTransformer("é").replaceNonAlpha().toString());
        Assert.assertEquals("tot  ", new StringTransformer("totô2").replaceNonAlpha().toString());
    }

    /**
     * To lower case test
     */
    @Test
    public void toLowerCase() {
        Assert.assertNull(new StringTransformer(null).toLowerCase().toString());
        Assert.assertEquals("", new StringTransformer("").toLowerCase().toString());
        Assert.assertEquals("a", new StringTransformer("A").toLowerCase().toString());
        Assert.assertEquals("aaaââ", new StringTransformer("aAaÂâ").toLowerCase().toString());
    }

    /**
     * To upper case test
     */
    @Test
    public void toUpperCase() {
        Assert.assertNull(new StringTransformer(null).toUpperCase().toString());
        Assert.assertEquals("", new StringTransformer("").toUpperCase().toString());
        Assert.assertEquals("A", new StringTransformer("a").toUpperCase().toString());
        Assert.assertEquals("AAAÂÂ", new StringTransformer("aAaÂâ").toUpperCase().toString());
    }

    /**
     * Trim test
     */
    @Test
    public void trim() {
        Assert.assertNull(new StringTransformer(null).trim().toString());
        Assert.assertEquals("", new StringTransformer("").trim().toString());
        Assert.assertEquals("", new StringTransformer("  ").trim().toString());
        Assert.assertEquals("toto", new StringTransformer("  toto  ").trim().toString());
        Assert.assertEquals("toto   titi", new StringTransformer("  toto   titi  ").trim().toString());
    }

    /**
     * Trim to null test
     */
    @Test
    public void trimToNull() {
        Assert.assertNull(new StringTransformer(null).trimToNull().toString());
        Assert.assertNull(new StringTransformer("").trimToNull().toString());
        Assert.assertNull(new StringTransformer("  ").trimToNull().toString());
        Assert.assertEquals("toto", new StringTransformer("  toto  ").trimToNull().toString());
        Assert.assertEquals("toto   titi", new StringTransformer("  toto   titi  ").trim().toString());
    }
}
