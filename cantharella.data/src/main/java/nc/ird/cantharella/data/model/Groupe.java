/*
 * #%L
 * Cantharella :: Data
 * $Id: Groupe.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.data/src/main/java/nc/ird/cantharella/data/model/Groupe.java $
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
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import nc.ird.cantharella.data.model.utils.AbstractModel;

import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Modèle : groupe
 * 
 * @author Mickael Tricot
 */
@Entity
@Table
public class Groupe extends AbstractModel {

    /** Droits sur les campagnes */
    @OneToMany(mappedBy = "id.pk2", fetch = FetchType.LAZY)
    @NotNull
    private List<CampagneGroupeDroits> campagnesDroits;

    /** Description */
    @Lob
    @Type(type = "org.hibernate.type.StringClobType")
    // see HHH-6105
    private String description;

    /** Groupe */
    @Id
    @GeneratedValue
    private Integer idGroupe;

    /** Droits sur les lots */
    @OneToMany(mappedBy = "id.pk2", fetch = FetchType.LAZY)
    @NotNull
    private List<LotGroupeDroits> lotsDroits;

    /** Nom */
    @Column(unique = true)
    @Length(max = LENGTH_MEDIUM_TEXT)
    @NotEmpty
    private String nom;

    /** Utilisateurs */
    @OneToMany(mappedBy = "groupe", fetch = FetchType.LAZY)
    @NotNull
    private List<Utilisateur> utilisateurs;

    /**
     * Constructeur
     */
    public Groupe() {
        utilisateurs = new ArrayList<Utilisateur>();
        campagnesDroits = new ArrayList<CampagneGroupeDroits>();
        lotsDroits = new ArrayList<LotGroupeDroits>();
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return nom;
    }

    /**
     * campagnesDroits getter
     * 
     * @return campagnesDroits
     */
    public List<CampagneGroupeDroits> getCampagnesDroits() {
        return campagnesDroits;
    }

    /**
     * campagnesDroits setter
     * 
     * @param campagnesDroits campagnesDroits
     */
    public void setCampagnesDroits(List<CampagneGroupeDroits> campagnesDroits) {
        this.campagnesDroits = campagnesDroits;
    }

    /**
     * description getter
     * 
     * @return description
     */
    public String getDescription() {
        return description;
    }

    /**
     * description setter
     * 
     * @param description description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * idGroupe getter
     * 
     * @return idGroupe
     */
    public Integer getIdGroupe() {
        return idGroupe;
    }

    /**
     * idGroupe setter
     * 
     * @param idGroupe idGroupe
     */
    public void setIdGroupe(Integer idGroupe) {
        this.idGroupe = idGroupe;
    }

    /**
     * lotsDroits getter
     * 
     * @return lotsDroits
     */
    public List<LotGroupeDroits> getLotsDroits() {
        return lotsDroits;
    }

    /**
     * lotsDroits setter
     * 
     * @param lotsDroits lotsDroits
     */
    public void setLotsDroits(List<LotGroupeDroits> lotsDroits) {
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
     * utilisateurs getter
     * 
     * @return utilisateurs
     */
    public List<Utilisateur> getUtilisateurs() {
        return utilisateurs;
    }

    /**
     * utilisateurs setter
     * 
     * @param utilisateurs utilisateurs
     */
    public void setUtilisateurs(List<Utilisateur> utilisateurs) {
        this.utilisateurs = utilisateurs;
    }

}
