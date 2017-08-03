/*
 * #%L
 * Cantharella :: Web
 * $Id: UpdateUtilisateurModel.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.web/src/main/java/nc/ird/cantharella/web/pages/model/UpdateUtilisateurModel.java $
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

import nc.ird.cantharella.utils.PasswordTools;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Modèle : champs supplémentaire pour la mise à jour de son profil utilisateur
 * 
 * @author Mickael Tricot
 */
public final class UpdateUtilisateurModel implements Serializable {

    /** Mot de passe courant */
    @NotEmpty
    private String currentPassword;

    /** Nouveau mot de passe */
    @Length(min = PasswordTools.PASSWORD_LENGTH_MIN, max = PasswordTools.PASSWORD_LENGTH_MAX)
    @NotNull
    private String newPassword;

    /** Confirmation du nouveau mot de passe */
    private String newPasswordConfirmation;

    /** Mot de passe */
    @NotEmpty
    private String password;

    /**
     * Validate the model
     * 
     * @return TRUE if it is valid
     */
    public boolean validate() {
        return newPassword.equals(newPasswordConfirmation);
    }

    /**
     * currentPassword getter
     * 
     * @return currentPassword
     */
    public String getCurrentPassword() {
        return currentPassword;
    }

    /**
     * currentPassword setter
     * 
     * @param currentPassword currentPassword
     */
    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    /**
     * newPassword getter
     * 
     * @return newPassword
     */
    public String getNewPassword() {
        return newPassword;
    }

    /**
     * newPassword setter
     * 
     * @param newPassword newPassword
     */
    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    /**
     * newPasswordConfirmation getter
     * 
     * @return newPasswordConfirmation
     */
    public String getNewPasswordConfirmation() {
        return newPasswordConfirmation;
    }

    /**
     * newPasswordConfirmation setter
     * 
     * @param newPasswordConfirmation newPasswordConfirmation
     */
    public void setNewPasswordConfirmation(String newPasswordConfirmation) {
        this.newPasswordConfirmation = newPasswordConfirmation;
    }

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

}
