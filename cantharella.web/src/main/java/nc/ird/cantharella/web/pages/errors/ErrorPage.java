/*
 * #%L
 * Cantharella :: Web
 * $Id: ErrorPage.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.web/src/main/java/nc/ird/cantharella/web/pages/errors/ErrorPage.java $
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
package nc.ird.cantharella.web.pages.errors;

import java.util.MissingResourceException;

import nc.ird.cantharella.web.pages.TemplatePage;

/**
 * Abstract error page
 * 
 * @author Mickael Tricot
 */
public abstract class ErrorPage extends TemplatePage {

    /** Error message pattern: resource.classSimpleName */
    private static final String PATTERN_ERROR_MSG = "%s.%s";

    /**
     * Constructor
     */
    public ErrorPage() {
        super(ErrorPage.class);
        String errorMsgKey = String.format(PATTERN_ERROR_MSG, getResource(), getClass().getSimpleName());
        try {
            errorMsgKey = getString(errorMsgKey);
        } catch (MissingResourceException e) {
            //
        }
        error(errorMsgKey);
    }

    /** {@inheritDoc} */
    @Override
    public final boolean isErrorPage() {
        return true;
    }
}
