/*
 * #%L
 * Cantharella :: Web
 * $Id: ReadTestBioPage.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.web/src/main/java/nc/ird/cantharella/web/pages/domain/testBio/ReadTestBioPage.java $
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
import java.util.ArrayList;
import java.util.List;

import nc.ird.cantharella.data.exceptions.DataConstraintException;
import nc.ird.cantharella.data.model.Extrait;
import nc.ird.cantharella.data.model.Fraction;
import nc.ird.cantharella.data.model.Personne;
import nc.ird.cantharella.data.model.Produit;
import nc.ird.cantharella.data.model.ResultatTestBio;
import nc.ird.cantharella.data.model.ResultatTestBio.TypeResultat;
import nc.ird.cantharella.data.model.TestBio;
import nc.ird.cantharella.service.services.TestBioService;
import nc.ird.cantharella.web.pages.TemplatePage;
import nc.ird.cantharella.web.pages.domain.document.panel.ReadListDocumentsPanel;
import nc.ird.cantharella.web.pages.domain.extraction.ReadExtractionPage;
import nc.ird.cantharella.web.pages.domain.personne.ReadPersonnePage;
import nc.ird.cantharella.web.pages.domain.purification.ReadPurificationPage;
import nc.ird.cantharella.web.utils.CallerPage;
import nc.ird.cantharella.web.utils.behaviors.JSConfirmationBehavior;
import nc.ird.cantharella.web.utils.behaviors.ReplaceEmptyLabelBehavior;
import nc.ird.cantharella.web.utils.forms.SubmittableButton;
import nc.ird.cantharella.web.utils.forms.SubmittableButtonEvents;
import nc.ird.cantharella.web.utils.models.DisplayBooleanPropertyModel;
import nc.ird.cantharella.web.utils.models.DisplayDecimalPropertyModel;
import nc.ird.cantharella.web.utils.models.DisplayDecimalPropertyModel.DecimalDisplFormat;
import nc.ird.cantharella.web.utils.models.DisplayEnumPropertyModel;
import nc.ird.cantharella.web.utils.models.DisplayShortDatePropertyModel;
import nc.ird.cantharella.web.utils.models.GenericLoadableDetachableModel;
import nc.ird.cantharella.web.utils.panels.PropertyLabelLinkPanel;
import nc.ird.cantharella.web.utils.panels.PropertyLabelLinkProduitPanel;
import nc.ird.cantharella.web.utils.panels.SimpleTooltipPanel;
import nc.ird.cantharella.web.utils.security.AuthSession;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * Consultation d'une manipulation de test biologique
 * 
 * @author Alban Diguer
 */
public final class ReadTestBioPage extends TemplatePage {

    /** Logger */
    //private static final Logger LOG = LoggerFactory.getLogger(ReadTestBioPage.class);
    /** Action : update */
    private static final String ACTION_DELETE = "Delete";

    /** testBio Model */
    private IModel<TestBio> testBioModel;

    /** Service : testBios */
    @SpringBean
    private TestBioService testBioService;

    /** Page appelante */
    private final CallerPage callerPage;

    /** Bouton d'ajout d'un résultat de test bio **/
    Button addResultatButton;

    /** Container pour l'affichage des paramètres de la méthode **/
    MarkupContainer paramsMethoContainer;

    /**
     * Constructeur
     * 
     * @param idTestBio identifiant de la manip
     * @param callerPage Page appelante
     */
    public ReadTestBioPage(Integer idTestBio, CallerPage callerPage) {
        super(ReadTestBioPage.class);
        final CallerPage currentPage = new CallerPage((TemplatePage) getPage());
        this.callerPage = callerPage;

        // Initialisation du modèle
        testBioModel = new GenericLoadableDetachableModel<TestBio>(TestBio.class, idTestBio);

        final TestBio testBio = testBioModel.getObject();

        initPrincipalFields(testBioModel, currentPage);
        initTestMethodFields(idTestBio);
        initResultsFields(idTestBio, currentPage);

        // add list document panel
        ReadListDocumentsPanel readListDocumentsPanel = new ReadListDocumentsPanel("ReadListDocumentsPanel",
                testBioModel, currentPage);
        add(readListDocumentsPanel);

        // Ajout du formulaire pour les actions
        Form<Void> formView = new Form<Void>("Form");

        // Action : mise à jour (redirection vers le formulaire)
        Link<TestBio> updateLink = new Link<TestBio>(getResource() + ".TestBio.Update", new Model<TestBio>(testBio)) {
            @Override
            public void onClick() {
                setResponsePage(new ManageTestBioPage(getModelObject().getIdTestBio(), currentPage));
            }
        };
        updateLink.setVisibilityAllowed(testBioService.updateOrdeleteTestBioEnabled(testBio, getSession()
                .getUtilisateur()));
        formView.add(updateLink);

        // Action : retour à la page précédente
        formView.add(new Link<Void>(getResource() + ".TestBio.Back") {
            @Override
            public void onClick() {
                redirect();
            }
        });

        // Action : suppression de la testBio
        Button deleteButton = new SubmittableButton(ACTION_DELETE, ManageTestBioPage.class,
                new SubmittableButtonEvents() {

                    @Override
                    public void onProcess() throws DataConstraintException {
                        testBioService.deleteTestBio(testBio);
                    }

                    @Override
                    public void onSuccess() {
                        successNextPage(ManageTestBioPage.class, ACTION_DELETE);
                        redirect();
                    }
                });
        deleteButton.setVisibilityAllowed(testBioService.updateOrdeleteTestBioEnabled(testBio, getSession()
                .getUtilisateur()));
        deleteButton.add(new JSConfirmationBehavior(getStringModel("Confirm")));
        formView.add(deleteButton);
        add(formView);
    }

    /**
     * Bind principal data to the markup
     * 
     * @param testBioModel tbm
     * @param currentPage currentPage
     */
    private void initPrincipalFields(IModel<TestBio> testBioModel, final CallerPage currentPage) {

        add(new Label("TestBio.ref", new PropertyModel<String>(testBioModel, "ref"))
                .add(new ReplaceEmptyLabelBehavior()));

        add(new PropertyLabelLinkPanel<Personne>("TestBio.manipulateur", new PropertyModel<Personne>(testBioModel,
                "manipulateur"), getStringModel("Read")) {
            @Override
            public void onClick() {
                setResponsePage(new ReadPersonnePage(getModelObject().getIdPersonne(), currentPage));
            }
        });

        add(new Label("TestBio.organismeTesteur", new PropertyModel<String>(testBioModel, "organismeTesteur"))
                .add(new ReplaceEmptyLabelBehavior()));
        add(new Label("TestBio.date", new DisplayShortDatePropertyModel(testBioModel, "date", getLocale()))
                .add(new ReplaceEmptyLabelBehavior()));

        // concatenation de concMasse par défaut et de uniteConcMasse par défaut
        add(new Label("TestBio.concMasseDefaut", new Model<Serializable>(testBioModel) {
            /** {@inheritDoc} */
            @Override
            public String getObject() {
                String masse = (String) new DisplayDecimalPropertyModel(super.getObject(), "concMasseDefaut",
                        DecimalDisplFormat.SMALL, getLocale()).getObject();

                String unite = (String) new DisplayEnumPropertyModel(super.getObject(), "uniteConcMasseDefaut",
                        (TemplatePage) getPage()).getObject();

                if (masse == null && unite == null) {
                    return ReplaceEmptyLabelBehavior.NULL_PROPERTY;
                } else if (masse == null) {
                    return ReplaceEmptyLabelBehavior.NULL_PROPERTY + unite;
                } else if (unite == null) {
                    return masse + ReplaceEmptyLabelBehavior.NULL_PROPERTY;
                }
                return masse + " " + unite;
            }
        }));

        add(new Label("TestBio.stadeDefaut", new DisplayEnumPropertyModel(testBioModel, "stadeDefaut", this))
                .add(new ReplaceEmptyLabelBehavior()));
        add(new MultiLineLabel("TestBio.complement", new PropertyModel<String>(testBioModel, "complement"))
                .add(new ReplaceEmptyLabelBehavior()));
        add(new PropertyLabelLinkPanel<Personne>("TestBio.createur", new PropertyModel<Personne>(testBioModel,
                "createur"), getStringModel("Read")) {
            @Override
            public void onClick() {
                setResponsePage(new ReadPersonnePage(getModelObject().getIdPersonne(), currentPage));
            }
        });
    }

    /**
     * Bind fields for test method to the markup
     * 
     * @param idTestBio itb
     */
    private void initTestMethodFields(Integer idTestBio) {
        WebMarkupContainer methodeCont = new WebMarkupContainer("TestBio.methode") {
            @Override
            public boolean isVisible() {
                // description cachée si pas de méthode sélectionnée
                return testBioModel.getObject().getMethode() != null;
            }
        };
        methodeCont.setOutputMarkupId(true);
        add(methodeCont);

        Label methodeCible = new Label("TestBio.cibleMethode", new PropertyModel<String>(testBioModel, "methode.cible"));
        methodeCont.add(methodeCible);

        Label methodeDomaine = new Label("TestBio.domaineMethode", new PropertyModel<String>(testBioModel,
                "methode.domaine"));
        methodeCont.add(methodeDomaine);

        MultiLineLabel methodeDesc = new MultiLineLabel("TestBio.descriptionMethode", new PropertyModel<String>(
                testBioModel, "methode.description"));
        methodeCont.add(methodeDesc);

        Label methodeValeurMesuree = new Label("TestBio.valeurMesureeMethode", new PropertyModel<String>(testBioModel,
                "methode.valeurMesuree"));
        methodeCont.add(methodeValeurMesuree);

        Label methodeCritereActivite = new Label("TestBio.critereActiviteMethode", new PropertyModel<String>(
                testBioModel, "methode.critereActivite"));
        methodeCont.add(methodeCritereActivite);

        Label methodeUniteResultat = new Label("TestBio.uniteResultatMethode", new PropertyModel<String>(testBioModel,
                "methode.uniteResultat"));
        methodeCont.add(methodeUniteResultat);

        Label nomMethode = new Label("TestBio.nomMethode", new PropertyModel<String>(testBioModel, "methode.nom"));
        methodeCont.add(nomMethode);

    }

    /**
     * Bind fields concerning results
     * 
     * @param idTestBio itb
     * @param currentPage currentPage
     */
    private void initResultsFields(Integer idTestBio, final CallerPage currentPage) {
        // Déclaration tableau des resultats
        // Pas de possibilité d'avoir le tableau vide car toujours au moins un résultat de type produit
        final MarkupContainer testsBioTable = new WebMarkupContainer("TestBio.resultats.Table");
        testsBioTable.setOutputMarkupId(true);
        add(testsBioTable);

        final WebMarkupContainer resultNotAccessibleCont = new WebMarkupContainer(
                "TestBio.resultats.resultsNotAccessibles");
        resultNotAccessibleCont.setOutputMarkupPlaceholderTag(true);
        testsBioTable.add(resultNotAccessibleCont);

        // Model de liste des résultats
        final LoadableDetachableModel<List<ResultatTestBio>> listResultsModel = new LoadableDetachableModel<List<ResultatTestBio>>() {
            @Override
            protected List<ResultatTestBio> load() {
                boolean isOneResultNotAccessible = false;

                List<ResultatTestBio> listResults = new ArrayList<ResultatTestBio>();

                for (ResultatTestBio res : testBioModel.getObject().getSortedResultats()) {
                    // les résultats de type blanc ou témoin sont tjr accessibles
                    if (res.getTypeResultat() != TypeResultat.PRODUIT
                            || testBioService.isResultatTestBioAccessibleByUser(res, ((AuthSession) getPage()
                                    .getSession()).getUtilisateur())) {
                        listResults.add(res);
                    } else {
                        isOneResultNotAccessible = true;
                    }
                }
                // si un des résultats non accessible, on rend visible le message d'avertissement
                resultNotAccessibleCont.setVisibilityAllowed(isOneResultNotAccessible);
                return listResults;
            }
        };

        // Contenu tableaux resultats
        testsBioTable.add(new ListView<ResultatTestBio>("TestBio.resultats.List", listResultsModel) {
            @Override
            protected void populateItem(ListItem<ResultatTestBio> item) {
                if (item.getIndex() % 2 == 1) {
                    item.add(new AttributeModifier("class", item.getIndex() % 2 == 0 ? "even" : "odd"));
                }

                final IModel<ResultatTestBio> resultatModel = item.getModel();
                final ResultatTestBio resultat = resultatModel.getObject();
                // Colonnes
                item.add(new Label("TestBio.resultats.List.repere", new PropertyModel<String>(resultat, "repere")));
                item.add(new Label("TestBio.resultats.List.typeResultat", new DisplayEnumPropertyModel(resultat,
                        "typeResultat", (TemplatePage) this.getPage())));

                item.add(new PropertyLabelLinkProduitPanel("TestBio.resultats.List.produit",
                        new PropertyModel<Produit>(resultat, "produit"), (TemplatePage) getPage()) {
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

                item.add(new Label("TestBio.resultats.List.produitTemoin", new PropertyModel<String>(resultat,
                        "produitTemoin")));
                // concaténation de concMasse et uniteMasse
                item.add(new Label("TestBio.resultats.List.concMasse", new Model<Serializable>(resultat) {

                    /** {@inheritDoc} */
                    @Override
                    public String getObject() {
                        String masse = (String) new DisplayDecimalPropertyModel(super.getObject(), "concMasse",
                                DecimalDisplFormat.SMALL, getLocale()).getObject();

                        String unite = (String) new DisplayEnumPropertyModel(super.getObject(), "uniteConcMasse",
                                (TemplatePage) getPage()).getObject();

                        if (masse == null && unite == null) {
                            return ReplaceEmptyLabelBehavior.NULL_PROPERTY;
                        } else if (masse == null) {
                            return ReplaceEmptyLabelBehavior.NULL_PROPERTY + unite;
                        } else if (unite == null) {
                            return masse + ReplaceEmptyLabelBehavior.NULL_PROPERTY;
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
            }
        });
    }

    /**
     * Redirection vers une autre page
     */
    private void redirect() {
        callerPage.responsePage(this);
    }
}
