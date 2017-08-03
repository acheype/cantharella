/*
 * #%L
 * Cantharella :: Data
 * $Id: ModelValidatorEventListener.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.data/src/main/java/nc/ird/cantharella/data/validation/utils/ModelValidatorEventListener.java $
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
package nc.ird.cantharella.data.validation.utils;

import java.util.HashSet;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import nc.ird.cantharella.utils.AssertTools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.hibernate.EntityMode;
import org.hibernate.event.spi.AbstractPreDatabaseOperationEvent;
import org.hibernate.event.spi.PreInsertEvent;
import org.hibernate.event.spi.PreInsertEventListener;
import org.hibernate.event.spi.PreUpdateEvent;
import org.hibernate.event.spi.PreUpdateEventListener;

/**
 * Validation event listener
 * 
 * @author Mickael Tricot
 * @author Adrien Cheype
 */
public final class ModelValidatorEventListener implements PreInsertEventListener, PreUpdateEventListener {

    /**
     * Operation
     */
    private enum Operation {
        /** Insert */
        INSERT,
        /** Update */
        UPDATE;
    }

    /** Logger */
    private static final Logger LOG = LoggerFactory.getLogger(ModelValidatorEventListener.class);

    /** Validator */
    private final Validator validator;

    /**
     * Constructor
     * 
     * @param factory Validator factory
     */
    public ModelValidatorEventListener(ValidatorFactory factory) {
        AssertTools.assertNotNull(factory);
        validator = factory.getValidator();
    }

    /** {@inheritDoc} */
    @Override
    public boolean onPreInsert(PreInsertEvent event) {
        validate(event, Operation.INSERT);
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public boolean onPreUpdate(PreUpdateEvent event) {
        validate(event, Operation.UPDATE);
        return false;
    }

    /**
     * Validation
     * 
     * @param event Event
     * @param operation Operation
     */
    private void validate(AbstractPreDatabaseOperationEvent event, Operation operation) {
        Object o = event.getEntity();
        if (event.getEntity() != null && event.getPersister().getEntityMode() == EntityMode.POJO) {
            Set<ConstraintViolation<Object>> violations = validator.validate(o);
            if (!violations.isEmpty()) {
                LOG.error("Validation before " + operation.name() + " " + o.getClass().getName() + ":");
                for (ConstraintViolation<Object> violation : violations) {
                    LOG.error("- " + violation.getPropertyPath() + ": " + violation.getMessage());
                }
                throw new ConstraintViolationException("Validation failed before " + operation.name() + " "
                        + o.getClass().getName(), new HashSet<ConstraintViolation<?>>(violations));
            }
        }
    }
}
