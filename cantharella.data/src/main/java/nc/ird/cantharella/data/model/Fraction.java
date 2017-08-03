/*
 * #%L
 * Cantharella :: Data
 * $Id: Fraction.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.data/src/main/java/nc/ird/cantharella/data/model/Fraction.java $
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

import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import nc.ird.cantharella.data.validation.CollectionUniqueField;

import org.hibernate.search.annotations.IndexedEmbedded;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Modèle : Produit issue d'une purification
 * 
 * @author Adrien Cheype
 */
@Entity
@CollectionUniqueField(fieldName = "ref", pathToCollection = "purification.fractions")
@Embeddable
public class Fraction extends Produit {

    /** indice de la fraction */
    @Length(max = 5)
    @NotEmpty
    private String indice;

    // purification doit être à EAGER sinon dans certains cas, setProduit de ResultatTestBio rend une
    // LazyInitializationException (les setters semble être effectués après la requête http et par conséquent une fois
    // la session hibernate fermée)
    /** Manip de purification dont fait partie la fraction */
    @NotNull
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @IndexedEmbedded
    private Purification purification;

    /**
     * Constructor
     */
    public Fraction() {
        super();
    }

    /**
     * Rend le rendement calculé pour la fraction (masseObtenue / masseDepart)
     * 
     * @return le résultat ou null si les valeurs actuelles ne donnent pas un résultat cohérent
     */
    public Float getRendement() {
        if (getPurification() != null && getPurification().getMasseDepart() != null
                && getPurification().getMasseDepart().floatValue() != 0f && getMasseObtenue() != null) {
            Float rendement = getMasseObtenue().floatValue() / getPurification().getMasseDepart().floatValue();
            if (rendement <= 1f && rendement >= 0) {
                return rendement;
            }
        }
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isExtrait() {
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isFraction() {
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public Fraction clone() throws CloneNotSupportedException {
        Fraction clone = (Fraction) super.clone();
        clone.indice = indice;
        clone.purification = purification;
        return clone;
    }

    /**
     * indice getter
     * 
     * @return indice
     */
    public String getIndice() {
        return indice;
    }

    /**
     * indice setter
     * 
     * @param indice indice
     */
    public void setIndice(String indice) {
        this.indice = indice;
    }

    /**
     * purification getter
     * 
     * @return purification
     */
    public Purification getPurification() {
        return purification;
    }

    /**
     * purification setter
     * 
     * @param purification purification
     */
    public void setPurification(Purification purification) {
        this.purification = purification;
    }

}
