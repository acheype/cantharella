/*
 * #%L
 * Cantharella :: Web
 * $Id: ManagePurificationPage.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.web/src/main/java/nc/ird/cantharella/web/pages/domain/purification/ManagePurificationPage.java $
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
package nc.ird.cantharella.web.pages.domain.purification;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import nc.ird.cantharella.data.exceptions.DataConstraintException;
import nc.ird.cantharella.data.exceptions.DataNotFoundException;
import nc.ird.cantharella.data.exceptions.UnexpectedException;
import nc.ird.cantharella.data.model.Fraction;
import nc.ird.cantharella.data.model.MethodePurification;
import nc.ird.cantharella.data.model.ParamMethoPuriEffectif;
import nc.ird.cantharella.data.model.Personne;
import nc.ird.cantharella.data.model.Produit;
import nc.ird.cantharella.data.model.Purification;
import nc.ird.cantharella.data.validation.utils.ModelValidator;
import nc.ird.cantharella.service.services.PersonneService;
import nc.ird.cantharella.service.services.ProduitService;
import nc.ird.cantharella.service.services.PurificationService;
import nc.ird.cantharella.service.utils.normalizers.UniqueFieldNormalizer;
import nc.ird.cantharella.service.utils.normalizers.utils.Normalizer;
import nc.ird.cantharella.web.pages.TemplatePage;
import nc.ird.cantharella.web.pages.domain.document.panel.ManageListDocumentsPanel;
import nc.ird.cantharella.web.pages.domain.personne.ManagePersonnePage;
import nc.ird.cantharella.web.pages.renderers.PersonneRenderer;
import nc.ird.cantharella.web.utils.CallerPage;
import nc.ird.cantharella.web.utils.behaviors.JSConfirmationBehavior;
import nc.ird.cantharella.web.utils.behaviors.ReplaceEmptyLabelBehavior;
import nc.ird.cantharella.web.utils.forms.SubmittableButton;
import nc.ird.cantharella.web.utils.forms.SubmittableButtonEvents;
import nc.ird.cantharella.web.utils.models.DisplayDecimalPropertyModel;
import nc.ird.cantharella.web.utils.models.DisplayDecimalPropertyModel.DecimalDisplFormat;
import nc.ird.cantharella.web.utils.models.DisplayPercentPropertyModel;
import nc.ird.cantharella.web.utils.panels.SimpleTooltipPanel;
import nc.ird.cantharella.web.utils.security.AuthRole;
import nc.ird.cantharella.web.utils.security.AuthRoles;
import nc.ird.cantharella.utils.BeanTools.AccessType;
import nc.ird.cantharella.utils.CollectionTools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxFallbackButton;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.extensions.markup.html.form.DateTextField;
import org.apache.wicket.extensions.yui.calendar.DatePicker;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.form.AbstractSingleSelectChoice;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
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
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.string.StringValueConversionException;

/**
 * Page for adding/update/delete a new "Purification"
 * 
 * @author Adrien Cheype
 */
@AuthRoles({ AuthRole.ADMIN, AuthRole.USER })
public final class ManagePurificationPage extends TemplatePage {

    /** Logger */
    private static final Logger LOG = LoggerFactory.getLogger(ManagePurificationPage.class);

    /** Action : create */
    private static final String ACTION_CREATE = "Create";

    /** Action : update */
    private static final String ACTION_UPDATE = "Update";

    /** Action : delete */
    private static final String ACTION_DELETE = "Delete";

    /** purification Model */
    private final IModel<Purification> purificationModel;

    /** model for adding fraction */
    private Model<Fraction> newFractionModel;

    /** Service : purifications */
    @SpringBean
    private PurificationService purificationService;

    /** Service : personnes */
    @SpringBean
    private PersonneService personneService;

    /** Service : produits */
    @SpringBean
    private ProduitService produitService;

    /** Liste des personnes existantes */
    private final List<Personne> personnes;

    /** Liste des méthodes de purification existantes */
    private final List<MethodePurification> methodes;

    /** Liste des produits existants */
    private final List<Produit> produits;

    /** Model validateur */
    @SpringBean(name = "webModelValidator")
    private ModelValidator validator;

    /** Page appelante */
    private final CallerPage callerPage;

    /** Saisie multiple */
    private boolean multipleEntry;

    /** createMode true si le formulaire est en creation, false en édition **/
    private boolean createMode;

    /**
     * Couple of <ref, id> in order to recuperate the id of the deleted type usefull in case of delete thus add of same
     * initial type
     */
    private HashMap<String, Integer> fractionsDeleted;

    /** Bouton d'ajout d'un purification **/
    Button addPurificationButton;

    /** Container pour l'affichage de la description de la méthode **/
    MarkupContainer descriptionMethoContainer;

    /** Container pour l'affichage des paramètres de la méthode **/
    MarkupContainer paramsMethoContainer;

    /**
     * Constructeur (mode création)
     * 
     * @param callerPage Page appelante
     * @param multipleEntry Saisie multiple
     */
    public ManagePurificationPage(CallerPage callerPage, boolean multipleEntry) {
        this(null, null, callerPage, multipleEntry);
    }

    /**
     * Constructeur (mode édition)
     * 
     * @param idManip Id de la manip d'purification
     * @param callerPage Page appelante
     */
    public ManagePurificationPage(Integer idManip, CallerPage callerPage) {
        this(idManip, null, callerPage, false);
    }

    /**
     * Constructeur (mode saisie de la manip suivante)
     * 
     * @param manip Manip d'purification
     * @param callerPage Page appelante
     */
    public ManagePurificationPage(Purification manip, CallerPage callerPage) {
        this(null, manip, callerPage, true);
    }

    /**
     * Constructeur. Si refManip et manip sont nuls, on créée une nouvelle manip d'purification. Si refManip est
     * renseignée, on édite la manip correspondante. Si manip est renseigné, on créée une nouvelle manipulation à partir
     * des informations qu'elle contient.
     * 
     * @param idManip Id de la manip d'purification
     * @param manip Manip d'purification
     * @param callerPage Page appelante
     * @param multipleEntry Saisie multiple
     */
    private ManagePurificationPage(Integer idManip, Purification manip, final CallerPage callerPage,
            boolean multipleEntry) {
        super(ManagePurificationPage.class);
        assert idManip == null || manip == null;
        this.callerPage = callerPage;
        final CallerPage currentPage = new CallerPage(this);
        this.multipleEntry = multipleEntry;

        fractionsDeleted = new HashMap<String, Integer>();
        newFractionModel = new Model<Fraction>(new Fraction());

        // Initialisation du modèle
        try {
            purificationModel = new Model<Purification>(idManip == null && manip == null ? new Purification()
                    : manip != null ? manip : purificationService.loadPurification(idManip));
        } catch (DataNotFoundException e) {
            LOG.error(e.getMessage(), e);
            throw new UnexpectedException(e);
        }

        createMode = idManip == null;
        if (createMode) {
            purificationModel.getObject().setCreateur(getSession().getUtilisateur());
        }

        // Initialisation des listes (pour le dropDownChoice)
        personnes = personneService.listPersonnes();
        methodes = purificationService.listMethodesPurification();
        produits = produitService.listProduits(getSession().getUtilisateur());

        if (manip != null) {
            // qd saisie multiple avec préremplissage, hack nécessaire afin d'avoir dans le model le même objet que
            // celui de la liste de choix (sinon comme les objets viennent de sessions hibernate différentes, on n'a pas
            // l'égalité entre les objets)
            purificationModel.getObject().setManipulateur(
                    CollectionTools.findWithValue(personnes, "idPersonne", AccessType.GETTER, purificationModel
                            .getObject().getManipulateur().getIdPersonne()));
        }

        // bind with markup
        final Form<Void> formView = new Form<Void>("Form");

        // initialisation du formulaire
        initPrincipalFields(formView);
        initMethodeFields(formView);
        initFractionsFields(formView);

        // add list document panel
        ManageListDocumentsPanel manageListDocumentsPanel = new ManageListDocumentsPanel("ManageListDocumentsPanel",
                purificationModel, currentPage);
        formView.add(manageListDocumentsPanel);

        // Action : create the purification
        Button createButton = new SubmittableButton(ACTION_CREATE, new SubmittableButtonEvents() {
            @Override
            public void onProcess() throws DataConstraintException {
                purificationService.createPurification(purificationModel.getObject());
            }

            @Override
            public void onSuccess() {
                successNextPage(ACTION_CREATE);
                redirect();
            }

            @Override
            public void onValidate() {
                validateModel();
            }
        });
        createButton.setVisibilityAllowed(createMode);
        formView.add(createButton);

        // Action : update the purification
        Button updateButton = new SubmittableButton(ACTION_UPDATE, new SubmittableButtonEvents() {
            @Override
            public void onProcess() throws DataConstraintException {
                purificationService.updatePurification(purificationModel.getObject());
            }

            @Override
            public void onSuccess() {
                successNextPage(ACTION_UPDATE);
                callerPage.responsePage((TemplatePage) getPage());
            }

            @Override
            public void onValidate() {
                validateModel();
            }
        });
        updateButton.setVisibilityAllowed(!createMode);
        formView.add(updateButton);

        // Action : suppression
        Button deleteButton = new SubmittableButton(ACTION_DELETE, ManagePurificationPage.class,
                new SubmittableButtonEvents() {
                    @Override
                    public void onProcess() throws DataConstraintException {
                        purificationService.deletePurification(purificationModel.getObject());
                    }

                    @Override
                    public void onSuccess() {
                        successNextPage(ACTION_DELETE);
                        callerPage.responsePage((TemplatePage) getPage());
                    }
                });
        deleteButton.setVisibilityAllowed(!createMode);
        deleteButton.setDefaultFormProcessing(false);
        deleteButton.add(new JSConfirmationBehavior(getStringModel("Confirm")));
        formView.add(deleteButton);

        formView.add(new Link<Void>("Cancel") {
            // Cas où le formulaire est annulé
            @Override
            public void onClick() {
                callerPage.responsePage((TemplatePage) getPage());
            }
        });

        formView.setDefaultButton(addPurificationButton);
        add(formView);

    }

    /**
     * Initialise les champs principaux
     * 
     * @param formView le formulaire
     */
    private void initPrincipalFields(Form<Void> formView) {
        formView.add(new TextField<String>("Purification.ref", new PropertyModel<String>(purificationModel, "ref")));

        formView.add(new DropDownChoice<Personne>("Purification.manipulateur", new PropertyModel<Personne>(
                purificationModel, "manipulateur"), personnes, new PersonneRenderer()).setNullValid(false));
        // Action : création d'une nouvelle personne
        // ajaxSubmitLink permet de sauvegarder l'état du formulaire
        formView.add(new AjaxSubmitLink("NewPersonne") {
            @Override
            protected void onSubmit(AjaxRequestTarget arg0, Form<?> arg1) {
                setResponsePage(new ManagePersonnePage(new CallerPage((TemplatePage) getPage()), false));
            }

            // si erreur, le formulaire est également enregistré puis la redirection effectuée
            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                setResponsePage(new ManagePersonnePage(new CallerPage((TemplatePage) getPage()), false));
            }
        });

        formView.add(new DateTextField("Purification.date", new PropertyModel<Date>(purificationModel, "date"))
                .add(new DatePicker()));

        AbstractSingleSelectChoice<Produit> produitsChoice = new DropDownChoice<Produit>("Purification.produit",
                new PropertyModel<Produit>(purificationModel, "produit"), produits);
        produitsChoice.setNullValid(false);
        produitsChoice.setEnabled(createMode);
        formView.add(produitsChoice);

        TextField<BigDecimal> masseDepartInput = new TextField<BigDecimal>("Purification.masseDepart",
                new PropertyModel<BigDecimal>(purificationModel, "masseDepart"));
        masseDepartInput.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            protected void onUpdate(AjaxRequestTarget target) {
            };
        });
        formView.add(masseDepartInput);

        formView.add(new TextArea<String>("Purification.complement", new PropertyModel<String>(purificationModel,
                "complement")));
        // Créateur en lecture seule
        formView.add(new TextField<String>("Purification.createur", new PropertyModel<String>(purificationModel,
                "createur")).setEnabled(false));
    }

    /**
     * Initialise les champs relatifs à la méthode
     * 
     * @param formView le formulaire
     */
    private void initMethodeFields(final Form<Void> formView) {

        final WebMarkupContainer methodeCont = new WebMarkupContainer("Purification.methode");
        methodeCont.setOutputMarkupId(true);
        formView.add(methodeCont);

        // Champs pour la méthode
        descriptionMethoContainer = new WebMarkupContainer("Purification.descriptionMethodeCont") {
            @Override
            public boolean isVisible() {
                // description cachée si pas de méthode sélectionnée
                return purificationModel.getObject().getMethode() != null;
            }
        };
        descriptionMethoContainer.setOutputMarkupId(true); // pour l'update Ajax
        descriptionMethoContainer.setOutputMarkupPlaceholderTag(true);
        methodeCont.add(descriptionMethoContainer);
        final MultiLineLabel methodeDesc = new MultiLineLabel("Purification.descriptionMethode",
                new PropertyModel<String>(purificationModel, "methode.description"));
        methodeDesc.setOutputMarkupId(true);
        descriptionMethoContainer.add(methodeDesc);

        // Déclaration du container des paramètres de la méthode
        paramsMethoContainer = new WebMarkupContainer("Purification.paramsMethode") {
            @Override
            public boolean isVisible() {
                // paramètres cachés si pas de méthode sélectionnée
                return purificationModel.getObject().getMethode() != null;
            }
        };
        final MarkupContainer paramsMethoTable = new WebMarkupContainer("Purification.paramsMethode.Table");
        paramsMethoContainer.add(paramsMethoTable);
        paramsMethoContainer.setOutputMarkupId(true);
        paramsMethoContainer.setOutputMarkupPlaceholderTag(true);

        paramsMethoTable.add(new ListView<ParamMethoPuriEffectif>("Purification.paramsMethode.List",
                new PropertyModel<List<ParamMethoPuriEffectif>>(purificationModel, "sortedParamsMetho")) {
            @Override
            protected void populateItem(ListItem<ParamMethoPuriEffectif> item) {
                if (item.getIndex() % 2 == 1) {
                    item.add(new AttributeModifier("class", item.getIndex() % 2 == 0 ? "even" : "odd"));
                }

                final ParamMethoPuriEffectif param = item.getModelObject();
                // Colonnes
                item.add(new Label("Purification.paramsMethode.nom", new PropertyModel<String>(param, "param.nom")));
                item.add(new SimpleTooltipPanel("Purification.paramsMethode.nom.info", new PropertyModel<String>(param,
                        "param.description")));
                item.add(new TextField<String>("Purification.paramsMethode.valeur", new PropertyModel<String>(param,
                        "valeur")));
            }
        });

        methodeCont.add(paramsMethoContainer);

        final DropDownChoice<MethodePurification> methodeChoice = new DropDownChoice<MethodePurification>(
                "Purification.nomMethode", new PropertyModel<MethodePurification>(purificationModel, "methode"),
                methodes);
        methodeChoice.setNullValid(false);
        // mise à jour de la description de la méthode et des fractions lors de la sélection de la méthode
        methodeChoice.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            protected void onUpdate(AjaxRequestTarget target) {
                // Intialisation des paramètres
                purificationService.initParamsMethoPuriEffectif(purificationModel.getObject());
                // mise à jour de la description et des paramètres
                target.add(methodeCont);
            }
        });
        methodeCont.add(methodeChoice);
    }

    /**
     * Initialise les champs relatifs aux fractions
     * 
     * @param formView Le formulaire
     */
    private void initFractionsFields(final Form<Void> formView) {

        // Déclaration tableau des fractions
        final MarkupContainer purificationsTable = new WebMarkupContainer("Purification.fractions.Table");
        purificationsTable.setOutputMarkupId(true);

        // Contenu tableaux fractions
        purificationsTable.add(new ListView<Fraction>("Purification.fractions.List", new PropertyModel<List<Fraction>>(
                purificationModel, "sortedFractions")) {
            @Override
            protected void populateItem(ListItem<Fraction> item) {
                if (item.getIndex() % 2 == 1) {
                    item.add(new AttributeModifier("class", item.getIndex() % 2 == 0 ? "even" : "odd"));
                }

                final Fraction fraction = item.getModelObject();
                // Colonnes
                item.add(new Label("Purification.fractions.List.indice", new PropertyModel<String>(fraction, "indice")));
                item.add(new Label("Purification.fractions.List.ref", new PropertyModel<String>(fraction, "ref")));
                item.add(new Label("Purification.fractions.List.masseObtenue", new DisplayDecimalPropertyModel(
                        fraction, "masseObtenue", DecimalDisplFormat.LARGE, getLocale())));

                item.add(new Label("Purification.fractions.List.rendement", new DisplayPercentPropertyModel(fraction,
                        "rendement", getLocale())).add(new ReplaceEmptyLabelBehavior()));

                // Action : suppression d'une fraction
                Button deleteButton = new AjaxFallbackButton("Purification.fractions.List.Delete", formView) {
                    @Override
                    protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                        // vérification si des données liées existe
                        if (produitService.isProduitReferenced(fraction)) {
                            getPage().error(getString("Fraction.isReferenced"));
                        } else {
                            // Suppression
                            purificationModel.getObject().getFractions().remove(fraction);
                            // keep the id of the deleted 'fraction' for the case of a new one with the same 'ref'
                            fractionsDeleted.put(fraction.getRef(), fraction.getId());
                        }
                        if (target != null) {
                            target.add(purificationsTable);
                            refreshFeedbackPage(target);
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

        final FormComponent<String> indiceInput = new TextField<String>("Purification.fractions.indice",
                new PropertyModel<String>(newFractionModel, "indice"));
        purificationsTable.add(indiceInput);

        final FormComponent<String> refInput = new TextField<String>("Purification.fractions.ref",
                new PropertyModel<String>(newFractionModel, "ref"));
        purificationsTable.add(refInput);

        final FormComponent<BigDecimal> masseObtenueInput = new TextField<BigDecimal>(
                "Purification.fractions.masseObtenue", new PropertyModel<BigDecimal>(newFractionModel, "masseObtenue"));
        purificationsTable.add(masseObtenueInput);

        // Bouton AJAX pour ajouter une fraction
        addPurificationButton = new AjaxFallbackButton("Purification.fractions.Add", formView) {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                try {
                    // normalisation de la fraction
                    newFractionModel.getObject().setRef(
                            Normalizer.normalize(UniqueFieldNormalizer.class, newFractionModel.getObject().getRef()));
                    // ajout du type purification
                    newFractionModel.getObject().setPurification(purificationModel.getObject());
                    // recuperate the id if an just deleted type
                    newFractionModel.getObject().setId(fractionsDeleted.get(newFractionModel.getObject().getRef()));

                    // ajout à la liste
                    Fraction fractionAdded = newFractionModel.getObject().clone();
                    purificationModel.getObject().getFractions().add(fractionAdded);

                    List<String> errors = validator.validate(newFractionModel.getObject(), getSession().getLocale());
                    // test si unicité dans la base
                    // erreur si non unique dans la base ET extrait de même réf non supprimé de la liste (dans ce
                    // dernier cas, ajout avec son ancien id) ET non pris en compte par la validation de
                    // CollectionUniqueFieldValidator
                    if (!purificationService.isFractionUnique(fractionAdded)
                            && fractionAdded.getRef() != null
                            && fractionsDeleted.get(fractionAdded.getRef()) == null
                            && CollectionTools.countWithValue(fractionAdded.getPurification().getFractions(), "ref",
                                    AccessType.GETTER, fractionAdded.getRef()) == 1) {
                        errors.add(getString("Fraction.notUnique"));
                    }

                    if (errors.isEmpty()) {
                        // réinit du champ ajout
                        newFractionModel.getObject().setIndice(null);
                        newFractionModel.getObject().setRef(null);
                        newFractionModel.getObject().setMasseObtenue(null);
                    } else {
                        purificationModel.getObject().getFractions().remove(fractionAdded);
                        addValidationErrors(errors);
                    }
                } catch (CloneNotSupportedException e) {
                    LOG.error(e.getMessage(), e);
                    throw new UnexpectedException(e);
                }

                if (target != null) {
                    target.add(purificationsTable);
                    refreshFeedbackPage(target);
                }
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                refreshFeedbackPage(target);
            }

        };
        purificationsTable.add(addPurificationButton);

        formView.add(purificationsTable);
    }

    /** {@inheritDoc} */
    @Override
    protected void onBeforeRender() {
        // On rafraichit le modèle lorsque la page est rechargée (par exemple après l'ajout d'une nouvelle entité
        // Personne ou Lot)
        refreshModel();

        super.onBeforeRender();
    }

    /**
     * Redirection vers une autre page. CAs où le formulaire est validé
     */
    private void redirect() {
        if (multipleEntry) {
            // Redirection de nouveau vers l'écran de saisie d'une nouvelle purification
            Purification nextManip = new Purification();
            nextManip.setManipulateur(purificationModel.getObject().getManipulateur());
            setResponsePage(new ManagePurificationPage(nextManip, callerPage));
        } else if (callerPage != null) {
            // On passe l'id de la purification associée à cette page, en paramètre de la prochaine page, pour lui
            // permettre de
            // l'exploiter si besoin
            callerPage.addPageParameter(Purification.class.getSimpleName(), purificationModel.getObject()
                    .getIdPurification());
            callerPage.responsePage(this);
        }
    }

    /**
     * Refresh model, appelé au rechargement de la page
     */
    private void refreshModel() {

        // Récupère (et supprime) les éventuels nouveaux objets créés dans les paramètres de la page.
        String key = Personne.class.getSimpleName();
        if (getPageParameters().getNamedKeys().contains(key)) {
            CollectionTools.setter(personnes, personneService.listPersonnes());
            try {
                Personne createdPersonne = personneService.loadPersonne(getPageParameters().get(key).toInt());
                purificationModel.getObject().setManipulateur(createdPersonne);
            } catch (StringValueConversionException e) {
                LOG.error(e.getMessage(), e);
                throw new UnexpectedException(e);
            } catch (DataNotFoundException e) {
                LOG.error(e.getMessage(), e);
                throw new UnexpectedException(e);
            }
            getPageParameters().remove(key);
        }
    }

    /**
     * Validate model
     */
    private void validateModel() {
        addValidationErrors(validator.validate(purificationModel.getObject(), getSession().getLocale()));
        if (!purificationService.isPurificationUnique(purificationModel.getObject())) {
            error(getString("Purification.notUnique"));
        }
    }
}
