/*
 * #%L
 * Cantharella :: Service
 * $Id: DocumentService.java 268 2014-05-06 15:45:40Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.service/src/main/java/nc/ird/cantharella/service/services/DocumentService.java $
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
package nc.ird.cantharella.service.services;

import java.util.List;

import nc.ird.cantharella.data.exceptions.DataConstraintException;
import nc.ird.cantharella.data.exceptions.DataNotFoundException;
import nc.ird.cantharella.data.model.Document;
import nc.ird.cantharella.data.model.TypeDocument;
import nc.ird.cantharella.data.model.Utilisateur;
import nc.ird.cantharella.data.model.utils.DocumentAttachable;
import nc.ird.cantharella.service.exceptions.InvalidFileExtensionException;
import nc.ird.cantharella.service.utils.normalizers.TypeDocumentNormalizer;
import nc.ird.cantharella.service.utils.normalizers.UniqueFieldNormalizer;
import nc.ird.cantharella.service.utils.normalizers.utils.Normalize;

import org.springframework.transaction.annotation.Transactional;

/**
 * Services for documents
 * 
 * @author Adrien Cheype
 */
public interface DocumentService {

    /**
     * List the document types available
     * 
     * @return The list of document types
     */
    @Transactional(readOnly = true)
    List<TypeDocument> listTypeDocuments();

    /**
     * Create a document type
     * 
     * @param typeDocument The document type to create
     * @throws DataConstraintException If the document type already exists (unique constraints)
     */
    void createTypeDocument(@Normalize(TypeDocumentNormalizer.class) TypeDocument typeDocument)
            throws DataConstraintException;

    /**
     * Load a document type
     * 
     * @param idTypeDocument ID
     * @return The corresponding document type
     * @throws DataNotFoundException If not found
     */
    TypeDocument loadTypeDocument(Integer idTypeDocument) throws DataNotFoundException;

    /**
     * Charger a document type
     * 
     * @param nom The document type name
     * @return The corresponding document type
     * @throws DataNotFoundException If not found
     */
    TypeDocument loadTypeDocument(@Normalize(UniqueFieldNormalizer.class) String nom) throws DataNotFoundException;

    /**
     * Modify a document type
     * 
     * @param typeDocument The document type to modify
     * @throws DataConstraintException If an unique constraint is broken with another document type
     */
    void updateTypeDocument(@Normalize(TypeDocumentNormalizer.class) TypeDocument typeDocument)
            throws DataConstraintException;

    /**
     * Delete a document type
     * 
     * @param typeDocument The document type to delete
     * @throws DataConstraintException If the document type has linked data
     */
    void deleteTypeDocument(TypeDocument typeDocument) throws DataConstraintException;

    /**
     * Editeurs already registered into documents.
     * 
     * @return document's editeur
     */
    @Transactional(readOnly = true)
    List<String> listDocumentEditeurs();

    /**
     * Liste les programmes des organismes déjà saisis
     * 
     * @return Organisme des molécules
     */
    @Transactional(readOnly = true)
    List<String> listDocumentContrainteLegales();

    /**
     * Modify a document attachable
     * 
     * @param documentAttachable The document attachable to modify
     * @throws DataConstraintException If an unique constraint is broken with another document
     */
    void updateDocumentAttachable(DocumentAttachable documentAttachable) throws DataConstraintException;

    /**
     * Manage attached file data as image and image thumbnail if possible.
     * 
     * @param document current document
     * @param clientFileName file name
     * @param contentType content type
     * @param content content data
     * @throws InvalidFileExtensionException when file name extension is not valid
     */
    @Transactional(readOnly = true)
    void addDocumentContent(Document document, String clientFileName, String contentType, byte[] content)
            throws InvalidFileExtensionException;

    /**
     * Détermine si un utilisateur peut modifier ou supprimer un document.
     * 
     * @param document Document
     * @param utilisateur Utilisateur
     * @return TRUE si il a le droit
     */
    @Transactional(readOnly = true)
    public boolean updateOrdeleteDocumentEnabled(Document document, Utilisateur utilisateur);
}
