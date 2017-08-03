/*
 * #%L
 * Cantharella :: Web
 * $Id: ListExtractionsPage.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.web/src/main/java/nc/ird/cantharella/web/pages/domain/extraction/ListExtractionsPage.java $
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

import java.util.ArrayList;
import java.util.List;

import nc.ird.cantharella.data.model.Extraction;
import nc.ird.cantharella.service.services.ExtractionService;
import nc.ird.cantharella.web.config.WebContext;
import nc.ird.cantharella.web.pages.TemplatePage;
import nc.ird.cantharella.web.pages.domain.campagne.ReadCampagnePage;
import nc.ird.cantharella.web.pages.domain.document.DocumentTooltipColumn;
import nc.ird.cantharella.web.pages.domain.lot.ReadLotPage;
import nc.ird.cantharella.web.utils.CallerPage;
import nc.ird.cantharella.web.utils.columns.DecimalPropertyColumn;
import nc.ird.cantharella.web.utils.columns.LinkPropertyColumn;
import nc.ird.cantharella.web.utils.columns.LinkableImagePropertyColumn;
import nc.ird.cantharella.web.utils.columns.ShortDatePropertyColumn;
import nc.ird.cantharella.web.utils.data.TableExportToolbar;
import nc.ird.cantharella.web.utils.models.DisplayDecimalPropertyModel.DecimalDisplFormat;
import nc.ird.cantharella.web.utils.models.LoadableDetachableSortableListDataProvider;
import nc.ird.cantharella.web.utils.security.AuthRole;
import nc.ird.cantharella.web.utils.security.AuthRoles;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * Page de consultation des extractions
 * 
 * @author Adrien Cheype
 */
@AuthRoles({ AuthRole.ADMIN, AuthRole.USER })
public final class ListExtractionsPage extends TemplatePage {

    /** Service : manipExtraction */
    @SpringBean
    private ExtractionService extractionService;

    /**
     * Constructeur
     */
    public ListExtractionsPage() {
        super(ListExtractionsPage.class);

        final CallerPage currentPage = new CallerPage(ListExtractionsPage.class);

        add(new Link<Void>(getResource() + ".NewExtraction") {
            @Override
            public void onClick() {
                setResponsePage(new ManageExtractionPage(currentPage, true));
            }
        });

        // On englobe le "DataView" dans un composant neutre que l'on pourra
        // rafraichir quand la liste évoluera
        final MarkupContainer extractionsRefresh = new WebMarkupContainer(getResource() + ".Extractions.Refresh");
        extractionsRefresh.setOutputMarkupId(true);
        add(extractionsRefresh);

        // Liste des Extractions
        final List<Extraction> extractions = extractionService.listExtractions(getSession().getUtilisateur());

        DataTable<Extraction, String> extractionsDataTable = initExtractionsDataTable(this,
                "ListExtractionsPage.Extractions", currentPage, extractions, extractionService);
        extractionsRefresh.add(extractionsDataTable);

    }

    /**
     * Init data table with extractions list.
     * 
     * This method is static to be reused in several places.
     * 
     * @param templatePage parent page
     * @param componentId data table id
     * @param callerPage caller page
     * @param extractions extractions list
     * @param extractionService extraction service
     * @return data table component
     */
    public static DataTable<Extraction, String> initExtractionsDataTable(final TemplatePage templatePage,
            final String componentId, final CallerPage callerPage, List<Extraction> extractions,
            final ExtractionService extractionService) {

        LoadableDetachableSortableListDataProvider<Extraction> extractionsDataProvider = new LoadableDetachableSortableListDataProvider<Extraction>(
                extractions, templatePage.getSession().getLocale());

        List<IColumn<Extraction, String>> columns = new ArrayList<IColumn<Extraction, String>>();

        columns.add(new LinkableImagePropertyColumn<Extraction, String>("images/read.png", templatePage
                .getStringModel("Read"), templatePage.getStringModel("Read")) {
            @Override
            public void onClick(Item<ICellPopulator<Extraction>> item, String componentId, IModel<Extraction> model) {
                templatePage.setResponsePage(new ReadExtractionPage(model.getObject().getIdExtraction(), callerPage));
            }
        });

        columns.add(new LinkPropertyColumn<Extraction, String>(templatePage.getStringModel("Extraction.lot"), "lot",
                "lot", templatePage.getStringModel("Read")) {
            @Override
            public void onClick(Item<ICellPopulator<Extraction>> item, String componentId, IModel<Extraction> model) {
                templatePage.setResponsePage(new ReadLotPage(model.getObject().getLot().getIdLot(), callerPage));
            }
        });

        columns.add(new LinkPropertyColumn<Extraction, String>(templatePage.getStringModel("Extraction.ref"), "ref",
                "ref", templatePage.getStringModel("Read")) {
            @Override
            public void onClick(Item<ICellPopulator<Extraction>> item, String componentId, IModel<Extraction> model) {
                templatePage.setResponsePage(new ReadExtractionPage(model.getObject().getIdExtraction(), callerPage));
            }
        });

        columns.add(new ShortDatePropertyColumn<Extraction, String>(templatePage.getStringModel("Extraction.date"),
                "date", "date", templatePage.getLocale()));

        columns.add(new PropertyColumn<Extraction, String>(templatePage.getStringModel("Extraction.methode2"),
                "methode", "methode"));

        columns.add(new DecimalPropertyColumn<Extraction, String>(templatePage
                .getStringModel("Extraction.masseDepart2"), "masseDepart", "masseDepart", DecimalDisplFormat.SMALL,
                templatePage.getLocale()));

        columns.add(new LinkPropertyColumn<Extraction, String>(templatePage.getStringModel("Campagne"), "lot.campagne",
                "lot.campagne", templatePage.getStringModel("Read")) {
            @Override
            public void onClick(Item<ICellPopulator<Extraction>> item, String componentId, IModel<Extraction> model) {
                templatePage.setResponsePage(new ReadCampagnePage(model.getObject().getLot().getCampagne()
                        .getIdCampagne(), callerPage));
            }
        });

        columns.add(new ExtraitsColumn(templatePage.getStringModel("Extraction.extraits"), templatePage.getLocale()));

        columns.add(new DocumentTooltipColumn<Extraction, String>(templatePage
                .getStringModel("ListDocumentsPage.AttachedDocuments")) {
            @Override
            public void onClick(IModel<Extraction> model) {
                int idExtraction = model.getObject().getIdExtraction();
                templatePage.setResponsePage(new ReadExtractionPage(idExtraction, callerPage));
            }
        });

        columns.add(new LinkableImagePropertyColumn<Extraction, String>("images/edit.png", templatePage
                .getStringModel("Update"), templatePage.getStringModel("Update")) {
            // pas de lien d'édition si l'utilisateur n'a pas les droits
            @Override
            public void populateItem(Item<ICellPopulator<Extraction>> item, String componentId, IModel<Extraction> model) {
                if (extractionService.updateOrdeleteExtractionEnabled(model.getObject(), templatePage.getSession()
                        .getUtilisateur())) {
                    item.add(new LinkableImagePanel(item, componentId, model));
                } else {
                    // label vide
                    item.add(new Label(componentId));
                }
            }

            @Override
            public void onClick(Item<ICellPopulator<Extraction>> item, String componentId, IModel<Extraction> model) {
                templatePage.setResponsePage(new ManageExtractionPage(model.getObject().getIdExtraction(), callerPage));
            }
        });

        final DataTable<Extraction, String> extractionsDataTable = new AjaxFallbackDefaultDataTable<Extraction, String>(
                componentId, columns, extractionsDataProvider, WebContext.ROWS_PER_PAGE);
        extractionsDataTable.addBottomToolbar(new TableExportToolbar(extractionsDataTable, "extractions", templatePage
                .getSession().getLocale()));
        return extractionsDataTable;
    }
}
