/*
 * #%L
 * Cantharella :: Data
 * $Id: Utilisateur.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.data/src/main/java/nc/ird/cantharella/data/model/Utilisateur.java $
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

import java.util.Date;

import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import nc.ird.cantharella.utils.PasswordTools;

import org.hibernate.annotations.Index;
import org.hibernate.validator.constraints.Length;

/**
 * Modèle : utilisateur
 * 
 * @author Mickael Tricot
 * @author Adrien Cheype
 */
@Entity
@Table
@Embeddable
public class Utilisateur extends Personne {

    /**
     * Types droits
     */
    public enum TypeDroit implements Comparable<TypeDroit> {
        /** Administrateur */
        ADMINISTRATEUR,
        /** Utilisateur */
        UTILISATEUR
    }

    /** Date de validité du compte */
    @Temporal(TemporalType.DATE)
    private Date dateValiditeCompte;

    /** Est-il valide ? */
    @NotNull
    @Index(name = "estValide")
    private Boolean estValide;

    /** Groupe */
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    private Groupe groupe;

    /** Mot de passe haché */
    @Length(min = PasswordTools.SHA1_LENGTH, max = PasswordTools.SHA1_LENGTH)
    @NotNull
    private String passwordHash;

    /** Type de droit */
    @NotNull
    @Enumerated(EnumType.ORDINAL)
    @Index(name = "typeDroit")
    private TypeDroit typeDroit;

    /**
     * Constructeur
     */
    public Utilisateur() {
        super();
    }

    /**
     * dateValiditeCompte getter
     * 
     * @return dateValiditeCompte
     */
    public Date getDateValiditeCompte() {
        return dateValiditeCompte;
    }

    /**
     * dateValiditeCompte setter
     * 
     * @param dateValiditeCompte dateValiditeCompte
     */
    public void setDateValiditeCompte(Date dateValiditeCompte) {
        this.dateValiditeCompte = dateValiditeCompte;
    }

    /**
     * estValide getter
     * 
     * @return estValide
     */
    public Boolean isValide() {
        return estValide;
    }

    /**
     * estValide setter
     * 
     * @param estValide estValide
     */
    public void setValide(Boolean estValide) {
        this.estValide = estValide;
    }

    /**
     * groupe getter
     * 
     * @return groupe
     */
    public Groupe getGroupe() {
        return groupe;
    }

    /**
     * groupe setter
     * 
     * @param groupe groupe
     */
    public void setGroupe(Groupe groupe) {
        this.groupe = groupe;
    }

    /**
     * passwordHash getter
     * 
     * @return passwordHash
     */
    public String getPasswordHash() {
        return passwordHash;
    }

    /**
     * passwordHash setter
     * 
     * @param passwordHash passwordHash
     */
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    /**
     * typeDroit getter
     * 
     * @return typeDroit
     */
    public TypeDroit getTypeDroit() {
        return typeDroit;
    }

    /**
     * typeDroit setter
     * 
     * @param typeDroit typeDroit
     */
    public void setTypeDroit(TypeDroit typeDroit) {
        this.typeDroit = typeDroit;
    }

}
