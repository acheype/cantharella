/*
 * #%L
 * Cantharella :: Data
 * $Id: GenericDao.java 268 2014-05-06 15:45:40Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.data/src/main/java/nc/ird/cantharella/data/dao/GenericDao.java $
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
package nc.ird.cantharella.data.dao;

import java.io.Serializable;
import java.util.List;

import nc.ird.cantharella.data.exceptions.DataConstraintException;
import nc.ird.cantharella.data.exceptions.DataNotFoundException;
import nc.ird.cantharella.data.model.utils.AbstractModel;

import org.hibernate.criterion.DetachedCriteria;
import org.springframework.transaction.annotation.Transactional;

/**
 * Generic DAO (works for all models)
 * 
 * @author Mickael Tricot
 * @author Adrien Cheype
 */
public interface GenericDao {

    /**
     * Count the number of rows
     * 
     * @param <M> Model type
     * @param modelClass Model class (not null)
     * @return Number of rows
     */
    @Transactional(readOnly = true)
    <M extends AbstractModel> long count(Class<M> modelClass);

    /**
     * Count the number of result given by the search criteria. The criteria must contain the count projection.
     * 
     * @param criteria The criteria
     * @return The number
     */
    @Transactional(readOnly = true)
    long count(DetachedCriteria criteria);

    /**
     * Count the number of result given by the hql search. The hql query must contain the count operator.
     * 
     * @param hqlQuery The HQL query string
     * @param parameters Parameters (in replacement of ?)
     * @return The number
     */
    @Transactional(readOnly = true)
    long count(String hqlQuery, Object... parameters);

    /**
     * Create a model
     * 
     * @param <M> Model type
     * @param model Model (not null)
     * @throws DataConstraintException When there is a problem of data integrity
     */
    @Transactional
    <M extends AbstractModel> void create(M model) throws DataConstraintException;

    /**
     * Create or update a model
     * 
     * @param <M> Model type
     * @param model Model (not null)
     * @throws DataConstraintException When there is a problem of data integrity
     */
    @Transactional
    <M extends AbstractModel> void createOrUpdate(M model) throws DataConstraintException;

    /**
     * Merge a model. Usefull when several detached objects are loaded (createOrUpdate throws NonUniqueObjectException
     * in this case).
     * 
     * @param <M> Model type
     * @param model Model (not null)
     * @throws DataConstraintException When there is a problem of data integrity
     */
    // @Transactional
    // public <M extends AbstractModel> void merge(M model) throws DataConstraintException;
    /**
     * Delete a model
     * 
     * @param <M> model type
     * @param modelClass Model class (not null)
     * @param id Model ID (not null)
     * @throws DataNotFoundException Model not found
     * @throws DataConstraintException Model linked to other objects
     */
    @Transactional
    <M extends AbstractModel> void delete(Class<M> modelClass, Serializable id) throws DataConstraintException,
            DataNotFoundException;

    /**
     * Delete a model
     * 
     * @param <M> Model type
     * @param model Model (not null)
     * @throws DataNotFoundException Model not found
     * @throws DataConstraintException Model linked to other objects
     */
    @Transactional
    <M extends AbstractModel> void delete(M model) throws DataNotFoundException, DataConstraintException;

    /**
     * List entities given by the search criteria
     * 
     * @param criteria The criteria
     * @return List of results
     */
    @Transactional(readOnly = true)
    List<?> list(DetachedCriteria criteria);

    /**
     * List entities given by the hql query
     * 
     * @param hqlQuery The HQL query string
     * @param parameters Parameters (in replacement of ?)
     * @return List of results
     */
    @Transactional(readOnly = true)
    List<?> list(String hqlQuery, Object... parameters);

    /**
     * Execute SQL query. This method is not recommanded because it shortcuts the Hibernate process. Do not use it if it
     * is not really necessary.
     * 
     * @param sqlQuery SQL query
     * @param parameters Parameters (in replacement of ?)
     * @return Number of rows affected
     * @throws DataConstraintException In case of data integrity exceptions
     */
    @Transactional
    int execute(String sqlQuery, Object... parameters) throws DataConstraintException;

    /**
     * Test model existence
     * 
     * @param <M> Model type
     * @param modelClass Model class (not null)
     * @param id ID (not null)
     * @return Existence
     */
    @Transactional(readOnly = true)
    <M extends AbstractModel> boolean exists(Class<M> modelClass, Serializable id);

    /**
     * Test model existence
     * 
     * @param <M> Model type
     * @param modelClass Model class
     * @param property Property name
     * @param value Property value
     * @return Existence
     */
    @Transactional(readOnly = true)
    <M extends AbstractModel> boolean exists(Class<M> modelClass, String property, Serializable value);

    /**
     * Test existence of a criteria
     * 
     * @param criteria The criteria
     * @return True if exists, otherwise false
     */
    @Transactional(readOnly = true)
    boolean exists(DetachedCriteria criteria);

    /**
     * Read models
     * 
     * @param <M> Model type
     * @param modelClass Model class (not null)
     * @return Models
     */
    @Transactional(readOnly = true)
    <M extends AbstractModel> List<M> readList(Class<M> modelClass);

    /**
     * Read models
     * 
     * @param <M> Model type
     * @param sortColumn column name for the models sort
     * @param modelClass Model class (not null)
     * @return Models
     */
    // @Transactional(readOnly = true)
    // <M extends AbstractModel> List<M> readList(Class<M> modelClass, String sortColumn);
    /**
     * Read models
     * 
     * @param <M> Model type
     * @param sortColumns column names for the models sort (with the order)
     * @param modelClass Model class (not null)
     * @return Models
     */
    @Transactional(readOnly = true)
    <M extends AbstractModel> List<M> readList(Class<M> modelClass, String... sortColumns);

    /**
     * Read models according to the order of @Id or @EmbeddedId column
     * 
     * @param <M> Model type
     * @param modelClass Model class (not null)
     * @param firstResult First result (not negative)
     * @param maxResults Max results (not negative)
     * @param sortColumns column names for the models sort (with the order)
     * @return Models
     */
    @Transactional(readOnly = true)
    <M extends AbstractModel> List<M> readList(Class<M> modelClass, int firstResult, int maxResults,
            String... sortColumns);

    /**
     * Read models according to the order of @Id or @EmbeddedId column
     * 
     * @param <M> Model type
     * @param modelClass Model class (not null)
     * @param firstResult First result (not negative)
     * @param maxResults Max results (not negative)
     * @return Models
     */
    @Transactional(readOnly = true)
    <M extends AbstractModel> List<M> readList(Class<M> modelClass, int firstResult, int maxResults);

    /**
     * Read a model
     * 
     * @param <M> Model type
     * @param modelClass Model class (not null)
     * @param id Model ID (not null)
     * @return Model (null if not found)
     * @throws DataNotFoundException Model not found
     */
    // if Transactional(readOnly = true) -> Bug ...
    // so nothing until this bug is fixed
    <M extends AbstractModel> M read(Class<M> modelClass, Serializable id) throws DataNotFoundException;

    /**
     * Read and fetch a model
     * 
     * @param <M> Model class
     * @param modelClass Model class
     * @param uniqueProperty Unique property name
     * @param value Unique property value
     * @return Model
     * @throws DataNotFoundException Model not found
     */
    @Transactional(readOnly = true)
    <M extends AbstractModel> M read(Class<M> modelClass, String uniqueProperty, Serializable value)
            throws DataNotFoundException;

    /**
     * Refresh a model object
     * 
     * @param <M> Model type
     * @param model Model object
     */
    @Transactional(readOnly = true)
    <M extends AbstractModel> void refresh(M model);

    /**
     * Update a model
     * 
     * @param <M> Model type
     * @param model Model (not null)
     * @throws DataNotFoundException Model not found
     * @throws DataConstraintException Model already exists
     */
    @Transactional
    <M extends AbstractModel> void update(M model) throws DataNotFoundException, DataConstraintException;

    /**
     * Evict a model. Remove from the hibernate session.
     * 
     * @param <M> Model type
     * @param model Model (not null)
     */
    @Transactional
    <M extends AbstractModel> void evict(M model);

    /**
     * Check if the given model is in the hibernate session.
     * 
     * @param <M> Model type
     * @param model Model (not null)
     * @return If the given model is in the hibernate session
     */
    @Transactional(readOnly = true)
    <M extends AbstractModel> boolean contains(M model);

    /**
     * Merge a model from the hibernate session.
     * 
     * @param <M> Model type
     * @param model Model (not null)
     * @return merged object
     */
    @Transactional
    <M extends AbstractModel> M merge(M model);
}
