/*
 * #%L
 * Cantharella :: Web
 * $Id: AuthSession.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.web/src/main/java/nc/ird/cantharella/web/utils/security/AuthSession.java $
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

import java.io.Serializable;
import java.util.HashSet;
import java.util.Locale;

import nc.ird.cantharella.data.config.DataContext;
import nc.ird.cantharella.data.exceptions.DataNotFoundException;
import nc.ird.cantharella.data.model.Utilisateur;
import nc.ird.cantharella.data.model.Utilisateur.TypeDroit;
import nc.ird.cantharella.service.services.PersonneService;
import nc.ird.cantharella.web.config.WebApplicationImpl;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.protocol.http.WebSession;
import org.apache.wicket.request.Request;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * Web session
 * 
 * @author Mickael Tricot
 * @author Adrien Cheype
 */
public final class AuthSession extends WebSession implements Serializable {

    /** Logger */
    private static final Logger LOG = LoggerFactory.getLogger(AuthSession.class);

    /** Attribute: rememberMe */
    private static final String ATTRIBUTE_REMEMBER_ME = "rememberMe";

    /** Attribute: roles */
    private static final String ATTRIBUTE_ROLE = "role";

    /** Attribute: utilisateur */
    private static final String ATTRIBUTE_UTILISATEUR = "utilisateur";

    /** Personne service */
    @SpringBean
    private PersonneService personneService;

    /**
     * Constructor
     * 
     * @param request Request
     */
    public AuthSession(Request request) {
        super(request);
        WebApplicationImpl.injectSpringBeans(this);
        checkLocale();
        setAttribute(ATTRIBUTE_ROLE, new HashSet<TypeDroit>());
        setUtilisateur(null);

        if (!autologin()) {
            logout();
        }
        bind();
    }

    /**
     * Check that current locale is a locale managed by cantharella.
     */
    protected void checkLocale() {
        Locale locale = super.getLocale();
        if (!DataContext.LOCALES.contains(locale)) {
            // try to get new locale with current locale language
            locale = new Locale(locale.getLanguage());
            if (!DataContext.LOCALES.contains(locale)) {
                // default to "en" locale
                locale = DataContext.LOCALES.get(0);
            }
            setLocale(locale);
        }
    }

    /**
     * Autologin
     * 
     * @return Success
     */
    private boolean autologin() {

        String[] cookie = WebApplication.get().getSecuritySettings().getAuthenticationStrategy().load();
        // cookie[0] = courriel, cookie[1] = passwordHash
        if (cookie != null && cookie.length == 2 && !StringUtils.isEmpty(cookie[0]) && !StringUtils.isEmpty(cookie[1])
                && authenticate(cookie[0], cookie[1])) {
            try {
                // connexion without cookie persistance because already exists
                connectUser(personneService.loadUtilisateur(cookie[0]), false);
            } catch (DataNotFoundException e) {
                // just in cas the user is deleted by another user between authenticate and plainLogin
                return false;
            }
            return true;
        }
        return false;
    }

    /**
     * Check user authenticate.
     * 
     * @param username username
     * @param password password
     * @return {@code true} if username and password match
     */
    public boolean authenticate(String username, String password) {
        boolean authenticate = personneService.authenticateUtilisateur(username, password);
        LOG.debug(String.valueOf(authenticate));
        return authenticate;
    }

    /**
     * rememberMe getter
     * 
     * @return rememberMe
     */
    public Boolean getRememberMe() {
        return (Boolean) getAttribute(ATTRIBUTE_REMEMBER_ME);
    }

    /**
     * role getter
     * 
     * @return role
     */
    public AuthRole getRole() {
        return (AuthRole) getAttribute(ATTRIBUTE_ROLE);
    }

    /**
     * utilisateur getter
     * 
     * @return utilisateur
     */
    public Utilisateur getUtilisateur() {
        Integer idPersonne = (Integer) getAttribute(ATTRIBUTE_UTILISATEUR);
        Utilisateur utilisateur = null;
        if (idPersonne != null) {
            try {
                utilisateur = personneService.loadUtilisateur(idPersonne);
            } catch (DataNotFoundException e) {
                logout();
            } catch (Exception e) {
                LOG.debug(e.getClass() + e.getMessage());
                return null;
            }
        }
        return utilisateur;
    }

    /**
     * Connect the user (to do after authenticating)
     * 
     * @param utilisateur Utilisateur
     * @param rememberMe True if the authentication is saved in a cookie
     */
    public void connectUser(Utilisateur utilisateur, boolean rememberMe) {
        setUtilisateur(utilisateur);
        setRole(utilisateur.getTypeDroit() == TypeDroit.UTILISATEUR ? AuthRole.USER : AuthRole.ADMIN);

        setRememberMe(rememberMe);
        if (rememberMe) {
            WebApplication.get().getSecuritySettings().getAuthenticationStrategy()
                    .save(utilisateur.getCourriel(), utilisateur.getPasswordHash());
        }
    }

    /**
     * Logout action
     */
    public void logout() {
        setUtilisateur(null);
        setRememberMe(false);
        setRole(AuthRole.VISITOR);
        WebApplication.get().getSecuritySettings().getAuthenticationStrategy().remove();
    }

    /**
     * rememberMe setter
     * 
     * @param rememberMe RememberMe
     */
    private void setRememberMe(Boolean rememberMe) {
        setAttribute(ATTRIBUTE_REMEMBER_ME, rememberMe);
    }

    /**
     * role setter
     * 
     * @param role Role
     */
    private void setRole(AuthRole role) {
        setAttribute(ATTRIBUTE_ROLE, role);
    }

    /**
     * utilisateur setter
     * 
     * @param utilisateur Utilisateur
     */
    private void setUtilisateur(Utilisateur utilisateur) {
        setAttribute(ATTRIBUTE_UTILISATEUR, utilisateur != null ? utilisateur.getIdPersonne() : null);
    }

    /**
     * Login action
     * 
     * @param utilisateur Utilisateur
     */
    public void update(Utilisateur utilisateur) {
        connectUser(utilisateur, getRememberMe());
    }
}
