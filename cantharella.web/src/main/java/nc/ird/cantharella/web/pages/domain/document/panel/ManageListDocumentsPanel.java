/*
 * #%L
 * Cantharella :: Web
 * $Id: ManageListDocumentsPanel.java 268 2014-05-06 15:45:40Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.web/src/main/java/nc/ird/cantharella/web/pages/domain/document/panel/ManageListDocumentsPanel.java $
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
package nc.ird.cantharella.web.pages.domain.document.panel;

import java.util.List;

import nc.ird.cantharella.data.model.Document;
import nc.ird.cantharella.data.model.utils.DocumentAttachable;
import nc.ird.cantharella.web.pages.domain.document.ManageDocumentPage;
import nc.ird.cantharella.web.pages.domain.document.ReadDocumentPage;
import nc.ird.cantharella.web.utils.CallerPage;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;

/**
 * Display document list in manage mode.
 * 
 * @author Eric Chatellier
 */
public final class ManageListDocumentsPanel extends Panel {

    /** Document edit enabled flag. */
    protected boolean updateOrDeleteEnabled;

    /** New Document link (for disable). */
    protected MarkupContainer newDocumentMarkup;

    /**
     * Constructeur.
     * 
     * @param id ID
     * @param documentAttachableModel entity model where documents are attached to
     * @param currentPage currentPage
     */
    public ManageListDocumentsPanel(String id, final IModel<? extends DocumentAttachable> documentAttachableModel,
            final CallerPage currentPage) {
        super(id, documentAttachableModel);

        // edit mode enabled by default
        this.updateOrDeleteEnabled = true;

        Form<Void> formView = new Form<Void>("form");
        add(formView);

        final MarkupContainer documentsTable = new WebMarkupContainer("ListDocumentsPage.AttachedDocuments.Table");
        documentsTable.setOutputMarkupId(true);
        formView.add(documentsTable);

        final IModel<List<Document>> listDocumentModel = new PropertyModel<List<Document>>(documentAttachableModel,
                "documents");

        // Contenu tableaux provenance
        ListView<Document> documentsListView = new ListView<Document>("ListDocumentsPage.AttachedDocuments.List",
                listDocumentModel) {
            @Override
            protected void populateItem(ListItem<Document> item) {
                if (item.getIndex() % 2 == 1) {
                    item.add(new AttributeModifier("class", item.getIndex() % 2 == 0 ? "even" : "odd"));
                }

                final IModel<Document> documentModel = item.getModel();
                // affichage + lien vers la fiche
                Link<Document> documentLink = new Link<Document>("Document.titre.List") {
                    @Override
                    public void onClick() {
                        ReadDocumentPage readDocumentPage = new ReadDocumentPage(documentModel.getObject(),
                                documentAttachableModel.getObject(), currentPage, true);
                        readDocumentPage.setUpdateOrDeleteEnabled(updateOrDeleteEnabled);
                        setResponsePage(readDocumentPage);
                    }
                };
                documentLink.add(new Label("Document.titre.Label.List", new PropertyModel<Document>(documentModel,
                        "titre")));
                item.add(documentLink);

                item.add(new Label("Document.typeDocument.List", new PropertyModel<String>(documentModel,
                        "typeDocument.nom")));
                item.add(new Label("Document.createur.List", new PropertyModel<String>(documentModel, "createur")));
                item.add(new DocumentLinkPanel("Document.link.List", documentModel));

                item.add(new AjaxButton("Document.Delete.List", new StringResourceModel("Delete", this, null)) {
                    @Override
                    protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                        Document document = documentModel.getObject();
                        documentAttachableModel.getObject().removeDocument(document);

                        if (target != null) {
                            target.add(documentsTable);
                        }
                    }
                }.setEnabled(updateOrDeleteEnabled));
            }
        };
        documentsTable.add(documentsListView);

        // Selon la non existence d'elements dans la liste on affiche le span
        MarkupContainer noTableDocuments = new WebMarkupContainer("ListDocumentsPage.AttachedDocuments.noTable") {
            @Override
            public boolean isVisible() {
                return listDocumentModel.getObject().isEmpty();
            }
        };
        documentsTable.add(noTableDocuments);

        newDocumentMarkup = new WebMarkupContainer("ListDocumentsPage.AttachedDocuments.NewDocument");
        documentsTable.add(newDocumentMarkup);

        AjaxSubmitLink newDocumentLink = new AjaxSubmitLink("NewDocument") {
            @Override
            protected void onSubmit(AjaxRequestTarget request, Form<?> form) {
                setResponsePage(new ManageDocumentPage(currentPage, documentAttachableModel.getObject(), false));
            }

            // si erreur, le formulaire est également enregistré puis la redirection effectuée
            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                setResponsePage(new ManageDocumentPage(currentPage, documentAttachableModel.getObject(), false));
            }
        };
        newDocumentMarkup.add(newDocumentLink);
    }

    /**
     * Modify update or delete enabled property.
     * 
     * @param updateOrDeleteEnabled update or delete enabled property
     */
    public void setUpdateOrDeleteEnabled(boolean updateOrDeleteEnabled) {
        this.updateOrDeleteEnabled = updateOrDeleteEnabled;

        newDocumentMarkup.setVisibilityAllowed(updateOrDeleteEnabled);
    }
}
