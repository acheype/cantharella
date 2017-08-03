/*
 * #%L
 * Cantharella :: Utils
 * $Id: CoordToolsTest.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.utils/src/test/java/nc/ird/cantharella/utils/CoordToolsTest.java $
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

import nc.ird.cantharella.utils.CoordTools;
import java.math.BigDecimal;

import org.junit.Assert;
import org.junit.Test;

/**
 * Coordonnate tools test
 * 
 * @author Mickael Tricot
 * @author Adrien Cheype
 */
public class CoordToolsTest {

    /**
     * Latitude test
     */
    @Test
    public void latitude() {
        Integer degrees = 1;
        BigDecimal minutes = new BigDecimal("2.300");
        Character orientation = 'N';
        String latitude = CoordTools.latitude(degrees, minutes, orientation);

        Assert.assertTrue(CoordTools.validateLatitude(latitude));
        Assert.assertFalse(CoordTools.validateLatitude(latitude.replace(orientation, Character.toLowerCase(orientation))));

        Assert.assertEquals(degrees, CoordTools.latitudeDegrees(latitude));
        Assert.assertEquals(minutes, CoordTools.latitudeMinutes(latitude));
        Assert.assertEquals(orientation, CoordTools.latitudeOrientation(latitude));
    }

    /**
     * Longitude test
     */
    @Test
    public void longitude() {
        Integer degrees = 1;
        BigDecimal minutes = new BigDecimal("52.999");
        Character orientation = 'E';
        String longitude = CoordTools.longitude(degrees, minutes, orientation);

        Assert.assertTrue(CoordTools.validateLongitude(longitude));
        Assert.assertFalse(CoordTools.validateLongitude(longitude.replace(orientation,
                Character.toLowerCase(orientation))));

        Assert.assertEquals(degrees, CoordTools.longitudeDegrees(longitude));
        Assert.assertEquals(minutes, CoordTools.longitudeMinutes(longitude));
        Assert.assertEquals(orientation, CoordTools.longitudeOrientation(longitude));
    }
}
