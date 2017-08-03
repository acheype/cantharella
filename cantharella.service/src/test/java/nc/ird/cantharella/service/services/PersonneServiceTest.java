/*
 * #%L
 * Cantharella :: Service
 * $Id: PersonneServiceTest.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.service/src/test/java/nc/ird/cantharella/service/services/PersonneServiceTest.java $
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
import nc.ird.cantharella.data.model.Personne;
import nc.ird.cantharella.service.AbstractServiceTest;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Test: personne service
 * 
 * @author Mickael Tricot
 */
public final class PersonneServiceTest extends AbstractServiceTest {

    /** Personne service */
    @Autowired
    private PersonneService personneService;

    /**
     * Test: checkOrCreateAdmin
     * 
     * @throws DataConstraintException -
     */
    @Test
    public void checkOrCreateAdmin() throws DataConstraintException {
        personneService.checkOrCreateAdmin();
    }

    /**
     * Test: load
     * 
     * @throws DataNotFoundException -
     * @throws DataConstraintException
     */
    @Test
    public void load() throws DataNotFoundException, DataConstraintException {
        personneService.checkOrCreateAdmin();
        List<Personne> personnes = personneService.listPersonnes();
        personneService.loadPersonne(personnes.get(0).getCourriel().toUpperCase());
    }
}
