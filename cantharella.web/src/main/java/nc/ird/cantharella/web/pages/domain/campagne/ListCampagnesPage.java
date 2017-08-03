/*
 * #%L
 * Cantharella :: Web
 * $Id: ListCampagnesPage.java 268 2014-05-06 15:45:40Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.web/src/main/java/nc/ird/cantharella/web/pages/domain/campagne/ListCampagnesPage.java $
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
package nc.ird.cantharella.web.pages.domain.campagne;

import java.util.ArrayList;
import java.util.List;

import nc.ird.cantharella.data.model.Campagne;
import nc.ird.cantharella.service.services.CampagneService;
import nc.ird.cantharella.web.config.WebContext;
import nc.ird.cantharella.web.pages.TemplatePage;
import nc.ird.cantharella.web.pages.domain.document.DocumentTooltipColumn;
import nc.ird.cantharella.web.utils.CallerPage;
import nc.ird.cantharella.web.utils.columns.LinkPropertyColumn;
import nc.ird.cantharella.web.utils.columns.LinkableImagePropertyColumn;
import nc.ird.cantharella.web.utils.columns.MapValuePropertyColumn;
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
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * Page de consultation des campagnes
 * 
 * @author Mickael Tricot
 * @author Adrien Cheype
 */
@AuthRoles({ AuthRole.ADMIN, AuthRole.USER })
public final class ListCampagnesPage extends TemplatePage {

    /** Service : campagne */
    @SpringBean
    private CampagneService campagneService;

    /**
     * Constructeur
     */
    public ListCampagnesPage() {
        super(ListCampagnesPage.class);

        final CallerPage currentPage = new CallerPage(ListCampagnesPage.class);

        // Lien pour l'age d'une nouvelle campagne
        add(new Link<Void>(getResource() + ".NewCampagne") {
            @Override
            public void onClick() {
                setResponsePage(new ManageCampagnePage(currentPage, true));
            }

        });

        // On englobe le "DataView" dans un composant neutre que l'on pourra
        // rafraichir quand la liste évoluera
        final MarkupContainer campagnesRefresh = new WebMarkupContainer(getResource() + ".Campagnes.Refresh");
        campagnesRefresh.setOutputMarkupId(true);
        add(campagnesRefresh);

        // Liste des campagnes
        final List<Campagne> campagnes = campagneService.listCampagnes(getSession().getUtilisateur());

        LoadableDetachableSortableListDataProvider<Campagne> campagnesDataProvider = new LoadableDetachableSortableListDataProvider<Campagne>(
                campagnes, getSession().getLocale());

        List<IColumn<Campagne, String>> columns = new ArrayList<IColumn<Campagne, String>>();

        columns.add(new LinkableImagePropertyColumn<Campagne, String>("images/read.png", getStringModel("Read"),
                getStringModel("Read")) {
            @Override
            public void onClick(Item<ICellPopulator<Campagne>> item, String componentId, IModel<Campagne> model) {
                setResponsePage(new ReadCampagnePage(model.getObject().getIdCampagne(), currentPage));
            }
        });

        columns.add(new LinkPropertyColumn<Campagne, String>(getStringModel("Campagne.nom"), "nom", "nom",
                getStringModel("Read")) {
            @Override
            public void onClick(Item<ICellPopulator<Campagne>> item, String componentId, IModel<Campagne> model) {
                setResponsePage(new ReadCampagnePage(model.getObject().getIdCampagne(), currentPage));
            }
        });

        columns.add(new ShortDatePropertyColumn<Campagne, String>(getStringModel("Campagne.dateDeb"), "dateDeb",
                "dateDeb", getLocale()));

        columns.add(new ShortDatePropertyColumn<Campagne, String>(getStringModel("Campagne.dateFin"), "dateFin",
                "dateFin", getLocale()));

        columns.add(new MapValuePropertyColumn<Campagne, String, String>(getStringModel("Campagne.codePays"),
                "codePays", "codePays", WebContext.COUNTRIES.get(getSession().getLocale())));

        columns.add(new DocumentTooltipColumn<Campagne, String>(getStringModel("ListDocumentsPage.AttachedDocuments")) {
            @Override
            public void onClick(IModel<Campagne> model) {
                int idCampagne = model.getObject().getIdCampagne();
                setResponsePage(new ReadCampagnePage(idCampagne, currentPage));
            }
        });

        columns.add(new LinkableImagePropertyColumn<Campagne, String>("images/edit.png", getStringModel("Update"),
                getStringModel("Update")) {
            // pas de lien d'édition si l'utilisateur n'a pas les droits
            @Override
            public void populateItem(Item<ICellPopulator<Campagne>> item, String componentId, IModel<Campagne> model) {
                if (campagneService.updateOrdeleteCampagneEnabled(model.getObject(), getSession().getUtilisateur())) {
                    item.add(new LinkableImagePanel(item, componentId, model));
                } else {
                    // label vide
                    item.add(new Label(componentId));
                }
            }

            @Override
            public void onClick(Item<ICellPopulator<Campagne>> item, String componentId, IModel<Campagne> model) {
                setResponsePage(new ManageCampagnePage(model.getObject().getIdCampagne(), currentPage));
            }
        });

        final DataTable<Campagne, String> campagnesDataTable = new AjaxFallbackDefaultDataTable<Campagne, String>(
                "ListCampagnesPage.Campagnes", columns, campagnesDataProvider, WebContext.ROWS_PER_PAGE);
        campagnesDataTable.addBottomToolbar(new TableExportToolbar(campagnesDataTable, "campagnes", getSession()
                .getLocale()));
        campagnesRefresh.add(campagnesDataTable);
    }
}
