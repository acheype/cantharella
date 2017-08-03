/*
 * #%L
 * Cantharella :: Web
 * $Id: CollapsiblePanel.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.web/src/main/java/nc/ird/cantharella/web/utils/panels/CollapsiblePanel.java $
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

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.request.resource.ResourceReference;

/**
 * Wicket panel that can be opened and closed. Source : https://gist.github.com/594489
 * 
 * @author Jonny Wray, adapted by Adrien Cheype
 */
public abstract class CollapsiblePanel extends Panel {

    /** Reference for the expand image */
    private PackageResourceReference closed = new PackageResourceReference(CollapsiblePanel.class, "expand.png");

    /** Reference for the collapse image */
    private PackageResourceReference open = new PackageResourceReference(CollapsiblePanel.class, "collapse.png");

    /** Does the panel is visible ? */
    private boolean visible = false;

    /** The panel which is showed/hid */
    protected Panel innerPanel;

    /**
     * Construct the panel
     * 
     * @param id Panel ID
     * @param titleModel Model used to get the panel title
     * @param defaultOpen Is the default state open
     */
    public CollapsiblePanel(String id, IModel<String> titleModel, boolean defaultOpen) {
        super(id);
        this.visible = defaultOpen;
        innerPanel = getInnerPanel("innerPanel");
        innerPanel.setVisible(visible);
        innerPanel.setOutputMarkupId(true);
        innerPanel.setOutputMarkupPlaceholderTag(true);
        add(innerPanel);

        final Image showHideImage = new Image("showHideIcon") {
            private static final long serialVersionUID = 8638737301579767296L;

            @Override
            public ResourceReference getImageResourceReference() {
                return visible ? open : closed;
            }
        };
        showHideImage.setOutputMarkupId(true);
        IndicatingAjaxLink<Void> showHideLink = new IndicatingAjaxLink<Void>("showHideLink") {
            private static final long serialVersionUID = -1929927616508773911L;

            @Override
            public void onClick(AjaxRequestTarget target) {
                visible = !visible;
                innerPanel.setVisible(visible);
                target.add(innerPanel);
                target.add(showHideImage);
            }
        };
        showHideLink.add(showHideImage);
        add(new Label("titlePanel", titleModel));
        add(showHideLink);
    }

    /**
     * Construct the container contained within the collapsible panel
     * 
     * @param markupId ID that should be used for the inner panel
     * @return The inner container displayed when collapsible is open
     */
    protected abstract Panel getInnerPanel(String markupId);

}