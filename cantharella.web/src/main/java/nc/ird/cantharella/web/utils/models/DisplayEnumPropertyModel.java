/*
 * #%L
 * Cantharella :: Web
 * $Id: DisplayEnumPropertyModel.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.web/src/main/java/nc/ird/cantharella/web/utils/models/DisplayEnumPropertyModel.java $
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
 * Well Display an enum with a property model
 * 
 * @author Alban Diguer
 */
public class DisplayEnumPropertyModel extends PropertyModel<Object> {

    /** pattern */
    final String pattern;

    /** page */
    final TemplatePage page;

    /**
     * Constructor
     * 
     * @param modelObject mo
     * @param expression e
     * @param page p
     */
    public DisplayEnumPropertyModel(Object modelObject, String expression, TemplatePage page) {
        super(modelObject, expression);
        this.pattern = TemplatePage.PATTERN_ENUM_VALUE_LABEL;
        this.page = page;
    }

    /**
     * {@inheritDoc}
     * 
     * @return the enum value
     */
    @Override
    public String getObject() {
        return enumValueMessage(super.getObject());
    }

    /**
     * Retrieve an enum value message (class.value). Null-safe methode
     * 
     * @param <E> Enum type
     * @param enumValue Enum value, null is the enumValue is null
     * @return Enum value message
     */
    private final <E extends Enum<?>> String enumValueMessage(Object enumValue) {
        if (enumValue == null) {
            return null;
        }
        String enumValueMessage = String.format(pattern, enumValue.getClass().getSimpleName(), enumValue.toString());
        return page.getString(enumValueMessage);
    }
}
