/*
 * #%L
 * Cantharella :: Utils
 * $Id: AssertTools.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.utils/src/main/java/nc/ird/cantharella/utils/AssertTools.java $
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

import java.util.Arrays;
import java.util.Collection;

/**
 * Assertion tools. You need to have -ea or -enableassertions in the JVM args.
 * 
 * @author Mickael Tricot
 * @author Adrien Cheype
 */
public final class AssertTools {

    /** Error message for wrong class argument */
    private static final String CLASS = "Argument must be of type %s";

    /** Error message for not equals argument */
    private static final String EQUALS = "Argument must be equals to %s";

    /** Error message for not greater argument */
    private static final String GREATER = "Argument must be greater than %s";

    /** Error message for not greater or equals argument */
    private static final String GREATER_OR_EQUALS = "Argument must be greater than or equals to %s";

    /** Error message for "not in" argument */
    private static final String IN = "Argument must be in %s";

    /** Error message for not lower argument */
    private static final String LOWER = "Argument must be lower than %s";

    /** Error message for not lower or equals argument */
    private static final String LOWER_OR_EQUALS = "Argument must be lower than or equals to %s";

    /** Error message for empty argument */
    private static final String NOT_EMPTY = "Argument must not be empty";

    /** Error message for equals argument */
    private static final String NOT_EQUALS = "Argument must not be equals to %s";

    /** Error message for null argument */
    private static final String NOT_NULL = "Argument must not be null";

    /**
     * Assert that none of the string in an array is empty
     * 
     * @param strings Strings array
     */
    public static void assertArrayNotEmpty(String[] strings) {
        assertNotNull(strings);
        for (String string : strings) {
            assertNotEmpty(string);
        }
    }

    /**
     * Assert that none of the objects in an array is null
     * 
     * @param objects Objects array
     */
    public static void assertArrayNotNull(Object[] objects) {
        assertNotNull(objects);
        for (Object object : objects) {
            assertNotNull(object);
        }
    }

    /**
     * Assert that an object class/superclass/interface/superinterface
     * 
     * @param object Object
     * @param clazz Expected class/superclass/interface/superinterface
     */
    public static void assertClassOrInterface(Object object, Class<?> clazz) {
        assertNotNull(object);
        assertNotNull(clazz);
        assert clazz.isAssignableFrom(object.getClass()) : String.format(CLASS, clazz.getName());
    }

    /**
     * Assert that none of the objects in a Collection is null
     * 
     * @param objects Objects collection
     */
    public static void assertCollectionNotNull(Collection<?> objects) {
        assertNotNull(objects);
        for (Object object : objects) {
            assertNotNull(object);
        }
    }

    /**
     * Assert that a value is equals to an expected value
     * 
     * @param <N> Number type
     * @param value Value
     * @param expectedValue Expected value
     */
    public static <N extends Number> void assertEquals(N value, N expectedValue) {
        assert value.doubleValue() == expectedValue.doubleValue() : String.format(EQUALS, expectedValue);
    }

    /**
     * Assert that a value is greater than to a bound
     * 
     * @param <N> Number type
     * @param value Value
     * @param bound Bound
     */
    public static <N extends Number> void assertGreater(N value, N bound) {
        assert value.doubleValue() > bound.doubleValue() : String.format(GREATER, bound);
    }

    /**
     * Assert that a value is greater than or equals to a bound
     * 
     * @param <N> Number type
     * @param value Value
     * @param bound Bound
     */
    public static <N extends Number> void assertGreaterOrEquals(N value, N bound) {
        assert value.doubleValue() >= bound.doubleValue() : String.format(GREATER_OR_EQUALS, bound);
    }

    /**
     * Assert that an object is contains in a values array
     * 
     * @param <O> Object type
     * @param object Object
     * @param values Values collection
     */
    public static <O> void assertIn(O object, Collection<O> values) {
        assertNotNull(object);
        assertNotEmpty(values);
        assert values.contains(object) : String.format(IN, object);
    }

    /**
     * Assert that an object is contains in a values array
     * 
     * @param <O> Object type
     * @param object Object
     * @param values Values array
     */
    @SafeVarargs
    public static <O> void assertIn(O object, O... values) {
        assertNotEmpty(values);
        assertIn(object, Arrays.asList(values));
    }

    /**
     * Assert that a value is lower than to a bound
     * 
     * @param <N> Number type
     * @param value Value
     * @param bound Bound
     */
    public static <N extends Number> void assertLower(N value, N bound) {
        assert value.doubleValue() < bound.doubleValue() : String.format(LOWER, bound);
    }

    /**
     * Assert that a value is lower than or equals to a bound
     * 
     * @param <N> Number type
     * @param value Value
     * @param bound Bound
     */
    public static <N extends Number> void assertLowerOrEquals(N value, N bound) {
        assert value.doubleValue() <= bound.doubleValue() : String.format(LOWER_OR_EQUALS, bound);
    }

    /**
     * Assert that an array is not null or empty
     * 
     * @param collection Collection
     */
    public static void assertNotEmpty(Collection<?> collection) {
        assertNotNull(collection);
        assert !collection.isEmpty() : NOT_EMPTY;
    }

    /**
     * Assert that a collection is not null or empty
     * 
     * @param array Array
     */
    public static void assertNotEmpty(Object[] array) {
        assertNotNull(array);
        assert array.length > 0 : NOT_EMPTY;
    }

    /**
     * Assert that a string is not null or empty
     * 
     * @param string String
     */
    public static void assertNotEmpty(String string) {
        assertNotNull(string);
        assert !string.isEmpty() : NOT_EMPTY;
    }

    /**
     * Assert that a value is not equals to an unexpected value
     * 
     * @param <N> Number type
     * @param value Value
     * @param unexpectedValue Unxpected value
     */
    public static <N extends Number> void assertNotEquals(N value, N unexpectedValue) {
        assert value.doubleValue() != unexpectedValue.doubleValue() : String.format(NOT_EQUALS, unexpectedValue);
    }

    /**
     * Assert that a value is not negative
     * 
     * @param <N> Number type
     * @param value Value
     */
    public static <N extends Number> void assertNotNegative(N value) {
        assertGreaterOrEquals(value, 0);
    }

    /**
     * Assert that an object is not null
     * 
     * @param object Object
     */
    public static void assertNotNull(Object object) {
        assert object != null : NOT_NULL;
    }

    /**
     * Assert that a value is positive
     * 
     * @param <N> Number type
     * @param value Value
     */
    public static <N extends Number> void assertPositive(N value) {
        assertGreater(value, 0);
    }

    /**
     * Constructor (prevents instantiation)
     */
    private AssertTools() {
        //
    }
}
