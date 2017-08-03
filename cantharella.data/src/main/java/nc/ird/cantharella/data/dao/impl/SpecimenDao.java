/*
 * #%L
 * Cantharella :: Data
 * $Id: SpecimenDao.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.data/src/main/java/nc/ird/cantharella/data/dao/impl/SpecimenDao.java $
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
import nc.ird.cantharella.data.model.Specimen;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;

/**
 * DAO specimens
 * 
 * @author Adrien Cheype
 */
public class SpecimenDao extends AbstractModelDao {

    /** Criteria : valeurs existantes du champ 'embranchement' existants pour les spécimens existants */
    public static final DetachedCriteria CRITERIA_DISTINCT_SPECIMEN_EMBRANCHEMENTS = DetachedCriteria
            .forClass(Specimen.class).setProjection(Projections.distinct(Projections.property("embranchement")))
            .addOrder(Order.asc("embranchement"));

    /** Criteria : valeurs existantes du champ 'famille' existants pour les spécimens existants */
    public static final DetachedCriteria CRITERIA_DISTINCT_SPECIMEN_FAMILLES = DetachedCriteria
            .forClass(Specimen.class).setProjection(Projections.distinct(Projections.property("famille")))
            .addOrder(Order.asc("famille"));

    /** Criteria : valeurs existantes du champ 'genre' existants pour les spécimens existants */
    public static final DetachedCriteria CRITERIA_DISTINCT_SPECIMEN_GENRES = DetachedCriteria.forClass(Specimen.class)
            .setProjection(Projections.distinct(Projections.property("genre"))).addOrder(Order.asc("genre"));

    /** Criteria : valeurs existantes du champ 'espece' existants pour les spécimens existants */
    public static final DetachedCriteria CRITERIA_DISTINCT_SPECIMEN_ESPECES = DetachedCriteria.forClass(Specimen.class)
            .setProjection(Projections.distinct(Projections.property("espece"))).addOrder(Order.asc("espece"));

    /** Criteria : valeurs existantes du champ 'sousEspece' existants pour les spécimens existants */
    public static final DetachedCriteria CRITERIA_DISTINCT_SPECIMEN_SOUSESPECES = DetachedCriteria
            .forClass(Specimen.class).setProjection(Projections.distinct(Projections.property("sousEspece")))
            .addOrder(Order.asc("sousEspece"));

    /** Criteria : valeurs existantes du champ 'variete' existants pour les spécimens existants */
    public static final DetachedCriteria CRITERIA_DISTINCT_SPECIMEN_VARIETES = DetachedCriteria
            .forClass(Specimen.class).setProjection(Projections.distinct(Projections.property("variete")))
            .addOrder(Order.asc("variete"));

    /** Criteria : valeurs existantes du champ 'variete' existants pour les spécimens existants */
    public static final DetachedCriteria CRITERIA_DISTINCT_LIEUX_DEPOT = DetachedCriteria.forClass(Specimen.class)
            .setProjection(Projections.distinct(Projections.property("lieuDepot"))).addOrder(Order.asc("lieuDepot"));

    /**
     * Constructeur (empêche l'instantiation)
     */
    private SpecimenDao() {
        //
    }

}
