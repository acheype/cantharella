/*
 * #%L
 * Cantharella :: Web
 * $Id: ManageUtilisateurModel.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.web/src/main/java/nc/ird/cantharella/web/pages/model/ManageUtilisateurModel.java $
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
package nc.ird.cantharella.web.pages.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nc.ird.cantharella.data.model.Campagne;
import nc.ird.cantharella.data.model.CampagnePersonneDroits;
import nc.ird.cantharella.data.model.Lot;
import nc.ird.cantharella.data.model.LotPersonneDroits;

/**
 * Modèle : gestion d'un compte utilisateur (champs supplémentaires)
 * 
 * @author Mickael Tricot
 */
public final class ManageUtilisateurModel implements Serializable {

    /** Campagnes pour lesquelles l'utilisateur a des droits */
    private final List<Campagne> campagnes = new ArrayList<Campagne>();

    /** Droits sur les campagnes pour l'utilisateur */
    private Map<Campagne, CampagnePersonneDroits> campagnesDroits;

    /** Lots pour lesquels l'utilisateur a des droits */
    private final List<Lot> lots = new ArrayList<Lot>();

    /** Droits sur les lots pour l'utilisateur */
    private Map<Lot, LotPersonneDroits> lotsDroits;

    /**
     * campagnesDroits getter
     * 
     * @return campagnesDroits
     */
    public Map<Campagne, CampagnePersonneDroits> getCampagnesDroits() {
        return campagnesDroits;
    }

    /**
     * campagnesDroits setter
     * 
     * @param campagnesDroits campagnesDroits
     */
    public void setCampagnesDroits(Map<Campagne, CampagnePersonneDroits> campagnesDroits) {
        this.campagnesDroits = campagnesDroits;
    }

    /**
     * lotsDroits getter
     * 
     * @return lotsDroits
     */
    public Map<Lot, LotPersonneDroits> getLotsDroits() {
        return lotsDroits;
    }

    /**
     * lotsDroits setter
     * 
     * @param lotsDroits lotsDroits
     */
    public void setLotsDroits(Map<Lot, LotPersonneDroits> lotsDroits) {
        this.lotsDroits = lotsDroits;
    }

    /**
     * campagnes getter
     * 
     * @return campagnes
     */
    public List<Campagne> getCampagnes() {
        return campagnes;
    }

    /**
     * lots getter
     * 
     * @return lots
     */
    public List<Lot> getLots() {
        return lots;
    }

}