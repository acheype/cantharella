/*
 * #%L
 * Cantharella :: Web
 * $Id: ReadExtractionPage.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.web/src/main/java/nc/ird/cantharella/web/pages/domain/extraction/ReadExtractionPage.java $
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

import java.util.Date;
import java.util.List;

import nc.ird.cantharella.data.exceptions.DataConstraintException;
import nc.ird.cantharella.data.model.Extraction;
import nc.ird.cantharella.data.model.Extrait;
import nc.ird.cantharella.data.model.Lot;
import nc.ird.cantharella.data.model.MethodeExtraction;
import nc.ird.cantharella.data.model.Personne;
import nc.ird.cantharella.service.services.ExtractionService;
import nc.ird.cantharella.web.pages.TemplatePage;
import nc.ird.cantharella.web.pages.domain.document.panel.ReadListDocumentsPanel;
import nc.ird.cantharella.web.pages.domain.lot.ReadLotPage;
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
 * Consultation d'une extraction
 * 
 * @author Alban Diguer
 */
public final class ReadExtractionPage extends TemplatePage {

    /** Action : delete */
    public static final String ACTION_DELETE = "Delete";

    /** Logger */
    //private static final Logger LOG = LoggerFactory.getLogger(ReadExtractionPage.class);
    /** extraction Model */
    private final IModel<Extraction> extractionModel;

    /** Service : extraits */
    @SpringBean
    private ExtractionService extractionService;

    /** Page appelante */
    private final CallerPage callerPage;

    /** Container contenant la liste des extraits */
    private MarkupContainer extraitsTable;

    /**
     * Constructeur
     * 
     * @param idExtraction identifiant de la manip
     * @param callerPage Page appelante
     */
    public ReadExtractionPage(Integer idExtraction, final CallerPage callerPage) {
        super(ReadExtractionPage.class);
        this.callerPage = callerPage;
        final CallerPage currentPage = new CallerPage((TemplatePage) getPage());

        // Initialisation du modèle
        extractionModel = new GenericLoadableDetachableModel<Extraction>(Extraction.class, idExtraction);

        add(new Label("Extraction.ref", new PropertyModel<String>(extractionModel, "ref"))
                .add(new ReplaceEmptyLabelBehavior()));

        add(new PropertyLabelLinkPanel<Personne>("Extraction.manipulateur", new PropertyModel<Personne>(
                extractionModel, "manipulateur"), getStringModel("Read")) {
            @Override
            public void onClick() {
                setResponsePage(new ReadPersonnePage(getModelObject().getIdPersonne(), currentPage));
            }
        });

        add(new Label("Extraction.date", new PropertyModel<Date>(extractionModel, "date"))
                .add(new ReplaceEmptyLabelBehavior()));

        add(new PropertyLabelLinkPanel<Lot>("Extraction.lot", new PropertyModel<Lot>(extractionModel, "lot"),
                getStringModel("Read")) {
            @Override
            public void onClick() {
                setResponsePage(new ReadLotPage(getModelObject().getIdLot(), currentPage));
            }
        });
        add(new Label("Extraction.masseDepart", new DisplayDecimalPropertyModel(extractionModel, "masseDepart",
                DecimalDisplFormat.SMALL, getLocale())).add(new ReplaceEmptyLabelBehavior()));
        add(new MultiLineLabel("Extraction.complement", new PropertyModel<String>(extractionModel, "complement"))
                .add(new ReplaceEmptyLabelBehavior()));

        add(new PropertyLabelLinkPanel<Personne>("Extraction.createur", new PropertyModel<Personne>(extractionModel,
                "createur"), getStringModel("Read")) {
            @Override
            public void onClick() {
                setResponsePage(new ReadPersonnePage(getModelObject().getIdPersonne(), currentPage));
            }
        });

        // Méthode d'extraction
        add(new MultiLineLabel("Extraction.methode.description", new PropertyModel<String>(extractionModel,
                "methode.description")).add(new ReplaceEmptyLabelBehavior()));
        add(new Label("Extraction.methode.nom", new PropertyModel<MethodeExtraction>(extractionModel, "methode"))
                .add(new ReplaceEmptyLabelBehavior()));

        // Déclaration tableau des extraits
        extraitsTable = new WebMarkupContainer("Extraction.extraits.Table") {
            @Override
            public boolean isVisible() {
                return extractionModel.getObject().getExtraits().size() > 0;
            }
        };
        extraitsTable.setOutputMarkupId(true);

        // Contenu tableaux extrait
        // Liste des types extraits ajoutés
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
                item.add(new Label("Extraction.extraits.List.masseObtenue", new DisplayDecimalPropertyModel(
                        extraitModel, "masseObtenue", DecimalDisplFormat.SMALL, getLocale())));

                item.add(new Label("Extraction.extraits.List.rendement", new DisplayPercentPropertyModel(extraitModel,
                        "rendement", getLocale())).add(new ReplaceEmptyLabelBehavior()));
            }
        });
        add(extraitsTable);

        // Selon la non existence d'elements dans la liste on affiche le span
        add(new WebMarkupContainer("Extraction.extraits.noTable") {
            @Override
            public boolean isVisible() {
                return !extraitsTable.isVisible();
            }
        });

        // add list document panel
        ReadListDocumentsPanel readListDocumentsPanel = new ReadListDocumentsPanel("ReadListDocumentsPanel",
                extractionModel, currentPage);
        add(readListDocumentsPanel);

        // Formulaire des actions
        final Form<Void> formView = new Form<Void>("Form");

        // Action : mise à jour (redirection vers le formulaire)
        Link<Extraction> updateLink = new Link<Extraction>(getResource() + ".Extraction.Update", new Model<Extraction>(
                extractionModel.getObject())) {
            @Override
            public void onClick() {
                setResponsePage(new ManageExtractionPage(getModelObject().getIdExtraction(), currentPage));
            }
        };
        updateLink.setVisibilityAllowed(extractionService.updateOrdeleteExtractionEnabled(extractionModel.getObject(),
                getSession().getUtilisateur()));
        formView.add(updateLink);

        // Action : suppression
        Button deleteButton = new SubmittableButton(ACTION_DELETE, ManageExtractionPage.class,
                new SubmittableButtonEvents() {
                    @Override
                    public void onProcess() throws DataConstraintException {
                        extractionService.deleteExtraction(extractionModel.getObject());
                    }

                    @Override
                    public void onSuccess() {
                        successNextPage(ManageExtractionPage.class, ACTION_DELETE);
                        redirect();
                    }
                });
        deleteButton.setVisibilityAllowed(extractionService.updateOrdeleteExtractionEnabled(
                extractionModel.getObject(), getSession().getUtilisateur()));
        deleteButton.add(new JSConfirmationBehavior(getStringModel("Confirm")));
        deleteButton.setDefaultFormProcessing(false);
        formView.add(deleteButton);

        // Action : retour
        formView.add(new Link<Void>(getResource() + ".Extraction.Back") {
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
