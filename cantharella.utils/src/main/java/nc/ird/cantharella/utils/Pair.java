/*
 * #%L
 * Cantharella :: Utils
 * $Id: Pair.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.utils/src/main/java/nc/ird/cantharella/utils/Pair.java $
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

import java.io.Serializable;
import java.util.Map.Entry;

import nc.ird.cantharella.utils.BeanTools.AccessType;

/**
 * Pair of objects
 * 
 * @author Mickael Tricot
 * @param <K> First element type
 * @param <V> Second element type
 */
public final class Pair<K, V> implements Entry<K, V>, Serializable {

    /** First element */
    private K key;

    /** Second element */
    private V value;

    /**
     * Constructor
     */
    public Pair() {
        //
    }

    /**
     * Constructor
     * 
     * @param key First element
     * @param value Second element
     */
    public Pair(K key, V value) {
        this();
        this.key = key;
        this.value = value;
    }

    /** @see java.lang.Object#equals(java.lang.Object) */
    @Override
    public boolean equals(Object obj) {
        return BeanTools.equals(this, obj, AccessType.GETTER, "key", "value");
    }

    /** {@inheritDoc} */
    @Override
    public K getKey() {
        return key;
    }

    /** {@inheritDoc} */
    @Override
    public V getValue() {
        return value;
    }

    /** @see java.lang.Object#hashCode() **/
    @Override
    public int hashCode() {
        return BeanTools.hashCode(this, getKey(), getValue());
    }

    /**
     * key setter
     * 
     * @param key key
     */
    public void setKey(K key) {
        this.key = key;
    }

    /** {@inheritDoc} */
    @Override
    public V setValue(V value) {
        V oldValue = this.value;
        this.value = value;
        return oldValue;
    }
}