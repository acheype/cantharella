/*
 * #%L
 * Cantharella :: Data
 * $Id: Campagne.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.data/src/main/java/nc/ird/cantharella/data/model/Campagne.java $
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

import java.util.ArrayList;
import java.util.Collections;
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
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import nc.ird.cantharella.data.model.utils.AbstractModel;
import nc.ird.cantharella.data.model.utils.DocumentAttachable;
import nc.ird.cantharella.data.validation.CountryCode;

import org.apache.commons.beanutils.BeanComparator;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Type;
import org.hibernate.search.annotations.ContainedIn;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Store;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Modèle : campagne
 * 
 * @author Mickael Tricot
 * @author Adrien Cheype
 */
@Entity
@Table
@Embeddable
public class Campagne extends AbstractModel implements Comparable<Campagne>, DocumentAttachable {

    /** ID */
    @Id
    @GeneratedValue
    private Integer idCampagne;

    /** Nom */
    @Column(unique = true)
    @NotNull
    @NotEmpty
    @Length(max = LENGTH_MEDIUM_TEXT)
    @Field(store = Store.YES)
    private String nom;

    /** Code pays */
    @NotNull
    @CountryCode
    @Length(min = 2, max = 2)
    private String codePays;

    /** Programme */
    @Length(max = LENGTH_MEDIUM_TEXT)
    private String programme;

    /** Mention légale décrivant les droits sur les données */
    @Lob
    @Type(type = "org.hibernate.type.StringClobType")
    // see HHH-6105
    private String mentionLegale;

    /** Complément */
    @Lob
    @Type(type = "org.hibernate.type.StringClobType")
    // see HHH-6105
    private String complement;

    /** Date début */
    @NotNull
    @Temporal(TemporalType.DATE)
    private Date dateDeb;

    /** Date fin */
    @NotNull
    @Temporal(TemporalType.DATE)
    private Date dateFin;

    /** Droits groupes */
    @NotNull
    @OneToMany(mappedBy = "id.pk1", fetch = FetchType.LAZY)
    private List<CampagneGroupeDroits> groupesDroits;

    /** Lots */
    @NotNull
    @OneToMany(mappedBy = "campagne", fetch = FetchType.LAZY)
    @ContainedIn
    private List<Lot> lots;

    /** Créateur */
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Personne createur;

    /** Participants */
    @NotNull
    @OneToMany(mappedBy = "id.pk1", fetch = FetchType.LAZY, orphanRemoval = true)
    @Cascade({ CascadeType.SAVE_UPDATE })
    private List<CampagnePersonneParticipant> participants;

    /** Droits personnes */
    @NotNull
    @OneToMany(mappedBy = "id.pk1", fetch = FetchType.LAZY)
    private List<CampagnePersonneDroits> personnesDroits;

    /** Stations prospectées **/
    @NotNull
    @ManyToMany(fetch = FetchType.LAZY)
    @Cascade({ CascadeType.SAVE_UPDATE })
    @ContainedIn
    private List<Station> stations;

    /** Attached documents. */
    @OneToMany(fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "campagne")
    @Cascade({ CascadeType.SAVE_UPDATE })
    @Fetch(value = FetchMode.SUBSELECT)
    // see HHH-1718
    private List<Document> documents;

    /**
     * Constructor
     */
    public Campagne() {
        lots = new ArrayList<Lot>();
        participants = new ArrayList<CampagnePersonneParticipant>();
        stations = new ArrayList<Station>();
        personnesDroits = new ArrayList<CampagnePersonneDroits>();
        groupesDroits = new ArrayList<CampagneGroupeDroits>();
        documents = new ArrayList<Document>();
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return nom;
    }

    /** {@inheritDoc} */
    @Override
    public int compareTo(Campagne campagne) {
        return new BeanComparator("nom").compare(this, campagne);
    }

    /**
     * Rend les stations triées par noms
     * 
     * @return Les stations triées
     */
    public List<Station> getSortedStations() {
        Collections.sort(stations);
        return stations;
    }

    /**
     * idCampagne getter
     * 
     * @return idCampagne
     */
    public Integer getIdCampagne() {
        return idCampagne;
    }

    /**
     * idCampagne setter
     * 
     * @param idCampagne idCampagne
     */
    public void setIdCampagne(Integer idCampagne) {
        this.idCampagne = idCampagne;
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
     * codePays getter
     * 
     * @return codePays
     */
    public String getCodePays() {
        return codePays;
    }

    /**
     * codePays setter
     * 
     * @param codePays codePays
     */
    public void setCodePays(String codePays) {
        this.codePays = codePays;
    }

    /**
     * programme getter
     * 
     * @return programme
     */
    public String getProgramme() {
        return programme;
    }

    /**
     * programme setter
     * 
     * @param programme programme
     */
    public void setProgramme(String programme) {
        this.programme = programme;
    }

    /**
     * mentionLegale getter
     * 
     * @return mentionLegale
     */
    public String getMentionLegale() {
        return mentionLegale;
    }

    /**
     * mentionLegale setter
     * 
     * @param mentionLegale mentionLegale
     */
    public void setMentionLegale(String mentionLegale) {
        this.mentionLegale = mentionLegale;
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
     * dateDeb getter
     * 
     * @return dateDeb
     */
    public Date getDateDeb() {
        return dateDeb;
    }

    /**
     * dateDeb setter
     * 
     * @param dateDeb dateDeb
     */
    public void setDateDeb(Date dateDeb) {
        this.dateDeb = dateDeb;
    }

    /**
     * dateFin getter
     * 
     * @return dateFin
     */
    public Date getDateFin() {
        return dateFin;
    }

    /**
     * dateFin setter
     * 
     * @param dateFin dateFin
     */
    public void setDateFin(Date dateFin) {
        this.dateFin = dateFin;
    }

    /**
     * groupesDroits getter
     * 
     * @return groupesDroits
     */
    public List<CampagneGroupeDroits> getGroupesDroits() {
        return groupesDroits;
    }

    /**
     * groupesDroits setter
     * 
     * @param groupesDroits groupesDroits
     */
    public void setGroupesDroits(List<CampagneGroupeDroits> groupesDroits) {
        this.groupesDroits = groupesDroits;
    }

    /**
     * lots getter
     * 
     * @return lots
     */
    public List<Lot> getLots() {
        return lots;
    }

    /**
     * lots setter
     * 
     * @param lots lots
     */
    public void setLots(List<Lot> lots) {
        this.lots = lots;
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
     * participants getter
     * 
     * @return participants
     */
    public List<CampagnePersonneParticipant> getParticipants() {
        return participants;
    }

    /**
     * participants setter
     * 
     * @param participants participants
     */
    public void setParticipants(List<CampagnePersonneParticipant> participants) {
        this.participants = participants;
    }

    /**
     * personnesDroits getter
     * 
     * @return personnesDroits
     */
    public List<CampagnePersonneDroits> getPersonnesDroits() {
        return personnesDroits;
    }

    /**
     * personnesDroits setter
     * 
     * @param personnesDroits personnesDroits
     */
    public void setPersonnesDroits(List<CampagnePersonneDroits> personnesDroits) {
        this.personnesDroits = personnesDroits;
    }

    /**
     * stations getter
     * 
     * @return stations
     */
    public List<Station> getStations() {
        return stations;
    }

    /**
     * stations setter
     * 
     * @param stations stations
     */
    public void setStations(List<Station> stations) {
        this.stations = stations;
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
