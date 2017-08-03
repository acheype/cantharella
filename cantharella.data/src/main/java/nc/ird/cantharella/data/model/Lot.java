/*
 * #%L
 * Cantharella :: Data
 * $Id: Lot.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.data/src/main/java/nc/ird/cantharella/data/model/Lot.java $
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
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;

import nc.ird.cantharella.data.config.DataContext;
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
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.IndexedEmbedded;
import org.hibernate.search.annotations.Store;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Modèle : lot
 * 
 * @author Mickael Tricot
 * @author Adrien Cheype
 */
@Entity
@Table
@Indexed
@Embeddable
public class Lot extends AbstractModel implements Comparable<Lot>, DocumentAttachable {

    /** ID */
    @Id
    @GeneratedValue
    private Integer idLot;

    /** Référence */
    @Column(unique = true)
    @NotEmpty
    @Length(max = LENGTH_MEDIUM_TEXT)
    @Field(store = Store.YES)
    private String ref;

    /** Campagne */
    @NotNull
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @IndexedEmbedded
    private Campagne campagne;

    /** complement */
    @Lob
    @Type(type = "org.hibernate.type.StringClobType")
    // see HHH-6105
    private String complement;

    /** createur */
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Personne createur;

    /** Date */
    @Past
    @Temporal(TemporalType.DATE)
    @NotNull
    private Date dateRecolte;

    /** Echantillon collecté */
    @NotNull
    private Boolean echantillonColl;

    /** Echantillon identifié */
    @NotNull
    private Boolean echantillonIdent;

    /** Echantillon phylo */
    @NotNull
    private Boolean echantillonPhylo;

    /** Droits attribués aux groupes */
    @OneToMany(mappedBy = "id.pk1", fetch = FetchType.LAZY)
    @NotNull
    private List<LotGroupeDroits> groupesDroits;

    /** Masse fraîche */
    @Min(value = 0)
    @Max(value = DataContext.DECIMAL_MAX)
    @Column(precision = DataContext.DECIMAL_PRECISION, scale = DataContext.DECIMAL_SCALE)
    private BigDecimal masseFraiche;

    /** Masse sèche */
    @Min(value = 0)
    @Max(value = DataContext.DECIMAL_MAX)
    @Column(precision = DataContext.DECIMAL_PRECISION, scale = DataContext.DECIMAL_SCALE)
    private BigDecimal masseSeche;

    /** Partie */
    @ManyToOne(fetch = FetchType.EAGER, optional = true)
    @IndexedEmbedded
    private Partie partie;

    /** Droits attribués aux personnes */
    @OneToMany(mappedBy = "id.pk1", fetch = FetchType.LAZY)
    @NotNull
    private List<LotPersonneDroits> personnesDroits;

    /** Spécimen source */
    @NotNull
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @IndexedEmbedded(depth = 1)
    private Specimen specimenRef;

    /** Station */
    @NotNull
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @IndexedEmbedded
    private Station station;

    /** Extractions provenants du lot */
    @OneToMany(mappedBy = "lot", fetch = FetchType.LAZY)
    @NotNull
    @ContainedIn
    private List<Extraction> extractions;

    /** Attached documents. */
    @OneToMany(fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "lot")
    @Cascade({ CascadeType.SAVE_UPDATE })
    @Fetch(value = FetchMode.SUBSELECT)
    // see HHH-1718
    private List<Document> documents;

    /**
     * Constructeur
     */
    public Lot() {
        personnesDroits = new ArrayList<LotPersonneDroits>();
        groupesDroits = new ArrayList<LotGroupeDroits>();
        extractions = new ArrayList<Extraction>();
        documents = new ArrayList<Document>();
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return ref;
    }

    /** {@inheritDoc} */
    @Override
    public int compareTo(Lot lot) {
        return new BeanComparator("ref").compare(this, lot);
    }

    /**
     * campagne getter
     * 
     * @return campagne
     */
    public Campagne getCampagne() {
        return campagne;
    }

    /**
     * campagne setter
     * 
     * @param campagne campagne
     */
    public void setCampagne(Campagne campagne) {
        this.campagne = campagne;
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
     * dateRecolte getter
     * 
     * @return dateRecolte
     */
    public Date getDateRecolte() {
        return dateRecolte;
    }

    /**
     * dateRecolte setter
     * 
     * @param dateRecolte dateRecolte
     */
    public void setDateRecolte(Date dateRecolte) {
        this.dateRecolte = dateRecolte;
    }

    /**
     * echantillonColl getter
     * 
     * @return echantillonColl
     */
    public Boolean getEchantillonColl() {
        return echantillonColl;
    }

    /**
     * echantillonColl setter
     * 
     * @param echantillonColl echantillonColl
     */
    public void setEchantillonColl(Boolean echantillonColl) {
        this.echantillonColl = echantillonColl;
    }

    /**
     * echantillonIdent getter
     * 
     * @return echantillonIdent
     */
    public Boolean getEchantillonIdent() {
        return echantillonIdent;
    }

    /**
     * echantillonIdent setter
     * 
     * @param echantillonIdent echantillonIdent
     */
    public void setEchantillonIdent(Boolean echantillonIdent) {
        this.echantillonIdent = echantillonIdent;
    }

    /**
     * echantillonPhylo getter
     * 
     * @return echantillonPhylo
     */
    public Boolean getEchantillonPhylo() {
        return echantillonPhylo;
    }

    /**
     * echantillonPhylo setter
     * 
     * @param echantillonPhylo echantillonPhylo
     */
    public void setEchantillonPhylo(Boolean echantillonPhylo) {
        this.echantillonPhylo = echantillonPhylo;
    }

    /**
     * groupesDroits getter
     * 
     * @return groupesDroits
     */
    public List<LotGroupeDroits> getGroupesDroits() {
        return groupesDroits;
    }

    /**
     * groupesDroits setter
     * 
     * @param groupesDroits groupesDroits
     */
    public void setGroupesDroits(List<LotGroupeDroits> groupesDroits) {
        this.groupesDroits = groupesDroits;
    }

    /**
     * idLot getter
     * 
     * @return idLot
     */
    public Integer getIdLot() {
        return idLot;
    }

    /**
     * idLot setter
     * 
     * @param idLot idLot
     */
    public void setIdLot(Integer idLot) {
        this.idLot = idLot;
    }

    /**
     * masseFraiche getter
     * 
     * @return masseFraiche
     */
    public BigDecimal getMasseFraiche() {
        return masseFraiche;
    }

    /**
     * masseFraiche setter
     * 
     * @param masseFraiche masseFraiche
     */
    public void setMasseFraiche(BigDecimal masseFraiche) {
        this.masseFraiche = masseFraiche;
    }

    /**
     * masseSeche getter
     * 
     * @return masseSeche
     */
    public BigDecimal getMasseSeche() {
        return masseSeche;
    }

    /**
     * masseSeche setter
     * 
     * @param masseSeche masseSeche
     */
    public void setMasseSeche(BigDecimal masseSeche) {
        this.masseSeche = masseSeche;
    }

    /**
     * partie getter
     * 
     * @return partie
     */
    public Partie getPartie() {
        return partie;
    }

    /**
     * partie setter
     * 
     * @param partie partie
     */
    public void setPartie(Partie partie) {
        this.partie = partie;
    }

    /**
     * personnesDroits getter
     * 
     * @return personnesDroits
     */
    public List<LotPersonneDroits> getPersonnesDroits() {
        return personnesDroits;
    }

    /**
     * personnesDroits setter
     * 
     * @param personnesDroits personnesDroits
     */
    public void setPersonnesDroits(List<LotPersonneDroits> personnesDroits) {
        this.personnesDroits = personnesDroits;
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
     * specimenRef getter
     * 
     * @return specimenRef
     */
    public Specimen getSpecimenRef() {
        return specimenRef;
    }

    /**
     * specimenRef setter
     * 
     * @param specimenRef specimenRef
     */
    public void setSpecimenRef(Specimen specimenRef) {
        this.specimenRef = specimenRef;
    }

    /**
     * station getter
     * 
     * @return station
     */
    public Station getStation() {
        return station;
    }

    /**
     * station setter
     * 
     * @param station station
     */
    public void setStation(Station station) {
        this.station = station;
    }

    /**
     * extractions getter
     * 
     * @return extractions
     */
    public List<Extraction> getExtractions() {
        return extractions;
    }

    /**
     * extractions setter
     * 
     * @param extractions extractions
     */
    public void setExtractions(List<Extraction> extractions) {
        this.extractions = extractions;
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
