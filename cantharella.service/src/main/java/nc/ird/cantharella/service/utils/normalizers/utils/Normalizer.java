/*
 * #%L
 * Cantharella :: Service
 * $Id: Normalizer.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.service/src/main/java/nc/ird/cantharella/service/utils/normalizers/utils/Normalizer.java $
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
package nc.ird.cantharella.service.utils.normalizers.utils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import nc.ird.cantharella.data.exceptions.UnexpectedException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Data normalizer
 * 
 * @author Mickael Tricot
 * @author Adrien Cheype
 * @param <O> Data type
 */
public abstract class Normalizer<O> {

    /** Logger */
    private static final Logger LOG = LoggerFactory.getLogger(Normalizer.class);

    /** Normalizers cache: normalizerClass + normalizer */
    private static final Map<Class<? extends Normalizer<?>>, Normalizer<?>> NORMALIZERS = Collections
            .synchronizedMap(new HashMap<Class<? extends Normalizer<?>>, Normalizer<?>>());

    /**
     * Normalize data
     * 
     * @param <T> Data type
     * @param normalizerClass Normalizer class
     * @param object Data object
     * @return Normalized data
     */
    @SuppressWarnings("unchecked")
    public static <T> T normalize(Class<? extends Normalizer<T>> normalizerClass, T object) {
        Normalizer<T> normalizer;
        synchronized (NORMALIZERS) {
            normalizer = (Normalizer<T>) NORMALIZERS.get(normalizerClass);
            if (normalizer == null) {
                try {
                    normalizer = normalizerClass.newInstance();
                } catch (InstantiationException e) {
                    LOG.error(e.getMessage(), e);
                    throw new UnexpectedException(e);
                } catch (IllegalAccessException e) {
                    LOG.error(e.getMessage(), e);
                    throw new UnexpectedException(e);
                }
                NORMALIZERS.put(normalizerClass, normalizer);
            }
        }
        return normalizer.normalize(object);
    }

    /**
     * Normalize data
     * 
     * @param object Data object
     * @return Normalized data object
     */
    protected abstract O normalize(O object);
}
