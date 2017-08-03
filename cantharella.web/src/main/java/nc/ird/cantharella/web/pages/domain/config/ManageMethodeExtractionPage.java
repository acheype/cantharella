/*
 * #%L
 * Cantharella :: Web
 * $Id: ManageMethodeExtractionPage.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.web/src/main/java/nc/ird/cantharella/web/pages/domain/config/ManageMethodeExtractionPage.java $
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
package nc.ird.cantharella.web.pages.domain.config;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import nc.ird.cantharella.data.exceptions.DataConstraintException;
import nc.ird.cantharella.data.exceptions.DataNotFoundException;
import nc.ird.cantharella.data.exceptions.UnexpectedException;
import nc.ird.cantharella.data.model.MethodeExtraction;
import nc.ird.cantharella.data.model.TypeExtrait;
import nc.ird.cantharella.data.validation.utils.ModelValidator;
import nc.ird.cantharella.service.services.ExtractionService;
import nc.ird.cantharella.service.utils.normalizers.ConfigNameNormalizer;
import nc.ird.cantharella.service.utils.normalizers.utils.Normalizer;
import nc.ird.cantharella.web.pages.TemplatePage;
import nc.ird.cantharella.web.utils.behaviors.JSConfirmationBehavior;
import nc.ird.cantharella.web.utils.forms.SubmittableButton;
import nc.ird.cantharella.web.utils.forms.SubmittableButtonEvents;
import nc.ird.cantharella.web.utils.security.AuthRole;
import nc.ird.cantharella.web.utils.security.AuthRoles;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxFallbackButton;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * Page for adding/update/delete a new "MethodeExtraction"
 * 
 * @author Adrien Cheype
 */
@AuthRoles({ AuthRole.ADMIN })
public final class ManageMethodeExtractionPage extends TemplatePage {

    /** Action : create */
    private static final String ACTION_CREATE = "Create";

    /** Action : update */
    private static final String ACTION_UPDATE = "Update";

    /** Action : delete */
    public static final String ACTION_DELETE = "Delete";

    /** The return page parameter (key and value) */
    final private String[] RETURN_PARAM = { "methodeExtraction", "opened" };

    /** Logger */
    private static final Logger LOG = LoggerFactory.getLogger(ManageMethodeExtractionPage.class);

    /** erreurTest Model */
    private final IModel<MethodeExtraction> methodeExtractionModel;

    /** model for adding TypeExtrait */
    private IModel<TypeExtrait> newTypeExtraitModel;

    /** Service : extraits */
    @SpringBean
    private ExtractionService extraitService;

    /** Model validateur */
    @SpringBean(name = "webModelValidator")
    private ModelValidator validator;

    /**
     * couple of <initiale, TypeExtrait> in order to recuperate the id of the deleted type usefull in case of delete
     * thus add of same initial type
     */
    private HashMap<String, TypeExtrait> typesDeleted;

    /**
     * Constructor
     */
    public ManageMethodeExtractionPage() {
        this(null);
    }

    /**
     * Constructor. If idMethodeExtraction is null, creating a new methode, else editing the corresponding methode
     * 
     * @param idMethode The ID of the methode
     */
    public ManageMethodeExtractionPage(Integer idMethode) {
        super(ManageMethodeExtractionPage.class);

        boolean createMode;

        typesDeleted = new HashMap<String, TypeExtrait>();

        // model initialization
        newTypeExtraitModel = new Model<TypeExtrait>(new TypeExtrait());
        if (idMethode == null) {
            // creation mode
            methodeExtractionModel = new Model<MethodeExtraction>(new MethodeExtraction());
            createMode = true;
        } else {
            try {
                methodeExtractionModel = new Model<MethodeExtraction>(extraitService.loadMethodeExtraction(idMethode));
            } catch (DataNotFoundException e) {
                LOG.error(e.getMessage(), e);
                throw new UnexpectedException(e);
            }
            createMode = false;
        }

        // bind with markup
        final Form<Void> formView = new Form<Void>("Form");

        formView.add(new TextField<String>("MethodeExtraction.nom", new PropertyModel<String>(methodeExtractionModel,
                "nom")));
        formView.add(new TextArea<String>("MethodeExtraction.description", new PropertyModel<String>(
                methodeExtractionModel, "description")));

        // Tableau des types en sortie
        final MarkupContainer typesEnSortieTable = new WebMarkupContainer("MethodeExtraction.typesEnSortie.Table");
        typesEnSortieTable.setOutputMarkupId(true);

        // Liste des types extraits ajoutés
        typesEnSortieTable.add(new ListView<TypeExtrait>("MethodeExtraction.typesEnSortie.List",
                new PropertyModel<List<TypeExtrait>>(methodeExtractionModel, "sortedTypesEnSortie")) {
            @Override
            protected void populateItem(ListItem<TypeExtrait> item) {
                if (item.getIndex() % 2 == 1) {
                    item.add(new AttributeModifier("class", item.getIndex() % 2 == 0 ? "even" : "odd"));
                }

                final TypeExtrait typeExtrait = item.getModelObject();
                // Colonnes
                item.add(new Label("MethodeExtraction.typesEnSortie.List.initiales", new PropertyModel<String>(
                        typeExtrait, "initiales")));
                item.add(new Label("MethodeExtraction.typesEnSortie.List.description", new PropertyModel<String>(
                        typeExtrait, "description")));

                // Action : suppression d'un type extrait
                Button deleteButton = new AjaxFallbackButton("MethodeExtraction.typesEnSortie.List.Delete", formView) {
                    @Override
                    protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                        // Suppression
                        methodeExtractionModel.getObject().getTypesEnSortie().remove(typeExtrait);
                        // keep the id of the deleted 'extrait' in case of new one with the same 'initiales'
                        typesDeleted.put(typeExtrait.getInitiales(), typeExtrait);

                        if (target != null) {
                            target.add(typesEnSortieTable);
                        }
                    }

                    @Override
                    protected void onError(AjaxRequestTarget target, Form<?> form) {
                        // never called
                    }

                };
                deleteButton.setDefaultFormProcessing(false);
                item.add(deleteButton);
            }
        });

        final FormComponent<String> initialesInput = new TextField<String>("MethodeExtraction.typesEnSortie.initiales",
                new PropertyModel<String>(newTypeExtraitModel, "initiales"));
        typesEnSortieTable.add(initialesInput);

        final FormComponent<String> descriptionInput = new TextField<String>(
                "MethodeExtraction.typesEnSortie.description", new PropertyModel<String>(newTypeExtraitModel,
                        "description"));
        typesEnSortieTable.add(descriptionInput);

        // Bouton AJAX pour ajouter un type extrait
        Button addButton = new AjaxFallbackButton("MethodeExtraction.typeEnSortie.Add", formView) {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                try {
                    // normalisation du type extrait
                    newTypeExtraitModel.getObject().setInitiales(
                            Normalizer.normalize(ConfigNameNormalizer.class, newTypeExtraitModel.getObject()
                                    .getInitiales()));
                    // Ajout du type extrait
                    newTypeExtraitModel.getObject().setMethodeExtraction(methodeExtractionModel.getObject());
                    // recuperate the id if an just deleted type
                    if (typesDeleted.get(newTypeExtraitModel.getObject().getInitiales()) != null) {
                        newTypeExtraitModel.getObject().setIdTypeExtrait(
                                typesDeleted.get(newTypeExtraitModel.getObject().getInitiales()).getIdTypeExtrait());
                    }

                    // ajout à la liste
                    TypeExtrait typeExtraitAdded = newTypeExtraitModel.getObject().clone();
                    methodeExtractionModel.getObject().getTypesEnSortie().add(typeExtraitAdded);

                    List<String> errors = validator.validate(newTypeExtraitModel.getObject(), getSession().getLocale());
                    if (errors.isEmpty()) {
                        if (typesDeleted.get(newTypeExtraitModel.getObject().getInitiales()) != null) {
                            // si ancien extrait supprimé, on l'enlève de la liste des supprimés
                            typesDeleted.remove(newTypeExtraitModel.getObject().getInitiales());
                        }

                        // réinit du champ ajout
                        newTypeExtraitModel.getObject().setInitiales(null);
                        newTypeExtraitModel.getObject().setDescription(null);
                    } else {
                        methodeExtractionModel.getObject().getTypesEnSortie().remove(typeExtraitAdded);
                        addValidationErrors(errors);
                    }
                } catch (CloneNotSupportedException e) {
                    LOG.error(e.getMessage(), e);
                    throw new UnexpectedException(e);
                }

                if (target != null) {
                    target.add(typesEnSortieTable);
                    refreshFeedbackPage(target);
                }
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                refreshFeedbackPage(target);
            }

        };
        typesEnSortieTable.add(addButton);

        // Action : create the methodeExtraction
        Button createButton = new SubmittableButton(ACTION_CREATE, new SubmittableButtonEvents() {
            @Override
            public void onProcess() throws DataConstraintException {
                extraitService.createMethodeExtraction(methodeExtractionModel.getObject());
            }

            @Override
            public void onSuccess() {
                successNextPage(ACTION_CREATE);
                PageParameters params = new PageParameters();
                params.add(RETURN_PARAM[0], RETURN_PARAM[1]);
                setResponsePage(ListConfigurationPage.class, params);
            }

            @Override
            public void onValidate() {
                validateModel();
            }
        });
        createButton.setVisibilityAllowed(createMode);
        formView.add(createButton);

        formView.add(typesEnSortieTable);

        // Action : update the methodeExtraction
        Button updateButton = new SubmittableButton(ACTION_UPDATE, new SubmittableButtonEvents() {
            @Override
            public void onProcess() throws DataConstraintException {
                extraitService.updateMethodeExtraction(methodeExtractionModel.getObject());
            }

            @Override
            public void onSuccess() {
                successNextPage(ACTION_UPDATE);
                PageParameters params = new PageParameters();
                params.add(RETURN_PARAM[0], RETURN_PARAM[1]);
                setResponsePage(ListConfigurationPage.class, params);
            }

            @Override
            public void onValidate() {
                validateModel();
            }
        });
        updateButton.setVisibilityAllowed(!createMode);
        formView.add(updateButton);

        // Action : suppression
        Button deleteButton = new SubmittableButton(ACTION_DELETE, new SubmittableButtonEvents() {
            @Override
            public void onProcess() throws DataConstraintException {
                extraitService.deleteMethodeExtraction(methodeExtractionModel.getObject());
            }

            @Override
            public void onSuccess() {
                successNextPage(ACTION_DELETE);
                PageParameters params = new PageParameters();
                params.add(RETURN_PARAM[0], RETURN_PARAM[1]);
                setResponsePage(ListConfigurationPage.class, params);
            }
        });
        deleteButton.setVisibilityAllowed(!createMode);
        deleteButton.setDefaultFormProcessing(false);
        deleteButton.add(new JSConfirmationBehavior(getStringModel("Confirm")));
        formView.add(deleteButton);

        formView.add(new Link<Void>("Cancel") {
            @Override
            public void onClick() {
                PageParameters params = new PageParameters();
                params.add(RETURN_PARAM[0], RETURN_PARAM[1]);
                setResponsePage(ListConfigurationPage.class, params);
            }
        });

        formView.setDefaultButton(addButton);
        add(formView);
    }

    /**
     * Validate model
     */
    private void validateModel() {
        addValidationErrors(validator.validate(methodeExtractionModel.getObject(), getSession().getLocale()));

        // vérifie s'il existe des types supprimés qui sont tjr référencés
        for (TypeExtrait curDelType : typesDeleted.values()) {
            if (extraitService.isTypeExtraitReferenced(curDelType)) {
                getPage().error(
                        getString("TypeExtrait.isReferenced",
                                new Model<Serializable>(new Serializable[] { curDelType.getInitiales() })));
            }
        }
    }
}
