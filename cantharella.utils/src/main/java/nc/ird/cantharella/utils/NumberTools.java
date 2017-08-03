/*
 * #%L
 * Cantharella :: Utils
 * $Id: NumberTools.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.utils/src/main/java/nc/ird/cantharella/utils/NumberTools.java $
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
package nc.ird.cantharella.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.Locale;

/**
 * Tools for numbers
 * 
 * @author Mickael Tricot
 * @author Adrien Cheype
 */
public final class NumberTools {

    /**
     * Constructor (prevents instantiation)
     */
    private NumberTools() {
        //
    }

    /**
     * Returns null if the value if zero
     * 
     * @param <N> Number type
     * @param n Number value
     * @return The value or null if zero
     */
    public static <N extends Number> N nullIfZero(N n) {
        return n == null || n.doubleValue() == 0D ? null : n;
    }

    /**
     * Parse a double from a String. If the number is not recognize with locale separator, try with '.' decimal
     * separator.
     * 
     * @param value String value to parse
     * @param locale The locale which define the decimal separator
     * @param maxFractionDigit The maximum number of digits allowed in the fraction portion
     * @return The double rounded according to maxFractionDigit (HALF_UP rounding mode)
     */
    public static Double parseDouble(final String value, final Locale locale, final int maxFractionDigit) {
        AssertTools.assertNotEmpty(value);
        AssertTools.assertNotNull(locale);

        DecimalFormat fmt = (DecimalFormat) DecimalFormat.getNumberInstance(locale);
        fmt.setMaximumFractionDigits(maxFractionDigit);
        fmt.setRoundingMode(RoundingMode.HALF_UP);

        ParsePosition position = new ParsePosition(0);
        Number nb = (Number) fmt.parseObject(value, position);

        if (position.getIndex() != value.length()) {
            // second try with '.' for decimal separator
            if (value.charAt(position.getIndex()) == '.') {
                DecimalFormatSymbols symbols = new DecimalFormatSymbols(locale);
                symbols.setDecimalSeparator('.');
                fmt.setDecimalFormatSymbols(symbols);

                position = new ParsePosition(0);
                nb = (Number) fmt.parseObject(value, position);
                if (position.getIndex() != value.length()) {
                    throw new NumberFormatException("Cannot convert '" + value
                            + "' to Double. Parse failed at position " + position.getIndex() + ".");
                }
            } else {
                throw new NumberFormatException("Cannot convert '" + value + "' to Double. Parse failed at position "
                        + position.getIndex() + ".");
            }
        }
        return nb.doubleValue();
    }

    /**
     * Parse a big decimal from a String. If the number is not recognize with locale separator, try with '.' decimal
     * separator
     * 
     * @param value String value to parse
     * @param locale The locale which define the decimal separator
     * @param maxFractionDigit The maximum number of digits allowed in the fraction portion
     * @param maxIntegerDigit The maximum number of digits allowed in the integer portion
     * @return The big decimal with a specified precision
     */
    public static BigDecimal parseBigDecimal(final String value, final Locale locale, final int maxFractionDigit,
            final int maxIntegerDigit) {
        AssertTools.assertNotEmpty(value);
        AssertTools.assertNotNull(locale);

        DecimalFormat fmt = (DecimalFormat) NumberFormat.getNumberInstance(locale);
        fmt.setMaximumFractionDigits(maxFractionDigit);
        fmt.setMaximumIntegerDigits(maxIntegerDigit);
        fmt.setParseBigDecimal(true);

        ParsePosition position = new ParsePosition(0);
        BigDecimal bd = (BigDecimal) fmt.parseObject(value, position);

        if (position.getIndex() != value.length()) {
            // second try with '.' for decimal separator
            if (value.charAt(position.getIndex()) == '.') {
                DecimalFormatSymbols symbols = new DecimalFormatSymbols(locale);
                symbols.setDecimalSeparator('.');
                fmt.setDecimalFormatSymbols(symbols);

                position = new ParsePosition(0);
                bd = (BigDecimal) fmt.parseObject(value, position);
                if (position.getIndex() != value.length()) {
                    throw new NumberFormatException("Cannot convert '" + value
                            + "' to BigDecimal. Parse failed at position " + position.getIndex() + ".");
                }
            } else {
                throw new NumberFormatException("Cannot convert '" + value
                        + "' to BigDecimal. Parse failed at position " + position.getIndex() + ".");
            }
        }

        return bd;
    }

    /**
     * Give the string representation of a double
     * 
     * @param value The double
     * @param locale The locale which define the decimal separator
     * @param minFractionDigit the minimum number of digits allowed in the fraction portion
     * @param maxFractionDigit The maximum number of digits allowed in the fraction portion
     * @return The double rounded according to maxFractionDigit (HALF_UP rounding mode)
     */
    public static String doubleToString(final Double value, final Locale locale, final int minFractionDigit,
            final int maxFractionDigit) {
        AssertTools.assertNotNull(value);
        AssertTools.assertNotNull(locale);

        NumberFormat fmt = NumberFormat.getInstance(locale);
        if (fmt != null) {
            fmt.setMinimumFractionDigits(minFractionDigit);
            fmt.setMaximumFractionDigits(maxFractionDigit);
            fmt.setRoundingMode(RoundingMode.HALF_UP);
            return fmt.format(value);
        }
        return value.toString();
    }

    /**
     * Give the string representation of a big decimal
     * 
     * @param value The big decimal
     * @param locale The locale which define the decimal separator
     * @param minFractionDigit the minimum number of digits allowed in the fraction portion
     * @param maxFractionDigit The maximum number of digits allowed in the fraction portion
     * @param maxIntegerDigit The maximum number of digits allowed in the integer portion
     * @return The big decimal with a specified precision
     */
    public static String bigDecimalToString(final BigDecimal value, final Locale locale, final int minFractionDigit,
            final int maxFractionDigit, final int maxIntegerDigit) {
        AssertTools.assertNotNull(value);
        AssertTools.assertNotNull(locale);

        NumberFormat fmt = NumberFormat.getInstance(locale);
        if (fmt != null) {
            fmt.setMinimumFractionDigits(minFractionDigit);
            fmt.setMaximumFractionDigits(maxFractionDigit);
            fmt.setMaximumIntegerDigits(maxIntegerDigit);
            fmt.setRoundingMode(RoundingMode.HALF_UP);
            return fmt.format(value);
        }
        return value.toString();
    }
}
