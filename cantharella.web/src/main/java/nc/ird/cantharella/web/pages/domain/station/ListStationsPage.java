/*
 * #%L
 * Cantharella :: Web
 * $Id: ListStationsPage.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.web/src/main/java/nc/ird/cantharella/web/pages/domain/station/ListStationsPage.java $
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
package nc.ird.cantharella.web.pages.domain.station;

import java.util.ArrayList;
import java.util.List;

import nc.ird.cantharella.data.model.Station;
import nc.ird.cantharella.service.services.StationService;
import nc.ird.cantharella.web.config.WebContext;
import nc.ird.cantharella.web.pages.TemplatePage;
import nc.ird.cantharella.web.pages.domain.document.DocumentTooltipColumn;
import nc.ird.cantharella.web.utils.CallerPage;
import nc.ird.cantharella.web.utils.columns.LinkPropertyColumn;
import nc.ird.cantharella.web.utils.columns.LinkableImagePropertyColumn;
import nc.ird.cantharella.web.utils.columns.MapValuePropertyColumn;
import nc.ird.cantharella.web.utils.data.TableExportToolbar;
import nc.ird.cantharella.web.utils.models.LoadableDetachableSortableListDataProvider;
import nc.ird.cantharella.web.utils.security.AuthRole;
import nc.ird.cantharella.web.utils.security.AuthRoles;

import org.apache.wicket.AttributeModifier;
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
 * Page de consultation des stations
 * 
 * @author Mickael Tricot
 * @author Adrien Cheype
 */
@AuthRoles({ AuthRole.ADMIN, AuthRole.USER })
public final class ListStationsPage extends TemplatePage {

    /** Service : station */
    @SpringBean
    private StationService stationService;

    /**
     * Constructeur
     */
    public ListStationsPage() {
        super(ListStationsPage.class);

        final CallerPage currentPage = new CallerPage(ListStationsPage.class);

        // Lien pour ajouter une nouvelle station
        add(new Link<Station>(getResource() + ".NewStation") {
            @Override
            public void onClick() {
                setResponsePage(new ManageStationPage(currentPage, true));
            }
        });

        // On englobe le "DataView" dans un composant neutre que l'on pourra
        // rafraichir quand la liste évoluera
        final MarkupContainer stationsRefresh = new WebMarkupContainer(getResource() + ".Stations.Refresh");
        stationsRefresh.setOutputMarkupId(true);
        add(stationsRefresh);

        // Liste des stations
        final List<Station> stations = stationService.listStations(getSession().getUtilisateur());
        DataTable<Station, String> stationsDataTable = initStationsDataTable(this, "ListStationsPage.Stations",
                currentPage, stations, stationService);
        stationsRefresh.add(stationsDataTable);

    }

    /**
     * Init data table with stations list.
     * 
     * This method is static to be reused in several places.
     * 
     * @param templatePage parent page
     * @param componentId data table id
     * @param callerPage caller page
     * @param stations stations list
     * @param stationService station service
     * @return data table component
     */
    public static DataTable<Station, String> initStationsDataTable(final TemplatePage templatePage,
            final String componentId, final CallerPage callerPage, List<Station> stations,
            final StationService stationService) {

        LoadableDetachableSortableListDataProvider<Station> stationsDataProvider = new LoadableDetachableSortableListDataProvider<Station>(
                stations, templatePage.getSession().getLocale());

        List<IColumn<Station, String>> columns = new ArrayList<IColumn<Station, String>>();

        columns.add(new LinkableImagePropertyColumn<Station, String>("images/read.png", templatePage
                .getStringModel("Read"), templatePage.getStringModel("Read")) {
            @Override
            public void onClick(Item<ICellPopulator<Station>> item, String componentId, IModel<Station> model) {
                templatePage.setResponsePage(new ReadStationPage(model.getObject().getIdStation(), callerPage));
            }
        });

        columns.add(new LinkPropertyColumn<Station, String>(templatePage.getStringModel("Station.nom"), "nom", "nom",
                templatePage.getStringModel("Read")) {
            @Override
            public void onClick(Item<ICellPopulator<Station>> item, String componentId, IModel<Station> model) {
                templatePage.setResponsePage(new ReadStationPage(model.getObject().getIdStation(), callerPage));
            }
        });

        columns.add(new MapValuePropertyColumn<Station, String, String>(
                templatePage.getStringModel("Station.codePays"), "codePays", "codePays", WebContext.COUNTRIES
                        .get(templatePage.getSession().getLocale())));

        columns.add(new PropertyColumn<Station, String>(templatePage.getStringModel("Station.localite"), "localite",
                "localite"));

        columns.add(new PropertyColumn<Station, String>(templatePage.getStringModel("Station.latitude"), "latitude",
                "latitude"));

        columns.add(new PropertyColumn<Station, String>(templatePage.getStringModel("Station.longitude"), "longitude",
                "longitude"));

        columns.add(new DocumentTooltipColumn<Station, String>(templatePage
                .getStringModel("ListDocumentsPage.AttachedDocuments")) {
            @Override
            public void onClick(IModel<Station> model) {
                int idStation = model.getObject().getIdStation();
                templatePage.setResponsePage(new ReadStationPage(idStation, callerPage));
            }
        });

        columns.add(new LinkableImagePropertyColumn<Station, String>("images/edit.png", templatePage
                .getStringModel("Update"), templatePage.getStringModel("Update")) {
            // pas de lien d'édition si l'utilisateur n'a pas les droits
            @Override
            public void populateItem(Item<ICellPopulator<Station>> item, String componentId, IModel<Station> model) {
                if (item.getIndex() % 2 == 1) {
                    item.add(new AttributeModifier("class", item.getIndex() % 2 == 0 ? "even" : "odd"));
                }

                if (stationService.updateOrdeleteStationEnabled(model.getObject(), templatePage.getSession()
                        .getUtilisateur())) {
                    item.add(new LinkableImagePanel(item, componentId, model));
                } else {
                    // label vide
                    item.add(new Label(componentId));
                }
            }

            @Override
            public void onClick(Item<ICellPopulator<Station>> item, String componentId, IModel<Station> model) {
                templatePage.setResponsePage(new ManageStationPage(model.getObject().getIdStation(), callerPage));
            }
        });

        final DataTable<Station, String> stationsDataTable = new AjaxFallbackDefaultDataTable<Station, String>(
                componentId, columns, stationsDataProvider, WebContext.ROWS_PER_PAGE);
        stationsDataTable.addBottomToolbar(new TableExportToolbar(stationsDataTable, "stations", templatePage
                .getSession().getLocale()));
        return stationsDataTable;
    }
}
