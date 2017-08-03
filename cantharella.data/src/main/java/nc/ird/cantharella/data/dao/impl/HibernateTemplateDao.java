/*
 * #%L
 * Cantharella :: Data
 * $Id: HibernateTemplateDao.java 268 2014-05-06 15:45:40Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.data/src/main/java/nc/ird/cantharella/data/dao/impl/HibernateTemplateDao.java $
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

import java.io.Serializable;
import java.util.List;

import javax.annotation.Resource;

import nc.ird.cantharella.data.dao.GenericDao;
import nc.ird.cantharella.data.model.utils.AbstractModel;
import nc.ird.cantharella.utils.AssertTools;
import nc.ird.cantharella.utils.GenericsTools;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.ObjectRetrievalFailureException;
import org.springframework.stereotype.Repository;

/**
 * Generic DAO implementation for Hibernate
 * 
 * @author Mickael Tricot
 * @author Adrien Cheype
 */
@Repository
public class HibernateTemplateDao implements GenericDao {

    @Resource
    private SessionFactory sessionFactory;

    /**
     * Empty constructor.
     */
    public HibernateTemplateDao() {

    }

    /**
     * Criteria : model from a property value
     * 
     * @param modelClass Model class
     * @param propertyName Property name
     * @param value Value
     * @return Criteria
     */
    private static DetachedCriteria criteriaByProperty(Class<? extends AbstractModel> modelClass, String propertyName,
            Serializable value) {
        return DetachedCriteria.forClass(modelClass).add(Restrictions.eq(propertyName, value));
    }

    /*
     * Constructor
     * @param sessionFactory Session factory
     *
    @Autowired
    public HibernateTemplateDao(SessionFactory sessionFactory) {
        setSessionFactory(sessionFactory);
        sessionFactory.getCurrentSession().setAllowCreate(false);
        sessionFactory.getCurrentSession().setCacheQueries(true);
        sessionFactory.getCurrentSession().afterPropertiesSet();
    }*/

    /**
     * {@inheritDoc}
     */
    @Override
    public long count(DetachedCriteria criteria) {
        AssertTools.assertNotNull(criteria);
        return (Long) list(criteria.setProjection(Projections.rowCount())).get(0);
    }

    /** {@inheritDoc} */
    @Override
    public <M extends AbstractModel> long count(Class<M> modelClass) {
        AssertTools.assertNotNull(modelClass);
        return (Long) list(DetachedCriteria.forClass(modelClass).setProjection(Projections.rowCount())).get(0);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long count(String hqlQuery, Object... parameters) {
        AssertTools.assertNotEmpty(hqlQuery);
        Query query = sessionFactory.getCurrentSession().createQuery(hqlQuery);
        for (int i = 0; i < parameters.length; i++) {
            query.setParameter(i, parameters[i]);
        }
        return (Long) query.iterate().next();
    }

    /** {@inheritDoc} */
    @Override
    public <M extends AbstractModel> void create(M model) {
        AssertTools.assertNotNull(model);
        sessionFactory.getCurrentSession().save(model);
    }

    /** {@inheritDoc} */
    @Override
    public <M extends AbstractModel> void createOrUpdate(M model) {
        AssertTools.assertNotNull(model);
        sessionFactory.getCurrentSession().saveOrUpdate(model);
    }

    /** {@inheritDoc} */
    @Override
    public <M extends AbstractModel> void delete(Class<M> modelClass, Serializable id) {
        AssertTools.assertNotNull(modelClass);
        AssertTools.assertNotNull(id);
        sessionFactory.getCurrentSession().delete(read(modelClass, id));
    }

    /** {@inheritDoc} */
    @Override
    public <M extends AbstractModel> void delete(M model) {
        AssertTools.assertNotNull(model);
        sessionFactory.getCurrentSession().delete(model);
    }

    /** {@inheritDoc} */
    @Override
    public List<?> list(DetachedCriteria criteria) {
        AssertTools.assertNotNull(criteria);
        Criteria execCriteria = criteria.getExecutableCriteria(sessionFactory.getCurrentSession());
        return execCriteria.list();
    }

    /** {@inheritDoc} */
    @Override
    public List<?> list(String hqlQuery, Object... parameters) {
        Query query = sessionFactory.getCurrentSession().createQuery(hqlQuery);
        for (int i = 0; i < parameters.length; i++) {
            query.setParameter(i, parameters[i]);
        }
        return query.list();
    }

    /** {@inheritDoc} */
    @Override
    public int execute(final String sqlQuery, final Object... parameters) {
        AssertTools.assertNotEmpty(sqlQuery);
        Query query = sessionFactory.getCurrentSession().createSQLQuery(sqlQuery);
        for (int i = 0; i < parameters.length; i++) {
            query.setParameter(i, parameters[i]);
        }
        int nbLines = Integer.valueOf(query.executeUpdate());
        // This way to execute requests shortcuts the Hibernate process, so we need to refresh the data cache
        sessionFactory.getCurrentSession().flush();
        sessionFactory.getCurrentSession().clear();
        return nbLines;
    }

    /** {@inheritDoc} */
    @Override
    public <M extends AbstractModel> boolean exists(Class<M> modelClass, Serializable id) {
        return count(criteriaByProperty(modelClass, AbstractModel.getIdField(modelClass).getName(), id).setProjection(
                Projections.rowCount())) > 0;
    }

    /** {@inheritDoc} */
    @Override
    public <M extends AbstractModel> boolean exists(Class<M> modelClass, String property, Serializable value) {
        return count(criteriaByProperty(modelClass, property, value).setProjection(Projections.rowCount())) > 0;
    }

    /** {@inheritDoc} */
    @Override
    public boolean exists(DetachedCriteria criteria) {
        return count(criteria) > 0;
    }

    /** {@inheritDoc} */
    @Override
    public <M extends AbstractModel> List<M> readList(Class<M> modelClass) {
        AssertTools.assertNotNull(modelClass);
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(modelClass);
        criteria.addOrder(Order.asc(AbstractModel.getIdField(modelClass).getName()));
        return GenericsTools.cast(criteria.list());
    }

    /** {@inheritDoc} */
    @Override
    public <M extends AbstractModel> List<M> readList(Class<M> modelClass, String... sortColumns) {
        AssertTools.assertNotNull(modelClass);
        AssertTools.assertNotEmpty(sortColumns);
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(modelClass);
        for (String sortCol : sortColumns) {
            criteria.addOrder(Order.asc(sortCol));
        }
        return GenericsTools.cast(criteria.list());
    }

    /** {@inheritDoc} */
    @Override
    public <M extends AbstractModel> List<M> readList(Class<M> modelClass, int firstResult, int maxResults) {
        AssertTools.assertNotNull(modelClass);
        AssertTools.assertPositive(firstResult);
        AssertTools.assertPositive(maxResults);
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(modelClass);
        criteria.addOrder(Order.asc(AbstractModel.getIdField(modelClass).getName()));
        criteria.setFirstResult(firstResult);
        criteria.setMaxResults(maxResults);
        return GenericsTools.cast(criteria.list());
    }

    /** {@inheritDoc} */
    @Override
    public <M extends AbstractModel> List<M> readList(Class<M> modelClass, int firstResult, int maxResults,
            String... sortColumns) {
        AssertTools.assertNotNull(modelClass);
        AssertTools.assertNotEmpty(sortColumns);
        AssertTools.assertPositive(firstResult);
        AssertTools.assertPositive(maxResults);
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(modelClass);
        for (String sortCol : sortColumns) {
            criteria.addOrder(Order.asc(sortCol));
        }
        criteria.setFirstResult(firstResult);
        criteria.setMaxResults(maxResults);
        return GenericsTools.cast(criteria.list());
    }

    /** {@inheritDoc} */
    @Override
    public <M extends AbstractModel> M read(Class<M> modelClass, Serializable id) {
        AssertTools.assertNotNull(modelClass);
        AssertTools.assertNotNull(id);

        M m = modelClass.cast(sessionFactory.getCurrentSession().get(modelClass, id));
        if (m == null) {
            throw new ObjectRetrievalFailureException(modelClass, id);
        }
        return m;
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    @Override
    public <M extends AbstractModel> M read(Class<M> modelClass, String uniqueProperty, Serializable value) {
        List<M> list = (List<M>) list(criteriaByProperty(modelClass, uniqueProperty, value));
        if (list.size() != 1) {
            throw new ObjectRetrievalFailureException(modelClass, value);
        }
        return list.get(0);
    }

    /** {@inheritDoc} */
    @Override
    public <M extends AbstractModel> void refresh(M model) {
        AssertTools.assertNotNull(model);
        sessionFactory.getCurrentSession().refresh(model);
    }

    /** {@inheritDoc} */
    @Override
    public <M extends AbstractModel> void update(M model) {
        AssertTools.assertNotNull(model);
        sessionFactory.getCurrentSession().update(model);
    }

    /** {@inheritDoc} */
    @Override
    public <M extends AbstractModel> void evict(M model) {
        AssertTools.assertNotNull(model);
        sessionFactory.getCurrentSession().evict(model);
    }

    /** {@inheritDoc} */
    @Override
    public <M extends AbstractModel> boolean contains(M model) {
        AssertTools.assertNotNull(model);
        return sessionFactory.getCurrentSession().contains(model);
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    @Override
    public <M extends AbstractModel> M merge(M model) {
        AssertTools.assertNotNull(model);
        return (M) sessionFactory.getCurrentSession().merge(model);
    }
}