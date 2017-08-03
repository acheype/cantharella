/*
 * #%L
 * Cantharella :: Data
 * $Id: ParamMethoPuriEffectif.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.data/src/main/java/nc/ird/cantharella/data/model/ParamMethoPuriEffectif.java $
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

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import nc.ird.cantharella.data.model.utils.AbstractModel;

import org.hibernate.validator.constraints.Length;

/**
 * Modèle : Paramètre effectif qui décrit la méthode de purification employée pour une purification
 * 
 * @author Adrien Cheype
 */
@Entity
@Table
public class ParamMethoPuriEffectif extends AbstractModel implements Cloneable {

    /** Id du paramètre effectif */
    @Id
    @GeneratedValue
    private Integer idParamMethoPuriEffectif;

    /** Paramètre */
    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    private ParamMethoPuri param;

    /** Valeur du paramètre */
    @Length(max = LENGTH_LONG_TEXT)
    private String valeur;

    /** Purification à laquelle se rattache le paramètre effectif */
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Purification purification;

    /**
     * Constructor
     */
    public ParamMethoPuriEffectif() {
        //
    }

    /** {@inheritDoc} */
    @Override
    public ParamMethoPuriEffectif clone() throws CloneNotSupportedException {
        ParamMethoPuriEffectif clone = (ParamMethoPuriEffectif) super.clone();
        clone.idParamMethoPuriEffectif = idParamMethoPuriEffectif;
        clone.param = param;
        clone.valeur = valeur;
        return clone;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return param + " : " + valeur;
    }

    /**
     * idParamMethoPuriEffectif getter
     * 
     * @return idParamMethoPuriEffectif
     */
    public Integer getIdParamMethoPuriEffectif() {
        return idParamMethoPuriEffectif;
    }

    /**
     * idParamMethoPuriEffectif setter
     * 
     * @param idParamMethoPuriEffectif idParamMethoPuriEffectif
     */
    public void setIdParamMethoPuriEffectif(Integer idParamMethoPuriEffectif) {
        this.idParamMethoPuriEffectif = idParamMethoPuriEffectif;
    }

    /**
     * param getter
     * 
     * @return param
     */
    public ParamMethoPuri getParam() {
        return param;
    }

    /**
     * param setter
     * 
     * @param param param
     */
    public void setParam(ParamMethoPuri param) {
        this.param = param;
    }

    /**
     * valeur getter
     * 
     * @return valeur
     */
    public String getValeur() {
        return valeur;
    }

    /**
     * valeur setter
     * 
     * @param valeur valeur
     */
    public void setValeur(String valeur) {
        this.valeur = valeur;
    }

    /**
     * purification getter
     * 
     * @return purification
     */
    public Purification getPurification() {
        return purification;
    }

    /**
     * purification setter
     * 
     * @param purification purification
     */
    public void setPurification(Purification purification) {
        this.purification = purification;
    }

}