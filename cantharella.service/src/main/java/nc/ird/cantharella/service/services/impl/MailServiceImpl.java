/*
 * #%L
 * Cantharella :: Service
 * $Id: MailServiceImpl.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.service/src/main/java/nc/ird/cantharella/service/services/impl/MailServiceImpl.java $
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
package nc.ird.cantharella.service.services.impl;

import java.util.Arrays;
import java.util.Collection;

import nc.ird.cantharella.data.model.Personne;
import nc.ird.cantharella.data.model.Utilisateur;
import nc.ird.cantharella.service.config.ServiceContext;
import nc.ird.cantharella.service.exceptions.EmailException;
import nc.ird.cantharella.service.services.MailService;
import nc.ird.cantharella.utils.AssertTools;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

/**
 * E-mail service implementation
 * 
 * @author Mickael Tricot
 * @author Adrien Cheype
 */
@Service
public final class MailServiceImpl implements MailService {

    /** Logger */
    private static final Logger LOG = LoggerFactory.getLogger(MailServiceImpl.class);

    /** E-mail message template */
    @Autowired
    private SimpleMailMessage mailMessage;

    /** E-mail sender */
    @Autowired
    private MailSender mailSender;

    /** {@inheritDoc} */
    @Override
    public void sendMail(Collection<Utilisateur> recipients, String subject, String text) throws EmailException {
        sendMail(recipients, subject, text, null);
    }

    /** {@inheritDoc} */
    @Override
    public void sendMail(Collection<Utilisateur> recipients, String subject, String text, String replyTo)
            throws EmailException {
        AssertTools.assertCollectionNotNull(recipients);
        String[] courriels = new String[recipients.size()];
        int i = 0;
        for (Utilisateur recipient : recipients) {
            courriels[i++] = recipient.getCourriel();
        }
        sendMail(subject, text, replyTo, courriels);
    }

    /** {@inheritDoc} */
    @Override
    public void sendMail(Personne recipient, String subject, String text) throws EmailException {
        AssertTools.assertNotNull(recipient);
        sendMail(subject, text, null, recipient.getCourriel());
    }

    /**
     * Send an e-mail
     * 
     * @param subject Subject
     * @param text Text
     * @param replyTo Reply to
     * @param recipients Recipients
     * @throws EmailException In case of error while sending an e-mail
     */
    private void sendMail(String subject, String text, String replyTo, String... recipients) throws EmailException {
        LOG.info("sendMail " + subject + " [to] " + Arrays.toString(recipients));
        if (ServiceContext.isMailActivated()) {
            AssertTools.assertNotEmpty(subject);
            AssertTools.assertNotEmpty(text);
            AssertTools.assertNotEmpty(recipients);
            AssertTools.assertArrayNotEmpty(recipients);
            // Validate e-mail
            mailMessage.setTo(recipients);
            mailMessage.setSubject(subject);
            mailMessage.setText(text);
            if (!StringUtils.isEmpty(replyTo)) {
                mailMessage.setReplyTo(replyTo);
            }
            try {
                mailSender.send(mailMessage);
            } catch (Exception e) {
                throw new EmailException(e);
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public void sendMailQuietly(Collection<Utilisateur> recipients, String subject, String text) {
        try {
            sendMail(recipients, subject, text);
        } catch (EmailException e) {
            LOG.warn(e.toString());
        }
    }

    /** {@inheritDoc} */
    @Override
    public void sendMailQuietly(Personne recipient, String subject, String text) {
        try {
            sendMail(recipient, subject, text);
        } catch (EmailException e) {
            LOG.warn(e.toString());
        }
    }
}