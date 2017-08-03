/*
 * #%L
 * Cantharella :: Data
 * $Id: DocumentAttachable.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.data/src/main/java/nc/ird/cantharella/data/model/utils/DocumentAttachable.java $
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
package nc.ird.cantharella.data.model.utils;

import java.io.Serializable;
import java.util.List;

import nc.ird.cantharella.data.model.Document;

/**
 * Interface to mark entity on which documents can be attached.
 * 
 * @author Eric Chatellier
 */
public interface DocumentAttachable extends Serializable {

    /**
     * Get document attached to entity.
     * 
     * @return document list
     */
    List<Document> getDocuments();

    /**
     * Attach new document.
     * 
     * @param document new document to attach
     */
    void addDocument(Document document);

    /**
     * Remove an attached document.
     * 
     * @param document document to remove
     */
    void removeDocument(Document document);
}
