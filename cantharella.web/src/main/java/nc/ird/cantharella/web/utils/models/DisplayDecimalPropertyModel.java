/*
 * #%L
 * Cantharella :: Web
 * $Id: DisplayDecimalPropertyModel.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.web/src/main/java/nc/ird/cantharella/web/utils/models/DisplayDecimalPropertyModel.java $
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

import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Locale;

import nc.ird.cantharella.data.config.DataContext;

import org.apache.wicket.model.PropertyModel;

/**
 * Property model to well display decimal fields
 * 
 * @author Alban Diguer
 */
public class DisplayDecimalPropertyModel extends PropertyModel<Object> {

    /** Format to display decimal */
    public enum DecimalDisplFormat {
        /** Small format, use the NB_DECIMAL_SMALL_FORMAT defined in the class **/
        SMALL,
        /** Large format, use the max number of decimal configured in the application **/
        LARGE
    }

    /** Number of decimal displayed for small number **/
    private int NB_DECIMAL_SMALL = 2;

    /** locale property */
    private Locale locale;

    /** format to display **/
    private DecimalDisplFormat format;

    /**
     * Constructor
     * 
     * @param modelObject modelObject
     * @param expression expression
     * @param format format
     * @param locale locale
     */
    public DisplayDecimalPropertyModel(Object modelObject, String expression, DecimalDisplFormat format, Locale locale) {
        super(modelObject, expression);
        this.format = format;
        this.locale = locale;
    }

    /** {@inheritDoc} */
    @Override
    public Object getObject() {
        return formatDecimalValue((Number) super.getObject());
    }

    /**
     * Get the format for decimals
     * 
     * @return the format
     */
    private final NumberFormat getDecimalFormat() {
        int numberOfDec = format == DecimalDisplFormat.SMALL ? NB_DECIMAL_SMALL : DataContext.DECIMAL_SCALE;

        NumberFormat fmt = NumberFormat.getNumberInstance(locale);

        fmt.setMaximumIntegerDigits(DataContext.DECIMAL_PRECISION - DataContext.DECIMAL_SCALE);

        fmt.setMaximumFractionDigits(numberOfDec);
        // affichage obligatoire des chiffres après la virgule
        fmt.setMinimumFractionDigits(numberOfDec);

        fmt.setRoundingMode(RoundingMode.HALF_UP);

        return fmt;
    }

    /**
     * Retrieve a decimal in the appropriate format. Null-safe methode
     * 
     * @param decimalValue The decimal value
     * @return The string who represent the decimal value, null if the decimal value is null
     */
    private final String formatDecimalValue(Number decimalValue) {
        if (decimalValue == null) {
            return null;
        }
        return getDecimalFormat().format(decimalValue);
    }

}