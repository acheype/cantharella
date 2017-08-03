/*
 * #%L
 * Cantharella :: Web
 * $Id: SubmittableButtonEvents.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.web/src/main/java/nc/ird/cantharella/web/utils/forms/SubmittableButtonEvents.java $
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
package nc.ird.cantharella.web.utils.forms;

import java.io.Serializable;

import nc.ird.cantharella.data.exceptions.AbstractException;

/**
 * Submittable form component actions
 * 
 * @author Mickael Tricot
 */
public abstract class SubmittableButtonEvents implements Serializable {

    /**
     * (3) Action to perform on process error. For example, reset some form fields.
     */
    public void onError() {
        // Nothing to do (by default)
    }

    /**
     * (2) Process the form goal. For example, call the related service method.
     * 
     * @throws AbstractException if an error occured
     */
    public abstract void onProcess() throws AbstractException;

    /**
     * (3) Action to perform on process success. For example, redirect and display a success message.
     */
    public abstract void onSuccess();

    /**
     * (1) Action to perform on validate. For example, validate the form model.
     */
    public void onValidate() {
        // Nothing to do (by default)
    }
}
