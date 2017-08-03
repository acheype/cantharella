/*
 * #%L
 * Cantharella :: Data
 * $Id: Extraction.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.data/src/main/java/nc/ird/cantharella/data/model/Extraction.java $
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import nc.ird.cantharella.data.config.DataContext;
import nc.ird.cantharella.data.model.comparators.ExtraitsOfExtractionComp;
import nc.ird.cantharella.data.model.utils.AbstractModel;
import nc.ird.cantharella.data.model.utils.DocumentAttachable;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections.comparators.ComparatorChain;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Type;
import org.hibernate.search.annotations.ContainedIn;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.IndexedEmbedded;
import org.hibernate.search.annotations.Store;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Modèle : Manipulation d'extraction
 * 
 * @author Adrien Cheype
 */
@Entity
@Indexed
public class Extraction extends AbstractModel implements Comparable<Extraction>, DocumentAttachable {

    /** Id de l'extraction */
    @Id
    @GeneratedValue
    private Integer idExtraction;

    /** Référence de la manip */
    @Length(max = LENGTH_MEDIUM_TEXT)
    @Column(unique = true)
    @NotEmpty
    @Field(store = Store.YES)
    private String ref;

    /** Manipulateur */
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Personne manipulateur;

    /** Méthode pour l'extraction **/
    @NotNull
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @IndexedEmbedded
    private MethodeExtraction methode;

    /** Date de la manip */
    @NotNull
    @Temporal(TemporalType.DATE)
    private Date date;

    /** Lot utilisé pour l'extraction **/
    @NotNull
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @IndexedEmbedded
    private Lot lot;

    /** Masse avant l'extraction **/
    @Min(value = 0)
    @Max(value = DataContext.DECIMAL_MAX)
    @Column(precision = DataContext.DECIMAL_PRECISION, scale = DataContext.DECIMAL_SCALE)
    private BigDecimal masseDepart;

    /** Commentaire pour la manip */
    @Lob
    @Type(type = "org.hibernate.type.StringClobType")
    // see HHH-6105
    private String complement;

    /** Créateur */
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Personne createur;

    /** Extraits produits par l'extraction */
    @NotNull
    @OneToMany(mappedBy = "extraction", fetch = FetchType.EAGER, orphanRemoval = true)
    @Cascade({ CascadeType.SAVE_UPDATE })
    @ContainedIn
    private List<Extrait> extraits;

    /** Attached documents. */
    @OneToMany(fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "extraction")
    @Cascade({ CascadeType.SAVE_UPDATE })
    @Fetch(value = FetchMode.SUBSELECT)
    // see HHH-1718
    private List<Document> documents;

    /**
     * Constructeur
     */
    public Extraction() {
        extraits = new ArrayList<Extrait>();
        documents = new ArrayList<Document>();
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return ref;
    }

    /** {@inheritDoc} */
    @Override
    public int compareTo(Extraction extraction) {
        ComparatorChain comparatorChain = new ComparatorChain();
        comparatorChain.addComparator(new BeanComparator("lot"));
        comparatorChain.addComparator(new BeanComparator("ref"));
        return comparatorChain.compare(this, extraction);
    }

    /**
     * Rend les extraits triés en utilisant le comparateur {@link ExtraitsOfExtractionComp}
     * 
     * @return resultats
     */
    public List<Extrait> getSortedExtraits() {
        // comme "@Sort(type = SortType.COMPARATOR, comparator = ExtraitsOfExtractionComp.class)" ne rend pas une
        // liste triée avec List, tri dans le getter
        Collections.sort(extraits, new ExtraitsOfExtractionComp());
        return extraits;
    }

    /**
     * idExtraction getter
     * 
     * @return idExtraction
     */
    public Integer getIdExtraction() {
        return idExtraction;
    }

    /**
     * idExtraction setter
     * 
     * @param idExtraction idExtraction
     */
    public void setIdExtraction(Integer idExtraction) {
        this.idExtraction = idExtraction;
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
     * manipulateur getter
     * 
     * @return manipulateur
     */
    public Personne getManipulateur() {
        return manipulateur;
    }

    /**
     * manipulateur setter
     * 
     * @param manipulateur manipulateur
     */
    public void setManipulateur(Personne manipulateur) {
        this.manipulateur = manipulateur;
    }

    /**
     * methode getter
     * 
     * @return methode
     */
    public MethodeExtraction getMethode() {
        return methode;
    }

    /**
     * methode setter
     * 
     * @param methode methode
     */
    public void setMethode(MethodeExtraction methode) {
        this.methode = methode;
    }

    /**
     * date getter
     * 
     * @return date
     */
    public Date getDate() {
        return date;
    }

    /**
     * date setter
     * 
     * @param date date
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * lot getter
     * 
     * @return lot
     */
    public Lot getLot() {
        return lot;
    }

    /**
     * lot setter
     * 
     * @param lot lot
     */
    public void setLot(Lot lot) {
        this.lot = lot;
    }

    /**
     * masseDepart getter
     * 
     * @return masseDepart
     */
    public BigDecimal getMasseDepart() {
        return masseDepart;
    }

    /**
     * masseDepart setter
     * 
     * @param masseDepart masseDepart
     */
    public void setMasseDepart(BigDecimal masseDepart) {
        this.masseDepart = masseDepart;
    }

    /**
     * complement getter
     * 
     * @return complement
     */
    public String getComplement() {
        return complement;
    }

    /**
     * complement setter
     * 
     * @param complement complement
     */
    public void setComplement(String complement) {
        this.complement = complement;
    }

    /**
     * createur getter
     * 
     * @return createur
     */
    public Personne getCreateur() {
        return createur;
    }

    /**
     * createur setter
     * 
     * @param createur createur
     */
    public void setCreateur(Personne createur) {
        this.createur = createur;
    }

    /**
     * extraits getter
     * 
     * @return extraits
     */
    public List<Extrait> getExtraits() {
        return extraits;
    }

    /**
     * extraits setter
     * 
     * @param extraits extraits
     */
    public void setExtraits(List<Extrait> extraits) {
        this.extraits = extraits;
    }

    /** {@inheritDoc} */
    public List<Document> getDocuments() {
        return documents;
    }

    /**
     * Documents setter.
     * 
     * @param documents the documents to set
     */
    public void setDocuments(List<Document> documents) {
        this.documents = documents;
    }

    /** {@inheritDoc} */
    @Override
    public void addDocument(Document document) {
        documents.add(document);
    }

    /** {@inheritDoc} */
    @Override
    public void removeDocument(Document document) {
        documents.remove(document);
    }
}
