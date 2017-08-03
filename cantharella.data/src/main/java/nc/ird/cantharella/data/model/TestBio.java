/*
 * #%L
 * Cantharella :: Data
 * $Id: TestBio.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.data/src/main/java/nc/ird/cantharella/data/model/TestBio.java $
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
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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
import nc.ird.cantharella.data.model.ResultatTestBio.Stade;
import nc.ird.cantharella.data.model.ResultatTestBio.UniteConcMasse;
import nc.ird.cantharella.data.model.comparators.ResultatsOfTestBioComp;
import nc.ird.cantharella.data.model.utils.AbstractModel;
import nc.ird.cantharella.data.model.utils.DocumentAttachable;

import org.apache.commons.beanutils.BeanComparator;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Type;
import org.hibernate.search.annotations.ContainedIn;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.IndexedEmbedded;
import org.hibernate.search.annotations.Store;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Modèle : Manipulation de test biologique
 * 
 * @author Adrien Cheype
 */
@Entity
@Embeddable
public class TestBio extends AbstractModel implements Comparable<TestBio>, DocumentAttachable {

    /** Id du test bio */
    @Id
    @GeneratedValue
    private Integer idTestBio;

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

    /** Organisme qui a effectué le test biologique **/
    @NotNull
    @Length(max = LENGTH_MEDIUM_TEXT)
    private String organismeTesteur;

    /** Date de la manip */
    @NotNull
    @Temporal(TemporalType.DATE)
    private Date date;

    /** Méthode pour la purification **/
    @NotNull
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @IndexedEmbedded
    private MethodeTestBio methode;

    /** Concentration/masse utilisé par défaut pour les résultats **/
    @Min(value = 0)
    @Max(value = DataContext.DECIMAL_MAX)
    @Column(precision = DataContext.DECIMAL_PRECISION, scale = DataContext.DECIMAL_SCALE)
    private BigDecimal concMasseDefaut;

    /** Unité utilisé pour la concentration/masse par défaut */
    @Enumerated(EnumType.ORDINAL)
    private UniteConcMasse uniteConcMasseDefaut;

    /** Référence de la manip */
    private Stade stadeDefaut;

    /** Commentaire pour la manip */
    @Lob
    @Type(type = "org.hibernate.type.StringClobType")
    // see HHH-6105
    private String complement;

    /** Détermine si la manip doit être confidentielle */
    private boolean confidentiel;

    /** Date jusqu'à laquelle la manip est confidentielle */
    @Future
    @Temporal(TemporalType.DATE)
    private Date dateConfidentialite;

    /** Créateur */
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Personne createur;

    /** Résultats produits par le test */
    @NotNull
    @OneToMany(mappedBy = "testBio", fetch = FetchType.LAZY, orphanRemoval = true)
    @Cascade({ CascadeType.SAVE_UPDATE })
    @ContainedIn
    private List<ResultatTestBio> resultats;

    /** Attached documents. */
    @OneToMany(fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "resultatTestBio")
    @Cascade({ CascadeType.SAVE_UPDATE })
    @Fetch(value = FetchMode.SUBSELECT)
    // see HHH-1718
    private List<Document> documents;

    /**
     * Constructeur
     */
    public TestBio() {
        resultats = new ArrayList<ResultatTestBio>();
        documents = new ArrayList<Document>();
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return ref;
    }

    /** {@inheritDoc} */
    @Override
    public int compareTo(TestBio testBio) {
        return new BeanComparator("ref").compare(this, testBio);
    }

    /**
     * Rend les résultats triés en utilisant le comparateur {@link ResultatsOfTestBioComp}
     * 
     * @return resultats
     */
    public List<ResultatTestBio> getSortedResultats() {
        // comme "@Sort(type = SortType.COMPARATOR, comparator = ResultatsOfTestBioComp.class)" ne rend pas une
        // liste triée avec List, tri dans le getter
        Collections.sort(resultats, new ResultatsOfTestBioComp());
        return resultats;
    }

    /**
     * idTestBio getter
     * 
     * @return idTestBio
     */
    public Integer getIdTestBio() {
        return idTestBio;
    }

    /**
     * idTestBio setter
     * 
     * @param idTestBio idTestBio
     */
    public void setIdTestBio(Integer idTestBio) {
        this.idTestBio = idTestBio;
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
     * organismeTesteur getter
     * 
     * @return organismeTesteur
     */
    public String getOrganismeTesteur() {
        return organismeTesteur;
    }

    /**
     * organismeTesteur setter
     * 
     * @param organismeTesteur organismeTesteur
     */
    public void setOrganismeTesteur(String organismeTesteur) {
        this.organismeTesteur = organismeTesteur;
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
    public MethodeTestBio getMethode() {
        return methode;
    }

    /**
     * methode setter
     * 
     * @param methode methode
     */
    public void setMethode(MethodeTestBio methode) {
        this.methode = methode;
    }

    /**
     * concMasseDefaut getter
     * 
     * @return concMasseDefaut
     */
    public BigDecimal getConcMasseDefaut() {
        return concMasseDefaut;
    }

    /**
     * concMasseDefaut setter
     * 
     * @param concMasseDefaut concMasseDefaut
     */
    public void setConcMasseDefaut(BigDecimal concMasseDefaut) {
        this.concMasseDefaut = concMasseDefaut;
    }

    /**
     * uniteConcMasseDefaut getter
     * 
     * @return uniteConcMasseDefaut
     */
    public UniteConcMasse getUniteConcMasseDefaut() {
        return uniteConcMasseDefaut;
    }

    /**
     * uniteConcMasseDefaut setter
     * 
     * @param uniteConcMasseDefaut uniteConcMasseDefaut
     */
    public void setUniteConcMasseDefaut(UniteConcMasse uniteConcMasseDefaut) {
        this.uniteConcMasseDefaut = uniteConcMasseDefaut;
    }

    /**
     * stadeDefaut getter
     * 
     * @return stadeDefaut
     */
    public Stade getStadeDefaut() {
        return stadeDefaut;
    }

    /**
     * stadeDefaut setter
     * 
     * @param stadeDefaut stadeDefaut
     */
    public void setStadeDefaut(Stade stadeDefaut) {
        this.stadeDefaut = stadeDefaut;
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
     * resultats getter
     * 
     * @return resultats
     */
    public List<ResultatTestBio> getResultats() {
        return resultats;
    }

    /**
     * resultats setter
     * 
     * @param resultats resultats
     */
    public void setResultats(List<ResultatTestBio> resultats) {
        this.resultats = resultats;
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
