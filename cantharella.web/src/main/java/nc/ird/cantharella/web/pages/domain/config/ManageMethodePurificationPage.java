/*
 * #%L
 * Cantharella :: Web
 * $Id: ManageMethodePurificationPage.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.web/src/main/java/nc/ird/cantharella/web/pages/domain/config/ManageMethodePurificationPage.java $
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
import nc.ird.cantharella.data.model.MethodePurification;
import nc.ird.cantharella.data.model.ParamMethoPuri;
import nc.ird.cantharella.data.validation.utils.ModelValidator;
import nc.ird.cantharella.service.services.PurificationService;
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
 * Page for adding/update/delete a new "MethodePurification"
 * 
 * @author Adrien Cheype
 */
@AuthRoles({ AuthRole.ADMIN })
public final class ManageMethodePurificationPage extends TemplatePage {

    /** Action : create */
    private static final String ACTION_CREATE = "Create";

    /** Action : update */
    private static final String ACTION_UPDATE = "Update";

    /** Action : delete */
    public static final String ACTION_DELETE = "Delete";

    /** The return page parameter (key and value) */
    final private String[] RETURN_PARAM = { "methodePurification", "opened" };

    /** Logger */
    private static final Logger LOG = LoggerFactory.getLogger(ManageMethodePurificationPage.class);

    /** erreurTest Model */
    private final IModel<MethodePurification> methodePurificationModel;

    /** model for adding Parametre */
    private IModel<ParamMethoPuri> newParamModel;

    /** Service : purification */
    @SpringBean
    private PurificationService purificationService;

    /** Model validateur */
    @SpringBean(name = "webModelValidator")
    private ModelValidator validator;

    /**
     * couple of <nom, param> in order to recuperate the id of the deleted parametre usefull in case of delete thus add
     * of same initial type
     */
    private HashMap<String, ParamMethoPuri> paramsDeleted;

    /**
     * Constructor
     */
    public ManageMethodePurificationPage() {
        this(null);
    }

    /**
     * Constructor. If idMethodePurification is null, creating a new methode, else editing the corresponding methode
     * 
     * @param idMethode The ID of the methode
     */
    public ManageMethodePurificationPage(Integer idMethode) {
        super(ManageMethodePurificationPage.class);

        boolean createMode;

        paramsDeleted = new HashMap<String, ParamMethoPuri>();

        // model initialization
        newParamModel = new Model<ParamMethoPuri>(new ParamMethoPuri());
        if (idMethode == null) {
            // creation mode
            methodePurificationModel = new Model<MethodePurification>(new MethodePurification());
            createMode = true;
        } else {
            try {
                methodePurificationModel = new Model<MethodePurification>(
                        purificationService.loadMethodePurification(idMethode));
            } catch (DataNotFoundException e) {
                LOG.error(e.getMessage(), e);
                throw new UnexpectedException(e);
            }
            createMode = false;
        }

        // bind with markup
        final Form<Void> formView = new Form<Void>("Form");

        formView.add(new TextField<String>("MethodePurification.nom", new PropertyModel<String>(
                methodePurificationModel, "nom")));
        formView.add(new TextArea<String>("MethodePurification.description", new PropertyModel<String>(
                methodePurificationModel, "description")));

        // Tableau des types en sortie
        final MarkupContainer typesEnSortieTable = new WebMarkupContainer("MethodePurification.parametres.Table");
        typesEnSortieTable.setOutputMarkupId(true);

        // Liste des types extraits ajoutés
        typesEnSortieTable.add(new ListView<ParamMethoPuri>("MethodePurification.parametres.List",
                new PropertyModel<List<ParamMethoPuri>>(methodePurificationModel, "sortedParametres")) {
            @Override
            protected void populateItem(final ListItem<ParamMethoPuri> item) {
                if (item.getIndex() % 2 == 1) {
                    item.add(new AttributeModifier("class", item.getIndex() % 2 == 0 ? "even" : "odd"));
                }

                final ParamMethoPuri param = item.getModelObject();
                // Colonnes
                item.add(new Label("MethodePurification.parametres.List.nom", new PropertyModel<String>(param, "nom")));
                item.add(new Label("MethodePurification.parametres.List.description", new PropertyModel<String>(param,
                        "description")));
                item.add(new Label("MethodePurification.parametres.List.index", new PropertyModel<String>(param,
                        "index")));

                // Action : suppression d'un type extrait
                Button deleteButton = new AjaxFallbackButton("MethodePurification.parametres.List.Delete", formView) {
                    @Override
                    protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                        // Suppression
                        methodePurificationModel.getObject().getParametres().remove(param);
                        // keep the id of the deleted 'extrait' in case of new one with the same 'initiales'
                        paramsDeleted.put(param.getNom(), param);
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

        final FormComponent<String> nomParamInput = new TextField<String>("MethodePurification.parametres.nom",
                new PropertyModel<String>(newParamModel, "nom"));
        typesEnSortieTable.add(nomParamInput);

        final FormComponent<String> descriptionParamInput = new TextField<String>(
                "MethodePurification.parametres.description", new PropertyModel<String>(newParamModel, "description"));
        typesEnSortieTable.add(descriptionParamInput);

        final FormComponent<String> indexParamInput = new TextField<String>("MethodePurification.parametres.index",
                new PropertyModel<String>(newParamModel, "index"));
        typesEnSortieTable.add(indexParamInput);

        // Bouton AJAX pour ajouter un type extrait
        Button addButton = new AjaxFallbackButton("MethodePurification.parametres.Add", formView) {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                try {
                    // normalisation du type extrait
                    newParamModel.getObject().setNom(
                            Normalizer.normalize(ConfigNameNormalizer.class, newParamModel.getObject().getNom()));
                    // Ajout du type extrait
                    newParamModel.getObject().setMethodePurification(methodePurificationModel.getObject());
                    // recuperate the id if an just deleted type
                    if (paramsDeleted.get(newParamModel.getObject().getNom()) != null) {
                        newParamModel.getObject().setIdParamMethoPuri(
                                paramsDeleted.get(newParamModel.getObject().getNom()).getIdParamMethoPuri());
                    }

                    // ajout à la liste
                    ParamMethoPuri paramAdded = newParamModel.getObject().clone();
                    methodePurificationModel.getObject().getParametres().add(paramAdded);

                    List<String> errors = validator.validate(newParamModel.getObject(), getSession().getLocale());
                    if (errors.isEmpty()) {
                        if (paramsDeleted.get(newParamModel.getObject().getNom()) != null) {
                            // si ancien extrait supprimé, on l'enlève de la liste des supprimés
                            paramsDeleted.remove(newParamModel.getObject().getNom());
                        }
                        // réinit du champ ajout
                        newParamModel.getObject().setNom(null);
                        newParamModel.getObject().setDescription(null);
                    } else {
                        methodePurificationModel.getObject().getParametres().remove(paramAdded);
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

        // Action : create the methodePurification
        Button createButton = new SubmittableButton(ACTION_CREATE, new SubmittableButtonEvents() {
            @Override
            public void onProcess() throws DataConstraintException {
                purificationService.createMethodePurification(methodePurificationModel.getObject());
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

        // Action : update the methodePurificationModel
        Button updateButton = new SubmittableButton(ACTION_UPDATE, new SubmittableButtonEvents() {
            @Override
            public void onProcess() throws DataConstraintException {
                purificationService.updateMethodePurification(methodePurificationModel.getObject());
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
                purificationService.deleteMethodePurification(methodePurificationModel.getObject());
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
        addValidationErrors(validator.validate(methodePurificationModel.getObject(), getSession().getLocale()));

        // vérifie s'il existe des paramètres supprimés qui sont tjr référencés
        for (ParamMethoPuri curDelParam : paramsDeleted.values()) {
            if (purificationService.isParamMethoPuriReferenced(curDelParam)) {
                getPage().error(
                        getString("ParamMethoPuri.isReferenced", new Model<Serializable>(
                                new Serializable[] { curDelParam.getNom() })));
            }
        }
    }
}
