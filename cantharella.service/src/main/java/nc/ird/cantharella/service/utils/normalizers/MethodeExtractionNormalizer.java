/*
 * #%L
 * Cantharella :: Service
 * $Id: MethodeExtractionNormalizer.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.service/src/main/java/nc/ird/cantharella/service/utils/normalizers/MethodeExtractionNormalizer.java $
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

import nc.ird.cantharella.data.model.MethodeExtraction;
import nc.ird.cantharella.data.model.TypeExtrait;
import nc.ird.cantharella.service.utils.normalizers.utils.Normalizer;
import nc.ird.cantharella.utils.AssertTools;

/**
 * MethodeExtraction normalizer
 * 
 * @author Adrien Cheype
 */
public final class MethodeExtractionNormalizer extends Normalizer<MethodeExtraction> {

    /** {@inheritDoc} */
    @Override
    protected MethodeExtraction normalize(MethodeExtraction methodeExtraction) {
        AssertTools.assertNotNull(methodeExtraction);
        // Unique fields
        methodeExtraction.setNom(Normalizer.normalize(ConfigNameNormalizer.class, methodeExtraction.getNom()));
        for (TypeExtrait curType : methodeExtraction.getTypesEnSortie()) {
            curType.setInitiales(Normalizer.normalize(ConfigNameNormalizer.class, curType.getInitiales()));
        }
        return methodeExtraction;
    }
}
