/*
 * #%L
 * Cantharella :: Web
 * $Id: DocumentTooltipColumn.java 268 2014-05-06 15:45:40Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.web/src/main/java/nc/ird/cantharella/web/pages/domain/document/DocumentTooltipColumn.java $
 * %%
 * Copyright (C) 2009 - 2013 IRD (Institut de Recherche pour le Developpement) and by respective authors (see below)
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
package nc.ird.cantharella.web.pages.domain.document;

import java.util.Iterator;
import java.util.List;

import nc.ird.cantharella.data.model.Document;
import nc.ird.cantharella.data.model.utils.DocumentAttachable;

import org.apache.commons.collections.CollectionUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.markup.html.image.ContextImage;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

/**
 * Column displaying an image and model as tooltip.
 * 
 * @author Eric Chatellier
 * @param <T> Generic type
 * @param <S> the type of the sort property
 */
public abstract class DocumentTooltipColumn<T extends DocumentAttachable, S> extends AbstractColumn<T, S> {

    /**
     * Constructor.
     * 
     * @param displayModel header display model
     */
    public DocumentTooltipColumn(IModel<String> displayModel) {
        super(displayModel);
    }

    /** {@inheritDoc} */
    @Override
    public Component getHeader(final String componentId) {
        ImagePanel panel = new ImagePanel(componentId, Model.of("images/open_folder_yellow.png"));
        panel.add(new AttributeModifier("title", getDisplayModel()));
        return panel;
    }

    /** {@inheritDoc} */
    @Override
    public void populateItem(Item<ICellPopulator<T>> item, String componentId, IModel<T> model) {

        List<Document> documents = model.getObject().getDocuments();
        StringBuilder builder = new StringBuilder();
        if (CollectionUtils.isNotEmpty(documents)) {
            Iterator<Document> itDocument = documents.iterator();
            while (itDocument.hasNext()) {
                Document document = itDocument.next();
                builder.append(document.getTitre());
                if (itDocument.hasNext()) {
                    builder.append("\n");
                }
            }
        }

        DocumentTooltipPanel panel = new DocumentTooltipPanel(componentId, model, Model.of(builder.toString()));
        panel.setVisibilityAllowed(CollectionUtils.isNotEmpty(documents));
        item.add(panel);
    }

    /**
     * Called when link is clicked.
     * 
     * @param model current model
     */
    public abstract void onClick(IModel<T> model);

    /**
     * Panel which include a linkable image. Used with the LinkableImagePropertyColumn$LinkablePanel.html file
     */
    public class DocumentTooltipPanel extends Panel {

        /**
         * Constructor
         * 
         * @param id Component id
         * @param model model
         * @param messageModel Message Model displayed over the image
         */
        public DocumentTooltipPanel(String id, final IModel<T> model, IModel<?> messageModel) {
            super(id);

            Link<T> link = new Link<T>("link") {
                @Override
                public void onClick() {
                    DocumentTooltipColumn.this.onClick(model);
                }
            };
            add(link);

            ContextImage image = new ContextImage("tooltip", Model.of("images/attachment.png"));
            image.add(new AttributeModifier("title", messageModel));

            link.add(image);
        }
    }

    /**
     * Use panel to be able to add an image into wicket <span wicket:id="label"> datatable column header.
     */
    public class ImagePanel extends Panel {

        /**
         * Constructor
         * 
         * @param componentId component id
         * @param imageSrc image src
         */
        public ImagePanel(String componentId, final Model<String> imageSrc) {
            super(componentId);

            ContextImage image = new ContextImage("image", imageSrc);
            add(image);
        }
    }
}