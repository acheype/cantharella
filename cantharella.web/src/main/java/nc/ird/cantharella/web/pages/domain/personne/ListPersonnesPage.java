/*
 * #%L
 * Cantharella :: Web
 * $Id: ListPersonnesPage.java 264 2014-04-18 15:34:37Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.web/src/main/java/nc/ird/cantharella/web/pages/domain/personne/ListPersonnesPage.java $
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
package nc.ird.cantharella.web.pages.domain.personne;

import java.util.ArrayList;
import java.util.List;

import nc.ird.cantharella.data.model.Personne;
import nc.ird.cantharella.data.model.Utilisateur;
import nc.ird.cantharella.data.model.Utilisateur.TypeDroit;
import nc.ird.cantharella.service.services.PersonneService;
import nc.ird.cantharella.web.config.WebContext;
import nc.ird.cantharella.web.pages.TemplatePage;
import nc.ird.cantharella.web.pages.domain.document.DocumentTooltipColumn;
import nc.ird.cantharella.web.pages.domain.utilisateur.ManageUtilisateurPage;
import nc.ird.cantharella.web.pages.domain.utilisateur.ReadUtilisateurPage;
import nc.ird.cantharella.web.pages.domain.utilisateur.UpdateUtilisateurPage;
import nc.ird.cantharella.web.utils.CallerPage;
import nc.ird.cantharella.web.utils.columns.LinkPropertyColumn;
import nc.ird.cantharella.web.utils.columns.LinkableImagePropertyColumn;
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
 * Page de consultation des personnes & utilisateurs, par un administrateur
 * 
 * @author Mickael Tricot
 * @author Adrien Cheype
 */
@AuthRoles({ AuthRole.ADMIN, AuthRole.USER })
public final class ListPersonnesPage extends TemplatePage {

    /** Logger */
    // private static final Logger LOG =
    // LoggerFactory.getLogger(ListPersonnesPage.class);
    /** Service : personne */
    @SpringBean
    private PersonneService personneService;

    /**
     * Constructeur
     */
    public ListPersonnesPage() {
        super(ListPersonnesPage.class);

        final CallerPage currentPage = new CallerPage(ListPersonnesPage.class);

        // Lien pour créer une nouvelle personne
        add(new Link<Personne>(getResource() + ".NewPersonne") {
            @Override
            public void onClick() {
                setResponsePage(new ManagePersonnePage(currentPage, true));
            }

        });

        // On englobe le "DataView" dans un composant neutre que l'on pourra
        // rafraichir quand la liste évoluera
        final MarkupContainer personnesRefresh = new WebMarkupContainer(getResource() + ".Personnes.Refresh");
        personnesRefresh.setOutputMarkupId(true);
        add(personnesRefresh);

        // Liste des personnes
        final List<Personne> personnes;
        if (getSession().getUtilisateur().getTypeDroit() == TypeDroit.ADMINISTRATEUR) {
            personnes = personneService.listPersonnesWithInvalidUsers();
        } else {
            personnes = personneService.listPersonnes();
        }

        final LoadableDetachableSortableListDataProvider<Personne> personnesDataProvider = new LoadableDetachableSortableListDataProvider<Personne>(
                personnes, getSession().getLocale());

        List<IColumn<Personne, String>> columns = new ArrayList<IColumn<Personne, String>>();

        columns.add(new LinkableImagePropertyColumn<Personne, String>("images/read.png", getStringModel("Read"),
                getStringModel("Read")) {
            @Override
            public void onClick(Item<ICellPopulator<Personne>> item, String componentId, IModel<Personne> model) {
                if (model.getObject() instanceof Utilisateur) {
                    setResponsePage(new ReadUtilisateurPage(model.getObject().getIdPersonne(), currentPage));
                } else { // sinon c'est une "Personne"
                    assert (model.getObject() instanceof Personne);
                    setResponsePage(new ReadPersonnePage(model.getObject().getIdPersonne(), currentPage));
                }
            }
        });

        columns.add(new PropertyColumn<Personne, String>(getStringModel("Personne.nom"), "nom", "nom"));

        columns.add(new PropertyColumn<Personne, String>(getStringModel("Personne.prenom"), "prenom", "prenom"));

        columns.add(new LinkPropertyColumn<Personne, String>(getStringModel("Personne.courriel"), "courriel",
                "courriel", getStringModel("Read")) {
            @Override
            public void onClick(Item<ICellPopulator<Personne>> item, String componentId, IModel<Personne> rowModel) {
                if (rowModel.getObject() instanceof Utilisateur) {
                    setResponsePage(new ReadUtilisateurPage(rowModel.getObject().getIdPersonne(), currentPage));
                } else { // sinon c'est une "Personne"
                    assert (rowModel.getObject() instanceof Personne);
                    setResponsePage(new ReadPersonnePage(rowModel.getObject().getIdPersonne(), currentPage));
                }
            }
        });

        columns.add(new TypeDroitColumn(getStringModel("Utilisateur.typeDroit"), this));

        columns.add(new DocumentTooltipColumn<Personne, String>(getStringModel("ListDocumentsPage.AttachedDocuments")) {
            @Override
            public void onClick(IModel<Personne> model) {
                int idPersonne = model.getObject().getIdPersonne();
                setResponsePage(new ReadPersonnePage(idPersonne, currentPage));
            }
        });

        columns.add(new LinkableImagePropertyColumn<Personne, String>("images/edit.png", getStringModel("Update"),
                getStringModel("Update")) {

            // pas de lien d'édition si l'utilisateur n'a pas les droits
            @Override
            public void populateItem(Item<ICellPopulator<Personne>> item, String componentId, IModel<Personne> model) {
                if (personneService.updateOrDeletePersonneEnabled(model.getObject(), getSession().getUtilisateur())) {
                    item.add(new LinkableImagePanel(item, componentId, model));
                } else {
                    // label vide
                    item.add(new Label(componentId));
                }
            }

            @Override
            public void onClick(Item<ICellPopulator<Personne>> item, String componentId, IModel<Personne> model) {
                if (model.getObject() instanceof Utilisateur) {
                    // Si l'utilisateur est l'utilisateur "courant", alors on le
                    // redirige vers la page de mise à
                    // jour de son profil sinon vers la page de gestion d'un
                    // utilisateur
                    setResponsePage(model.getObject().getIdPersonne()
                            .equals(getSession().getUtilisateur().getIdPersonne()) ? new UpdateUtilisateurPage(
                            currentPage) : new ManageUtilisateurPage(model.getObject().getIdPersonne(), currentPage));
                } else { // sinon c'est une "Personne"
                    assert (model.getObject() instanceof Personne);
                    setResponsePage(new ManagePersonnePage(model.getObject().getIdPersonne(), currentPage));
                }
            }
        });

        final DataTable<Personne, String> personnesDataTable = new AjaxFallbackDefaultDataTable<Personne, String>(
                "ListPersonnesPage.Personnes", columns, personnesDataProvider, WebContext.ROWS_PER_PAGE);
        personnesDataTable.addBottomToolbar(new TableExportToolbar(personnesDataTable, "personnes", getSession()
                .getLocale()));
        personnesRefresh.add(personnesDataTable);

    }
}
