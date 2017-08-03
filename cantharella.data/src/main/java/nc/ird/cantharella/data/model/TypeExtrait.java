/*
 * #%L
 * Cantharella :: Data
 * $Id: TypeExtrait.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.data/src/main/java/nc/ird/cantharella/data/model/TypeExtrait.java $
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

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import nc.ird.cantharella.data.model.utils.AbstractModel;
import nc.ird.cantharella.data.validation.CollectionUniqueField;

import org.apache.commons.beanutils.BeanComparator;
import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Modèle : Type d'extrait pour une méthode d'extraction
 * 
 * @author Mickael Tricot
 * @author Adrien Cheype
 */
@Entity
@CollectionUniqueField(fieldName = "initiales", pathToCollection = "methodeExtraction.typesEnSortie")
public class TypeExtrait extends AbstractModel implements Cloneable, Comparable<TypeExtrait> {

    /** Id du type d'extrait */
    @Id
    @GeneratedValue
    private Integer idTypeExtrait;

    /** Initiales du type d'extrait */
    @Length(max = LENGTH_TINY_TEXT)
    @NotEmpty
    private String initiales;

    /** Description du type d'extrait */
    @NotEmpty
    @Lob
    @Type(type = "org.hibernate.type.StringClobType")
    // see HHH-6105
    private String description;

    /** Méthode d'extraction pour laquelle est défini le type d'extrait */
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private MethodeExtraction methodeExtraction;

    /** {@inheritDoc} */
    @Override
    public TypeExtrait clone() throws CloneNotSupportedException {
        TypeExtrait clone = (TypeExtrait) super.clone();
        clone.idTypeExtrait = idTypeExtrait;
        clone.initiales = initiales;
        clone.description = description;
        clone.methodeExtraction = methodeExtraction;
        return clone;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return this.initiales;
    }

    /** {@inheritDoc} */
    @Override
    public int compareTo(TypeExtrait typeExtrait) {
        return new BeanComparator("initiales").compare(this, typeExtrait);
    }

    /**
     * idTypeExtrait getter
     * 
     * @return idTypeExtrait
     */
    public Integer getIdTypeExtrait() {
        return idTypeExtrait;
    }

    /**
     * idTypeExtrait setter
     * 
     * @param idTypeExtrait idTypeExtrait
     */
    public void setIdTypeExtrait(Integer idTypeExtrait) {
        this.idTypeExtrait = idTypeExtrait;
    }

    /**
     * initiales getter
     * 
     * @return initiales
     */
    public String getInitiales() {
        return initiales;
    }

    /**
     * initiales setter
     * 
     * @param initiales initiales
     */
    public void setInitiales(String initiales) {
        this.initiales = initiales;
    }

    /**
     * description getter
     * 
     * @return description
     */
    public String getDescription() {
        return description;
    }

    /**
     * description setter
     * 
     * @param description description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * methodeExtraction getter
     * 
     * @return methodeExtraction
     */
    public MethodeExtraction getMethodeExtraction() {
        return methodeExtraction;
    }

    /**
     * methodeExtraction setter
     * 
     * @param methodeExtraction methodeExtraction
     */
    public void setMethodeExtraction(MethodeExtraction methodeExtraction) {
        this.methodeExtraction = methodeExtraction;
    }

}
