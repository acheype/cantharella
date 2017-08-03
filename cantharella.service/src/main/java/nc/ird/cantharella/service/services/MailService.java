/*
 * #%L
 * Cantharella :: Service
 * $Id: MailService.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.service/src/main/java/nc/ird/cantharella/service/services/MailService.java $
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
package nc.ird.cantharella.service.services;

import java.util.Collection;

import nc.ird.cantharella.data.model.Personne;
import nc.ird.cantharella.data.model.Utilisateur;
import nc.ird.cantharella.service.exceptions.EmailException;

/**
 * Service: e-mail
 * 
 * @author Mickael Tricot
 * @author Adrien Cheype
 */
public interface MailService {

    /**
     * Send an e-mail
     * 
     * @param recipients Recipients (not empty, no null object)
     * @param subject Subject (not empty)
     * @param text Text (not empty)
     * @throws EmailException When an e-mail error occurs
     */
    void sendMail(Collection<Utilisateur> recipients, String subject, String text) throws EmailException;

    /**
     * Send an e-mail
     * 
     * @param recipients Recipients (not empty, no null object)
     * @param subject Subject (not empty)
     * @param text Text (not empty)
     * @param replyTo Reply to
     * @throws EmailException When an e-mail error occurs
     */
    void sendMail(Collection<Utilisateur> recipients, String subject, String text, String replyTo)
            throws EmailException;

    /**
     * Send an e-mail
     * 
     * @param recipient Recipient (not null)
     * @param subject Subject (not empty)
     * @param text Text (not empty)
     * @throws EmailException When an e-mail error occurs
     */
    void sendMail(Personne recipient, String subject, String text) throws EmailException;

    /**
     * Send an e-mail quietly (without throwing exceptions)
     * 
     * @param recipients Recipients (not empty, no null object)
     * @param subject Subject (not empty)
     * @param text Text (not empty)
     */
    void sendMailQuietly(Collection<Utilisateur> recipients, String subject, String text);

    /**
     * Send an e-mail quietly (without throwing exceptions)
     * 
     * @param recipient Recipient (not null)
     * @param subject Subject (not empty)
     * @param text Text (not empty)
     */
    void sendMailQuietly(Personne recipient, String subject, String text);
}