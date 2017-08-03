/*
 * #%L
 * Cantharella :: Web
 * $Id: RegisterPage.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.web/src/main/java/nc/ird/cantharella/web/pages/domain/utilisateur/RegisterPage.java $
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

import java.util.List;

import nc.ird.cantharella.data.exceptions.DataConstraintException;
import nc.ird.cantharella.data.model.Utilisateur;
import nc.ird.cantharella.data.validation.utils.ModelValidator;
import nc.ird.cantharella.service.services.PersonneService;
import nc.ird.cantharella.web.pages.HomePage;
import nc.ird.cantharella.web.pages.TemplatePage;
import nc.ird.cantharella.web.pages.domain.personne.panels.ManagePersonnePanel;
import nc.ird.cantharella.web.pages.model.CaptchaModel;
import nc.ird.cantharella.web.pages.model.RegisterModel;
import nc.ird.cantharella.web.utils.forms.SubmittableButton;
import nc.ird.cantharella.web.utils.forms.SubmittableButtonEvents;
import nc.ird.cantharella.web.utils.security.AuthRole;
import nc.ird.cantharella.web.utils.security.AuthRoles;
import nc.ird.cantharella.utils.CaptchaTools;

import org.apache.wicket.Component;
import org.apache.wicket.extensions.markup.html.captcha.CaptchaImageResource;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.image.NonCachingImage;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * Page de demande de création de compte utilisateur
 * 
 * @author Mickael Tricot
 * @author Adrien Cheype
 */
@AuthRoles(AuthRole.VISITOR)
public final class RegisterPage extends TemplatePage {

    /** Service : personne */
    @SpringBean
    private PersonneService personneService;

    /** Validateur modèle */
    @SpringBean(name = "webModelValidator")
    private ModelValidator validator;

    /**
     * Constructeur
     */
    public RegisterPage() {
        super(RegisterPage.class);

        // Initialisation des modèles (registerModel, CaptchaModel, Utilisateur)
        final IModel<RegisterModel> registerModel = new Model<RegisterModel>(new RegisterModel());
        final IModel<CaptchaModel> captchaModel = new Model<CaptchaModel>(new CaptchaModel());
        captchaModel.getObject().setCaptchaTextGenerated(CaptchaTools.random());
        final IModel<Utilisateur> utilisateurModel = new Model<Utilisateur>(new Utilisateur());

        // Formulaire
        final Form<Void> formView = new Form<Void>("Form");
        final ManagePersonnePanel personnePanel = new ManagePersonnePanel("ManagePersonnePanel", utilisateurModel);
        formView.add(personnePanel);

        // Champs n'apparaissant pas dans le "ManagePersonnePanel"
        formView.add(new PasswordTextField("RegisterModel.password", new PropertyModel<String>(registerModel,
                "password")).setRequired(false));
        final Component passwordConfirmationView = new PasswordTextField("RegisterModel.passwordConfirmation",
                new PropertyModel<String>(registerModel, "passwordConfirmation")).setRequired(false);
        formView.add(passwordConfirmationView);
        formView.add(new NonCachingImage("CaptchaModel.captchaImage", new CaptchaImageResource(captchaModel.getObject()
                .getCaptchaTextGenerated())));
        final Component captchaTextView = new TextField<String>("CaptchaModel.captchaText", new PropertyModel<String>(
                captchaModel, "captchaText"));
        formView.add(captchaTextView);

        // Action : enregistrement de l'utilisateur
        formView.add(new SubmittableButton("Register", new SubmittableButtonEvents() {
            @Override
            public void onError() {
                // Remise à zéro de certains champs
                registerModel.getObject().setPassword(null);
                registerModel.getObject().setPasswordConfirmation(null);
                captchaModel.getObject().setCaptchaText(null);
                captchaModel.getObject().setCaptchaTextGenerated(CaptchaTools.random());
                formView.replace(new NonCachingImage("CaptchaModel.captchaImage", new CaptchaImageResource(captchaModel
                        .getObject().getCaptchaTextGenerated())));
            }

            @Override
            public void onProcess() throws DataConstraintException {
                // Cryptage du mot de passe
                utilisateurModel.getObject().setPasswordHash(
                        personneService.hashPassword(registerModel.getObject().getPassword()));
                // Mise à jour de l'utilisateur
                personneService.createUtilisateur(utilisateurModel.getObject());
            }

            @Override
            public void onSuccess() {
                successNextPage("Register");
                setResponsePage(HomePage.class);
            }

            @Override
            public void onValidate() {
                personnePanel.validate();
                List<String> passwordErrors = validator.validate(registerModel.getObject(), getSession().getLocale(),
                        "password");
                if (passwordErrors.isEmpty()) {
                    if (!registerModel.getObject().validate()) {
                        errorCurrentPage(passwordConfirmationView);
                    }
                } else {
                    addValidationErrors(passwordErrors);
                }
                if (!captchaModel.getObject().validate()) {
                    errorCurrentPage(captchaTextView);
                }
            }
        }));

        add(formView);
    }
}