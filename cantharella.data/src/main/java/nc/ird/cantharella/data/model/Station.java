/*
 * #%L
 * Cantharella :: Data
 * $Id: Station.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.data/src/main/java/nc/ird/cantharella/data/model/Station.java $
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

import java.util.ArrayList;
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
import javax.validation.constraints.NotNull;

import nc.ird.cantharella.data.model.utils.AbstractModel;
import nc.ird.cantharella.data.model.utils.DocumentAttachable;
import nc.ird.cantharella.data.validation.CountryCode;
import nc.ird.cantharella.data.validation.Latitude;
import nc.ird.cantharella.data.validation.Longitude;
import nc.ird.cantharella.data.validation.Referentiel;

import org.apache.commons.beanutils.BeanComparator;
import org.hibernate.annotations.AccessType;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Type;
import org.hibernate.search.annotations.ContainedIn;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.IndexedEmbedded;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Modèle : station
 * 
 * @author Mickael Tricot
 * @author Adrien Cheype
 */
@Entity
@Table
@AccessType("field")
@Indexed
@Embeddable
public class Station extends AbstractModel implements Cloneable, Comparable<Station>, DocumentAttachable {

    /** ID */
    @Id
    @GeneratedValue
    private Integer idStation;

    /** Nom */
    @Column(unique = true)
    @NotEmpty
    @Length(max = LENGTH_MEDIUM_TEXT)
    @Field
    private String nom;

    /** Code pays */
    @NotNull
    @Length(min = 2, max = 2)
    @CountryCode
    @Field
    private String codePays;

    /** Complément */
    @Lob
    @Type(type = "org.hibernate.type.StringClobType")
    // see HHH-6105
    private String complement;

    /** Créateur */
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Personne createur;

    /** Localité */
    @Length(max = LENGTH_MEDIUM_TEXT)
    @Field
    private String localite;

    /** Latitude */
    @Latitude
    private String latitude;

    /** Longitude */
    @Longitude
    private String longitude;

    /** Référentiel */
    @Referentiel
    private Integer referentiel;

    /** Lots */
    @NotNull
    @OneToMany(mappedBy = "station", fetch = FetchType.LAZY)
    @ContainedIn
    private List<Lot> lots;

    /** Stations */
    @NotNull
    @ManyToMany(mappedBy = "stations", fetch = FetchType.EAGER)
    // FIXME echatellier 20130524 EAGER for hibernate search (no other simple solution)
    @Fetch(value = FetchMode.SUBSELECT)
    // see HHH-1718
    @IndexedEmbedded
    private List<Campagne> campagnes;

    /** Spécimens de référence qui sont rattachés à cette station */
    @OneToMany(mappedBy = "station", fetch = FetchType.LAZY)
    @ContainedIn
    private List<Specimen> specimensRattaches;

    /** Attached documents. */
    @OneToMany(fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "station")
    @Cascade({ CascadeType.SAVE_UPDATE })
    @Fetch(value = FetchMode.SUBSELECT)
    // see HHH-1718
    private List<Document> documents;

    /**
     * Constructeur
     */
    public Station() {
        lots = new ArrayList<Lot>();
        campagnes = new ArrayList<Campagne>();
        documents = new ArrayList<Document>();
    }

    /** {@inheritDoc} */
    @Override
    public Station clone() throws CloneNotSupportedException {
        Station clone = (Station) super.clone();
        clone.idStation = idStation;
        clone.nom = nom;
        clone.codePays = codePays;
        clone.complement = complement;
        clone.createur = createur;
        clone.localite = localite;
        clone.latitude = latitude;
        clone.longitude = longitude;
        clone.referentiel = referentiel;
        return clone;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return nom;
    }

    /** {@inheritDoc} */
    @Override
    public int compareTo(Station station) {
        return new BeanComparator("nom").compare(this, station);
    }

    /**
     * idStation getter
     * 
     * @return idStation
     */
    public Integer getIdStation() {
        return idStation;
    }

    /**
     * idStation setter
     * 
     * @param idStation idStation
     */
    public void setIdStation(Integer idStation) {
        this.idStation = idStation;
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
     * localite getter
     * 
     * @return localite
     */
    public String getLocalite() {
        return localite;
    }

    /**
     * localite setter
     * 
     * @param localite localite
     */
    public void setLocalite(String localite) {
        this.localite = localite;
    }

    /**
     * latitude getter
     * 
     * @return latitude
     */
    public String getLatitude() {
        return latitude;
    }

    /**
     * latitude setter
     * 
     * @param latitude latitude
     */
    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    /**
     * longitude getter
     * 
     * @return longitude
     */
    public String getLongitude() {
        return longitude;
    }

    /**
     * longitude setter
     * 
     * @param longitude longitude
     */
    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    /**
     * referentiel getter
     * 
     * @return referentiel
     */
    public Integer getReferentiel() {
        return referentiel;
    }

    /**
     * referentiel setter
     * 
     * @param referentiel referentiel
     */
    public void setReferentiel(Integer referentiel) {
        this.referentiel = referentiel;
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
     * campagnes getter
     * 
     * @return campagnes
     */
    public List<Campagne> getCampagnes() {
        return campagnes;
    }

    /**
     * campagnes setter
     * 
     * @param campagnes campagnes
     */
    public void setCampagnes(List<Campagne> campagnes) {
        this.campagnes = campagnes;
    }

    /**
     * specimensRattaches getter
     * 
     * @return specimensRattaches
     */
    public List<Specimen> getSpecimensRattaches() {
        return specimensRattaches;
    }

    /**
     * specimensRattaches setter
     * 
     * @param specimensRattaches specimensRattaches
     */
    public void setSpecimensRattaches(List<Specimen> specimensRattaches) {
        this.specimensRattaches = specimensRattaches;
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