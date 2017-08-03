/*
 * #%L
 * Cantharella :: Web
 * $Id: ManageDocumentPage.java 268 2014-05-06 15:45:40Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.web/src/main/java/nc/ird/cantharella/web/pages/domain/document/ManageDocumentPage.java $
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

import java.util.Date;
import java.util.List;

import nc.ird.cantharella.data.exceptions.DataConstraintException;
import nc.ird.cantharella.data.model.Document;
import nc.ird.cantharella.data.model.Personne;
import nc.ird.cantharella.data.model.TypeDocument;
import nc.ird.cantharella.data.model.utils.DocumentAttachable;
import nc.ird.cantharella.data.validation.utils.ModelValidator;
import nc.ird.cantharella.service.exceptions.InvalidFileExtensionException;
import nc.ird.cantharella.service.services.DocumentService;
import nc.ird.cantharella.service.services.PersonneService;
import nc.ird.cantharella.utils.BeanTools.AccessType;
import nc.ird.cantharella.utils.CollectionTools;
import nc.ird.cantharella.web.config.WebApplicationImpl;
import nc.ird.cantharella.web.config.WebContext;
import nc.ird.cantharella.web.pages.TemplatePage;
import nc.ird.cantharella.web.pages.domain.personne.ManagePersonnePage;
import nc.ird.cantharella.web.pages.renderers.PersonneRenderer;
import nc.ird.cantharella.web.utils.CallerPage;
import nc.ird.cantharella.web.utils.behaviors.JSConfirmationBehavior;
import nc.ird.cantharella.web.utils.forms.AutoCompleteTextFieldString;
import nc.ird.cantharella.web.utils.forms.AutoCompleteTextFieldString.ComparisonMode;
import nc.ird.cantharella.web.utils.forms.SubmittableButton;
import nc.ird.cantharella.web.utils.forms.SubmittableButtonEvents;
import nc.ird.cantharella.web.utils.panels.SimpleTooltipPanel;
import nc.ird.cantharella.web.utils.renderers.MapChoiceRenderer;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.extensions.markup.html.form.DateTextField;
import org.apache.wicket.extensions.yui.calendar.DatePicker;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.lang.Bytes;

/**
 * Document management page (creation/edition).
 * 
 * @author Eric Chatellier
 */
public class ManageDocumentPage extends TemplatePage {

    /** Action : create later (delegate to entity cascade). */
    private static final String ACTION_CREATE_LATER = "CreateLater";

    /** Action : delete later (delegate to entity cascade). */
    private static final String ACTION_DELETE_LATER = "DeleteLater";

    /** Action : update later (delegate to entity cascade). */
    private static final String ACTION_UPDATE_LATER = "UpdateLater";

    /** Action : create. */
    private static final String ACTION_CREATE = "Create";

    /** Action : delete. */
    private static final String ACTION_DELETE = "Delete";

    /** Action : update. */
    private static final String ACTION_UPDATE = "Update";

    /** Model : document. */
    private IModel<Document> documentModel;

    /** Service : document */
    @SpringBean
    private DocumentService documentService;

    /** Service : personnes */
    @SpringBean
    private PersonneService personneService;

    /** Model validateur */
    @SpringBean(name = "webModelValidator")
    private ModelValidator validator;

    /** Caller page. */
    private final CallerPage callerPage;

    /**
     * Constructeur (mode création)
     * 
     * @param callerPage Page appelante
     * @param documentAttachable entity where document is attached to
     * @param multipleEntry Saisie multiple
     */
    public ManageDocumentPage(CallerPage callerPage, DocumentAttachable documentAttachable, boolean multipleEntry) {
        this(null, documentAttachable, callerPage, multipleEntry, false);
    }

    /**
     * Constructeur (mode édition)
     * 
     * @param document document to edit
     * @param documentAttachable document sur le
     * @param callerPage Page appelante
     * @param updateWithService if {@code true} should update entity with service
     */
    public ManageDocumentPage(Document document, DocumentAttachable documentAttachable, CallerPage callerPage,
            boolean updateWithService) {
        this(document, documentAttachable, callerPage, false, updateWithService);
    }

    /**
     * Constructeur. Si idDocument et document sont null, on créée un nouveau Document. Si idDocument est renseigné, on
     * édite le document correspondant. Si document est renseigné, on créée un nouveau document à partir des
     * informations qu'il contient.
     * 
     * @param document document
     * @param documentAttachable entity where document is attached to
     * @param callerPage Page appelante
     * @param multipleEntry Saisie multiple
     */
    private ManageDocumentPage(Document document, final DocumentAttachable documentAttachable,
            final CallerPage callerPage, boolean multipleEntry, final boolean updateWithService) {
        super(ManageDocumentPage.class);
        this.callerPage = callerPage;

        final CallerPage currentPage = new CallerPage(this);

        // get configuration
        long documentMaxUploadSize = ((WebApplicationImpl) getApplication()).getDocumentMaxUploadSize();
        String documentExtensionAllowed = ((WebApplicationImpl) getApplication()).getDocumentExtensionAllowed();

        // Initialisation du modèle
        documentModel = new Model<Document>(document == null ? new Document() : document);

        final boolean createMode = document == null;
        if (createMode) {
            documentModel.getObject().setDateCreation(new Date());
            documentModel.getObject().setCreateur(getSession().getUtilisateur());
            documentModel.getObject().setEditeur(getSession().getUtilisateur().getOrganisme());
            documentModel.getObject().setAjoutePar(getSession().getUtilisateur());
        }

        // Initialisation des listes (pour le dropDownChoice)
        List<Personne> personnes = personneService.listPersonnes();
        List<TypeDocument> typeDocuments = documentService.listTypeDocuments();
        List<String> editeurs = documentService.listDocumentEditeurs();
        List<String> containteLegales = documentService.listDocumentContrainteLegales();

        if (document != null) {
            // hack nécessaire afin d'avoir dans le model le même objet que
            // celui de la liste de choix (sinon comme les objets viennent de sessions hibernate différentes, on n'a pas
            // l'égalité entre les objets)
            documentModel.getObject().setCreateur(
                    CollectionTools.findWithValue(personnes, "idPersonne", AccessType.GETTER, documentModel.getObject()
                            .getCreateur().getIdPersonne()));
            documentModel.getObject().setTypeDocument(
                    CollectionTools.findWithValue(typeDocuments, "idTypeDocument", AccessType.GETTER, documentModel
                            .getObject().getTypeDocument().getIdTypeDocument()));
        }

        // champ fichier
        final FileUploadField fileUploadField = new FileUploadField("Document.file");

        // initialisation du formulaire wicket
        final Form<Void> formView = new Form<Void>("ManageDocumentPage.Form");
        formView.setMultiPart(true);
        formView.setMaxSize(Bytes.megabytes(documentMaxUploadSize));

        formView.add(new TextField<String>("Document.titre", new PropertyModel<String>(documentModel, "titre")));

        final DropDownChoice<Personne> createurInput = new DropDownChoice<Personne>("Document.createur",
                new PropertyModel<Personne>(documentModel, "createur"), personnes, new PersonneRenderer());
        createurInput.setOutputMarkupId(true);
        createurInput.setNullValid(false);
        formView.add(createurInput);

        // Action : création d'une nouvelle personne
        // ajaxSubmitLink permet de sauvegarder l'état du formulaire
        formView.add(new AjaxSubmitLink("NewPersonne") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                setResponsePage(new ManagePersonnePage(currentPage, false));
            }

            // si erreur, le formulaire est également enregistré puis la redirection effectuée
            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                setResponsePage(new ManagePersonnePage(currentPage, false));
            }
        });

        formView.add(new DateTextField("Document.dateCreation", new PropertyModel<Date>(documentModel, "dateCreation"))
                .add(new DatePicker()));

        AutoCompleteTextFieldString editeurInput = new AutoCompleteTextFieldString("Document.editeur",
                new PropertyModel<String>(documentModel, "editeur"), editeurs, ComparisonMode.CONTAINS);
        formView.add(editeurInput);

        formView.add(new TextArea<String>("Document.description", new PropertyModel<String>(documentModel,
                "description")));

        formView.add(new DropDownChoice<String>("Document.langue", new PropertyModel<String>(documentModel, "langue"),
                WebContext.LANGUAGE_CODES.get(getSession().getLocale()), new MapChoiceRenderer<String, String>(
                        WebContext.LANGUAGES.get(getSession().getLocale()))));

        AutoCompleteTextFieldString containteLegalesInput = new AutoCompleteTextFieldString(
                "Document.contrainteLegale", new PropertyModel<String>(documentModel, "contrainteLegale"),
                containteLegales, ComparisonMode.CONTAINS);
        formView.add(containteLegalesInput);

        // AjoutePar en lecture seule
        formView.add(new TextField<String>("Document.ajoutePar", new PropertyModel<String>(documentModel, "ajoutePar"))
                .setEnabled(false));

        // Type de document
        final DropDownChoice<TypeDocument> typeDocumentChoice = new DropDownChoice<TypeDocument>("TypeDocument.nom",
                new PropertyModel<TypeDocument>(documentModel, "typeDocument"), typeDocuments);
        typeDocumentChoice.setOutputMarkupId(true);
        typeDocumentChoice.setNullValid(false);
        formView.add(typeDocumentChoice);

        final Label typeDocumentDomainLabel = new Label("TypeDocument.domaine", new PropertyModel<String>(
                documentModel, "typeDocument.domaine"));
        typeDocumentDomainLabel.setOutputMarkupId(true);
        formView.add(typeDocumentDomainLabel);

        final Label typeDocumentDescriptionLabel = new Label("TypeDocument.description", new PropertyModel<String>(
                documentModel, "typeDocument.description"));
        typeDocumentDescriptionLabel.setOutputMarkupId(true);
        formView.add(typeDocumentDescriptionLabel);

        typeDocumentChoice.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            protected void onUpdate(AjaxRequestTarget target) {
                target.add(typeDocumentDomainLabel, typeDocumentDescriptionLabel);
            }
        });

        // Fichier
        formView.add(new SimpleTooltipPanel("Document.file.info", new Model<String>(getString("Document.file.info",
                Model.of(new Object[] { documentMaxUploadSize, documentExtensionAllowed })))));
        formView.add(fileUploadField);

        // Action : création du document
        Button createButton = new SubmittableButton(ACTION_CREATE, new SubmittableButtonEvents() {
            @Override
            public void onValidate() {
                Document document = documentModel.getObject();
                final FileUpload uploadedFile = fileUploadField.getFileUpload();
                if (uploadedFile != null) {
                    try {
                        documentService.addDocumentContent(document, uploadedFile.getClientFileName(),
                                uploadedFile.getContentType(), uploadedFile.getBytes());

                        // if no error
                        validateModel();
                    } catch (InvalidFileExtensionException ex) {
                        error(getString("ManageDocumentPage.Error.notAllowedExtension"));
                    }
                } else if (StringUtils.isEmpty(document.getFileName())) {
                    error(getString("ManageDocumentPage.Error.emptyFile"));
                } else {
                    validateModel();
                }
            }

            @Override
            public void onProcess() throws DataConstraintException {
                // document can only be created from an attached entity
                Document document = documentModel.getObject();
                documentAttachable.addDocument(document);
            }

            @Override
            public void onSuccess() {
                // document can only be created from an attached entity
                successNextPage(ACTION_CREATE_LATER);
                redirect();
            }
        });
        createButton.setVisibilityAllowed(createMode);
        formView.add(createButton);

        // Action : mise à jour du document
        Button updateButton = new SubmittableButton(ACTION_UPDATE, new SubmittableButtonEvents() {
            @Override
            public void onProcess() throws DataConstraintException {
                Document document = documentModel.getObject();
                final FileUpload uploadedFile = fileUploadField.getFileUpload();
                if (uploadedFile != null) {
                    try {
                        documentService.addDocumentContent(document, uploadedFile.getClientFileName(),
                                uploadedFile.getContentType(), uploadedFile.getBytes());

                        // if no error
                        validateModel();
                    } catch (InvalidFileExtensionException ex) {
                        error(getString("ManageDocumentPage.Error.notAllowedExtension"));
                    }
                }
                if (updateWithService) {
                    documentService.updateDocumentAttachable(documentAttachable);
                } // otherwise, nothing, will be updated by cascade
            }

            @Override
            public void onSuccess() {
                if (updateWithService) {
                    successNextPage(ACTION_UPDATE);
                } else {
                    successNextPage(ACTION_UPDATE_LATER);
                }
                callerPage.responsePage((TemplatePage) getPage());
            }

            @Override
            public void onValidate() {
                validateModel();
            }
        });
        updateButton.setVisibilityAllowed(!createMode);
        formView.add(updateButton);

        // Action : suppression du document
        Button deleteButton = new SubmittableButton(ACTION_DELETE, new SubmittableButtonEvents() {
            @Override
            public void onProcess() throws DataConstraintException {
                Document document = documentModel.getObject();
                if (updateWithService) {
                    documentAttachable.removeDocument(document);
                    documentService.updateDocumentAttachable(documentAttachable);
                } else {
                    // remove document from attached entity
                    // and save will be performed by cascade
                    documentAttachable.removeDocument(document);
                }
            }

            @Override
            public void onSuccess() {
                if (updateWithService) {
                    successNextPage(ACTION_DELETE);
                    // first getPage() is read document
                    // it has been deleted so go to previous one
                    callerPage.responsePage((TemplatePage) getPage().getPage());
                } else {
                    successNextPage(ACTION_DELETE_LATER);
                    callerPage.responsePage((TemplatePage) getPage());
                }

            }
        });
        deleteButton.setVisibilityAllowed(!createMode);
        deleteButton.add(new JSConfirmationBehavior(getStringModel("Confirm")));
        deleteButton.setDefaultFormProcessing(false);
        formView.add(deleteButton);

        formView.add(new Link<Void>("Cancel") {
            // Cas où le formulaire est annulé
            @Override
            public void onClick() {
                callerPage.responsePage((TemplatePage) this.getPage());
            }
        });

        add(formView);
    }

    /**
     * Redirection vers une autre page. Cas où le formulaire est validé
     */
    private void redirect() {
        if (callerPage != null) {
            // On passe l'id du document associé à cette page, en paramètre de la prochaine page, pour lui permettre de
            // l'exploiter si besoin
            //callerPage.addPageParameter(Document.class.getSimpleName(), documentModel.getObject().getIdDocument());
            callerPage.responsePage(this);
        }
    }

    /**
     * Validate model
     */
    private void validateModel() {
        if (documentModel.getObject().getCreateur() == null) {
            documentModel.getObject().setCreateur(getSession().getUtilisateur());
        }
        List<String> errors = validator.validate(documentModel.getObject(), getSession().getLocale());
        addValidationErrors(errors);
    }
}
