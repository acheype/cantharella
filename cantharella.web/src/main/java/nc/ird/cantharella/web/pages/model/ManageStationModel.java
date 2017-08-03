/*
 * #%L
 * Cantharella :: Web
 * $Id: ManageStationModel.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.web/src/main/java/nc/ird/cantharella/web/pages/model/ManageStationModel.java $
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
import java.math.BigDecimal;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;

import nc.ird.cantharella.data.validation.LatitudeOrientation;
import nc.ird.cantharella.data.validation.LongitudeOrientation;
import nc.ird.cantharella.utils.CoordTools;

import org.hibernate.validator.constraints.Range;

/**
 * Modèle : gestion d'une station (champs supplémentaires)
 * 
 * @author Mickael Tricot
 */
public final class ManageStationModel implements Serializable {

    /** Latitude : degrés */
    @Range(min = CoordTools.LATITUDE_MIN_DEGREES, max = CoordTools.LATITUDE_MAX_DEGREES)
    private Integer latitudeDegrees;

    /** Latitude : minutes */
    @DecimalMin(CoordTools.LATITUDE_MIN_MINUTES_STRING)
    @DecimalMax(CoordTools.LATITUDE_MAX_MINUTES_STRING)
    private BigDecimal latitudeMinutes;

    /** Latitude : orientation */
    @LatitudeOrientation
    private Character latitudeOrientation;

    /** Longitude : degrés */
    @Range(min = CoordTools.LONGITUDE_MIN_DEGREES, max = CoordTools.LONGITUDE_MAX_DEGREES)
    private Integer longitudeDegrees;

    /** Longitude : minutes */
    @DecimalMin(CoordTools.LONGITUDE_MIN_MINUTES_STRING)
    @DecimalMax(CoordTools.LONGITUDE_MAX_MINUTES_STRING)
    private BigDecimal longitudeMinutes;

    /** Longitude : orientation */
    @LongitudeOrientation
    private Character longitudeOrientation;

    /**
     * latitudeDegrees getter
     * 
     * @return latitudeDegrees
     */
    public Integer getLatitudeDegrees() {
        return latitudeDegrees;
    }

    /**
     * latitudeDegrees setter
     * 
     * @param latitudeDegrees latitudeDegrees
     */
    public void setLatitudeDegrees(Integer latitudeDegrees) {
        this.latitudeDegrees = latitudeDegrees;
    }

    /**
     * latitudeMinutes getter
     * 
     * @return latitudeMinutes
     */
    public BigDecimal getLatitudeMinutes() {
        return latitudeMinutes;
    }

    /**
     * latitudeMinutes setter
     * 
     * @param latitudeMinutes latitudeMinutes
     */
    public void setLatitudeMinutes(BigDecimal latitudeMinutes) {
        this.latitudeMinutes = latitudeMinutes;
    }

    /**
     * latitudeOrientation getter
     * 
     * @return latitudeOrientation
     */
    public Character getLatitudeOrientation() {
        return latitudeOrientation;
    }

    /**
     * latitudeOrientation setter
     * 
     * @param latitudeOrientation latitudeOrientation
     */
    public void setLatitudeOrientation(Character latitudeOrientation) {
        this.latitudeOrientation = latitudeOrientation;
    }

    /**
     * longitudeDegrees getter
     * 
     * @return longitudeDegrees
     */
    public Integer getLongitudeDegrees() {
        return longitudeDegrees;
    }

    /**
     * longitudeDegrees setter
     * 
     * @param longitudeDegrees longitudeDegrees
     */
    public void setLongitudeDegrees(Integer longitudeDegrees) {
        this.longitudeDegrees = longitudeDegrees;
    }

    /**
     * longitudeMinutes getter
     * 
     * @return longitudeMinutes
     */
    public BigDecimal getLongitudeMinutes() {
        return longitudeMinutes;
    }

    /**
     * longitudeMinutes setter
     * 
     * @param longitudeMinutes longitudeMinutes
     */
    public void setLongitudeMinutes(BigDecimal longitudeMinutes) {
        this.longitudeMinutes = longitudeMinutes;
    }

    /**
     * longitudeOrientation getter
     * 
     * @return longitudeOrientation
     */
    public Character getLongitudeOrientation() {
        return longitudeOrientation;
    }

    /**
     * longitudeOrientation setter
     * 
     * @param longitudeOrientation longitudeOrientation
     */
    public void setLongitudeOrientation(Character longitudeOrientation) {
        this.longitudeOrientation = longitudeOrientation;
    }

}
