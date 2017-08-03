/*
 * #%L
 * Cantharella :: Data
 * $Id: ModelValidatorImpl.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.data/src/main/java/nc/ird/cantharella/data/validation/utils/ModelValidatorImpl.java $
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.MessageInterpolator;
import javax.validation.MessageInterpolator.Context;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.metadata.ConstraintDescriptor;

import nc.ird.cantharella.data.model.utils.AbstractModel;
import nc.ird.cantharella.utils.AssertTools;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.support.MessageSourceAccessor;

/**
 * Generic model validator implementation
 * 
 * @author Mickael Tricot
 * @author Adrien Cheype
 */
public final class ModelValidatorImpl implements ModelValidator {

    /** Logger */
    private static final Logger LOG = LoggerFactory.getLogger(ModelValidatorImpl.class);

    /** Pattern error property: "property message" */
    private static final String PATTERN_ERROR_MESSAGE = "%s - %s";

    /** Pattern error property: "simpleClassName.property" */
    private static final String PATTERN_ERROR_PROPERTY = "%s.%s";

    /** Message interpolator */
    private final MessageInterpolator messageInterpolator;

    /** Internationalization messages */
    private MessageSourceAccessor[] messageSources;

    /** Validator */
    private final Validator validator;

    /**
     * Constructor
     * 
     * @param validatorFactory Validator factory
     * @param messageSourceAccessor Message source accessor
     */
    public ModelValidatorImpl(ValidatorFactory validatorFactory, MessageSourceAccessor... messageSourceAccessor) {
        AssertTools.assertNotNull(validatorFactory);
        messageInterpolator = validatorFactory.getMessageInterpolator();
        validator = validatorFactory.getValidator();
        messageSources = messageSourceAccessor;
    }

    /** {@inheritDoc} */
    @Override
    public <M extends AbstractModel> void debug(Class<M> modelClass, M model) {
        if (LOG.isDebugEnabled()) {
            for (ConstraintViolation<M> violation : validator.validate(model)) {
                LOG.debug(violation.getMessage());
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public <M> List<String> validate(M model, Locale locale, String... properties) {
        Set<ConstraintViolation<M>> violations = validate(model, properties);
        List<String> violationMessages = new ArrayList<String>(violations.size());
        for (final ConstraintViolation<M> violation : violations) {
            // Lookup in the bean class
            Class<?> clazz = violation.getRootBeanClass();
            // LOG.debug("clazz :" + clazz);
            String label = null;
            String labelDefault;
            // LOG.debug("getPropertyPath :" + violation.getPropertyPath().toString());
            if (StringUtils.isEmpty(violation.getPropertyPath().toString())) {
                labelDefault = getMessage(clazz.getSimpleName(), locale);
            } else {
                labelDefault = String
                        .format(PATTERN_ERROR_PROPERTY, clazz.getSimpleName(), violation.getPropertyPath());
            }
            // LOG.debug("labelDefault :" + labelDefault);
            try {
                label = getMessage(labelDefault, locale);
            } catch (NoSuchMessageException e) {
                //
            }
            // LOG.debug("label :" + label);
            // Lookup in the bean superclasses
            while (label == null && !Object.class.equals(clazz)) {
                clazz = clazz.getSuperclass();
                try {
                    if (StringUtils.isEmpty(violation.getPropertyPath().toString())) {
                        getMessage(clazz.getSimpleName(), locale);
                    } else {
                        getMessage(
                                String.format(PATTERN_ERROR_PROPERTY, clazz.getSimpleName(),
                                        violation.getPropertyPath()), locale);
                    }
                } catch (NoSuchMessageException e) {
                    //
                }
            }
            if (label == null) {
                label = labelDefault;
            }

            violationMessages.add(String.format(PATTERN_ERROR_MESSAGE, label,
                    messageInterpolator.interpolate(violation.getMessageTemplate(), new Context() {
                        @Override
                        public ConstraintDescriptor<?> getConstraintDescriptor() {
                            return violation.getConstraintDescriptor();
                        }

                        @Override
                        public Object getValidatedValue() {
                            return violation.getInvalidValue();
                        }

                        @Override
                        public <T> T unwrap(Class<T> type) {
                            //allow unwrapping into public super types
                            if (type.isAssignableFrom(Context.class)) {
                                return type.cast(this);
                            }
                            throw new ClassCastException();
                        }
                    }, locale)));
        }

        return violationMessages;
    }

    /**
     * Validate properties for a model
     * 
     * @param <M> Model type
     * @param model Model
     * @param properties Property names (all properties if empty array)
     * @return Constraint violations (not null)
     */
    private <M> Set<ConstraintViolation<M>> validate(M model, String... properties) {
        if (properties == null || properties.length == 0) {
            return validator.validate(model);
        }
        Set<ConstraintViolation<M>> violations = new HashSet<ConstraintViolation<M>>();
        for (String property : properties) {
            violations.addAll(validator.validateProperty(model, property));
        }
        return violations;
    }

    /**
     * Recuperate a msg from the msg accessor given to the class
     * 
     * @param key The message key
     * @param locale The used locale
     * @return The corresponding msg, null if no such key
     */
    private String getMessage(String key, Locale locale) {
        String msg = null;
        int i = 0;
        while (msg == null && i < messageSources.length) {
            MessageSourceAccessor curMsgSource = messageSources[i];
            msg = curMsgSource.getMessage(key, null, null, locale);
            i++;
        }
        return msg;
    }
}
