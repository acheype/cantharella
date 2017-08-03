/*
 * #%L
 * Cantharella :: Data
 * $Id: ModelValidator.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.data/src/main/java/nc/ird/cantharella/data/validation/utils/ModelValidator.java $
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
package nc.ird.cantharella.data.validation.utils;

import java.util.List;
import java.util.Locale;

import nc.ird.cantharella.data.model.utils.AbstractModel;

/**
 * Generic model validator
 * 
 * @author Mickael Tricot
 * @author Adrien Cheype
 */
public interface ModelValidator {

    /**
     * Debug properties validation for a model
     * 
     * @param <M> Model type
     * @param modelClass Model class
     * @param model Model
     */
    <M extends AbstractModel> void debug(Class<M> modelClass, M model);

    /**
     * Validate properties for a model
     * 
     * @param <M> Model type
     * @param model Model
     * @param locale Locale for error messages
     * @param properties Property names (all properties if empty array)
     * @return Constraint error messages (not null)
     */
    <M> List<String> validate(M model, Locale locale, String... properties);

    /**
     * Validate properties for a model
     * 
     * @param <M> Model type
     * @param model Model
     * @param locale Locale for error messages
     * @param properties Property names (all properties if empty array)
     * @return Constraint error messages (not null), pair of property + message
     */
    // <M> List<Pair<String, String>> validate(M model, Locale locale, String... properties);
}
