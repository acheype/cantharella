/*
 * #%L
 * Cantharella :: Data
 * $Id: ResultatTestBio.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.data/src/main/java/nc/ird/cantharella/data/model/ResultatTestBio.java $
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
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import nc.ird.cantharella.data.config.DataContext;
import nc.ird.cantharella.data.model.utils.AbstractModel;
import nc.ird.cantharella.data.model.utils.DocumentAttachable;
import nc.ird.cantharella.data.validation.CollectionUniqueField;
import nc.ird.cantharella.utils.AssertTools;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections.comparators.ComparatorChain;
import org.hibernate.annotations.Index;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.IndexedEmbedded;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Modèle : Résultat issue d'un test biologique
 * 
 * Custom indexing of polymorphic produit is based on example at:
 * http://blog.pfa-labs.com/2009/03/building-custom-entity-bridge-with.html
 * 
 * @author Adrien Cheype
 */
@Entity
@CollectionUniqueField(fieldName = "repere", pathToCollection = "testBio.resultats")
@Indexed
public class ResultatTestBio extends AbstractModel implements Cloneable, Comparable<ResultatTestBio>,
        DocumentAttachable {

    /**
     * Type du résultat de test biologique
     */
    public enum TypeResultat implements Comparable<TypeResultat> {
        /** Blanc, dans ce cas pas de réf, conc./masse, stade et actif renseigné */
        BLANC,
        /** Témoin, dans ce cas pas de stade renseigné */
        TEMOIN,
        /** Produit */
        PRODUIT
    }

    /**
     * Stade du résultat de test biologique
     */
    public enum Stade implements Comparable<Stade> {
        /** Détection */
        DETECTION,
        /** Confirmation */
        CONFIRMATION,
        /** Fractionnement */
        FRACTIONNEMENT
    }

    /**
     * Unité de la concentration/masse d'un test biologique
     */
    public enum UniteConcMasse implements Comparable<UniteConcMasse> {
        /** mg **/
        MG,
        /** μg **/
        MICROG,
        /** ng **/
        NG,
        /** ng/ml **/
        MG_ML,
        /** μg/ml **/
        MICROG_ML,
        /** ng/ml **/
        NG_ML
    }

    /** Id du produit */
    @Id
    @GeneratedValue
    private Integer id;

    /** Réference du produit */
    @Length(max = LENGTH_TINY_TEXT)
    @NotEmpty
    private String repere;

    /** Type du résultat */
    @NotNull
    @Enumerated(EnumType.ORDINAL)
    @Index(name = "typeResultat")
    private TypeResultat typeResultat;

    /** Produit utilisé obtenir le résultat **/
    @ManyToOne(fetch = FetchType.EAGER, optional = true)
    @IndexedEmbedded
    private Produit produit;

    /** Nom du produit utilisé lorsque le résultat est de type "temoin" */
    @Length(max = LENGTH_MEDIUM_TEXT)
    private String produitTemoin;

    /** Concentration/masse **/
    @Min(value = 0)
    @Max(value = DataContext.DECIMAL_MAX)
    @Column(precision = DataContext.DECIMAL_PRECISION, scale = DataContext.DECIMAL_SCALE)
    private BigDecimal concMasse;

    /** Unité utilisé pour la concentration/masse */
    @Enumerated(EnumType.ORDINAL)
    private UniteConcMasse uniteConcMasse;

    /** Stade */
    @Enumerated(EnumType.ORDINAL)
    private Stade stade;

    /** Valeur du résultat **/
    @Max(value = DataContext.DECIMAL_MAX)
    @Column(precision = DataContext.DECIMAL_PRECISION, scale = DataContext.DECIMAL_SCALE)
    private BigDecimal valeur;

    /** True si une activité est détectée dans le résultat **/
    private Boolean estActif;

    /** Erreur (pas de valeur si renseigné) */
    @ManyToOne(fetch = FetchType.EAGER, optional = true)
    private ErreurTestBio erreur;

    /** Manip de purification dont fait partie la fraction */
    @NotNull
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @IndexedEmbedded
    private TestBio testBio;

    /**
     * Type d'extrait dont provient le test biologique. Stocké en tant que propriété dans l'objet afin d'optimiser son
     * accès
     */
    @ManyToOne(fetch = FetchType.EAGER, optional = true)
    private TypeExtrait typeExtraitSource;

    /**
     * Constructor
     */
    public ResultatTestBio() {
        super();
    }

    /**
     * Rend le lot dont provient le test biologique
     * 
     * @return Le lot source
     */
    public Lot getLotSource() {
        if (produit == null) {
            return null;
        }
        if (produit.isExtrait()) {
            Extrait extrait = (Extrait) produit;
            return extrait.getExtraction().getLot();
        }
        // cas où c'est une fraction
        Fraction fraction = (Fraction) produit;
        return fraction.getPurification().getLotSource();
    }

    /** {@inheritDoc} */
    @Override
    public ResultatTestBio clone() throws CloneNotSupportedException {
        ResultatTestBio clone = (ResultatTestBio) super.clone();
        clone.id = id;
        clone.repere = repere;
        clone.typeResultat = typeResultat;
        clone.produit = produit;
        clone.stade = stade;
        clone.estActif = estActif;
        clone.testBio = testBio;
        return clone;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return "(" + this.getRepere() + ", " + this.getTypeResultat() + ", "
                + (TypeResultat.PRODUIT.equals(this.getTypeResultat()) ? this.getProduit() : this.getProduitTemoin())
                + ")";
    }

    /** {@inheritDoc} */
    @Override
    public final int compareTo(ResultatTestBio resultat) {
        ComparatorChain comparatorChain = new ComparatorChain();
        comparatorChain.addComparator(new BeanComparator("testBio.methode.cible"));
        comparatorChain.addComparator(new BeanComparator("produit"));
        comparatorChain.addComparator(new BeanComparator("repere"));
        return comparatorChain.compare(this, resultat);
    }

    /**
     * produit setter. Mise à jour du type d'extrait source dont provient le test biologique
     * 
     * @param produit produit
     */
    public void setProduit(Produit produit) {
        if (produit == null) {
            this.typeExtraitSource = null;
        } else {
            this.typeExtraitSource = findExtraitSourceFromProduit(produit).getTypeExtrait();
        }
        this.produit = produit;
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
     * repere getter
     * 
     * @return repere
     */
    public String getRepere() {
        return repere;
    }

    /**
     * repere setter
     * 
     * @param repere repere
     */
    public void setRepere(String repere) {
        this.repere = repere;
    }

    /**
     * typeResultat getter
     * 
     * @return typeResultat
     */
    public TypeResultat getTypeResultat() {
        return typeResultat;
    }

    /**
     * typeResultat setter
     * 
     * @param typeResultat typeResultat
     */
    public void setTypeResultat(TypeResultat typeResultat) {
        this.typeResultat = typeResultat;
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
     * produitTemoin getter
     * 
     * @return produitTemoin
     */
    public String getProduitTemoin() {
        return produitTemoin;
    }

    /**
     * produitTemoin setter
     * 
     * @param produitTemoin produitTemoin
     */
    public void setProduitTemoin(String produitTemoin) {
        this.produitTemoin = produitTemoin;
    }

    /**
     * concMasse getter
     * 
     * @return concMasse
     */
    public BigDecimal getConcMasse() {
        return concMasse;
    }

    /**
     * concMasse setter
     * 
     * @param concMasse concMasse
     */
    public void setConcMasse(BigDecimal concMasse) {
        this.concMasse = concMasse;
    }

    /**
     * uniteConcMasse getter
     * 
     * @return uniteConcMasse
     */
    public UniteConcMasse getUniteConcMasse() {
        return uniteConcMasse;
    }

    /**
     * uniteConcMasse setter
     * 
     * @param uniteConcMasse uniteConcMasse
     */
    public void setUniteConcMasse(UniteConcMasse uniteConcMasse) {
        this.uniteConcMasse = uniteConcMasse;
    }

    /**
     * stade getter
     * 
     * @return stade
     */
    public Stade getStade() {
        return stade;
    }

    /**
     * stade setter
     * 
     * @param stade stade
     */
    public void setStade(Stade stade) {
        this.stade = stade;
    }

    /**
     * valeur getter
     * 
     * @return valeur
     */
    public BigDecimal getValeur() {
        return valeur;
    }

    /**
     * valeur setter
     * 
     * @param valeur valeur
     */
    public void setValeur(BigDecimal valeur) {
        this.valeur = valeur;
    }

    /**
     * estActif getter
     * 
     * @return estActif
     */
    public Boolean isActif() {
        return estActif;
    }

    /**
     * estActif setter
     * 
     * @param estActif estActif
     */
    public void setActif(Boolean estActif) {
        this.estActif = estActif;
    }

    /**
     * erreur getter
     * 
     * @return erreur
     */
    public ErreurTestBio getErreur() {
        return erreur;
    }

    /**
     * erreur setter
     * 
     * @param erreur erreur
     */
    public void setErreur(ErreurTestBio erreur) {
        this.erreur = erreur;
    }

    /**
     * testBio getter
     * 
     * @return testBio
     */
    public TestBio getTestBio() {
        return testBio;
    }

    /**
     * testBio setter
     * 
     * @param testBio testBio
     */
    public void setTestBio(TestBio testBio) {
        this.testBio = testBio;
    }

    /**
     * typeExtraitSource getter
     * 
     * @return typeExtraitSource
     */
    public TypeExtrait getTypeExtraitSource() {
        return typeExtraitSource;
    }

    /**
     * typeExtraitSource setter
     * 
     * @param typeExtraitSource typeExtraitSource
     */
    public void setTypeExtraitSource(TypeExtrait typeExtraitSource) {
        this.typeExtraitSource = typeExtraitSource;
    }

    /**
     * Remonte à l'extrait dont provient le produit source du test biologique
     * 
     * @param produit Le produit en question
     * @return Le lot
     */
    public Extrait findExtraitSourceFromProduit(Produit produit) {
        if (produit.isExtrait()) {
            Extrait extrait = (Extrait) produit;
            return extrait;
        }
        // cas où c'est une fraction
        Produit curProd = produit;
        while (curProd.isFraction()) {
            Fraction curFraction = (Fraction) curProd;
            curProd = curFraction.getPurification().getProduit();
        }
        AssertTools.assertClassOrInterface(curProd, Extrait.class);

        return (Extrait) curProd;
    }

    /** {@inheritDoc} */
    public List<Document> getDocuments() {
        return testBio.getDocuments();
    }

    /** {@inheritDoc} */
    @Override
    public void addDocument(Document document) {
        testBio.addDocument(document);
    }

    /** {@inheritDoc} */
    @Override
    public void removeDocument(Document document) {
        testBio.removeDocument(document);
    }
}
