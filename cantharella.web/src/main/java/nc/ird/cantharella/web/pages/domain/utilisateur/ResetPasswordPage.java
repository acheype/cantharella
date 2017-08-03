/*
 * #%L
 * Cantharella :: Web
 * $Id: ResetPasswordPage.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.web/src/main/java/nc/ird/cantharella/web/pages/domain/utilisateur/ResetPasswordPage.java $
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
package nc.ird.cantharella.web.pages.domain.utilisateur;

import nc.ird.cantharella.data.exceptions.DataNotFoundException;
import nc.ird.cantharella.data.model.Utilisateur;
import nc.ird.cantharella.data.validation.utils.ModelValidator;
import nc.ird.cantharella.service.exceptions.EmailException;
import nc.ird.cantharella.service.services.PersonneService;
import nc.ird.cantharella.web.pages.TemplatePage;
import nc.ird.cantharella.web.utils.forms.SubmittableButton;
import nc.ird.cantharella.web.utils.forms.SubmittableButtonEvents;
import nc.ird.cantharella.web.utils.security.AuthRole;
import nc.ird.cantharella.web.utils.security.AuthRoles;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * Mise-à-jour du mot de passe perdu d'un utilisateur
 * 
 * @author Mickael Tricot
 * @author Adrien Cheype
 */
@AuthRoles(AuthRole.VISITOR)
public final class ResetPasswordPage extends TemplatePage {

    /** Service : personne */
    @SpringBean
    private PersonneService personneService;

    /** Validateur modèle */
    @SpringBean(name = "webModelValidator")
    private ModelValidator validator;

    /**
     * Constructeur
     */
    public ResetPasswordPage() {
        super(ResetPasswordPage.class);
        // Modèle
        final IModel<Utilisateur> utilisateurModel = new Model<Utilisateur>(new Utilisateur());
        // Formulaire
        Form<Utilisateur> formView = new Form<Utilisateur>("Form", utilisateurModel);
        formView.add(new TextField<String>("Personne.courriel", new PropertyModel<String>(utilisateurModel, "courriel")));

        // Action : regénération du mot de passe
        formView.add(new SubmittableButton("Reset", new SubmittableButtonEvents() {
            @Override
            public void onProcess() throws DataNotFoundException, EmailException {
                personneService.resetPasswordUtilisateur(utilisateurModel.getObject().getCourriel());
            }

            @Override
            public void onSuccess() {
                successNextPage("Reset");

                setResponsePage(getApplication().getHomePage());
            }

            @Override
            public void onValidate() {
                addValidationErrors(validator.validate(utilisateurModel.getObject(), getSession().getLocale(),
                        "courriel"));
            }
        }));

        add(formView);
    }
}
