package nc.ird.cantharella.utils;

/*
 * #%L
 * Cantharella :: Utils
 * $Id: CantharellaConfig.java 268 2014-05-06 15:45:40Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.utils/src/main/java/nc/ird/cantharella/utils/CantharellaConfig.java $
 * %%
 * Copyright (C) 2009 - 2013 IRD (Institut de Recherche pour le Developpement) and by respective authors (see below)
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

import java.util.Properties;

import org.nuiton.config.ApplicationConfig;
import org.nuiton.config.ArgumentsParserException;

/**
 * Used as factory in spring configuration to get configuration file as properties this class use internaly
 * {@link ApplicationConfig}.
 * 
 * @author poussin
 * @version $Revision: 268 $
 * 
 *          Last update: $Date: 2014-05-07 02:45:40 +1100 (Wed, 07 May 2014) $ by : $Author: echatellier $
 */
public class CantharellaConfig {

    /**
     * force filename to cantharella.config
     * 
     * @return cantharella configuration as properties
     * @throws ArgumentsParserException
     */
    static public Properties getProperties() throws ArgumentsParserException {
        return getProperties("cantharella.conf");
    }

    /**
     * This method take file name in argument, this permit to force filename for example for unit tests
     * 
     * @param filename filename to use
     * @return cantharella configuration as properties
     * @throws ArgumentsParserException
     */
    static public Properties getProperties(String filename) throws ArgumentsParserException {
        ApplicationConfig config = new ApplicationConfig(filename);
        config.parse();
        Properties result = config.getFlatOptions();
        return result;
    }
}
