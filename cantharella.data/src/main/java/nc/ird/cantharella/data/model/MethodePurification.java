/*
 * #%L
 * Cantharella :: Data
 * $Id: MethodePurification.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.data/src/main/java/nc/ird/cantharella/data/model/MethodePurification.java $
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

import nc.ird.cantharella.data.model.utils.AbstractModel;

import org.apache.commons.beanutils.BeanComparator;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Type;
import org.hibernate.search.annotations.Field;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Modèle : Methode de purification
 * 
 * @author Adrien Cheype
 */
@Entity
@Embeddable
public class MethodePurification extends AbstractModel implements Comparable<MethodePurification> {

    /** Id de la méthode */
    @Id
    @GeneratedValue
    private Integer idMethodePurification;

    /** Nom de la méthode */
    @Length(max = LENGTH_MEDIUM_TEXT)
    @Column(unique = true)
    @NotEmpty
    @Field
    private String nom;

    /** Description de la méthode */
    @NotEmpty
    @Lob
    @Type(type = "org.hibernate.type.StringClobType")
    // see HHH-6105
    private String description;

    /** Types extraits définis en sortie pour la méthode */
    @OneToMany(mappedBy = "methodePurification", fetch = FetchType.LAZY, orphanRemoval = true)
    @Cascade({ CascadeType.SAVE_UPDATE })
    @OrderBy("index")
    private List<ParamMethoPuri> parametres;

    /**
     * Constructor
     */
    public MethodePurification() {
        parametres = new ArrayList<ParamMethoPuri>();
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return nom;
    }

    /** {@inheritDoc} */
    @Override
    public int compareTo(MethodePurification methodePurification) {
        return new BeanComparator("nom").compare(this, methodePurification);
    }

    /**
     * Rend les paramètres triés par index
     * 
     * @return Les paramètres
     */
    public List<ParamMethoPuri> getSortedParametres() {
        Collections.sort(parametres);
        return parametres;
    }

    /**
     * idMethodePurification getter
     * 
     * @return idMethodePurification
     */
    public Integer getIdMethodePurification() {
        return idMethodePurification;
    }

    /**
     * idMethodePurification setter
     * 
     * @param idMethodePurification idMethodePurification
     */
    public void setIdMethodePurification(Integer idMethodePurification) {
        this.idMethodePurification = idMethodePurification;
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
     * parametres getter
     * 
     * @return parametres
     */
    public List<ParamMethoPuri> getParametres() {
        return parametres;
    }

    /**
     * parametres setter
     * 
     * @param parametres parametres
     */
    public void setParametres(List<ParamMethoPuri> parametres) {
        this.parametres = parametres;
    }

}
