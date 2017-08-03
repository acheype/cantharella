/*
 * #%L
 * Cantharella :: Web
 * $Id: RebuildLuceneIndexPanel.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.web/src/main/java/nc/ird/cantharella/web/pages/domain/config/panels/RebuildLuceneIndexPanel.java $
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
package nc.ird.cantharella.web.pages.domain.config.panels;

import nc.ird.cantharella.service.services.SearchService;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxFallbackButton;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * Panel qui permet de reconstruire l'index lucene.
 * 
 * @author Eric Chatellier
 */
public class RebuildLuceneIndexPanel extends Panel {

    /** Service : test */
    @SpringBean
    private SearchService searchService;

    /**
     * Constructor
     * 
     * @param id The panel ID
     */
    public RebuildLuceneIndexPanel(String id) {
        super(id);

        final Form<Void> formView = new Form<Void>("Form");

        final IModel<String> stringLabel = Model.of("");
        final Label label = new Label("Status", stringLabel);
        label.setOutputMarkupId(true);
        formView.add(label);

        final AjaxFallbackButton addButton = new AjaxFallbackButton("Rebuild", formView) {
            @Override
            protected void onAfterSubmit(AjaxRequestTarget target, Form<?> form) {
                searchService.reIndex();
                stringLabel.setObject("Done");
                target.add(label);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                stringLabel.setObject("Error");
                target.add(label);
            }
        };
        addButton.setOutputMarkupId(true);
        formView.add(addButton);

        add(formView);
    }
}
