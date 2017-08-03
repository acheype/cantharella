/*
 * #%L
 * Cantharella :: Data
 * $Id: QueryConstraintValidator.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.data/src/main/java/nc/ird/cantharella/data/validation/QueryConstraintValidator.java $
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
package nc.ird.cantharella.data.validation;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Validator for QueryConstraint
 * 
 * @Autowired marche avec la déclaration d'un validatorFactory de type LocalValidatorFactoryBean, existe seulement
 *            depuis Spring 3
 * @author Adrien Cheype
 */
public class QueryConstraintValidator implements ConstraintValidator<QueryConstraint, String> {

    /** Logger */
    private static final Logger LOG = LoggerFactory.getLogger(QueryConstraintValidator.class);

    /** Hql query to request */
    private String hql;

    /** The collection of named parameters in the HQL statement */
    private String[] params;

    /** session factory injected */
    @Autowired
    private SessionFactory sessionFactory;

    /**
     * {@inheritDoc}
     */
    @Override
    public void initialize(QueryConstraint queryConstraint) {
        this.hql = queryConstraint.hql();
        this.params = createParameterList(this.hql);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isValid(String domainObject, ConstraintValidatorContext context) {
        BeanWrapper beanWrapper = new BeanWrapperImpl(domainObject);

        if (sessionFactory != null) {
            LOG.debug("Enabled - Validating constraint with: ");
            LOG.debug(hql);

            Session session = sessionFactory.openSession();
            Transaction tx = session.beginTransaction();
            Query query = session.createQuery(this.hql);

            for (String parameterName : this.params) {
                query.setParameter(parameterName, beanWrapper.getPropertyValue(parameterName));
            }

            boolean result = (Long) query.uniqueResult() == 0;
            // boolean result = query.list().size() == 0;

            tx.commit();
            session.close();

            return result;
        }

        return true;
    }

    /**
     * Extracts the named parameters from the specified HQL statement.
     * 
     * @param query the HQL statement to parse
     * @return an array of all the named parameters (of the form :name) found in the provided string
     */
    private String[] createParameterList(final String query) {
        final Matcher matcher = Pattern.compile(":[^\\s]*").matcher(query);
        List<String> paramList = new ArrayList<String>();
        while (matcher.find()) {
            paramList.add(this.hql.substring(matcher.start() + 1, matcher.end()));
        }

        return paramList.toArray(new String[paramList.size()]);
    }

    // public boolean isValid(String domainObject) {
    // return isValid(domainObject, null);
    // }

    /**
     * Method to determine whether or not the value passes validation.
     * <p>
     * Validation in this case refers to a value being unique.
     * 
     * @param value the value to validate for uniqueness
     * @return true if the value is unique, false otherwise
     */
    /*
     * public boolean isValid(final Object value) { Query query =
     * sessionFactory.getCurrentSession().createQuery(this.hql); Class valueClass = value.getClass(); Field field;
     * for(int i = 0; i < this.params.length; i++) { try { field = valueClass.getDeclaredField(this.params[i]);
     * field.setAccessible(true); query.setParameter(this.params[i], (null != field.get(value)) ? field.get(value) :
     * ""); } catch(final NoSuchFieldException e) { throw new SystemException(e.getMessage()); } catch(final
     * IllegalAccessException e) { throw new SystemException(e.getMessage()); } } return query.list().size() == 0; }
     */

}