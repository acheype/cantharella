/*
 * #%L
 * Cantharella :: Service
 * $Id: PersonneService.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.service/src/main/java/nc/ird/cantharella/service/services/PersonneService.java $
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

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import nc.ird.cantharella.data.exceptions.DataConstraintException;
import nc.ird.cantharella.data.exceptions.DataNotFoundException;
import nc.ird.cantharella.data.model.Personne;
import nc.ird.cantharella.data.model.Utilisateur;
import nc.ird.cantharella.service.exceptions.EmailException;
import nc.ird.cantharella.service.utils.normalizers.EmailNormalizer;
import nc.ird.cantharella.service.utils.normalizers.PersonneNormalizer;
import nc.ird.cantharella.service.utils.normalizers.utils.Normalize;

/**
 * Service : personnes, utilisateurs
 * 
 * @author Mickael Tricot
 * @author Adrien Cheype
 */
public interface PersonneService {

    /**
     * Créée un administrateur par défaut s'il n'y en a pas
     * 
     * @throws DataConstraintException Si la personne existe déjà (champs uniques)
     */
    void checkOrCreateAdmin() throws DataConstraintException;

    /**
     * Compte le nombre de personnes
     * 
     * @return Nombre de personnes
     */
    long countPersonnes();

    /**
     * Création d'une personne
     * 
     * @param personne Personne
     * @throws DataConstraintException Si la personne existe déjà (champs uniques)
     */
    void createPersonne(@Normalize(PersonneNormalizer.class) Personne personne) throws DataConstraintException;

    /**
     * Enregistrement d'un utilisateur
     * 
     * @param utilisateur Utilisateur
     * @throws DataConstraintException Si l'utilisateur existe déjà (champs uniques)
     */
    void createUtilisateur(@Normalize(PersonneNormalizer.class) Utilisateur utilisateur) throws DataConstraintException;

    /**
     * Supprime une personne
     * 
     * @param personne Personne
     * @throws DataConstraintException Si des données sont liées à la personne
     */
    void deletePersonne(Personne personne) throws DataConstraintException;

    /**
     * Supprimer un utilisateur, le transformer en personne si il a des données liées. On vérifie avant qu'il y aura
     * toujours un administrateur.
     * 
     * @param utilisateur Utilisateur
     * @throws DataConstraintException Si l'utilisateur est encore référencé (créateur d'entités)
     * @throws EmailException En cas d'erreur dans l'envoi d'e-mail
     */
    void deleteUtilisateur(Utilisateur utilisateur) throws DataConstraintException, EmailException;

    /**
     * Hachage du mot de passe
     * 
     * @param password Mot de passe
     * @return Mot de passe haché
     */
    String hashPassword(String password);

    /**
     * Liste les personnes (triés par nom, prénom). Les utilisateurs non valides sont exclus.
     * 
     * @return Personnes
     */
    @Transactional(readOnly = true)
    List<Personne> listPersonnes();

    /**
     * Liste les personnes (triés par nom, prénom) avec y compris les utilisateurs invalides.
     * 
     * @return Personnes
     */
    @Transactional(readOnly = true)
    List<Personne> listPersonnesWithInvalidUsers();

    /**
     * Liste les organismes existants pour les personnes
     * 
     * @return Les organismes
     */
    @Transactional(readOnly = true)
    List<String> listPersonneOrganismes();

    /**
     * Liste les utilisateurs à valider
     * 
     * @return Utilisateurs à valider
     */
    @Transactional(readOnly = true)
    List<Utilisateur> listUtilisateursInvalid();

    /**
     * Liste les utilisateurs valides
     * 
     * @return Utilisateurs valides
     */
    @Transactional(readOnly = true)
    List<Utilisateur> listUtilisateursValid();

    /**
     * Charge une personne
     * 
     * @param idPersonne Identifiant
     * @return Utilisateur
     * @throws DataNotFoundException Si la personne n'existe pas
     */
    Personne loadPersonne(Integer idPersonne) throws DataNotFoundException;

    /**
     * Charge une personne
     * 
     * @param courriel Courriel
     * @return Utilisateur
     * @throws DataNotFoundException Si la personne n'existe pas
     */
    Personne loadPersonne(@Normalize(EmailNormalizer.class) String courriel) throws DataNotFoundException;

    /**
     * Charge un utilisateur
     * 
     * @param idPersonne Identifiant
     * @return Utilisateur
     * @throws DataNotFoundException Si l'utilisateur n'existe pas
     */
    Utilisateur loadUtilisateur(Integer idPersonne) throws DataNotFoundException;

    /**
     * Charge un utilisateur
     * 
     * @param courriel Courriel
     * @return Utilisateur
     * @throws DataNotFoundException Si l'utilisateur n'existe pas
     */
    Utilisateur loadUtilisateur(@Normalize(EmailNormalizer.class) String courriel) throws DataNotFoundException;

    /**
     * Authentifie un utilisateur. L'utilisateur ne doit pas seulement exister mais être également validé.
     * 
     * @param courriel Courriel
     * @param passwordHash Mot de passe haché
     * @return Vrai si l'authentification réussit
     */
    boolean authenticateUtilisateur(@Normalize(EmailNormalizer.class) String courriel, String passwordHash);

    /**
     * Rafraichit une personne (pour éviter des LazyLoadingException)
     * 
     * @param personne Personne
     */
    void refreshPersonne(Personne personne);

    /**
     * Refuser un utilisateur, et le prévient par e-mail (supprimé de la BD ou transformé en personne si données liées)
     * 
     * @param utilisateur Utilisateur
     */
    void rejectUtilisateur(Utilisateur utilisateur);

    /**
     * Met à jour le mot de passe (perdu) d'un utilisateur, et lui envoie un e-mail
     * 
     * @param courriel Courriel
     * @throws DataNotFoundException Si le courriel n'existe pas
     * @throws EmailException En cas d'erreur dans l'envoi d'e-mail
     */
    void resetPasswordUtilisateur(@Normalize(EmailNormalizer.class) String courriel) throws DataNotFoundException,
            EmailException;

    /**
     * Envoie un e-mail aux administrateurs
     * 
     * @param subject Sujet
     * @param message Message
     * @param replyTo Expéditeur
     * @throws EmailException En cas d'erreur lors de l'envoi
     */
    void sendMailAdmins(String subject, String message, String replyTo) throws EmailException;

    /**
     * Met à jour et transforme une personne en utilisateur, génère un mot de passe automatique et le prévient par
     * e-mail
     * 
     * @param personne Personne
     * @return Utilisateur créé
     * @throws EmailException En cas d'erreur dans l'envoi d'e-mail
     * @throws DataConstraintException Si la personne existe déjà (champs uniques)
     */
    Utilisateur updateAndCreateUtilisateur(@Normalize(PersonneNormalizer.class) Personne personne)
            throws EmailException, DataConstraintException;

    /**
     * Met à jour une personne
     * 
     * @param personne Personne
     * @throws DataConstraintException Si la personne existe déjà (champs uniques)
     */
    void updatePersonne(@Normalize(PersonneNormalizer.class) Personne personne) throws DataConstraintException;

    /**
     * Met à jour un utilisateur, et lui envoie un e-mail si cela a été effectuée par un administrateur
     * 
     * @param utilisateur Utilisateur
     * @param admin Effectuée par un administrateur ?
     * @throws DataConstraintException Si l'utilisateur existe déjà (champs uniques)
     */
    void updateUtilisateur(@Normalize(PersonneNormalizer.class) Utilisateur utilisateur, boolean admin)
            throws DataConstraintException;

    /**
     * Valide et met à jour un utilisateur, et le prévient par e-mail
     * 
     * @param utilisateur Utilisateur
     * @throws DataConstraintException Si l'utilisateur existe déjà (champs uniques)
     */
    void validAndUpdateUtilisateur(@Normalize(PersonneNormalizer.class) Utilisateur utilisateur)
            throws DataConstraintException;

    /**
     * Test de droits pour la modification ou suppression d'une personne (ou par héritage d'un utilisateur)
     * 
     * @param personne Personne à mettre à jour
     * @param modifieur L'utilisateur qui fait la modification
     * @return vrai si l'utilisateur peut modifier ou supprimer la personne
     */
    boolean updateOrDeletePersonneEnabled(Personne personne, Utilisateur modifieur);
}
