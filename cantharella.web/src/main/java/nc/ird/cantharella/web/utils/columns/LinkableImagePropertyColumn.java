/*
 * #%L
 * Cantharella :: Web
 * $Id: LinkableImagePropertyColumn.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.web/src/main/java/nc/ird/cantharella/web/utils/columns/LinkableImagePropertyColumn.java $
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
package nc.ird.cantharella.web.utils.columns;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.markup.html.image.ContextImage;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

/**
 * Column embeded in a datatable which represent an linkable image
 * 
 * @author Adrien Cheype
 * @param <T> Generic type
 * @param <S> the type of the sort property
 */
abstract public class LinkableImagePropertyColumn<T, S> extends AbstractColumn<T, S> {

    /** Relative url for the image */
    private String imageSrc;

    /** Title displayed for the link */
    private IModel<String> linkTitle;

    /** Alternative message displayed when image can't be rendered */
    private IModel<String> altMessage;

    /**
     * Constructor
     * 
     * @param imageSrc Relative url for the image
     * @param linkTitle Title displayed for the link
     * @param altMessage Alternative message displayed when image can't be rendered
     */
    public LinkableImagePropertyColumn(String imageSrc, IModel<String> linkTitle, IModel<String> altMessage) {
        this(new Model<String>(), imageSrc, linkTitle, altMessage);
    }

    /**
     * Constructor
     * 
     * @param displayModel header display model
     * @param imageSrc Relative url for the image
     * @param linkTitle Title displayed for the link
     * @param altMessage Alternative message displayed when image can't be rendered
     */
    public LinkableImagePropertyColumn(IModel<String> displayModel, String imageSrc, IModel<String> linkTitle,
            IModel<String> altMessage) {
        super(displayModel);
        this.imageSrc = imageSrc;
        this.linkTitle = linkTitle;
        this.altMessage = altMessage;
    }

    /** {@inheritDoc} */
    @Override
    public void populateItem(Item<ICellPopulator<T>> item, String componentId, IModel<T> model) {
        LinkableImagePanel panel = new LinkableImagePanel(item, componentId, model);
        item.add(panel);
    }

    /**
     * Override this method to react to link clicks. Your own/internal row id will most likely be inside the model.
     * 
     * @param item Item
     * @param componentId Component id
     * @param model Model
     */
    public abstract void onClick(Item<ICellPopulator<T>> item, String componentId, IModel<T> model);

    /**
     * Panel which include a linkable image. Used with the LinkableImagePropertyColumn$LinkablePanel.html file
     */
    public class LinkableImagePanel extends Panel {

        /**
         * Constructor
         * 
         * @param item Item
         * @param componentId Component id
         * @param model Model
         */
        public LinkableImagePanel(final Item<ICellPopulator<T>> item, final String componentId, final IModel<T> model) {
            super(componentId);

            Link<T> link = new Link<T>("link") {

                @Override
                public void onClick() {
                    LinkableImagePropertyColumn.this.onClick(item, componentId, model);
                }
            };
            if (linkTitle != null) {
                link.add(new AttributeModifier("title", linkTitle));
            }
            ContextImage image = new ContextImage("image", imageSrc);
            if (altMessage != null) {
                image.add(new AttributeModifier("alt", altMessage));
            }
            link.add(image);

            add(link);
        }
    }
}