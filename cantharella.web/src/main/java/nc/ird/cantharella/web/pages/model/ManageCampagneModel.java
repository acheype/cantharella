/*
 * #%L
 * Cantharella :: Web
 * $Id: ManageCampagneModel.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.web/src/main/java/nc/ird/cantharella/web/pages/model/ManageCampagneModel.java $
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

import nc.ird.cantharella.data.model.Personne;

/**
 * Modèle : gestion d'une campagne (champs supplémentaires)
 * 
 * @author Mickael Tricot
 */
public final class ManageCampagneModel implements Serializable {

    /** complement associé à la personne sélectionnée */
    private String complement;

    /** Personne sélectionnée */
    private Personne selectedPersonne;

    /**
     * complement getter
     * 
     * @return complement
     */
    public String getComplement() {
        return complement;
    }

    /**
     * complement setter
     * 
     * @param complement complement
     */
    public void setComplement(String complement) {
        this.complement = complement;
    }

    /**
     * selectedPersonne getter
     * 
     * @return selectedPersonne
     */
    public Personne getSelectedPersonne() {
        return selectedPersonne;
    }

    /**
     * selectedPersonne setter
     * 
     * @param selectedPersonne selectedPersonne
     */
    public void setSelectedPersonne(Personne selectedPersonne) {
        this.selectedPersonne = selectedPersonne;
    }

}