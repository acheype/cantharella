/*
 * #%L
 * Cantharella :: Service
 * $Id: SearchService.java 153 2013-02-22 17:56:05Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.service/src/main/java/nc/ird/cantharella/service/services/SearchService.java $
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
package nc.ird.cantharella.service.services;

import nc.ird.cantharella.data.model.Utilisateur;
import nc.ird.cantharella.service.model.SearchBean;
import nc.ird.cantharella.service.model.SearchResult;

import org.springframework.transaction.annotation.Transactional;

/**
 * Service de recherche.
 * 
 * @author echatellier
 */
public interface SearchService {

    /**
     * Rebuild all indexed with existing database entities.
     */
    void reIndex();

    /**
     * Search for query result into data model.
     * 
     * @param search search query
     * @param utilisateur utilisateur to filter results
     * @return search result
     */
    @Transactional(readOnly = true)
    SearchResult search(SearchBean search, Utilisateur utilisateur);
}
