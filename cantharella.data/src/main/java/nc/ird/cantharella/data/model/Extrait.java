/*
 * #%L
 * Cantharella :: Data
 * $Id: Extrait.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.data/src/main/java/nc/ird/cantharella/data/model/Extrait.java $
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

import org.hibernate.search.annotations.IndexedEmbedded;

import nc.ird.cantharella.data.validation.CollectionUniqueField;

/**
 * Modèle : Produit issue d'une extraction
 * 
 * @author Adrien Cheype
 */
@Entity
@CollectionUniqueField(fieldName = "ref", pathToCollection = "extraction.extraits")
@Embeddable
public class Extrait extends Produit {

    /** Type d'extrait */
    @NotNull
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    private TypeExtrait typeExtrait;

    /** Manip d'extraction dont fait partie l'extrait */
    @NotNull
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @IndexedEmbedded
    private Extraction extraction;

    /**
     * Constructor
     */
    public Extrait() {
        super();
    }

    /**
     * Rend le rendement calculé pour l'extrait (masseObtenue / masseDepart)
     * 
     * @return le résultat ou null si les valeurs actuelles ne donnent pas un résultat cohérent
     */
    public Float getRendement() {
        if (getExtraction() != null && getExtraction().getMasseDepart() != null
                && getExtraction().getMasseDepart().floatValue() != 0f && getMasseObtenue() != null) {
            Float rendement = getMasseObtenue().floatValue() / getExtraction().getMasseDepart().floatValue();
            if (rendement <= 1f && rendement >= 0) {
                return rendement;
            }
        }
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isExtrait() {
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isFraction() {
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public Extrait clone() throws CloneNotSupportedException {
        Extrait clone = (Extrait) super.clone();
        clone.typeExtrait = typeExtrait;
        clone.extraction = extraction;
        return clone;
    }

    /**
     * typeExtrait getter
     * 
     * @return typeExtrait
     */
    public TypeExtrait getTypeExtrait() {
        return typeExtrait;
    }

    /**
     * typeExtrait setter
     * 
     * @param typeExtrait typeExtrait
     */
    public void setTypeExtrait(TypeExtrait typeExtrait) {
        this.typeExtrait = typeExtrait;
    }

    /**
     * extraction getter
     * 
     * @return extraction
     */
    public Extraction getExtraction() {
        return extraction;
    }

    /**
     * extraction setter
     * 
     * @param extraction extraction
     */
    public void setExtraction(Extraction extraction) {
        this.extraction = extraction;
    }

}
