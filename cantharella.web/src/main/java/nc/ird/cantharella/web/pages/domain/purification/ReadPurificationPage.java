/*
 * #%L
 * Cantharella :: Web
 * $Id: ReadPurificationPage.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.web/src/main/java/nc/ird/cantharella/web/pages/domain/purification/ReadPurificationPage.java $
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

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import nc.ird.cantharella.data.exceptions.DataConstraintException;
import nc.ird.cantharella.data.model.Extrait;
import nc.ird.cantharella.data.model.Fraction;
import nc.ird.cantharella.data.model.MethodePurification;
import nc.ird.cantharella.data.model.ParamMethoPuriEffectif;
import nc.ird.cantharella.data.model.Personne;
import nc.ird.cantharella.data.model.Produit;
import nc.ird.cantharella.data.model.Purification;
import nc.ird.cantharella.service.services.PurificationService;
import nc.ird.cantharella.web.pages.TemplatePage;
import nc.ird.cantharella.web.pages.domain.document.panel.ReadListDocumentsPanel;
import nc.ird.cantharella.web.pages.domain.extraction.ReadExtractionPage;
import nc.ird.cantharella.web.pages.domain.personne.ReadPersonnePage;
import nc.ird.cantharella.web.utils.CallerPage;
import nc.ird.cantharella.web.utils.behaviors.JSConfirmationBehavior;
import nc.ird.cantharella.web.utils.behaviors.ReplaceEmptyLabelBehavior;
import nc.ird.cantharella.web.utils.forms.SubmittableButton;
import nc.ird.cantharella.web.utils.forms.SubmittableButtonEvents;
import nc.ird.cantharella.web.utils.models.DisplayDecimalPropertyModel;
import nc.ird.cantharella.web.utils.models.DisplayDecimalPropertyModel.DecimalDisplFormat;
import nc.ird.cantharella.web.utils.models.DisplayPercentPropertyModel;
import nc.ird.cantharella.web.utils.models.GenericLoadableDetachableModel;
import nc.ird.cantharella.web.utils.panels.PropertyLabelLinkPanel;
import nc.ird.cantharella.web.utils.panels.PropertyLabelLinkProduitPanel;
import nc.ird.cantharella.web.utils.panels.SimpleTooltipPanel;

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
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * Consultation d'une purification
 * 
 * @author Alban Diguer
 */
public final class ReadPurificationPage extends TemplatePage {

    /** Logger */
    //private static final Logger LOG = LoggerFactory.getLogger(ReadPurificationPage.class);
    /** Action : delete */
    public static final String ACTION_DELETE = "Delete";

    /** purification Model */
    private final IModel<Purification> purificationModel;

    /** Service : purifications */
    @SpringBean
    private PurificationService purificationService;

    /** Container pour l'affichage de la description de la méthode */
    MarkupContainer descriptionMethoContainer;

    /** Container pour l'affichage des paramètres de la méthode */
    MarkupContainer paramsMethoContainer;

    /** Page appelante */
    private final CallerPage callerPage;

    /**
     * Constructeur
     * 
     * @param idPurification identifiant de la manip
     * @param callerPage Page appelante
     */
    public ReadPurificationPage(Integer idPurification, final CallerPage callerPage) {
        super(ReadPurificationPage.class);
        this.callerPage = callerPage;
        final CallerPage currentPage = new CallerPage((TemplatePage) getPage());

        // Initialisation du modèle
        purificationModel = new GenericLoadableDetachableModel<Purification>(Purification.class, idPurification);

        add(new Label("Purification.ref", new PropertyModel<String>(purificationModel, "ref"))
                .add(new ReplaceEmptyLabelBehavior()));
        add(new Label("Purification.manipulateur", new PropertyModel<Personne>(purificationModel, "manipulateur"))
                .add(new ReplaceEmptyLabelBehavior()));
        add(new Label("Purification.date", new PropertyModel<Date>(purificationModel, "date"))
                .add(new ReplaceEmptyLabelBehavior()));

        add(new PropertyLabelLinkProduitPanel("Purification.produit", new PropertyModel<Produit>(purificationModel,
                "produit"), (TemplatePage) getPage()) {
            @Override
            public void onClickIfExtrait(Extrait extrait) {
                setResponsePage(new ReadExtractionPage(extrait.getExtraction().getIdExtraction(), currentPage));
            }

            @Override
            public void onClickIfFraction(Fraction fraction) {
                setResponsePage(new ReadPurificationPage(fraction.getPurification().getIdPurification(), currentPage));
            }
        });

        add(new Label("Purification.masseDepart", new PropertyModel<BigInteger>(purificationModel, "masseDepart"))
                .add(new ReplaceEmptyLabelBehavior()));
        add(new MultiLineLabel("Purification.complement", new PropertyModel<String>(purificationModel, "complement"))
                .add(new ReplaceEmptyLabelBehavior()));
        add(new PropertyLabelLinkPanel<Personne>("Purification.createur", new PropertyModel<Personne>(
                purificationModel, "createur"), getStringModel("Read")) {
            @Override
            public void onClick() {
                setResponsePage(new ReadPersonnePage(getModelObject().getIdPersonne(), currentPage));
            }
        });
        // Méthode purification
        final WebMarkupContainer methodeCont = new WebMarkupContainer("Purification.methode");
        methodeCont.add(new Label("Purification.nomMethode", new PropertyModel<MethodePurification>(purificationModel,
                "methode")));
        methodeCont.setOutputMarkupId(true);
        add(methodeCont);

        // Champs pour la méthode
        descriptionMethoContainer = new WebMarkupContainer("Purification.descriptionMethodeCont") {
            @Override
            public boolean isVisible() {
                // description cachée si pas de méthode sélectionnée
                return purificationModel.getObject().getMethode() != null;
            }
        };
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

        ListView<ParamMethoPuriEffectif> paramMethodes = new ListView<ParamMethoPuriEffectif>(
                "Purification.paramsMethode.List", new PropertyModel<List<ParamMethoPuriEffectif>>(purificationModel,
                        "sortedParamsMetho")) {
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
                item.add(new Label("Purification.paramsMethode.valeur", new PropertyModel<String>(param, "valeur"))
                        .add(new ReplaceEmptyLabelBehavior()));
            }
        };
        paramsMethoTable.add(paramMethodes);
        methodeCont.add(paramsMethoContainer);

        add(methodeCont);

        // Fractions
        // Déclaration tableau des fractions
        final MarkupContainer purificationsTable = new WebMarkupContainer("Purification.fractions.Table") {
            @Override
            public boolean isVisible() {
                return purificationModel.getObject().getFractions().size() > 0;
            }
        };
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
                item.add(new Label("Purification.fractions.List.masseObtenue", new DisplayDecimalPropertyModel(item
                        .getModel(), "masseObtenue", DecimalDisplFormat.LARGE, getLocale())));
                item.add(new Label("Purification.fractions.List.rendement", new DisplayPercentPropertyModel(item
                        .getModel(), "rendement", getLocale())).add(new ReplaceEmptyLabelBehavior()));
            }
        });
        add(purificationsTable);

        // Selon la non existence d'elements dans la liste on affiche le span
        add(new WebMarkupContainer("Purification.fractions.noTable") {
            @Override
            public boolean isVisible() {
                return !purificationsTable.isVisible();
            }
        });

        // add list document panel
        ReadListDocumentsPanel readListDocumentsPanel = new ReadListDocumentsPanel("ReadListDocumentsPanel",
                purificationModel, currentPage);
        add(readListDocumentsPanel);

        // Formulaire des actions
        final Form<Void> formView = new Form<Void>("Form");

        // Action : mise à jour (redirection vers le formulaire)
        Link<Purification> updateLink = new Link<Purification>(getResource() + ".Purification.Update",
                new Model<Purification>(purificationModel.getObject())) {
            @Override
            public void onClick() {
                setResponsePage(new ManagePurificationPage(getModelObject().getIdPurification(), currentPage));
            }
        };
        updateLink.setVisibilityAllowed(purificationService.updateOrdeletePurificationEnabled(
                purificationModel.getObject(), getSession().getUtilisateur()));
        formView.add(updateLink);

        // Action : suppression
        Button deleteButton = new SubmittableButton(ACTION_DELETE, ManagePurificationPage.class,
                new SubmittableButtonEvents() {
                    @Override
                    public void onProcess() throws DataConstraintException {
                        purificationService.deletePurification(purificationModel.getObject());
                    }

                    @Override
                    public void onSuccess() {
                        successNextPage(ManagePurificationPage.class, ACTION_DELETE);
                        redirect();
                    }
                });
        deleteButton.setVisibilityAllowed(purificationService.updateOrdeletePurificationEnabled(
                purificationModel.getObject(), getSession().getUtilisateur()));
        deleteButton.add(new JSConfirmationBehavior(getStringModel("Confirm")));
        deleteButton.setDefaultFormProcessing(false);
        formView.add(deleteButton);

        // Action : retour
        formView.add(new Link<Void>(getResource() + ".Purification.Back") {
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
}
