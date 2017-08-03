/*
 * #%L
 * Cantharella :: Web
 * $Id: HomePage.java 268 2014-05-06 15:45:40Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.web/src/main/java/nc/ird/cantharella/web/pages/HomePage.java $
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
package nc.ird.cantharella.web.pages;

import java.text.MessageFormat;
import java.util.List;

import nc.ird.cantharella.data.exceptions.DataConstraintException;
import nc.ird.cantharella.data.exceptions.DataNotFoundException;
import nc.ird.cantharella.data.model.Utilisateur;
import nc.ird.cantharella.data.model.Utilisateur.TypeDroit;
import nc.ird.cantharella.data.validation.utils.ModelValidator;
import nc.ird.cantharella.service.services.CampagneService;
import nc.ird.cantharella.service.services.ExtractionService;
import nc.ird.cantharella.service.services.LotService;
import nc.ird.cantharella.service.services.MoleculeService;
import nc.ird.cantharella.service.services.PersonneService;
import nc.ird.cantharella.service.services.PurificationService;
import nc.ird.cantharella.service.services.SpecimenService;
import nc.ird.cantharella.service.services.StationService;
import nc.ird.cantharella.service.services.TestBioService;
import nc.ird.cantharella.utils.BeanTools.AccessType;
import nc.ird.cantharella.utils.CollectionTools;
import nc.ird.cantharella.web.pages.domain.utilisateur.ManageUtilisateurPage;
import nc.ird.cantharella.web.pages.domain.utilisateur.ReadUtilisateurPage;
import nc.ird.cantharella.web.pages.domain.utilisateur.RegisterPage;
import nc.ird.cantharella.web.pages.domain.utilisateur.ResetPasswordPage;
import nc.ird.cantharella.web.pages.model.LoginModel;
import nc.ird.cantharella.web.utils.CallerPage;
import nc.ird.cantharella.web.utils.behaviors.JSConfirmationBehavior;
import nc.ird.cantharella.web.utils.forms.SubmittableButton;
import nc.ird.cantharella.web.utils.forms.SubmittableButtonEvents;
import nc.ird.cantharella.web.utils.models.LoadableDetachableSortableListDataProvider;
import nc.ird.cantharella.web.utils.security.AuthContainer;
import nc.ird.cantharella.web.utils.security.AuthRole;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Home page
 * 
 * @author Mickael Tricot
 * @author Adrien Cheype
 */
public final class HomePage extends TemplatePage {

    /** Logger */
    private static final Logger LOG = LoggerFactory.getLogger(HomePage.class);

    /** Action : login */
    private static final String ACTION_LOGIN = "Login";

    /** Service : personne */
    @SpringBean
    private PersonneService personneService;

    /** Service : campagne */
    @SpringBean
    private CampagneService campagneService;

    /** Service : station */
    @SpringBean
    private StationService stationService;

    /** Service : specimen */
    @SpringBean
    private SpecimenService specimenService;

    /** Service : lots */
    @SpringBean
    private LotService lotService;

    /** Service : extraction */
    @SpringBean
    private ExtractionService extractionService;

    /** Service : purification */
    @SpringBean
    private PurificationService purificationService;

    /** Service : molecule */
    @SpringBean
    private MoleculeService moleculeService;

    /** Service : test biologique */
    @SpringBean
    private TestBioService testBioService;

    /** Validateur modèle */
    @SpringBean(name = "webModelValidator")
    private ModelValidator validator;

    /**
     * Constructeur
     */
    public HomePage() {
        super(HomePage.class);
        addVisitorContent();
        addUserContent();
        addAdminContent();
    }

    /**
     * Ajour du contenu administrateur
     */
    private void addAdminContent() {
        final AuthContainer adminContent = new AuthContainer("Content.ADMIN", AuthRole.ADMIN);
        if (adminContent.isAuthorized()) {

            // Affiche les utilisateurs à valider sur la page d'accueil ADMIN
            final MarkupContainer invalidUtilisateursView = new WebMarkupContainer("InvalidUtilisateurs.List");
            invalidUtilisateursView.setOutputMarkupId(true);

            final List<Utilisateur> utilisateursInvalid = personneService.listUtilisateursInvalid();
            invalidUtilisateursView.setVisibilityAllowed(!utilisateursInvalid.isEmpty());
            if (invalidUtilisateursView.isVisibilityAllowed()) {
                invalidUtilisateursView.add(new DataView<Utilisateur>("InvalidUtilisateurs.List.Utilisateur",
                        new LoadableDetachableSortableListDataProvider<Utilisateur>(utilisateursInvalid, getSession()
                                .getLocale())) {
                    @Override
                    protected void populateItem(Item<Utilisateur> item) {
                        Utilisateur utilisateur = item.getModelObject();
                        item.add(new Label("InvalidUtilisateurs.List.Utilisateur.Label", utilisateur.toString()));

                        // Action : valider l'utilisateur
                        item.add(new AjaxFallbackLink<Utilisateur>("InvalidUtilisateurs.List.Utilisateur.Valid",
                                new Model<Utilisateur>(utilisateur)) {
                            @Override
                            public void onClick(AjaxRequestTarget target) {
                                try {
                                    // Validation
                                    personneService.validAndUpdateUtilisateur(getModelObject());
                                    successCurrentPage(ManageUtilisateurPage.class, "Update");

                                    // Mise à jour de la liste
                                    CollectionTools.setter(utilisateursInvalid,
                                            personneService.listUtilisateursInvalid());
                                    invalidUtilisateursView.setVisibilityAllowed(!utilisateursInvalid.isEmpty());
                                    if (target != null) {
                                        target.add(invalidUtilisateursView);
                                    }
                                } catch (DataConstraintException e) {
                                    errorCurrentPage(ManageUtilisateurPage.class, "Update", e);
                                }
                                refreshFeedbackPage(target);
                            }
                        });

                        // Action : mettre à jour l'utilisateur (redirection vers le formulaire)
                        item.add(new Link<Utilisateur>("InvalidUtilisateurs.List.Utilisateur.Update",
                                new Model<Utilisateur>(utilisateur)) {
                            @Override
                            public void onClick() {
                                setResponsePage(new ManageUtilisateurPage(getModelObject().getIdPersonne(),
                                        new CallerPage(HomePage.class)));
                            }
                        });

                        // Action : supprimer l'utilisateur
                        Link<Utilisateur> deleteLink = new AjaxFallbackLink<Utilisateur>(
                                "InvalidUtilisateurs.List.Utilisateur.Reject", new Model<Utilisateur>(utilisateur)) {
                            @Override
                            public void onClick(AjaxRequestTarget target) {
                                // Suppression
                                personneService.rejectUtilisateur(getModelObject());
                                successCurrentPage(ManageUtilisateurPage.class, "Reject");

                                // Mise à jour de la liste
                                CollectionTools.setter(utilisateursInvalid, personneService.listUtilisateursInvalid());
                                invalidUtilisateursView.setVisibilityAllowed(!utilisateursInvalid.isEmpty());
                                if (target != null) {
                                    target.add(invalidUtilisateursView);
                                }
                                refreshFeedbackPage(target);
                            }
                        };
                        deleteLink.add(new JSConfirmationBehavior(getStringModel("Confirm")));
                        item.add(deleteLink);
                    }
                });

            }

            adminContent.add(invalidUtilisateursView);

        }
        add(adminContent);
    }

    /**
     * Ajour du contenu utilisateur (s'affiche également pour l'admin)
     */
    private void addUserContent() {
        AuthContainer userContent = new AuthContainer("Content.USER", AuthRole.USER, AuthRole.ADMIN);
        if (userContent.isAuthorized()) {
            userContent.add(new Label(getResource() + ".Statistics.Personnes", String.valueOf(personneService
                    .countPersonnes())));
            userContent.add(new Label(getResource() + ".Statistics.Campagnes", String.valueOf(campagneService
                    .countCampagnes())));
            userContent.add(new Label(getResource() + ".Statistics.Stations", String.valueOf(stationService
                    .countStations())));
            userContent.add(new Label(getResource() + ".Statistics.Specimens", String.valueOf(specimenService
                    .countSpecimens())));
            userContent.add(new Label(getResource() + ".Statistics.Lots", String.valueOf(lotService.countLots())));
            userContent.add(new Label(getResource() + ".Statistics.Extractions", String.valueOf(extractionService
                    .countExtractions())));
            userContent.add(new Label(getResource() + ".Statistics.Purifications", String.valueOf(purificationService
                    .countPurifications())));
            userContent.add(new Label(getResource() + ".Statistics.Molecules", String.valueOf(moleculeService
                    .countMolecules())));
            userContent.add(new Label(getResource() + ".Statistics.TestsBio", String.valueOf(testBioService
                    .countResultatsTestsBio())));

            // affichage du volet droits
            Link<Utilisateur> detailsRight = new Link<Utilisateur>("HomePage.Rights.User.Details",
                    new Model<Utilisateur>(getSession().getUtilisateur())) {
                @Override
                public void onClick() {
                    setResponsePage(new ReadUtilisateurPage(getModelObject().getIdPersonne(), new CallerPage(
                            HomePage.this)));
                }

                /** {@inheritDoc} */
                @Override
                protected CharSequence getURL() {
                    return super.getURL() + "#rights";
                }

            };
            userContent.add(detailsRight);

            if (getSession().getUtilisateur().getTypeDroit() == TypeDroit.ADMINISTRATEUR) {
                userContent.add(new Label("HomePage.Rights.User1", getStringModel("HomePage.Rights.Admin")));
                // pas d'affichage du lien de détails
                detailsRight.setVisibilityAllowed(false);
                EmptyPanel rights2 = new EmptyPanel("HomePage.Rights.User2");
                rights2.setVisibilityAllowed(false);
                userContent.add(rights2);
            } else {
                // pour l'utilisateur simple, nb de campagnes et de lots accessibles affichés
                Integer nbCampagnesDroits = getSession().getUtilisateur().getCampagnesDroits().size();
                Integer nbLotsDroits = getSession().getUtilisateur().getLotsDroits().size();
                String msgRights = MessageFormat.format(getString("HomePage.Rights.User"), nbCampagnesDroits,
                        nbLotsDroits);
                userContent.add(new Label("HomePage.Rights.User1", msgRights));

                Integer nbCampagnesCrees = getSession().getUtilisateur().getCampagnesCreees().size();
                String msgRights2 = MessageFormat.format(getString("HomePage.Rights.User2"), nbCampagnesCrees);
                Label rights2 = new Label("HomePage.Rights.User2", msgRights2);
                rights2.setVisibilityAllowed(nbCampagnesCrees != 0);
                userContent.add(rights2);
            }
        }
        add(userContent);
    }

    /**
     * Ajout du contenu visiteur
     */
    private void addVisitorContent() {
        AuthContainer visitorContent = new AuthContainer("Content.VISITOR", AuthRole.VISITOR);
        if (visitorContent.isAuthorized()) {

            // Initialisation des modèles pour la connexion
            final IModel<LoginModel> loginModel = new Model<LoginModel>(new LoginModel());
            final IModel<Utilisateur> utilisateurModel = new Model<Utilisateur>(new Utilisateur());

            // Formulaire de connexion
            final Form<Void> formView = new Form<Void>("Form");

            // Champs du formulaire
            formView.add(new TextField<String>("Personne.courriel", new PropertyModel<String>(utilisateurModel,
                    "courriel")));
            formView.add(new PasswordTextField("LoginModel.password", new PropertyModel<String>(loginModel, "password"))
                    .setRequired(false));
            formView.add(new CheckBox("LoginModel.rememberMe", new PropertyModel<Boolean>(loginModel, "rememberMe")));

            // Action : connexion
            formView.add(new SubmittableButton(ACTION_LOGIN, new SubmittableButtonEvents() {

                @Override
                public void onProcess() throws DataNotFoundException, DataConstraintException {
                    if (getSession().authenticate(utilisateurModel.getObject().getCourriel(),
                            personneService.hashPassword(loginModel.getObject().getPassword()))) {
                        Utilisateur user = personneService.loadUtilisateur(utilisateurModel.getObject().getCourriel());
                        getSession().connectUser(user, loginModel.getObject().getRememberMe());
                        LOG.info("connexion de l'utilisateur : " + user.getPrenom() + " " + user.getNom());
                    } else {
                        // si échec dans l'authentification
                        if (CollectionTools.containsWithValue(personneService.listUtilisateursInvalid(), "courriel",
                                AccessType.GETTER, utilisateurModel.getObject().getCourriel())) {
                            // si le courriel est celui d'un utilisateur non validé, message d'erreur approprié
                            error(getString("HomePage.Login.NotYetValid"));
                        } else {
                            error(getString("HomePage.Login.KO"));
                        }
                        // Ràz du mot de passe
                        loginModel.getObject().setPassword(null);
                    }
                }

                @Override
                public void onSuccess() {
                    successNextPage(ACTION_LOGIN);
                    setResponsePage(getApplication().getHomePage());
                }

                @Override
                public void onValidate() {
                    // Validate Utilisateur
                    addValidationErrors(validator.validate(utilisateurModel.getObject(), getSession().getLocale(),
                            "courriel"));
                    // Validate LoginModel
                    addValidationErrors(validator.validate(loginModel.getObject(), getSession().getLocale(),
                            "password", "rememberMe"));
                }
            }));

            // Lien pour regénérer le mot de passe
            formView.add(new BookmarkablePageLink<Void>(ResetPasswordPage.class.getSimpleName(),
                    ResetPasswordPage.class));

            // Lien pour s'enregistrer
            visitorContent.add(new BookmarkablePageLink<Void>(RegisterPage.class.getSimpleName(), RegisterPage.class));

            visitorContent.add(formView);

        }
        add(visitorContent);
    }
}