/*
 * #%L
 * Cantharella :: Data
 * $Id: MoleculeProvenance.java 168 2013-03-04 11:33:07Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.data/src/main/java/nc/ird/cantharella/data/model/MoleculeProvenance.java $
 * %%
 * Copyright (C) 2009 - 2013 IRD (Institut de Recherche pour le Developpement) and by respective authors (see below)
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

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.hibernate.search.annotations.ContainedIn;
import org.hibernate.search.annotations.IndexedEmbedded;

import nc.ird.cantharella.data.config.DataContext;
import nc.ird.cantharella.data.model.utils.AbstractModel;

/**
 * MoleculeProvenance association entity between {@link Molecule} and {@link Produit}.
 * 
 * @author Eric Chatellier
 */
@Entity
public class MoleculeProvenance extends AbstractModel implements Cloneable {

    /** Id */
    @Id
    @GeneratedValue
    private Integer id;

    /** Concentration/masse **/
    @Min(value = 0)
    @Max(value = 100)
    @Column(precision = DataContext.DECIMAL_PRECISION, scale = DataContext.DECIMAL_SCALE)
    private BigDecimal pourcentage;

    /** Molecule dont fait partie la provenance */
    @NotNull
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @ContainedIn
    private Molecule molecule;

    /** Produit sur lequel porte la provenance **/
    @NotNull
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @IndexedEmbedded
    private Produit produit;

    /**
     * Id getter.
     * 
     * @return the id
     */
    public Integer getId() {
        return id;
    }

    /**
     * Id setter.
     * 
     * @param id the id to set
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Pourcentage getter.
     * 
     * @return the pourcentage
     */
    public BigDecimal getPourcentage() {
        return pourcentage;
    }

    /**
     * Pourcentage setter.
     * 
     * @param pourcentage the pourcentage to set
     */
    public void setPourcentage(BigDecimal pourcentage) {
        this.pourcentage = pourcentage;
    }

    /**
     * Molecule getter.
     * 
     * @return the molecule
     */
    public Molecule getMolecule() {
        return molecule;
    }

    /**
     * Molecule setter.
     * 
     * @param molecule the molecule to set
     */
    public void setMolecule(Molecule molecule) {
        this.molecule = molecule;
    }

    /**
     * Produit getter.
     * 
     * @return the produit
     */
    public Produit getProduit() {
        return produit;
    }

    /**
     * Produit setter.
     * 
     * @param produit the produit to set
     */
    public void setProduit(Produit produit) {
        this.produit = produit;
    }

    /** {@inheritDoc} */
    @Override
    public MoleculeProvenance clone() throws CloneNotSupportedException {
        MoleculeProvenance clone = (MoleculeProvenance) super.clone();
        clone.id = id;
        clone.pourcentage = pourcentage;
        clone.molecule = molecule;
        clone.produit = produit;
        return clone;
    }
}
