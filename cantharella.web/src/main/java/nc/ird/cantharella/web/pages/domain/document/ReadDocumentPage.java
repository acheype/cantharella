/*
 * #%L
 * Cantharella :: Web
 * $Id: ReadDocumentPage.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.web/src/main/java/nc/ird/cantharella/web/pages/domain/document/ReadDocumentPage.java $
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

import nc.ird.cantharella.data.exceptions.DataConstraintException;
import nc.ird.cantharella.data.model.Document;
import nc.ird.cantharella.data.model.Utilisateur;
import nc.ird.cantharella.data.model.utils.DocumentAttachable;
import nc.ird.cantharella.service.services.DocumentService;
import nc.ird.cantharella.web.config.WebContext;
import nc.ird.cantharella.web.pages.TemplatePage;
import nc.ird.cantharella.web.pages.domain.document.panel.DocumentLinkPanel;
import nc.ird.cantharella.web.pages.domain.lot.ManageLotPage;
import nc.ird.cantharella.web.utils.CallerPage;
import nc.ird.cantharella.web.utils.behaviors.JSConfirmationBehavior;
import nc.ird.cantharella.web.utils.behaviors.ReplaceEmptyLabelBehavior;
import nc.ird.cantharella.web.utils.forms.SubmittableButton;
import nc.ird.cantharella.web.utils.forms.SubmittableButtonEvents;
import nc.ird.cantharella.web.utils.models.DisplayMapValuePropertyModel;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * Document read page.
 * 
 * @author Eric Chatellier
 */
public class ReadDocumentPage extends TemplatePage {

    /** Action : delete later (delegate to entity cascade). */
    public static final String ACTION_DELETE_LATER = "DeleteLater";

    /** Action : delete. */
    public static final String ACTION_DELETE = "Delete";

    /** Model : document */
    private final IModel<Document> documentModel;

    /** Service : document */
    @SpringBean
    private DocumentService documentService;

    /** Caller page. */
    private final CallerPage callerPage;

    /** Update or delete enabled flag. */
    protected boolean updateOrDeleteEnabled;

    /** Update link. */
    protected Link<Document> updateLink;

    /** Delete button. */
    protected Button deleteButton;

    /**
     * Constructeur
     * 
     * @param document document
     * @param documentAttachable entity where document is attached to
     * @param callerPage caller page
     * @param fromEditEntity page called from entity in edition mode
     */
    public ReadDocumentPage(final Document document, final DocumentAttachable documentAttachable,
            final CallerPage callerPage, final boolean fromEditEntity) {
        super(ReadDocumentPage.class);
        this.callerPage = callerPage;
        final CallerPage currentPage = new CallerPage(this);

        Utilisateur utilisateur = getSession().getUtilisateur();
        boolean updateOrDelete = documentService.updateOrdeleteDocumentEnabled(document, utilisateur);

        // Initialisation du modèle
        documentModel = new Model<Document>(document);

        add(new Label("Document.titre", new PropertyModel<String>(documentModel, "titre"))
                .add(new ReplaceEmptyLabelBehavior()));
        add(new Label("Document.createur", new PropertyModel<String>(documentModel, "createur"))
                .add(new ReplaceEmptyLabelBehavior()));
        add(new Label("Document.dateCreation", new PropertyModel<String>(documentModel, "dateCreation"))
                .add(new ReplaceEmptyLabelBehavior()));
        add(new Label("Document.editeur", new PropertyModel<String>(documentModel, "editeur"))
                .add(new ReplaceEmptyLabelBehavior()));
        add(new MultiLineLabel("Document.description", new PropertyModel<String>(documentModel, "description"))
                .add(new ReplaceEmptyLabelBehavior()));
        add(new Label("Document.langue", new DisplayMapValuePropertyModel<String>(documentModel, "langue",
                WebContext.LANGUAGES.get(getSession().getLocale()))).add(new ReplaceEmptyLabelBehavior()));
        add(new Label("Document.contrainteLegale", new PropertyModel<String>(documentModel, "contrainteLegale"))
                .add(new ReplaceEmptyLabelBehavior()));
        add(new Label("Document.ajoutePar", new PropertyModel<String>(documentModel, "ajoutePar"))
                .add(new ReplaceEmptyLabelBehavior()));
        add(new Label("TypeDocument.nom", new PropertyModel<String>(documentModel, "typeDocument.nom"))
                .add(new ReplaceEmptyLabelBehavior()));
        add(new Label("TypeDocument.domaine", new PropertyModel<String>(documentModel, "typeDocument.domaine"))
                .add(new ReplaceEmptyLabelBehavior()));
        add(new Label("TypeDocument.description", new PropertyModel<String>(documentModel, "typeDocument.description"))
                .add(new ReplaceEmptyLabelBehavior()));
        add(new Label("Document.file", new PropertyModel<String>(documentModel, "fileName"))
                .add(new ReplaceEmptyLabelBehavior()));
        add(new Label("Document.fileMimetype", new PropertyModel<String>(documentModel, "fileMimetype"))
                .add(new ReplaceEmptyLabelBehavior()));
        add(new DocumentLinkPanel("Document.link", documentModel));

        // Formulaire des actions
        final Form<Void> formView = new Form<Void>("Form");

        // Action : mise à jour (redirection vers le formulaire)
        updateLink = new Link<Document>(getResource() + ".Document.Update", new Model<Document>(
                documentModel.getObject())) {
            @Override
            public void onClick() {
                setResponsePage(new ManageDocumentPage(getModelObject(), documentAttachable, currentPage,
                        !fromEditEntity));
            }
        };
        updateLink.setVisibilityAllowed(updateOrDelete);
        formView.add(updateLink);

        // Action : suppression
        deleteButton = new SubmittableButton(ACTION_DELETE, ManageLotPage.class, new SubmittableButtonEvents() {
            @Override
            public void onProcess() throws DataConstraintException {
                documentAttachable.removeDocument(documentModel.getObject());
                if (!fromEditEntity) {
                    documentService.updateDocumentAttachable(documentAttachable);
                }
            }

            @Override
            public void onSuccess() {
                if (!fromEditEntity) {
                    successNextPage(ManageDocumentPage.class, ACTION_DELETE);
                } else {
                    successNextPage(ManageDocumentPage.class, ACTION_DELETE_LATER);
                }
                redirect();
            }
        });
        deleteButton.add(new JSConfirmationBehavior(getStringModel("Confirm")));
        deleteButton.setDefaultFormProcessing(false);
        deleteButton.setVisibilityAllowed(updateOrDelete);
        formView.add(deleteButton);

        // Action : retour
        formView.add(new Link<Void>(getResource() + ".Document.Back") {
            @Override
            public void onClick() {
                callerPage.responsePage((TemplatePage) getPage());
            }
        });
        add(formView);
    }

    /**
     * Redirection vers une autre page
     */
    private void redirect() {
        callerPage.responsePage(this);
    }

    /**
     * Modify update or delete enabled property.
     * 
     * @param updateOrDeleteEnabled update or delete enabled property
     */
    public void setUpdateOrDeleteEnabled(boolean updateOrDeleteEnabled) {
        this.updateOrDeleteEnabled = updateOrDeleteEnabled;

        updateLink.setVisibilityAllowed(updateOrDeleteEnabled);
        deleteButton.setVisibilityAllowed(updateOrDeleteEnabled);
    }
}
