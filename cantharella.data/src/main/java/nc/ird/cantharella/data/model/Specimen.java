/*
 * #%L
 * Cantharella :: Data
 * $Id: Specimen.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.data/src/main/java/nc/ird/cantharella/data/model/Specimen.java $
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
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import nc.ird.cantharella.data.model.utils.AbstractModel;
import nc.ird.cantharella.data.model.utils.DocumentAttachable;

import org.apache.commons.beanutils.BeanComparator;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Type;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.IndexedEmbedded;
import org.hibernate.search.annotations.Store;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Modèle : specimen
 * 
 * @author Mickael Tricot
 * @author Adrien Cheype
 */
@Entity
@Table
@Indexed
@Embeddable
public class Specimen extends AbstractModel implements Comparable<Specimen>, DocumentAttachable {

    /**
     * Types d'organisme pour un spécimen
     */
    public enum TypeOrganisme implements Comparable<TypeOrganisme> {
        /** Plante */
        PLANTE,
        /** Organisme marin */
        ORGANISME_MARIN,
        /** Microorganisme */
        MICROORGANISME,
        /** Insecte */
        INSECTE,
        /** Champignon */
        CHAMPIGNON,
        /** Lichen */
        LICHEN,
        /** Autre */
        AUTRE
    }

    /** ID */
    @Id
    @GeneratedValue
    private Integer idSpecimen;

    /** Nom */
    @Column(unique = true)
    @NotEmpty
    @Length(max = LENGTH_TINY_TEXT)
    @Field(store = Store.YES)
    private String ref;

    /** Embranchement */
    @Length(max = LENGTH_MEDIUM_TEXT)
    @NotEmpty
    @Field
    private String embranchement;

    /** Famille */
    @Length(max = LENGTH_MEDIUM_TEXT)
    @Field
    private String famille;

    /** Genre */
    @Length(max = LENGTH_MEDIUM_TEXT)
    @Field
    private String genre;

    /** Espèce */
    @Length(max = LENGTH_MEDIUM_TEXT)
    @Field
    private String espece;

    /** Sous-espèce */
    @Length(max = LENGTH_MEDIUM_TEXT)
    private String sousEspece;

    /** Embranchement */
    @Length(max = LENGTH_MEDIUM_TEXT)
    private String variete;

    /** Type d'organisme */
    @NotNull
    @Enumerated(EnumType.ORDINAL)
    private TypeOrganisme typeOrganisme;

    /** Créateur */
    @ManyToOne(fetch = FetchType.EAGER)
    private Personne identificateur;

    /** Date dépôt */
    @Temporal(TemporalType.DATE)
    private Date dateDepot;

    /** Num dépot */
    @Length(max = LENGTH_TINY_TEXT)
    private String numDepot;

    /** Lieu depot */
    @Length(max = LENGTH_MEDIUM_TEXT)
    private String lieuDepot;

    /** Station */
    @ManyToOne(fetch = FetchType.EAGER)
    @IndexedEmbedded
    private Station station;

    /** Compléments d'information */
    @Lob
    @Type(type = "org.hibernate.type.StringClobType")
    // see HHH-6105
    private String complement;

    /** Créateur */
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Personne createur;

    /** Attached documents. */
    @OneToMany(fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "specimen")
    @Cascade({ CascadeType.SAVE_UPDATE })
    @Fetch(value = FetchMode.SUBSELECT)
    // see HHH-1718
    private List<Document> documents;

    /**
     * Constructor.
     */
    public Specimen() {
        documents = new ArrayList<Document>();
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return ref;
    }

    /** {@inheritDoc} */
    @Override
    public int compareTo(Specimen specimen) {
        return new BeanComparator("ref").compare(this, specimen);
    }

    /**
     * idSpecimen getter
     * 
     * @return idSpecimen
     */
    public Integer getIdSpecimen() {
        return idSpecimen;
    }

    /**
     * idSpecimen setter
     * 
     * @param idSpecimen idSpecimen
     */
    public void setIdSpecimen(Integer idSpecimen) {
        this.idSpecimen = idSpecimen;
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
     * embranchement getter
     * 
     * @return embranchement
     */
    public String getEmbranchement() {
        return embranchement;
    }

    /**
     * embranchement setter
     * 
     * @param embranchement embranchement
     */
    public void setEmbranchement(String embranchement) {
        this.embranchement = embranchement;
    }

    /**
     * famille getter
     * 
     * @return famille
     */
    public String getFamille() {
        return famille;
    }

    /**
     * famille setter
     * 
     * @param famille famille
     */
    public void setFamille(String famille) {
        this.famille = famille;
    }

    /**
     * genre getter
     * 
     * @return genre
     */
    public String getGenre() {
        return genre;
    }

    /**
     * genre setter
     * 
     * @param genre genre
     */
    public void setGenre(String genre) {
        this.genre = genre;
    }

    /**
     * espece getter
     * 
     * @return espece
     */
    public String getEspece() {
        return espece;
    }

    /**
     * espece setter
     * 
     * @param espece espece
     */
    public void setEspece(String espece) {
        this.espece = espece;
    }

    /**
     * sousEspece getter
     * 
     * @return sousEspece
     */
    public String getSousEspece() {
        return sousEspece;
    }

    /**
     * sousEspece setter
     * 
     * @param sousEspece sousEspece
     */
    public void setSousEspece(String sousEspece) {
        this.sousEspece = sousEspece;
    }

    /**
     * variete getter
     * 
     * @return variete
     */
    public String getVariete() {
        return variete;
    }

    /**
     * variete setter
     * 
     * @param variete variete
     */
    public void setVariete(String variete) {
        this.variete = variete;
    }

    /**
     * typeOrganisme getter
     * 
     * @return typeOrganisme
     */
    public TypeOrganisme getTypeOrganisme() {
        return typeOrganisme;
    }

    /**
     * typeOrganisme setter
     * 
     * @param typeOrganisme typeOrganisme
     */
    public void setTypeOrganisme(TypeOrganisme typeOrganisme) {
        this.typeOrganisme = typeOrganisme;
    }

    /**
     * identificateur getter
     * 
     * @return identificateur
     */
    public Personne getIdentificateur() {
        return identificateur;
    }

    /**
     * identificateur setter
     * 
     * @param identificateur identificateur
     */
    public void setIdentificateur(Personne identificateur) {
        this.identificateur = identificateur;
    }

    /**
     * dateDepot getter
     * 
     * @return dateDepot
     */
    public Date getDateDepot() {
        return dateDepot;
    }

    /**
     * dateDepot setter
     * 
     * @param dateDepot dateDepot
     */
    public void setDateDepot(Date dateDepot) {
        this.dateDepot = dateDepot;
    }

    /**
     * numDepot getter
     * 
     * @return numDepot
     */
    public String getNumDepot() {
        return numDepot;
    }

    /**
     * numDepot setter
     * 
     * @param numDepot numDepot
     */
    public void setNumDepot(String numDepot) {
        this.numDepot = numDepot;
    }

    /**
     * lieuDepot getter
     * 
     * @return lieuDepot
     */
    public String getLieuDepot() {
        return lieuDepot;
    }

    /**
     * lieuDepot setter
     * 
     * @param lieuDepot lieuDepot
     */
    public void setLieuDepot(String lieuDepot) {
        this.lieuDepot = lieuDepot;
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
