/*
 * #%L
 * Cantharella :: Data
 * $Id: Purification.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.data/src/main/java/nc/ird/cantharella/data/model/Purification.java $
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
import javax.validation.constraints.Future;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import nc.ird.cantharella.data.config.DataContext;
import nc.ird.cantharella.data.model.comparators.FractionsOfPurificationComp;
import nc.ird.cantharella.data.model.utils.AbstractModel;
import nc.ird.cantharella.data.model.utils.DocumentAttachable;
import nc.ird.cantharella.utils.AssertTools;

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
 * Modèle : Manipulation de purification
 * 
 * @author Adrien Cheype
 */
@Entity
@Indexed
public class Purification extends AbstractModel implements Comparable<Purification>, DocumentAttachable {

    /** Id de la purification */
    @Id
    @GeneratedValue
    private Integer idPurification;

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

    /** Date de la manip */
    @NotNull
    @Temporal(TemporalType.DATE)
    private Date date;

    /** Méthode pour la purification **/
    @NotNull
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @IndexedEmbedded
    private MethodePurification methode;

    /** Paramètres qui caractérisent la méthode pour cette purification */
    @NotNull
    @OneToMany(mappedBy = "purification", fetch = FetchType.LAZY, orphanRemoval = true)
    @Cascade({ CascadeType.SAVE_UPDATE })
    private List<ParamMethoPuriEffectif> paramsMetho;

    /** Produit utilisé pour la purification **/
    @NotNull
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @IndexedEmbedded
    private Produit produit;

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

    /** Détermine si la manip doit être confidentielle */
    private boolean confidentiel;

    /** Date jusqu'à laquelle la purification est confidentielle */
    @Future
    @Temporal(TemporalType.DATE)
    private Date dateConfidentialite;

    /** Créateur */
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Personne createur;

    /** Fractions produites par la purification */
    @NotNull
    @OneToMany(mappedBy = "purification", fetch = FetchType.LAZY, orphanRemoval = true)
    @Cascade({ CascadeType.REFRESH, CascadeType.SAVE_UPDATE })
    @ContainedIn
    private List<Fraction> fractions;

    /**
     * Lot dont provient la purification. Stocké en tant que propriété dans l'objet afin d'optimiser son accès
     */
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @IndexedEmbedded
    private Lot lotSource;

    /** Attached documents. */
    @OneToMany(fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "purification")
    @Cascade({ CascadeType.SAVE_UPDATE })
    @Fetch(value = FetchMode.SUBSELECT)
    // see HHH-1718
    private List<Document> documents;

    /**
     * Constructeur
     */
    public Purification() {
        fractions = new ArrayList<Fraction>();
        paramsMetho = new ArrayList<ParamMethoPuriEffectif>();
        documents = new ArrayList<Document>();
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return ref;
    }

    /** {@inheritDoc} */
    @Override
    public int compareTo(Purification purification) {
        ComparatorChain comparatorChain = new ComparatorChain();
        comparatorChain.addComparator(new BeanComparator("produit"));
        comparatorChain.addComparator(new BeanComparator("ref"));
        return comparatorChain.compare(this, purification);
    }

    /**
     * produit setter. Mise à jour du lot source dont provient le produit
     * 
     * @param produit produit
     */
    public void setProduit(Produit produit) {
        this.lotSource = findLotSourceFromProduit(produit);
        this.produit = produit;
    }

    /**
     * Rend les fractions triées en utilisant le comparateur {@link FractionsOfPurificationComp}
     * 
     * @return fractions
     */
    public List<Fraction> getSortedFractions() {
        // comme "@Sort(type = SortType.COMPARATOR, comparator = FractionsOfPurificationComp.class)" ne rend pas une
        // liste triée avec List, tri dans le getter
        Collections.sort(fractions, new FractionsOfPurificationComp());
        return fractions;
    }

    /**
     * Rend les paramètres triés par index
     * 
     * @return Les paramètres
     */
    @SuppressWarnings("unchecked")
    public List<ParamMethoPuriEffectif> getSortedParamsMetho() {
        Collections.sort(paramsMetho, new BeanComparator("param.index"));
        return paramsMetho;
    }

    /**
     * idPurification getter
     * 
     * @return idPurification
     */
    public Integer getIdPurification() {
        return idPurification;
    }

    /**
     * idPurification setter
     * 
     * @param idPurification idPurification
     */
    public void setIdPurification(Integer idPurification) {
        this.idPurification = idPurification;
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
     * methode getter
     * 
     * @return methode
     */
    public MethodePurification getMethode() {
        return methode;
    }

    /**
     * methode setter
     * 
     * @param methode methode
     */
    public void setMethode(MethodePurification methode) {
        this.methode = methode;
    }

    /**
     * paramsMetho getter
     * 
     * @return paramsMetho
     */
    public List<ParamMethoPuriEffectif> getParamsMetho() {
        return paramsMetho;
    }

    /**
     * paramsMetho setter
     * 
     * @param paramsMetho paramsMetho
     */
    public void setParamsMetho(List<ParamMethoPuriEffectif> paramsMetho) {
        this.paramsMetho = paramsMetho;
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
     * confidentiel getter
     * 
     * @return confidentiel
     */
    public boolean isConfidentiel() {
        return confidentiel;
    }

    /**
     * confidentiel setter
     * 
     * @param confidentiel confidentiel
     */
    public void setConfidentiel(boolean confidentiel) {
        this.confidentiel = confidentiel;
    }

    /**
     * dateConfidentialite getter
     * 
     * @return dateConfidentialite
     */
    public Date getDateConfidentialite() {
        return dateConfidentialite;
    }

    /**
     * dateConfidentialite setter
     * 
     * @param dateConfidentialite dateConfidentialite
     */
    public void setDateConfidentialite(Date dateConfidentialite) {
        this.dateConfidentialite = dateConfidentialite;
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
     * lotSource getter
     * 
     * @return lotSource
     */
    public Lot getLotSource() {
        return lotSource;
    }

    /**
     * fractions getter
     * 
     * @return fractions
     */
    public List<Fraction> getFractions() {
        return fractions;
    }

    /**
     * fractions setter
     * 
     * @param fractions fractions
     */
    public void setFractions(List<Fraction> fractions) {
        this.fractions = fractions;
    }

    /**
     * lotSource setter
     * 
     * @param lotSource lotSource
     */
    public void setLotSource(Lot lotSource) {
        this.lotSource = lotSource;
    }

    /**
     * produit getter
     * 
     * @return produit
     */
    public Produit getProduit() {
        return produit;
    }

    /**
     * Remonte au lot dont provient le produit source de la purification
     * 
     * @param produit Le produit en question
     * @return Le lot
     */
    public Lot findLotSourceFromProduit(Produit produit) {
        Produit curProd = produit;
        while (curProd.isFraction()) {
            Fraction curFraction = (Fraction) curProd;
            curProd = curFraction.getPurification().getProduit();
        }
        AssertTools.assertClassOrInterface(curProd, Extrait.class);

        Extrait extrait = (Extrait) curProd;

        return extrait.getExtraction().getLot();
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
