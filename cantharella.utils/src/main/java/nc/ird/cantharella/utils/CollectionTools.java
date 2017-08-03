/*
 * #%L
 * Cantharella :: Utils
 * $Id: CollectionTools.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.utils/src/main/java/nc/ird/cantharella/utils/CollectionTools.java $
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import nc.ird.cantharella.utils.BeanTools.AccessType;

import org.apache.commons.lang3.ObjectUtils;

/**
 * Tools for collections
 * 
 * @author Mickael Tricot
 * @author Adrien Cheype
 */
public final class CollectionTools {

    /**
     * Test if all collection's values contain always the same value
     * 
     * @param <T> Collection type
     * @param collection Collection to parse
     * @param value Value which is tested for the identity (may be null)
     * @return The answer
     */
    public static <T> boolean containsOnlySameValue(Collection<T> collection, T value) {
        AssertTools.assertNotNull(collection);

        if (collection.isEmpty()) {
            return false;
        }

        boolean hasSame = true;
        Iterator<T> itCol = collection.iterator();
        while (hasSame && itCol.hasNext()) {
            T curVal = itCol.next();
            if (!ObjectUtils.equals(curVal, value)) {
                hasSame = false;
            }
        }
        return hasSame;
    }

    /**
     * Intersection of two sets
     * 
     * @param <T> Set type
     * @param s1 Set 1
     * @param s2 Set 2
     * @return Intersection
     */
    public static <T> Set<T> intersect(Set<T> s1, Set<T> s2) {
        Set<T> intersect = s1 == null ? new HashSet<T>() : new HashSet<T>(s1);
        intersect.retainAll(s2);
        return intersect;
    }

    /**
     * Setter for collections, preserving the instance
     * 
     * @param <T> Collection type
     * @param collectionToWrite Collection to write
     * @param collectionToRead Collection to read
     */
    public static <T> void setter(Collection<T> collectionToWrite, Collection<T> collectionToRead) {
        AssertTools.assertNotNull(collectionToWrite);
        collectionToWrite.clear();
        if (collectionToRead != null) {
            collectionToWrite.addAll(collectionToRead);
        }
    }

    /**
     * Check in a collection of beans if a property has a specific value
     * 
     * @param collection The collection to parse
     * @param pathToProperty The path to the tested bean property (for each bean in the collection), example :
     *            beanX.beanY.propZ
     * @param accessType The access type to reach each property value
     * @param value The value to find
     * @return true if the value is found, otherwise false
     */
    public static boolean containsWithValue(Collection<? extends Object> collection, String pathToProperty,
            AccessType accessType, Object value) {
        AssertTools.assertNotNull(collection);
        AssertTools.assertNotEmpty(pathToProperty);
        for (Object curBean : collection) {
            Object curVal = BeanTools.getValueFromPath(curBean, accessType, pathToProperty);
            if (curVal.equals(value)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Count in a collection of beans the number of beans which have a specific value
     * 
     * @param collection The collection to parse
     * @param pathToProperty The path to the tested bean property, example : beanX.beanY.propZ
     * @param accessType The access type to reach each property value
     * @param value The value to find
     * @return The count result
     */
    public static int countWithValue(Collection<? extends Object> collection, String pathToProperty,
            AccessType accessType, Object value) {
        AssertTools.assertNotNull(collection);
        AssertTools.assertNotEmpty(pathToProperty);
        int count = 0;
        for (Object curBean : collection) {
            Object curVal = BeanTools.getValueFromPath(curBean, accessType, pathToProperty);
            if (value.equals(curVal)) {
                count++;
            }
        }
        return count;
    }

    /**
     * Get from a beans collection the first bean which have a property with a specific value. If the value is not
     * found, return null
     * 
     * @param <T> The beans class in the collection
     * @param collection The collection to parse
     * @param pathToProperty The path to the tested bean property, example : beanX.beanY.propZ
     * @param accessType The access type to reach each property value
     * @param value The value to find
     * @return The bean with the specific value, null if not found
     */
    public static <T extends Object> T findWithValue(List<T> collection, String pathToProperty, AccessType accessType,
            Object value) {
        AssertTools.assertNotNull(collection);
        AssertTools.assertNotEmpty(pathToProperty);
        for (T curBean : collection) {
            Object curVal = BeanTools.getValueFromPath(curBean, accessType, pathToProperty);
            if (value.equals(curVal)) {
                return curBean;
            }
        }
        return null;
    }

    /**
     * Remove in a collection of beans the beans which have a specific value. If the value is found, remove it and
     * return true.
     * 
     * @param collection The collection to parse
     * @param pathToProperty The path to the tested bean property, example : beanX.beanY.propZ
     * @param accessType The access type to reach each property value
     * @param value The value to find
     * @return true if found, false otherwise
     */
    public static boolean removeWithValue(Collection<? extends Object> collection, String pathToProperty,
            AccessType accessType, Object value) {
        AssertTools.assertNotNull(collection);
        AssertTools.assertNotEmpty(pathToProperty);
        boolean hasDeleted = false;

        Iterator<? extends Object> itList = collection.iterator();
        while (itList.hasNext()) {
            Object curBean = itList.next();
            Object curVal = BeanTools.getValueFromPath(curBean, accessType, pathToProperty);
            if (value.equals(curVal)) {
                itList.remove();
                hasDeleted = true;
            }
        }
        return hasDeleted;
    }

    /**
     * Remove in a collection of beans the beans which have a specifics values. If on these values are found, remove
     * them and return true.
     * 
     * @param collection The collection to parse
     * @param pathToProperty The path to the tested bean property, example : beanX.beanY.propZ
     * @param accessType The access type to reach each property value
     * @param values The values to find
     * @return true if at least one value is found, false otherwise
     */
    public static boolean removeAllWithValue(Collection<? extends Object> collection, String pathToProperty,
            AccessType accessType, Collection<? extends Object> values) {
        AssertTools.assertNotNull(collection);
        AssertTools.assertNotEmpty(pathToProperty);
        boolean hasDeleted = false;

        Iterator<? extends Object> itList = collection.iterator();
        while (itList.hasNext()) {
            Object curBean = itList.next();
            Object curVal = BeanTools.getValueFromPath(curBean, accessType, pathToProperty);
            if (curVal != null && values.contains(curVal)) {
                itList.remove();
                hasDeleted = true;
            }
        }
        return hasDeleted;
    }

    /**
     * Build a list from values properties of a bean list. Ex : From a beans list which contains the property "name",
     * you can build the list of these names.
     * 
     * @param collection The beans list to parse
     * @param pathToProperty The path to the bean property, example : beanX.beanY.propZ
     * @param accessType The access type to reach each property value
     * @return These values
     */
    public static List<Object> valuesFromList(Collection<? extends Object> collection, String pathToProperty,
            AccessType accessType) {
        AssertTools.assertNotNull(collection);
        AssertTools.assertNotEmpty(pathToProperty);

        List<Object> values = new ArrayList<Object>();
        for (Object curBean : collection) {
            Object curVal = BeanTools.getValueFromPath(curBean, accessType, pathToProperty);
            values.add(curVal);
        }
        return values;
    }

    /**
     * Constructor (prevents instantiation)
     */
    private CollectionTools() {
        //
    }

}
