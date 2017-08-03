/*
 * #%L
 * Cantharella :: Service
 * $Id: MoleculeService.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.service/src/main/java/nc/ird/cantharella/service/services/MoleculeService.java $
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

import java.util.List;

import nc.ird.cantharella.data.exceptions.DataConstraintException;
import nc.ird.cantharella.data.exceptions.DataNotFoundException;
import nc.ird.cantharella.data.model.Molecule;
import nc.ird.cantharella.data.model.MoleculeProvenance;
import nc.ird.cantharella.data.model.Utilisateur;
import nc.ird.cantharella.service.model.MoleculeProvenanceBean;
import nc.ird.cantharella.service.utils.normalizers.MoleculeNormalizer;
import nc.ird.cantharella.service.utils.normalizers.utils.Normalize;

import org.springframework.transaction.annotation.Transactional;

/**
 * Service : molecules.
 * 
 * @author Eric Chatellier
 */
public interface MoleculeService {

    /**
     * Compte le nombre de molecules
     * 
     * @return Nombre de Molecules
     */
    @Transactional(readOnly = true)
    long countMolecules();

    /**
     * Créée une molecule
     * 
     * @param molecule Molecule
     * @throws DataConstraintException Si la molecule existe déjà
     */
    void createMolecule(@Normalize(MoleculeNormalizer.class) Molecule molecule) throws DataConstraintException;

    /**
     * Supprime une molecule
     * 
     * @param molecule Molecule
     * @throws DataConstraintException Si la molecule a des données liées
     */
    void deleteMolecule(Molecule molecule) throws DataConstraintException;

    /**
     * List molecule provenance and molecule without provenance or provenance not visible by current user.
     * 
     * @return Molecules
     */
    @Transactional(readOnly = true)
    List<Molecule> listMolecules();

    /**
     * List molecule provenance and molecule without provenance or provenance not visible by current user.
     * 
     * @param utilisateur utilisateur to filter results
     * @return Molecules
     */
    @Transactional(readOnly = true)
    List<MoleculeProvenanceBean> listMoleculeProvenances(Utilisateur utilisateur);

    /**
     * List molecule provenance and molecule without provenance or provenance not visible by current user.
     * 
     * @param molecules molecules list to transform
     * @param utilisateur utilisateur to filter results
     * @return Molecules
     */
    List<MoleculeProvenanceBean> listMoleculeProvenances(List<Molecule> molecules, Utilisateur utilisateur);

    /**
     * Charge une molecule
     * 
     * @param numero numero
     * @return Le lot correspondant
     * @throws DataNotFoundException Si le lot n'existe pas
     */
    @Transactional(readOnly = true)
    Molecule loadMolecule(Integer numero) throws DataNotFoundException;

    /**
     * Met à jour une molecule
     * 
     * @param molecule Molecule
     * @throws DataConstraintException Si la molecule existe déjà
     */
    void updateMolecule(@Normalize(MoleculeNormalizer.class) Molecule molecule) throws DataConstraintException;

    /**
     * Détermine si un utilisateur peut modifier ou supprimer une molecule.
     * 
     * @param molecule la molecule
     * @param utilisateur L'utilisateur
     * @return TRUE s'il a le droit
     */
    boolean updateOrdeleteMoleculeEnabled(Molecule molecule, Utilisateur utilisateur);

    /**
     * Liste les programmes des organismes déjà saisis
     * 
     * @return Organisme des molécules
     */
    @Transactional(readOnly = true)
    List<String> listMoleculeOrganisme();

    /**
     * Détermine si un utilisateur peut accéder à une provenance de molecule.
     * 
     * @param moleculeProvenance provenance
     * @param utilisateur L'utilisateur
     * @return TRUE s'il a le droit
     */
    @Transactional(readOnly = true)
    boolean isMoleculeProvenanceAccessibleByUser(MoleculeProvenance moleculeProvenance, Utilisateur utilisateur);
}
