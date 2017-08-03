/*
 * #%L
 * Cantharella :: Web
 * $Id: ContactModel.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.web/src/main/java/nc/ird/cantharella/web/pages/model/ContactModel.java $
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

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Modèle : contact (message send)
 * 
 * @author Mickael Tricot
 */
public class ContactModel implements Serializable {

    /** E-mail (from) */
    @NotEmpty
    @Email
    private String mail;

    /** Message */
    @NotEmpty
    private String message;

    /** Subject */
    @NotEmpty
    private String subject;

    /**
     * mail getter
     * 
     * @return mail
     */
    public String getMail() {
        return mail;
    }

    /**
     * mail setter
     * 
     * @param mail mail
     */
    public void setMail(String mail) {
        this.mail = mail;
    }

    /**
     * message getter
     * 
     * @return message
     */
    public String getMessage() {
        return message;
    }

    /**
     * message setter
     * 
     * @param message message
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * subject getter
     * 
     * @return subject
     */
    public String getSubject() {
        return subject;
    }

    /**
     * subject setter
     * 
     * @param subject subject
     */
    public void setSubject(String subject) {
        this.subject = subject;
    }
}
