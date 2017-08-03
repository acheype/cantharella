/*
 * #%L
 * Cantharella :: Web
 * $Id: SimpleTooltipPanel.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.web/src/main/java/nc/ird/cantharella/web/utils/panels/SimpleTooltipPanel.java $
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
import org.apache.wicket.markup.html.WebComponent;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.resource.PackageResourceReference;

/**
 * Panel which display a image (a question mark) with an associated message (display by the navigator on "on over"
 * event)
 * 
 * @author Adrien Cheype
 */
public class SimpleTooltipPanel extends Panel {

    /**
     * Constructor
     * 
     * @param id Component id
     * @param messageModel Message Model displayed over the image
     */
    public SimpleTooltipPanel(String id, IModel<?> messageModel) {
        super(id);
        WebComponent img = new Image("tooltip", new PackageResourceReference(SimpleTooltipPanel.class, "tooltip.png"));
        img.add(new AttributeModifier("title", messageModel));
        add(img);
    }
}
