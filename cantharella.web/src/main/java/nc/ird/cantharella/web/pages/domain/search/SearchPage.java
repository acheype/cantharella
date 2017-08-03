/*
 * #%L
 * Cantharella :: Web
 * $Id: SearchPage.java 268 2014-05-06 15:45:40Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.web/src/main/java/nc/ird/cantharella/web/pages/domain/search/SearchPage.java $
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
package nc.ird.cantharella.web.pages.domain.search;

import java.util.List;

import nc.ird.cantharella.data.model.Extraction;
import nc.ird.cantharella.data.model.Lot;
import nc.ird.cantharella.data.model.Molecule;
import nc.ird.cantharella.data.model.Purification;
import nc.ird.cantharella.data.model.ResultatTestBio;
import nc.ird.cantharella.data.model.Specimen;
import nc.ird.cantharella.data.model.Station;
import nc.ird.cantharella.data.model.Utilisateur;
import nc.ird.cantharella.service.model.MoleculeProvenanceBean;
import nc.ird.cantharella.service.model.SearchBean;
import nc.ird.cantharella.service.model.SearchResult;
import nc.ird.cantharella.service.services.ExtractionService;
import nc.ird.cantharella.service.services.LotService;
import nc.ird.cantharella.service.services.MoleculeService;
import nc.ird.cantharella.service.services.PurificationService;
import nc.ird.cantharella.service.services.SearchService;
import nc.ird.cantharella.service.services.SpecimenService;
import nc.ird.cantharella.service.services.StationService;
import nc.ird.cantharella.service.services.TestBioService;
import nc.ird.cantharella.web.config.WebContext;
import nc.ird.cantharella.web.pages.TemplatePage;
import nc.ird.cantharella.web.pages.domain.extraction.ListExtractionsPage;
import nc.ird.cantharella.web.pages.domain.lot.ListLotsPage;
import nc.ird.cantharella.web.pages.domain.molecule.ListMoleculesPage;
import nc.ird.cantharella.web.pages.domain.purification.ListPurificationsPage;
import nc.ird.cantharella.web.pages.domain.specimen.ListSpecimensPage;
import nc.ird.cantharella.web.pages.domain.station.ListStationsPage;
import nc.ird.cantharella.web.pages.domain.testBio.ListTestsBioPage;
import nc.ird.cantharella.web.utils.CallerPage;
import nc.ird.cantharella.web.utils.renderers.MapChoiceRenderer;
import nc.ird.cantharella.web.utils.security.AuthRole;
import nc.ird.cantharella.web.utils.security.AuthRoles;

import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.value.ValueMap;

/**
 * Search results page.
 * 
 * @author echatellier
 */
@AuthRoles({ AuthRole.ADMIN, AuthRole.USER })
public class SearchPage extends TemplatePage {

    /** Form query bean. */
    protected Model<SearchBean> queryModel;

    /** Search service. */
    @SpringBean
    protected SearchService searchService;

    /** Specimen service. */
    @SpringBean
    protected SpecimenService specimenService;

    /** Lot service. */
    @SpringBean
    protected LotService lotService;

    /** Extraction service. */
    @SpringBean
    protected ExtractionService extractionService;

    /** Purification service. */
    @SpringBean
    protected PurificationService purificationService;

    /** Testbio service. */
    @SpringBean
    protected TestBioService testBioService;

    /** Station service. */
    @SpringBean
    protected StationService stationService;

    /** Molecule service. */
    @SpringBean
    protected MoleculeService moleculeService;

    /**
     * Constructor.
     */
    public SearchPage() {
        this(null);
    }

    /**
     * Constructor.
     * 
     * @param queryBean form query bean
     */
    public SearchPage(SearchBean queryBean) {
        super(SearchPage.class);
        final CallerPage currentPage = new CallerPage(this);

        // ca sert a rien, mais wicket est pas content
        ExternalLink link = new ExternalLink("advancedLink",
                "http://lucene.apache.org/core/3_6_2/queryparsersyntax.html");
        add(link);

        // init query model
        queryModel = Model.of(queryBean == null ? new SearchBean() : queryBean);

        // search form
        Form<ValueMap> searchForm = new Form<ValueMap>(getResource() + ".Form") {
            protected void onSubmit() {
                setResponsePage(new SearchPage(queryModel.getObject()));
            }
        };
        searchForm.add(new TextField<String>("SearchPage.Query", new PropertyModel<String>(queryModel, "query")));
        searchForm.add(new DropDownChoice<String>("SearchPage.Country",
                new PropertyModel<String>(queryModel, "country"), WebContext.COUNTRY_CODES
                        .get(getSession().getLocale()), new MapChoiceRenderer<String, String>(WebContext.COUNTRIES
                        .get(getSession().getLocale()))));
        add(searchForm);

        // search results
        Utilisateur utilisateur = getSession().getUtilisateur();
        SearchResult searchResult = searchService.search(queryModel.getObject(), utilisateur);
        // Additional transformation for molecule provenances
        List<Molecule> molecules = searchResult.getMolecules();
        List<MoleculeProvenanceBean> moleculeProvenances = moleculeService.listMoleculeProvenances(molecules,
                utilisateur);

        // specimens table
        DataTable<Specimen, String> specimensDataTable = ListSpecimensPage.initSpecimensDataTable(this,
                "SearchPage.Specimens.Results", currentPage, searchResult.getSpecimens(), specimenService);
        add(specimensDataTable);

        // lot table
        DataTable<Lot, String> lotsDataTable = ListLotsPage.initLotsDataTable(this, "SearchPage.Lots.Results",
                currentPage, searchResult.getLots(), lotService);
        add(lotsDataTable);

        // extractions table
        DataTable<Extraction, String> extractionsDataTable = ListExtractionsPage.initExtractionsDataTable(this,
                "SearchPage.Extractions.Results", currentPage, searchResult.getExtractions(), extractionService);
        add(extractionsDataTable);

        // purification table
        DataTable<Purification, String> purificationsDataTable = ListPurificationsPage.initPurificationsDataTable(this,
                "SearchPage.Purifications.Results", currentPage, searchResult.getPurifications(), purificationService);
        add(purificationsDataTable);

        // test bio table
        DataTable<ResultatTestBio, String> testBiosDataTable = ListTestsBioPage.initTestsBioDataTable(this,
                "SearchPage.ResultatTestBios.Results", currentPage, searchResult.getResultatTestBios(), testBioService);
        add(testBiosDataTable);

        // stations table
        DataTable<Station, String> stationsDataTable = ListStationsPage.initStationsDataTable(this,
                "SearchPage.Stations.Results", currentPage, searchResult.getStations(), stationService);
        add(stationsDataTable);

        // molecule table
        DataTable<MoleculeProvenanceBean, String> moleculesDataTable = ListMoleculesPage.initMoleculesDataTable(this,
                "SearchPage.Molecules.Results", currentPage, moleculeProvenances);
        add(moleculesDataTable);
    }
}
