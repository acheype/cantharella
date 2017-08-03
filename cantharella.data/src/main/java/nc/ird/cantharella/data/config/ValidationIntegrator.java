package nc.ird.cantharella.data.config;

/*
 * #%L
 * Cantharella :: Data
 * $Id: ValidationIntegrator.java 148 2013-02-21 14:47:28Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.data/src/main/java/nc/ird/cantharella/data/config/ValidationIntegrator.java $
 * %%
 * Copyright (C) 2009 - 2013 IRD (Institut de Recherche pour le Developpement) and by respective authors (see below)
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

import javax.validation.Validation;
import javax.validation.ValidatorFactory;

import nc.ird.cantharella.data.validation.utils.ModelValidatorEventListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.integrator.spi.Integrator;
import org.hibernate.metamodel.source.MetadataImplementor;
import org.hibernate.service.spi.SessionFactoryServiceRegistry;

/**
 * Register cantharella validator into hibernate using integrator.
 * 
 * @author echatellier
 */
public class ValidationIntegrator implements Integrator {

    /** Logger */
    private static final Logger LOG = LoggerFactory.getLogger(ValidationIntegrator.class);

    /** {@inheritDoc} */
    @Override
    public void integrate(Configuration configuration, SessionFactoryImplementor sessionFactory,
            SessionFactoryServiceRegistry serviceRegistry) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Registering validator into hibernate");
        }

        // declare validator
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        ModelValidatorEventListener validationListener = new ModelValidatorEventListener(factory);

        // register validator
        EventListenerRegistry eventRegistry = serviceRegistry.getService(EventListenerRegistry.class);
        eventRegistry.prependListeners(EventType.PRE_INSERT, validationListener);
        eventRegistry.prependListeners(EventType.PRE_UPDATE, validationListener);
    }

    /** {@inheritDoc} */
    @Override
    public void integrate(MetadataImplementor metadata, SessionFactoryImplementor sessionFactory,
            SessionFactoryServiceRegistry serviceRegistry) {
        integrate((Configuration) null, sessionFactory, serviceRegistry);
    }

    /** {@inheritDoc} */
    @Override
    public void disintegrate(SessionFactoryImplementor sessionFactory, SessionFactoryServiceRegistry serviceRegistry) {

    }
}
