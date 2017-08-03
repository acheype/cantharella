/*
 * #%L
 * Cantharella :: Data
 * $Id: GenericDaoAspect.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.data/src/main/java/nc/ird/cantharella/data/dao/utils/GenericDaoAspect.java $
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
package nc.ird.cantharella.data.dao.utils;

import nc.ird.cantharella.data.exceptions.DataConstraintException;
import nc.ird.cantharella.data.exceptions.DataNotFoundException;

import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.stereotype.Component;

/**
 * Generic DAO aspect, automatically converts Spring data access exceptions into ou own exceptions
 * 
 * @author Mickael Tricot
 * @author Adrien Cheype
 */
@Aspect
// @Order NOT NEEDED YET (and not works yet), to survey :
// if this order is not defined, afterThrowing don't recuperate the exception throwed in the transactional aspect
// (this aspect is executed then) ---> OK now, still to survey
@Order(value = Ordered.HIGHEST_PRECEDENCE)
@Component
public final class GenericDaoAspect {

    /**
     * Convert a DataIntegrityViolationException into a DataConstraintException
     * 
     * @param e DataIntegrityViolationException
     * @throws DataConstraintException In any case
     */
    @AfterThrowing(pointcut = "execution(* nc.ird.cantharella.data.dao.GenericDao.*(..))", throwing = "e")
    public void afterThrowing(DataIntegrityViolationException e) throws DataConstraintException {
        throw new DataConstraintException(e);
    }

    /**
     * Convert a DataRetrievalFailureException into a DataNotFoundException
     * 
     * @param e DataRetrievalFailureException
     * @throws DataNotFoundException In any case
     */
    @AfterThrowing(pointcut = "execution(* nc.ird.cantharella.data.dao.GenericDao.*(..))", throwing = "e")
    public void afterThrowing(DataRetrievalFailureException e) throws DataNotFoundException {
        throw new DataNotFoundException(e);
    }
}
