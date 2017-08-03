/*
 * #%L
 * Cantharella :: Web
 * $Id: ListMethodePurificationPanel.java 268 2014-05-06 15:45:40Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.web/src/main/java/nc/ird/cantharella/web/pages/domain/config/panels/ListMethodePurificationPanel.java $
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

import nc.ird.cantharella.data.model.MethodePurification;
import nc.ird.cantharella.service.services.PurificationService;
import nc.ird.cantharella.web.pages.domain.config.ManageMethodePurificationPage;
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
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * Panel qui liste les méthodes de purification
 * 
 * @author Adrien Cheype
 */
public class ListMethodePurificationPanel extends Panel {

    /** Service : purification */
    @SpringBean
    private PurificationService purificationService;

    /**
     * Constructor
     * 
     * @param id The panel ID
     */
    public ListMethodePurificationPanel(String id) {
        super(id);

        add(new BookmarkablePageLink<Void>("ListMethodePurificationPanel.NewMethodePurification",
                ManageMethodePurificationPage.class));

        // On englobe le "DataView" dans un composant neutre que l'on pourra
        // rafraichir quand la liste évoluera
        final MarkupContainer methodesPurificationRefresh = new WebMarkupContainer(
                "ListMethodePurificationPanel.MethodesPurification.Refresh");
        methodesPurificationRefresh.setOutputMarkupId(true);
        add(methodesPurificationRefresh);

        // Liste des methodesPurification
        final List<MethodePurification> methodesPurification = purificationService.listMethodesPurification();
        LoadableDetachableSortableListDataProvider<MethodePurification> methodesDataProvider = new LoadableDetachableSortableListDataProvider<MethodePurification>(
                methodesPurification, getSession().getLocale());

        methodesPurificationRefresh.add(new DataView<MethodePurification>(
                "ListMethodePurificationPanel.MethodesPurification", methodesDataProvider) {
            @Override
            protected void populateItem(Item<MethodePurification> item) {
                if (item.getIndex() % 2 == 1) {
                    item.add(new AttributeModifier("class", item.getIndex() % 2 == 0 ? "even" : "odd"));
                }

                MethodePurification methodePurification = item.getModelObject();
                // Refresh the methodePurification for Ajax refreshes (parametres is LAZY and session different from
                // the one used for the init)
                purificationService.refreshMethodePurification(methodePurification);
                // Colonnes
                item.add(new Label("ListMethodePurificationPanel.MethodesPurification.nom", methodePurification
                        .getNom()));
                item.add(new MultiLineLabel("ListMethodePurificationPanel.MethodesPurification.description",
                        methodePurification.getDescription()));
                item.add(new Label("ListMethodePurificationPanel.MethodesPurification.parametres", StringUtils.join(
                        methodePurification.getParametres(), ", ")));

                // Action : mise à jour (redirection vers le formulaire)
                Link<MethodePurification> updateLink = new Link<MethodePurification>(
                        "ListMethodePurificationPanel.MethodesPurification.Update", new Model<MethodePurification>(
                                methodePurification)) {
                    @Override
                    public void onClick() {
                        setResponsePage(new ManageMethodePurificationPage(getModelObject().getIdMethodePurification()));
                    }
                };
                item.add(updateLink);
            }
        });

    }

}
