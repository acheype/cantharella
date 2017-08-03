/*
 * #%L
 * Cantharella :: Service
 * $Id: DocumentServiceImpl.java 268 2014-05-06 15:45:40Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.service/src/main/java/nc/ird/cantharella/service/services/impl/DocumentServiceImpl.java $
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
package nc.ird.cantharella.service.services.impl;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.imageio.ImageIO;

import nc.ird.cantharella.data.dao.GenericDao;
import nc.ird.cantharella.data.dao.impl.DocumentDao;
import nc.ird.cantharella.data.exceptions.DataConstraintException;
import nc.ird.cantharella.data.exceptions.DataNotFoundException;
import nc.ird.cantharella.data.exceptions.UnexpectedException;
import nc.ird.cantharella.data.model.Document;
import nc.ird.cantharella.data.model.DocumentContent;
import nc.ird.cantharella.data.model.TypeDocument;
import nc.ird.cantharella.data.model.Utilisateur;
import nc.ird.cantharella.data.model.Utilisateur.TypeDroit;
import nc.ird.cantharella.data.model.utils.AbstractModel;
import nc.ird.cantharella.data.model.utils.DocumentAttachable;
import nc.ird.cantharella.service.exceptions.InvalidFileExtensionException;
import nc.ird.cantharella.service.services.DocumentService;
import nc.ird.cantharella.utils.AssertTools;

import org.apache.commons.io.FilenameUtils;
import org.imgscalr.Scalr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Document service implementation
 * 
 * @author Adrien Cheype
 */
@Service
public final class DocumentServiceImpl implements DocumentService {

    /** Logger */
    private static final Logger LOG = LoggerFactory.getLogger(DocumentServiceImpl.class);

    /** DAO */
    @Autowired
    private GenericDao dao;

    /** Configuration allowed extension list */
    private Collection<String> documentExtensionAllowed;

    /**
     * Configuration allowed extension list setter.
     * 
     * @param documentExtensionAllowed configuration value
     */
    @Value("${document.extension.allowed}")
    private void setDcumentExtensionAllowed(String documentExtensionAllowed) {
        String[] values = documentExtensionAllowed.split("\\s*,\\s*");
        this.documentExtensionAllowed = Arrays.asList(values);
    }

    /** {@inheritDoc} */
    @Override
    public List<TypeDocument> listTypeDocuments() {
        return dao.readList(TypeDocument.class, "nom");
    }

    /** {@inheritDoc} */
    @Override
    public void createTypeDocument(TypeDocument typeDocument) throws DataConstraintException {
        LOG.info("createTypeDocument: " + typeDocument.getNom());
        dao.create(typeDocument);
    }

    /** {@inheritDoc} */
    @Override
    public TypeDocument loadTypeDocument(Integer idTypeDocument) throws DataNotFoundException {
        return dao.read(TypeDocument.class, idTypeDocument);
    }

    /** {@inheritDoc} */
    @Override
    public TypeDocument loadTypeDocument(String nom) throws DataNotFoundException {
        return dao.read(TypeDocument.class, "nom", nom);
    }

    /** {@inheritDoc} */
    @Override
    public void updateTypeDocument(TypeDocument typeDocument) throws DataConstraintException {
        LOG.info("updateTypeDocument: " + typeDocument.getNom());
        try {
            dao.update(typeDocument);
        } catch (DataNotFoundException e) {
            LOG.error(e.getMessage(), e);
            throw new UnexpectedException(e);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void deleteTypeDocument(TypeDocument typeDocument) throws DataConstraintException {
        AssertTools.assertNotNull(typeDocument);
        LOG.info("deleteTypeDocument: " + typeDocument.getNom());
        try {
            dao.delete(typeDocument);
        } catch (DataNotFoundException e) {
            LOG.error(e.getMessage(), e);
            throw new UnexpectedException(e);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void updateDocumentAttachable(DocumentAttachable documentAttachable) throws DataConstraintException {
        AssertTools.assertNotNull(documentAttachable);
        LOG.info("updateDocumentAttachable: " + documentAttachable.toString());
        try {
            // DocumentAttachable est une interface mais toutes
            // les entity qui en herite herite aussi de AbstractModel
            dao.update((AbstractModel) documentAttachable);
        } catch (DataNotFoundException e) {
            LOG.error(e.getMessage(), e);
            throw new UnexpectedException(e);
        }

    }

    /** {@inheritDoc} */
    @Override
    @SuppressWarnings("unchecked")
    public List<String> listDocumentEditeurs() {
        List<String> result = (List<String>) dao.list(DocumentDao.CRITERIA_DISTINCT_DOCUMENT_EDITEURS);
        return result;
    }

    /** {@inheritDoc} */
    @Override
    @SuppressWarnings("unchecked")
    public List<String> listDocumentContrainteLegales() {
        List<String> result = (List<String>) dao.list(DocumentDao.CRITERIA_DISTINCT_DOCUMENT_CONTRAINTE_LEGALES);
        return result;
    }

    /** {@inheritDoc} */
    @Override
    public void addDocumentContent(Document document, String clientFileName, String contentType, byte[] content)
            throws InvalidFileExtensionException {
        AssertTools.assertNotEmpty(clientFileName);

        String extension = FilenameUtils.getExtension(clientFileName);
        extension = extension.toLowerCase(); // check lower case
        if (!documentExtensionAllowed.contains(extension)) {
            throw new InvalidFileExtensionException("File extension is not valid");
        }

        try {
            // resize original image
            document.setFileName(clientFileName);
            document.setFileMimetype(contentType);

            // original file content
            DocumentContent documentContent = document.getFileContent();
            if (documentContent == null) {
                documentContent = new DocumentContent();
                document.setFileContent(documentContent);
            }
            documentContent.setFileContent(content);

            // image detection is based on file mimetype
            DocumentContent thumbnailContent = document.getFileContentThumb();
            if (contentType.startsWith("image/")) {
                BufferedImage originalImage = ImageIO.read(new ByteArrayInputStream(content));
                BufferedImage thumbImage = Scalr.resize(originalImage, 100, 100);

                ByteArrayOutputStream thumbStream = new ByteArrayOutputStream();
                ImageIO.write(thumbImage, "png", thumbStream);

                // thumbnail content
                if (thumbnailContent == null) {
                    thumbnailContent = new DocumentContent();
                }
                thumbnailContent.setFileContent(thumbStream.toByteArray());
            } else {
                // make sure that changing content type doesn't keep an old
                // image thumb
                thumbnailContent = null;
            }
            document.setFileContentThumb(thumbnailContent);
        } catch (IOException ex) {
            LOG.error("Can't manipulate image", ex);
            throw new UnexpectedException("Can't manipulate image", ex);
        }
    }

    /** {@inheritDoc} */
    @Override
    public boolean updateOrdeleteDocumentEnabled(Document document, Utilisateur utilisateur) {
        return utilisateur.getTypeDroit() == TypeDroit.ADMINISTRATEUR
                || utilisateur.getIdPersonne() == document.getCreateur().getIdPersonne();
    }
}
