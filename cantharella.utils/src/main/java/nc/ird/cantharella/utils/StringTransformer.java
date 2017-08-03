/*
 * #%L
 * Cantharella :: Utils
 * $Id: StringTransformer.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.utils/src/main/java/nc/ird/cantharella/utils/StringTransformer.java $
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

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;

/**
 * String transformer, chained, null-safe
 * 
 * @author Mickael Tricot
 */
public final class StringTransformer {

    /** String to transform */
    private String string;

    /**
     * Constructor
     * 
     * @param string String to transform
     */
    public StringTransformer(String string) {
        this.string = string;
    }

    /**
     * Capitalize
     * 
     * @return this
     * @see WordUtils
     */
    public StringTransformer capitalize() {
        string = StringUtils.isEmpty(string) ? string : WordUtils.capitalize(string);
        return this;
    }

    /**
     * Capitalize fully
     * 
     * @return this
     * @see WordUtils
     */
    public StringTransformer capitalizeFully() {
        string = StringUtils.isEmpty(string) ? string : WordUtils.capitalizeFully(string);
        return this;
    }

    /**
     * Replace accents
     * 
     * @return this
     * @see StringTools
     */
    public StringTransformer replaceAccents() {
        string = StringUtils.isEmpty(string) ? string : StringTools.replaceAccents(string);
        return this;
    }

    /**
     * Replace consecutive whitespaces
     * 
     * @return this
     * @see StringTools
     */
    public StringTransformer replaceConsecutiveWhitespaces() {
        string = StringUtils.isEmpty(string) ? string : StringTools.replaceConsecutiveWhitespaces(string);
        return this;
    }

    /**
     * Replace consecutive whitespaces by an underscore
     * 
     * @return this
     * @see StringTools
     */
    public StringTransformer replaceConsecutiveWhitespacesByUnderscore() {
        string = StringUtils.isEmpty(string) ? string : StringTools.replaceConsecutiveWhitespacesByUnderscore(string);
        return this;
    }

    /**
     * Replace non alpha
     * 
     * @return this
     * @see StringTools
     */
    public StringTransformer replaceNonAlpha() {
        string = StringUtils.isEmpty(string) ? string : StringTools.replaceNonAlpha(string);
        return this;
    }

    /**
     * To lower case
     * 
     * @return this
     * @see String
     */
    public StringTransformer toLowerCase() {
        string = StringUtils.isEmpty(string) ? string : string.toLowerCase();
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return string;
    }

    /**
     * To upper case
     * 
     * @return this
     * @see String
     */
    public StringTransformer toUpperCase() {
        string = StringUtils.isEmpty(string) ? string : string.toUpperCase();
        return this;
    }

    /**
     * Trim
     * 
     * @return this
     * @see String
     */
    public StringTransformer trim() {
        string = StringUtils.isEmpty(string) ? string : string.trim();
        return this;
    }

    /**
     * Trim to null
     * 
     * @return this
     * @see StringUtils
     */
    public StringTransformer trimToNull() {
        string = string == null ? null : StringUtils.trimToNull(string);
        return this;
    }
}
