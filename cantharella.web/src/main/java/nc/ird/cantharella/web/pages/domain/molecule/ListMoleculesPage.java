/*
 * #%L
 * Cantharella :: Web
 * $Id: ListMoleculesPage.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.web/src/main/java/nc/ird/cantharella/web/pages/domain/molecule/ListMoleculesPage.java $
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
package nc.ird.cantharella.web.pages.domain.molecule;

import java.util.ArrayList;
import java.util.List;

import nc.ird.cantharella.data.model.Lot;
import nc.ird.cantharella.data.model.Utilisateur;
import nc.ird.cantharella.service.model.MoleculeProvenanceBean;
import nc.ird.cantharella.service.services.MoleculeService;
import nc.ird.cantharella.web.config.WebContext;
import nc.ird.cantharella.web.pages.TemplatePage;
import nc.ird.cantharella.web.pages.domain.document.DocumentTooltipColumn;
import nc.ird.cantharella.web.pages.domain.lot.ReadLotPage;
import nc.ird.cantharella.web.utils.CallerPage;
import nc.ird.cantharella.web.utils.behaviors.MoleculeViewBehavior;
import nc.ird.cantharella.web.utils.columns.BooleanPropertyColumn;
import nc.ird.cantharella.web.utils.columns.DecimalPropertyColumn;
import nc.ird.cantharella.web.utils.columns.LinkPropertyColumn;
import nc.ird.cantharella.web.utils.columns.LinkableImagePropertyColumn;
import nc.ird.cantharella.web.utils.data.TableExportToolbar;
import nc.ird.cantharella.web.utils.models.DisplayDecimalPropertyModel.DecimalDisplFormat;
import nc.ird.cantharella.web.utils.models.SimpleSortableListDataProvider;
import nc.ird.cantharella.web.utils.security.AuthRole;
import nc.ird.cantharella.web.utils.security.AuthRoles;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * Molecules list page.
 * 
 * @author Eric Chatellier
 */
@AuthRoles({ AuthRole.ADMIN, AuthRole.USER })
public class ListMoleculesPage extends TemplatePage {

    /** Service : molecule */
    @SpringBean
    private MoleculeService moleculeService;

    /**
     * Constructor.
     */
    public ListMoleculesPage() {
        super(ListMoleculesPage.class);

        final CallerPage currentPage = new CallerPage(ListMoleculesPage.class);

        add(new Link<Void>(getResource() + ".NewMolecule") {
            @Override
            public void onClick() {
                setResponsePage(new ManageMoleculePage(currentPage));
            }
        });

        // On englobe le "DataView" dans un composant neutre que l'on pourra
        // rafraichir quand la liste évoluera
        final MarkupContainer moleculesRefresh = new WebMarkupContainer(getResource() + ".Molecules.Refresh");
        moleculesRefresh.setOutputMarkupId(true);
        add(moleculesRefresh);

        // Liste des molecules
        Utilisateur utilisateur = getSession().getUtilisateur();
        final List<MoleculeProvenanceBean> moleculeProvenances = moleculeService.listMoleculeProvenances(utilisateur);

        DataTable<MoleculeProvenanceBean, String> moleculesDataTable = initMoleculesDataTable(this,
                "ListMoleculesPage.Molecules", currentPage, moleculeProvenances);
        moleculesRefresh.add(moleculesDataTable);
    }

    /**
     * Init data table with molecule provenances list.
     * 
     * This method is static to be reused in several places.
     * 
     * @param templatePage parent page
     * @param componentId data table id
     * @param callerPage caller page
     * @param moleculeProvenances molecules provenance list
     * @return data table component
     */
    public static DataTable<MoleculeProvenanceBean, String> initMoleculesDataTable(final TemplatePage templatePage,
            final String componentId, final CallerPage callerPage, List<MoleculeProvenanceBean> moleculeProvenances) {

        SimpleSortableListDataProvider<MoleculeProvenanceBean> moleculesDataProvider = new SimpleSortableListDataProvider<MoleculeProvenanceBean>(
                moleculeProvenances, templatePage.getSession().getLocale());

        List<IColumn<MoleculeProvenanceBean, String>> columns = new ArrayList<IColumn<MoleculeProvenanceBean, String>>();

        columns.add(new LinkableImagePropertyColumn<MoleculeProvenanceBean, String>("images/read.png", templatePage
                .getStringModel("Read"), templatePage.getStringModel("Read")) {
            @Override
            public void onClick(Item<ICellPopulator<MoleculeProvenanceBean>> item, String componentId,
                    IModel<MoleculeProvenanceBean> model) {
                templatePage.setResponsePage(new ReadMoleculePage(model.getObject().getIdMolecule(), callerPage));
            }
        });

        columns.add(new LinkPropertyColumn<MoleculeProvenanceBean, String>(templatePage
                .getStringModel("Molecule.idMolecule"), "idMolecule", "idMolecule") {
            @Override
            public void onClick(Item<ICellPopulator<MoleculeProvenanceBean>> item, String componentId,
                    IModel<MoleculeProvenanceBean> model) {
                templatePage.setResponsePage(new ReadMoleculePage(model.getObject().getIdMolecule(), callerPage));
            }
        });

        columns.add(new AbstractColumn<MoleculeProvenanceBean, String>(templatePage
                .getStringModel("Molecule.formuleDevMol")) {
            public void populateItem(Item<ICellPopulator<MoleculeProvenanceBean>> cellItem, String componentId,
                    IModel<MoleculeProvenanceBean> rowModel) {
                cellItem.add(new Label(componentId, "-").add(new MoleculeViewBehavior(new PropertyModel<String>(
                        rowModel, "molecule.formuleDevMol"))));
            }
        });

        columns.add(new PropertyColumn<MoleculeProvenanceBean, String>(templatePage
                .getStringModel("Molecule.nomCommun"), "molecule.nomCommun", "molecule.nomCommun"));

        columns.add(new PropertyColumn<MoleculeProvenanceBean, String>(templatePage
                .getStringModel("Molecule.familleChimique"), "molecule.familleChimique", "molecule.familleChimique"));

        columns.add(new PropertyColumn<MoleculeProvenanceBean, String>(templatePage
                .getStringModel("Molecule.formuleBrute"), "molecule.formuleBrute", "molecule.formuleBrute"));

        columns.add(new DecimalPropertyColumn<MoleculeProvenanceBean, String>(templatePage
                .getStringModel("Molecule.masseMolaire"), "molecule.masseMolaire", "molecule.masseMolaire",
                DecimalDisplFormat.SMALL, templatePage.getLocale()));

        columns.add(new BooleanPropertyColumn<MoleculeProvenanceBean, String>(templatePage
                .getStringModel("Molecule.nouvMolecul"), "molecule.nouvMolecul", "molecule.nouvMolecul", templatePage));

        columns.add(new PropertyColumn<MoleculeProvenanceBean, String>(templatePage
                .getStringModel("Molecule.provenance.embranchement"), "lot.specimenRef.embranchement",
                "lot.specimenRef.embranchement"));

        columns.add(new PropertyColumn<MoleculeProvenanceBean, String>(templatePage
                .getStringModel("Molecule.provenance.genre"), "lot.specimenRef.genre", "lot.specimenRef.genre"));

        columns.add(new PropertyColumn<MoleculeProvenanceBean, String>(templatePage
                .getStringModel("Molecule.provenance.espece"), "lot.specimenRef.espece", "lot.specimenRef.espece"));

        columns.add(new LinkPropertyColumn<MoleculeProvenanceBean, String>(templatePage
                .getStringModel("Molecule.provenance.lot.ref"), "lot.ref", "lot.ref") {
            @Override
            public void onClick(Item<ICellPopulator<MoleculeProvenanceBean>> item, String componentId,
                    IModel<MoleculeProvenanceBean> model) {
                Lot lot = model.getObject().getLot();
                if (lot != null) {
                    int idLot = lot.getIdLot();
                    templatePage.setResponsePage(new ReadLotPage(idLot, callerPage));
                }
            }
        });

        columns.add(new PropertyColumn<MoleculeProvenanceBean, String>(templatePage
                .getStringModel("Molecule.provenance.programme"), "lot.campagne.programme", "lot.campagne.programme"));

        columns.add(new DocumentTooltipColumn<MoleculeProvenanceBean, String>(templatePage
                .getStringModel("ListDocumentsPage.AttachedDocuments")) {
            @Override
            public void onClick(IModel<MoleculeProvenanceBean> model) {
                int idMolecule = model.getObject().getIdMolecule();
                templatePage.setResponsePage(new ReadMoleculePage(idMolecule, callerPage));
            }
        });

        columns.add(new LinkableImagePropertyColumn<MoleculeProvenanceBean, String>("images/edit.png", templatePage
                .getStringModel("Update"), templatePage.getStringModel("Update")) {
            @Override
            public void onClick(Item<ICellPopulator<MoleculeProvenanceBean>> item, String componentId,
                    IModel<MoleculeProvenanceBean> model) {
                int idMolecule = model.getObject().getIdMolecule();
                templatePage.setResponsePage(new ManageMoleculePage(idMolecule, callerPage));
            }
        });

        final DataTable<MoleculeProvenanceBean, String> moleculesDataTable = new AjaxFallbackDefaultDataTable<MoleculeProvenanceBean, String>(
                componentId, columns, moleculesDataProvider, WebContext.ROWS_PER_PAGE);
        moleculesDataTable.addBottomToolbar(new TableExportToolbar(moleculesDataTable, "molecules", templatePage
                .getSession().getLocale()));
        return moleculesDataTable;
    }
}
