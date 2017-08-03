/*
 * #%L
 * Cantharella :: Service
 * $Id: SpecimenNormalizer.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.service/src/main/java/nc/ird/cantharella/service/utils/normalizers/SpecimenNormalizer.java $
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

import nc.ird.cantharella.data.model.Specimen;
import nc.ird.cantharella.service.utils.normalizers.utils.Normalizer;
import nc.ird.cantharella.utils.AssertTools;
import nc.ird.cantharella.utils.StringTransformer;

/**
 * Spécimen normalizer
 * 
 * @author Mickael Tricot
 * @author Adrien Cheype
 */
public final class SpecimenNormalizer extends Normalizer<Specimen> {

    /** {@inheritDoc} */
    @Override
    protected Specimen normalize(Specimen specimen) {
        AssertTools.assertNotNull(specimen);
        // Unique fields
        specimen.setRef(normalize(UniqueFieldNormalizer.class, specimen.getRef()));

        specimen.setEmbranchement(new StringTransformer(specimen.getEmbranchement()).replaceConsecutiveWhitespaces()
                .trimToNull().toLowerCase().capitalize().toString());
        specimen.setFamille(new StringTransformer(specimen.getFamille()).replaceConsecutiveWhitespaces().trimToNull()
                .toLowerCase().capitalize().toString());
        specimen.setGenre(new StringTransformer(specimen.getGenre()).replaceConsecutiveWhitespaces().trimToNull()
                .toLowerCase().capitalize().toString());
        specimen.setEspece(new StringTransformer(specimen.getEspece()).replaceConsecutiveWhitespaces().trimToNull()
                .toLowerCase().toString());
        specimen.setSousEspece(new StringTransformer(specimen.getSousEspece()).replaceConsecutiveWhitespaces()
                .trimToNull().toLowerCase().toString());
        specimen.setVariete(new StringTransformer(specimen.getVariete()).replaceConsecutiveWhitespaces().trimToNull()
                .toLowerCase().toString());
        specimen.setLieuDepot(new StringTransformer(specimen.getLieuDepot()).trimToNull()
                .replaceConsecutiveWhitespaces().toString());

        return specimen;
    }
}
