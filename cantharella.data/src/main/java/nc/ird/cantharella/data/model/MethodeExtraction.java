/*
 * #%L
 * Cantharella :: Data
 * $Id: MethodeExtraction.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.data/src/main/java/nc/ird/cantharella/data/model/MethodeExtraction.java $
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

import nc.ird.cantharella.data.model.utils.AbstractModel;

import org.apache.commons.beanutils.BeanComparator;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Type;
import org.hibernate.search.annotations.Field;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Modèle : Methode d'extraction d'un lot
 * 
 * @author Mickael Tricot
 * @author Adrien Cheype
 */
@Entity
@Embeddable
public class MethodeExtraction extends AbstractModel implements Comparable<MethodeExtraction> {

    /** Id de la méthode */
    @Id
    @GeneratedValue
    private Integer idMethodeExtraction;

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
    @OneToMany(mappedBy = "methodeExtraction", fetch = FetchType.LAZY, orphanRemoval = true)
    @Cascade({ CascadeType.SAVE_UPDATE })
    private List<TypeExtrait> typesEnSortie;

    /**
     * Constructor
     */
    public MethodeExtraction() {
        typesEnSortie = new ArrayList<TypeExtrait>();
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return nom;
    }

    /** {@inheritDoc} */
    @Override
    public int compareTo(MethodeExtraction methodeExtraction) {
        return new BeanComparator("nom").compare(this, methodeExtraction);
    }

    /**
     * Rend les types d'extraits triés par initiales
     * 
     * @return Les types d'extraits
     */
    public List<TypeExtrait> getSortedTypesEnSortie() {
        Collections.sort(typesEnSortie);
        return typesEnSortie;
    }

    /**
     * idMethodeExtraction getter
     * 
     * @return idMethodeExtraction
     */
    public Integer getIdMethodeExtraction() {
        return idMethodeExtraction;
    }

    /**
     * idMethodeExtraction setter
     * 
     * @param idMethodeExtraction idMethodeExtraction
     */
    public void setIdMethodeExtraction(Integer idMethodeExtraction) {
        this.idMethodeExtraction = idMethodeExtraction;
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
     * typesEnSortie getter
     * 
     * @return typesEnSortie
     */
    public List<TypeExtrait> getTypesEnSortie() {
        return typesEnSortie;
    }

    /**
     * typesEnSortie setter
     * 
     * @param typesEnSortie typesEnSortie
     */
    public void setTypesEnSortie(List<TypeExtrait> typesEnSortie) {
        this.typesEnSortie = typesEnSortie;
    }

}
