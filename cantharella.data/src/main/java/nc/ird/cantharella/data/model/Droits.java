/*
 * #%L
 * Cantharella :: Data
 * $Id: Droits.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.data/src/main/java/nc/ird/cantharella/data/model/Droits.java $
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

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

import nc.ird.cantharella.utils.BeanTools;
import nc.ird.cantharella.utils.BeanTools.AccessType;

/**
 * Modèles : droits
 * 
 * @author Mickael Tricot
 * @author Adrien Cheype
 */
@Embeddable
public class Droits implements Serializable, Cloneable {

    /** Droits extrait */
    @NotNull
    private Boolean droitExtrait;

    /** Droits purification */
    @NotNull
    private Boolean droitPuri;

    /** Droits récolte */
    @NotNull
    private Boolean droitRecolte;

    /** Droits test */
    @NotNull
    private Boolean droitTestBio;

    /** {@inheritDoc} */
    @Override
    public Droits clone() throws CloneNotSupportedException {
        Droits clone = (Droits) super.clone();
        clone.droitExtrait = droitExtrait;
        clone.droitPuri = droitPuri;
        clone.droitRecolte = droitRecolte;
        clone.droitTestBio = droitTestBio;
        return clone;
    }

    /** {@inheritDoc} */
    @Override
    public boolean equals(Object obj) {
        return BeanTools.equals(this, obj, AccessType.GETTER, "droitExtrait", "droitPuri", "droitRecolte",
                "droitTestBio");
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        return BeanTools.hashCode(this, droitExtrait, droitPuri, droitRecolte, droitTestBio);
    }

    /**
     * droitExtrait getter
     * 
     * @return droitExtrait
     */
    public Boolean getDroitExtrait() {
        return droitExtrait;
    }

    /**
     * droitExtrait setter
     * 
     * @param droitExtrait droitExtrait
     */
    public void setDroitExtrait(Boolean droitExtrait) {
        this.droitExtrait = droitExtrait;
    }

    /**
     * droitPuri getter
     * 
     * @return droitPuri
     */
    public Boolean getDroitPuri() {
        return droitPuri;
    }

    /**
     * droitPuri setter
     * 
     * @param droitPuri droitPuri
     */
    public void setDroitPuri(Boolean droitPuri) {
        this.droitPuri = droitPuri;
    }

    /**
     * droitRecolte getter
     * 
     * @return droitRecolte
     */
    public Boolean getDroitRecolte() {
        return droitRecolte;
    }

    /**
     * droitRecolte setter
     * 
     * @param droitRecolte droitRecolte
     */
    public void setDroitRecolte(Boolean droitRecolte) {
        this.droitRecolte = droitRecolte;
    }

    /**
     * droitTestBio getter
     * 
     * @return droitTestBio
     */
    public Boolean getDroitTestBio() {
        return droitTestBio;
    }

    /**
     * droitTestBio setter
     * 
     * @param droitTestBio droitTestBio
     */
    public void setDroitTestBio(Boolean droitTestBio) {
        this.droitTestBio = droitTestBio;
    }

}
