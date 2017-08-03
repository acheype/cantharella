/*
 * #%L
 * Cantharella :: Utils
 * $Id: AssertToolsTest.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.utils/src/test/java/nc/ird/cantharella/utils/AssertToolsTest.java $
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

import nc.ird.cantharella.utils.AssertTools;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;

/**
 * ArgumentTools test
 * 
 * @author Mickael Tricot
 */
public final class AssertToolsTest {

    /**
     * assert array not empty : false (empty value)
     */
    @Test(expected = AssertionError.class)
    public void assertArrayNotEmptyFalse1() {
        AssertTools.assertArrayNotEmpty(new String[] { "toto", "" });
    }

    /**
     * assert array not empty : false (null value)
     */
    @Test(expected = AssertionError.class)
    public void assertArrayNotEmptyFalse2() {
        AssertTools.assertArrayNotEmpty(new String[] { null, "toto" });
    }

    /**
     * assert array not empty : true
     */
    public void assertArrayNotEmptyTrue() {
        AssertTools.assertArrayNotEmpty(new String[] {});
        AssertTools.assertArrayNotEmpty(new String[] { "toto", "titi" });
    }

    /**
     * assert array not null : false
     */
    @Test(expected = AssertionError.class)
    public void assertArrayNotNullFalse() {
        AssertTools.assertArrayNotNull(new Integer[] { Integer.valueOf(0), null, Integer.valueOf(2) });
    }

    /**
     * assert array not null : true
     */
    public void assertArrayNotNullTrue() {
        AssertTools.assertArrayNotNull(new Integer[] { Integer.valueOf(0), Integer.valueOf(1), Integer.valueOf(2) });
    }

    /**
     * assertClassOrInterface test: error
     */
    @Test(expected = AssertionError.class)
    public void assertClassOrInterfaceFalse() {
        AssertTools.assertClassOrInterface("", AssertToolsTest.class);
    }

    /**
     * assert class or interface: false class
     */
    @Test(expected = AssertionError.class)
    public void assertClassOrInterfaceFalseClass() {
        AssertTools.assertClassOrInterface("", AssertToolsTest.class);
    }

    /**
     * assert class or interface: false interface
     */
    @Test(expected = AssertionError.class)
    public void assertClassOrInterfaceFalseInterface() {
        AssertTools.assertClassOrInterface("", Iterable.class);
    }

    /**
     * assertClassOrInterface test: null class
     */
    @Test(expected = AssertionError.class)
    public void assertClassOrInterfaceNullClass() {
        AssertTools.assertClassOrInterface(this, null);
    }

    /**
     * assertClassOrInterface test: null object
     */
    @Test(expected = AssertionError.class)
    public void assertClassOrInterfaceNullObject() {
        AssertTools.assertClassOrInterface(null, AssertToolsTest.class);
    }

    /**
     * assertClassOrInterface test: success
     */
    @Test
    public void assertClassOrInterfaceTrue() {
        AssertTools.assertClassOrInterface(this, AssertToolsTest.class);
        AssertTools.assertClassOrInterface(this, Object.class);
        AssertTools.assertClassOrInterface(Integer.valueOf(0), Number.class);
        AssertTools.assertClassOrInterface("", Serializable.class);
    }

    /**
     * assert collection not null : not null
     */
    @Test
    public void assertCollectionNotNullNotNull() {
        AssertTools.assertCollectionNotNull(Arrays.asList(new String[] {}));
        AssertTools.assertCollectionNotNull(Arrays.asList(new String[] { "" }));
    }

    /**
     * assert collection not null : null
     */
    @Test(expected = AssertionError.class)
    public void assertCollectionNotNullNull() {
        AssertTools.assertCollectionNotNull(Arrays.asList(new String[] { null }));
    }

    /**
     * assertEquals test: equals
     */
    @Test
    public void assertEqualsEquals() {
        AssertTools.assertEquals(-1, -1);
        AssertTools.assertEquals(0, 0);
        AssertTools.assertEquals(1, 1);
    }

    /**
     * assertEquals test: equals
     */
    @Test(expected = AssertionError.class)
    public void assertEqualsNotEquals() {
        AssertTools.assertEquals(-1, 0);
    }

    /**
     * assertGreaterOrEquals test: equals
     */
    @Test
    public void assertGreateOrEqualsEquals() {
        AssertTools.assertGreaterOrEquals(0, 0);
    }

    /**
     * assertGreaterOrEquals test: greater
     */
    @Test
    public void assertGreateOrEqualsGreater() {
        AssertTools.assertGreaterOrEquals(1, 0);
    }

    /**
     * assertGreaterOrEquals test: equals
     */
    @Test(expected = AssertionError.class)
    public void assertGreateOrEqualsLower() {
        AssertTools.assertGreaterOrEquals(-1, 0);
    }

    /**
     * AssertIn test: false
     */
    @Test(expected = AssertionError.class)
    public void assertInArrayFalse() {
        AssertTools.assertIn("b", "a", "c");
    }

    /**
     * AssertIn test: false
     */
    @Test(expected = AssertionError.class)
    public void assertInCollectionFalse() {
        AssertTools.assertIn("b", Arrays.asList(new String[] { "a", "c" }));
    }

    /**
     * AssertIn test: true
     */
    @Test
    public void assertInTrue() {
        AssertTools.assertIn("b", "b");
        AssertTools.assertIn("b", "a", "b");
        AssertTools.assertIn("b", Arrays.asList(new String[] { "b" }));
        AssertTools.assertIn("b", Arrays.asList(new String[] { "a", "b" }));
    }

    /**
     * Assert lower test: equals
     */
    @Test(expected = AssertionError.class)
    public void assertLowerEquals() {
        AssertTools.assertLower(0, 0);
    }

    /**
     * Assert lower test: lower
     */
    @Test
    public void assertLowerLower() {
        AssertTools.assertLower(-1, 0);
    }

    /**
     * Assert lower or equals: lower or equals
     */
    @Test
    public void assertLowerOrEqualsLowerOrEquals() {
        AssertTools.assertLowerOrEquals(-1, 0);
        AssertTools.assertLowerOrEquals(0, 0);
    }

    /**
     * Assert lower or equals: upper
     */
    @Test(expected = AssertionError.class)
    public void assertLowerOrEqualsUpper() {
        AssertTools.assertLower(1, 0);
    }

    /**
     * Assert lower: upper
     */
    @Test(expected = AssertionError.class)
    public void assertLowerUpper() {
        AssertTools.assertLower(1, 0);
    }

    /**
     * assertNotEmpty test: array empty
     */
    @Test(expected = AssertionError.class)
    public void assertNotEmptyArrayEmpty() {
        AssertTools.assertNotEmpty(new Integer[] {});
    }

    /**
     * assertNotEmpty test: array not empty
     */
    @Test
    public void assertNotEmptyArrayNotEmpty() {
        AssertTools.assertNotEmpty(new String[] { "toto" });
    }

    /**
     * assertNotEmpty : Null test
     */
    @Test(expected = AssertionError.class)
    public void assertNotEmptyArrayNull() {
        AssertTools.assertNotEmpty((Integer[]) null);
    }

    /**
     * assertNotEmpty test: collection empty
     */
    @Test(expected = AssertionError.class)
    public void assertNotEmptyCollectionEmpty() {
        AssertTools.assertNotEmpty(new ArrayList<String>());
    }

    /**
     * assertNotEmpty test: collection not empty
     */
    @Test
    public void assertNotEmptyCollectionNotEmpty() {
        AssertTools.assertNotEmpty(Arrays.asList("toto"));
    }

    /**
     * assertNotEmptyCollection : Null test
     */
    @Test(expected = AssertionError.class)
    public void assertNotEmptyCollectionNull() {
        AssertTools.assertNotEmpty((Collection<?>) null);
    }

    /**
     * assertEmpty test: string empty
     */
    @Test(expected = AssertionError.class)
    public void assertNotEmptyStringEmpty() {
        AssertTools.assertNotEmpty("");
    }

    /**
     * assertEmpty test: string not empty
     */
    @Test
    public void assertNotEmptyStringNotEmpty() {
        AssertTools.assertNotEmpty("toto");
    }

    /**
     * assertNotEmpty test: string null
     */
    @Test(expected = AssertionError.class)
    public void assertNotEmptyStringNull() {
        AssertTools.assertNotEmpty((String) null);
    }

    /**
     * Assert not equals test: equals
     */
    @Test(expected = AssertionError.class)
    public void assertNotEqualsEquals() {
        AssertTools.assertNotEquals(0, 0);
    }

    /**
     * Assert not equals: upper
     */
    @Test
    public void assertNotEqualsNotEquals() {
        AssertTools.assertNotEquals(1, 0);
        AssertTools.assertNotEquals(-1, 0);
    }

    /**
     * Assert not negative: negative
     */
    @Test(expected = AssertionError.class)
    public void assertNotNegativeNegative() {
        AssertTools.assertNotNegative(-1);
    }

    /**
     * Assert not negative: not negative
     */
    @Test
    public void assertNotNegativeNotNegative() {
        AssertTools.assertNotNegative(1);
        AssertTools.assertNotNegative(0);
    }

    /**
     * assertNotNull test: not null
     */
    @Test
    public void assertNotNullNotNull() {
        AssertTools.assertNotNull("");
    }

    /**
     * assert not null: null
     */
    @Test(expected = AssertionError.class)
    public void assertNotNullNull() {
        AssertTools.assertNotNull(null);
    }

    /**
     * assert positive: negative
     */
    @Test(expected = AssertionError.class)
    public void assertPositiveNegative() {
        AssertTools.assertPositive(-1);
    }

    /**
     * assert positive: positive
     */
    @Test
    public void assertPositivePositive() {
        AssertTools.assertPositive(1);
    }

    /**
     * assert positive: zero
     */
    @Test(expected = AssertionError.class)
    public void assertPositiveZero() {
        AssertTools.assertPositive(0);
    }
}
