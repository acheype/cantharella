/*
 * #%L
 * Cantharella :: Web
 * $Id: LoginModel.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.web/src/main/java/nc/ird/cantharella/web/pages/model/LoginModel.java $
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
package nc.ird.cantharella.web.pages.model;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

/**
 * Modèle : connexion d'un utilisateur, champs supplémentaires
 * 
 * @author Mickael Tricot
 */
public final class LoginModel implements Serializable {

    /** Mot de passe */
    @NotEmpty
    private String password;

    /** Se souvenir de moi */
    @NotNull
    private Boolean rememberMe;

    /**
     * password getter
     * 
     * @return password
     */
    public String getPassword() {
        return password;
    }

    /**
     * password setter
     * 
     * @param password password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * rememberMe getter
     * 
     * @return rememberMe
     */
    public Boolean getRememberMe() {
        return rememberMe;
    }

    /**
     * rememberMe setter
     * 
     * @param rememberMe rememberMe
     */
    public void setRememberMe(Boolean rememberMe) {
        this.rememberMe = rememberMe;
    }

}