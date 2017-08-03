/*
 * #%L
 * Cantharella :: Data
 * $Id: PersonneDao.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.data/src/main/java/nc/ird/cantharella/data/dao/impl/PersonneDao.java $
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
package nc.ird.cantharella.data.dao.impl;

import nc.ird.cantharella.data.dao.AbstractModelDao;
import nc.ird.cantharella.data.model.Personne;
import nc.ird.cantharella.data.model.Utilisateur;
import nc.ird.cantharella.data.model.Utilisateur.TypeDroit;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

/** DAO : personne */
public final class PersonneDao extends AbstractModelDao {

    /** Criteria : compte les administrateurs */
    public static final DetachedCriteria CRITERIA_COUNT_ADMINS = DetachedCriteria.forClass(Utilisateur.class)
            .add(Restrictions.eq("typeDroit", TypeDroit.ADMINISTRATEUR))
            .add(Restrictions.eq("estValide", Boolean.TRUE)).setProjection(Projections.rowCount());

    /** Criteria : liste les administrateurs */
    public static final DetachedCriteria CRITERIA_LIST_ADMINS = DetachedCriteria.forClass(Utilisateur.class)
            .add(Restrictions.eq("typeDroit", TypeDroit.ADMINISTRATEUR))
            .add(Restrictions.eq("estValide", Boolean.TRUE));

    /** Criteria : liste les utilisateurs à valider */
    public static final DetachedCriteria CRITERIA_LIST_UTILISATEURS_INVALID = DetachedCriteria
            .forClass(Utilisateur.class).add(Restrictions.eq("estValide", Boolean.FALSE)).addOrder(Order.asc("nom"))
            .addOrder(Order.asc("prenom"));

    /** Criteria : liste les utilisateurs qui sont déjà valides */
    public static final DetachedCriteria CRITERIA_LIST_UTILISATEURS_VALID = DetachedCriteria
            .forClass(Utilisateur.class).add(Restrictions.eq("estValide", Boolean.TRUE)).addOrder(Order.asc("nom"))
            .addOrder(Order.asc("prenom"));

    /** SQL : créé un utilisateur à partir d'une personne */
    public static final String SQL_CREATE_UTILISATEUR_FROM_PERSONNE = String.format(
            "INSERT INTO %s(estValide, passwordHash, typeDroit, idPersonne) VALUES (?, ?, ?, ?);",
            Utilisateur.class.getSimpleName());

    /** SQL : supprime un utilisateur (mais pas la personne associée) */
    public static final String SQL_DELETE_UTILISATEUR = String.format("DELETE FROM %s WHERE idPersonne = ?;",
            Utilisateur.class.getSimpleName());

    /** Criteria : valeurs existantes du champ 'organisme' pour les personnes existantes */
    public static final DetachedCriteria CRITERIA_DISTINCT_PERSONNE_ORGANISMES = DetachedCriteria
            .forClass(Personne.class).setProjection(Projections.distinct(Projections.property("organisme")))
            .addOrder(Order.asc("organisme"));

    /**
     * Liste l'utilisateur qui a le courriel et le password donné et qui est un utilisateur validé
     * 
     * @param courriel Le courriel recherché
     * @param passwordHash Le password recherché
     * @return Le criteria
     **/
    public static DetachedCriteria getCriteriaAuthenticateUser(String courriel, String passwordHash) {
        return DetachedCriteria.forClass(Utilisateur.class).add(Restrictions.eq("courriel", courriel))
                .add(Restrictions.eq("passwordHash", passwordHash)).add(Restrictions.eq("estValide", Boolean.TRUE));
    }

    /**
     * Constructor (empêche l'instantiation)
     */
    private PersonneDao() {
        //
    }
}
