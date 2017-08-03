/*
 * #%L
 * Cantharella :: Web
 * $Id: UpdateUtilisateurPage.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.web/src/main/java/nc/ird/cantharella/web/pages/domain/utilisateur/UpdateUtilisateurPage.java $
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
import nc.ird.cantharella.web.pages.TemplatePage;
import nc.ird.cantharella.web.pages.domain.personne.panels.ManagePersonnePanel;
import nc.ird.cantharella.web.pages.model.UpdateUtilisateurModel;
import nc.ird.cantharella.web.utils.CallerPage;
import nc.ird.cantharella.web.utils.forms.SubmittableButton;
import nc.ird.cantharella.web.utils.forms.SubmittableButtonEvents;
import nc.ird.cantharella.web.utils.security.AuthRole;
import nc.ird.cantharella.web.utils.security.AuthRoles;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * Mise à jour du profil utilisateur
 * 
 * @author Mickael Tricot
 * @author Adrien Cheype
 */
@AuthRoles({ AuthRole.USER, AuthRole.ADMIN })
public final class UpdateUtilisateurPage extends TemplatePage {

    /** Action : update password */
    private static final String ACTION_UPDATE_PASSWORD = "UpdatePassword";

    /** Action : update profile */
    private static final String ACTION_UPDATE_PROFILE = "UpdateProfile";

    /** Service : personne */
    @SpringBean
    private PersonneService personneService;

    /** Validateur modèle */
    @SpringBean(name = "webModelValidator")
    private ModelValidator validator;

    /**
     * Constructeur
     */
    public UpdateUtilisateurPage() {
        this(null);
    }

    /**
     * Constructeur
     * 
     * @param callerPage Page appelante (pour redirection)
     */
    public UpdateUtilisateurPage(final CallerPage callerPage) {
        super(UpdateUtilisateurPage.class);

        // Initialisation du modèle (pour le profil)
        final IModel<Utilisateur> utilisateurProfileModel = new Model<Utilisateur>(getSession().getUtilisateur());

        // Initialisation du modèle supplémentaire
        final IModel<UpdateUtilisateurModel> utilisateurPasswordModel = new Model<UpdateUtilisateurModel>(
                new UpdateUtilisateurModel());

        // Formulaire de mise à jour du profil
        final Form<Utilisateur> formProfileView = new Form<Utilisateur>("FormProfile", utilisateurProfileModel);

        final ManagePersonnePanel personnePanel = new ManagePersonnePanel("ManagePersonnePanel",
                utilisateurProfileModel);
        formProfileView.add(personnePanel);

        // Action : mise à jour du profil
        formProfileView.add(new SubmittableButton(ACTION_UPDATE_PROFILE, new SubmittableButtonEvents() {
            @Override
            public void onError() {
                utilisateurPasswordModel.getObject().setPassword(null);
            }

            @Override
            public void onProcess() throws DataConstraintException {
                personneService.updateUtilisateur(utilisateurProfileModel.getObject(), false);
                getSession().update(utilisateurProfileModel.getObject());
            }

            @Override
            public void onSuccess() {
                if (callerPage == null) {
                    successCurrentPage(ACTION_UPDATE_PROFILE);
                } else {
                    successNextPage(ACTION_UPDATE_PROFILE);
                    callerPage.responsePage((TemplatePage) getPage());
                }
            }

            @Override
            public void onValidate() {
                personnePanel.validate();
            }
        }));

        // Action : annulation
        Link<Void> cancelProfileLink = new Link<Void>("CancelProfile") {
            @Override
            public void onClick() {
                callerPage.responsePage((TemplatePage) getPage());
            }
        };
        cancelProfileLink.setVisibilityAllowed(callerPage != null);
        formProfileView.add(cancelProfileLink);
        add(formProfileView);

        // Formulaire de mise à jour du mot de passe
        final Form<UpdateUtilisateurModel> formPasswordView = new Form<UpdateUtilisateurModel>("FormPassword",
                utilisateurPasswordModel);

        final Component currentPasswordView = new PasswordTextField("UpdateUtilisateurModel.currentPassword",
                new PropertyModel<String>(utilisateurPasswordModel, "currentPassword")).setRequired(false);
        formPasswordView.add(currentPasswordView);
        formPasswordView.add(new PasswordTextField("UpdateUtilisateurModel.newPassword", new PropertyModel<String>(
                utilisateurPasswordModel, "newPassword")).setRequired(false));
        final Component newPasswordConfirmationView = new PasswordTextField(
                "UpdateUtilisateurModel.newPasswordConfirmation", new PropertyModel<String>(utilisateurPasswordModel,
                        "newPasswordConfirmation")).setRequired(false);
        formPasswordView.add(newPasswordConfirmationView);

        // Action : mise à jour du mot de passe
        formPasswordView.add(new SubmittableButton(ACTION_UPDATE_PASSWORD, new SubmittableButtonEvents() {
            @Override
            public void onError() {
                utilisateurPasswordModel.getObject().setCurrentPassword(null);
                utilisateurPasswordModel.getObject().setNewPassword(null);
                utilisateurPasswordModel.getObject().setNewPasswordConfirmation(null);
            }

            @Override
            public void onProcess() throws DataConstraintException {
                utilisateurProfileModel.getObject().setPasswordHash(
                        personneService.hashPassword(utilisateurPasswordModel.getObject().getNewPassword()));
                personneService.updateUtilisateur(utilisateurProfileModel.getObject(), false);
                getSession().update(utilisateurProfileModel.getObject());
            }

            @Override
            public void onSuccess() {
                if (callerPage == null) {
                    successCurrentPage(ACTION_UPDATE_PASSWORD);
                } else {
                    successNextPage(ACTION_UPDATE_PASSWORD);
                    callerPage.responsePage((TemplatePage) getPage());
                }
            }

            @Override
            public void onValidate() {
                // Erreurs mot de passe courant
                List<String> passwordErrors = validator.validate(utilisateurPasswordModel.getObject(), getSession()
                        .getLocale(), "currentPassword");
                addValidationErrors(passwordErrors);
                if (passwordErrors.isEmpty()
                        && !utilisateurProfileModel
                                .getObject()
                                .getPasswordHash()
                                .equals(personneService.hashPassword(utilisateurPasswordModel.getObject()
                                        .getCurrentPassword()))) {
                    errorCurrentPage(currentPasswordView);
                }
                // Erreurs nouveau mot de passe
                passwordErrors = validator.validate(utilisateurPasswordModel.getObject(), getSession().getLocale(),
                        "newPassword");
                addValidationErrors(passwordErrors);
                if (passwordErrors.isEmpty() && !utilisateurPasswordModel.getObject().validate()) {
                    errorCurrentPage(newPasswordConfirmationView);
                }
            }

        }));

        // Action : annulation
        Link<Void> cancelPasswordLink = new Link<Void>("CancelPassword") {
            @Override
            public void onClick() {
                callerPage.responsePage((TemplatePage) getPage());
            }
        };
        cancelPasswordLink.setVisibilityAllowed(callerPage != null);
        formPasswordView.add(cancelPasswordLink);

        add(formPasswordView);
    }
}
