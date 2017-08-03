/*
 * #%L
 * Cantharella :: Data
 * $Id: Personne.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.data/src/main/java/nc/ird/cantharella/data/model/Personne.java $
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import nc.ird.cantharella.data.model.utils.AbstractModel;
import nc.ird.cantharella.data.model.utils.DocumentAttachable;
import nc.ird.cantharella.data.validation.CountryCode;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections.comparators.ComparatorChain;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Modèle : personne
 * 
 * @author Mickael Tricot
 * @author Adrien Cheype
 */
@Entity
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "nom", "prenom" }) })
@Inheritance(strategy = InheritanceType.JOINED)
public class Personne extends AbstractModel implements Comparable<Personne>, DocumentAttachable {

    /** ID */
    @Id
    @GeneratedValue
    private Integer idPersonne;

    /** Adresse postale */
    @NotEmpty
    @Lob
    @Type(type = "org.hibernate.type.StringClobType")
    // see HHH-6105
    private String adressePostale;

    /** Campagnes créées */
    @OneToMany(mappedBy = "createur", fetch = FetchType.LAZY)
    @NotNull
    private List<Campagne> campagnesCreees;

    /** Droits sur les campagnes */
    @NotNull
    @OneToMany(mappedBy = "id.pk2", fetch = FetchType.LAZY, orphanRemoval = true)
    @MapKey(name = "id.pk1")
    @Cascade({ CascadeType.SAVE_UPDATE })
    private Map<Campagne, CampagnePersonneDroits> campagnesDroits;

    /** Campagnes participées */
    @OneToMany(mappedBy = "id.pk2", fetch = FetchType.LAZY)
    @NotNull
    private List<CampagnePersonneParticipant> campagnesParticipees;

    /** Code pays */
    @NotEmpty
    @Length(min = 2, max = 2)
    @CountryCode
    private String codePays;

    /** Code postal */
    @Length(max = LENGTH_TINY_TEXT)
    @NotEmpty
    private String codePostal;

    /** Courriel */
    @Column(unique = true)
    @Length(max = LENGTH_MEDIUM_TEXT)
    @Email
    @NotEmpty
    @Index(name = "courriel")
    private String courriel;

    /** Fax */
    @Length(max = LENGTH_MEDIUM_TEXT)
    private String fax;

    /** Fonction */
    @Length(max = LENGTH_MEDIUM_TEXT)
    private String fonction;

    /** Lots créés */
    @OneToMany(mappedBy = "createur", fetch = FetchType.LAZY)
    @NotNull
    private List<Lot> lotsCrees;

    /** Droits lots */
    @NotNull
    @OneToMany(mappedBy = "id.pk2", fetch = FetchType.LAZY, orphanRemoval = true)
    @MapKey(name = "id.pk1")
    @Cascade({ CascadeType.SAVE_UPDATE })
    private Map<Lot, LotPersonneDroits> lotsDroits;

    /** Nom */
    @Length(max = LENGTH_MEDIUM_TEXT)
    @NotEmpty
    private String nom;

    /** Organisme */
    @Length(max = LENGTH_MEDIUM_TEXT)
    @NotEmpty
    private String organisme;

    /** Prénom */
    @Length(max = LENGTH_MEDIUM_TEXT)
    @NotEmpty
    private String prenom;

    /** Téléphone */
    @Length(max = LENGTH_MEDIUM_TEXT)
    private String tel;

    /** Ville */
    @Length(max = LENGTH_MEDIUM_TEXT)
    @NotEmpty
    private String ville;

    /** Stations créées */
    @OneToMany(mappedBy = "createur", fetch = FetchType.LAZY)
    @NotNull
    private List<Station> stationsCrees;

    /** Spécimens créés */
    @OneToMany(mappedBy = "createur", fetch = FetchType.LAZY)
    @NotNull
    private List<Specimen> specimensCrees;

    /** Extractions créées */
    @OneToMany(mappedBy = "createur", fetch = FetchType.LAZY)
    @NotNull
    private List<Extraction> extractionsCrees;

    /** Purification créées */
    @OneToMany(mappedBy = "createur", fetch = FetchType.LAZY)
    @NotNull
    private List<Purification> purificationsCrees;

    /** Tests biologiques créés */
    @OneToMany(mappedBy = "createur", fetch = FetchType.LAZY)
    @NotNull
    private List<TestBio> testsBioCrees;

    /** Attached documents. */
    @OneToMany(fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "personne")
    @Cascade({ CascadeType.SAVE_UPDATE })
    @Fetch(value = FetchMode.SUBSELECT)
    // see HHH-1718
    private List<Document> documents;

    /**
     * Constructeur
     */
    public Personne() {
        campagnesCreees = new ArrayList<Campagne>();
        campagnesParticipees = new ArrayList<CampagnePersonneParticipant>();
        campagnesDroits = new HashMap<Campagne, CampagnePersonneDroits>();
        lotsCrees = new ArrayList<Lot>();
        lotsDroits = new HashMap<Lot, LotPersonneDroits>();
        stationsCrees = new ArrayList<Station>();
        specimensCrees = new ArrayList<Specimen>();
        extractionsCrees = new ArrayList<Extraction>();
        purificationsCrees = new ArrayList<Purification>();
        testsBioCrees = new ArrayList<TestBio>();
        documents = new ArrayList<Document>();
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return prenom + " " + nom;
    }

    /** {@inheritDoc} */
    @Override
    public int compareTo(Personne personne) {
        ComparatorChain comparatorChain = new ComparatorChain();
        comparatorChain.addComparator(new BeanComparator("nom"));
        comparatorChain.addComparator(new BeanComparator("prenom"));
        return comparatorChain.compare(this, personne);
    }

    /**
     * idPersonne getter
     * 
     * @return idPersonne
     */
    public Integer getIdPersonne() {
        return idPersonne;
    }

    /**
     * idPersonne setter
     * 
     * @param idPersonne idPersonne
     */
    public void setIdPersonne(Integer idPersonne) {
        this.idPersonne = idPersonne;
    }

    /**
     * adressePostale getter
     * 
     * @return adressePostale
     */
    public String getAdressePostale() {
        return adressePostale;
    }

    /**
     * adressePostale setter
     * 
     * @param adressePostale adressePostale
     */
    public void setAdressePostale(String adressePostale) {
        this.adressePostale = adressePostale;
    }

    /**
     * campagnesCreees getter
     * 
     * @return campagnesCreees
     */
    public List<Campagne> getCampagnesCreees() {
        return campagnesCreees;
    }

    /**
     * campagnesCreees setter
     * 
     * @param campagnesCreees campagnesCreees
     */
    public void setCampagnesCreees(List<Campagne> campagnesCreees) {
        this.campagnesCreees = campagnesCreees;
    }

    /**
     * campagnesDroits getter
     * 
     * @return campagnesDroits
     */
    public Map<Campagne, CampagnePersonneDroits> getCampagnesDroits() {
        return campagnesDroits;
    }

    /**
     * campagnesDroits setter
     * 
     * @param campagnesDroits campagnesDroits
     */
    public void setCampagnesDroits(Map<Campagne, CampagnePersonneDroits> campagnesDroits) {
        this.campagnesDroits = campagnesDroits;
    }

    /**
     * campagnesParticipees getter
     * 
     * @return campagnesParticipees
     */
    public List<CampagnePersonneParticipant> getCampagnesParticipees() {
        return campagnesParticipees;
    }

    /**
     * campagnesParticipees setter
     * 
     * @param campagnesParticipees campagnesParticipees
     */
    public void setCampagnesParticipees(List<CampagnePersonneParticipant> campagnesParticipees) {
        this.campagnesParticipees = campagnesParticipees;
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
     * codePostal getter
     * 
     * @return codePostal
     */
    public String getCodePostal() {
        return codePostal;
    }

    /**
     * codePostal setter
     * 
     * @param codePostal codePostal
     */
    public void setCodePostal(String codePostal) {
        this.codePostal = codePostal;
    }

    /**
     * courriel getter
     * 
     * @return courriel
     */
    public String getCourriel() {
        return courriel;
    }

    /**
     * courriel setter
     * 
     * @param courriel courriel
     */
    public void setCourriel(String courriel) {
        this.courriel = courriel;
    }

    /**
     * fax getter
     * 
     * @return fax
     */
    public String getFax() {
        return fax;
    }

    /**
     * fax setter
     * 
     * @param fax fax
     */
    public void setFax(String fax) {
        this.fax = fax;
    }

    /**
     * fonction getter
     * 
     * @return fonction
     */
    public String getFonction() {
        return fonction;
    }

    /**
     * fonction setter
     * 
     * @param fonction fonction
     */
    public void setFonction(String fonction) {
        this.fonction = fonction;
    }

    /**
     * lotsCrees getter
     * 
     * @return lotsCrees
     */
    public List<Lot> getLotsCrees() {
        return lotsCrees;
    }

    /**
     * lotsCrees setter
     * 
     * @param lotsCrees lotsCrees
     */
    public void setLotsCrees(List<Lot> lotsCrees) {
        this.lotsCrees = lotsCrees;
    }

    /**
     * lotsDroits getter
     * 
     * @return lotsDroits
     */
    public Map<Lot, LotPersonneDroits> getLotsDroits() {
        return lotsDroits;
    }

    /**
     * lotsDroits setter
     * 
     * @param lotsDroits lotsDroits
     */
    public void setLotsDroits(Map<Lot, LotPersonneDroits> lotsDroits) {
        this.lotsDroits = lotsDroits;
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
     * organisme getter
     * 
     * @return organisme
     */
    public String getOrganisme() {
        return organisme;
    }

    /**
     * organisme setter
     * 
     * @param organisme organisme
     */
    public void setOrganisme(String organisme) {
        this.organisme = organisme;
    }

    /**
     * prenom getter
     * 
     * @return prenom
     */
    public String getPrenom() {
        return prenom;
    }

    /**
     * prenom setter
     * 
     * @param prenom prenom
     */
    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    /**
     * tel getter
     * 
     * @return tel
     */
    public String getTel() {
        return tel;
    }

    /**
     * tel setter
     * 
     * @param tel tel
     */
    public void setTel(String tel) {
        this.tel = tel;
    }

    /**
     * ville getter
     * 
     * @return ville
     */
    public String getVille() {
        return ville;
    }

    /**
     * ville setter
     * 
     * @param ville ville
     */
    public void setVille(String ville) {
        this.ville = ville;
    }

    /**
     * stationsCrees getter
     * 
     * @return stationsCrees
     */
    public List<Station> getStationsCrees() {
        return stationsCrees;
    }

    /**
     * stationsCrees setter
     * 
     * @param stationsCrees stationsCrees
     */
    public void setStationsCrees(List<Station> stationsCrees) {
        this.stationsCrees = stationsCrees;
    }

    /**
     * specimensCrees getter
     * 
     * @return specimensCrees
     */
    public List<Specimen> getSpecimensCrees() {
        return specimensCrees;
    }

    /**
     * specimensCrees setter
     * 
     * @param specimensCrees specimensCrees
     */
    public void setSpecimensCrees(List<Specimen> specimensCrees) {
        this.specimensCrees = specimensCrees;
    }

    /**
     * extractionsCrees getter
     * 
     * @return extractionsCrees
     */
    public List<Extraction> getExtractionsCrees() {
        return extractionsCrees;
    }

    /**
     * extractionsCrees setter
     * 
     * @param extractionsCrees extractionsCrees
     */
    public void setExtractionsCrees(List<Extraction> extractionsCrees) {
        this.extractionsCrees = extractionsCrees;
    }

    /**
     * purificationsCrees getter
     * 
     * @return purificationsCrees
     */
    public List<Purification> getPurificationsCrees() {
        return purificationsCrees;
    }

    /**
     * purificationsCrees setter
     * 
     * @param purificationsCrees purificationsCrees
     */
    public void setPurificationsCrees(List<Purification> purificationsCrees) {
        this.purificationsCrees = purificationsCrees;
    }

    /**
     * testsBioCrees getter
     * 
     * @return testsBioCrees
     */
    public List<TestBio> getTestsBioCrees() {
        return testsBioCrees;
    }

    /**
     * testsBioCrees setter
     * 
     * @param testsBioCrees testsBioCrees
     */
    public void setTestsBioCrees(List<TestBio> testsBioCrees) {
        this.testsBioCrees = testsBioCrees;
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
