/*
 * #%L
 * Cantharella :: Web
 * $Id: AuthContainer.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.web/src/main/java/nc/ird/cantharella/web/utils/security/AuthContainer.java $
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
package nc.ird.cantharella.web.utils.security;

import org.apache.wicket.markup.html.WebMarkupContainer;

/**
 * Container displayed only if the specified roles are authorized
 * 
 * @author Mickael Tricot
 */
public final class AuthContainer extends WebMarkupContainer {

    /** Authorization? */
    private final boolean authorized;

    /**
     * Constructor
     * 
     * @param id ID
     * @param roles Authorized roles
     */
    public AuthContainer(String id, AuthRole... roles) {
        super(id);
        authorized = AuthStrategy.isAuthorized(roles);
    }

    /**
     * authorized getter
     * 
     * @return authorized
     */
    public boolean isAuthorized() {
        return authorized;
    }

    /** {@inheritDoc} */
    @Override
    protected void onBeforeRender() {
        setVisibilityAllowed(isAuthorized());
        super.onBeforeRender();
    }
}