/*
 * #%L
 * Cantharella :: Data
 * $Id: TypeDocument.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.data/src/main/java/nc/ird/cantharella/data/model/TypeDocument.java $
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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import nc.ird.cantharella.data.model.utils.AbstractModel;

import org.apache.commons.beanutils.BeanComparator;
import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Model : Document type
 * 
 * @author Adrien Cheype
 */
@Entity
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "nom" }) })
public class TypeDocument extends AbstractModel implements Cloneable, Comparable<TypeDocument> {

    /** Document type ID */
    @Id
    @GeneratedValue
    private Integer idTypeDocument;

    /** Document type name */
    @Length(max = LENGTH_MEDIUM_TEXT)
    @NotEmpty
    @Column(unique = true)
    private String nom;

    /** Document type domain */
    @Length(max = LENGTH_MEDIUM_TEXT)
    @NotEmpty
    private String domaine;

    /** Document type description */
    @NotEmpty
    @Lob
    @Type(type = "org.hibernate.type.StringClobType")
    // see HHH-6105
    private String description;

    /** {@inheritDoc} */
    @Override
    public TypeDocument clone() throws CloneNotSupportedException {
        TypeDocument clone = (TypeDocument) super.clone();
        clone.idTypeDocument = idTypeDocument;
        clone.nom = nom;
        clone.domaine = domaine;
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
    public int compareTo(TypeDocument typeExtrait) {
        return new BeanComparator("nom").compare(this, typeExtrait);
    }

    /**
     * idTypeDocument getter
     * 
     * @return idTypeDocument
     */
    public Integer getIdTypeDocument() {
        return idTypeDocument;
    }

    /**
     * idTypeDocument setter
     * 
     * @param idTypeDocument idTypeDocument
     */
    public void setIdTypeDocument(Integer idTypeDocument) {
        this.idTypeDocument = idTypeDocument;
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
     * domaine getter
     * 
     * @return domaine
     */
    public String getDomaine() {
        return domaine;
    }

    /**
     * domaine setter
     * 
     * @param domaine domaine
     */
    public void setDomaine(String domaine) {
        this.domaine = domaine;
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
}
