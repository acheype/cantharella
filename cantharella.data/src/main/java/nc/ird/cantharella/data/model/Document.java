/*
 * #%L
 * Cantharella :: Data
 * $Id: Document.java 268 2014-05-06 15:45:40Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.data/src/main/java/nc/ird/cantharella/data/model/Document.java $
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
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import nc.ird.cantharella.data.model.utils.AbstractModel;
import nc.ird.cantharella.data.validation.LanguageCode;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Document entity.
 * 
 * @author Eric Chatellier
 */
@Entity
@Embeddable
public class Document extends AbstractModel {

    /** Id du document. */
    @Id
    @GeneratedValue
    private Integer idDocument;

    /** Titre. */
    @Length(max = LENGTH_LONG_TEXT)
    @NotEmpty
    private String titre;

    /** Createur. */
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Personne createur;

    /** Date de creation. */
    @NotNull
    @Temporal(TemporalType.DATE)
    private Date dateCreation;

    /** Editeur. */
    @Length(max = LENGTH_LONG_TEXT)
    @NotEmpty
    private String editeur;

    /** Description. */
    @Lob
    @Type(type = "org.hibernate.type.StringClobType")
    // see HHH-6105
    private String description;

    /** Langue. */
    @Length(min = 2, max = 2)
    @LanguageCode
    private String langue;

    /** Contrainte légale. */
    @Length(max = LENGTH_LONG_TEXT)
    private String contrainteLegale;

    /** Ajouté par. */
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Personne ajoutePar;

    /** Type document. */
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private TypeDocument typeDocument;

    /** File name. */
    @NotEmpty
    @Length(max = LENGTH_MEDIUM_TEXT)
    private String fileName;

    /** File data. */
    @NotNull
    @OneToOne(orphanRemoval = true, optional = false, fetch = FetchType.LAZY)
    @Cascade({ CascadeType.SAVE_UPDATE })
    private DocumentContent fileContent;

    /** File data thumbnail. */
    @OneToOne(orphanRemoval = true, optional = true, fetch = FetchType.LAZY)
    @Cascade({ CascadeType.SAVE_UPDATE })
    private DocumentContent fileContentThumb;

    /** File mime type. */
    @NotEmpty
    @Length(max = LENGTH_MEDIUM_TEXT)
    private String fileMimetype;

    /**
     * Document id getter.
     * 
     * @return document id
     */
    public Integer getIdDocument() {
        return idDocument;
    }

    /**
     * Document id setter.
     * 
     * @param idDocument document id
     */
    public void setIdDocument(Integer idDocument) {
        this.idDocument = idDocument;
    }

    /**
     * Titre getter.
     * 
     * @return titre
     */
    public String getTitre() {
        return titre;
    }

    /**
     * Titre setter.
     * 
     * @param titre titre
     */
    public void setTitre(String titre) {
        this.titre = titre;
    }

    /**
     * Createur getter.
     * 
     * @return createur
     */
    public Personne getCreateur() {
        return createur;
    }

    /**
     * Createur setter.
     * 
     * @param createur createur
     */
    public void setCreateur(Personne createur) {
        this.createur = createur;
    }

    /**
     * Date creation getter.
     * 
     * @return date creation
     */
    public Date getDateCreation() {
        return dateCreation;
    }

    /**
     * Date creation setter.
     * 
     * @param dateCreation date creation
     */
    public void setDateCreation(Date dateCreation) {
        this.dateCreation = dateCreation;
    }

    /**
     * Editeur getter.
     * 
     * @return editeur
     */
    public String getEditeur() {
        return editeur;
    }

    /**
     * Editeur setter.
     * 
     * @param editeur editeur
     */
    public void setEditeur(String editeur) {
        this.editeur = editeur;
    }

    /**
     * Description getter.
     * 
     * @return description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Description setter.
     * 
     * @param description description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Langue getter.
     * 
     * @return langue
     */
    public String getLangue() {
        return langue;
    }

    /**
     * Langue setter.
     * 
     * @param langue langue
     */
    public void setLangue(String langue) {
        this.langue = langue;
    }

    /**
     * Contrainte legale getter.
     * 
     * @return contrainte legale
     */
    public String getContrainteLegale() {
        return contrainteLegale;
    }

    /**
     * Contrainte legale setter.
     * 
     * @param contrainteLegale containte legale
     */
    public void setContrainteLegale(String contrainteLegale) {
        this.contrainteLegale = contrainteLegale;
    }

    /**
     * Ajoute par setter.
     * 
     * @return ajoute apr
     */
    public Personne getAjoutePar() {
        return ajoutePar;
    }

    /**
     * Ajoute par setter.
     * 
     * @param ajoutePar
     */
    public void setAjoutePar(Personne ajoutePar) {
        this.ajoutePar = ajoutePar;
    }

    /**
     * Type document getter.
     * 
     * @return type document
     */
    public TypeDocument getTypeDocument() {
        return typeDocument;
    }

    /**
     * Type document setter.
     * 
     * @param typeDocument type document
     */
    public void setTypeDocument(TypeDocument typeDocument) {
        this.typeDocument = typeDocument;
    }

    /**
     * File name getter.
     * 
     * @return file name
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * File name setter.
     * 
     * @param fileName file name
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * File content getter.
     * 
     * @return file content
     */
    public DocumentContent getFileContent() {
        return fileContent;
    }

    /**
     * File content setter.
     * 
     * @param fileContent file content
     */
    public void setFileContent(DocumentContent fileContent) {
        this.fileContent = fileContent;
    }

    /**
     * File content thumbnail getter.
     * 
     * @return file content thumbnail
     */
    public DocumentContent getFileContentThumb() {
        return fileContentThumb;
    }

    /**
     * File content thumbnail setter.
     * 
     * @param fileContentThumb file content thumbnail
     */
    public void setFileContentThumb(DocumentContent fileContentThumb) {
        this.fileContentThumb = fileContentThumb;
    }

    /**
     * File mime type getter.
     * 
     * @return file mime type
     */
    public String getFileMimetype() {
        return fileMimetype;
    }

    /**
     * File mime type setter.
     * 
     * @param fileMimetype file mime type
     */
    public void setFileMimetype(String fileMimetype) {
        this.fileMimetype = fileMimetype;
    }
}
