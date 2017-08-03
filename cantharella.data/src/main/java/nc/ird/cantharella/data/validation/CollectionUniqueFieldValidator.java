/*
 * #%L
 * Cantharella :: Data
 * $Id: CollectionUniqueFieldValidator.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.data/src/main/java/nc/ird/cantharella/data/validation/CollectionUniqueFieldValidator.java $
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

import java.util.Collection;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import nc.ird.cantharella.utils.BeanTools;
import nc.ird.cantharella.utils.BeanTools.AccessType;
import nc.ird.cantharella.utils.CollectionTools;
import nc.ird.cantharella.utils.GenericsTools;

/**
 * Field unique validator
 * 
 * @author Adrien Cheype
 */
public final class CollectionUniqueFieldValidator implements ConstraintValidator<CollectionUniqueField, Object> {

    /**
     * field name of the list which must be unique
     */
    String fieldName;

    /**
     * properties path to access the collection which contain the beans, example : beanX.beanY.collZ
     */
    String pathToCollection;

    /** {@inheritDoc} */
    @Override
    public void initialize(CollectionUniqueField annotation) {
        this.fieldName = annotation.fieldName();
        this.pathToCollection = annotation.pathToCollection();
    }

    /** {@inheritDoc} */
    @Override
    public boolean isValid(Object value, ConstraintValidatorContext constraintContext) {
        Collection<Object> collection = GenericsTools.cast(BeanTools.getValueFromPath(value, AccessType.GETTER,
                this.pathToCollection));
        Object propertyVal = BeanTools.getValue(value, AccessType.GETTER, fieldName);

        // each value of the field must have one occurrence in the collection
        return (propertyVal == null)
                || CollectionTools.countWithValue(collection, fieldName, AccessType.GETTER, propertyVal) == 1;
    }

}