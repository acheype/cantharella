/*
 * #%L
 * Cantharella :: Data
 * $Id: DbUniqueField.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.data/src/main/java/nc/ird/cantharella/data/validation/DbUniqueField.java $
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

import nc.ird.cantharella.data.model.utils.AbstractModel;

/**
 * Check if the a field value of the bean is unique in a collection. The field name and the access to the collection is
 * given to this annotation.
 * 
 * @author Adrien Cheype
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DbUniqueFieldValidator.class)
@Documented
public @interface DbUniqueField {

    /**
     * @return Groups
     */
    Class<?>[] groups() default {};

    /**
     * @return Message key
     */
    String message() default "{nc.ird.cantharella.data.validation.UniqueFieldList.message}";

    /**
     * @return Payload
     */
    Class<? extends Payload>[] payload() default {};

    /**
     * @return The classe which contain the unique field
     */
    Class<? extends AbstractModel> entity();

    /**
     * @return the name of the field which must be unique
     */
    String fieldName();

}