/*
 * #%L
 * Cantharella :: Data
 * $Id: CompositeId.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.data/src/main/java/nc/ird/cantharella/data/model/utils/CompositeId.java $
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

import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import nc.ird.cantharella.utils.BeanTools;
import nc.ird.cantharella.utils.GenericsTools;
import nc.ird.cantharella.utils.BeanTools.AccessType;

/**
 * Composite-ID
 * 
 * @param <PK1> Primary key 1 type
 * @param <PK2> Primary key 2 type
 */
@Embeddable
public class CompositeId<PK1 extends AbstractModel, PK2 extends AbstractModel> extends AbstractModel implements
        Cloneable {

    /** Primary key 1 */
    @ManyToOne
    @NotNull
    private PK1 pk1;

    /** Primary key 2 */
    @ManyToOne
    @NotNull
    private PK2 pk2;

    /** {@inheritDoc} */
    @Override
    public CompositeId<PK1, PK2> clone() throws CloneNotSupportedException {
        CompositeId<PK1, PK2> clone = GenericsTools.cast(super.clone());
        clone.pk1 = pk1;
        clone.pk2 = pk2;
        return clone;
    }

    /** {@inheritDoc} */
    @Override
    public boolean equals(Object obj) {
        return BeanTools.equals(this, obj, AccessType.GETTER, "pk1", "pk2");
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        return BeanTools.hashCode(this, pk1, pk2);
    }

    /**
     * pk1 getter
     * 
     * @return pk1
     */
    public PK1 getPk1() {
        return pk1;
    }

    /**
     * pk1 setter
     * 
     * @param pk1 pk1
     */
    public void setPk1(PK1 pk1) {
        this.pk1 = pk1;
    }

    /**
     * pk2 getter
     * 
     * @return pk2
     */
    public PK2 getPk2() {
        return pk2;
    }

    /**
     * pk2 setter
     * 
     * @param pk2 pk2
     */
    public void setPk2(PK2 pk2) {
        this.pk2 = pk2;
    }

}