/*
 * #%L
 * Cantharella :: Web
 * $Id: DisplayPercentPropertyModel.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.web/src/main/java/nc/ird/cantharella/web/utils/models/DisplayPercentPropertyModel.java $
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

import java.text.NumberFormat;
import java.util.Locale;

import nc.ird.cantharella.web.config.WebContext;

import org.apache.wicket.model.PropertyModel;

/**
 * Property Model to well display percent informations fields
 * 
 * @author Alban Diguer
 * @author Adrien Cheype
 */
public class DisplayPercentPropertyModel extends PropertyModel<Object> {

    /** locale */
    private final Locale locale;

    /**
     * Constructor
     * 
     * @param modelObject mo
     * @param expression e
     * @param locale locale
     */
    public DisplayPercentPropertyModel(Object modelObject, String expression, Locale locale) {
        super(modelObject, expression);
        this.locale = locale;
    }

    /** {@inheritDoc} */
    @Override
    public String getObject() {
        return formatPercentValue((Number) super.getObject());
    }

    /**
     * Retrieve a purcent in the appropriate format. Null-safe methode
     * 
     * @param percentValue The purcentValue
     * @return The string who represent the purcent, null if the purcent value is null
     */
    private final String formatPercentValue(Number percentValue) {
        if (percentValue == null) {
            return null;
        }
        return this.getPercentFormat().format(percentValue);
    }

    /**
     * Get the format for purcents
     * 
     * @return the format
     */
    private final NumberFormat getPercentFormat() {
        // pourcents with two digits after the point
        NumberFormat percentFormat = NumberFormat.getPercentInstance(locale);
        percentFormat.setMaximumFractionDigits(WebContext.PERCENT_PRECISION);
        return percentFormat;
    }

}
