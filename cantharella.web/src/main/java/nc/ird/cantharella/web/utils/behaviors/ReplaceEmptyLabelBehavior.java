/*
 * #%L
 * Cantharella :: Web
 * $Id: ReplaceEmptyLabelBehavior.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.web/src/main/java/nc/ird/cantharella/web/utils/behaviors/ReplaceEmptyLabelBehavior.java $
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
package nc.ird.cantharella.web.utils.behaviors;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;

/**
 * For instance, used to replace empty fields by customed regular expression when model is null
 * 
 * @author Alban Diguer
 */
public class ReplaceEmptyLabelBehavior extends Behavior {

    /** Regular expression to display */
    public static final String NULL_PROPERTY = " - ";

    /** {@inheritDoc} */
    @Override
    public void beforeRender(Component component) {
        super.bind(component);
        if (component.getDefaultModelObject() == null) {
            component.getResponse().write(NULL_PROPERTY);
        }
    }

}
