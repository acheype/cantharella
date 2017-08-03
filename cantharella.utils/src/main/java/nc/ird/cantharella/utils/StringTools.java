/*
 * #%L
 * Cantharella :: Utils
 * $Id: StringTools.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.utils/src/main/java/nc/ird/cantharella/utils/StringTools.java $
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

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.LineIterator;
import org.apache.commons.lang3.StringUtils;

/**
 * Tools for strings
 * 
 * @author Mickael Tricot
 * @author Adrien Cheype
 */
public final class StringTools {

    /**
     * Create a string representing a list of couples (key + value)
     * 
     * @param <O1> Keys type
     * @param <O2> Values type
     * @param couples Couples (key + value)
     * @param separatorKeysValues Separator between keys and values
     * @param separatorCouples Separator between couples
     * @return String representing the list of couples, or null if empty
     */
    public static <O1, O2> String couplesToString(Map<O1, O2> couples, String separatorKeysValues,
            String separatorCouples) {
        AssertTools.assertNotNull(couples);
        AssertTools.assertNotNull(separatorKeysValues);
        AssertTools.assertNotNull(separatorCouples);
        StringBuilder builder = new StringBuilder();
        for (Entry<O1, O2> entry : couples.entrySet()) {
            builder.append(entry.getKey() + separatorKeysValues + entry.getValue() + separatorCouples);
        }
        return StringUtils.stripToNull(truncate(builder, separatorCouples));
    }

    /**
     * Display a list of objects
     * 
     * @param list The list to browsed
     * @param delimiter The delimiter between each object
     * @return List display
     */
    public static String listToString(List<? extends Object> list, String delimiter) {
        AssertTools.assertNotNull(list);
        AssertTools.assertNotEmpty(delimiter);
        StringBuilder builder = new StringBuilder();
        Iterator<? extends Object> itBeans = list.iterator();
        while (itBeans.hasNext()) {
            Object bean = itBeans.next();
            builder.append(bean.toString());
            if (itBeans.hasNext()) {
                builder.append(delimiter);
            }
        }
        return builder.toString();
    }

    /**
     * Null-safe string length
     * 
     * @param string String
     * @return String length or -1 if null
     */
    public static int length(String string) {
        return string == null ? -1 : string.length();
    }

    /**
     * Create a line iterator, without null or empty lines
     * 
     * @param string String
     * @return Iterator
     */
    public static Iterator<String> lineIterator(String string) {
        AssertTools.assertNotNull(string);
        List<String> lines = new ArrayList<String>();
        for (LineIterator i = new LineIterator(new StringReader(string)); i.hasNext();) {
            String line = StringUtils.trimToNull(i.nextLine());
            if (line != null) {
                lines.add(line);
            }
        }
        return lines.iterator();
    }

    /**
     * Create a line iterator, without null or empty lines
     * 
     * @param stringBuilder String
     * @return Iterator
     */
    public static Iterator<String> lineIterator(StringBuilder stringBuilder) {
        AssertTools.assertNotNull(stringBuilder);
        return lineIterator(stringBuilder.toString());
    }

    /**
     * Replace accented characters by their corresponding non-accented characters
     * 
     * @param string Accented string
     * @return Non-accented string
     */
    public static String replaceAccents(String string) {
        return StringUtils.stripAccents(string);
    }

    /**
     * Replace consecutive whitespaces by a single whitespace
     * 
     * @param string String to transform
     * @return String transformed
     */
    public static String replaceConsecutiveWhitespaces(String string) {
        return StringUtils.isEmpty(string) ? string : string.replaceAll("\\p{Space}+", " ");
    }

    /**
     * Replace consecutive whitespaces by an underscore
     * 
     * @param string String to transform
     * @return String transformed
     */
    public static String replaceConsecutiveWhitespacesByUnderscore(String string) {
        return StringUtils.isEmpty(string) ? string : string.replaceAll("\\p{Space}+", "_");
    }

    /**
     * Replace non alpha characters by a whitespace (accentuated characters are considered as non alpha characters)
     * 
     * @param string String to transform
     * @return String transformed
     */
    public static String replaceNonAlpha(String string) {
        return StringUtils.isEmpty(string) ? string : string.replaceAll("[^\\p{Alpha}]", " ");
    }

    /**
     * Truncate a string by the end
     * 
     * @param toTruncate String to truncate
     * @param truncation String to delete
     * @return Truncated string (empty if empty, null if null)
     */
    public static String truncate(String toTruncate, String truncation) {
        AssertTools.assertNotNull(truncation);
        String result = toTruncate;
        if (!StringUtils.isEmpty(toTruncate)
                && toTruncate.length() >= truncation.length()
                && truncation.equals(toTruncate.substring(toTruncate.length() - truncation.length(),
                        toTruncate.length()))) {
            result = toTruncate.substring(0, toTruncate.length() - truncation.length());
        }
        return result;
    }

    /**
     * Truncate a string by the end
     * 
     * @param toTruncate String to truncate
     * @param truncation String to delete
     * @return Truncated string (empty if empty, null if null)
     */
    public static String truncate(StringBuilder toTruncate, String truncation) {
        return toTruncate != null ? truncate(toTruncate.toString(), truncation) : null;
    }

    /**
     * Constructor (prevents from instantiation)
     */
    private StringTools() {
        //
    }

    /**
     * Create a comparator according to the toString method
     * 
     * @param <T> Type
     * @return Comparator
     */
    public static <T> Comparator<T> createStringComparator() {
        return new Comparator<T>() {
            @Override
            public int compare(T t1, T t2) {
                String t1String = t1 == null ? "" : t1.toString();
                String t2String = t2 == null ? "" : t2.toString();

                return t1String.compareTo(t2String);
            }
        };
    }
}
