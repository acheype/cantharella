/*
 * #%L
 * Cantharella :: Web
 * $Id: SubmittableButton.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.web/src/main/java/nc/ird/cantharella/web/utils/forms/SubmittableButton.java $
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

import nc.ird.cantharella.data.exceptions.UnexpectedException;
import nc.ird.cantharella.web.pages.TemplatePage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.model.IModel;

/**
 * Submittable form button, which overrides the default form behavior
 * 
 * @author Mickael Tricot
 */
public final class SubmittableButton extends Button {

    /** Logger */
    private static final Logger LOG = LoggerFactory.getLogger(SubmittableButton.class);

    /** Submittable */
    private final SubmittableButtonEvents submittable;

    /** Specific page which provide error messages, null if none */
    private Class<? extends TemplatePage> specificMsgPage = null;

    /**
     * Constructor
     * 
     * @param id ID
     * @param submittable Submittable
     * @param specificMsgPage Page wich provide error messages
     */
    public SubmittableButton(String id, Class<? extends TemplatePage> specificMsgPage,
            SubmittableButtonEvents submittable) {
        super(id);
        this.submittable = submittable;
        this.specificMsgPage = specificMsgPage;
    }

    /**
     * Constructor
     * 
     * @param id ID
     * @param model Model
     * @param submittable Submittable
     */
    public SubmittableButton(String id, IModel<String> model, SubmittableButtonEvents submittable) {
        super(id, model);
        this.submittable = submittable;
    }

    /**
     * Constructor
     * 
     * @param id ID
     * @param submittable Submittable
     */
    public SubmittableButton(String id, SubmittableButtonEvents submittable) {
        super(id);
        this.submittable = submittable;
    }

    /** {@inheritDoc} */
    @Override
    public void onSubmit() {
        submittable.onValidate();

        if (!getPage().hasErrorMessage()) {
            try {
                submittable.onProcess();
            } catch (UnexpectedException e) {
                throw e;
            } catch (RuntimeException e) {
                LOG.error(e.getMessage(), e);
                throw e;
            } catch (Exception e) {
                if (specificMsgPage == null) {
                    ((TemplatePage) getPage()).errorCurrentPage(getId(), e);
                } else {
                    ((TemplatePage) getPage()).errorCurrentPage(specificMsgPage, getId(), e);
                }
            }
        }
        if (getPage().hasErrorMessage()) {
            submittable.onError();
        } else {
            submittable.onSuccess();
        }
    }
}