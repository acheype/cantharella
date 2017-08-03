/*
 * #%L
 * Cantharella :: Web
 * $Id: ListMethodeExtractionPanel.java 268 2014-05-06 15:45:40Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.web/src/main/java/nc/ird/cantharella/web/pages/domain/config/panels/ListMethodeExtractionPanel.java $
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

import nc.ird.cantharella.data.model.MethodeExtraction;
import nc.ird.cantharella.service.services.ExtractionService;
import nc.ird.cantharella.web.pages.domain.config.ManageMethodeExtractionPage;
import nc.ird.cantharella.web.utils.models.LoadableDetachableSortableListDataProvider;

import org.apache.commons.lang3.StringUtils;
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
 * Panel qui liste les méthodes d'extraction
 * 
 * @author Adrien Cheype
 */
public class ListMethodeExtractionPanel extends Panel {

    /** Service : extrait */
    @SpringBean
    private ExtractionService extraitService;

    /**
     * Constructor
     * 
     * @param id The panel ID
     */
    public ListMethodeExtractionPanel(String id) {
        super(id);

        add(new BookmarkablePageLink<Void>("ListMethodeExtractionPanel.NewMethodeExtraction",
                ManageMethodeExtractionPage.class));

        // On englobe le "DataView" dans un composant neutre que l'on pourra
        // rafraichir quand la liste évoluera
        final MarkupContainer methodesExtractionRefresh = new WebMarkupContainer(
                "ListMethodeExtractionPanel.MethodesExtraction.Refresh");
        methodesExtractionRefresh.setOutputMarkupId(true);
        add(methodesExtractionRefresh);

        // Liste des methodesExtraction
        final List<MethodeExtraction> methodesExtraction = extraitService.listMethodesExtraction();
        LoadableDetachableSortableListDataProvider<MethodeExtraction> methodesDataProvider = new LoadableDetachableSortableListDataProvider<MethodeExtraction>(
                methodesExtraction, getSession().getLocale());

        methodesExtractionRefresh.add(new DataView<MethodeExtraction>("ListMethodeExtractionPanel.MethodesExtraction",
                methodesDataProvider) {
            @Override
            protected void populateItem(Item<MethodeExtraction> item) {
                if (item.getIndex() % 2 == 1) {
                    item.add(new AttributeModifier("class", item.getIndex() % 2 == 0 ? "even" : "odd"));
                }

                MethodeExtraction methodeExtraction = item.getModelObject();
                // Refresh the methodeExtraction for Ajax refreshes (typesEnSorties is LAZY and session different from
                // the one used for the init)
                extraitService.refreshMethodeExtraction(methodeExtraction);
                // Colonnes
                item.add(new Label("ListMethodeExtractionPanel.MethodesExtraction.nom", new PropertyModel<String>(
                        methodeExtraction, "nom")));
                item.add(new MultiLineLabel("ListMethodeExtractionPanel.MethodesExtraction.description",
                        new PropertyModel<String>(methodeExtraction, "description")));
                item.add(new Label("ListMethodeExtractionPanel.MethodesExtraction.typesEnSortie", StringUtils.join(
                        methodeExtraction.getSortedTypesEnSortie(), ", ")));

                // Action : mise à jour (redirection vers le formulaire)
                Link<MethodeExtraction> updateLink = new Link<MethodeExtraction>(
                        "ListMethodeExtractionPanel.MethodesExtraction.Update", new Model<MethodeExtraction>(
                                methodeExtraction)) {
                    @Override
                    public void onClick() {
                        setResponsePage(new ManageMethodeExtractionPage(getModelObject().getIdMethodeExtraction()));
                    }
                };
                item.add(updateLink);
            }
        });
    }

}
