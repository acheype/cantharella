/*
 * #%L
 * Cantharella :: Data
 * $Id: Partie.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.data/src/main/java/nc/ird/cantharella/data/model/Partie.java $
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
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import nc.ird.cantharella.data.model.utils.AbstractModel;

import org.apache.commons.beanutils.BeanComparator;
import org.hibernate.search.annotations.Field;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Modèle : partie
 * 
 * @author Mickael Tricot
 * @author Adrien Cheype
 */
@Entity
@Table
@Embeddable
public class Partie extends AbstractModel implements Comparable<Partie> {

    /** Id de la partie */
    @Id
    @GeneratedValue
    private Integer idPartie;

    /** Nom */
    @Length(max = LENGTH_MEDIUM_TEXT)
    @NotEmpty
    @Column(unique = true)
    @Field
    private String nom;

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return nom;
    }

    /** {@inheritDoc} */
    @Override
    public int compareTo(Partie partie) {
        return new BeanComparator("nom").compare(this, partie);
    }

    /**
     * idPartie getter
     * 
     * @return idPartie
     */
    public Integer getIdPartie() {
        return idPartie;
    }

    /**
     * idPartie setter
     * 
     * @param idPartie idPartie
     */
    public void setIdPartie(Integer idPartie) {
        this.idPartie = idPartie;
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

}
