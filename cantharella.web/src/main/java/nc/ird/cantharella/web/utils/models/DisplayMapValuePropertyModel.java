/*
 * #%L
 * Cantharella :: Web
 * $Id: DisplayMapValuePropertyModel.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.web/src/main/java/nc/ird/cantharella/web/utils/models/DisplayMapValuePropertyModel.java $
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
package nc.ird.cantharella.web.utils.models;

import java.util.Map;

import org.apache.wicket.model.PropertyModel;

/**
 * Property Model which give the string resulting value of applying model value on a map.
 * 
 * @author Alban Diguer
 * @author Adrien Cheype
 * @param <T> Type of the map key. The map is thus parametred by <T, String>
 */
public class DisplayMapValuePropertyModel<T> extends PropertyModel<Object> {

    /** map */
    private final Map<T, String> map;

    /**
     * Constructor
     * 
     * @param modelObject Model object
     * @param expression Expression to reach property model
     * @param map Applying map
     */
    public DisplayMapValuePropertyModel(Object modelObject, String expression, Map<T, String> map) {
        super(modelObject, expression);
        this.map = map;
    }

    /** {@inheritDoc} */
    @Override
    public String getObject() {
        return map.get(super.getObject());
    }

}
