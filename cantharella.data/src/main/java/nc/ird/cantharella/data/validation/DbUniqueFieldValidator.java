/*
 * #%L
 * Cantharella :: Data
 * $Id: DbUniqueFieldValidator.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.data/src/main/java/nc/ird/cantharella/data/validation/DbUniqueFieldValidator.java $
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

import java.io.Serializable;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import nc.ird.cantharella.data.dao.GenericDao;
import nc.ird.cantharella.data.exceptions.DataNotFoundException;
import nc.ird.cantharella.data.model.utils.AbstractModel;
import nc.ird.cantharella.utils.AssertTools;
import nc.ird.cantharella.utils.BeanTools;
import nc.ird.cantharella.utils.BeanTools.AccessType;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * Field unique validator TODO faire marcher la classe, erreur lors de l'accès au bean injecté
 * 
 * @author Adrien Cheype
 */
public final class DbUniqueFieldValidator implements ConstraintValidator<DbUniqueField, Object> {

    /**
     * field name of the list which must be unique
     */
    String fieldName;

    /**
     * The classe which contain the unique field
     */
    Class<? extends AbstractModel> entity;

    /** DAO */
    @Autowired
    private GenericDao dao;

    /** {@inheritDoc} */
    @Override
    public void initialize(DbUniqueField annotation) {
        this.fieldName = annotation.fieldName();
        this.entity = annotation.entity();
    }

    /** {@inheritDoc} */
    @Override
    public boolean isValid(Object value, ConstraintValidatorContext constraintContext) {
        AssertTools.assertNotNull(value);
        Serializable fieldVal = (Serializable) BeanTools.getValue(value, AccessType.GETTER, fieldName);
        if (fieldVal == null) {
            return false;
        }

        // valid if it doesn't exist different value or if it exists but with the same id (so same row in the db)
        if (!dao.exists(entity, fieldName, fieldVal)) {
            return true;
        }
        AbstractModel modelForm = (AbstractModel) value;
        AbstractModel modelWithSameVal;
        try {
            modelWithSameVal = dao.read(entity, fieldName, fieldVal);
        } catch (DataNotFoundException e) {
            return true; // never call, cover by dao.exists...
        }
        return modelForm.getIdValue().equals(modelWithSameVal.getIdValue());
    }

}