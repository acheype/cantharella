/*
 * #%L
 * Cantharella :: Utils
 * $Id: CoordTools.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.utils/src/main/java/nc/ird/cantharella/utils/CoordTools.java $
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
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

/**
 * Tools for coordonates
 * 
 * @author Mickael Tricot
 * @author Adrien Cheype
 */
public final class CoordTools {

    /** Degrees character */
    public static final char DEGREES = '°';

    /** Latitude length */
    public static final int LATITUDE_LENGTH = 11;

    /** Latitude max degrees */
    public static final int LATITUDE_MAX_DEGREES = 90;

    /** Latitude max minutes */
    public static final String LATITUDE_MAX_MINUTES_STRING = "59.999";

    /** Latitude max minutes */
    public static final BigDecimal LATITUDE_MAX_MINUTES = new BigDecimal(LATITUDE_MAX_MINUTES_STRING);

    /** Latitude min degrees */
    public static final int LATITUDE_MIN_DEGREES = 0;

    /** Latitude min minutes */
    public static final String LATITUDE_MIN_MINUTES_STRING = "00.000";

    /** Latitude min minutes */
    public static final BigDecimal LATITUDE_MIN_MINUTES = new BigDecimal(LATITUDE_MIN_MINUTES_STRING);

    /** Latitude orientations */
    public static final Character[] LATITUDE_ORIENTATIONS = { 'N', 'S' };

    /** Latitude pattern */
    private static final Pattern LATITUDE_PATTERN;

    /** Longitude length */
    public static final int LONGITUDE_LENGTH = 12;

    /** Longitude max degrees */
    public static final int LONGITUDE_MAX_DEGREES = 180;

    /** Longitude max minutes */
    public static final String LONGITUDE_MAX_MINUTES_STRING = "59.999";

    /** Longitude max minutes */
    public static final BigDecimal LONGITUDE_MAX_MINUTES = new BigDecimal(LONGITUDE_MAX_MINUTES_STRING);

    /** Longitude min degrees */
    public static final int LONGITUDE_MIN_DEGREES = 0;

    /** Longitude min minutes */
    public static final String LONGITUDE_MIN_MINUTES_STRING = "00.000";

    /** Longitude min minutes */
    public static final BigDecimal LONGITUDE_MIN_MINUTES = new BigDecimal(LONGITUDE_MIN_MINUTES_STRING);

    /** Longitude orientations */
    public static final Character[] LONGITUDE_ORIENTATIONS = { 'E', 'W' };

    /** Longitude pattern */
    private static final Pattern LONGITUDE_PATTERN;

    /** Minutes character */
    public static final char MINUTES = '\'';

    /** Minutes blank */
    private static final char MINUTES_BLANK = '0';

    /** Minutes decimal length */
    private static final int MINUTES_DECIMAL_LENGTH;

    /** Minutes integer length */
    private static final int MINUTES_INTEGER_LENGTH;

    /** Minutes separator */
    public static final char MINUTES_SEPARATOR = '.';

    /** Minutes separator */
    private static final String MINUTES_SEPARATOR_PATTERN = "\\.";

    static {
        String[] md = LONGITUDE_MAX_MINUTES_STRING.split(MINUTES_SEPARATOR_PATTERN);

        MINUTES_INTEGER_LENGTH = md[0].length();
        MINUTES_DECIMAL_LENGTH = md[1].length();

        String patternMinutes = "[0-9]";
        String patternDegrees = "[ 0-9]";

        StringBuilder pattern = new StringBuilder("^(");
        for (int i = 0; i < String.valueOf(LATITUDE_MAX_DEGREES).length(); ++i) {
            pattern.append(patternDegrees);
        }
        pattern.append(')');
        pattern.append(DEGREES);
        pattern.append('(');
        for (int i = 0; i < MINUTES_INTEGER_LENGTH; ++i) {
            pattern.append(patternMinutes);
        }
        pattern.append(")");
        pattern.append(MINUTES_SEPARATOR_PATTERN);
        pattern.append('(');
        for (int i = 0; i < MINUTES_DECIMAL_LENGTH; ++i) {
            pattern.append(patternMinutes);
        }
        pattern.append(')');
        pattern.append(MINUTES);
        pattern.append("([");
        for (char o : LATITUDE_ORIENTATIONS) {
            pattern.append(o);
        }
        pattern.append("])$");
        LATITUDE_PATTERN = Pattern.compile(pattern.toString());

        pattern = new StringBuilder("^(");
        for (int i = 0; i < String.valueOf(LONGITUDE_MAX_DEGREES).length(); ++i) {
            pattern.append(patternDegrees);
        }
        pattern.append(')');
        pattern.append(DEGREES);
        pattern.append('(');
        for (int i = 0; i < MINUTES_INTEGER_LENGTH; ++i) {
            pattern.append(patternMinutes);
        }
        pattern.append(")");
        pattern.append(MINUTES_SEPARATOR_PATTERN);
        pattern.append('(');
        for (int i = 0; i < MINUTES_DECIMAL_LENGTH; ++i) {
            pattern.append(patternMinutes);
        }
        pattern.append(')');
        pattern.append(MINUTES);
        pattern.append("([");
        for (char o : LONGITUDE_ORIENTATIONS) {
            pattern.append(o);
        }
        pattern.append("])$");

        LONGITUDE_PATTERN = Pattern.compile(pattern.toString());
    }

    /**
     * Format a latitude (dd.mm.sss"o)
     * 
     * @param d Degrees (0-90)
     * @param m Minutes (00.000-59.999)
     * @param o Orientation (N-S)
     * @return Latitude
     */
    public static String latitude(Integer d, BigDecimal m, Character o) {
        // Check values
        AssertTools.assertGreaterOrEquals(d, LATITUDE_MIN_DEGREES);
        AssertTools.assertLowerOrEquals(d, LATITUDE_MAX_DEGREES);
        AssertTools.assertGreaterOrEquals(m, LATITUDE_MIN_MINUTES);
        AssertTools.assertLowerOrEquals(m, LATITUDE_MAX_MINUTES);
        AssertTools.assertIn(Character.toUpperCase(o), LATITUDE_ORIENTATIONS);
        // Format values
        // LOG.debug(m);
        // LOG.debug(NumberTools.doubleToString(m, Locale.ENGLISH, MINUTES_DECIMAL_LENGTH, MINUTES_DECIMAL_LENGTH));
        String[] md = NumberTools.bigDecimalToString(m, Locale.ENGLISH, MINUTES_DECIMAL_LENGTH, MINUTES_DECIMAL_LENGTH,
                2).split(MINUTES_SEPARATOR_PATTERN);
        // LOG.debug(md);
        return StringUtils.leftPad(String.valueOf(d), String.valueOf(LATITUDE_MAX_DEGREES).length()) + DEGREES
                + StringUtils.leftPad(String.valueOf(md[0]), MINUTES_INTEGER_LENGTH, MINUTES_BLANK) + MINUTES_SEPARATOR
                + StringUtils.rightPad(md[1], MINUTES_DECIMAL_LENGTH, MINUTES_BLANK) + MINUTES
                + Character.toUpperCase(o);
    }

    /**
     * Retrieve the degrees from a complete latitude
     * 
     * @param latitude Latitude
     * @return Degrees
     */
    public static Integer latitudeDegrees(String latitude) {
        if (StringUtils.isEmpty(latitude)) {
            return null;
        }
        Matcher matcher = LATITUDE_PATTERN.matcher(latitude);
        matcher.lookingAt();
        return Integer.valueOf(matcher.group(1).trim());
    }

    /**
     * Retrieve the minutes from a complete latitude
     * 
     * @param latitude Latitude
     * @return Minutes
     */
    public static BigDecimal latitudeMinutes(String latitude) {
        if (StringUtils.isEmpty(latitude)) {
            return null;
        }
        Matcher matcher = LATITUDE_PATTERN.matcher(latitude);
        matcher.lookingAt();
        return NumberTools.parseBigDecimal(matcher.group(2) + MINUTES_SEPARATOR + matcher.group(3), Locale.ENGLISH,
                MINUTES_DECIMAL_LENGTH, 2);
    }

    /**
     * Retrieve the orientation from a complete latitude
     * 
     * @param latitude Latitude
     * @return Orientation
     */
    public static Character latitudeOrientation(String latitude) {
        if (StringUtils.isEmpty(latitude)) {
            return null;
        }
        Matcher matcher = LATITUDE_PATTERN.matcher(latitude);
        matcher.lookingAt();
        return Character.valueOf(matcher.group(4).charAt(0));
    }

    /**
     * Format a longitude (ddd.mm'ss"o)
     * 
     * @param d Degrees (0-180)
     * @param m Minutes (00.000-59.999)
     * @param o Orientation (E-W)
     * @return Longitude
     */
    public static String longitude(Integer d, BigDecimal m, Character o) {
        // Check values
        AssertTools.assertGreaterOrEquals(d, LONGITUDE_MIN_DEGREES);
        AssertTools.assertLowerOrEquals(d, LONGITUDE_MAX_DEGREES);
        AssertTools.assertGreaterOrEquals(m, LONGITUDE_MIN_MINUTES);
        AssertTools.assertLowerOrEquals(m, LONGITUDE_MAX_MINUTES);
        AssertTools.assertIn(Character.toUpperCase(o), LONGITUDE_ORIENTATIONS);
        // Format values
        String[] md = NumberTools.bigDecimalToString(m, Locale.ENGLISH, MINUTES_DECIMAL_LENGTH, MINUTES_DECIMAL_LENGTH,
                3).split(MINUTES_SEPARATOR_PATTERN);
        return StringUtils.leftPad(String.valueOf(d), String.valueOf(LONGITUDE_MAX_DEGREES).length()) + DEGREES
                + StringUtils.leftPad(String.valueOf(md[0]), MINUTES_INTEGER_LENGTH, MINUTES_BLANK) + MINUTES_SEPARATOR
                + StringUtils.rightPad(md[1], MINUTES_DECIMAL_LENGTH, MINUTES_BLANK) + MINUTES
                + Character.toUpperCase(o);
    }

    /**
     * Retrieve the degrees from a complete longitude
     * 
     * @param longitude Longitude
     * @return Degrees
     */
    public static Integer longitudeDegrees(String longitude) {
        if (StringUtils.isEmpty(longitude)) {
            return null;
        }
        Matcher matcher = LONGITUDE_PATTERN.matcher(longitude);
        matcher.lookingAt();
        return Integer.valueOf(matcher.group(1).trim());
    }

    /**
     * Retrieve the minutes from a complete longitude
     * 
     * @param longitude Longitude
     * @return Minutes
     */
    public static BigDecimal longitudeMinutes(String longitude) {
        if (StringUtils.isEmpty(longitude)) {
            return null;
        }
        Matcher matcher = LONGITUDE_PATTERN.matcher(longitude);
        matcher.lookingAt();
        return NumberTools.parseBigDecimal(matcher.group(2) + MINUTES_SEPARATOR + matcher.group(3), Locale.ENGLISH,
                MINUTES_DECIMAL_LENGTH, 3);
    }

    /**
     * Retrieve the orientation from a complete longitude
     * 
     * @param longitude Longitude
     * @return Orientation
     */
    public static Character longitudeOrientation(String longitude) {
        if (StringUtils.isEmpty(longitude)) {
            return null;
        }
        Matcher matcher = LONGITUDE_PATTERN.matcher(longitude);
        matcher.lookingAt();
        return Character.valueOf(matcher.group(4).charAt(0));
    }

    /**
     * Validate a latitude (ddd.mm.sss"o)
     * 
     * @param latitude Latitude
     * @return TRUE if the latitude is valid
     */
    public static boolean validateLatitude(String latitude) {
        AssertTools.assertNotEmpty(latitude);

        Matcher matcher = LATITUDE_PATTERN.matcher(latitude);
        if (matcher.lookingAt()) {
            int d = Integer.valueOf(matcher.group(1).trim());
            BigDecimal m = NumberTools.parseBigDecimal(matcher.group(2) + MINUTES_SEPARATOR + matcher.group(3),
                    Locale.ENGLISH, MINUTES_DECIMAL_LENGTH, 2);
            // if the latitude is 90 with others minutes, return false
            return d >= LATITUDE_MIN_DEGREES && d <= LATITUDE_MAX_DEGREES && LATITUDE_MIN_MINUTES.compareTo(m) <= 0
                    && LATITUDE_MAX_MINUTES.compareTo(m) >= 0 && (d != 90 || new BigDecimal("0.000").compareTo(m) == 0);
        }
        return false;
    }

    /**
     * Validate a longitude (dd.mm.sss"o)
     * 
     * @param longitude Longitude
     * @return TRUE if the longitude is valid
     */
    public static boolean validateLongitude(String longitude) {
        AssertTools.assertNotEmpty(longitude);
        Matcher matcher = LONGITUDE_PATTERN.matcher(longitude);
        if (matcher.lookingAt()) {
            int d = Integer.valueOf(matcher.group(1).trim());
            BigDecimal m = NumberTools.parseBigDecimal(matcher.group(2) + MINUTES_SEPARATOR + matcher.group(3),
                    Locale.ENGLISH, MINUTES_DECIMAL_LENGTH, 3);
            // if the longitude is 180 with others minutes, return false
            return d >= LONGITUDE_MIN_DEGREES && d <= LONGITUDE_MAX_DEGREES && LONGITUDE_MIN_MINUTES.compareTo(m) <= 0
                    && LATITUDE_MAX_MINUTES.compareTo(m) >= 0
                    && (d != 180 || new BigDecimal("0.000").compareTo(m) == 0);
        }
        return false;
    }

    /**
     * Constructor
     */
    private CoordTools() {
        //
    }
}
