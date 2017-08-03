/*
 * #%L
 * Cantharella :: Web
 * $Id: CallerPage.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.web/src/main/java/nc/ird/cantharella/web/utils/CallerPage.java $
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
package nc.ird.cantharella.web.utils;

import java.io.Serializable;

import nc.ird.cantharella.web.pages.TemplatePage;
import nc.ird.cantharella.utils.AssertTools;

/**
 * Caller page, to make easy redirections between pages. Works either with a page class or instance.
 * 
 * @author Mickael Tricot
 * @author Adrien Cheype
 */
public final class CallerPage implements Serializable {

    /** Page class */
    private final Class<? extends TemplatePage> classPage;

    /** Page instance */
    private final TemplatePage instancePage;

    /**
     * Constructor
     * 
     * @param page Page class
     */
    public CallerPage(Class<? extends TemplatePage> page) {
        AssertTools.assertNotNull(page);
        classPage = page;
        instancePage = null;
    }

    /**
     * Constructor
     * 
     * @param page Page instance
     */
    public CallerPage(TemplatePage page) {
        AssertTools.assertNotNull(page);
        classPage = null;
        instancePage = page;
    }

    /**
     * Add a page parameter (only for instance pages)
     * 
     * @param key Key
     * @param value Value
     */
    public void addPageParameter(String key, Object value) {
        if (instancePage != null) {
            instancePage.getPageParameters().add(key, value);
        }
    }

    /**
     * Set the response page for the current page
     * 
     * @param currentPage Current page
     */
    public void responsePage(TemplatePage currentPage) {
        if (classPage != null) {
            currentPage.setResponsePage(classPage);
        } else {
            currentPage.setResponsePage(instancePage);
        }
    }
}
