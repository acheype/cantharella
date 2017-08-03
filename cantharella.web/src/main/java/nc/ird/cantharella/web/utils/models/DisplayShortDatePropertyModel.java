/*
 * #%L
 * Cantharella :: Web
 * $Id: DisplayShortDatePropertyModel.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.web/src/main/java/nc/ird/cantharella/web/utils/models/DisplayShortDatePropertyModel.java $
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

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import org.apache.wicket.model.PropertyModel;

/**
 * Property Model to well display short date fields informations
 * 
 * @author Alban Diguer
 */
public class DisplayShortDatePropertyModel extends PropertyModel<Object> {

    /** locale */
    private final Locale locale;

    /**
     * Constructor
     * 
     * @param modelObject mo
     * @param expression e
     * @param locale l
     */
    public DisplayShortDatePropertyModel(Object modelObject, String expression, Locale locale) {
        super(modelObject, expression);
        this.locale = locale;
    }

    /** {@inheritDoc} */
    @Override
    public String getObject() {
        return formatShortDateValue((Date) super.getObject());
    }

    /**
     * Retrieve a date in the appropriate format. Null-safe methode
     * 
     * @param dateValue The date (year, month, day only)
     * @return The string who represent the date, null if the date is null
     */
    private final String formatShortDateValue(Date dateValue) {
        if (dateValue == null) {
            return null;
        }
        return getShortDateFormat().format(dateValue);
    }

    /**
     * Get the format for short date
     * 
     * @return the format
     */
    private final DateFormat getShortDateFormat() {
        return DateFormat.getDateInstance(DateFormat.SHORT, locale);
    }

}
