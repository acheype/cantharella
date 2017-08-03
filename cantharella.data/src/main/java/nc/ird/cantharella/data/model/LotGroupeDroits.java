/*
 * #%L
 * Cantharella :: Data
 * $Id: LotGroupeDroits.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.data/src/main/java/nc/ird/cantharella/data/model/LotGroupeDroits.java $
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
package nc.ird.cantharella.data.model;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import nc.ird.cantharella.data.model.utils.AbstractModel;
import nc.ird.cantharella.data.model.utils.CompositeId;

/**
 * Modèle : droits d'un groupe sur un lot
 * 
 * @author Mickael Tricot
 */
@Entity
@Table
public class LotGroupeDroits extends AbstractModel {

    /** Droits */
    @NotNull
    private Droits droits;

    /** ID */
    @EmbeddedId
    @NotNull
    private CompositeId<Lot, Groupe> id;

    /**
     * Constructeur
     */
    public LotGroupeDroits() {
        droits = new Droits();
        id = new CompositeId<Lot, Groupe>();
    }

    /**
     * droits getter
     * 
     * @return droits
     */
    public Droits getDroits() {
        return droits;
    }

    /**
     * droits setter
     * 
     * @param droits droits
     */
    public void setDroits(Droits droits) {
        this.droits = droits;
    }

    /**
     * id getter
     * 
     * @return id
     */
    public CompositeId<Lot, Groupe> getId() {
        return id;
    }

    /**
     * id setter
     * 
     * @param id id
     */
    public void setId(CompositeId<Lot, Groupe> id) {
        this.id = id;
    }

}