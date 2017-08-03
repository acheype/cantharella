/*
 * #%L
 * Cantharella :: Web
 * $Id: ManageExtractionPage.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.web/src/main/java/nc/ird/cantharella/web/pages/domain/extraction/ManageExtractionPage.java $
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
package nc.ird.cantharella.web.pages.domain.extraction;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import nc.ird.cantharella.data.exceptions.DataConstraintException;
import nc.ird.cantharella.data.exceptions.DataNotFoundException;
import nc.ird.cantharella.data.exceptions.UnexpectedException;
import nc.ird.cantharella.data.model.Extraction;
import nc.ird.cantharella.data.model.Extrait;
import nc.ird.cantharella.data.model.Lot;
import nc.ird.cantharella.data.model.MethodeExtraction;
import nc.ird.cantharella.data.model.Personne;
import nc.ird.cantharella.data.model.TypeExtrait;
import nc.ird.cantharella.data.validation.utils.ModelValidator;
import nc.ird.cantharella.service.services.ExtractionService;
import nc.ird.cantharella.service.services.LotService;
import nc.ird.cantharella.service.services.PersonneService;
import nc.ird.cantharella.service.services.ProduitService;
import nc.ird.cantharella.service.utils.normalizers.UniqueFieldNormalizer;
import nc.ird.cantharella.service.utils.normalizers.utils.Normalizer;
import nc.ird.cantharella.web.pages.TemplatePage;
import nc.ird.cantharella.web.pages.domain.document.panel.ManageListDocumentsPanel;
import nc.ird.cantharella.web.pages.domain.lot.ManageLotPage;
import nc.ird.cantharella.web.pages.domain.personne.ManagePersonnePage;
import nc.ird.cantharella.web.pages.renderers.PersonneRenderer;
import nc.ird.cantharella.web.utils.CallerPage;
import nc.ird.cantharella.web.utils.behaviors.JSConfirmationBehavior;
import nc.ird.cantharella.web.utils.behaviors.ReplaceEmptyLabelBehavior;
import nc.ird.cantharella.web.utils.forms.SubmittableButton;
import nc.ird.cantharella.web.utils.forms.SubmittableButtonEvents;
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
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.string.StringValueConversionException;

/**
 * Page for adding/update/delete a new "Extraction"
 * 
 * @author Adrien Cheype
 */
@AuthRoles({ AuthRole.ADMIN, AuthRole.USER })
public final class ManageExtractionPage extends TemplatePage {

    /** Action : create */
    private static final String ACTION_CREATE = "Create";

    /** Action : update */
    private static final String ACTION_UPDATE = "Update";

    /** Action : delete */
    private static final String ACTION_DELETE = "Delete";

    /** Logger */
    private static final Logger LOG = LoggerFactory.getLogger(ManageExtractionPage.class);

    /** extraction Model */
    private final IModel<Extraction> extractionModel;

    /** model for adding Extrait */
    private IModel<Extrait> newExtraitModel;

    /** Service : extraits */
    @SpringBean
    private ExtractionService extractionService;

    /** Service : personnes */
    @SpringBean
    private PersonneService personneService;

    /** Service : produits */
    @SpringBean
    private ProduitService produitService;

    /** Service : lots */
    @SpringBean
    private LotService lotService;

    /** Liste des personnes existantes */
    private final List<Personne> personnes;

    /** Liste des méthodes d'extraction existantes */
    private final List<MethodeExtraction> methodes;

    /** Liste des types d'extraits issue de la méthode sélectionnée */
    private List<TypeExtrait> typesExtrait;

    /** Liste des lots existants */
    private List<Lot> lots;

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
    private HashMap<String, Integer> extraitsDeleted;

    /** Container pour la table des extraits (ajoutés, et saisie) */
    private MarkupContainer extraitsTable;

    /** Saisie typeExtrait pour l'ajout d'un extrait */
    private AbstractSingleSelectChoice<TypeExtrait> typeExtraitInput;

    /** Info-bulle comprenant la description du type d'extrait **/
    private SimpleTooltipPanel typeExtraitTooltip;

    /** Saisie ref pour l'ajout d'un extrait */
    private FormComponent<String> refInput;

    /** Saisie masse pour l'ajout d'un extrait */
    private FormComponent<BigDecimal> masseObtenueInput;

    /** Bouton d'ajout d'un extrait **/
    private Button addExtraitButton;

    /** Pattern pour le renseignement automatique des réf d'extrait */
    private static final String PATTERN_REF_EXTRAIT = "%s%s";

    /**
     * Constructeur (mode création)
     * 
     * @param callerPage Page appelante
     * @param multipleEntry Saisie multiple
     */
    public ManageExtractionPage(CallerPage callerPage, boolean multipleEntry) {
        this(null, null, callerPage, multipleEntry);
    }

    /**
     * Constructeur (mode édition)
     * 
     * @param idManip Id de la manip d'extraction
     * @param callerPage Page appelante
     */
    public ManageExtractionPage(Integer idManip, CallerPage callerPage) {
        this(idManip, null, callerPage, false);
    }

    /**
     * Constructeur (mode saisie de la manip suivante)
     * 
     * @param manip Manip d'extraction
     * @param callerPage Page appelante
     */
    public ManageExtractionPage(Extraction manip, CallerPage callerPage) {
        this(null, manip, callerPage, true);
    }

    /**
     * Constructeur. Si refManip et manip sont nuls, on créée une nouvelle manip d'extraction. Si refManip est
     * renseignée, on édite la manip correspondante. Si manip est renseigné, on créée une nouvelle manipulation à partir
     * des informations qu'elle contient.
     * 
     * @param idManip Id de la manip d'extraction
     * @param manip Manip d'extraction
     * @param callerPage Page appelante
     * @param multipleEntry Saisie multiple
     */
    private ManageExtractionPage(Integer idManip, Extraction manip, final CallerPage callerPage, boolean multipleEntry) {
        super(ManageExtractionPage.class);
        assert idManip == null || manip == null;
        this.callerPage = callerPage;
        final CallerPage currentPage = new CallerPage(this);
        this.multipleEntry = multipleEntry;

        extraitsDeleted = new HashMap<String, Integer>();
        newExtraitModel = new Model<Extrait>(new Extrait());

        // Initialisation du modèle
        try {
            extractionModel = new Model<Extraction>(idManip == null && manip == null ? new Extraction()
                    : manip != null ? manip : extractionService.loadExtraction(idManip));
        } catch (DataNotFoundException e) {
            LOG.error(e.getMessage(), e);
            throw new UnexpectedException(e);
        }

        createMode = idManip == null;
        if (createMode) {
            extractionModel.getObject().setCreateur(getSession().getUtilisateur());
        }

        // Initialisation des listes (pour le dropDownChoice)
        personnes = personneService.listPersonnes();
        methodes = extractionService.listMethodesExtraction();
        lots = lotService.listLots(getSession().getUtilisateur());

        if (manip != null) {
            // qd saisie multiple avec préremplissage, hack nécessaire afin d'avoir dans le model le même objet que
            // celui de la liste de choix (sinon comme les objets viennent de sessions hibernate différentes, on n'a pas
            // l'égalité entre les objets)
            extractionModel.getObject().setManipulateur(
                    CollectionTools.findWithValue(personnes, "idPersonne", AccessType.GETTER, extractionModel
                            .getObject().getManipulateur().getIdPersonne()));
            extractionModel.getObject().setMethode(
                    CollectionTools.findWithValue(methodes, "idMethodeExtraction", AccessType.GETTER, extractionModel
                            .getObject().getMethode().getIdMethodeExtraction()));
        }

        typesExtrait = new ArrayList<TypeExtrait>();

        // bind with markup
        final Form<Void> formView = new Form<Void>("Form");

        initPrincipalFields(formView);

        initMethodeFields(formView);

        initExtraitsFields(formView);

        // add list document panel
        ManageListDocumentsPanel manageListDocumentsPanel = new ManageListDocumentsPanel("ManageListDocumentsPanel",
                extractionModel, currentPage);
        formView.add(manageListDocumentsPanel);

        // Action : create the extraction
        Button createButton = new SubmittableButton(ACTION_CREATE, new SubmittableButtonEvents() {
            @Override
            public void onProcess() throws DataConstraintException {
                extractionService.createExtraction(extractionModel.getObject());
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

        // Action : update the extraction
        Button updateButton = new SubmittableButton(ACTION_UPDATE, ManageExtractionPage.class,
                new SubmittableButtonEvents() {
                    @Override
                    public void onProcess() throws DataConstraintException {
                        extractionService.updateExtraction(extractionModel.getObject());
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
        Button deleteButton = new SubmittableButton(ACTION_DELETE, new SubmittableButtonEvents() {
            @Override
            public void onProcess() throws DataConstraintException {
                extractionService.deleteExtraction(extractionModel.getObject());
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

        formView.setDefaultButton(addExtraitButton);
        add(formView);
    }

    /**
     * Initialise les champs principaux
     * 
     * @param formView le formulaire
     */
    private void initPrincipalFields(final Form<Void> formView) {
        formView.add(new TextField<String>("Extraction.ref", new PropertyModel<String>(extractionModel, "ref")));

        DropDownChoice<Personne> pers = new DropDownChoice<Personne>("Extraction.manipulateur",
                new PropertyModel<Personne>(extractionModel, "manipulateur"), personnes, new PersonneRenderer());
        pers.setNullValid(false);

        pers.setModelObject(extractionModel.getObject().getManipulateur());
        formView.add(pers);
        pers.getChoices().indexOf(pers.getModelObject());
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

        formView.add(new DateTextField("Extraction.date", new PropertyModel<Date>(extractionModel, "date"))
                .add(new DatePicker()));

        final AbstractSingleSelectChoice<Lot> lotsChoice = new DropDownChoice<Lot>("Extraction.lot",
                new PropertyModel<Lot>(extractionModel, "lot"), lots);
        lotsChoice.setNullValid(false);
        lotsChoice.setEnabled(createMode);
        lotsChoice.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            protected void onUpdate(AjaxRequestTarget target) {
                updateRefExtrait(target);
            }
        });
        formView.add(lotsChoice);

        // Action : création d'un nouveau lot
        // ajaxSubmitLink permet de sauvegarder l'état du formulaire
        AbstractLink newLot = new AjaxSubmitLink("NewLot") {
            @Override
            protected void onSubmit(AjaxRequestTarget arg0, Form<?> arg1) {
                setResponsePage(new ManageLotPage(new CallerPage((TemplatePage) getPage()), false));
            }

            // si erreur, le formulaire est également enregistré puis la redirection effectuée
            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                setResponsePage(new ManageLotPage(new CallerPage((TemplatePage) getPage()), false));
            }
        };
        newLot.setOutputMarkupPlaceholderTag(true);
        newLot.setVisibilityAllowed(createMode);
        formView.add(newLot);

        TextField<BigDecimal> masseDepartInput = new TextField<BigDecimal>("Extraction.masseDepart",
                new PropertyModel<BigDecimal>(extractionModel, "masseDepart"));
        masseDepartInput.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            protected void onUpdate(AjaxRequestTarget target) {
            };
        });
        formView.add(masseDepartInput);

        formView.add(new TextArea<String>("Extraction.complement", new PropertyModel<String>(extractionModel,
                "complement")));
        // Créateur en lecture seule
        formView.add(new TextField<String>("Extraction.createur",
                new PropertyModel<String>(extractionModel, "createur")).setEnabled(false));
    }

    /**
     * Initialise les champs relatifs à la méthode
     * 
     * @param formView le formulaire
     */
    private void initMethodeFields(final Form<Void> formView) {
        // Champs méthode

        final WebMarkupContainer descriptionMethoContainer = new WebMarkupContainer("TestBio.descriptionMethodeCont") {
            @Override
            public boolean isVisible() {
                // description cachée si pas de méthode sélectionnée
                return extractionModel.getObject().getMethode() != null;
            }
        };
        descriptionMethoContainer.setOutputMarkupId(true); // pour l'update Ajax
        descriptionMethoContainer.setOutputMarkupPlaceholderTag(true); // pour accéder à l'élement html qd son état est
        // non visible
        formView.add(descriptionMethoContainer);

        descriptionMethoContainer.add(new MultiLineLabel("Extraction.methode.description", new PropertyModel<String>(
                extractionModel, "methode.description")));

        final DropDownChoice<MethodeExtraction> methodeChoice = new DropDownChoice<MethodeExtraction>(
                "Extraction.methode.nom", new PropertyModel<MethodeExtraction>(extractionModel, "methode"), methodes);
        methodeChoice.setNullValid(false);
        methodeChoice.setOutputMarkupId(true); // pour l'update Ajax
        // mise à jour de la description de la méthode et des extraits lors de la sélection de la méthode
        methodeChoice.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            protected void onUpdate(AjaxRequestTarget target) {
                extractionModel.getObject().getExtraits().clear();
                // maj de la liste des typesExtraits suivant la sélection
                typesExtrait.clear();
                if (extractionModel.getObject().getMethode() != null) {
                    // rafraichi la méthode pour pouvoir accéder aux types en sortie accédés en LAZY
                    extractionService.refreshMethodeExtraction(extractionModel.getObject().getMethode());
                    typesExtrait.addAll(extractionModel.getObject().getMethode().getSortedTypesEnSortie());
                }
                // mise à jour de typesExtrait
                updateTypesExtrait(true, target);

                target.add(methodeChoice, descriptionMethoContainer);
            }
        });
        formView.add(methodeChoice);
    }

    /**
     * Initialise les champs relatifs aux extraits
     * 
     * @param formView le formulaire
     */
    private void initExtraitsFields(final Form<Void> formView) {

        extraitsTable = new WebMarkupContainer("Extraction.extraits.Table");
        // Contenu tableaux extrait
        // Liste des types extraits ajoutés (pour raffraichissements)
        extraitsTable.add(new ListView<Extrait>("Extraction.extraits.List", new PropertyModel<List<Extrait>>(
                extractionModel, "sortedExtraits")) {
            @Override
            protected void populateItem(ListItem<Extrait> item) {
                if (item.getIndex() % 2 == 1) {
                    item.add(new AttributeModifier("class", item.getIndex() % 2 == 0 ? "even" : "odd"));
                }

                IModel<Extrait> extraitModel = item.getModel();
                final Extrait extrait = item.getModelObject();
                // Colonnes
                item.add(new Label("Extraction.extraits.List.typeExtrait", new PropertyModel<String>(extrait,
                        "typeExtrait")));
                // info-bulle comprenant la description du type d'extrait
                item.add(new SimpleTooltipPanel("Extraction.extraits.List.typeExtrait.info", new PropertyModel<String>(
                        extrait, "typeExtrait.description")));
                item.add(new Label("Extraction.extraits.List.ref", new PropertyModel<String>(extrait, "ref")));
                item.add(new Label("Extraction.extraits.List.masseObtenue", new PropertyModel<String>(extrait,
                        "masseObtenue")));

                item.add(new Label("Extraction.extraits.List.rendement", new DisplayPercentPropertyModel(extraitModel,
                        "rendement", getLocale())).add(new ReplaceEmptyLabelBehavior()));

                // Action : suppression d'un extrait
                Button deleteButton = new AjaxFallbackButton("Extraction.extraits.List.Delete", formView) {
                    @Override
                    protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                        // vérification si des données liées existe
                        if (produitService.isProduitReferenced(extrait)) {
                            getPage().error(getString("Extrait.isReferenced"));
                        } else {
                            // Suppression
                            extractionModel.getObject().getExtraits().remove(extrait);
                            // keep the id of the deleted 'extrait' for the case of a new one with the same 'ref'
                            extraitsDeleted.put(extrait.getRef(), extrait.getId());

                            // mise à jour de l'extrait
                            updateTypesExtrait(false, target);
                        }
                        if (target != null) {
                            target.add(extraitsTable);
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
        extraitsTable.setOutputMarkupId(true);

        typeExtraitInput = new DropDownChoice<TypeExtrait>("Extraction.extraits.typeExtrait",
                new PropertyModel<TypeExtrait>(newExtraitModel, "typeExtrait"), typesExtrait).setNullValid(false);
        // info-bulle comprenant la description du type d'extrait
        typeExtraitTooltip = new SimpleTooltipPanel("Extraction.extraits.typeExtrait.info", new PropertyModel<String>(
                newExtraitModel, "typeExtrait.description"));
        // permet la mise en visibité ou non en Ajax
        typeExtraitTooltip.setOutputMarkupId(true);
        typeExtraitTooltip.setOutputMarkupPlaceholderTag(true);
        extraitsTable.add(typeExtraitTooltip);

        typeExtraitInput.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            protected void onUpdate(AjaxRequestTarget target) {
                updateRefExtrait(target);
                target.add(typeExtraitTooltip);
            }
        });
        extraitsTable.add(typeExtraitInput);

        refInput = new TextField<String>("Extraction.extraits.ref", new PropertyModel<String>(newExtraitModel, "ref"));
        refInput.setOutputMarkupId(true);
        extraitsTable.add(refInput);

        masseObtenueInput = new TextField<BigDecimal>("Extraction.extraits.masseObtenue",
                new PropertyModel<BigDecimal>(newExtraitModel, "masseObtenue"));
        masseObtenueInput.setOutputMarkupId(true);
        extraitsTable.add(masseObtenueInput);

        // Bouton AJAX pour ajouter un extrait
        addExtraitButton = new AjaxFallbackButton("Extraction.extraits.Add", formView) {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                try {
                    // normalisation de l'extrait
                    newExtraitModel.getObject().setRef(
                            Normalizer.normalize(UniqueFieldNormalizer.class, newExtraitModel.getObject().getRef()));
                    // Ajout du type extrait
                    newExtraitModel.getObject().setExtraction(extractionModel.getObject());
                    // recuperate the id if an just deleted type, null otherwise
                    newExtraitModel.getObject().setId(extraitsDeleted.get(newExtraitModel.getObject().getRef()));

                    // ajout à la liste
                    Extrait extraitAdded = newExtraitModel.getObject().clone();
                    extractionModel.getObject().getExtraits().add(extraitAdded);

                    List<String> errors = validator.validate(newExtraitModel.getObject(), getSession().getLocale());
                    // test si unicité dans la base
                    // erreur si non unique dans la base ET extrait de même réf non supprimé de la liste (dans ce
                    // dernier cas, ajout avec son ancien id) ET non pris en compte par la validation de
                    // CollectionUniqueFieldValidator
                    if (!extractionService.isExtraitUnique(extraitAdded)
                            && extraitAdded.getRef() != null
                            && extraitsDeleted.get(extraitAdded.getRef()) == null
                            && CollectionTools.countWithValue(extraitAdded.getExtraction().getExtraits(), "ref",
                                    AccessType.GETTER, extraitAdded.getRef()) == 1) {
                        errors.add(getString("Extrait.notUnique"));
                    }

                    if (errors.isEmpty()) {
                        // réinit du champ ajout
                        newExtraitModel.getObject().setRef(null);
                        newExtraitModel.getObject().setMasseObtenue(null);

                        // mise à jour de typesExtrait
                        updateTypesExtrait(true, target);
                    } else {
                        extractionModel.getObject().getExtraits().remove(extraitAdded);
                        addValidationErrors(errors);
                    }
                } catch (CloneNotSupportedException e) {
                    LOG.error(e.getMessage(), e);
                    throw new UnexpectedException(e);
                }

                if (target != null) {
                    target.add(extraitsTable);
                    refreshFeedbackPage(target);
                }
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                refreshFeedbackPage(target);
            }

        };
        addExtraitButton.setOutputMarkupId(true);
        extraitsTable.add(addExtraitButton);

        formView.add(extraitsTable);
        updateTypesExtrait(true, null);
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
     * Redirection vers une autre page. Cas où le formulaire est validé
     */
    private void redirect() {
        if (multipleEntry) {
            // Redirection de nouveau vers l'écran de saisie d'une nouvelle extraction
            Extraction nextManip = new Extraction();
            nextManip.setManipulateur(extractionModel.getObject().getManipulateur());
            nextManip.setMethode(extractionModel.getObject().getMethode());
            nextManip.setMasseDepart(extractionModel.getObject().getMasseDepart());
            setResponsePage(new ManageExtractionPage(nextManip, callerPage));
        } else if (callerPage != null) {
            // On passe l'id de l'extraction associée à cette page, en paramètre de la prochaine page, pour lui
            // permettre de
            // l'exploiter si besoin
            callerPage
                    .addPageParameter(Extraction.class.getSimpleName(), extractionModel.getObject().getIdExtraction());
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
                extractionModel.getObject().setManipulateur(createdPersonne);
            } catch (StringValueConversionException e) {
                LOG.error(e.getMessage(), e);
                throw new UnexpectedException(e);
            } catch (DataNotFoundException e) {
                LOG.error(e.getMessage(), e);
                throw new UnexpectedException(e);
            }
            getPageParameters().remove(key);
        }

        key = Lot.class.getSimpleName();
        if (getPageParameters().getNamedKeys().contains(key)) {
            CollectionTools.setter(lots, lotService.listLots(getSession().getUtilisateur()));
            try {
                Lot createdLot = lotService.loadLot(getPageParameters().get(key).toInt());
                extractionModel.getObject().setLot(createdLot);
            } catch (StringValueConversionException e) {
                LOG.error(e.getMessage(), e);
                throw new UnexpectedException(e);
            } catch (DataNotFoundException e) {
                LOG.error(e.getMessage(), e);
                throw new UnexpectedException(e);
            }
            getPageParameters().remove(key);
        }

        // mise à jour du champ réf d'un nouvel extrait au cas où retour sur le formulaire avec un nouveau lot
        // sélectionné
        updateRefExtrait(null);
    }

    /**
     * Update the extrait reference with an auto-generated value (ref and typeExtrait concatenated)
     * 
     * @param target An AjaxRequestTarget, null if not a ajax request
     */
    private void updateRefExtrait(AjaxRequestTarget target) {
        if (extractionModel.getObject().getLot() != null && newExtraitModel.getObject().getTypeExtrait() != null) {
            // met à jour la réf de l'extrait suivant celle du lot et le type d'extrait
            newExtraitModel.getObject().setRef(
                    String.format(PATTERN_REF_EXTRAIT, extractionModel.getObject().getLot().getRef(), newExtraitModel
                            .getObject().getTypeExtrait().getInitiales()));
            if (target != null) {
                target.add(refInput);
            }
        }
    }

    /**
     * Met à jour la liste des types extrait (suivant la méthode et les extraits déjà saisis)
     * 
     * @param target An AjaxRequestTarget, null if not a ajax request
     * @param selectFirstTypeExtrait If the first element of the TypeExtraits combobox need to be selected
     */
    private void updateTypesExtrait(boolean selectFirstTypeExtrait, AjaxRequestTarget target) {
        if (extractionModel.getObject().getMethode() != null) {
            CollectionTools.setter(typesExtrait, extractionModel.getObject().getMethode().getSortedTypesEnSortie());
            for (Extrait curExtrait : extractionModel.getObject().getExtraits()) {
                typesExtrait.remove(curExtrait.getTypeExtrait());
            }
        }
        configureExtraitInputs();

        if (selectFirstTypeExtrait) {
            // Si liste des types extraits non vide, sélection du premier élement
            newExtraitModel.getObject().setTypeExtrait(
                    typeExtraitInput.getChoices().isEmpty() ? null : typeExtraitInput.getChoices().get(0));
        }

        updateRefExtrait(target);

        if (target != null) {
            target.add(typeExtraitInput, typeExtraitTooltip, refInput, masseObtenueInput, addExtraitButton);
        }
    }

    /**
     * Configure les composants pour la saisie d'extraits
     */
    private void configureExtraitInputs() {
        // désactivation de la saisie d'un extrait si pas de méthode ou si déjà un extrait par type d'extrait
        if (extractionModel.getObject().getMethode() == null
                || extractionModel.getObject().getExtraits() != null
                && extractionModel.getObject().getExtraits().size() == extractionModel.getObject().getMethode()
                        .getTypesEnSortie().size()) {
            disableExtraitAdding();
        } else {
            enableExtraitAdding();
        }
    }

    /**
     * Active la saisie pour ajouter un nouvel extrait
     */
    private void enableExtraitAdding() {
        typeExtraitInput.setEnabled(true);
        typeExtraitTooltip.setVisibilityAllowed(true);
        refInput.setEnabled(true);
        masseObtenueInput.setEnabled(true);
        addExtraitButton.setEnabled(true);
    }

    /**
     * Désactive la saisie pour ajouter un nouvel extrait
     */
    private void disableExtraitAdding() {
        typeExtraitInput.setEnabled(false);
        typeExtraitTooltip.setVisibilityAllowed(false);
        refInput.setEnabled(false);
        masseObtenueInput.setEnabled(false);
        addExtraitButton.setEnabled(false);
    }

    /**
     * Validate model
     */
    private void validateModel() {
        addValidationErrors(validator.validate(extractionModel.getObject(), getSession().getLocale()));
        if (!extractionService.isExtractionUnique(extractionModel.getObject())) {
            error(getString("Extraction.notUnique"));
        }
    }
}
