/*
 * #%L
 * Cantharella :: Utils
 * $Id: GenericsTools.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.utils/src/main/java/nc/ird/cantharella/utils/GenericsTools.java $
 * %%
 * Copyright (C) 2009 - 2013 IRD (Institut de Recherche pour le Developpement) and by respective authors (see below)
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

/**
 * Tools for generics classes
 * 
 * @author Mickael Tricot
 * @author Adrien Cheype
 */
public final class GenericsTools {

    /**
     * Object cast
     * 
     * @param <I> Input type
     * @param <O> Output type
     * @param i Input object
     * @return Output object
     */
    @SuppressWarnings("unchecked")
    public static <I, O extends I> O cast(I i) {
        return (O) i;
    }

    /**
     * Constructor (prevents instantiation)
     */
    private GenericsTools() {
        //
    }
}
