/*
 * #%L
 * Cantharella :: Utils
 * $Id: CaptchaTools.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.utils/src/main/java/nc/ird/cantharella/utils/CaptchaTools.java $
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

import org.apache.commons.lang3.RandomStringUtils;

/**
 * Tools for captchas
 * 
 * @author Mickael Tricot
 */
public final class CaptchaTools {

    /** Captcha max length */
    public static final int CAPTCHA_LENGTH_MAX = 6;

    /** Captcha min length */
    public static final int CAPTCHA_LENGTH_MIN = 3;

    /**
     * Generate a random numeric catpcha
     * 
     * @return Captcha
     */
    public static String random() {
        return RandomStringUtils
                .randomNumeric((int) (Math.random() * (CAPTCHA_LENGTH_MAX - CAPTCHA_LENGTH_MIN) + CAPTCHA_LENGTH_MIN));
    }

    /**
     * Constructor (prevents from instantiation)
     */
    private CaptchaTools() {
        //
    }
}
