/*
 * #%L
 * Cantharella :: Web
 * $Id: AuthStrategy.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.web/src/main/java/nc/ird/cantharella/web/utils/security/AuthStrategy.java $
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

import java.util.Arrays;
import java.util.HashSet;

import org.apache.wicket.Page;
import org.apache.wicket.Session;
import org.apache.wicket.authorization.strategies.page.AbstractPageAuthorizationStrategy;

/**
 * Authorization strategy based on AuthRoles annotations
 * 
 * @author Mickael Tricot
 * @author Adrien Cheype
 */
public final class AuthStrategy extends AbstractPageAuthorizationStrategy {

    /**
     * Check security
     * 
     * @param authorizedRoles Authorized roles
     * @return TRUE if the personne is authorized or if authorizedRoles is null or empty
     */
    public static boolean isAuthorized(AuthRole[] authorizedRoles) {
        return authorizedRoles == null
                || authorizedRoles.length == 0
                || new HashSet<AuthRole>(Arrays.asList(authorizedRoles)).contains(((AuthSession) Session.get())
                        .getRole());
    }

    /** {@inheritDoc} */
    @Override
    protected <P extends Page> boolean isPageAuthorized(Class<P> pageClass) {
        AuthRoles roles = pageClass.getAnnotation(AuthRoles.class);
        return roles == null || isAuthorized(roles.value());
    }
}
