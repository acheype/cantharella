/*
 * #%L
 * Cantharella :: Data
 * $Id: ReferentielValidator.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.data/src/main/java/nc/ird/cantharella/data/validation/ReferentielValidator.java $
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
package nc.ird.cantharella.data.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import nc.ird.cantharella.data.config.DataContext;

/**
 * Country code validator
 * 
 * @author Mickael Tricot
 * @author Adrien Cheype
 */
public final class ReferentielValidator implements ConstraintValidator<Referentiel, Integer> {

    /** {@inheritDoc} */
    @Override
    public void initialize(Referentiel annotation) {
        //
    }

    /** {@inheritDoc} */
    @Override
    public boolean isValid(Integer referentiel, ConstraintValidatorContext constraintContext) {
        return referentiel == null || DataContext.REFERENTIELS.keySet().contains(referentiel);
    }
}