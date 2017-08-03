/*
 * #%L
 * Cantharella :: Web
 * $Id: ListPartiePanel.java 268 2014-05-06 15:45:40Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.web/src/main/java/nc/ird/cantharella/web/pages/domain/config/panels/ListPartiePanel.java $
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
package nc.ird.cantharella.web.pages.domain.config.panels;

import java.util.List;

import nc.ird.cantharella.data.model.Partie;
import nc.ird.cantharella.service.services.LotService;
import nc.ird.cantharella.web.pages.domain.config.ManagePartiePage;
import nc.ird.cantharella.web.utils.models.LoadableDetachableSortableListDataProvider;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * Partie part of the SI configuration
 * 
 * @author Mickael Tricot
 * @author Adrien Cheype
 */
public class ListPartiePanel extends Panel {

    /** Service : lot */
    @SpringBean
    private LotService lotService;

    /**
     * Constructor
     * 
     * @param id The panel ID
     */
    public ListPartiePanel(String id) {
        super(id);

        add(new BookmarkablePageLink<Void>("ListPartiePanel.NewPartie", ManagePartiePage.class));

        // On englobe le "DataView" dans un composant neutre que l'on pourra
        // rafraichir quand la liste évoluera
        final MarkupContainer partiesRefresh = new WebMarkupContainer("ListPartiePanel.Parties.Refresh");
        partiesRefresh.setOutputMarkupId(true);
        add(partiesRefresh);

        // Liste des parties
        final List<Partie> parties = lotService.listParties();
        LoadableDetachableSortableListDataProvider<Partie> partiesDataProvider = new LoadableDetachableSortableListDataProvider<Partie>(
                parties, getSession().getLocale());

        partiesRefresh.add(new DataView<Partie>("ListPartiePanel.Parties", partiesDataProvider) {
            @Override
            protected void populateItem(Item<Partie> item) {
                if (item.getIndex() % 2 == 1) {
                    item.add(new AttributeModifier("class", item.getIndex() % 2 == 0 ? "even" : "odd"));
                }

                Partie partie = item.getModelObject();
                // Colonnes
                item.add(new Label("ListPartiePanel.Parties.nom", new PropertyModel<String>(partie, "nom")));

                // Action : mise à jour (redirection vers le formulaire)
                Link<Partie> updateLink = new Link<Partie>("ListPartiePanel.Parties.Update", new Model<Partie>(partie)) {
                    @Override
                    public void onClick() {
                        setResponsePage(new ManagePartiePage(getModelObject().getIdPartie()));
                    }
                };
                item.add(updateLink);
            }
        });
    }

}
