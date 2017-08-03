/*
 * #%L
 * Cantharella :: Data
 * $Id: Produit.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.data/src/main/java/nc/ird/cantharella/data/model/Produit.java $
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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import nc.ird.cantharella.data.config.DataContext;
import nc.ird.cantharella.data.model.search.ProduitBridge;
import nc.ird.cantharella.data.model.utils.AbstractModel;

import org.apache.commons.beanutils.BeanComparator;
import org.hibernate.search.annotations.ClassBridge;
import org.hibernate.search.annotations.ContainedIn;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Store;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Modèle : Produit issue d'une purification
 * 
 * @author Adrien Cheype
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@ClassBridge(name = "produit", index = Index.YES, store = Store.YES, impl = ProduitBridge.class)
public abstract class Produit extends AbstractModel implements Cloneable, Comparable<Produit> {

    /** Id du produit */
    @Id
    @GeneratedValue
    private Integer id;

    /** Réference du produit */
    @Length(max = LENGTH_MEDIUM_TEXT)
    @NotEmpty
    @Column(unique = true)
    @Field(store = Store.YES)
    private String ref;

    /** Masse obtenue pour le produit **/
    @Min(value = 0)
    @Max(value = DataContext.DECIMAL_MAX)
    @Column(precision = DataContext.DECIMAL_PRECISION, scale = DataContext.DECIMAL_SCALE)
    private BigDecimal masseObtenue;

    /** Purifications effectuées à partir du produit */
    @NotNull
    @OneToMany(mappedBy = "produit", fetch = FetchType.LAZY)
    @ContainedIn
    private List<Purification> purificationsSuivantes;

    /** Résultats de tests biologiques effectués à partir du produit */
    @NotNull
    @OneToMany(mappedBy = "produit", fetch = FetchType.LAZY)
    @ContainedIn
    private List<ResultatTestBio> resultatsTestsBioSuivants;

    /**
     * Constructor
     */
    public Produit() {
        super();
        purificationsSuivantes = new ArrayList<Purification>();
        resultatsTestsBioSuivants = new ArrayList<ResultatTestBio>();
    }

    /**
     * Détermine si le produit est un extrait
     * 
     * @return true si c'est un extrait
     */
    public abstract boolean isExtrait();

    /**
     * Détermine si le produit est une fraction
     * 
     * @return true si c'est une fraction
     */
    public abstract boolean isFraction();

    /** {@inheritDoc} */
    @Override
    public Produit clone() throws CloneNotSupportedException {
        Produit clone = (Produit) super.clone();
        clone.id = id;
        clone.ref = ref;
        return clone;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return this.ref;
    }

    /** {@inheritDoc} */
    @Override
    public int compareTo(Produit produit) {
        return new BeanComparator("ref").compare(this, produit);
    }

    /**
     * id getter
     * 
     * @return id
     */
    public Integer getId() {
        return id;
    }

    /**
     * id setter
     * 
     * @param id id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * ref getter
     * 
     * @return ref
     */
    public String getRef() {
        return ref;
    }

    /**
     * ref setter
     * 
     * @param ref ref
     */
    public void setRef(String ref) {
        this.ref = ref;
    }

    /**
     * masseObtenue getter
     * 
     * @return masseObtenue
     */
    public BigDecimal getMasseObtenue() {
        return masseObtenue;
    }

    /**
     * masseObtenue setter
     * 
     * @param masseObtenue masseObtenue
     */
    public void setMasseObtenue(BigDecimal masseObtenue) {
        this.masseObtenue = masseObtenue;
    }

    /**
     * purificationsSuivantes getter
     * 
     * @return purificationsSuivantes
     */
    public List<Purification> getPurificationsSuivantes() {
        return purificationsSuivantes;
    }

    /**
     * purificationsSuivantes setter
     * 
     * @param purificationsSuivantes purificationsSuivantes
     */
    public void setPurificationsSuivantes(List<Purification> purificationsSuivantes) {
        this.purificationsSuivantes = purificationsSuivantes;
    }

    /**
     * resultatsTestsBioSuivants getter
     * 
     * @return resultatsTestsBioSuivants
     */
    public List<ResultatTestBio> getResultatsTestsBioSuivants() {
        return resultatsTestsBioSuivants;
    }

    /**
     * resultatsTestsBioSuivants setter
     * 
     * @param resultatsTestsBioSuivants resultatsTestsBioSuivants
     */
    public void setResultatsTestsBioSuivants(List<ResultatTestBio> resultatsTestsBioSuivants) {
        this.resultatsTestsBioSuivants = resultatsTestsBioSuivants;
    }
}
