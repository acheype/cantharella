/*
 * #%L
 * Cantharella :: Web
 * $Id: CaptchaModel.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.web/src/main/java/nc/ird/cantharella/web/pages/model/CaptchaModel.java $
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

import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Modèle (champs supplémentaires) pour le captcha
 * 
 * @author Mickael Tricot
 */
public final class CaptchaModel implements Serializable {

    /** Texte captcha */
    @NotEmpty
    private String captchaText;

    /** Texte généré pour le captcha */
    private String captchaTextGenerated;

    /**
     * Validate the model
     * 
     * @return TRUE if it is valid
     */
    public boolean validate() {
        return !StringUtils.isEmpty(captchaText) && captchaTextGenerated.equals(captchaText);
    }

    /**
     * captchaText getter
     * 
     * @return captchaText
     */
    public String getCaptchaText() {
        return captchaText;
    }

    /**
     * captchaText setter
     * 
     * @param captchaText captchaText
     */
    public void setCaptchaText(String captchaText) {
        this.captchaText = captchaText;
    }

    /**
     * captchaTextGenerated getter
     * 
     * @return captchaTextGenerated
     */
    public String getCaptchaTextGenerated() {
        return captchaTextGenerated;
    }

    /**
     * captchaTextGenerated setter
     * 
     * @param captchaTextGenerated captchaTextGenerated
     */
    public void setCaptchaTextGenerated(String captchaTextGenerated) {
        this.captchaTextGenerated = captchaTextGenerated;
    }

}