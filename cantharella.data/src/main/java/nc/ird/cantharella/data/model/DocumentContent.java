/*
 * #%L
 * Cantharella :: Data
 * $Id: DocumentContent.java 268 2014-05-06 15:45:40Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.data/src/main/java/nc/ird/cantharella/data/model/DocumentContent.java $
 * %%
 * Copyright (C) 2009 - 2014 IRD (Institut de Recherche pour le Developpement) and by respective authors (see below)
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

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

import nc.ird.cantharella.data.model.utils.AbstractModel;

/**
 * Document entity content.
 * 
 * This entity extract bytea (byte[]) content from document entity for lazy loading to not load on file content from
 * database.
 * 
 * @author Eric Chatellier
 */
@Entity
public class DocumentContent extends AbstractModel {

    /** Document content id. */
    @Id
    @GeneratedValue
    private Integer idDocumentContent;

    /** Binary content. */
    @Basic(fetch = FetchType.LAZY)
    @NotNull
    private byte[] fileContent;

    /**
     * Document content id getter.
     * 
     * @return document id
     */
    public Integer getIdDocumentContent() {
        return idDocumentContent;
    }

    /**
     * Document id setter
     * 
     * @param idDocumentContent document id
     */
    public void setIdDocumentContent(Integer idDocumentContent) {
        this.idDocumentContent = idDocumentContent;
    }

    /**
     * File content getter.
     * 
     * @return file content
     */
    public byte[] getFileContent() {
        return fileContent;
    }

    /**
     * File content setter.
     * 
     * @param fileContent file content
     */
    public void setFileContent(byte[] fileContent) {
        this.fileContent = fileContent;
    }
}
