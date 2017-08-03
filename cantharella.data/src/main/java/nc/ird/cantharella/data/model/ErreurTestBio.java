/*
 * #%L
 * Cantharella :: Data
 * $Id: ErreurTestBio.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.data/src/main/java/nc/ird/cantharella/data/model/ErreurTestBio.java $
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
 * Modèle : Erreur pour un test biologique
 * 
 * @author Mickael Tricot
 * @author Adrien Cheype
 */
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = "nom"))
public class ErreurTestBio extends AbstractModel implements Comparable<ErreurTestBio> {

    /** Id de l'erreur */
    @Id
    @GeneratedValue
    private Integer idErreurTest;

    /** Nom */
    @Length(max = LENGTH_MEDIUM_TEXT)
    @NotEmpty
    private String nom;

    /** Description */
    @Lob
    @Type(type = "org.hibernate.type.StringClobType")
    // see HHH-6105
    @NotEmpty
    private String description;

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return nom;
    }

    /** {@inheritDoc} */
    @Override
    public int compareTo(ErreurTestBio erreurTest) {
        return new BeanComparator("nom").compare(this, erreurTest);
    }

    /**
     * idErreurTest getter
     * 
     * @return idErreurTest
     */
    public Integer getIdErreurTest() {
        return idErreurTest;
    }

    /**
     * idErreurTest setter
     * 
     * @param idErreurTest idErreurTest
     */
    public void setIdErreurTest(Integer idErreurTest) {
        this.idErreurTest = idErreurTest;
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
}
