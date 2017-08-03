/*
 * #%L
 * Cantharella :: Service
 * $Id: TypeDocumentNormalizer.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.service/src/main/java/nc/ird/cantharella/service/utils/normalizers/TypeDocumentNormalizer.java $
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
package nc.ird.cantharella.service.utils.normalizers;

import nc.ird.cantharella.data.model.TypeDocument;
import nc.ird.cantharella.service.utils.normalizers.utils.Normalizer;
import nc.ird.cantharella.utils.AssertTools;

/**
 * Document type normalizer
 * 
 * @author Adrien Cheype
 */
public final class TypeDocumentNormalizer extends Normalizer<TypeDocument> {

    /** {@inheritDoc} */
    @Override
    protected TypeDocument normalize(TypeDocument typeDocument) {
        AssertTools.assertNotNull(typeDocument);
        // Unique field
        typeDocument.setNom(Normalizer.normalize(ConfigNameNormalizer.class, typeDocument.getNom()));
        return typeDocument;
    }
}
