/*
 * #%L
 * Cantharella :: Data
 * $Id: MethodeTestBio.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.data/src/main/java/nc/ird/cantharella/data/model/MethodeTestBio.java $
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

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;

import nc.ird.cantharella.data.model.utils.AbstractModel;

import org.apache.commons.beanutils.BeanComparator;
import org.hibernate.annotations.Type;
import org.hibernate.search.annotations.Field;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Modèle : Methode de test biologique
 * 
 * @author Adrien Cheype
 */
@Entity
@Embeddable
public class MethodeTestBio extends AbstractModel implements Comparable<MethodeTestBio> {

    /** Id de la méthode */
    @Id
    @GeneratedValue
    private Integer idMethodeTest;

    /** Nom de la méthode */
    @Length(max = LENGTH_MEDIUM_TEXT)
    @Column(unique = true)
    @NotEmpty
    @Field
    private String nom;

    /** Cible pour un test (acronyme du nom de méthode) */
    @Length(max = LENGTH_MEDIUM_TEXT)
    @Column(unique = true)
    @NotEmpty
    @Field
    private String cible;

    /** Domaine de recherche pour le test */
    @Length(max = LENGTH_MEDIUM_TEXT)
    @NotEmpty
    private String domaine;

    /** Description de la méthode */
    @NotEmpty
    @Lob
    @Type(type = "org.hibernate.type.StringClobType")
    // see HHH-6105
    private String description;

    /** Valeur mesurée pour la méthode */
    @Length(max = LENGTH_LONG_TEXT)
    @NotEmpty
    private String valeurMesuree;

    /** Unité de mesure */
    @Length(max = LENGTH_TINY_TEXT)
    @NotEmpty
    private String uniteResultat;

    /** Critère d'activité de la méthode */
    @Length(max = LENGTH_LONG_TEXT)
    private String critereActivite;

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return nom;
    }

    /** {@inheritDoc} */
    @Override
    public int compareTo(MethodeTestBio methodeTest) {
        return new BeanComparator("nom").compare(this, methodeTest);
    }

    /**
     * idMethodeTest getter
     * 
     * @return idMethodeTest
     */
    public Integer getIdMethodeTest() {
        return idMethodeTest;
    }

    /**
     * idMethodeTest setter
     * 
     * @param idMethodeTest idMethodeTest
     */
    public void setIdMethodeTest(Integer idMethodeTest) {
        this.idMethodeTest = idMethodeTest;
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
     * cible getter
     * 
     * @return cible
     */
    public String getCible() {
        return cible;
    }

    /**
     * cible setter
     * 
     * @param cible cible
     */
    public void setCible(String cible) {
        this.cible = cible;
    }

    /**
     * domaine getter
     * 
     * @return domaine
     */
    public String getDomaine() {
        return domaine;
    }

    /**
     * domaine setter
     * 
     * @param domaine domaine
     */
    public void setDomaine(String domaine) {
        this.domaine = domaine;
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
     * valeurMesuree getter
     * 
     * @return valeurMesuree
     */
    public String getValeurMesuree() {
        return valeurMesuree;
    }

    /**
     * valeurMesuree setter
     * 
     * @param valeurMesuree valeurMesuree
     */
    public void setValeurMesuree(String valeurMesuree) {
        this.valeurMesuree = valeurMesuree;
    }

    /**
     * uniteResultat getter
     * 
     * @return uniteResultat
     */
    public String getUniteResultat() {
        return uniteResultat;
    }

    /**
     * uniteResultat setter
     * 
     * @param uniteResultat uniteResultat
     */
    public void setUniteResultat(String uniteResultat) {
        this.uniteResultat = uniteResultat;
    }

    /**
     * critereActivite getter
     * 
     * @return critereActivite
     */
    public String getCritereActivite() {
        return critereActivite;
    }

    /**
     * critereActivite setter
     * 
     * @param critereActivite critereActivite
     */
    public void setCritereActivite(String critereActivite) {
        this.critereActivite = critereActivite;
    }

}
