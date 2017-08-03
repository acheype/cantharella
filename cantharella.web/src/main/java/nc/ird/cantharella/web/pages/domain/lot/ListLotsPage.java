/*
 * #%L
 * Cantharella :: Web
 * $Id: ListLotsPage.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.web/src/main/java/nc/ird/cantharella/web/pages/domain/lot/ListLotsPage.java $
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
package nc.ird.cantharella.web.pages.domain.lot;

import java.util.ArrayList;
import java.util.List;

import nc.ird.cantharella.data.model.Lot;
import nc.ird.cantharella.service.services.LotService;
import nc.ird.cantharella.web.config.WebContext;
import nc.ird.cantharella.web.pages.TemplatePage;
import nc.ird.cantharella.web.pages.columns.TaxonomyPropertyColumn;
import nc.ird.cantharella.web.pages.domain.campagne.ReadCampagnePage;
import nc.ird.cantharella.web.pages.domain.document.DocumentTooltipColumn;
import nc.ird.cantharella.web.pages.domain.specimen.ReadSpecimenPage;
import nc.ird.cantharella.web.pages.domain.station.ReadStationPage;
import nc.ird.cantharella.web.utils.CallerPage;
import nc.ird.cantharella.web.utils.columns.DecimalPropertyColumn;
import nc.ird.cantharella.web.utils.columns.EnumPropertyColumn;
import nc.ird.cantharella.web.utils.columns.LinkPropertyColumn;
import nc.ird.cantharella.web.utils.columns.LinkableImagePropertyColumn;
import nc.ird.cantharella.web.utils.columns.MapValuePropertyColumn;
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
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * Page de consultation des lots
 * 
 * @author Mickael Tricot
 * @author Adrien Cheype
 */
@AuthRoles({ AuthRole.ADMIN, AuthRole.USER })
public final class ListLotsPage extends TemplatePage {

    /** Service : lot */
    @SpringBean
    private LotService lotService;

    /**
     * Constructeur
     */
    public ListLotsPage() {
        super(ListLotsPage.class);
        final CallerPage currentPage = new CallerPage(ListLotsPage.class);

        add(new Link<Void>(getResource() + ".NewLot") {
            @Override
            public void onClick() {
                setResponsePage(new ManageLotPage(currentPage, true));
            }
        });

        // On englobe le "DataView" dans un composant neutre que l'on pourra
        // rafraichir quand la liste évoluera
        final MarkupContainer lotsRefresh = new WebMarkupContainer(getResource() + ".Lots.Refresh");
        lotsRefresh.setOutputMarkupId(true);
        add(lotsRefresh);

        // Liste des lots
        final List<Lot> lots = lotService.listLots(getSession().getUtilisateur());
        DataTable<Lot, String> lotsDataTable = initLotsDataTable(this, "ListLotsPage.Lots", currentPage, lots,
                lotService);
        lotsRefresh.add(lotsDataTable);
    }

    /**
     * Init data table with lots list.
     * 
     * This method is static to be reused in several places.
     * 
     * @param templatePage parent page
     * @param componentId data table id
     * @param callerPage caller page
     * @param lots lots list
     * @param lotService lot service
     * @return data table component
     */
    public static DataTable<Lot, String> initLotsDataTable(final TemplatePage templatePage, final String componentId,
            final CallerPage callerPage, List<Lot> lots, final LotService lotService) {
        LoadableDetachableSortableListDataProvider<Lot> lotsDataProvider = new LoadableDetachableSortableListDataProvider<Lot>(
                lots, templatePage.getSession().getLocale());

        List<IColumn<Lot, String>> columns = new ArrayList<IColumn<Lot, String>>();

        columns.add(new LinkableImagePropertyColumn<Lot, String>("images/read.png",
                templatePage.getStringModel("Read"), templatePage.getStringModel("Read")) {
            @Override
            public void onClick(Item<ICellPopulator<Lot>> item, String componentId, IModel<Lot> model) {
                templatePage.setResponsePage(new ReadLotPage(model.getObject().getIdLot(), callerPage));
            }
        });

        columns.add(new LinkPropertyColumn<Lot, String>(templatePage.getStringModel("Lot.ref"), "ref", "ref",
                templatePage.getStringModel("Read")) {
            @Override
            public void onClick(Item<ICellPopulator<Lot>> item, String componentId, IModel<Lot> model) {
                templatePage.setResponsePage(new ReadLotPage(model.getObject().getIdLot(), callerPage));
            }
        });

        columns.add(new PropertyColumn<Lot, String>(templatePage.getStringModel("Lot.dateRecolte2"), "dateRecolte",
                "dateRecolte"));

        columns.add(new LinkPropertyColumn<Lot, String>(templatePage.getStringModel("Lot.station"), "station",
                "station", templatePage.getStringModel("Read")) {
            @Override
            public void onClick(Item<ICellPopulator<Lot>> item, String componentId, IModel<Lot> model) {
                templatePage.setResponsePage(new ReadStationPage(model.getObject().getStation().getIdStation(),
                        callerPage));
            }
        });

        columns.add(new LinkPropertyColumn<Lot, String>(templatePage.getStringModel("Lot.specimenRef2"), "specimenRef",
                "specimenRef", templatePage.getStringModel("Read")) {
            @Override
            public void onClick(Item<ICellPopulator<Lot>> item, String componentId, IModel<Lot> model) {
                templatePage.setResponsePage(new ReadSpecimenPage(model.getObject().getSpecimenRef().getIdSpecimen(),
                        callerPage));
            }
        });

        columns.add(new EnumPropertyColumn<Lot, String>(templatePage.getStringModel("Specimen.typeOrganisme"),
                "specimenRef.typeOrganisme", "specimenRef.typeOrganisme", templatePage));

        columns.add(new TaxonomyPropertyColumn<Lot, String>(templatePage.getStringModel("Specimen.embranchement"),
                "specimenRef.embranchement", "specimenRef.embranchement"));

        columns.add(new TaxonomyPropertyColumn<Lot, String>(templatePage.getStringModel("Specimen.famille"),
                "specimenRef.famille", "specimenRef.famille"));

        columns.add(new TaxonomyPropertyColumn<Lot, String>(templatePage.getStringModel("Specimen.genre"),
                "specimenRef.genre", "specimenRef.genre"));

        columns.add(new TaxonomyPropertyColumn<Lot, String>(templatePage.getStringModel("Specimen.espece"),
                "specimenRef.espece", "specimenRef.espece"));

        columns.add(new PropertyColumn<Lot, String>(templatePage.getStringModel("Lot.partie"), "partie", "partie"));

        columns.add(new DecimalPropertyColumn<Lot, String>(templatePage.getStringModel("Lot.masseFraiche2"),
                "masseFraiche", "masseFraiche", DecimalDisplFormat.SMALL, templatePage.getLocale()));

        columns.add(new DecimalPropertyColumn<Lot, String>(
                new Model<String>(templatePage.getString("Lot.masseSeche2")), "masseSeche", "masseSeche",
                DecimalDisplFormat.SMALL, templatePage.getLocale()));

        columns.add(new LinkPropertyColumn<Lot, String>(templatePage.getStringModel("Lot.campagne"), "campagne",
                "campagne", templatePage.getStringModel("Read")) {
            @Override
            public void onClick(Item<ICellPopulator<Lot>> item, String componentId, IModel<Lot> model) {
                templatePage.setResponsePage(new ReadCampagnePage(model.getObject().getCampagne().getIdCampagne(),
                        callerPage));
            }
        });

        columns.add(new MapValuePropertyColumn<Lot, String, String>(templatePage.getStringModel("Campagne.codePays"),
                "campagne.codePays", "campagne.codePays", WebContext.COUNTRIES.get(templatePage.getSession()
                        .getLocale())));

        columns.add(new DocumentTooltipColumn<Lot, String>(templatePage
                .getStringModel("ListDocumentsPage.AttachedDocuments")) {
            @Override
            public void onClick(IModel<Lot> model) {
                int idLot = model.getObject().getIdLot();
                templatePage.setResponsePage(new ReadLotPage(idLot, callerPage));
            }
        });

        columns.add(new LinkableImagePropertyColumn<Lot, String>("images/edit.png", templatePage
                .getStringModel("Update"), templatePage.getStringModel("Update")) {
            // pas de lien d'édition si l'utilisateur n'a pas les droits
            @Override
            public void populateItem(Item<ICellPopulator<Lot>> item, String componentId, IModel<Lot> model) {
                if (lotService.updateOrdeleteLotEnabled(model.getObject(), templatePage.getSession().getUtilisateur())) {
                    item.add(new LinkableImagePanel(item, componentId, model));
                } else {
                    // label vide
                    item.add(new Label(componentId));
                }
            }

            @Override
            public void onClick(Item<ICellPopulator<Lot>> item, String componentId, IModel<Lot> model) {
                templatePage.setResponsePage(new ManageLotPage(model.getObject().getIdLot(), callerPage));
            }
        });

        final DataTable<Lot, String> lotsDataTable = new AjaxFallbackDefaultDataTable<Lot, String>(componentId,
                columns, lotsDataProvider, WebContext.ROWS_PER_PAGE);
        lotsDataTable.addBottomToolbar(new TableExportToolbar(lotsDataTable, "lots", templatePage.getSession()
                .getLocale()));
        return lotsDataTable;
    }
}
