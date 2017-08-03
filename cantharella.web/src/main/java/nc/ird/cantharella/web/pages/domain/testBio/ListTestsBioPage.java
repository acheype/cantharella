/*
 * #%L
 * Cantharella :: Web
 * $Id: ListTestsBioPage.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.web/src/main/java/nc/ird/cantharella/web/pages/domain/testBio/ListTestsBioPage.java $
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

import java.util.ArrayList;
import java.util.List;

import nc.ird.cantharella.data.model.Extrait;
import nc.ird.cantharella.data.model.Fraction;
import nc.ird.cantharella.data.model.ResultatTestBio;
import nc.ird.cantharella.service.services.TestBioService;
import nc.ird.cantharella.web.config.WebContext;
import nc.ird.cantharella.web.pages.TemplatePage;
import nc.ird.cantharella.web.pages.columns.LinkProduitPropertyColumn;
import nc.ird.cantharella.web.pages.columns.TaxonomyPropertyColumn;
import nc.ird.cantharella.web.pages.domain.document.DocumentTooltipColumn;
import nc.ird.cantharella.web.pages.domain.extraction.ReadExtractionPage;
import nc.ird.cantharella.web.pages.domain.purification.ReadPurificationPage;
import nc.ird.cantharella.web.utils.CallerPage;
import nc.ird.cantharella.web.utils.columns.BooleanPropertyColumn;
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
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.TextFilteredPropertyColumn;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * Page de consultation des résultats des tests biologiques
 * 
 * @author Adrien Cheype
 */
@AuthRoles({ AuthRole.ADMIN, AuthRole.USER })
public final class ListTestsBioPage extends TemplatePage {

    /** Service : test biologique */
    @SpringBean
    private TestBioService testBioService;

    /**
     * Constructeur
     */
    public ListTestsBioPage() {
        super(ListTestsBioPage.class);

        final CallerPage currentPage = new CallerPage(ListTestsBioPage.class);

        add(new Link<Void>("ListTestsBioPage.NewTestBio") {
            @Override
            public void onClick() {
                setResponsePage(new ManageTestBioPage(currentPage, true));
            }
        });

        // On englobe le "DataView" dans un composant neutre que l'on pourra
        // rafraichir quand la liste évoluera
        final MarkupContainer resTestsBiosRefresh = new WebMarkupContainer("ListTestsBioPage.ResultatsTestsBio.Refresh");
        resTestsBiosRefresh.setOutputMarkupId(true);
        add(resTestsBiosRefresh);

        // Liste des résultats de tests biologiques
        final List<ResultatTestBio> resTestsBios = testBioService.listResultatsTestBio(getSession().getUtilisateur());
        DataTable<ResultatTestBio, String> resTestsBiosDataTable = initTestsBioDataTable(this,
                "ListTestsBioPage.ResultatsTestsBio", currentPage, resTestsBios, testBioService);
        resTestsBiosRefresh.add(resTestsBiosDataTable);
    }

    /**
     * Init data table with testsbio list.
     * 
     * This method is static to be reused in several places.
     * 
     * @param templatePage parent page
     * @param componentId data table id
     * @param callerPage caller page
     * @param resTestsBios tests bio list
     * @param testBioService test bio service
     * @return data table component
     */
    public static DataTable<ResultatTestBio, String> initTestsBioDataTable(final TemplatePage templatePage,
            final String componentId, final CallerPage callerPage, List<ResultatTestBio> resTestsBios,
            final TestBioService testBioService) {

        LoadableDetachableSortableListDataProvider<ResultatTestBio> resTestBiosDataProvider = new LoadableDetachableSortableListDataProvider<ResultatTestBio>(
                resTestsBios, templatePage.getSession().getLocale());

        List<IColumn<ResultatTestBio, String>> columns = new ArrayList<IColumn<ResultatTestBio, String>>();

        columns.add(new LinkableImagePropertyColumn<ResultatTestBio, String>("images/read.png", templatePage
                .getStringModel("Read"), templatePage.getStringModel("Read")) {
            @Override
            public void onClick(Item<ICellPopulator<ResultatTestBio>> item, String componentId,
                    IModel<ResultatTestBio> model) {
                templatePage.setResponsePage(new ReadTestBioPage(model.getObject().getTestBio().getIdTestBio(),
                        callerPage));
            }
        });

        columns.add(new TextFilteredPropertyColumn<ResultatTestBio, String, String>(templatePage
                .getStringModel("MethodeTestBio.cible2"), "testBio.methode.cible", "testBio.methode.cible"));

        columns.add(new DecimalPropertyColumn<ResultatTestBio, String>(templatePage
                .getStringModel("ResultatTestBio.concMasse2"), "concMasse", "concMasse", DecimalDisplFormat.SMALL,
                templatePage.getLocale()));

        columns.add(new EnumPropertyColumn<ResultatTestBio, String>(templatePage
                .getStringModel("TestBio.uniteConcMasse2"), "uniteConcMasse", "uniteConcMasse", templatePage));

        columns.add(new LinkProduitPropertyColumn<ResultatTestBio, String>(templatePage
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

        columns.add(new DecimalPropertyColumn<ResultatTestBio, String>(templatePage
                .getStringModel("ResultatTestBio.valeur"), "valeur", "valeur", DecimalDisplFormat.SMALL, templatePage
                .getLocale()));

        columns.add(new PropertyColumn<ResultatTestBio, String>(templatePage
                .getStringModel("MethodeTestBio.uniteResultat2"), "testBio.methode.uniteResultat",
                "testBio.methode.uniteResultat"));

        columns.add(new PropertyColumn<ResultatTestBio, String>(templatePage.getStringModel("Extrait.typeExtrait2"),
                "typeExtraitSource", "typeExtraitSource"));

        columns.add(new BooleanPropertyColumn<ResultatTestBio, String>(templatePage
                .getStringModel("ResultatTestBio.actif"), "actif", "actif", templatePage));

        columns.add(new TaxonomyPropertyColumn<ResultatTestBio, String>(
                templatePage.getStringModel("Specimen.famille"), "lotSource.specimenRef.famille",
                "lotSource.specimenRef.famille"));

        columns.add(new TaxonomyPropertyColumn<ResultatTestBio, String>(templatePage.getStringModel("Specimen.genre"),
                "lotSource.specimenRef.genre", "lotSource.specimenRef.genre"));

        columns.add(new TaxonomyPropertyColumn<ResultatTestBio, String>(templatePage.getStringModel("Specimen.espece"),
                "lotSource.specimenRef.espece", "lotSource.specimenRef.espece"));

        columns.add(new MapValuePropertyColumn<ResultatTestBio, String, String>(templatePage
                .getStringModel("Campagne.codePays"), "lotSource.campagne.codePays", "lotSource.campagne.codePays",
                WebContext.COUNTRIES.get(templatePage.getSession().getLocale())));

        columns.add(new PropertyColumn<ResultatTestBio, String>(templatePage.getStringModel("ResultatTestBio.repere"),
                "repere", "repere"));

        columns.add(new LinkPropertyColumn<ResultatTestBio, String>(templatePage.getStringModel("TestBio.ref"),
                "testBio.ref", "testBio.ref", templatePage.getStringModel("Read")) {
            @Override
            public void onClick(Item<ICellPopulator<ResultatTestBio>> item, String componentId,
                    IModel<ResultatTestBio> model) {
                templatePage.setResponsePage(new ReadTestBioPage(model.getObject().getTestBio().getIdTestBio(),
                        callerPage));
            }
        });

        columns.add(new DocumentTooltipColumn<ResultatTestBio, String>(templatePage
                .getStringModel("ListDocumentsPage.AttachedDocuments")) {
            @Override
            public void onClick(IModel<ResultatTestBio> model) {
                int idTestBio = model.getObject().getTestBio().getIdTestBio();
                templatePage.setResponsePage(new ReadTestBioPage(idTestBio, callerPage));
            }
        });

        columns.add(new LinkableImagePropertyColumn<ResultatTestBio, String>("images/edit.png", templatePage
                .getStringModel("Update"), templatePage.getStringModel("Update")) {
            // pas de lien d'édition si l'utilisateur n'a pas les droits
            @Override
            public void populateItem(Item<ICellPopulator<ResultatTestBio>> item, String componentId,
                    IModel<ResultatTestBio> model) {
                if (testBioService.updateOrdeleteTestBioEnabled(model.getObject().getTestBio(), templatePage
                        .getSession().getUtilisateur())) {
                    item.add(new LinkableImagePanel(item, componentId, model));
                } else {
                    // label vide
                    item.add(new Label(componentId));
                }
            }

            @Override
            public void onClick(Item<ICellPopulator<ResultatTestBio>> item, String componentId,
                    IModel<ResultatTestBio> model) {
                templatePage.setResponsePage(new ManageTestBioPage(model.getObject().getTestBio().getIdTestBio(),
                        callerPage));
            }
        });

        final DataTable<ResultatTestBio, String> resTestBiosDataTable = new AjaxFallbackDefaultDataTable<ResultatTestBio, String>(
                componentId, columns, resTestBiosDataProvider, WebContext.ROWS_PER_PAGE);
        resTestBiosDataTable.addBottomToolbar(new TableExportToolbar(resTestBiosDataTable, "testsbio", templatePage
                .getSession().getLocale()));
        // DRAFT FOR FILTER TABLE
        // resTestBiosDataTable.addTopToolbar(new NavigationToolbar(resTestBiosDataTable));
        // resTestBiosDataTable.addTopToolbar(new HeadersToolbar(resTestBiosDataTable, resTestBiosDataProvider));

        // create the form used to contain all filter components
        /*
         * final FilterForm filterForm = new FilterForm("filter-form", resTestBiosDataProvider) { private static final
         * long serialVersionUID = 1L;
         * @Override protected void onSubmit() { resTestBiosDataTable.setCurrentPage(0); } }; resTestBiosDataTable
         * .addTopToolbar(new FilterToolbar(resTestBiosDataTable, filterForm, resTestBiosDataProvider));
         */
        return resTestBiosDataTable;
    }
}
