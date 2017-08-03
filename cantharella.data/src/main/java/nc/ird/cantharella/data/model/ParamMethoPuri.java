/*
 * #%L
 * Cantharella :: Data
 * $Id: ParamMethoPuri.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.data/src/main/java/nc/ird/cantharella/data/model/ParamMethoPuri.java $
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
import javax.validation.constraints.Min;
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
 * @author Adrien Cheype
 */
@Entity
@CollectionUniqueField(fieldName = "nom", pathToCollection = "methodePurification.parametres")
public class ParamMethoPuri extends AbstractModel implements Cloneable, Comparable<ParamMethoPuri> {

    /** Id du type d'extrait */
    @Id
    @GeneratedValue
    private Integer idParamMethoPuri;

    /** Index to order parameters */
    @NotNull
    @Min(value = 0)
    private Integer index;

    /** Initiales du type d'extrait */
    @Length(max = LENGTH_MEDIUM_TEXT)
    @NotEmpty
    private String nom;

    /** Description du type d'extrait */
    @NotEmpty
    @Lob
    @Type(type = "org.hibernate.type.StringClobType")
    // see HHH-6105
    private String description;

    /** Méthode d'extraction pour laquelle est défini le type d'extrait */
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private MethodePurification methodePurification;

    /** {@inheritDoc} */
    @Override
    public ParamMethoPuri clone() throws CloneNotSupportedException {
        ParamMethoPuri clone = (ParamMethoPuri) super.clone();
        clone.idParamMethoPuri = idParamMethoPuri;
        clone.nom = nom;
        clone.description = description;
        return clone;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return this.nom;
    }

    /** {@inheritDoc} */
    @Override
    public int compareTo(ParamMethoPuri paramMethoPuri) {
        return new BeanComparator("index").compare(this, paramMethoPuri);
    }

    /**
     * idParamMethoPuri getter
     * 
     * @return idParamMethoPuri
     */
    public Integer getIdParamMethoPuri() {
        return idParamMethoPuri;
    }

    /**
     * idParamMethoPuri setter
     * 
     * @param idParamMethoPuri idParamMethoPuri
     */
    public void setIdParamMethoPuri(Integer idParamMethoPuri) {
        this.idParamMethoPuri = idParamMethoPuri;
    }

    /**
     * index getter
     * 
     * @return index
     */
    public Integer getIndex() {
        return index;
    }

    /**
     * index setter
     * 
     * @param index index
     */
    public void setIndex(Integer index) {
        this.index = index;
    }

    /**
     * nom getter
     * 
     * @return nom
     */
    public String getNom() {
        return nom;
    }

    /**
     * nom setter
     * 
     * @param nom nom
     */
    public void setNom(String nom) {
        this.nom = nom;
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
     * methodePurification getter
     * 
     * @return methodePurification
     */
    public MethodePurification getMethodePurification() {
        return methodePurification;
    }

    /**
     * methodePurification setter
     * 
     * @param methodePurification methodePurification
     */
    public void setMethodePurification(MethodePurification methodePurification) {
        this.methodePurification = methodePurification;
    }

}
