/*
 * #%L
 * Cantharella :: Web
 * $Id: ManageMoleculePage.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.web/src/main/java/nc/ird/cantharella/web/pages/domain/molecule/ManageMoleculePage.java $
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
package nc.ird.cantharella.web.pages.domain.molecule;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import nc.ird.cantharella.data.exceptions.DataConstraintException;
import nc.ird.cantharella.data.exceptions.DataNotFoundException;
import nc.ird.cantharella.data.exceptions.UnexpectedException;
import nc.ird.cantharella.data.model.Campagne;
import nc.ird.cantharella.data.model.Extrait;
import nc.ird.cantharella.data.model.Fraction;
import nc.ird.cantharella.data.model.Molecule;
import nc.ird.cantharella.data.model.MoleculeProvenance;
import nc.ird.cantharella.data.model.Produit;
import nc.ird.cantharella.data.model.Utilisateur;
import nc.ird.cantharella.data.validation.utils.ModelValidator;
import nc.ird.cantharella.service.services.CampagneService;
import nc.ird.cantharella.service.services.MoleculeService;
import nc.ird.cantharella.service.services.ProduitService;
import nc.ird.cantharella.web.pages.TemplatePage;
import nc.ird.cantharella.web.pages.domain.campagne.ManageCampagnePage;
import nc.ird.cantharella.web.pages.domain.document.panel.ManageListDocumentsPanel;
import nc.ird.cantharella.web.pages.domain.extraction.ReadExtractionPage;
import nc.ird.cantharella.web.pages.domain.purification.ReadPurificationPage;
import nc.ird.cantharella.web.pages.renderers.ProduitRenderer;
import nc.ird.cantharella.web.utils.CallerPage;
import nc.ird.cantharella.web.utils.behaviors.JSConfirmationBehavior;
import nc.ird.cantharella.web.utils.behaviors.MoleculeEditorBehavior;
import nc.ird.cantharella.web.utils.behaviors.MoleculeViewBehavior;
import nc.ird.cantharella.web.utils.forms.AutoCompleteTextFieldString;
import nc.ird.cantharella.web.utils.forms.AutoCompleteTextFieldString.ComparisonMode;
import nc.ird.cantharella.web.utils.forms.SubmittableButton;
import nc.ird.cantharella.web.utils.forms.SubmittableButtonEvents;
import nc.ird.cantharella.web.utils.models.DisplayDecimalPropertyModel;
import nc.ird.cantharella.web.utils.models.DisplayDecimalPropertyModel.DecimalDisplFormat;
import nc.ird.cantharella.web.utils.panels.PropertyLabelLinkProduitPanel;
import nc.ird.cantharella.web.utils.panels.SimpleTooltipPanel;
import nc.ird.cantharella.web.utils.security.AuthRole;
import nc.ird.cantharella.web.utils.security.AuthRoles;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.ajax.markup.html.form.AjaxFallbackButton;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.HiddenField;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.AbstractPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manage molecule page.
 * 
 * @author Eric Chatellier
 */
@AuthRoles({ AuthRole.USER, AuthRole.ADMIN })
public final class ManageMoleculePage extends TemplatePage {

    /** Action : create */
    private static final String ACTION_CREATE = "Create";

    /** Action : delete */
    private static final String ACTION_DELETE = "Delete";

    /** Action : update */
    private static final String ACTION_UPDATE = "Update";

    /** Logger */
    private static final Logger LOG = LoggerFactory.getLogger(ManageMoleculePage.class);

    /** Campagnes */
    private final List<Campagne> campagnes;

    /** Service : campagne */
    @SpringBean
    private CampagneService campagneService;

    /** Service : produit */
    @SpringBean
    private ProduitService produitService;

    /** Modèle : molecule */
    private final IModel<Molecule> moleculeModel;

    /** Modèle to add new provenance. */
    private Model<MoleculeProvenance> newProvenanceModel;

    /** Service : molecule */
    @SpringBean
    private MoleculeService moleculeService;

    /** Model validateur */
    @SpringBean(name = "webModelValidator")
    private ModelValidator validator;

    /** Page appelante */
    private final CallerPage callerPage;

    /** Bouton d'ajout d'une provenance **/
    protected Button addProvenanceButton;

    /**
     * Constructeur (mode création).
     * 
     * @param callerPage Page appelante
     */
    public ManageMoleculePage(CallerPage callerPage) {
        this(null, callerPage);
    }

    /**
     * Constructeur. Si idMolecule est null, on créée une nouvelle Molecule. Si idMolecule est renseigné, on édite la
     * molecule correspondante.
     * 
     * @param idMolecule ID molecule
     * @param callerPage Page appelante
     */
    public ManageMoleculePage(Integer idMolecule, final CallerPage callerPage) {
        super(ManageMoleculePage.class);
        this.callerPage = callerPage;

        final CallerPage currentPage = new CallerPage(this);

        newProvenanceModel = new Model<MoleculeProvenance>(new MoleculeProvenance());

        // Initialisation du modèle
        try {
            moleculeModel = new Model<Molecule>(idMolecule == null ? new Molecule()
                    : moleculeService.loadMolecule(idMolecule));
        } catch (DataNotFoundException e) {
            LOG.error(e.getMessage(), e);
            throw new UnexpectedException(e);
        }

        boolean createMode = idMolecule == null;
        if (createMode) {
            moleculeModel.getObject().setCreateur(getSession().getUtilisateur());
        }

        // Initialisation des listes
        List<String> organismes = moleculeService.listMoleculeOrganisme();
        campagnes = campagneService.listCampagnes(getSession().getUtilisateur());

        final Form<Void> formView = new Form<Void>("Form");
        initProvenanceFields(formView, currentPage);

        // page can be accessed by anyone for editing provenance
        // but molecule fields can be edited only by administrators or
        // molecule creator
        Utilisateur utilisateur = getSession().getUtilisateur();
        boolean updateOrDeleteEnabled = createMode
                || moleculeService.updateOrdeleteMoleculeEnabled(moleculeModel.getObject(), utilisateur);

        formView.add(new TextField<String>("Molecule.nomCommun", new PropertyModel<String>(moleculeModel, "nomCommun"))
                .setEnabled(updateOrDeleteEnabled));
        formView.add(new TextField<String>("Molecule.familleChimique", new PropertyModel<String>(moleculeModel,
                "familleChimique")).setEnabled(updateOrDeleteEnabled));

        if (updateOrDeleteEnabled) {
            formView.add(new HiddenField<String>("Molecule.formuleDevMol", new PropertyModel<String>(moleculeModel,
                    "formuleDevMol")).add(new MoleculeEditorBehavior(new PropertyModel<String>(moleculeModel,
                    "formuleDevMol"))));
        } else {
            formView.add(new HiddenField<String>("Molecule.formuleDevMol", new PropertyModel<String>(moleculeModel,
                    "formuleDevMol")).setEnabled(false) // important otherwise, loose data
                    .add(new MoleculeViewBehavior(new PropertyModel<String>(moleculeModel, "formuleDevMol"), true)));
        }

        formView.add(new TextField<String>("Molecule.nomIupca", new PropertyModel<String>(moleculeModel, "nomIupca"))
                .setEnabled(updateOrDeleteEnabled));
        formView.add(new SimpleTooltipPanel("Molecule.formuleBrute.info", getStringModel("Molecule.formuleBrute.info")));
        formView.add(new TextField<String>("Molecule.formuleBrute", new PropertyModel<String>(moleculeModel,
                "formuleBrute")).setEnabled(updateOrDeleteEnabled));

        TextField<String> masseMolaireField = new TextField<String>("Molecule.masseMolaire", new PropertyModel<String>(
                moleculeModel, "masseMolaire"));
        masseMolaireField.setRequired(true);
        masseMolaireField.setEnabled(updateOrDeleteEnabled);
        formView.add(masseMolaireField);

        // div qui englobe les champs visible ssi nouvMolecul est coché
        final MarkupContainer nouvMoleculRefresh = new WebMarkupContainer("Molecule.nouvMolecul.Refresh");
        nouvMoleculRefresh.setOutputMarkupPlaceholderTag(true);
        nouvMoleculRefresh.setVisible(moleculeModel.getObject().isNouvMolecul());
        formView.add(nouvMoleculRefresh);

        // predéclaration des champs activé par la chec
        formView.add(new SimpleTooltipPanel("Molecule.nouvMolecul.info", getStringModel("Molecule.nouvMolecul.info")));
        formView.add(new AjaxCheckBox("Molecule.nouvMolecul", new PropertyModel<Boolean>(moleculeModel, "nouvMolecul")) {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                nouvMoleculRefresh.setVisible(moleculeModel.getObject().isNouvMolecul());
                target.add(nouvMoleculRefresh);
            }
        }.setEnabled(updateOrDeleteEnabled));

        AutoCompleteTextFieldString identifieeParInput = new AutoCompleteTextFieldString("Molecule.identifieePar",
                new PropertyModel<String>(moleculeModel, "identifieePar"), organismes, ComparisonMode.CONTAINS);
        identifieeParInput.setEnabled(updateOrDeleteEnabled);
        nouvMoleculRefresh.add(identifieeParInput);

        DropDownChoice<Campagne> campagnesInput = new DropDownChoice<Campagne>("Molecule.campagne",
                new PropertyModel<Campagne>(moleculeModel, "campagne"), campagnes);
        campagnesInput.setEnabled(updateOrDeleteEnabled);
        nouvMoleculRefresh.add(campagnesInput);

        // Action : création d'une nouvelle campagne
        // ajaxSubmitLink permet de sauvegarder l'état du formulaire
        nouvMoleculRefresh.add(new AjaxSubmitLink("NewCampagne") {
            @Override
            protected void onSubmit(AjaxRequestTarget request, Form<?> form) {
                setResponsePage(new ManageCampagnePage(currentPage, false));
            }

            // si erreur, le formulaire est également enregistré puis la redirection effectuée
            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                setResponsePage(new ManageCampagnePage(currentPage, false));
            }
        }.setVisibilityAllowed(updateOrDeleteEnabled));

        nouvMoleculRefresh.add(new TextArea<String>("Molecule.publiOrigine", new PropertyModel<String>(moleculeModel,
                "publiOrigine")).setEnabled(updateOrDeleteEnabled));

        formView.add(new TextArea<String>("Molecule.complement", new PropertyModel<String>(moleculeModel, "complement"))
                .setEnabled(updateOrDeleteEnabled));

        // Créateur en lecture seule
        formView.add(new TextField<String>("Molecule.createur", new PropertyModel<String>(moleculeModel, "createur"))
                .setEnabled(false));

        // add list document panel
        ManageListDocumentsPanel manageListDocumentsPanel = new ManageListDocumentsPanel("ManageListDocumentsPanel",
                moleculeModel, currentPage);
        manageListDocumentsPanel.setUpdateOrDeleteEnabled(updateOrDeleteEnabled);
        formView.add(manageListDocumentsPanel);

        // Action : create molecule
        Button createButton = new SubmittableButton(ACTION_CREATE, new SubmittableButtonEvents() {
            @Override
            public void onProcess() throws DataConstraintException {
                moleculeService.createMolecule(moleculeModel.getObject());
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

        // Action : mise à jour du lot
        Button updateButton = new SubmittableButton(ACTION_UPDATE, new SubmittableButtonEvents() {
            @Override
            public void onProcess() throws DataConstraintException {
                moleculeService.updateMolecule(moleculeModel.getObject());
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

        // Action : suppression du lot
        Button deleteButton = new SubmittableButton(ACTION_DELETE, new SubmittableButtonEvents() {
            @Override
            public void onProcess() throws DataConstraintException {
                moleculeService.deleteMolecule(moleculeModel.getObject());
            }

            @Override
            public void onSuccess() {
                successNextPage(ACTION_DELETE);
                callerPage.responsePage((TemplatePage) getPage());
            }
        });
        deleteButton.setVisibilityAllowed(!createMode && updateOrDeleteEnabled);
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
     * Init provenance table.
     * 
     * @param formView
     */
    private void initProvenanceFields(final Form<Void> formView, final CallerPage currentPage) {
        // Déclaration tableau des provenances
        final MarkupContainer provenanceTable = new WebMarkupContainer("Molecule.provenance.Table");
        provenanceTable.setOutputMarkupId(true);
        final WebMarkupContainer resultNotAccessibleCont = new WebMarkupContainer(
                "Molecule.provenance.resultsNotAccessibles");
        resultNotAccessibleCont.setOutputMarkupPlaceholderTag(true);
        provenanceTable.add(resultNotAccessibleCont);

        // chargement des données
        final Utilisateur utilisateur = getSession().getUtilisateur();
        final List<Produit> utilisateurProduits = produitService.listProduits(utilisateur);

        // Model de liste des provenances
        final LoadableDetachableModel<List<MoleculeProvenance>> listProvenanceModel = new LoadableDetachableModel<List<MoleculeProvenance>>() {
            @Override
            protected List<MoleculeProvenance> load() {
                boolean isOneResultNotAccessible = false;

                List<MoleculeProvenance> listResults = new ArrayList<MoleculeProvenance>();
                for (MoleculeProvenance res : moleculeModel.getObject().getProvenances()) {
                    // les résultats de type blanc ou témoin sont tjr accessibles
                    if (moleculeService.isMoleculeProvenanceAccessibleByUser(res, utilisateur)) {
                        listResults.add(res);
                    } else {
                        isOneResultNotAccessible = true;
                    }
                }
                // si une des provenances est non accessible, on rend visible le message d'avertissement
                resultNotAccessibleCont.setVisibilityAllowed(isOneResultNotAccessible);
                return listResults;
            }
        };

        // Contenu tableaux provenance
        provenanceTable.add(new ListView<MoleculeProvenance>("Molecule.provenance.List", listProvenanceModel) {
            @Override
            protected void populateItem(ListItem<MoleculeProvenance> item) {
                if (item.getIndex() % 2 == 1) {
                    item.add(new AttributeModifier("class", item.getIndex() % 2 == 0 ? "even" : "odd"));
                }

                IModel<MoleculeProvenance> provenanceModel = item.getModel();
                final MoleculeProvenance provenance = provenanceModel.getObject();

                // affichage + lien vers la fiche
                item.add(new PropertyLabelLinkProduitPanel("Molecule.provenance.List.produit.ref",
                        new PropertyModel<Produit>(provenanceModel, "produit"), (TemplatePage) getPage()) {
                    @Override
                    public void onClickIfExtrait(Extrait extrait) {
                        setResponsePage(new ReadExtractionPage(extrait.getExtraction().getIdExtraction(), currentPage));
                    }

                    @Override
                    public void onClickIfFraction(Fraction fraction) {
                        setResponsePage(new ReadPurificationPage(fraction.getPurification().getIdPurification(),
                                currentPage));
                    }
                });

                item.add(new Label("Molecule.provenance.List.presence", new DisplayDecimalPropertyModel(
                        provenanceModel, "pourcentage", DecimalDisplFormat.SMALL, getLocale())));

                if (provenance.getProduit() instanceof Fraction) {
                    item.add(new Label("Molecule.provenance.List.lot.ref", new PropertyModel<String>(provenanceModel,
                            "produit.purification.lotSource.ref")));
                    item.add(new Label("Molecule.provenance.List.genre", new PropertyModel<String>(provenanceModel,
                            "produit.purification.lotSource.specimenRef.genre")));
                    item.add(new Label("Molecule.provenance.List.espece", new PropertyModel<String>(provenanceModel,
                            "produit.purification.lotSource.specimenRef.espece")));
                    item.add(new Label("Molecule.provenance.List.campagne", new PropertyModel<String>(provenanceModel,
                            "produit.purification.lotSource.campagne.nom")));
                } else {
                    item.add(new Label("Molecule.provenance.List.lot.ref", new PropertyModel<String>(provenanceModel,
                            "produit.extraction.lot.ref")));
                    item.add(new Label("Molecule.provenance.List.genre", new PropertyModel<String>(provenanceModel,
                            "produit.extraction.lot.specimenRef.genre")));
                    item.add(new Label("Molecule.provenance.List.espece", new PropertyModel<String>(provenanceModel,
                            "produit.extraction.lot.specimenRef.espece")));
                    item.add(new Label("Molecule.provenance.List.campagne", new PropertyModel<String>(provenanceModel,
                            "produit.extraction.lot.campagne.nom")));
                }

                // Action : suppression d'un résultat de test
                Button deleteButton = new AjaxFallbackButton("Molecule.provenance.List.Delete", formView) {
                    @Override
                    protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                        // Suppression
                        moleculeModel.getObject().getProvenances().remove(provenance);

                        if (target != null) {
                            target.add(provenanceTable);
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
        final DropDownChoice<Produit> produitChoice = new DropDownChoice<Produit>("Molecule.provenance.produit.ref",
                new PropertyModel<Produit>(newProvenanceModel, "produit"), utilisateurProduits, new ProduitRenderer());
        produitChoice.setNullValid(false);
        provenanceTable.add(produitChoice);

        final TextField<BigDecimal> presenceInput = new TextField<BigDecimal>("Molecule.provenance.presence",
                new PropertyModel<BigDecimal>(newProvenanceModel, "pourcentage"));
        presenceInput.setOutputMarkupId(true);
        provenanceTable.add(presenceInput);

        final Label lotRefLabel = new Label("Molecule.provenance.lot.ref", new AbstractPropertyModel<String>(
                newProvenanceModel) {
            @Override
            protected String propertyExpression() {
                if (newProvenanceModel.getObject().getProduit() instanceof Fraction) {
                    return "produit.purification.lotSource.ref";
                } else {
                    return "produit.extraction.lot.ref";
                }
            }
        });
        lotRefLabel.setOutputMarkupId(true);
        provenanceTable.add(lotRefLabel);

        final Label genreLabel = new Label("Molecule.provenance.genre", new AbstractPropertyModel<String>(
                newProvenanceModel) {
            @Override
            protected String propertyExpression() {
                if (newProvenanceModel.getObject().getProduit() instanceof Fraction) {
                    return "produit.purification.lotSource.specimenRef.genre";
                } else {
                    return "produit.extraction.lot.specimenRef.genre";
                }
            }
        });
        genreLabel.setOutputMarkupId(true);
        provenanceTable.add(genreLabel);

        final Label especeLabel = new Label("Molecule.provenance.espece", new AbstractPropertyModel<String>(
                newProvenanceModel) {
            @Override
            protected String propertyExpression() {
                if (newProvenanceModel.getObject().getProduit() instanceof Fraction) {
                    return "produit.purification.lotSource.specimenRef.espece";
                } else {
                    return "produit.extraction.lot.specimenRef.espece";
                }
            }
        });
        especeLabel.setOutputMarkupId(true);
        provenanceTable.add(especeLabel);

        final Label campagneLabel = new Label("Molecule.provenance.campagne", new AbstractPropertyModel<String>(
                newProvenanceModel) {
            @Override
            protected String propertyExpression() {
                if (newProvenanceModel.getObject().getProduit() instanceof Fraction) {
                    return "produit.purification.lotSource.campagne.nom";
                } else {
                    return "produit.extraction.lot.campagne.nom";
                }
            }
        });
        campagneLabel.setOutputMarkupId(true);
        provenanceTable.add(campagneLabel);

        produitChoice.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            protected void onUpdate(AjaxRequestTarget target) {
                target.add(lotRefLabel, genreLabel, especeLabel, campagneLabel);
            }
        });

        // Bouton AJAX pour ajouter un résultat de test
        addProvenanceButton = new AjaxFallbackButton("Molecule.provenance.Add", formView) {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                try {
                    // ajout du type molecule
                    newProvenanceModel.getObject().setMolecule(moleculeModel.getObject());

                    // ajout à la liste
                    MoleculeProvenance provenanceAdded = newProvenanceModel.getObject().clone();
                    moleculeModel.getObject().getProvenances().add(provenanceAdded);

                    List<String> errors = validator.validate(provenanceAdded, getSession().getLocale());

                    if (errors.isEmpty()) {
                        // réinit des champs de la ligne "ajout"
                        newProvenanceModel.getObject().setProduit(null);
                        newProvenanceModel.getObject().setPourcentage(null);
                        newProvenanceModel.getObject().setMolecule(null);
                    } else {
                        moleculeModel.getObject().getProvenances().remove(provenanceAdded);
                        addValidationErrors(errors);
                    }
                } catch (CloneNotSupportedException e) {
                    LOG.error(e.getMessage(), e);
                    throw new UnexpectedException(e);
                }

                if (target != null) {
                    target.add(provenanceTable);
                    refreshFeedbackPage(target);
                }
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                refreshFeedbackPage(target);
            }

        };
        provenanceTable.add(addProvenanceButton);

        formView.add(provenanceTable);
    }

    /**
     * Redirection vers une autre page. Cas où le formulaire est validé
     */
    private void redirect() {
        if (callerPage != null) {
            callerPage.responsePage(this);
        }
    }

    /**
     * Validate model
     */
    private void validateModel() {
        if (moleculeModel.getObject().getCreateur() == null) {
            moleculeModel.getObject().setCreateur(getSession().getUtilisateur());
        }
        addValidationErrors(validator.validate(moleculeModel.getObject(), getSession().getLocale()));
    }
}
