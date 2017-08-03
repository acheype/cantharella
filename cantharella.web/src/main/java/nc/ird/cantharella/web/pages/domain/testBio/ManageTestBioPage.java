/*
 * #%L
 * Cantharella :: Web
 * $Id: ManageTestBioPage.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.web/src/main/java/nc/ird/cantharella/web/pages/domain/testBio/ManageTestBioPage.java $
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
package nc.ird.cantharella.web.pages.domain.testBio;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import nc.ird.cantharella.data.exceptions.DataConstraintException;
import nc.ird.cantharella.data.exceptions.DataNotFoundException;
import nc.ird.cantharella.data.exceptions.UnexpectedException;
import nc.ird.cantharella.data.model.ErreurTestBio;
import nc.ird.cantharella.data.model.MethodeTestBio;
import nc.ird.cantharella.data.model.Personne;
import nc.ird.cantharella.data.model.Produit;
import nc.ird.cantharella.data.model.ResultatTestBio;
import nc.ird.cantharella.data.model.ResultatTestBio.Stade;
import nc.ird.cantharella.data.model.ResultatTestBio.TypeResultat;
import nc.ird.cantharella.data.model.ResultatTestBio.UniteConcMasse;
import nc.ird.cantharella.data.model.TestBio;
import nc.ird.cantharella.data.model.Utilisateur;
import nc.ird.cantharella.data.validation.utils.ModelValidator;
import nc.ird.cantharella.service.services.PersonneService;
import nc.ird.cantharella.service.services.ProduitService;
import nc.ird.cantharella.service.services.TestBioService;
import nc.ird.cantharella.service.utils.normalizers.UniqueFieldNormalizer;
import nc.ird.cantharella.service.utils.normalizers.utils.Normalizer;
import nc.ird.cantharella.web.pages.TemplatePage;
import nc.ird.cantharella.web.pages.domain.document.panel.ManageListDocumentsPanel;
import nc.ird.cantharella.web.pages.domain.personne.ManagePersonnePage;
import nc.ird.cantharella.web.pages.renderers.PersonneRenderer;
import nc.ird.cantharella.web.utils.CallerPage;
import nc.ird.cantharella.web.utils.behaviors.JSConfirmationBehavior;
import nc.ird.cantharella.web.utils.behaviors.ReplaceEmptyLabelBehavior;
import nc.ird.cantharella.web.utils.forms.AutoCompleteTextFieldString;
import nc.ird.cantharella.web.utils.forms.AutoCompleteTextFieldString.ComparisonMode;
import nc.ird.cantharella.web.utils.forms.SubmittableButton;
import nc.ird.cantharella.web.utils.forms.SubmittableButtonEvents;
import nc.ird.cantharella.web.utils.models.DisplayBooleanPropertyModel;
import nc.ird.cantharella.web.utils.models.DisplayDecimalPropertyModel;
import nc.ird.cantharella.web.utils.models.DisplayDecimalPropertyModel.DecimalDisplFormat;
import nc.ird.cantharella.web.utils.models.DisplayEnumPropertyModel;
import nc.ird.cantharella.web.utils.panels.SimpleTooltipPanel;
import nc.ird.cantharella.web.utils.renderers.EnumChoiceRenderer;
import nc.ird.cantharella.web.utils.security.AuthRole;
import nc.ird.cantharella.web.utils.security.AuthRoles;
import nc.ird.cantharella.utils.BeanTools.AccessType;
import nc.ird.cantharella.utils.CollectionTools;

import org.apache.commons.lang3.StringUtils;
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
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
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
 * Page for adding/update/delete a new "Test biologique"
 * 
 * @author Adrien Cheype
 */
@AuthRoles({ AuthRole.ADMIN, AuthRole.USER })
public final class ManageTestBioPage extends TemplatePage {

    /** Logger */
    private static final Logger LOG = LoggerFactory.getLogger(ManageTestBioPage.class);

    /** Action : create */
    private static final String ACTION_CREATE = "Create";

    /** Action : update */
    private static final String ACTION_UPDATE = "Update";

    /** Action : delete */
    private static final String ACTION_DELETE = "Delete";

    /** testBio Model */
    private final IModel<TestBio> testBioModel;

    /** model for adding resultat */
    private Model<ResultatTestBio> newResultatModel;

    /** Service : testBios */
    @SpringBean
    private TestBioService testBioService;

    /** Service : personnes */
    @SpringBean
    private PersonneService personneService;

    /** Service : produits */
    @SpringBean
    private ProduitService produitService;

    /** Liste des personnes existantes */
    private final List<Personne> personnes;

    /** Liste des méthodes de testBio existantes */
    private final List<MethodeTestBio> methodes;

    /** Liste des organismes testeurs déjà renseignés pour les personnes */
    private final List<String> organismes;

    /** Liste des produits témoins testeurs déjà renseignés pour résultats de test */
    private final List<String> produitsTemoins;

    /** Liste des produits existants */
    private final List<Produit> produits;

    /** Liste des erreurs de test existantes */
    private final List<ErreurTestBio> erreurs;

    /** Model validateur */
    @SpringBean(name = "webModelValidator")
    private ModelValidator validator;

    /** Page appelante */
    private final CallerPage callerPage;

    /** Saisie multiple */
    private boolean multipleEntry;

    /** Bouton d'ajout d'un résultat de test bio **/
    Button addResultatButton;

    /** Container pour l'affichage de la description de la méthode **/
    MarkupContainer descriptionMethoContainer;

    /** Container pour l'affichage des paramètres de la méthode **/
    MarkupContainer paramsMethoContainer;

    /** Input du conc./masse par défaut du test bio **/
    TextField<BigDecimal> concMasseDefautInput;

    /** Input pour l'unité de conc./masse par défaut du test bio **/
    DropDownChoice<UniteConcMasse> uniteConcMasseDefautInput;

    /** Input du conc./masse du résultat courant **/
    TextField<BigInteger> concMasseInput;

    /** Input pour l'unité de conc./masse du résultat courant **/
    DropDownChoice<UniteConcMasse> uniteConcMasseInput;

    /** Input pour le stade du résultat courant **/
    DropDownChoice<Stade> stadeInput;

    /**
     * Constructeur (mode création)
     * 
     * @param callerPage Page appelante
     * @param multipleEntry Saisie multiple
     */
    public ManageTestBioPage(CallerPage callerPage, boolean multipleEntry) {
        this(null, null, callerPage, multipleEntry);
    }

    /**
     * Constructeur (mode édition)
     * 
     * @param idManip Id de la manip d'testBio
     * @param callerPage Page appelante
     */
    public ManageTestBioPage(Integer idManip, CallerPage callerPage) {
        this(idManip, null, callerPage, false);
    }

    /**
     * Constructeur (mode saisie de la manip suivante)
     * 
     * @param manip Manip d'testBio
     * @param callerPage Page appelante
     */
    public ManageTestBioPage(TestBio manip, CallerPage callerPage) {
        this(null, manip, callerPage, true);
    }

    /**
     * Constructeur. Si refManip et manip sont nuls, on créée une nouvelle manip d'testBio. Si refManip est renseignée,
     * on édite la manip correspondante. Si manip est renseigné, on créée une nouvelle manipulation à partir des
     * informations qu'elle contient.
     * 
     * @param idManip Id de la manip d'testBio
     * @param manip Manip d'testBio
     * @param callerPage Page appelante
     * @param multipleEntry Saisie multiple
     */
    private ManageTestBioPage(Integer idManip, TestBio manip, final CallerPage callerPage, boolean multipleEntry) {
        super(ManageTestBioPage.class);
        assert idManip == null || manip == null;
        this.callerPage = callerPage;
        final CallerPage currentPage = new CallerPage(this);
        this.multipleEntry = multipleEntry;

        newResultatModel = new Model<ResultatTestBio>(new ResultatTestBio());

        // Initialisation du modèle
        try {
            testBioModel = new Model<TestBio>(idManip == null && manip == null ? new TestBio() : manip != null ? manip
                    : testBioService.loadTestBio(idManip));
        } catch (DataNotFoundException e) {
            LOG.error(e.getMessage(), e);
            throw new UnexpectedException(e);
        }
        boolean createMode = idManip == null;
        if (createMode) {
            testBioModel.getObject().setCreateur(getSession().getUtilisateur());
        }

        // Initialisation des listes (pour le dropDownChoice)
        personnes = personneService.listPersonnes();
        methodes = testBioService.listMethodesTestBio();
        produits = produitService.listProduits((Utilisateur) getSession().getUtilisateur());
        erreurs = testBioService.listErreursTestBio();

        if (manip != null) {
            // qd saisie multiple avec préremplissage, hack nécessaire afin d'avoir dans le model le même objet que
            // celui de la liste de choix (sinon comme les objets viennent de sessions hibernate différentes, on n'a pas
            // l'égalité entre les objets)
            testBioModel.getObject().setManipulateur(
                    CollectionTools.findWithValue(personnes, "idPersonne", AccessType.GETTER, testBioModel.getObject()
                            .getManipulateur().getIdPersonne()));
            testBioModel.getObject().setMethode(
                    CollectionTools.findWithValue(methodes, "idMethodeTest", AccessType.GETTER, testBioModel
                            .getObject().getMethode().getIdMethodeTest()));
        }

        // liste des organismes suggérés à la saisie
        organismes = personneService.listPersonneOrganismes();
        produitsTemoins = testBioService.listProduitsTemoins();

        // bind with markup
        final Form<Void> formView = new Form<Void>("Form");

        // initialisation du formulaire
        initPrincipalFields(formView);
        initMethodeFields(formView);
        initResultatsFields(formView);

        // add list document panel
        ManageListDocumentsPanel manageListDocumentsPanel = new ManageListDocumentsPanel("ManageListDocumentsPanel",
                testBioModel, currentPage);
        formView.add(manageListDocumentsPanel);

        // Action : create the testBio
        Button createButton = new SubmittableButton(ACTION_CREATE, new SubmittableButtonEvents() {
            @Override
            public void onProcess() throws DataConstraintException {
                testBioService.createTestBio(testBioModel.getObject());
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

        // Action : update the testBio
        Button updateButton = new SubmittableButton(ACTION_UPDATE, new SubmittableButtonEvents() {
            @Override
            public void onProcess() throws DataConstraintException {
                testBioService.updateTestBio(testBioModel.getObject());
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
                testBioService.deleteTestBio(testBioModel.getObject());
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

        formView.setDefaultButton(addResultatButton);
        add(formView);

    }

    /**
     * Initialise les champs principaux
     * 
     * @param formView le formulaire
     */
    private void initPrincipalFields(Form<Void> formView) {
        formView.add(new TextField<String>("TestBio.ref", new PropertyModel<String>(testBioModel, "ref")));

        formView.add(new DropDownChoice<Personne>("TestBio.manipulateur", new PropertyModel<Personne>(testBioModel,
                "manipulateur"), personnes, new PersonneRenderer()).setNullValid(false));

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

        formView.add(new AutoCompleteTextFieldString("TestBio.organismeTesteur", new PropertyModel<String>(
                testBioModel, "organismeTesteur"), organismes, ComparisonMode.CONTAINS));

        formView.add(new DateTextField("TestBio.date", new PropertyModel<Date>(testBioModel, "date"))
                .add(new DatePicker()));

        concMasseDefautInput = new TextField<BigDecimal>("TestBio.concMasseDefaut", new PropertyModel<BigDecimal>(
                testBioModel, "concMasseDefaut"));
        concMasseDefautInput.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            protected void onUpdate(AjaxRequestTarget target) {
                // maj du champ concMasse du résultat si résultat de type produit ou témoin
                if (newResultatModel.getObject().getTypeResultat() == TypeResultat.PRODUIT
                        || newResultatModel.getObject().getTypeResultat() == TypeResultat.TEMOIN) {
                    newResultatModel.getObject().setConcMasse(concMasseDefautInput.getModelObject());
                    target.add(concMasseInput);
                }
            };
        });
        formView.add(concMasseDefautInput);

        uniteConcMasseDefautInput = new DropDownChoice<UniteConcMasse>("TestBio.uniteConcMasseDefaut",
                new PropertyModel<UniteConcMasse>(testBioModel, "uniteConcMasseDefaut"), Arrays.asList(UniteConcMasse
                        .values()), new EnumChoiceRenderer<UniteConcMasse>(this));
        uniteConcMasseDefautInput.setNullValid(true);

        uniteConcMasseDefautInput.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            protected void onUpdate(AjaxRequestTarget target) {
                // maj du champ uniteConcMasse du résultat si résultat de type produit
                if (newResultatModel.getObject().getTypeResultat() == TypeResultat.PRODUIT
                        || newResultatModel.getObject().getTypeResultat() == TypeResultat.TEMOIN) {
                    newResultatModel.getObject().setUniteConcMasse(uniteConcMasseDefautInput.getModelObject());
                    target.add(uniteConcMasseInput);
                }
            }
        });
        formView.add(uniteConcMasseDefautInput);

        final AbstractSingleSelectChoice<Stade> stadeDefautInput = new DropDownChoice<Stade>("TestBio.stadeDefaut",
                new PropertyModel<Stade>(testBioModel, "stadeDefaut"), Arrays.asList(Stade.values()),
                new EnumChoiceRenderer<Stade>(this));
        stadeDefautInput.setNullValid(true);

        stadeDefautInput.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            protected void onUpdate(AjaxRequestTarget target) {
                // maj du champ stade du résultat si résultat de type produit
                if (newResultatModel.getObject().getTypeResultat() == TypeResultat.PRODUIT
                        || newResultatModel.getObject().getTypeResultat() == TypeResultat.TEMOIN) {
                    newResultatModel.getObject().setStade(stadeDefautInput.getModelObject());
                    target.add(stadeInput);
                }
            }
        });
        formView.add(stadeDefautInput);

        formView.add(new TextArea<String>("TestBio.complement", new PropertyModel<String>(testBioModel, "complement")));
        // Créateur en lecture seule
        formView.add(new TextField<String>("TestBio.createur", new PropertyModel<String>(testBioModel, "createur"))
                .setEnabled(false));
    }

    /**
     * Initialise les champs relatifs à la méthode
     * 
     * @param formView le formulaire
     */
    private void initMethodeFields(final Form<Void> formView) {

        final WebMarkupContainer methodeCont = new WebMarkupContainer("TestBio.methode");
        methodeCont.setOutputMarkupId(true);
        formView.add(methodeCont);

        // Champs pour la méthode
        descriptionMethoContainer = new WebMarkupContainer("TestBio.descriptionMethodeCont") {
            @Override
            public boolean isVisible() {
                // description cachée si pas de méthode sélectionnée
                return testBioModel.getObject().getMethode() != null;
            }
        };
        descriptionMethoContainer.setOutputMarkupId(true); // pour l'update Ajax
        descriptionMethoContainer.setOutputMarkupPlaceholderTag(true); // pour accéder à l'élement html qd son état est
        // non visible
        methodeCont.add(descriptionMethoContainer);

        Label methodeCible = new Label("TestBio.cibleMethode", new PropertyModel<String>(testBioModel, "methode.cible"));
        descriptionMethoContainer.add(methodeCible);

        Label methodeDomaine = new Label("TestBio.domaineMethode", new PropertyModel<String>(testBioModel,
                "methode.domaine"));
        descriptionMethoContainer.add(methodeDomaine);

        MultiLineLabel methodeDesc = new MultiLineLabel("TestBio.descriptionMethode", new PropertyModel<String>(
                testBioModel, "methode.description"));
        descriptionMethoContainer.add(methodeDesc);

        Label methodeValeurMesuree = new Label("TestBio.valeurMesureeMethode", new PropertyModel<String>(testBioModel,
                "methode.valeurMesuree"));
        descriptionMethoContainer.add(methodeValeurMesuree);

        Label methodeCritereActivite = new Label("TestBio.critereActiviteMethode", new PropertyModel<String>(
                testBioModel, "methode.critereActivite"));
        descriptionMethoContainer.add(methodeCritereActivite);

        Label methodeUniteResultat = new Label("TestBio.uniteResultatMethode", new PropertyModel<String>(testBioModel,
                "methode.uniteResultat"));
        descriptionMethoContainer.add(methodeUniteResultat);

        final DropDownChoice<MethodeTestBio> methodeChoice = new DropDownChoice<MethodeTestBio>("TestBio.nomMethode",
                new PropertyModel<MethodeTestBio>(testBioModel, "methode"), methodes);
        methodeChoice.setNullValid(false);
        // mise à jour de la description de la méthode et des fractions lors de la sélection de la méthode
        methodeChoice.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            protected void onUpdate(AjaxRequestTarget target) {
                // mise à jour de la description et des paramètres
                target.add(methodeCont);
            }
        });
        methodeCont.add(methodeChoice);
    }

    /**
     * Initialise les champs relatifs aux résultats de test
     * 
     * @param formView Le formulaire
     */
    private void initResultatsFields(final Form<Void> formView) {

        // Déclaration tableau des resultats
        final MarkupContainer testsBioTable = new WebMarkupContainer("TestBio.resultats.Table");
        testsBioTable.setOutputMarkupId(true);

        // Contenu tableaux resultats
        testsBioTable.add(new ListView<ResultatTestBio>("TestBio.resultats.List",
                new PropertyModel<List<ResultatTestBio>>(testBioModel, "sortedResultats")) {
            @Override
            protected void populateItem(ListItem<ResultatTestBio> item) {
                if (item.getIndex() % 2 == 1) {
                    item.add(new AttributeModifier("class", item.getIndex() % 2 == 0 ? "even" : "odd"));
                }

                final IModel<ResultatTestBio> resultatModel = item.getModel();
                final ResultatTestBio resultat = item.getModelObject();

                // Colonnes
                item.add(new Label("TestBio.resultats.List.repere", new PropertyModel<String>(resultat, "repere")));
                item.add(new Label("TestBio.resultats.List.typeResultat", new DisplayEnumPropertyModel(resultat,
                        "typeResultat", (TemplatePage) this.getPage())));
                item.add(new Label("TestBio.resultats.List.produit", new PropertyModel<String>(resultat, "produit.ref")));

                item.add(new Label("TestBio.resultats.List.produitTemoin", new PropertyModel<String>(resultat,
                        "produitTemoin")));

                // concatenation de concMasse et de uniteConcMasse
                item.add(new Label("TestBio.resultats.List.concMasse", new Model<Serializable>(resultat) {

                    /** {@inheritDoc} */
                    @Override
                    public String getObject() {
                        String masse = (String) new DisplayDecimalPropertyModel(super.getObject(), "concMasse",
                                DecimalDisplFormat.SMALL, getLocale()).getObject();
                        if (masse == null) {
                            masse = "";
                        }

                        String unite = (String) new DisplayEnumPropertyModel(super.getObject(), "uniteConcMasse",
                                (TemplatePage) getPage()).getObject();
                        if (unite == null) {
                            unite = "";
                        }
                        return masse + " " + unite;
                    }
                }));

                item.add(new Label("TestBio.resultats.List.stade", new DisplayEnumPropertyModel(resultat, "stade",
                        (TemplatePage) this.getPage())));
                item.add(new Label("TestBio.resultats.List.valeur", new DisplayDecimalPropertyModel(resultat, "valeur",
                        DecimalDisplFormat.SMALL, getSession().getLocale())));
                item.add(new Label("TestBio.resultats.List.actif", new DisplayBooleanPropertyModel(resultatModel,
                        "estActif", (TemplatePage) this.getPage())).add(new ReplaceEmptyLabelBehavior()));

                item.add(new Label("TestBio.resultats.List.erreur", new PropertyModel<String>(resultat, "erreur.nom")));
                // info-bulle comprenant la description de l'erreur
                item.add(new SimpleTooltipPanel("TestBio.resultats.List.erreur.info", new PropertyModel<String>(
                        resultat, "erreur.description")) {
                    /** {@inheritDoc} */
                    @Override
                    public boolean isVisible() {
                        return resultat.getErreur() != null;
                    }
                });

                // Action : suppression d'un résultat de test
                Button deleteButton = new AjaxFallbackButton("TestBio.resultats.List.Delete", formView) {
                    @Override
                    protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                        // Suppression
                        testBioModel.getObject().getResultats().remove(resultat);

                        if (target != null) {
                            target.add(testsBioTable);
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

        // champs d'input
        testsBioTable.add(new TextField<String>("TestBio.resultats.repere", new PropertyModel<String>(newResultatModel,
                "repere")) {
            @Override
            @SuppressWarnings("unchecked")
            public boolean isRequired() {
                // champs requis uniquement qd le bouton d'ajout de résultat est activé
                // de même pour les autres composants ci-dessous
                Form<Void> form = (Form<Void>) findParent(Form.class);
                return form.getRootForm().findSubmittingButton() == addResultatButton;
            }
        });

        final DropDownChoice<TypeResultat> typeResultatChoice = new DropDownChoice<TypeResultat>(
                "TestBio.resultats.typeResultat", new PropertyModel<TypeResultat>(newResultatModel, "typeResultat"),
                Arrays.asList(TypeResultat.values()), new EnumChoiceRenderer<TypeResultat>(this)) {
            @Override
            @SuppressWarnings("unchecked")
            public boolean isRequired() {
                Form<Void> form = (Form<Void>) findParent(Form.class);
                return form.getRootForm().findSubmittingButton() == addResultatButton;
            }
        };
        typeResultatChoice.setNullValid(false);
        testsBioTable.add(typeResultatChoice);

        final DropDownChoice<Produit> produitChoice = new DropDownChoice<Produit>("TestBio.resultats.produit",
                new PropertyModel<Produit>(newResultatModel, "produit"), produits) {
            @Override
            @SuppressWarnings("unchecked")
            public boolean isRequired() {
                Form<Void> form = (Form<Void>) findParent(Form.class);
                return form.getRootForm().findSubmittingButton() == addResultatButton
                        && typeResultatChoice.getModelObject() == TypeResultat.PRODUIT;
            }
        };
        produitChoice.setOutputMarkupId(true);
        produitChoice.setOutputMarkupPlaceholderTag(true);
        testsBioTable.add(produitChoice);

        final AutoCompleteTextFieldString produitTemoinInput = new AutoCompleteTextFieldString(
                "TestBio.resultats.produitTemoin", new PropertyModel<String>(newResultatModel, "produitTemoin"),
                produitsTemoins, ComparisonMode.CONTAINS) {
            @Override
            @SuppressWarnings("unchecked")
            public boolean isRequired() {
                Form<Void> form = (Form<Void>) findParent(Form.class);
                return form.getRootForm().findSubmittingButton() == addResultatButton
                        && typeResultatChoice.getModelObject() == TypeResultat.TEMOIN;
            }
        };
        produitTemoinInput.setOutputMarkupId(true);
        produitTemoinInput.setOutputMarkupPlaceholderTag(true);
        testsBioTable.add(produitTemoinInput);

        concMasseInput = new TextField<BigInteger>("TestBio.resultats.concMasse", new PropertyModel<BigInteger>(
                newResultatModel, "concMasse"));
        concMasseInput.setOutputMarkupId(true);
        concMasseInput.setOutputMarkupPlaceholderTag(true);
        testsBioTable.add(concMasseInput);

        uniteConcMasseInput = new DropDownChoice<UniteConcMasse>("TestBio.resultats.uniteConcMasse",
                new PropertyModel<UniteConcMasse>(newResultatModel, "uniteConcMasse"), Arrays.asList(UniteConcMasse
                        .values()), new EnumChoiceRenderer<UniteConcMasse>(this));
        uniteConcMasseInput.setOutputMarkupId(true);
        uniteConcMasseInput.setOutputMarkupPlaceholderTag(true);
        uniteConcMasseInput.setNullValid(true);

        testsBioTable.add(uniteConcMasseInput);

        stadeInput = new DropDownChoice<Stade>("TestBio.resultats.stade", new PropertyModel<Stade>(newResultatModel,
                "stade"), Arrays.asList(Stade.values()), new EnumChoiceRenderer<Stade>(this)) {
            @Override
            @SuppressWarnings("unchecked")
            public boolean isRequired() {
                Form<Void> form = (Form<Void>) findParent(Form.class);
                return form.getRootForm().findSubmittingButton() == addResultatButton
                        && typeResultatChoice.getModelObject() == TypeResultat.PRODUIT;
            }
        };
        stadeInput.setOutputMarkupId(true);
        stadeInput.setOutputMarkupPlaceholderTag(true);
        testsBioTable.add(stadeInput);

        testsBioTable
                .add(new CheckBox("TestBio.resultats.actif", new PropertyModel<Boolean>(newResultatModel, "actif")));

        final DropDownChoice<ErreurTestBio> erreurChoice = new DropDownChoice<ErreurTestBio>(
                "TestBio.resultats.erreur", new PropertyModel<ErreurTestBio>(newResultatModel, "erreur"), erreurs);
        erreurChoice.setNullValid(true);
        testsBioTable.add(erreurChoice);

        // info-bulle comprenant la description de l'erreur
        final SimpleTooltipPanel infoBulle = new SimpleTooltipPanel("TestBio.resultats.erreur.info",
                new PropertyModel<String>(newResultatModel, "erreur.description"));
        infoBulle.setVisibilityAllowed(false);
        // permet la mise en visibité ou non en Ajax
        infoBulle.setOutputMarkupId(true);
        infoBulle.setOutputMarkupPlaceholderTag(true);
        testsBioTable.add(infoBulle);

        final TextField<BigDecimal> valeurInput = new TextField<BigDecimal>("TestBio.resultats.valeur",
                new PropertyModel<BigDecimal>(newResultatModel, "valeur")) {
            @Override
            @SuppressWarnings("unchecked")
            public boolean isRequired() {
                Form<Void> form = (Form<Void>) findParent(Form.class);
                return form.getRootForm().findSubmittingButton() == addResultatButton
                        && erreurChoice.getModelObject() == null;
            }
        };
        valeurInput.setOutputMarkupId(true);
        valeurInput.setOutputMarkupPlaceholderTag(true);
        testsBioTable.add(valeurInput);

        // comportement dynamique sur les inputs
        typeResultatChoice.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            protected void onUpdate(AjaxRequestTarget target) {
                if (typeResultatChoice.getModelObject() == TypeResultat.BLANC) {
                    activeInputsForBlancType(produitChoice, produitTemoinInput, concMasseInput, uniteConcMasseInput,
                            stadeInput, target);
                } else if (typeResultatChoice.getModelObject() == TypeResultat.TEMOIN) {
                    activeInputsForTemoinType(produitChoice, produitTemoinInput, concMasseInput, uniteConcMasseInput,
                            stadeInput, target);
                } else {
                    activeInputsForProduitType(produitChoice, produitTemoinInput, concMasseInput, uniteConcMasseInput,
                            stadeInput, true, target);
                }
            }
        });

        erreurChoice.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            protected void onUpdate(AjaxRequestTarget target) {
                if (erreurChoice.getModelObject() != null) {
                    // si une erreur est sélectionnée, le champ valeur est désactivé et l'info-bulle affichée
                    valeurInput.setVisibilityAllowed(false);
                    newResultatModel.getObject().setValeur(null);
                    infoBulle.setVisibilityAllowed(true);
                } else {
                    // si une erreur est sélectionnée, le champ valeur est désactivé et l'info-bulle désactivée
                    valeurInput.setVisibilityAllowed(true);
                    infoBulle.setVisibilityAllowed(false);
                }
                target.add(valeurInput, infoBulle);
            }
        });

        // Bouton AJAX pour ajouter un résultat de test
        addResultatButton = new AjaxFallbackButton("TestBio.resultats.Add", formView) {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                try {
                    // normalisation du résultat
                    newResultatModel.getObject()
                            .setRepere(
                                    Normalizer.normalize(UniqueFieldNormalizer.class, newResultatModel.getObject()
                                            .getRepere()));
                    // ajout du type testBio
                    newResultatModel.getObject().setTestBio(testBioModel.getObject());

                    // ajout à la liste
                    ResultatTestBio resultatAdded = newResultatModel.getObject().clone();
                    testBioModel.getObject().getResultats().add(resultatAdded);

                    List<String> errors = validator.validate(newResultatModel.getObject(), getSession().getLocale());

                    // si une saisie existe pour concMasse, on s'assure que les deux composantes sont bien renseignées
                    if ((newResultatModel.getObject().getConcMasse() != null && newResultatModel.getObject()
                            .getUniteConcMasse() == null)
                            || (newResultatModel.getObject().getConcMasse() == null && newResultatModel.getObject()
                                    .getUniteConcMasse() != null)) {
                        errors.add(getString("TestBio.resultats.concMasse.KO"));
                    }

                    if (errors.isEmpty()) {
                        // réinit des champs de la ligne "ajout"
                        newResultatModel.getObject().setRepere(null);
                        newResultatModel.getObject().setTypeResultat(TypeResultat.PRODUIT);
                        activeInputsForProduitType(produitChoice, produitTemoinInput, concMasseInput,
                                uniteConcMasseInput, stadeInput, true, target);
                        newResultatModel.getObject().setProduit(null);

                        // si non vide, ajout du témoin produit à la liste des propositions
                        if (StringUtils.isNotEmpty(newResultatModel.getObject().getProduitTemoin())) {
                            produitTemoinInput.addChoice(newResultatModel.getObject().getProduitTemoin());
                        }
                        newResultatModel.getObject().setProduitTemoin(null);

                        // concMasse et uniteConcMasse prennent les valeurs par défaut
                        newResultatModel.getObject().setConcMasse(concMasseDefautInput.getModelObject());
                        newResultatModel.getObject().setUniteConcMasse(uniteConcMasseDefautInput.getModelObject());
                        newResultatModel.getObject().setStade(stadeInput.getModelObject());
                        newResultatModel.getObject().setValeur(null);
                        newResultatModel.getObject().setActif(null);
                        newResultatModel.getObject().setErreur(null);
                        // réactivation du champ valeur en cas d'ancienne sélection d'erreur
                        valeurInput.setVisibilityAllowed(true);
                    } else {
                        testBioModel.getObject().getResultats().remove(resultatAdded);
                        addValidationErrors(errors);
                    }
                } catch (CloneNotSupportedException e) {
                    LOG.error(e.getMessage(), e);
                    throw new UnexpectedException(e);
                }

                if (target != null) {
                    target.add(testsBioTable);
                    refreshFeedbackPage(target);
                }
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                refreshFeedbackPage(target);
            }

        };
        testsBioTable.add(addResultatButton);
        formView.add(testsBioTable);

        // pre-initialisation of the value Produit for TypeProduit
        newResultatModel.getObject().setTypeResultat(TypeResultat.PRODUIT);
        activeInputsForProduitType(produitChoice, produitTemoinInput, concMasseInput, uniteConcMasseInput, stadeInput,
                false, null);
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
            // Redirection de nouveau vers l'écran de saisie d'une nouvelle testBio
            TestBio nextManip = new TestBio();
            nextManip.setManipulateur(testBioModel.getObject().getManipulateur());
            nextManip.setOrganismeTesteur(testBioModel.getObject().getOrganismeTesteur());
            nextManip.setMethode(testBioModel.getObject().getMethode());
            setResponsePage(new ManageTestBioPage(nextManip, callerPage));
        } else if (callerPage != null) {
            // On passe l'id du testBio associée à cette page, en paramètre de la prochaine page, pour lui permettre de
            // l'exploiter si besoin
            callerPage.addPageParameter(TestBio.class.getSimpleName(), testBioModel.getObject());
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
                testBioModel.getObject().setManipulateur(createdPersonne);
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
     * Initialise les champs produit, conc., unité conc.n stade pour un résultat de type "blanc"
     * 
     * @param produitChoice Liste des produits
     * @param produitTemoinInput Texte pour le produit témoin
     * @param concMasseInput Texte pour la conc./masse
     * @param uniteConcMasseChoice Liste pour l'unité de la conc./masse
     * @param stadeChoice Liste des stades
     * @param target Cible de la requête ajax
     */
    private void activeInputsForBlancType(final DropDownChoice<Produit> produitChoice,
            final TextField<String> produitTemoinInput, final TextField<BigInteger> concMasseInput,
            final DropDownChoice<UniteConcMasse> uniteConcMasseChoice, final DropDownChoice<Stade> stadeChoice,
            AjaxRequestTarget target) {
        // si blanc, aucune saisie pour produit, produit témoin, conc., unité conc., stade
        produitChoice.setNullValid(true);
        newResultatModel.getObject().setProduit(null);
        produitChoice.setVisibilityAllowed(false);

        newResultatModel.getObject().setProduitTemoin(null);
        produitTemoinInput.setVisibilityAllowed(false);

        newResultatModel.getObject().setConcMasse(null);
        concMasseInput.setVisibilityAllowed(false);

        newResultatModel.getObject().setUniteConcMasse(null);
        uniteConcMasseChoice.setVisibilityAllowed(false);

        stadeChoice.setNullValid(true);
        newResultatModel.getObject().setStade(null);
        stadeChoice.setVisibilityAllowed(false);

        target.add(produitChoice, produitTemoinInput, concMasseInput, uniteConcMasseChoice, stadeChoice);
    }

    /**
     * Initialise les champs produit, conc., unité conc.n stade pour un résultat de type "témoin"
     * 
     * @param produitChoice Liste des produits
     * @param produitTemoinInput Texte pour le produit témoin
     * @param concMasseInput Texte pour la conc./masse
     * @param uniteConcMasseChoice Liste pour l'unité de la conc./masse
     * @param stadeChoice Liste des stades
     * @param target Cible de la requête ajax
     */
    private void activeInputsForTemoinType(final DropDownChoice<Produit> produitChoice,
            final TextField<String> produitTemoinInput, final TextField<BigInteger> concMasseInput,
            final DropDownChoice<UniteConcMasse> uniteConcMasseChoice, final DropDownChoice<Stade> stadeChoice,
            AjaxRequestTarget target) {
        // si témoin, aucune saisie pour produit et stade
        produitChoice.setNullValid(true);
        newResultatModel.getObject().setProduit(null);
        produitChoice.setVisibilityAllowed(false);

        stadeChoice.setNullValid(true);
        newResultatModel.getObject().setStade(null);
        stadeChoice.setVisibilityAllowed(false);

        // saisie pour le reste
        produitTemoinInput.setVisibilityAllowed(true);

        // masse/conc. par défaut comme valeur
        newResultatModel.getObject().setConcMasse(concMasseDefautInput.getModelObject());
        concMasseInput.setVisibilityAllowed(true);

        // unité masse/conc. par défaut comme valeur
        newResultatModel.getObject().setUniteConcMasse(testBioModel.getObject().getUniteConcMasseDefaut());
        uniteConcMasseChoice.setVisibilityAllowed(true);

        target.add(produitChoice, produitTemoinInput, concMasseInput, uniteConcMasseChoice, stadeChoice);
    }

    /**
     * Initialise les champs produit, conc., unité conc.n stade pour un résultat de type "produit"
     * 
     * @param produitChoice Liste des produits
     * @param produitTemoinInput Texte pour le produit témoin
     * @param concMasseInput Texte pour la conc./masse
     * @param uniteConcMasseChoice Liste pour l'unité de la conc./masse
     * @param stadeChoice Liste des stades
     * @param isAjaxRequete Vrai si l'initialisation se fait via une requête ajax
     * @param target Cible de la requête ajax, null si non appelé par une requête ajax
     */
    private void activeInputsForProduitType(final DropDownChoice<Produit> produitChoice,
            final TextField<String> produitTemoinInput, final TextField<BigInteger> concMasseInput,
            final DropDownChoice<UniteConcMasse> uniteConcMasseChoice, final DropDownChoice<Stade> stadeChoice,
            boolean isAjaxRequete, AjaxRequestTarget target) {
        // cas où type est produit, produit témoin désactivé et produit, conc., unité conc., stade activés
        newResultatModel.getObject().setProduitTemoin(null);
        produitTemoinInput.setVisibilityAllowed(false);

        produitChoice.setNullValid(false);
        produitChoice.setVisibilityAllowed(true);

        // masse/conc. par défaut comme valeur
        newResultatModel.getObject().setConcMasse(testBioModel.getObject().getConcMasseDefaut());
        concMasseInput.setVisibilityAllowed(true);

        // unité masse/conc. par défaut comme valeur
        newResultatModel.getObject().setUniteConcMasse(testBioModel.getObject().getUniteConcMasseDefaut());
        uniteConcMasseChoice.setVisibilityAllowed(true);

        stadeChoice.setNullValid(false);
        // stade par défaut comme valeur
        newResultatModel.getObject().setStade(testBioModel.getObject().getStadeDefaut());
        stadeChoice.setVisibilityAllowed(true);

        if (isAjaxRequete) {
            target.add(produitChoice, produitTemoinInput, concMasseInput, uniteConcMasseChoice, stadeChoice);
        }
    }

    /**
     * Validate model
     */
    private void validateModel() {
        addValidationErrors(validator.validate(testBioModel.getObject(), getSession().getLocale()));

        if (!CollectionTools.containsWithValue(testBioModel.getObject().getResultats(), "typeResultat",
                AccessType.GETTER, TypeResultat.PRODUIT)) {
            error(getString("TestBio.resultats.noProduit"));
        }
        if (!testBioService.isTestBioUnique(testBioModel.getObject())) {
            error(getString("TestBio.notUnique"));
        }
    }
}
