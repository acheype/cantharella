/*
 * #%L
 * Cantharella :: Data
 * $Id: AbstractModel.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.data/src/main/java/nc/ird/cantharella/data/model/utils/AbstractModel.java $
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
package nc.ird.cantharella.data.model.utils;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import javax.persistence.EmbeddedId;
import javax.persistence.Id;

import nc.ird.cantharella.data.exceptions.UnexpectedException;
import nc.ird.cantharella.utils.AssertTools;
import nc.ird.cantharella.utils.BeanTools;
import nc.ird.cantharella.utils.BeanTools.AccessType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract model
 * <p>
 * A model must not be final, and should override hashCode and equals methods.
 * </p>
 * 
 * @author Mickael Tricot
 * @author Adrien Cheype
 */
public abstract class AbstractModel implements Serializable {

    /** ID fields cache (for each model class name) */
    private static final Map<String, Field> ID_FIELDS = Collections.synchronizedMap(new TreeMap<String, Field>());

    /** Length: big text */
    protected static final int LENGTH_BIG_TEXT = 255;

    /** Length: long text */
    protected static final int LENGTH_LONG_TEXT = 100;

    /** Length: medium text */
    protected static final int LENGTH_MEDIUM_TEXT = 60;

    /** Length: tiny text */
    protected static final int LENGTH_TINY_TEXT = 10;

    /** Logger */
    private static final Logger LOG = LoggerFactory.getLogger(AbstractModel.class);

    /**
     * Retrieve the @Id or @EmbeddedId field
     * 
     * @return @Id or @EmbeddedId field
     * @throws UnexpectedException If it does not exist
     */
    public final Field getIdField() {
        return getIdField(this.getClass());
    }

    /**
     * Retrieve the @Id or @EmbeddedId field
     * 
     * @param clazz the class of the model
     * @return @Id or @EmbeddedId field
     * @throws UnexpectedException If it does not exist
     */
    public final static Field getIdField(final Class<? extends AbstractModel> clazz) {
        AssertTools.assertNotNull(clazz);
        Field field = null;
        String modelClassName = clazz.getName();
        field = ID_FIELDS.get(modelClassName);

        if (field == null) {
            try {
                field = BeanTools.getAnnotatedPrivateField(clazz, Id.class, EmbeddedId.class);
            } catch (NoSuchFieldException e) {
                LOG.error(e.getMessage(), e);
                throw new UnexpectedException(e);
            }
            ID_FIELDS.put(modelClassName, field);
        }

        return field;
    }

    /**
     * Retrieve the @Id or @EmbeddedId field value
     * 
     * @return @Id or @EmbeddedId field value
     * @throws UnexpectedException If the field does not exist
     */
    public final Serializable getIdValue() {
        return (Serializable) BeanTools.getValue(this, AccessType.GETTER, getIdField().getName());
    }

}