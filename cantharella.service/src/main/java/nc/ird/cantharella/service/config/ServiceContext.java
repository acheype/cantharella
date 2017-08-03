/*
 * #%L
 * Cantharella :: Service
 * $Id: ServiceContext.java 268 2014-05-06 15:45:40Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.service/src/main/java/nc/ird/cantharella/service/config/ServiceContext.java $
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
package nc.ird.cantharella.service.config;

import java.util.Properties;

import nc.ird.cantharella.data.config.DataContext;
import nc.ird.cantharella.data.exceptions.UnexpectedException;
import nc.ird.cantharella.data.model.Utilisateur;
import nc.ird.cantharella.data.model.Utilisateur.TypeDroit;
import nc.ird.cantharella.service.utils.normalizers.PersonneNormalizer;
import nc.ird.cantharella.service.utils.normalizers.utils.Normalizer;
import nc.ird.cantharella.utils.CantharellaConfig;
import nc.ird.cantharella.utils.PasswordTools;

import org.nuiton.config.ArgumentsParserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;

/**
 * Spring context for the service layer
 * 
 * @author Mickael Tricot
 * @author Adrien Cheype
 */
// Load the data-layer configuration
@Import(DataContext.class)
// Scans for @Repository, @Service and @Component
@ComponentScan(basePackages = { "nc.ird.cantharella.service.services", "nc.ird.cantharella.service.utils" })
// Enable @Transactional support -> not work with <aop:aspectj-autoproxy /> in xml context file
// @EnableTransactionManagement
// This is a configuration class
@Configuration
public abstract class ServiceContext {

    /**
     * Logger
     */
    private static final Logger LOG = LoggerFactory.getLogger(ServiceContext.class);

    /** Mail SMTP: port */
    private static final int MAIL_SMTP_PORT = 25;

    /** Mail SMTP: time out */
    private static final int MAIL_SMTP_TIMEOUT = 10000;

    /** Mail activation (thus mails can be disabled for test purposes) */
    private static boolean mailActivated = true;

    /** Admin default courriel */
    @Value("${admin.courriel}")
    protected String adminCourrielProperty;

    /** Admin default password */
    @Value("${admin.password}")
    protected String adminPasswordProperty;

    /** E-mail debug mode */
    @Value("${mail.debug}")
    protected boolean mailDebugProperty;

    /** E-mail "from" */
    @Value("${mail.from}")
    protected String mailFromProperty;

    /** E-mail SMTP host */
    @Value("${mail.host}")
    protected String mailHostProperty;

    /**
     * mailActivated getter
     * 
     * @return mailActivated
     */
    public static boolean isMailActivated() {
        return mailActivated;
    }

    /**
     * mailActivated setter
     * 
     * @param mailActivated mailActivated
     */
    public static void setMailActivated(boolean mailActivated) {
        ServiceContext.mailActivated = mailActivated;
    }

    /**
     * @return Default admin, if none already exists
     */
    @Bean
    public Utilisateur defaultAdmin() {
        Utilisateur admin = new Utilisateur();
        admin.setTypeDroit(TypeDroit.ADMINISTRATEUR);
        admin.setAdressePostale("BP A5");
        admin.setCodePostal("98848");
        admin.setCourriel(adminCourrielProperty);
        admin.setNom("ADMIN");
        admin.setOrganisme("IRD");
        admin.setPasswordHash(PasswordTools.sha1(adminPasswordProperty));
        admin.setCodePays("FR");
        admin.setPrenom("Admin");
        admin.setVille("Nouméa");
        Normalizer.normalize(PersonneNormalizer.class, admin);
        return admin;
    }

    /**
     * @return E-mail message template
     */
    @Bean
    public SimpleMailMessage mailMessage() {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(mailFromProperty);
        return mailMessage;
    }

    /**
     * @return E-mail sender
     */
    @Bean
    public MailSender mailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        Properties properties = new Properties();
        properties.put("mail.debug", mailDebugProperty);
        properties.put("mail.smtp.connectiontimeout", MAIL_SMTP_TIMEOUT);
        properties.put("mail.smtp.timeout", MAIL_SMTP_TIMEOUT);
        mailSender.setJavaMailProperties(properties);
        mailSender.setHost(mailHostProperty);
        mailSender.setPort(MAIL_SMTP_PORT);
        mailSender.setDefaultEncoding(DataContext.ENCODING);
        mailSender.setProtocol(JavaMailSenderImpl.DEFAULT_PROTOCOL);
        return mailSender;
    }

    /**
     * @return Internationalization messages
     */
    @Bean
    public MessageSourceAccessor serviceMessageSource() {
        // The ResourceBundleMessageSource does not handle UTF-8, so we use the Reloadable one
        ReloadableResourceBundleMessageSource serviceMessageSource = new ReloadableResourceBundleMessageSource();
        serviceMessageSource.setBasename("service");
        serviceMessageSource.setDefaultEncoding(DataContext.ENCODING);
        serviceMessageSource.setCacheSeconds(-1);
        return new MessageSourceAccessor(serviceMessageSource);
    }

    /**
     * Set the service layer properties for the cantharella configuration
     * 
     * @return The placeholder configurer which get the service layer properties
     */
    @Bean
    public static PropertySourcesPlaceholderConfigurer properties() {
        PropertySourcesPlaceholderConfigurer pspc = new PropertySourcesPlaceholderConfigurer();
        try {
            pspc.setProperties(CantharellaConfig.getProperties());
        } catch (ArgumentsParserException e) {
            LOG.error(e.getMessage(), e);
            throw new UnexpectedException(e);
        }
        pspc.setIgnoreUnresolvablePlaceholders(true);
        return pspc;
    }
}
