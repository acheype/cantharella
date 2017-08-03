/*
 * #%L
 * Cantharella :: Web
 * $Id: DoubleConverterImpl.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.web/src/main/java/nc/ird/cantharella/web/utils/converters/DoubleConverterImpl.java $
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
package nc.ird.cantharella.web.utils.converters;

import java.util.Locale;

import nc.ird.cantharella.web.config.WebContext;
import nc.ird.cantharella.utils.NumberTools;

import org.apache.wicket.util.convert.ConversionException;
import org.apache.wicket.util.convert.IConverter;
import org.apache.wicket.util.convert.converter.AbstractDecimalConverter;

/**
 * Converter for Float. If the number is not recognize with locale parameters, try with '.' decimal separator
 * 
 * @author Adrien Cheype
 */
public final class DoubleConverterImpl extends AbstractDecimalConverter<Double> {

    /** The minimum number of digits printed for the fraction portion of decimals **/
    public static final int DECIMAL_MIN_FRACTION_DIGIT = 0;

    /** Logger */
    //private static final Logger LOG = LoggerFactory.getLogger(DoubleConverterImpl.class);
    /** The singleton instance for a float converter */
    public static final IConverter<Double> INSTANCE = new DoubleConverterImpl();

    /** {@inheritDoc} */
    @Override
    public Double convertToObject(String value, Locale locale) {
        if (value == null || value.trim().equals("")) {
            return null;
        }

        if (locale == null) {
            locale = Locale.getDefault();
        }

        try {
            return NumberTools.parseDouble(value, locale, WebContext.DOUBLE_MAX_FRACTION_DIGIT);
        } catch (Exception e) {
            throw new ConversionException(e);
        }
    }

    /** {@inheritDoc} */
    @Override
    public String convertToString(final Double value, Locale locale) {
        try {
            return NumberTools.doubleToString(value, locale, DECIMAL_MIN_FRACTION_DIGIT,
                    WebContext.DOUBLE_MAX_FRACTION_DIGIT);
        } catch (Exception e) {
            throw new ConversionException(e);
        }
    }

    /**
     * @see org.apache.wicket.util.convert.converter.AbstractConverter#getTargetType()
     */
    @Override
    protected Class<Double> getTargetType() {
        return Double.class;
    }

}
