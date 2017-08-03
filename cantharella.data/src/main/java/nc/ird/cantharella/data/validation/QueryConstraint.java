/*
 * #%L
 * Cantharella :: Data
 * $Id: QueryConstraint.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.data/src/main/java/nc/ird/cantharella/data/validation/QueryConstraint.java $
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

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * Constraint which execute a query in the database
 * 
 * @author Adrien Cheype
 */
@Constraint(validatedBy = QueryConstraintValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface QueryConstraint {
    /**
     * @return Groups
     */
    Class<?>[] groups() default {};

    /**
     * Error msg
     * 
     * @return the message
     */
    String message() default "{nc.ird.cantharella.data.validation.QueryConstraint.message}";

    /**
     * @return Payload
     */
    Class<? extends Payload>[] payload() default {};

    /**
     * Hql query to request
     * 
     * @return The query
     */
    String hql() default "";

}