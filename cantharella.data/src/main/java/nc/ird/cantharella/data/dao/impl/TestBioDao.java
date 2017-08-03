/*
 * #%L
 * Cantharella :: Data
 * $Id: TestBioDao.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.data/src/main/java/nc/ird/cantharella/data/dao/impl/TestBioDao.java $
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

import nc.ird.cantharella.data.model.MethodeTestBio;
import nc.ird.cantharella.data.model.ResultatTestBio;
import nc.ird.cantharella.data.model.ResultatTestBio.TypeResultat;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

/**
 * DAO : test bio dao
 * 
 * @author Mickael Tricot
 * @author Adrien Cheype
 */
public class TestBioDao {

    /**
     * Criteria : liste les résultats de tests bio qui sont de type "produit". L'ensemble est trié par (cible, produit,
     * id)
     */
    public static final DetachedCriteria CRITERIA_LIST_RESULTATS_TYPE_PRODUIT = DetachedCriteria
            .forClass(ResultatTestBio.class, "r").add(Restrictions.eq("r.typeResultat", TypeResultat.PRODUIT))
            .createCriteria("testBio", "t").createCriteria("t.methode", "m").addOrder(Order.asc("m.cible"))
            .createCriteria("r.produit", "p").addOrder(Order.asc("p.ref")).addOrder(Order.asc("r.id"));

    /** Criteria : compte les résultats de tests bio qui sont de type "produit" */
    public static final DetachedCriteria CRITERIA_COUNT_RESULTATS_TYPE_PRODUIT = DetachedCriteria
            .forClass(ResultatTestBio.class).add(Restrictions.eq("typeResultat", TypeResultat.PRODUIT))
            .setProjection(Projections.rowCount());

    /** Criteria : valeurs existantes du champ 'domaines' existants pour les méthodes de tests biologiques */
    public static final DetachedCriteria CRITERIA_DISTINCT_DOMAINES_METHODES = DetachedCriteria
            .forClass(MethodeTestBio.class).setProjection(Projections.distinct(Projections.property("domaine")))
            .addOrder(Order.asc("domaine"));

    /** Criteria : valeurs existantes du champ 'uniteResultat' existants pour les méthodes de tests biologiques */
    public static final DetachedCriteria CRITERIA_DISTINCT_UNITES_RESULTAT_METHODES = DetachedCriteria
            .forClass(MethodeTestBio.class).setProjection(Projections.distinct(Projections.property("uniteResultat")))
            .addOrder(Order.asc("uniteResultat"));

    /** Criteria : valeurs existantes du champ 'uniteResultat' existants pour les méthodes de tests biologiques */
    public static final DetachedCriteria CRITERIA_DISTINCT_PRODUITS_TEMOINS = DetachedCriteria
            .forClass(ResultatTestBio.class).setProjection(Projections.distinct(Projections.property("produitTemoin")))
            .addOrder(Order.asc("produitTemoin"));

    /**
     * Constructor (empêche l'instantiation)
     */
    private TestBioDao() {
        //
    }

}
