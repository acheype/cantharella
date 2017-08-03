/*
 * #%L
 * Cantharella :: Web
 * $Id: ManagePersonnePanel.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.web/src/main/java/nc/ird/cantharella/web/pages/domain/personne/panels/ManagePersonnePanel.java $
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

import java.util.List;

import nc.ird.cantharella.data.model.Personne;
import nc.ird.cantharella.data.validation.utils.ModelValidator;
import nc.ird.cantharella.service.services.PersonneService;
import nc.ird.cantharella.web.config.WebContext;
import nc.ird.cantharella.web.pages.TemplatePage;
import nc.ird.cantharella.web.utils.forms.AutoCompleteTextFieldString;
import nc.ird.cantharella.web.utils.forms.AutoCompleteTextFieldString.ComparisonMode;
import nc.ird.cantharella.web.utils.renderers.MapChoiceRenderer;

import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * Panneau de gestion d'une personne
 * 
 * @author Mickael Tricot
 * @author Adrien Cheype
 */
public final class ManagePersonnePanel extends Panel {

    /** Model validateur */
    @SpringBean(name = "webModelValidator")
    private ModelValidator validator;

    /** Services : personne */
    @SpringBean
    private PersonneService personneService;

    /**
     * Constructeur
     * 
     * @param id ID
     * @param personneModel Modèle
     */
    public ManagePersonnePanel(String id, IModel<? extends Personne> personneModel) {
        super(id, personneModel);

        // liste des organismes suggérés à la saisie
        final List<String> organismes = personneService.listPersonneOrganismes();

        add(new TextField<String>("Personne.prenom", new PropertyModel<String>(personneModel, "prenom")));
        add(new TextField<String>("Personne.nom", new PropertyModel<String>(personneModel, "nom")));
        add(new AutoCompleteTextFieldString("Personne.organisme",
                new PropertyModel<String>(personneModel, "organisme"), organismes, ComparisonMode.CONTAINS));
        add(new TextField<String>("Personne.fonction", new PropertyModel<String>(personneModel, "fonction")));
        add(new TextField<String>("Personne.tel", new PropertyModel<String>(personneModel, "tel")));
        add(new TextField<String>("Personne.fax", new PropertyModel<String>(personneModel, "fax")));
        add(new TextField<String>("Personne.courriel", new PropertyModel<String>(personneModel, "courriel")));
        add(new TextArea<String>("Personne.adressePostale", new PropertyModel<String>(personneModel, "adressePostale")));
        add(new TextField<String>("Personne.codePostal", new PropertyModel<String>(personneModel, "codePostal")));
        add(new TextField<String>("Personne.ville", new PropertyModel<String>(personneModel, "ville")));
        add(new DropDownChoice<String>("Personne.codePays", new PropertyModel<String>(personneModel, "codePays"),
                WebContext.COUNTRY_CODES.get(getSession().getLocale()), new MapChoiceRenderer<String, String>(
                        WebContext.COUNTRIES.get(getSession().getLocale()))));
        if (personneModel.getObject().getCodePays() == null) {
            personneModel.getObject().setCodePays(
                    WebContext.COUNTRIES.get(getSession().getLocale()).entrySet().iterator().next().getKey());
        }

    }

    /**
     * Validation
     */
    public void validate() {
        ((TemplatePage) getPage()).addValidationErrors(validator.validate((Personne) getDefaultModelObject(),
                getSession().getLocale(), "prenom", "nom", "organisme", "fonction", "tel", "fax", "courriel",
                "adressePostale", "codePostal", "ville", "codePays"));
    }
}
