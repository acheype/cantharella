/*
 * #%L
 * Cantharella :: Web
 * $Id: 
 * $HeadURL:
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
package nc.ird.cantharella.web.pages.domain.config.panels;

import java.util.List;

import nc.ird.cantharella.data.model.TypeDocument;
import nc.ird.cantharella.service.services.DocumentService;
import nc.ird.cantharella.web.pages.domain.config.ManageTypeDocumentPage;
import nc.ird.cantharella.web.utils.models.LoadableDetachableSortableListDataProvider;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * Panel wich list the document types
 * 
 * @author Adrien Cheype
 */
public class ListTypeDocumentPanel extends Panel {

    /**
     * Service : document
     */
    @SpringBean
    private DocumentService documentService;

    /**
     * Constructor
     * 
     * @param id The panel ID
     */
    public ListTypeDocumentPanel(String id) {
        super(id);

        add(new BookmarkablePageLink<Void>("ListTypeDocumentPanel.NewTypeDocument", ManageTypeDocumentPage.class));

        // On englobe le "DataView" dans un composant neutre que l'on pourra
        // rafraichir quand la liste évoluera
        final MarkupContainer documentTypesRefresh = new WebMarkupContainer(
                "ListTypeDocumentPanel.TypesDocument.Refresh");
        documentTypesRefresh.setOutputMarkupId(true);
        add(documentTypesRefresh);

        // Liste des erreursTest
        final List<TypeDocument> typesDocument = documentService.listTypeDocuments();
        LoadableDetachableSortableListDataProvider<TypeDocument> erreursDataProvider = new LoadableDetachableSortableListDataProvider<TypeDocument>(
                typesDocument, getSession().getLocale());

        documentTypesRefresh
                .add(new DataView<TypeDocument>("ListTypeDocumentPanel.TypesDocument", erreursDataProvider) {
                    @Override
                    protected void populateItem(Item<TypeDocument> item) {
                        if (item.getIndex() % 2 == 1) {
                            item.add(new AttributeModifier("class", item.getIndex() % 2 == 0 ? "even" : "odd"));
                        }

                        TypeDocument documentType = item.getModelObject();
                        // Colonnes
                        item.add(new Label("ListTypeDocumentPanel.TypesDocument.nom", new PropertyModel<String>(
                                documentType, "nom")));
                        item.add(new Label("ListTypeDocumentPanel.TypesDocument.domaine", new PropertyModel<String>(
                                documentType, "domaine")));
                        item.add(new Label("ListTypeDocumentPanel.TypesDocument.description",
                                new PropertyModel<String>(documentType, "description")));

                        // Action : mise à jour (redirection vers le formulaire)
                        Link<TypeDocument> updateLink = new Link<TypeDocument>(
                                "ListTypeDocumentPanel.TypesDocument.Update", new Model<TypeDocument>(documentType)) {
                            @Override
                            public void onClick() {
                                setResponsePage(new ManageTypeDocumentPage(getModelObject().getIdTypeDocument()));
                            }
                        };
                        item.add(updateLink);
                    }
                });
    }
}
