/*
 * #%L
 * Cantharella :: Web
 * $Id: ListSpecimensPage.java 268 2014-05-06 15:45:40Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.web/src/main/java/nc/ird/cantharella/web/pages/domain/specimen/ListSpecimensPage.java $
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
package nc.ird.cantharella.web.pages.domain.specimen;

import java.util.ArrayList;
import java.util.List;

import nc.ird.cantharella.data.model.Specimen;
import nc.ird.cantharella.service.services.SpecimenService;
import nc.ird.cantharella.web.config.WebContext;
import nc.ird.cantharella.web.pages.TemplatePage;
import nc.ird.cantharella.web.pages.columns.TaxonomyPropertyColumn;
import nc.ird.cantharella.web.pages.domain.document.DocumentTooltipColumn;
import nc.ird.cantharella.web.pages.domain.station.ReadStationPage;
import nc.ird.cantharella.web.utils.CallerPage;
import nc.ird.cantharella.web.utils.columns.EnumPropertyColumn;
import nc.ird.cantharella.web.utils.columns.LinkPropertyColumn;
import nc.ird.cantharella.web.utils.columns.LinkableImagePropertyColumn;
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
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * Page de consultation des spécimens
 * 
 * @author Mickael Tricot
 * @author Adrien Cheype
 */
@AuthRoles({ AuthRole.ADMIN, AuthRole.USER })
public final class ListSpecimensPage extends TemplatePage {

    /** Service : specimen */
    @SpringBean
    private SpecimenService specimenService;

    /**
     * Constructeur
     */
    public ListSpecimensPage() {
        super(ListSpecimensPage.class);

        final CallerPage currentPage = new CallerPage(ListSpecimensPage.class);

        add(new Link<Void>(getResource() + ".NewSpecimen") {
            @Override
            public void onClick() {
                setResponsePage(new ManageSpecimenPage(currentPage, true));
            }
        });

        // On englobe le "DataView" dans un composant neutre que l'on pourra
        // rafraichir quand la liste évoluera
        final MarkupContainer specimensRefresh = new WebMarkupContainer(getResource() + ".Specimens.Refresh");
        specimensRefresh.setOutputMarkupId(true);
        add(specimensRefresh);

        // Liste des Specimens
        final List<Specimen> specimens = specimenService.listSpecimens(getSession().getUtilisateur());
        DataTable<Specimen, String> specimensDataTable = initSpecimensDataTable(this, "ListSpecimensPage.Specimens",
                currentPage, specimens, specimenService);
        specimensRefresh.add(specimensDataTable);
    }

    /**
     * Init data table with specimens list.
     * 
     * This method is static to be reused in several places.
     * 
     * @param templatePage parent page
     * @param componentId data table id
     * @param callerPage caller page
     * @param specimens specimens list
     * @param specimenService specimen service
     * @return data table component
     */
    public static DataTable<Specimen, String> initSpecimensDataTable(final TemplatePage templatePage,
            final String componentId, final CallerPage callerPage, List<Specimen> specimens,
            final SpecimenService specimenService) {

        final LoadableDetachableSortableListDataProvider<Specimen> specimensDataProvider = new LoadableDetachableSortableListDataProvider<Specimen>(
                specimens, templatePage.getSession().getLocale());

        List<IColumn<Specimen, String>> columns = new ArrayList<IColumn<Specimen, String>>();

        columns.add(new LinkableImagePropertyColumn<Specimen, String>("images/read.png", templatePage
                .getStringModel("Read"), templatePage.getStringModel("Read")) {
            @Override
            public void onClick(Item<ICellPopulator<Specimen>> item, String componentId, IModel<Specimen> model) {
                templatePage.setResponsePage(new ReadSpecimenPage(model.getObject().getIdSpecimen(), callerPage));
            }
        });

        columns.add(new LinkPropertyColumn<Specimen, String>(templatePage.getStringModel("Specimen.ref"), "ref", "ref",
                templatePage.getStringModel("Read")) {
            @Override
            public void onClick(Item<ICellPopulator<Specimen>> item, String componentId, IModel<Specimen> model) {
                templatePage.setResponsePage(new ReadSpecimenPage(model.getObject().getIdSpecimen(), callerPage));
            }
        });

        columns.add(new EnumPropertyColumn<Specimen, String>(templatePage.getStringModel("Specimen.typeOrganisme"),
                "typeOrganisme", "typeOrganisme", templatePage));

        columns.add(new TaxonomyPropertyColumn<Specimen, String>(templatePage.getStringModel("Specimen.embranchement"),
                "embranchement", "embranchement"));

        columns.add(new TaxonomyPropertyColumn<Specimen, String>(templatePage.getStringModel("Specimen.famille"),
                "famille", "famille"));

        columns.add(new TaxonomyPropertyColumn<Specimen, String>(templatePage.getStringModel("Specimen.genre"),
                "genre", "genre"));

        columns.add(new TaxonomyPropertyColumn<Specimen, String>(templatePage.getStringModel("Specimen.espece"),
                "espece", "espece"));

        columns.add(new LinkPropertyColumn<Specimen, String>(templatePage.getStringModel("Specimen.station2"),
                "station", "station", templatePage.getStringModel("Read")) {
            @Override
            public void onClick(Item<ICellPopulator<Specimen>> item, String componentId, IModel<Specimen> model) {
                templatePage.setResponsePage(new ReadStationPage(model.getObject().getStation().getIdStation(),
                        callerPage));
            }
        });

        columns.add(new DocumentTooltipColumn<Specimen, String>(templatePage
                .getStringModel("ListDocumentsPage.AttachedDocuments")) {
            @Override
            public void onClick(IModel<Specimen> model) {
                int idSpecimen = model.getObject().getIdSpecimen();
                templatePage.setResponsePage(new ReadSpecimenPage(idSpecimen, callerPage));
            }
        });

        columns.add(new LinkableImagePropertyColumn<Specimen, String>("images/edit.png", templatePage
                .getStringModel("Update"), templatePage.getStringModel("Update")) {
            // pas de lien d'édition si l'utilisateur n'a pas les droits
            @Override
            public void populateItem(Item<ICellPopulator<Specimen>> item, String componentId, IModel<Specimen> model) {
                if (item.getIndex() % 2 == 1) {
                    item.add(new AttributeModifier("class", item.getIndex() % 2 == 0 ? "even" : "odd"));
                }

                if (specimenService.updateOrdeleteSpecimenEnabled(model.getObject(), templatePage.getSession()
                        .getUtilisateur())) {
                    item.add(new LinkableImagePanel(item, componentId, model));
                } else {
                    // label vide
                    item.add(new Label(componentId));
                }
            }

            @Override
            public void onClick(Item<ICellPopulator<Specimen>> item, String componentId, IModel<Specimen> model) {
                templatePage.setResponsePage(new ManageSpecimenPage(model.getObject().getIdSpecimen(), callerPage));
            }
        });

        final DataTable<Specimen, String> specimensDataTable = new AjaxFallbackDefaultDataTable<Specimen, String>(
                componentId, columns, specimensDataProvider, WebContext.ROWS_PER_PAGE);
        specimensDataTable.addBottomToolbar(new TableExportToolbar(specimensDataTable, "specimens", templatePage
                .getSession().getLocale()));
        return specimensDataTable;
    }
}
