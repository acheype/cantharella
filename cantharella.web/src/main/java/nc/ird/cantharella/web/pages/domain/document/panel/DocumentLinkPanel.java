package nc.ird.cantharella.web.pages.domain.document.panel;

/*
 * #%L
 * Cantharella :: Web
 * $Id: DocumentLinkPanel.java 267 2014-05-06 15:39:05Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.web/src/main/java/nc/ird/cantharella/web/pages/domain/document/panel/DocumentLinkPanel.java $
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

import nc.ird.cantharella.data.model.Document;
import nc.ird.cantharella.data.model.DocumentContent;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebComponent;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.link.ResourceLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.resource.ByteArrayResource;
import org.apache.wicket.request.resource.ContextRelativeResource;
import org.apache.wicket.util.time.Duration;

/**
 * Panel which include a linkable image. Used with the LinkableImagePropertyColumn$LinkablePanel.html file
 */
public class DocumentLinkPanel extends Panel {

    /**
     * Constructor
     * 
     * @param id Component id
     * @param model model
     */
    public DocumentLinkPanel(String id, final IModel<Document> model) {
        super(id);

        final Document document = model.getObject();
        DocumentContent documentContent = document.getFileContent();
        ResourceLink<Document> link = new ResourceLink<Document>("link", new ByteArrayResource(
                document.getFileMimetype(), documentContent.getFileContent(), document.getFileName()));
        add(link);

        // les images ne doivent pas être mise en cache car les liens
        // étant défini sur les cellules des tableaux, lors
        // de la suppression, les index sont changés et les images
        // supprimées sont retrouvées par leurs lien dans le cache
        // navigateur
        WebComponent img;

        DocumentContent thumbContent = document.getFileContentThumb();
        if (thumbContent != null) {
            link.add(new AttributeModifier("class", "colorbox"));
            link.add(new AttributeModifier("title", document.getFileName()));
            img = new Image("image", new ByteArrayResource("image/png", thumbContent.getFileContent()) {
                @Override
                protected void configureResponse(ResourceResponse response, Attributes attributes) {
                    response.setCacheDuration(Duration.NONE);
                }
            });
        } else {
            img = new Image("image", new ContextRelativeResource("images/download.png") {
                @Override
                public boolean isCachingEnabled() {
                    return false;
                }
            });
        }
        img.add(new AttributeModifier("title", document.getFileName()));
        link.add(img);
    }
}
