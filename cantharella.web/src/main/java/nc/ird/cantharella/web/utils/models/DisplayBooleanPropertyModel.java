/*
 * #%L
 * Cantharella :: Web
 * $Id: DisplayBooleanPropertyModel.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.web/src/main/java/nc/ird/cantharella/web/utils/models/DisplayBooleanPropertyModel.java $
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

import nc.ird.cantharella.web.pages.TemplatePage;

import org.apache.wicket.model.PropertyModel;

/**
 * Model to well display boolean informations
 * 
 * @author Alban Diguer
 */
public class DisplayBooleanPropertyModel extends PropertyModel<Object> {

    /** page */
    private final TemplatePage page;

    /** true string */
    private static final String TRUE_KEY = "Boolean.true";

    /** false string */
    private static final String FALSE_KEY = "Boolean.false";

    /**
     * Constructor
     * 
     * @param modelObject mo
     * @param expression e
     * @param page p
     */
    public DisplayBooleanPropertyModel(Object modelObject, String expression, TemplatePage page) {
        super(modelObject, expression);
        this.page = page;
    }

    /** {@inheritDoc} */
    @Override
    public String getObject() {
        return booleanValueMessage((Boolean) super.getObject());
    }

    /**
     * Retrieve a boolean value message. Null-safe methode
     * 
     * @param boolValue The boolean value
     * @return The associated message, null if the Boolean is null
     */
    private final String booleanValueMessage(Boolean boolValue) {
        if (boolValue == null) {
            return null;
        }
        if (boolValue) {
            return page.getString(TRUE_KEY);
        } else {
            return page.getString(FALSE_KEY);
        }
    }
}
