/*
 * #%L
 * Cantharella :: Web
 * $Id: BigDecimalConverterImpl.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.web/src/main/java/nc/ird/cantharella/web/utils/converters/BigDecimalConverterImpl.java $
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

import java.math.BigDecimal;
import java.util.Locale;

import nc.ird.cantharella.data.config.DataContext;
import nc.ird.cantharella.utils.NumberTools;

import org.apache.wicket.util.convert.ConversionException;
import org.apache.wicket.util.convert.IConverter;
import org.apache.wicket.util.convert.converter.AbstractDecimalConverter;

/**
 * Converter for BigDecimal, work with DECIMAL_PRECISION and SCALE_PRECISION. If the number is not recognize with locale
 * parameters, try with '.' decimal separator
 * 
 * @see DataContext#DECIMAL_PRECISION
 * @see DataContext#DECIMAL_SCALE
 * @author Adrien Cheype
 */
public final class BigDecimalConverterImpl extends AbstractDecimalConverter<BigDecimal> {

    /** The minimum number of digits printed for the fraction portion of decimals **/
    public static final int DECIMAL_MIN_FRACTION_DIGIT = 0;

    /** Logger */
    //private static final Logger LOG = LoggerFactory.getLogger(BigDecimalConverterImpl.class);
    /** The singleton instance for a big integer converter */
    public static final IConverter<BigDecimal> INSTANCE = new BigDecimalConverterImpl();

    /** {@inheritDoc} */
    @Override
    public BigDecimal convertToObject(String value, Locale locale) {
        if (value == null || value.trim().equals("")) {
            return null;
        }

        if (locale == null) {
            locale = Locale.getDefault();
        }

        try {
            return NumberTools.parseBigDecimal(value, locale, DataContext.DECIMAL_SCALE, DataContext.DECIMAL_PRECISION
                    - DataContext.DECIMAL_SCALE);
        } catch (RuntimeException e) {
            throw new ConversionException(e);
        }
    }

    /** {@inheritDoc} */
    public String convertToString(final BigDecimal value, Locale locale) {
        try {
            return NumberTools.bigDecimalToString(value, locale, DECIMAL_MIN_FRACTION_DIGIT, DataContext.DECIMAL_SCALE,
                    DataContext.DECIMAL_PRECISION - DataContext.DECIMAL_SCALE);
        } catch (RuntimeException e) {
            throw new ConversionException(e);
        }
    }

    /**
     * @see org.apache.wicket.util.convert.converter.AbstractConverter#getTargetType()
     */
    @Override
    protected Class<BigDecimal> getTargetType() {
        return BigDecimal.class;
    }
}
