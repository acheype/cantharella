/*
 * #%L
 * Cantharella :: Data
 * $Id: Molecule.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.data/src/main/java/nc/ird/cantharella/data/model/Molecule.java $
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
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import nc.ird.cantharella.data.config.DataContext;
import nc.ird.cantharella.data.model.utils.AbstractModel;
import nc.ird.cantharella.data.model.utils.DocumentAttachable;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Type;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.IndexedEmbedded;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Molecule entity.
 * 
 * @author Eric Chatellier
 */
@Entity
@Indexed
public class Molecule extends AbstractModel implements DocumentAttachable {

    /** ID */
    @Id
    @GeneratedValue(generator = "molecule_sequence_gen")
    @SequenceGenerator(name = "molecule_sequence_gen", sequenceName = "molecule_sequence")
    private Integer idMolecule;

    /** Nom commun */
    @Length(max = LENGTH_LONG_TEXT)
    @Field
    private String nomCommun;

    /** Famille chimique */
    @Length(max = LENGTH_MEDIUM_TEXT)
    @Field
    private String familleChimique;

    /** Famille developpée */
    @Lob
    @Type(type = "org.hibernate.type.StringClobType")
    // see HHH-6105
    private String formuleDevMol;

    /** Nom IUPAC */
    @Length(max = LENGTH_BIG_TEXT)
    @Field
    private String nomIupca;

    /** Formule brute */
    @Length(max = LENGTH_MEDIUM_TEXT)
    @NotEmpty
    @Field
    private String formuleBrute;

    /** Masse molaire */
    @Min(value = 0)
    @Max(value = DataContext.DECIMAL_MAX)
    @Column(precision = DataContext.DECIMAL_PRECISION, scale = DataContext.DECIMAL_SCALE)
    private BigDecimal masseMolaire;

    /** Est-ce une nouvelle molécule ? */
    private boolean nouvMolecul;

    /** Campagne. */
    @ManyToOne(fetch = FetchType.EAGER)
    @IndexedEmbedded
    private Campagne campagne;

    /** Identifiee par. */
    @Length(max = LENGTH_MEDIUM_TEXT)
    @Field
    private String identifieePar;

    /** Publication d'origine */
    @Lob
    @Type(type = "org.hibernate.type.StringClobType")
    // see HHH-6105
    private String publiOrigine;

    /** Complement */
    @Lob
    @Type(type = "org.hibernate.type.StringClobType")
    // see HHH-6105
    private String complement;

    /** Créateur */
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Personne createur;

    /** Produit utilisé obtenir le résultat */
    @OneToMany(mappedBy = "molecule", fetch = FetchType.LAZY, orphanRemoval = true)
    @Cascade({ CascadeType.SAVE_UPDATE })
    @IndexedEmbedded
    private List<MoleculeProvenance> provenances;

    /** Attached documents. */
    @OneToMany(fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "molecule")
    @Cascade({ CascadeType.SAVE_UPDATE })
    @Fetch(value = FetchMode.SUBSELECT)
    // see HHH-1718
    private List<Document> documents;

    /**
     * Constructor.
     */
    public Molecule() {
        provenances = new ArrayList<MoleculeProvenance>();
        documents = new ArrayList<Document>();
    }

    /**
     * Id molecule getter.
     * 
     * @return the idMolecule
     */
    public Integer getIdMolecule() {
        return idMolecule;
    }

    /**
     * Id molecule setter.
     * 
     * @param idMolecule the idMolecule to set
     */
    public void setIdMolecule(Integer idMolecule) {
        this.idMolecule = idMolecule;
    }

    /**
     * Nom commun getter.
     * 
     * @return the nomCommun
     */
    public String getNomCommun() {
        return nomCommun;
    }

    /**
     * Nom commun getter.
     * 
     * @param nomCommun the nomCommun to set
     */
    public void setNomCommun(String nomCommun) {
        this.nomCommun = nomCommun;
    }

    /**
     * Famille chimique getter.
     * 
     * @return the familleChimique
     */
    public String getFamilleChimique() {
        return familleChimique;
    }

    /**
     * Famille chimique setter.
     * 
     * @param familleChimique the familleChimique to set
     */
    public void setFamilleChimique(String familleChimique) {
        this.familleChimique = familleChimique;
    }

    /**
     * Fomule dev mol getter.
     * 
     * @return the formuleDevMol
     */
    public String getFormuleDevMol() {
        return formuleDevMol;
    }

    /**
     * Formule dev mol setter.
     * 
     * @param formuleDevMol the formuleDevMol to set
     */
    public void setFormuleDevMol(String formuleDevMol) {
        this.formuleDevMol = formuleDevMol;
    }

    /**
     * Nom iupca getter.
     * 
     * @return the nomIupca
     */
    public String getNomIupca() {
        return nomIupca;
    }

    /**
     * Nom iupca setter.
     * 
     * @param nomIupca the nomIupca to set
     */
    public void setNomIupca(String nomIupca) {
        this.nomIupca = nomIupca;
    }

    /**
     * Formule brute getter.
     * 
     * @return the formuleBrute
     */
    public String getFormuleBrute() {
        return formuleBrute;
    }

    /**
     * Formule brute setter.
     * 
     * @param formuleBrute the formuleBrute to set
     */
    public void setFormuleBrute(String formuleBrute) {
        this.formuleBrute = formuleBrute;
    }

    /**
     * Masse molaire getter.
     * 
     * @return the masseMolaire
     */
    public BigDecimal getMasseMolaire() {
        return masseMolaire;
    }

    /**
     * Masse molaire setter.
     * 
     * @param masseMolaire the masseMolaire to set
     */
    public void setMasseMolaire(BigDecimal masseMolaire) {
        this.masseMolaire = masseMolaire;
    }

    /**
     * Nouv molecule getter.
     * 
     * @return the nouvMolecul
     */
    public boolean isNouvMolecul() {
        return nouvMolecul;
    }

    /**
     * Nouv molecule setter.
     * 
     * @param nouvMolecul the nouvMolecul to set
     */
    public void setNouvMolecul(boolean nouvMolecul) {
        this.nouvMolecul = nouvMolecul;
    }

    /**
     * Campagne getter.
     * 
     * @return the campagne
     */
    public Campagne getCampagne() {
        return campagne;
    }

    /**
     * Campagne setter.
     * 
     * @param campagne the campagne to set
     */
    public void setCampagne(Campagne campagne) {
        this.campagne = campagne;
    }

    /**
     * Identifiee par getter.
     * 
     * @return the identifieePar
     */
    public String getIdentifieePar() {
        return identifieePar;
    }

    /**
     * Identifiee par setter.
     * 
     * @param identifieePar the identifieePar to set
     */
    public void setIdentifieePar(String identifieePar) {
        this.identifieePar = identifieePar;
    }

    /**
     * Publi origine getter.
     * 
     * @return the publiOrigine
     */
    public String getPubliOrigine() {
        return publiOrigine;
    }

    /**
     * Publi origine setter.
     * 
     * @param publiOrigine the publiOrigine to set
     */
    public void setPubliOrigine(String publiOrigine) {
        this.publiOrigine = publiOrigine;
    }

    /**
     * Complement getter.
     * 
     * @return the complement
     */
    public String getComplement() {
        return complement;
    }

    /**
     * Complement setter.
     * 
     * @param complement the complement to set
     */
    public void setComplement(String complement) {
        this.complement = complement;
    }

    /**
     * Createur getter.
     * 
     * @return the createur
     */
    public Personne getCreateur() {
        return createur;
    }

    /**
     * Createur setter.
     * 
     * @param createur the createur to set
     */
    public void setCreateur(Personne createur) {
        this.createur = createur;
    }

    /**
     * Provenances getter.
     * 
     * @return the provenances
     */
    public List<MoleculeProvenance> getProvenances() {
        return provenances;
    }

    /**
     * Provenances setter.
     * 
     * @param provenances the provenances to set
     */
    public void setProvenances(List<MoleculeProvenance> provenances) {
        this.provenances = provenances;
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
