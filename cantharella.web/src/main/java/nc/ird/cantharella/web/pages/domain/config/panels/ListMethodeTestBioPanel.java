/*
 * #%L
 * Cantharella :: Web
 * $Id: ListMethodeTestBioPanel.java 268 2014-05-06 15:45:40Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.web/src/main/java/nc/ird/cantharella/web/pages/domain/config/panels/ListMethodeTestBioPanel.java $
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

import nc.ird.cantharella.data.model.MethodeTestBio;
import nc.ird.cantharella.service.services.TestBioService;
import nc.ird.cantharella.web.pages.domain.config.ManageMethodeTestBioPage;
import nc.ird.cantharella.web.utils.models.LoadableDetachableSortableListDataProvider;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * Panel qui liste les méthodes de tests biologiques
 * 
 * @author Adrien Cheype
 */
public class ListMethodeTestBioPanel extends Panel {

    /** Service : test */
    @SpringBean
    private TestBioService testService;

    /**
     * Constructor
     * 
     * @param id The panel ID
     */
    public ListMethodeTestBioPanel(String id) {
        super(id);

        add(new BookmarkablePageLink<Void>("ListMethodeTestBioPanel.NewMethodeTestBio", ManageMethodeTestBioPage.class));

        // On englobe le "DataView" dans un composant neutre que l'on pourra
        // rafraichir quand la liste évoluera
        final MarkupContainer methodesTestRefresh = new WebMarkupContainer(
                "ListMethodeTestBioPanel.MethodesTestBio.Refresh");
        methodesTestRefresh.setOutputMarkupId(true);
        add(methodesTestRefresh);

        // Liste des methodesTest
        final List<MethodeTestBio> methodesTest = testService.listMethodesTestBio();
        LoadableDetachableSortableListDataProvider<MethodeTestBio> methodesDataProvider = new LoadableDetachableSortableListDataProvider<MethodeTestBio>(
                methodesTest, getSession().getLocale());

        methodesTestRefresh.add(new DataView<MethodeTestBio>("ListMethodeTestBioPanel.MethodesTestBio",
                methodesDataProvider) {
            @Override
            protected void populateItem(Item<MethodeTestBio> item) {
                if (item.getIndex() % 2 == 1) {
                    item.add(new AttributeModifier("class", item.getIndex() % 2 == 0 ? "even" : "odd"));
                }

                MethodeTestBio methodeExtraction = item.getModelObject();

                // Colonnes
                item.add(new Label("ListMethodeTestBioPanel.MethodesTestBio.nom", new PropertyModel<String>(
                        methodeExtraction, "nom")));
                item.add(new Label("ListMethodeTestBioPanel.MethodesTestBio.cible", new PropertyModel<String>(
                        methodeExtraction, "cible")));
                item.add(new Label("ListMethodeTestBioPanel.MethodesTestBio.domaine", new PropertyModel<String>(
                        methodeExtraction, "domaine")));
                item.add(new MultiLineLabel("ListMethodeTestBioPanel.MethodesTestBio.description",
                        new PropertyModel<String>(methodeExtraction, "description")));
                item.add(new Label("ListMethodeTestBioPanel.MethodesTestBio.valeurMesuree", new PropertyModel<String>(
                        methodeExtraction, "valeurMesuree")));
                item.add(new Label("ListMethodeTestBioPanel.MethodesTestBio.uniteResultat", new PropertyModel<String>(
                        methodeExtraction, "uniteResultat")));
                item.add(new Label("ListMethodeTestBioPanel.MethodesTestBio.critereActivite",
                        new PropertyModel<String>(methodeExtraction, "critereActivite")));

                // Action : mise à jour (redirection vers le formulaire)
                Link<MethodeTestBio> updateLink = new Link<MethodeTestBio>(
                        "ListMethodeTestBioPanel.MethodesTestBio.Update", new Model<MethodeTestBio>(methodeExtraction)) {
                    @Override
                    public void onClick() {
                        setResponsePage(new ManageMethodeTestBioPage(getModelObject().getIdMethodeTest()));
                    }
                };
                item.add(updateLink);
            }
        });
    }

}
