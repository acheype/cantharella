/*
 * #%L
 * Cantharella :: Utils
 * $Id: CaptchaToolsTest.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.utils/src/test/java/nc/ird/cantharella/utils/CaptchaToolsTest.java $
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

import nc.ird.cantharella.utils.CaptchaTools;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test: captcha tools
 * 
 * @author Mickael Tricot
 */
public final class CaptchaToolsTest {

    /**
     * random test
     */
    @Test
    public void random() {
        String captcha1 = CaptchaTools.random();
        Assert.assertNotNull(captcha1);
        Assert.assertTrue(captcha1.length() >= CaptchaTools.CAPTCHA_LENGTH_MIN);
        Assert.assertTrue(captcha1.length() <= CaptchaTools.CAPTCHA_LENGTH_MAX);
        String captcha2 = CaptchaTools.random();
        Assert.assertNotNull(captcha2);
        Assert.assertTrue(captcha2.length() >= CaptchaTools.CAPTCHA_LENGTH_MIN);
        Assert.assertTrue(captcha2.length() <= CaptchaTools.CAPTCHA_LENGTH_MAX);
        Assert.assertTrue(!captcha1.equals(captcha2));
    }
}