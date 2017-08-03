/*
 * #%L
 * Cantharella :: Web
 * $Id: MapChoiceRenderer.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.web/src/main/java/nc/ird/cantharella/web/utils/renderers/MapChoiceRenderer.java $
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
package nc.ird.cantharella.web.utils.renderers;

import java.io.Serializable;
import java.util.Map;

import org.apache.wicket.markup.html.form.IChoiceRenderer;

/**
 * IChoiceRender for a map
 * 
 * @author Mickael Tricot
 * @param <K> Key (ID)
 * @param <V> Value (display value)
 */
public final class MapChoiceRenderer<K, V> implements IChoiceRenderer<K>, Serializable {

    /** Map */
    private final Map<K, V> map;

    /**
     * Constructor
     * 
     * @param map Map
     */
    public MapChoiceRenderer(Map<K, V> map) {
        this.map = map;
    }

    /** {@inheritDoc} */
    @Override
    public V getDisplayValue(K object) {
        return map.get(object);
    }

    /** {@inheritDoc} */
    @Override
    public String getIdValue(K object, int index) {
        return object != null ? object.toString() : null;
    }
}