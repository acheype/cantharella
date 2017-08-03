/*
 * #%L
 * Cantharella :: Service
 * $Id: SearchBean.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.service/src/main/java/nc/ird/cantharella/service/model/SearchBean.java $
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
package nc.ird.cantharella.service.model;

import java.io.Serializable;

/**
 * Search bean.
 * 
 * @author Eric Chatellier
 */
public class SearchBean implements Serializable {

    /** Search query. */
    protected String query;

    /** Search country. */
    protected String country;

    /**
     * Constructor.
     */
    public SearchBean() {

    }

    /**
     * Constructor.
     * 
     * @param query query
     */
    public SearchBean(String query) {
        this();
        this.query = query;
    }

    /**
     * Search query getter.
     * 
     * @return query
     */
    public String getQuery() {
        return query;
    }

    /**
     * Search query setter.
     * 
     * @param query query
     */
    public void setQuery(String query) {
        this.query = query;
    }

    /**
     * Search country getter.
     * 
     * @return country
     */
    public String getCountry() {
        return country;
    }

    /**
     * Search country setter.
     * 
     * @param country country
     */
    public void setCountry(String country) {
        this.country = country;
    }
}
