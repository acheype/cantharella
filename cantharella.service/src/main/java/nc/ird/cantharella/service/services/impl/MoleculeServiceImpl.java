/*
 * #%L
 * Cantharella :: Service
 * $Id: MoleculeServiceImpl.java 125 2013-02-18 10:11:43Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.service/src/main/java/nc/ird/cantharella/service/services/impl/MoleculeServiceImpl.java $
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
package nc.ird.cantharella.service.services.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import nc.ird.cantharella.data.dao.GenericDao;
import nc.ird.cantharella.data.dao.impl.MoleculeDao;
import nc.ird.cantharella.data.dao.impl.PersonneDao;
import nc.ird.cantharella.data.exceptions.DataConstraintException;
import nc.ird.cantharella.data.exceptions.DataNotFoundException;
import nc.ird.cantharella.data.exceptions.UnexpectedException;
import nc.ird.cantharella.data.model.Extrait;
import nc.ird.cantharella.data.model.Fraction;
import nc.ird.cantharella.data.model.Lot;
import nc.ird.cantharella.data.model.Molecule;
import nc.ird.cantharella.data.model.MoleculeProvenance;
import nc.ird.cantharella.data.model.Produit;
import nc.ird.cantharella.data.model.Utilisateur;
import nc.ird.cantharella.data.model.Utilisateur.TypeDroit;
import nc.ird.cantharella.service.model.MoleculeProvenanceBean;
import nc.ird.cantharella.service.services.LotService;
import nc.ird.cantharella.service.services.MoleculeService;
import nc.ird.cantharella.utils.AssertTools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Implémentation du service molecule.
 * 
 * @author Eric Chatellier
 */
@Service
public final class MoleculeServiceImpl implements MoleculeService {

    /** Logger */
    private static final Logger LOG = LoggerFactory.getLogger(MoleculeServiceImpl.class);

    /** DAO */
    @Autowired
    private GenericDao dao;

    /** Lot service. */
    @Autowired
    private LotService lotService;

    /** {@inheritDoc} */
    @Override
    public long countMolecules() {
        return dao.count(Molecule.class);
    }

    /** {@inheritDoc} */
    @Override
    public void createMolecule(Molecule molecule) throws DataConstraintException {
        LOG.info("createMolecule " + molecule.getFormuleBrute());
        dao.create(molecule);
    }

    /** {@inheritDoc} */
    @Override
    public void deleteMolecule(Molecule molecule) throws DataConstraintException {
        AssertTools.assertNotNull(molecule);
        LOG.info("deleteMolecule " + molecule.getIdMolecule());
        try {
            dao.delete(molecule);
        } catch (DataNotFoundException e) {
            LOG.error(e.getMessage(), e);
            throw new UnexpectedException(e);
        }
    }

    /** {@inheritDoc} */
    @Override
    public List<Molecule> listMolecules() {
        return dao.readList(Molecule.class, "idMolecule");
    }

    /** {@inheritDoc} */
    @Override
    public List<MoleculeProvenanceBean> listMoleculeProvenances(Utilisateur utilisateur) {
        List<Molecule> molecules = listMolecules();
        List<MoleculeProvenanceBean> result = listMoleculeProvenances(molecules, utilisateur);
        return result;
    }

    /** {@inheritDoc} */
    @Override
    public List<MoleculeProvenanceBean> listMoleculeProvenances(List<Molecule> molecules, Utilisateur utilisateur) {
        List<MoleculeProvenanceBean> result = new ArrayList<MoleculeProvenanceBean>();

        for (Molecule molecule : molecules) {
            boolean isOneProductVisible = false;

            List<MoleculeProvenance> moleculeProvenances = molecule.getProvenances();
            for (MoleculeProvenance moleculeProvenance : moleculeProvenances) {
                if (isMoleculeProvenanceAccessibleByUser(moleculeProvenance, utilisateur)) {
                    result.add(new MoleculeProvenanceBean(moleculeProvenance));
                    isOneProductVisible = true;
                }
            }

            if (!isOneProductVisible) {
                result.add(new MoleculeProvenanceBean(molecule));
            }
        }

        return result;
    }

    /** {@inheritDoc} */
    @Override
    public Molecule loadMolecule(Integer numero) throws DataNotFoundException {
        return dao.read(Molecule.class, numero);
    }

    /** {@inheritDoc} */
    @Override
    public void updateMolecule(Molecule molecule) throws DataConstraintException {
        LOG.info("updateMolecule: " + molecule.getIdMolecule());
        try {
            dao.update(molecule);
        } catch (DataNotFoundException e) {
            LOG.error(e.getMessage(), e);
            throw new UnexpectedException(e);
        }
    }

    /** {@inheritDoc} */
    @Override
    public boolean updateOrdeleteMoleculeEnabled(Molecule molecule, Utilisateur utilisateur) {
        return utilisateur.getTypeDroit() == TypeDroit.ADMINISTRATEUR
                || utilisateur.getIdPersonne() == molecule.getCreateur().getIdPersonne();
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    @Override
    public List<String> listMoleculeOrganisme() {
        Set<String> organismes = new HashSet<String>();
        organismes.addAll((List<String>) dao.list(MoleculeDao.CRITERIA_DISTINCT_MOLECULE_ORGANISMES));
        organismes.addAll((List<String>) dao.list(PersonneDao.CRITERIA_DISTINCT_PERSONNE_ORGANISMES));
        List<String> result = new ArrayList<String>(organismes);
        result.remove(null); // fix NPE on sort
        Collections.sort(result);
        return result;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isMoleculeProvenanceAccessibleByUser(MoleculeProvenance moleculeProvenance, Utilisateur utilisateur) {
        Lot lot;
        Produit produit = moleculeProvenance.getProduit();
        if (produit instanceof Extrait) {
            lot = ((Extrait) produit).getExtraction().getLot();
        } else {
            lot = ((Fraction) produit).getPurification().getLotSource();
        }
        return lotService.isLotAccessibleByUser(lot, utilisateur);
    }
}
