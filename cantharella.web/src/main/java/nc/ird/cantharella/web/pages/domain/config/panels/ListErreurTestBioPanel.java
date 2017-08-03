/*
 * #%L
 * Cantharella :: Web
 * $Id: ListErreurTestBioPanel.java 268 2014-05-06 15:45:40Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.web/src/main/java/nc/ird/cantharella/web/pages/domain/config/panels/ListErreurTestBioPanel.java $
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

import nc.ird.cantharella.data.model.ErreurTestBio;
import nc.ird.cantharella.service.services.TestBioService;
import nc.ird.cantharella.web.pages.domain.config.ManageErreurTestBioPage;
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
 * Panel qui liste les erreurs de tests biologiques
 * 
 * @author Adrien Cheype
 */
public class ListErreurTestBioPanel extends Panel {

    /** Service : test */
    @SpringBean
    private TestBioService testService;

    /**
     * Constructor
     * 
     * @param id The panel ID
     */
    public ListErreurTestBioPanel(String id) {
        super(id);

        add(new BookmarkablePageLink<Void>("ListErreurTestBioPanel.NewErreurTestBio", ManageErreurTestBioPage.class));

        // On englobe le "DataView" dans un composant neutre que l'on pourra
        // rafraichir quand la liste évoluera
        final MarkupContainer erreursTestRefresh = new WebMarkupContainer(
                "ListErreurTestBioPanel.ErreursTestBio.Refresh");
        erreursTestRefresh.setOutputMarkupId(true);
        add(erreursTestRefresh);

        // Liste des erreursTest
        final List<ErreurTestBio> erreursTest = testService.listErreursTestBio();
        LoadableDetachableSortableListDataProvider<ErreurTestBio> erreursDataProvider = new LoadableDetachableSortableListDataProvider<ErreurTestBio>(
                erreursTest, getSession().getLocale());

        erreursTestRefresh
                .add(new DataView<ErreurTestBio>("ListErreurTestBioPanel.ErreursTestBio", erreursDataProvider) {
                    @Override
                    protected void populateItem(Item<ErreurTestBio> item) {
                        if (item.getIndex() % 2 == 1) {
                            item.add(new AttributeModifier("class", item.getIndex() % 2 == 0 ? "even" : "odd"));
                        }

                        ErreurTestBio erreurTest = item.getModelObject();
                        // Colonnes
                        item.add(new Label("ListErreurTestBioPanel.ErreursTestBio.nom", new PropertyModel<String>(
                                erreurTest, "nom")));
                        item.add(new Label("ListErreurTestBioPanel.ErreursTestBio.description",
                                new PropertyModel<String>(erreurTest, "description")));

                        // Action : mise à jour (redirection vers le formulaire)
                        Link<ErreurTestBio> updateLink = new Link<ErreurTestBio>(
                                "ListErreurTestBioPanel.ErreursTestBio.Update", new Model<ErreurTestBio>(erreurTest)) {
                            @Override
                            public void onClick() {
                                setResponsePage(new ManageErreurTestBioPage(getModelObject().getIdErreurTest()));
                            }
                        };
                        item.add(updateLink);
                    }
                });
    }

}
