/*
 * #%L
 * Cantharella :: Service
 * $Id: ExtractionServiceImpl.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.service/src/main/java/nc/ird/cantharella/service/services/impl/ExtractionServiceImpl.java $
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

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import nc.ird.cantharella.data.dao.GenericDao;
import nc.ird.cantharella.data.dao.impl.ExtractionDao;
import nc.ird.cantharella.data.exceptions.DataConstraintException;
import nc.ird.cantharella.data.exceptions.DataNotFoundException;
import nc.ird.cantharella.data.exceptions.UnexpectedException;
import nc.ird.cantharella.data.model.Extraction;
import nc.ird.cantharella.data.model.Extrait;
import nc.ird.cantharella.data.model.Lot;
import nc.ird.cantharella.data.model.MethodeExtraction;
import nc.ird.cantharella.data.model.TypeExtrait;
import nc.ird.cantharella.data.model.Utilisateur;
import nc.ird.cantharella.data.model.Utilisateur.TypeDroit;
import nc.ird.cantharella.service.services.ExtractionService;
import nc.ird.cantharella.service.services.LotService;
import nc.ird.cantharella.utils.AssertTools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Implémentation du service extrait
 * 
 * @author Mickael Tricot
 * @author Adrien Cheype
 */
@Service
public final class ExtractionServiceImpl implements ExtractionService {

    /** Logger */
    private static final Logger LOG = LoggerFactory.getLogger(ExtractionServiceImpl.class);

    /** DAO */
    @Autowired
    private GenericDao dao;

    /** Service : lots **/
    @Autowired
    private LotService lotService;

    /** {@inheritDoc} */
    @Override
    public long countExtractions() {
        return dao.count(Extraction.class);
    }

    /** {@inheritDoc} */
    @Override
    public void createExtraction(Extraction extraction) throws DataConstraintException {
        LOG.info("createExtraction: " + extraction.getRef());
        dao.create(extraction);
    }

    /** {@inheritDoc} */
    @Override
    public void deleteExtraction(Extraction extraction) throws DataConstraintException {
        AssertTools.assertNotNull(extraction);
        LOG.info("deleteExtraction: " + extraction.getRef());
        try {
            dao.delete(extraction);
        } catch (DataNotFoundException e) {
            LOG.error(e.getMessage(), e);
            throw new UnexpectedException(e);
        }

    }

    /** {@inheritDoc} */
    @Override
    public List<Extraction> listExtractions(Utilisateur utilisateur) {
        AssertTools.assertNotNull(utilisateur);
        if (utilisateur.getTypeDroit() == TypeDroit.ADMINISTRATEUR) {
            // si admin, on ajoute toutes les extractions de la base

            // SortedSet pour garder une liste "DISTINCT", sinon une ligne par extrait (OUTER JOIN avec Fetch=EAGER)
            SortedSet<Extraction> extractions = new TreeSet<Extraction>(dao.readList(Extraction.class));
            return new ArrayList<Extraction>(extractions);
        }
        // gestion des droits en plus pour les utilisateurs
        return new ArrayList<Extraction>(listExtractionsForUser(utilisateur));
    }

    /** {@inheritDoc} */
    @Override
    public SortedSet<Extraction> listExtractionsForUser(Utilisateur utilisateur) {
        AssertTools.assertNotNull(utilisateur);
        SortedSet<Extraction> extractions = new TreeSet<Extraction>();

        extractions.addAll(utilisateur.getExtractionsCrees());
        SortedSet<Lot> lotsAllowed = lotService.listLotsForUser(utilisateur);
        for (Lot l : lotsAllowed) {
            extractions.addAll(l.getExtractions());
        }
        return extractions;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isExtractionUnique(Extraction extraction) {
        AssertTools.assertNotNull(extraction);

        // unique if it doesn't exist different value or if it exists but with the same id (so same row in the db)
        if (!dao.exists(Extraction.class, "ref", extraction.getRef())) {
            return true;
        }

        Extraction extrWithSameVal;
        try {
            extrWithSameVal = dao.read(Extraction.class, "ref", extraction.getRef());
            dao.evict(extrWithSameVal);
        } catch (DataNotFoundException e) {
            return true; // never call, cover by dao.exists...
        }
        // in case of new record, id is null
        return extraction.getIdExtraction() != null
                && extraction.getIdExtraction().equals(extrWithSameVal.getIdExtraction());
    }

    /** {@inheritDoc} */
    @Override
    public Extraction loadExtraction(Integer idExtraction) throws DataNotFoundException {
        LOG.debug("read Extraction");
        return dao.read(Extraction.class, idExtraction);
    }

    /** {@inheritDoc} */
    @Override
    public Extraction loadExtraction(String ref) throws DataNotFoundException {
        return dao.read(Extraction.class, "ref", ref);
    }

    /** {@inheritDoc} */
    @Override
    public void updateExtraction(Extraction extraction) throws DataConstraintException {
        LOG.info("updateExtraction: " + extraction.getRef());
        try {
            dao.update(extraction);
        } catch (DataNotFoundException e) {
            LOG.error(e.getMessage(), e);
            throw new UnexpectedException(e);
        }
    }

    /** {@inheritDoc} */
    @Override
    public boolean updateOrdeleteExtractionEnabled(Extraction extraction, Utilisateur utilisateur) {
        return utilisateur.getTypeDroit() == TypeDroit.ADMINISTRATEUR
                || utilisateur.getIdPersonne() == extraction.getCreateur().getIdPersonne();
    }

    /** {@inheritDoc} */
    @Override
    public void createMethodeExtraction(MethodeExtraction methode) throws DataConstraintException {
        LOG.info("createMethodeExtraction: " + methode.getNom());
        dao.create(methode);
    }

    /** {@inheritDoc} */
    @Override
    public void deleteMethodeExtraction(MethodeExtraction methode) throws DataConstraintException {
        AssertTools.assertNotNull(methode);
        LOG.info("deleteMethodeExtraction: " + methode.getNom());
        try {
            dao.delete(methode);
        } catch (DataNotFoundException e) {
            LOG.error(e.getMessage(), e);
            throw new UnexpectedException(e);
        }

    }

    /** {@inheritDoc} */
    @Override
    public List<MethodeExtraction> listMethodesExtraction() {
        return dao.readList(MethodeExtraction.class, "nom");
    }

    /** {@inheritDoc} */
    @Override
    public MethodeExtraction loadMethodeExtraction(Integer idMethode) throws DataNotFoundException {
        return dao.read(MethodeExtraction.class, idMethode);
    }

    /** {@inheritDoc} */
    @Override
    public MethodeExtraction loadMethodeExtraction(String nom) throws DataNotFoundException {
        return dao.read(MethodeExtraction.class, "nom", nom);
    }

    /** {@inheritDoc} */
    @Override
    public void updateMethodeExtraction(MethodeExtraction methode) throws DataConstraintException {
        LOG.info("updateMethodeExtraction: " + methode.getNom());
        try {
            dao.update(methode);
        } catch (DataNotFoundException e) {
            LOG.error(e.getMessage(), e);
            throw new UnexpectedException(e);
        }

    }

    /** {@inheritDoc} */
    @Override
    public void refreshMethodeExtraction(MethodeExtraction methode) {
        AssertTools.assertNotNull(methode);
        dao.refresh(methode);
    }

    /** {@inheritDoc} */
    @Override
    public boolean isTypeExtraitReferenced(TypeExtrait typeExtrait) {
        AssertTools.assertNotNull(typeExtrait);
        AssertTools.assertNotNull(typeExtrait.getIdTypeExtrait());
        return dao.count(ExtractionDao.getCriteriaCountExtraitOfTypeExtrait(typeExtrait.getIdTypeExtrait())) > 0;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isExtraitUnique(Extrait extrait) {
        AssertTools.assertNotNull(extrait);

        // unique if it doesn't exist different value or if it exists with the same id (so same row in the db)
        if (!dao.exists(Extrait.class, "ref", extrait.getRef())) {
            return true;
        }
        Extrait extrWithSameVal;
        try {
            extrWithSameVal = dao.read(Extrait.class, "ref", extrait.getRef());
            dao.evict(extrWithSameVal);
        } catch (DataNotFoundException e) {
            return true; // never call, covers by dao.exists...
        }
        // in case of new record, id is null
        return extrait.getId() != null && extrait.getId().equals(extrWithSameVal.getId());
    }

}