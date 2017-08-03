/*
 * #%L
 * Cantharella :: Web
 * $Id: PropertyLabelLinkPanel.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.web/src/main/java/nc/ird/cantharella/web/utils/panels/PropertyLabelLinkPanel.java $
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
package nc.ird.cantharella.web.utils.panels;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

/**
 * Panel which display a link with inside the corresponding value of the model
 * 
 * @author Adrien Cheype
 * @param <T> Generic class of the model
 **/
abstract public class PropertyLabelLinkPanel<T> extends Panel {

    /** generated link */
    private Link<T> link;

    /**
     * Constructor
     * 
     * @param id panel id
     * @param linkModel model used to generate the link
     */
    public PropertyLabelLinkPanel(String id, IModel<T> linkModel) {
        super(id, linkModel);
        link = new Link<T>("link") {

            @Override
            public void onClick() {
                PropertyLabelLinkPanel.this.onClick();
            }
        };
        add(link);

        link.add(new Label("label", linkModel));
    }

    /**
     * Constructor
     * 
     * @param id panel id
     * @param linkModel model used to generate the link
     * @param linkTitle title displayed for the link
     */
    public PropertyLabelLinkPanel(String id, IModel<T> linkModel, IModel<String> linkTitle) {
        this(id, linkModel);

        if (linkTitle != null) {
            link.add(new AttributeModifier("title", linkTitle));
        }
    }

    /**
     * Get the model
     * 
     * @return model
     */
    @SuppressWarnings("unchecked")
    public IModel<T> getModel() {
        return (IModel<T>) getDefaultModel();
    }

    /**
     * Get the model object
     * 
     * @return model object
     */
    @SuppressWarnings("unchecked")
    public T getModelObject() {
        return (T) getDefaultModelObject();
    }

    /**
     * Override this method to react to link clicks.
     */
    public abstract void onClick();

}
