/*
 * #%L
 * Cantharella :: Utils
 * $Id: CollectionToolsTest.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.utils/src/test/java/nc/ird/cantharella/utils/CollectionToolsTest.java $
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
import nc.ird.cantharella.utils.CollectionTools;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import nc.ird.cantharella.utils.BeanTools.AccessType;
import nc.ird.cantharella.utils.BeanToolsTest.Bean1;
import nc.ird.cantharella.utils.BeanToolsTest.Bean2;

import org.junit.Assert;
import org.junit.Test;

/**
 * CollectionTools test
 * 
 * @author Mickael Tricot
 * @author Adrien Cheype
 */
public final class CollectionToolsTest {

    /** beans collection for tests */
    public AbstractList<Bean1> beansCol = new ArrayList<Bean1>();

    /**
     * Constructor
     */
    public CollectionToolsTest() {
        init();
    }

    /**
     * initialisation of tests
     */
    private void init() {
        BeanToolsTest beanTest = new BeanToolsTest();

        Bean1 b1 = beanTest.new Bean1();
        Bean2 b12 = beanTest.new Bean2();
        b12.setName2("name12");
        b1.setName1("name");
        b1.setBean2(b12);

        Bean1 b2 = beanTest.new Bean1();
        Bean2 b22 = beanTest.new Bean2();
        b22.setName2("name22");
        b2.setName1("name");
        b2.setBean2(b22);

        Bean1 b3 = beanTest.new Bean1();
        Bean2 b32 = beanTest.new Bean2();
        b32.setName2(null);
        b3.setName1("name");
        b3.setBean2(b32);

        beansCol.add(b1);
        beansCol.add(b2);
        beansCol.add(b3);
    }

    /**
     * containsOnlyValue test
     */
    @Test
    public void containsOnlyValue() {
        Collection<String> col1 = new ArrayList<String>(Arrays.asList(new String[] { "fff", "fff" }));
        Collection<String> col2 = new ArrayList<String>();
        Collection<String> col3 = new ArrayList<String>(Arrays.asList(new String[] { "ab", "fff" }));
        Collection<String> col4 = new ArrayList<String>(Arrays.asList(new String[] { null, null }));
        Collection<String> col5 = new ArrayList<String>(Arrays.asList(new String[] { null, "fff" }));
        Collection<String> col6 = new ArrayList<String>(Arrays.asList(new String[] { "fff" }));
        Collection<Integer> col7 = new ArrayList<Integer>(Arrays.asList(new Integer[] { 5, 5, 5, 5, 5 }));

        Assert.assertTrue(CollectionTools.containsOnlySameValue(col1, "fff"));
        Assert.assertFalse(CollectionTools.containsOnlySameValue(col2, ""));
        Assert.assertFalse(CollectionTools.containsOnlySameValue(col3, "fff"));
        Assert.assertTrue(CollectionTools.containsOnlySameValue(col4, null));
        Assert.assertFalse(CollectionTools.containsOnlySameValue(col5, null));
        Assert.assertTrue(CollectionTools.containsOnlySameValue(col6, "fff"));
        Assert.assertTrue(CollectionTools.containsOnlySameValue(col7, 5));
    }

    /**
     * Intersect test
     */
    @Test
    public void intersect() {
        Set<Integer> s1 = new HashSet<Integer>();
        Set<Integer> s2 = new HashSet<Integer>();
        Assert.assertTrue(CollectionTools.intersect(null, null).isEmpty());
        Assert.assertTrue(CollectionTools.intersect(s1, null).isEmpty());
        Assert.assertTrue(CollectionTools.intersect(null, s2).isEmpty());
        Assert.assertTrue(CollectionTools.intersect(s1, s2).isEmpty());
        s1.add(Integer.valueOf(0));
        Assert.assertTrue(CollectionTools.intersect(s1, s2).isEmpty());
        s2.add(Integer.valueOf(1));
        Assert.assertTrue(CollectionTools.intersect(s1, s2).isEmpty());
        s1.add(Integer.valueOf(2));
        s2.add(Integer.valueOf(2));
        Set<Integer> intersect = CollectionTools.intersect(s1, s2);
        Assert.assertEquals(1, intersect.size());
        Assert.assertTrue(intersect.contains(Integer.valueOf(2)));
        s1.add(Integer.valueOf(3));
        s2.add(Integer.valueOf(3));
        intersect = CollectionTools.intersect(s1, s2);
        Assert.assertEquals(2, intersect.size());
        Assert.assertTrue(intersect.contains(Integer.valueOf(2)));
        Assert.assertTrue(intersect.contains(Integer.valueOf(3)));
        intersect = CollectionTools.intersect(s2, s1);
        Assert.assertEquals(2, intersect.size());
        Assert.assertTrue(intersect.contains(Integer.valueOf(2)));
        Assert.assertTrue(intersect.contains(Integer.valueOf(3)));
    }

    /**
     * setter test
     */
    @Test
    public void setter() {
        Collection<Integer> toWrite = new ArrayList<Integer>();
        Collection<Integer> toRead = null;
        toWrite.add(0);
        CollectionTools.setter(toWrite, toRead);
        Assert.assertTrue(toWrite.isEmpty());
        toWrite.add(0);
        toRead = new ArrayList<Integer>();
        CollectionTools.setter(toWrite, toRead);
        Assert.assertTrue(toWrite.isEmpty());
        toWrite.add(0);
        toRead.add(1);
        CollectionTools.setter(toWrite, toRead);
        Assert.assertArrayEquals(toRead.toArray(), toWrite.toArray());
    }

    /**
     * toStringComparatorTest
     */
    @Test
    public void toStringComparator() {
        List<String> list = new ArrayList<String>();
        list.add("t");
        list.add(null);
        list.add("");
        list.add("z");
        list.add("b");
        list.add("   ");
        Collections.sort(list, StringTools.createStringComparator());
        Assert.assertArrayEquals(list.toArray(new String[0]), new String[] { null, "", "   ", "b", "t", "z" });
    }

    /**
     * containsFieldWithValue tests
     */
    @Test
    public void containsFieldWithValue() {
        Assert.assertTrue(CollectionTools.containsWithValue(beansCol, "name1", AccessType.GETTER, "name"));
        Assert.assertTrue(CollectionTools.containsWithValue(beansCol, "bean2.name2", AccessType.GETTER, "name12"));
        Assert.assertTrue(CollectionTools.containsWithValue(beansCol, "bean2.name2", AccessType.GETTER, "name22"));
        Assert.assertFalse(CollectionTools.containsWithValue(beansCol, "name1", AccessType.GETTER, "XXX"));
    }

    /**
     * containsFieldWithValue tests
     */
    @Test
    public void countFieldWithValue() {
        Assert.assertSame(CollectionTools.countWithValue(beansCol, "name1", AccessType.GETTER, "name"), 3);
        Assert.assertSame(CollectionTools.countWithValue(beansCol, "bean2.name2", AccessType.GETTER, "name12"), 1);
        Assert.assertSame(CollectionTools.countWithValue(beansCol, "bean2.name2", AccessType.GETTER, "name22"), 1);
        Assert.assertSame(CollectionTools.countWithValue(beansCol, "name1", AccessType.GETTER, "XXX"), 0);
    }

    /**
     * removeWithValue tests
     */
    @Test
    public void removeWithValue() {
        Assert.assertTrue(CollectionTools.removeWithValue(beansCol, "name1", AccessType.GETTER, "name"));
        Assert.assertSame(beansCol.size(), 0);
        beansCol.clear();
        init();

        Assert.assertTrue(CollectionTools.removeWithValue(beansCol, "bean2.name2", AccessType.GETTER, "name12"));
        Assert.assertSame(beansCol.size(), 2);
        beansCol.clear();
        init();

        Assert.assertFalse(CollectionTools.removeWithValue(beansCol, "name1", AccessType.GETTER, "XXX"));
    }

    /**
     * removeWithValue tests
     */
    @Test
    public void removeAllWithValue() {
        Assert.assertTrue(CollectionTools.removeAllWithValue(beansCol, "name1", AccessType.GETTER,
                Arrays.asList(new String[] { "name" })));
        Assert.assertSame(beansCol.size(), 0);
        beansCol.clear();
        init();

        Assert.assertTrue(CollectionTools.removeAllWithValue(beansCol, "bean2.name2", AccessType.GETTER,
                Arrays.asList(new String[] { "name12", "name22" })));
        Assert.assertSame(beansCol.size(), 1);
        beansCol.clear();
        init();

        Assert.assertFalse(CollectionTools.removeAllWithValue(beansCol, "name1", AccessType.GETTER,
                Arrays.asList(new String[] { "XXX", "YYY", "ZZZ" })));
    }

    /**
     * valuesFromList tests
     */
    @Test
    public void valuesFromList() {
        Assert.assertEquals(CollectionTools.valuesFromList(beansCol, "name1", AccessType.GETTER),
                Arrays.asList(new String[] { "name", "name", "name" }));
        Assert.assertEquals(CollectionTools.valuesFromList(beansCol, "bean2.name2", AccessType.GETTER),
                Arrays.asList(new String[] { "name12", "name22", null }));
    }
}
