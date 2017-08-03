/*
 * #%L
 * Cantharella :: Web
 * $Id: ReadPersonnePanel.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.web/src/main/java/nc/ird/cantharella/web/pages/domain/personne/panels/ReadPersonnePanel.java $
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
package nc.ird.cantharella.web.pages.domain.personne.panels;

import nc.ird.cantharella.data.model.Personne;
import nc.ird.cantharella.web.config.WebContext;
import nc.ird.cantharella.web.utils.behaviors.ReplaceEmptyLabelBehavior;
import nc.ird.cantharella.web.utils.models.DisplayMapValuePropertyModel;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

/**
 * Panneau de consultation d'une personne
 * 
 * @author Alban Diguer
 */
public final class ReadPersonnePanel extends Panel {

    /**
     * Constructeur
     * 
     * @param id ID
     * @param personneModel Modèle
     */
    public ReadPersonnePanel(String id, IModel<? extends Personne> personneModel) {
        super(id, personneModel);
        add(new Label("Personne.prenom", new PropertyModel<String>(personneModel, "prenom"))
                .add(new ReplaceEmptyLabelBehavior()));
        add(new Label("Personne.nom", new PropertyModel<String>(personneModel, "nom"))
                .add(new ReplaceEmptyLabelBehavior()));
        add(new Label("Personne.organisme", new PropertyModel<String>(personneModel, "organisme"))
                .add(new ReplaceEmptyLabelBehavior()));
        add(new Label("Personne.fonction", new PropertyModel<String>(personneModel, "fonction"))
                .add(new ReplaceEmptyLabelBehavior()));
        add(new Label("Personne.tel", new PropertyModel<String>(personneModel, "tel"))
                .add(new ReplaceEmptyLabelBehavior()));
        add(new Label("Personne.fax", new PropertyModel<String>(personneModel, "fax"))
                .add(new ReplaceEmptyLabelBehavior()));
        add(new Label("Personne.courriel", new PropertyModel<String>(personneModel, "courriel"))
                .add(new ReplaceEmptyLabelBehavior()));
        add(new MultiLineLabel("Personne.adressePostale", new PropertyModel<String>(personneModel, "adressePostale"))
                .add(new ReplaceEmptyLabelBehavior()));
        add(new Label("Personne.codePostal", new PropertyModel<String>(personneModel, "codePostal"))
                .add(new ReplaceEmptyLabelBehavior()));
        add(new Label("Personne.ville", new PropertyModel<String>(personneModel, "ville"))
                .add(new ReplaceEmptyLabelBehavior()));
        add(new Label("Personne.codePays", new DisplayMapValuePropertyModel<String>(personneModel, "codePays",
                WebContext.COUNTRIES.get(getSession().getLocale()))).add(new ReplaceEmptyLabelBehavior()));
    }
}
