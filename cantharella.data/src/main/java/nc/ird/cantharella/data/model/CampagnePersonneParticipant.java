/*
 * #%L
 * Cantharella :: Data
 * $Id: CampagnePersonneParticipant.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.data/src/main/java/nc/ird/cantharella/data/model/CampagnePersonneParticipant.java $
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
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Type;

import nc.ird.cantharella.data.model.utils.AbstractModel;
import nc.ird.cantharella.data.model.utils.CompositeId;

/**
 * Modèle : personne participant à une campagne
 * 
 * @author Mickael Tricot
 */
@Entity
@Table
public class CampagnePersonneParticipant extends AbstractModel implements Cloneable {

    /** Complément */
    @Lob
    @Type(type = "org.hibernate.type.StringClobType")
    // see HHH-6105
    private String complement;

    /** ID */
    @EmbeddedId
    @NotNull
    private CompositeId<Campagne, Personne> id;

    /**
     * Constructor
     */
    public CampagnePersonneParticipant() {
        id = new CompositeId<Campagne, Personne>();
    }

    /** {@inheritDoc} */
    @Override
    public final CampagnePersonneParticipant clone() throws CloneNotSupportedException {
        CampagnePersonneParticipant clone = (CampagnePersonneParticipant) super.clone();
        clone.id = id.clone();
        clone.complement = complement;
        return clone;
    }

    /**
     * complement getter
     * 
     * @return complement
     */
    public String getComplement() {
        return complement;
    }

    /**
     * complement setter
     * 
     * @param complement complement
     */
    public void setComplement(String complement) {
        this.complement = complement;
    }

    /**
     * id getter
     * 
     * @return id
     */
    public CompositeId<Campagne, Personne> getId() {
        return id;
    }

    /**
     * id setter
     * 
     * @param id id
     */
    public void setId(CompositeId<Campagne, Personne> id) {
        this.id = id;
    }
}