/*
 * #%L
 * Cantharella :: Data
 * $Id: ModelValidatorTest.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.data/src/test/java/nc/ird/cantharella/data/model/ModelValidatorTest.java $
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
package nc.ird.cantharella.data.model;

import java.util.Locale;

import javax.xml.bind.ValidationException;

import nc.ird.cantharella.data.AbstractDataTest;
import nc.ird.cantharella.data.validation.utils.ModelValidator;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Test: Validator
 * 
 * @author Mickael Tricot
 */
public final class ModelValidatorTest extends AbstractDataTest {

    /** Validator */
    @Autowired
    private ModelValidator validator;

    /**
     * Test: validate
     */
    @Test
    public void validateModel() {
        Assert.assertFalse(validator.validate(new Utilisateur(), Locale.getDefault()).isEmpty());
    }

    /**
     * Test: validate properties
     * 
     * @throws ValidationException In case of validation errors
     */
    @Test
    public void validateModelProperty() throws ValidationException {
        Assert.assertFalse(validator.validate(new Utilisateur(), Locale.getDefault(), "courriel").isEmpty());
    }
}
