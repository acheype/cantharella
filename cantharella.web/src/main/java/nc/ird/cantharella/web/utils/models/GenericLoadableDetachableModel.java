/*
 * #%L
 * Cantharella :: Web
 * $Id: GenericLoadableDetachableModel.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.web/src/main/java/nc/ird/cantharella/web/utils/models/GenericLoadableDetachableModel.java $
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
package nc.ird.cantharella.web.utils.models;

import java.io.Serializable;

import nc.ird.cantharella.data.dao.GenericDao;
import nc.ird.cantharella.data.exceptions.UnexpectedException;
import nc.ird.cantharella.data.model.utils.AbstractModel;
import nc.ird.cantharella.web.config.WebApplicationImpl;
import nc.ird.cantharella.utils.AssertTools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * Generic loadable/detachable model, it stores only the model class & ID, and retrieve the model from the DB when
 * necessary. Warning: do not use it in forms.
 * 
 * @author Mickael Tricot
 * @author Adrien Cheype
 * @param <M> Model type
 */
public final class GenericLoadableDetachableModel<M extends AbstractModel> extends LoadableDetachableModel<M> {

    /** Logger */
    private static final Logger LOG = LoggerFactory.getLogger(GenericLoadableDetachableModel.class);

    /** DAO */
    @SpringBean
    private GenericDao dao;

    /** Model class */
    private final Class<M> modelClass;

    /** Model ID */
    private final Serializable modelID;

    /**
     * Constructor
     * 
     * @param modelClass Model class
     * @param modelID Model ID
     */
    public GenericLoadableDetachableModel(Class<M> modelClass, Serializable modelID) {
        AssertTools.assertNotNull(modelClass);
        AssertTools.assertNotNull(modelID);
        this.modelClass = modelClass;
        this.modelID = modelID;
        WebApplicationImpl.injectSpringBeans(this);
    }

    /**
     * Constructor
     * 
     * @param model Model
     */
    @SuppressWarnings("unchecked")
    public GenericLoadableDetachableModel(M model) {
        this((Class<M>) model.getClass(), model.getIdValue());
        setObject(model);
    }

    /** {@inheritDoc} */
    @Override
    protected M load() {
        try {
            // LOG.debug("chargement LOAD, " + modelClass + "- id : " + modelID);
            return dao.read(modelClass, modelID);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            throw new UnexpectedException(e);
        }
    }

}
