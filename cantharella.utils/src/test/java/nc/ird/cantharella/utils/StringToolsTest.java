/*
 * #%L
 * Cantharella :: Utils
 * $Id: StringToolsTest.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.utils/src/test/java/nc/ird/cantharella/utils/StringToolsTest.java $
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

import nc.ird.cantharella.utils.StringTools;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

/**
 * StringTools test
 * 
 * @author Mickael Tricot
 * @author Adrien Cheype
 */
public final class StringToolsTest {

    /**
     * coupleToString test
     */
    @Test
    public void couplesToString() {
        // Empty
        Map<String, Integer> couples = new HashMap<String, Integer>();
        String toString = StringTools.couplesToString(couples, " ", " / ");
        // One couple
        couples.put("Toto", 1);
        toString = StringTools.couplesToString(couples, " ", " / ");
        Assert.assertEquals("Toto 1", toString);
        // Two couples
        couples.put("Titi", 2);
        toString = StringTools.couplesToString(couples, " ", " / ");
        Assert.assertEquals("Toto 1 / Titi 2", toString);
    }

    /**
     * length test
     */
    @Test
    public void length() {
        Assert.assertEquals(-1, StringTools.length(null));
        Assert.assertEquals(0, StringTools.length(""));
        Assert.assertEquals(1, StringTools.length("a"));
    }

    /**
     * lineIterator test
     */
    @Test
    public void lineIterator() {
        // Empty
        StringBuilder lines = new StringBuilder();
        Iterator<String> i = StringTools.lineIterator(lines);
        Assert.assertFalse(i.hasNext());
        // One line
        lines.append("Toto");
        i = StringTools.lineIterator(lines);
        Assert.assertTrue(i.hasNext());
        Assert.assertEquals("Toto", i.next());
        Assert.assertFalse(i.hasNext());
        // Two lines with a blank one
        lines.append("\n ");
        i = StringTools.lineIterator(lines);
        Assert.assertTrue(i.hasNext());
        Assert.assertEquals("Toto", i.next());
        Assert.assertFalse(i.hasNext());
        // Three lines with a blank one
        lines.append("\nTiti");
        i = StringTools.lineIterator(lines);
        Assert.assertTrue(i.hasNext());
        Assert.assertEquals("Toto", i.next());
        Assert.assertTrue(i.hasNext());
        Assert.assertEquals("Titi", i.next());
        Assert.assertFalse(i.hasNext());
    }

    /**
     * replaceAccents test
     */
    @Test
    public void replaceAccents() {
        Assert.assertEquals(null, StringTools.replaceAccents(null));
        Assert.assertEquals("", StringTools.replaceAccents(""));
        Assert.assertEquals("AbCdEfGhIjKlMnOpQrStUvWxYz", StringTools.replaceAccents("AbCdEfGhIjKlMnOpQrStUvWxYz"));
        Assert.assertEquals("&e'(-e_ca)=u%*µ^¨$£}¤]@^`|[{#~",
                StringTools.replaceAccents("&é'(-è_çà)=ù%*µ^¨$£}¤]@^`|[{#~"));
    }

    /**
     * Replace consecutive whitespaces test
     */
    @Test
    public void replaceConsecutiveWhitespaces() {
        Assert.assertNull(StringTools.replaceConsecutiveWhitespaces(null));
        Assert.assertEquals("", StringTools.replaceConsecutiveWhitespaces(""));
        Assert.assertEquals(" ", StringTools.replaceConsecutiveWhitespaces(" "));
        Assert.assertEquals(" ", StringTools.replaceConsecutiveWhitespaces("  "));
        Assert.assertEquals(" toto titi ", StringTools.replaceConsecutiveWhitespaces("  toto    titi  "));
    }

    /**
     * Replace consecutive whitespaces by underscore test
     */
    @Test
    public void replaceConsecutiveWhitespacesByUnderscore() {
        Assert.assertNull(StringTools.replaceConsecutiveWhitespaces(null));
        Assert.assertEquals("", StringTools.replaceConsecutiveWhitespacesByUnderscore(""));
        Assert.assertEquals("_", StringTools.replaceConsecutiveWhitespacesByUnderscore(" "));
        Assert.assertEquals("_", StringTools.replaceConsecutiveWhitespacesByUnderscore(" "));
        Assert.assertEquals("_toto_titi_", StringTools.replaceConsecutiveWhitespacesByUnderscore("  toto    titi  "));
    }

    /**
     * Replace non alpha test
     */
    @Test
    public void replaceNonAlpha() {
        Assert.assertNull(StringTools.replaceNonAlpha(null));
        Assert.assertEquals("", StringTools.replaceNonAlpha(""));
        Assert.assertEquals(" ", StringTools.replaceNonAlpha(" "));
        Assert.assertEquals("e", StringTools.replaceNonAlpha("e"));
        Assert.assertEquals(" ", StringTools.replaceNonAlpha("é"));
        Assert.assertEquals("tot  ", StringTools.replaceNonAlpha("totô2"));
    }

    /**
     * truncate test
     */
    @Test
    public void truncate() {
        String truncation = "..";
        StringBuilder toTruncate = new StringBuilder();
        // Null
        Assert.assertNull(StringTools.truncate((StringBuilder) null, truncation));
        Assert.assertNull(StringTools.truncate((String) null, truncation));
        // Empty
        Assert.assertNotNull(StringTools.truncate(toTruncate, truncation));
        Assert.assertTrue(StringTools.truncate(toTruncate, truncation).isEmpty());
        // Without truncation
        toTruncate.append(".");
        Assert.assertEquals(".", StringTools.truncate(toTruncate, truncation));
        // With truncation -> empty
        toTruncate.append(".");
        Assert.assertNotNull(StringTools.truncate(toTruncate, truncation));
        Assert.assertTrue(StringTools.truncate(toTruncate, truncation).isEmpty());
        // With truncation -> !empty
        toTruncate.append(".");
        Assert.assertEquals(".", StringTools.truncate(toTruncate, truncation));
    }
}
