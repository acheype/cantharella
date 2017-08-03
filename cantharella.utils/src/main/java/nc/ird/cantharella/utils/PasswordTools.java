/*
 * #%L
 * Cantharella :: Utils
 * $Id: PasswordTools.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.utils/src/main/java/nc/ird/cantharella/utils/PasswordTools.java $
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

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;

/**
 * Tools for passwords
 * 
 * @author Mickael Tricot
 */
public final class PasswordTools {

    /** Password MD5 length */
    public static final int MD5_LENGTH = 32;

    /** Password max length */
    public static final int PASSWORD_LENGTH_MAX = 12;

    /** Password min length */
    public static final int PASSWORD_LENGTH_MIN = 5;

    /** Password SHA1 length */
    public static final int SHA1_LENGTH = 40;

    /**
     * Hash a password with the MD5 algorithm
     * 
     * @param password Password
     * @return Hashed password
     */
    public static String md5(String password) {
        AssertTools.assertNotNull(password);
        return DigestUtils.md5Hex(password);
    }

    /**
     * Generate a random password
     * 
     * @return Password
     */
    public static String random() {
        return RandomStringUtils
                .randomAlphanumeric((int) (Math.random() * (PASSWORD_LENGTH_MAX - PASSWORD_LENGTH_MIN) + PASSWORD_LENGTH_MIN));
    }

    /**
     * Hash a password with the SHA-1 algorithm
     * 
     * @param password Password
     * @return Hashed password
     */
    public static String sha1(String password) {
        AssertTools.assertNotNull(password);
        return DigestUtils.sha1Hex(password);
    }

    /**
     * Constructor (prevents from instantiation)
     */
    private PasswordTools() {
        //
    }
}
