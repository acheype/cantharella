/*
 * #%L
 * Cantharella :: Service
 * $Id: PersonneServiceImpl.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.service/src/main/java/nc/ird/cantharella/service/services/impl/PersonneServiceImpl.java $
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

import java.util.List;

import javax.annotation.Resource;

import nc.ird.cantharella.data.dao.GenericDao;
import nc.ird.cantharella.data.dao.impl.PersonneDao;
import nc.ird.cantharella.data.exceptions.DataConstraintException;
import nc.ird.cantharella.data.exceptions.DataNotFoundException;
import nc.ird.cantharella.data.exceptions.UnexpectedException;
import nc.ird.cantharella.data.model.Personne;
import nc.ird.cantharella.data.model.Utilisateur;
import nc.ird.cantharella.data.model.Utilisateur.TypeDroit;
import nc.ird.cantharella.service.exceptions.EmailException;
import nc.ird.cantharella.service.services.MailService;
import nc.ird.cantharella.service.services.PersonneService;
import nc.ird.cantharella.service.utils.normalizers.EmailNormalizer;
import nc.ird.cantharella.service.utils.normalizers.utils.Normalize;
import nc.ird.cantharella.utils.AssertTools;
import nc.ird.cantharella.utils.PasswordTools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.orm.ObjectRetrievalFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implémentation du service personne
 * 
 * @author Mickael Tricot
 * @author Adrien Cheype
 */
@Service
public final class PersonneServiceImpl implements PersonneService {

    /** Logger */
    private static final Logger LOG = LoggerFactory.getLogger(PersonneServiceImpl.class);

    /** Accès aux données */
    @Autowired
    private GenericDao dao;

    /** Administrateur par défaut */
    @Autowired
    private Utilisateur defaultAdmin;

    /** Mail service */
    @Autowired
    private MailService mailService;

    /** Messages d'internationalisation */
    @Resource(name = "serviceMessageSource")
    private MessageSourceAccessor messages;

    /** {@inheritDoc} */
    @Override
    public void checkOrCreateAdmin() throws DataConstraintException {
        if (countAdmins() == 0L) {
            defaultAdmin.setTypeDroit(TypeDroit.ADMINISTRATEUR);
            defaultAdmin.setValide(Boolean.TRUE);
            plainCreateUtilisateur(defaultAdmin);
        }
    }

    /**
     * Compte le nombre d'administrateurs
     * 
     * @return Nombre d'administrateurs
     */
    private Long countAdmins() {
        return dao.count(PersonneDao.CRITERIA_COUNT_ADMINS);
    }

    /** {@inheritDoc} */
    @Override
    public long countPersonnes() {
        return dao.count(Personne.class);
    }

    /** {@inheritDoc} */
    @Override
    public void createPersonne(Personne personne) throws DataConstraintException {
        LOG.info("createPersonne " + personne.getCourriel());
        try {
            dao.create(personne);
        } catch (DataIntegrityViolationException e) {
            throw new DataConstraintException(e);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void createUtilisateur(Utilisateur utilisateur) throws DataConstraintException {
        try {
            plainCreateUtilisateur(utilisateur);
        } catch (DataIntegrityViolationException e) {
            throw new DataConstraintException(e);
        }
        mailService.sendMailQuietly(utilisateur, messages.getMessage("register.subject"),
                messages.getMessage("register.text"));
        mailService.sendMailQuietly(
                listAdmins(),
                messages.getMessage("register.subject.admin"),
                messages.getMessage("register.text.admin", new Object[] { utilisateur.getPrenom(),
                        utilisateur.getNom(), utilisateur.getCourriel() }));
    }

    /** {@inheritDoc} */
    @Override
    public void deletePersonne(Personne personne) throws DataConstraintException {
        AssertTools.assertNotNull(personne);
        LOG.info("deletePersonne " + personne.getIdPersonne());
        AssertTools.assertNotNull(personne.getIdPersonne());
        try {
            dao.delete(personne);
        } catch (DataNotFoundException e) {
            LOG.error(e.getMessage(), e);
            throw new UnexpectedException(e);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void deleteUtilisateur(Utilisateur utilisateur) throws DataConstraintException, EmailException {
        deleteUtilisateur(utilisateur, messages.getMessage("delete.subject"), messages.getMessage("delete.text"));
    }

    /**
     * @param utilisateur
     * @param mailSubject
     * @param mailText
     * @throws EmailException
     * @throws DataConstraintException
     */
    /**
     * Supprime un utilisateur, ou le transforme en personne s'il a des données liées, et lui envoie un e-mail
     * 
     * @param utilisateur Utilisateur
     * @param mailSubject E-mail subject
     * @param mailText E-mail text
     * @throws EmailException En cas d'erreur lors de l'envoi de mail
     * @throws DataConstraintException Si l'utilisateur est encore référencé (créateur d'entités)
     */
    @Transactional(rollbackFor = EmailException.class)
    public void deleteUtilisateur(Utilisateur utilisateur, String mailSubject, String mailText) throws EmailException,
            DataConstraintException {
        AssertTools.assertNotNull(utilisateur);
        LOG.info("deleteUtilisateur " + utilisateur.getIdPersonne());
        AssertTools.assertNotNull(utilisateur.getIdPersonne());
        try {
            dao.delete(utilisateur);
        } catch (DataConstraintException e) {
            dao.execute(PersonneDao.SQL_DELETE_UTILISATEUR, utilisateur.getIdPersonne());
        } catch (DataNotFoundException e) {
            LOG.error(e.getMessage(), e);
            throw new UnexpectedException(e);
        }
        mailService.sendMail(utilisateur, mailSubject, mailText);
    }

    /** {@inheritDoc} */
    @Override
    public String hashPassword(String password) {
        AssertTools.assertNotEmpty(password);
        return PasswordTools.sha1(password);
    }

    /**
     * Liste les administrateurs
     * 
     * @return Administrateurs
     */
    @SuppressWarnings("unchecked")
    private List<Utilisateur> listAdmins() {
        return (List<Utilisateur>) dao.list(PersonneDao.CRITERIA_LIST_ADMINS);
    }

    /** {@inheritDoc} */
    @Override
    public List<Personne> listPersonnes() {
        List<Personne> allPersonnes = dao.readList(Personne.class, "nom", "prenom");
        allPersonnes.removeAll(listUtilisateursInvalid());
        return allPersonnes;
    }

    /** {@inheritDoc} */
    @Override
    public List<Personne> listPersonnesWithInvalidUsers() {
        return dao.readList(Personne.class, new String[] { "nom", "prenom" });
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    @Override
    public List<String> listPersonneOrganismes() {
        return (List<String>) dao.list(PersonneDao.CRITERIA_DISTINCT_PERSONNE_ORGANISMES);
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    @Override
    public List<Utilisateur> listUtilisateursInvalid() {
        return (List<Utilisateur>) dao.list(PersonneDao.CRITERIA_LIST_UTILISATEURS_INVALID);
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    @Override
    public List<Utilisateur> listUtilisateursValid() {
        return (List<Utilisateur>) dao.list(PersonneDao.CRITERIA_LIST_UTILISATEURS_VALID);
    }

    /** {@inheritDoc} */
    @Override
    public Personne loadPersonne(Integer idPersonne) throws DataNotFoundException {
        AssertTools.assertNotNull(idPersonne);
        try {
            return dao.read(Personne.class, idPersonne);
        } catch (DataRetrievalFailureException e) {
            throw new DataNotFoundException(e);
        }
    }

    /** {@inheritDoc} */
    @Override
    public Personne loadPersonne(String courriel) throws DataNotFoundException {
        AssertTools.assertNotNull(courriel);
        return dao.read(Personne.class, "courriel", courriel);
    }

    /** {@inheritDoc} */
    @Override
    public Utilisateur loadUtilisateur(Integer idPersonne) throws DataNotFoundException {
        AssertTools.assertNotNull(idPersonne);
        try {
            return dao.read(Utilisateur.class, idPersonne);
        } catch (DataRetrievalFailureException e) {
            throw new DataNotFoundException(e);
        }
    }

    /** {@inheritDoc} */
    @Override
    public Utilisateur loadUtilisateur(String courriel) throws DataNotFoundException {
        AssertTools.assertNotEmpty(courriel);
        Personne personne = loadPersonne(courriel);
        if (!(personne instanceof Utilisateur)) {
            throw new DataNotFoundException(new ObjectRetrievalFailureException(Utilisateur.class, courriel));
        }
        return (Utilisateur) personne;
    }

    /** {@inheritDoc} */
    @Override
    public boolean authenticateUtilisateur(@Normalize(EmailNormalizer.class) String courriel, String passwordHash) {
        AssertTools.assertNotEmpty(courriel);
        AssertTools.assertNotEmpty(passwordHash);
        return dao.exists(PersonneDao.getCriteriaAuthenticateUser(courriel, passwordHash));
    }

    /**
     * Enregistre un utilisateur de façon brute (pas de conversion d'exceptions, ni d'envoi d'e-mail)
     * 
     * @param utilisateur Utilisateur
     * @throws DataConstraintException Si l'utilisateur existe déjà
     */
    private void plainCreateUtilisateur(Utilisateur utilisateur) throws DataConstraintException {
        AssertTools.assertNotNull(utilisateur);
        LOG.info("createUtilisateur " + utilisateur.getCourriel());
        if (utilisateur.getTypeDroit() == null) {
            utilisateur.setTypeDroit(TypeDroit.UTILISATEUR);
        }
        if (utilisateur.isValide() == null) {
            utilisateur.setValide(Boolean.FALSE);
        }
        dao.create(utilisateur);
    }

    /** {@inheritDoc} */
    @Override
    public void refreshPersonne(Personne personne) {
        AssertTools.assertNotNull(personne);
        dao.refresh(personne);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void rejectUtilisateur(Utilisateur utilisateur) {
        try {
            deleteUtilisateur(utilisateur, messages.getMessage("reject.subject"), messages.getMessage("reject.text"));
        } catch (EmailException e) {
            // Quiet
        } catch (DataConstraintException e) {
            LOG.error(e.getMessage());
            // cas impossible normalement car un utilisateur non validé ne peut avoir créé des entités
        }
    }

    /** {@inheritDoc} */
    @Override
    @Transactional(rollbackFor = { DataNotFoundException.class, EmailException.class })
    public void resetPasswordUtilisateur(String courriel) throws DataNotFoundException, EmailException {
        LOG.info("reListPasswordUtilisateur " + courriel);
        Utilisateur utilisateur;
        try {
            utilisateur = loadUtilisateur(courriel);
        } catch (DataNotFoundException e) {
            LOG.warn("Unknown e-mail: " + courriel);
            throw e;
        }

        String newPassword = PasswordTools.random();
        utilisateur.setPasswordHash(hashPassword(newPassword));
        try {
            dao.update(utilisateur);
        } catch (DataConstraintException e) {
            LOG.error(e.getMessage(), e);
            throw new UnexpectedException(e);
        }
        mailService.sendMail(utilisateur, messages.getMessage("updatePassword.subject"),
                messages.getMessage("updatePassword.text", new Object[] { newPassword }));
    }

    /** {@inheritDoc} */
    @Override
    public void sendMailAdmins(String subject, String message, String replyTo) throws EmailException {
        LOG.info("sendMailAdmins " + subject + " [reply to] " + replyTo);
        mailService.sendMail(listAdmins(), messages.getMessage("contact.subject", new String[] { subject }),
                messages.getMessage("contact.body", new String[] { replyTo, message }), replyTo);
    }

    /** {@inheritDoc} */
    @Override
    @Transactional(rollbackFor = { EmailException.class, DataAccessException.class, UnexpectedException.class })
    public Utilisateur updateAndCreateUtilisateur(Personne personne) throws EmailException, DataConstraintException {
        LOG.info("updateAndCreateUtilisateur " + personne.getIdPersonne());
        String password = PasswordTools.random();
        try {
            dao.update(personne);
        } catch (DataNotFoundException e) {
            LOG.error(e.getMessage(), e);
            throw new UnexpectedException(e);
        }
        dao.execute(PersonneDao.SQL_CREATE_UTILISATEUR_FROM_PERSONNE, Boolean.TRUE, hashPassword(password),
                TypeDroit.UTILISATEUR.ordinal(), personne.getIdPersonne());
        Utilisateur utilisateur;
        try {
            utilisateur = dao.read(Utilisateur.class, personne.getIdPersonne());
        } catch (DataNotFoundException e) {
            LOG.error(e.getMessage(), e);
            throw new UnexpectedException(e);
        }
        mailService.sendMail(utilisateur, messages.getMessage("create.subject"),
                messages.getMessage("create.text", new Object[] { password }));
        return utilisateur;
    }

    /** {@inheritDoc} */
    @Override
    public void updatePersonne(Personne personne) throws DataConstraintException {
        LOG.info("updatePersonne " + personne.getIdPersonne());
        try {
            dao.update(personne);
        } catch (DataNotFoundException e) {
            LOG.error(e.getMessage(), e);
            throw new UnexpectedException(e);
        }
    }

    /**
     * Met à jour un utilisateur
     * 
     * @param utilisateur Utilisateur
     * @throws DataConstraintException Si l'utilisateur existe déjà (champs uniques)
     */
    private void updateUtilisateur(Utilisateur utilisateur) throws DataConstraintException {
        AssertTools.assertNotNull(utilisateur);
        LOG.info("updateUtilisateur " + utilisateur.getCourriel());
        try {
            dao.update(utilisateur);
        } catch (DataNotFoundException e) {
            LOG.error(e.getMessage(), e);
            throw new UnexpectedException(e);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void updateUtilisateur(Utilisateur utilisateur, boolean admin) throws DataConstraintException {
        updateUtilisateur(utilisateur);
        if (admin) {
            mailService.sendMailQuietly(utilisateur, messages.getMessage("update.subject"),
                    messages.getMessage("update.text"));
        }
    }

    /** {@inheritDoc} */
    @Override
    public void validAndUpdateUtilisateur(Utilisateur utilisateur) throws DataConstraintException {
        utilisateur.setValide(true);
        updateUtilisateur(utilisateur);
        mailService.sendMailQuietly(utilisateur, messages.getMessage("valid.subject"),
                messages.getMessage("valid.text"));
    }

    /** {@inheritDoc} */
    @Override
    public boolean updateOrDeletePersonneEnabled(Personne personne, Utilisateur modifieur) {
        AssertTools.assertNotNull(personne);
        AssertTools.assertNotNull(modifieur);
        return !(personne instanceof Utilisateur) || modifieur.getTypeDroit() == TypeDroit.ADMINISTRATEUR;
    }
}