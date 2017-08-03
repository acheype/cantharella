package nc.ird.cantharella.service.model;

/*
 * #%L
 * Cantharella :: Service
 * $Id: MoleculeProvenanceBean.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.service/src/main/java/nc/ird/cantharella/service/model/MoleculeProvenanceBean.java $
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

import java.util.List;

import nc.ird.cantharella.data.model.Document;
import nc.ird.cantharella.data.model.Extrait;
import nc.ird.cantharella.data.model.Fraction;
import nc.ird.cantharella.data.model.Lot;
import nc.ird.cantharella.data.model.Molecule;
import nc.ird.cantharella.data.model.MoleculeProvenance;
import nc.ird.cantharella.data.model.Produit;
import nc.ird.cantharella.data.model.utils.DocumentAttachable;
import nc.ird.cantharella.service.services.MoleculeService;

/**
 * Simple bean object used as result for {@link MoleculeService#listMoleculeProvenances}
 * 
 * @author poussin
 */
public class MoleculeProvenanceBean implements DocumentAttachable {

    /** Molecule. */
    protected Molecule molecule;

    /** Provenance, this field can be null if provenance is not readable by user. */
    protected MoleculeProvenance provenance;

    /**
     * Constructor with molecule and null provenance.
     * 
     * @param molecule molecule
     */
    public MoleculeProvenanceBean(Molecule molecule) {
        this.molecule = molecule;
    }

    /**
     * Constructor with provenance.
     * 
     * @param provenance
     */
    public MoleculeProvenanceBean(MoleculeProvenance provenance) {
        this(provenance.getMolecule());
        this.provenance = provenance;
    }

    /**
     * Molecule id getter.
     * 
     * @return molecule id
     */
    public Integer getIdMolecule() {
        return molecule.getIdMolecule();
    }

    /**
     * Molecule getter.
     * 
     * @return molecule
     */
    public Molecule getMolecule() {
        return molecule;
    }

    /**
     * Molecule provenance getter.
     * 
     * @return molecule provenance (can be null)
     */
    public MoleculeProvenance getMoleculeProvenance() {
        return provenance;
    }

    /**
     * Lot getter.
     * 
     * @return lot (can be null if provenance is null)
     */
    public Lot getLot() {
        Lot result = null;
        if (provenance != null) {
            Produit produit = provenance.getProduit();
            if (produit instanceof Fraction) {
                Fraction fraction = (Fraction) produit;
                result = fraction.getPurification().getLotSource();
            } else {
                Extrait extrait = (Extrait) produit;
                result = extrait.getExtraction().getLot();
            }
        }
        return result;
    }

    /** {@inheritDoc} */
    @Override
    public List<Document> getDocuments() {
        return molecule.getDocuments();
    }

    /** {@inheritDoc} */
    @Override
    public void addDocument(Document document) {
        molecule.addDocument(document);
    }

    /** {@inheritDoc} */
    @Override
    public void removeDocument(Document document) {
        molecule.removeDocument(document);
    }
}
