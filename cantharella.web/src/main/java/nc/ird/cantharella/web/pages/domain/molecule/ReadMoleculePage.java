/*
 * #%L
 * Cantharella :: Web
 * $Id: ReadMoleculePage.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.web/src/main/java/nc/ird/cantharella/web/pages/domain/molecule/ReadMoleculePage.java $
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

import java.util.ArrayList;
import java.util.List;

import nc.ird.cantharella.data.exceptions.DataConstraintException;
import nc.ird.cantharella.data.model.Campagne;
import nc.ird.cantharella.data.model.Extrait;
import nc.ird.cantharella.data.model.Fraction;
import nc.ird.cantharella.data.model.Molecule;
import nc.ird.cantharella.data.model.MoleculeProvenance;
import nc.ird.cantharella.data.model.Personne;
import nc.ird.cantharella.data.model.Produit;
import nc.ird.cantharella.data.model.Utilisateur;
import nc.ird.cantharella.service.services.MoleculeService;
import nc.ird.cantharella.web.pages.TemplatePage;
import nc.ird.cantharella.web.pages.domain.document.panel.ReadListDocumentsPanel;
import nc.ird.cantharella.web.pages.domain.extraction.ReadExtractionPage;
import nc.ird.cantharella.web.pages.domain.lot.ManageLotPage;
import nc.ird.cantharella.web.pages.domain.personne.ReadPersonnePage;
import nc.ird.cantharella.web.pages.domain.purification.ReadPurificationPage;
import nc.ird.cantharella.web.utils.CallerPage;
import nc.ird.cantharella.web.utils.behaviors.JSConfirmationBehavior;
import nc.ird.cantharella.web.utils.behaviors.MoleculeViewBehavior;
import nc.ird.cantharella.web.utils.behaviors.ReplaceEmptyLabelBehavior;
import nc.ird.cantharella.web.utils.forms.SubmittableButton;
import nc.ird.cantharella.web.utils.forms.SubmittableButtonEvents;
import nc.ird.cantharella.web.utils.models.DisplayBooleanPropertyModel;
import nc.ird.cantharella.web.utils.models.DisplayDecimalPropertyModel;
import nc.ird.cantharella.web.utils.models.DisplayDecimalPropertyModel.DecimalDisplFormat;
import nc.ird.cantharella.web.utils.models.GenericLoadableDetachableModel;
import nc.ird.cantharella.web.utils.panels.PropertyLabelLinkPanel;
import nc.ird.cantharella.web.utils.panels.PropertyLabelLinkProduitPanel;
import nc.ird.cantharella.web.utils.security.AuthRole;
import nc.ird.cantharella.web.utils.security.AuthRoles;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.link.ResourceLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.resource.ByteArrayResource;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * Lecture d'une molécule.
 * 
 * @author Eric Chatellier
 */
@AuthRoles({ AuthRole.ADMIN, AuthRole.USER })
public class ReadMoleculePage extends TemplatePage {

    /** Action : delete */
    public static final String ACTION_DELETE = "Delete";

    /** Modèle : molecule */
    private IModel<Molecule> moleculeModel;

    /** Service : molecule */
    @SpringBean
    private MoleculeService moleculeService;

    /** Page appelante */
    private CallerPage callerPage;

    /**
     * Constructor with molecule id to render.
     * 
     * @param idMolecule molecul id
     * @param callerPage caller page
     */
    public ReadMoleculePage(Integer idMolecule, final CallerPage callerPage) {
        super(ReadMoleculePage.class);
        this.callerPage = callerPage;
        final CallerPage currentPage = new CallerPage(this);

        // Initialisation du modèle
        moleculeModel = new GenericLoadableDetachableModel<Molecule>(Molecule.class, idMolecule);

        initProvenanceFields(currentPage);

        add(new Label("Molecule.idMolecule", new PropertyModel<String>(moleculeModel, "idMolecule")));
        add(new Label("Molecule.nomCommun", new PropertyModel<String>(moleculeModel, "nomCommun"))
                .add(new ReplaceEmptyLabelBehavior()));
        add(new Label("Molecule.familleChimique", new PropertyModel<String>(moleculeModel, "familleChimique"))
                .add(new ReplaceEmptyLabelBehavior()));
        add(new Label("Molecule.formuleDevMol", new PropertyModel<String>(moleculeModel, "formuleDevMol")).add(
                new ReplaceEmptyLabelBehavior()).add(
                new MoleculeViewBehavior(new PropertyModel<String>(moleculeModel, "formuleDevMol"), true)));
        add(new ResourceLink<Molecule>("DownloadMolFile", new ByteArrayResource("chemical/x-mdl-molfile", moleculeModel
                .getObject().getFormuleDevMol().getBytes(), "molecule.mol")));
        add(new Label("Molecule.nomIupca", new PropertyModel<String>(moleculeModel, "nomIupca"))
                .add(new ReplaceEmptyLabelBehavior()));
        add(new Label("Molecule.formuleBrute", new PropertyModel<String>(moleculeModel, "formuleBrute"))
                .add(new ReplaceEmptyLabelBehavior()));
        add(new Label("Molecule.masseMolaire", new DisplayDecimalPropertyModel(moleculeModel, "masseMolaire",
                DecimalDisplFormat.SMALL, getLocale())).add(new ReplaceEmptyLabelBehavior()));
        add(new Label("Molecule.nouvMolecul", new DisplayBooleanPropertyModel(moleculeModel, "nouvMolecul", this))
                .add(new ReplaceEmptyLabelBehavior()));

        final MarkupContainer nouvMoleculRefresh = new WebMarkupContainer("Molecule.nouvMolecul.Refresh");
        nouvMoleculRefresh.setOutputMarkupId(true);
        nouvMoleculRefresh.setVisible(moleculeModel.getObject().isNouvMolecul());
        nouvMoleculRefresh.add(new Label("Molecule.campagne", new PropertyModel<Campagne>(moleculeModel, "campagne"))
                .add(new ReplaceEmptyLabelBehavior()));
        nouvMoleculRefresh.add(new Label("Molecule.identifieePar", new PropertyModel<Personne>(moleculeModel,
                "identifieePar")).add(new ReplaceEmptyLabelBehavior()));
        nouvMoleculRefresh.add(new MultiLineLabel("Molecule.publiOrigine", new PropertyModel<String>(moleculeModel,
                "publiOrigine")).add(new ReplaceEmptyLabelBehavior()));
        add(nouvMoleculRefresh);

        add(new MultiLineLabel("Molecule.complement", new PropertyModel<String>(moleculeModel, "complement"))
                .add(new ReplaceEmptyLabelBehavior()));

        add(new PropertyLabelLinkPanel<Personne>("Molecule.createur", new PropertyModel<Personne>(moleculeModel,
                "createur"), getStringModel("Read")) {
            @Override
            public void onClick() {
                setResponsePage(new ReadPersonnePage(getModelObject().getIdPersonne(), currentPage));
            }
        });

        // add list document panel
        ReadListDocumentsPanel readListDocumentsPanel = new ReadListDocumentsPanel("ReadListDocumentsPanel",
                moleculeModel, currentPage);
        add(readListDocumentsPanel);

        // Formulaire des actions
        final Form<Void> formView = new Form<Void>("Form");

        // Action : mise à jour (redirection vers le formulaire)
        Link<Molecule> updateLink = new Link<Molecule>(getResource() + ".Molecule.Update", moleculeModel) {
            @Override
            public void onClick() {
                setResponsePage(new ManageMoleculePage(getModelObject().getIdMolecule(), currentPage));
            }
        };
        formView.add(updateLink);

        // Action : suppression
        Button deleteButton = new SubmittableButton(ACTION_DELETE, ManageLotPage.class, new SubmittableButtonEvents() {
            @Override
            public void onProcess() throws DataConstraintException {
                moleculeService.deleteMolecule(moleculeModel.getObject());
            }

            @Override
            public void onSuccess() {
                successNextPage(ManageMoleculePage.class, ACTION_DELETE);
                redirect();
            }
        });
        deleteButton.setVisibilityAllowed(moleculeService.updateOrdeleteMoleculeEnabled(moleculeModel.getObject(),
                getSession().getUtilisateur()));
        deleteButton.add(new JSConfirmationBehavior(getStringModel("Confirm")));
        deleteButton.setDefaultFormProcessing(false);
        formView.add(deleteButton);

        // Action : retour
        formView.add(new Link<Void>(getResource() + ".Molecule.Back") {
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
     * Init provenance table.
     * 
     * @param formView
     */
    private void initProvenanceFields(final CallerPage currentPage) {
        // Déclaration tableau des provenances
        final MarkupContainer provenanceTable = new WebMarkupContainer("Molecule.provenance.Table");
        provenanceTable.setOutputMarkupId(true);

        final WebMarkupContainer resultNotAccessibleCont = new WebMarkupContainer(
                "Molecule.provenance.resultsNotAccessibles");
        resultNotAccessibleCont.setOutputMarkupPlaceholderTag(true);
        provenanceTable.add(resultNotAccessibleCont);

        // Model de liste des provenances
        final LoadableDetachableModel<List<MoleculeProvenance>> listProvenanceModel = new LoadableDetachableModel<List<MoleculeProvenance>>() {
            @Override
            protected List<MoleculeProvenance> load() {
                boolean isOneResultNotAccessible = false;

                Utilisateur utilisateur = getSession().getUtilisateur();
                List<MoleculeProvenance> moleculeProvenances = moleculeModel.getObject().getProvenances();
                List<MoleculeProvenance> listResults = new ArrayList<MoleculeProvenance>();
                for (MoleculeProvenance res : moleculeProvenances) {
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
        ListView<MoleculeProvenance> provenanceListView = new ListView<MoleculeProvenance>("Molecule.provenance.List",
                listProvenanceModel) {
            @Override
            protected void populateItem(ListItem<MoleculeProvenance> item) {
                if (item.getIndex() % 2 == 1) {
                    item.add(new AttributeModifier("class", item.getIndex() % 2 == 0 ? "even" : "odd"));
                }

                IModel<MoleculeProvenance> provenanceModel = item.getModel();
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

                item.add(
                        new Label("Molecule.provenance.List.presence", new DisplayDecimalPropertyModel(provenanceModel,
                                "pourcentage", DecimalDisplFormat.SMALL, getLocale()))).add(
                        new ReplaceEmptyLabelBehavior());
                if (provenanceModel.getObject().getProduit() instanceof Fraction) {
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
            }
        };
        provenanceTable.add(provenanceListView);
        add(provenanceTable);

        // Selon la non existence d'elements dans la liste on affiche le span
        MarkupContainer noTableProvenances = new WebMarkupContainer("Molecule.provenance.noTable") {
            @Override
            public boolean isVisible() {
                return moleculeModel.getObject().getProvenances().isEmpty();
            }
        };
        add(noTableProvenances);
    }
}
