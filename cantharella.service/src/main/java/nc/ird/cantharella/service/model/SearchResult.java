/*
 * #%L
 * Cantharella :: Service
 * $Id: SearchResult.java 152 2013-02-22 10:42:23Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.service/src/main/java/nc/ird/cantharella/service/model/SearchResult.java $
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
package nc.ird.cantharella.service.model;

import java.io.Serializable;
import java.util.List;

import nc.ird.cantharella.data.model.Extraction;
import nc.ird.cantharella.data.model.Lot;
import nc.ird.cantharella.data.model.Molecule;
import nc.ird.cantharella.data.model.Purification;
import nc.ird.cantharella.data.model.ResultatTestBio;
import nc.ird.cantharella.data.model.Specimen;
import nc.ird.cantharella.data.model.Station;

/**
 * Search result containing somes entities collections of various types.
 * 
 * @author Eric Chatellier
 */
public class SearchResult implements Serializable {

    /** Search result specimens. */
    protected List<Specimen> specimens;

    /** Search result lots. */
    protected List<Lot> lots;

    /** Search result extractions. */
    protected List<Extraction> extractions;

    /** Search result purification. */
    protected List<Purification> purifications;

    /** Search result resultatTestBios. */
    protected List<ResultatTestBio> resultatTestBios;

    /** Search result stations. */
    protected List<Station> stations;

    /** Search result molecules. */
    protected List<Molecule> molecules;

    /**
     * Specimens getter.
     * 
     * @return specimes
     */
    public List<Specimen> getSpecimens() {
        return specimens;
    }

    /**
     * Specimens setter.
     * 
     * @param specimens specimens
     */
    public void setSpecimens(List<Specimen> specimens) {
        this.specimens = specimens;
    }

    /**
     * Lots getter.
     * 
     * @return lots
     */
    public List<Lot> getLots() {
        return lots;
    }

    /**
     * Lots setter.
     * 
     * @param lots lots
     */
    public void setLots(List<Lot> lots) {
        this.lots = lots;
    }

    /**
     * Extraction getter.
     * 
     * @return extractions
     */
    public List<Extraction> getExtractions() {
        return extractions;
    }

    /**
     * Extraction setter.
     * 
     * @param extractions extraction
     */
    public void setExtractions(List<Extraction> extractions) {
        this.extractions = extractions;
    }

    /**
     * Purifcations getter.
     * 
     * @return purification
     */
    public List<Purification> getPurifications() {
        return purifications;
    }

    /**
     * Purification setter.
     * 
     * @param purifications purification
     */
    public void setPurifications(List<Purification> purifications) {
        this.purifications = purifications;
    }

    /**
     * ResultatTestBios getter.
     * 
     * @return resultattestbios
     */
    public List<ResultatTestBio> getResultatTestBios() {
        return resultatTestBios;
    }

    /**
     * ResultatTestBios setter.
     * 
     * @param resultatTestBios resultatTestBios
     */
    public void setResultatTestBios(List<ResultatTestBio> resultatTestBios) {
        this.resultatTestBios = resultatTestBios;
    }

    /**
     * Stations getter.
     * 
     * @return stations
     */
    public List<Station> getStations() {
        return stations;
    }

    /**
     * Stations setter.
     * 
     * @param stations stations
     */
    public void setStations(List<Station> stations) {
        this.stations = stations;
    }

    /**
     * Molecule getter.
     * 
     * @return molecule
     */
    public List<Molecule> getMolecules() {
        return molecules;
    }

    /**
     * Molecule setter.
     * 
     * @param molecules molecules
     */
    public void setMolecules(List<Molecule> molecules) {
        this.molecules = molecules;
    }
}
