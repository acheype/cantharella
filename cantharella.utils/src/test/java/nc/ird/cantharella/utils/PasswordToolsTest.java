/*
 * #%L
 * Cantharella :: Utils
 * $Id: PasswordToolsTest.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.utils/src/test/java/nc/ird/cantharella/utils/PasswordToolsTest.java $
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

import nc.ird.cantharella.utils.PasswordTools;
import org.junit.Assert;
import org.junit.Test;

/**
 * PasswordTools test
 * 
 * @author Mickael Tricot
 */
public final class PasswordToolsTest {

    /**
     * md5 test
     */
    @Test
    public void md5() {
        String hash1 = PasswordTools.md5("toto");
        Assert.assertNotNull(hash1);
        Assert.assertEquals(PasswordTools.MD5_LENGTH, hash1.length());
        Assert.assertEquals(PasswordTools.md5("toto"), hash1);
        String hash2 = PasswordTools.md5("titi");
        Assert.assertNotNull(hash2);
        Assert.assertEquals(PasswordTools.MD5_LENGTH, hash2.length());
        Assert.assertEquals(PasswordTools.md5("titi"), hash2);
        Assert.assertNotNull(hash1);
        Assert.assertTrue(!hash1.equals(hash2));
    }

    /**
     * random test
     */
    @Test
    public void random() {
        String password1 = PasswordTools.random();
        Assert.assertNotNull(password1);
        Assert.assertTrue(password1.length() >= PasswordTools.PASSWORD_LENGTH_MIN);
        Assert.assertTrue(password1.length() <= PasswordTools.PASSWORD_LENGTH_MAX);
        String password2 = PasswordTools.random();
        Assert.assertNotNull(password2);
        Assert.assertTrue(password2.length() >= PasswordTools.PASSWORD_LENGTH_MIN);
        Assert.assertTrue(password2.length() <= PasswordTools.PASSWORD_LENGTH_MAX);
        Assert.assertTrue(!password1.equals(password2));
    }

    /**
     * sha1 test
     */
    @Test
    public void sha1() {
        String hash1 = PasswordTools.sha1("toto");
        Assert.assertNotNull(hash1);
        Assert.assertEquals(PasswordTools.sha1("toto"), hash1);
        Assert.assertEquals(PasswordTools.SHA1_LENGTH, hash1.length());
        String hash2 = PasswordTools.sha1("titi");
        Assert.assertNotNull(hash2);
        Assert.assertEquals(PasswordTools.SHA1_LENGTH, hash2.length());
        Assert.assertNotNull(hash1);
        Assert.assertTrue(!hash1.equals(hash2));
    }
}
