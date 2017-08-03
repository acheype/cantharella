/*
 * #%L
 * Cantharella :: Service
 * $Id: ExcelColumnStructure.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.service/src/test/java/nc/ird/cantharella/service/utils/ExcelColumnStructure.java $
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
package nc.ird.cantharella.service.utils;

/**
 * Structure asked for a sheet column
 * 
 * @author Adrien Cheype
 */
public class ExcelColumnStructure {

    /** Name of the column **/
    public String name;

    /**
     * Different type of value used for the importation
     */
    public enum ExcelColumnType {
        /** string **/
        STRING,
        /** integer **/
        INTEGER,
        /** float number **/
        REEL,
        /** date **/
        DATE,
        /** boolean **/
        BOOLEAN
    }

    /** Type of the column **/
    public ExcelColumnType type;

    /** If a value is required for each row of that column */
    public boolean required;

    /**
     * Constructor
     * 
     * @param name Name of the column
     * @param type Type of the column
     * @param required If a value is required for each row of that column
     */
    public ExcelColumnStructure(String name, ExcelColumnType type, boolean required) {
        this.name = name;
        this.type = type;
        this.required = required;
    }

}
