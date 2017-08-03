/*
 * #%L
 * Cantharella :: Web
 * $Id: JSConfirmationBehavior.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.web/src/main/java/nc/ird/cantharella/web/utils/behaviors/JSConfirmationBehavior.java $
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
package nc.ird.cantharella.web.utils.behaviors;

import nc.ird.cantharella.utils.AssertTools;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.IModel;

/**
 * JavaScript confirmation behavior
 * 
 * @author Mickael Tricot
 * @author Adrien Cheype
 */
public final class JSConfirmationBehavior extends Behavior {

    /** JS event */
    private static final String JS_EVENT = "onclick";

    /** JS script template */
    private static final String JS_SCRIPT_TEMPLATE = "return confirm(\"%s\");";

    /** JS script */
    private final IModel<String> message;

    /**
     * Constructor
     * 
     * @param message Confirmation message
     */
    public JSConfirmationBehavior(IModel<String> message) {
        AssertTools.assertNotNull(message);
        this.message = message;
    }

    /** {@inheritDoc} */
    @Override
    public void onComponentTag(Component component, ComponentTag tag) {
        if (component instanceof Button || component instanceof Link<?>) {
            tag.getAttributes().remove(JS_EVENT);
            String jsScript = String.format(JS_SCRIPT_TEMPLATE, message.getObject());
            tag.getAttributes().put(JS_EVENT, jsScript);
        }
    }
}