/*
 * #%L
 * Cantharella :: Web
 * $Id: ListPurificationsPage.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.web/src/main/java/nc/ird/cantharella/web/pages/domain/purification/ListPurificationsPage.java $
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

import java.util.ArrayList;
import java.util.List;

import nc.ird.cantharella.data.model.Extrait;
import nc.ird.cantharella.data.model.Fraction;
import nc.ird.cantharella.data.model.Purification;
import nc.ird.cantharella.service.services.PurificationService;
import nc.ird.cantharella.web.config.WebContext;
import nc.ird.cantharella.web.pages.TemplatePage;
import nc.ird.cantharella.web.pages.columns.LinkProduitPropertyColumn;
import nc.ird.cantharella.web.pages.domain.campagne.ReadCampagnePage;
import nc.ird.cantharella.web.pages.domain.document.DocumentTooltipColumn;
import nc.ird.cantharella.web.pages.domain.extraction.ReadExtractionPage;
import nc.ird.cantharella.web.pages.domain.lot.ReadLotPage;
import nc.ird.cantharella.web.utils.CallerPage;
import nc.ird.cantharella.web.utils.columns.LinkPropertyColumn;
import nc.ird.cantharella.web.utils.columns.LinkableImagePropertyColumn;
import nc.ird.cantharella.web.utils.columns.ShortDatePropertyColumn;
import nc.ird.cantharella.web.utils.data.TableExportToolbar;
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
 * Page de consultation des purification
 * 
 * @author Adrien Cheype
 */
@AuthRoles({ AuthRole.ADMIN, AuthRole.USER })
public final class ListPurificationsPage extends TemplatePage {

    /** Service : manipPurification */
    @SpringBean
    private PurificationService purificationService;

    /**
     * Constructeur
     */
    public ListPurificationsPage() {
        super(ListPurificationsPage.class);

        final CallerPage currentPage = new CallerPage(ListPurificationsPage.class);

        add(new Link<Void>(getResource() + ".NewPurification") {
            @Override
            public void onClick() {
                setResponsePage(new ManagePurificationPage(currentPage, true));
            }
        });

        // On englobe le "DataView" dans un composant neutre que l'on pourra
        // rafraichir quand la liste évoluera
        final MarkupContainer purificationsRefresh = new WebMarkupContainer(getResource() + ".Purifications.Refresh");
        purificationsRefresh.setOutputMarkupId(true);
        add(purificationsRefresh);

        // Liste des Purifications
        final List<Purification> purifications = purificationService.listPurifications(getSession().getUtilisateur());

        DataTable<Purification, String> purificationsDataTable = initPurificationsDataTable(this,
                "ListPurificationsPage.Purifications", currentPage, purifications, purificationService);
        purificationsRefresh.add(purificationsDataTable);

    }

    /**
     * Init data table with purification list.
     * 
     * This method is static to be reused in several places.
     * 
     * @param templatePage parent page
     * @param componentId data table id
     * @param callerPage caller page
     * @param purifications purifications list
     * @param purificationService purification service
     * @return data table component
     */
    public static DataTable<Purification, String> initPurificationsDataTable(final TemplatePage templatePage,
            final String componentId, final CallerPage callerPage, List<Purification> purifications,
            final PurificationService purificationService) {

        LoadableDetachableSortableListDataProvider<Purification> purificationsDataProvider = new LoadableDetachableSortableListDataProvider<Purification>(
                purifications, templatePage.getSession().getLocale());

        List<IColumn<Purification, String>> columns = new ArrayList<IColumn<Purification, String>>();

        columns.add(new LinkableImagePropertyColumn<Purification, String>("images/read.png", templatePage
                .getStringModel("Read"), templatePage.getStringModel("Read")) {
            @Override
            public void onClick(Item<ICellPopulator<Purification>> item, String componentId, IModel<Purification> model) {
                templatePage
                        .setResponsePage(new ReadPurificationPage(model.getObject().getIdPurification(), callerPage));
            }
        });

        columns.add(new LinkPropertyColumn<Purification, String>(templatePage.getStringModel("Extraction.lot"),
                "lotSource", "lotSource", templatePage.getStringModel("Read")) {
            @Override
            public void onClick(Item<ICellPopulator<Purification>> item, String componentId, IModel<Purification> model) {
                templatePage.setResponsePage(new ReadLotPage(model.getObject().getLotSource().getIdLot(), callerPage));
            }
        });

        columns.add(new LinkProduitPropertyColumn<Purification, String>(templatePage
                .getStringModel("ResultatTestBio.produit"), "produit", "produit", templatePage) {
            @Override
            public void onClickIfExtrait(Extrait extrait) {
                templatePage.setResponsePage(new ReadExtractionPage(extrait.getExtraction().getIdExtraction(),
                        callerPage));
            }

            @Override
            public void onClickIfFraction(Fraction fraction) {
                templatePage.setResponsePage(new ReadPurificationPage(fraction.getPurification().getIdPurification(),
                        callerPage));
            }
        });

        columns.add(new LinkPropertyColumn<Purification, String>(templatePage.getStringModel("Purification.ref"),
                "ref", "ref", templatePage.getStringModel("Read")) {
            @Override
            public void onClick(Item<ICellPopulator<Purification>> item, String componentId, IModel<Purification> model) {
                templatePage
                        .setResponsePage(new ReadPurificationPage(model.getObject().getIdPurification(), callerPage));
            }
        });

        columns.add(new ShortDatePropertyColumn<Purification, String>(templatePage.getStringModel("Purification.date"),
                "date", "date", templatePage.getLocale()));

        columns.add(new PropertyColumn<Purification, String>(templatePage.getStringModel("Purification.methode2"),
                "methode", "methode"));

        columns.add(new LinkPropertyColumn<Purification, String>(templatePage.getStringModel("Campagne"),
                "lotSource.campagne", "lotSource.campagne", templatePage.getStringModel("Read")) {
            @Override
            public void onClick(Item<ICellPopulator<Purification>> item, String componentId, IModel<Purification> model) {
                templatePage.setResponsePage(new ReadCampagnePage(model.getObject().getLotSource().getCampagne()
                        .getIdCampagne(), callerPage));
            }
        });

        columns.add(new DocumentTooltipColumn<Purification, String>(templatePage
                .getStringModel("ListDocumentsPage.AttachedDocuments")) {
            @Override
            public void onClick(IModel<Purification> model) {
                int idPurification = model.getObject().getIdPurification();
                templatePage.setResponsePage(new ReadPurificationPage(idPurification, callerPage));
            }
        });

        columns.add(new LinkableImagePropertyColumn<Purification, String>("images/edit.png", templatePage
                .getStringModel("Update"), templatePage.getStringModel("Update")) {
            // pas de lien d'édition si l'utilisateur n'a pas les droits
            @Override
            public void populateItem(Item<ICellPopulator<Purification>> item, String componentId,
                    IModel<Purification> model) {
                if (purificationService.updateOrdeletePurificationEnabled(model.getObject(), templatePage.getSession()
                        .getUtilisateur())) {
                    item.add(new LinkableImagePanel(item, componentId, model));
                } else {
                    // label vide
                    item.add(new Label(componentId));
                }
            }

            @Override
            public void onClick(Item<ICellPopulator<Purification>> item, String componentId, IModel<Purification> model) {
                templatePage.setResponsePage(new ManagePurificationPage(model.getObject().getIdPurification(),
                        callerPage));
            }
        });

        final DataTable<Purification, String> purificationsDataTable = new AjaxFallbackDefaultDataTable<Purification, String>(
                componentId, columns, purificationsDataProvider, WebContext.ROWS_PER_PAGE);
        purificationsDataTable.addBottomToolbar(new TableExportToolbar(purificationsDataTable, "purifications",
                templatePage.getSession().getLocale()));
        return purificationsDataTable;

    }
}
