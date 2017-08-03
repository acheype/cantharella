/*
 * #%L
 * Cantharella :: Web
 * $Id: ReadUtilisateurPage.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.web/src/main/java/nc/ird/cantharella/web/pages/domain/utilisateur/ReadUtilisateurPage.java $
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

import nc.ird.cantharella.data.exceptions.DataConstraintException;
import nc.ird.cantharella.data.model.Personne;
import nc.ird.cantharella.data.model.Utilisateur;
import nc.ird.cantharella.data.model.Utilisateur.TypeDroit;
import nc.ird.cantharella.service.services.PersonneService;
import nc.ird.cantharella.web.pages.TemplatePage;
import nc.ird.cantharella.web.pages.domain.document.panel.ReadListDocumentsPanel;
import nc.ird.cantharella.web.pages.domain.personne.panels.ReadPersonnePanel;
import nc.ird.cantharella.web.pages.domain.utilisateur.panels.ReadDroitsUtilisateurPanel;
import nc.ird.cantharella.web.utils.CallerPage;
import nc.ird.cantharella.web.utils.behaviors.JSConfirmationBehavior;
import nc.ird.cantharella.web.utils.forms.SubmittableButton;
import nc.ird.cantharella.web.utils.forms.SubmittableButtonEvents;
import nc.ird.cantharella.web.utils.models.DisplayEnumPropertyModel;
import nc.ird.cantharella.web.utils.models.GenericLoadableDetachableModel;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * Consultation d'un utilisateur
 * 
 * @author Alban Diguer
 */
public final class ReadUtilisateurPage extends TemplatePage {

    /** Action : delete */
    public static final String ACTION_DELETE = "Delete";

    /** Logger */
    //private static final Logger LOG = LoggerFactory.getLogger(ReadUtilisateurPage.class);
    /** Page appelante */
    private final CallerPage callerPage;

    /** Modèle : personne */
    private final IModel<Utilisateur> utilisateurModel;

    /** Service : personne */
    @SpringBean
    private PersonneService personneService;

    /**
     * Constructeur
     * 
     * @param idUtilisateur ID de l'utilisateur
     * @param callerPage Page appelante
     */
    public ReadUtilisateurPage(Integer idUtilisateur, CallerPage callerPage) {
        super(ReadUtilisateurPage.class);

        final CallerPage currentPage = new CallerPage((TemplatePage) getPage());
        this.callerPage = callerPage;

        // Initialisation du modèle
        utilisateurModel = new GenericLoadableDetachableModel<Utilisateur>(Utilisateur.class, idUtilisateur);

        // Ajout du panel ReadPersonnePanel
        add(new ReadPersonnePanel("ReadPersonnePanel", utilisateurModel));

        add(new Label("Utilisateur.typeDroits", new DisplayEnumPropertyModel(utilisateurModel, "typeDroit", this)));

        // Ajout du formulaire pour les actions
        final Form<Utilisateur> formView = new Form<Utilisateur>("Form", utilisateurModel);

        // Fieldset pour les droits de l'utilisateur (affiché uniquement si l'utilisateur est administrateur ou s'il
        // affiche son propre profil)
        MarkupContainer autorizationsFieldset = new WebMarkupContainer("ReadUtilisateurPage.AutorizationsFieldset") {
            /** {@inheritDoc} */
            @Override
            public boolean isVisible() {
                Utilisateur currentUser = ReadUtilisateurPage.this.getSession().getUtilisateur();
                return currentUser.getTypeDroit() == TypeDroit.ADMINISTRATEUR
                        || currentUser.getIdPersonne().equals(utilisateurModel.getObject().getIdPersonne());
            }
        };

        autorizationsFieldset.add(new ReadDroitsUtilisateurPanel("ReadDroitsUtilisateurPanel", utilisateurModel));
        add(autorizationsFieldset);

        // add list document panel
        ReadListDocumentsPanel readListDocumentsPanel = new ReadListDocumentsPanel("ReadListDocumentsPanel",
                utilisateurModel, currentPage);
        add(readListDocumentsPanel);

        // Action : mise à jour (redirection vers le formulaire)
        Link<Utilisateur> updateLink = new Link<Utilisateur>(getResource() + ".Personne.Update", utilisateurModel) {
            @Override
            public void onClick() {
                setResponsePage(new ManageUtilisateurPage(getModelObject().getIdPersonne(), currentPage));
            }
        };
        updateLink.setVisibilityAllowed(getSession().getUtilisateur().getIdPersonne() != utilisateurModel.getObject()
                .getIdPersonne()
                && personneService.updateOrDeletePersonneEnabled(utilisateurModel.getObject(), getSession()
                        .getUtilisateur()));
        formView.add(updateLink);

        // Action : retour à la page précédente
        formView.add(new Link<Void>(getResource() + ".Personne.Back") {
            @Override
            public void onClick() {
                redirect();
            }
        });

        // Action : suppression de la personne
        Button deleteButton = new SubmittableButton(ACTION_DELETE, ManageUtilisateurPage.class,
                new SubmittableButtonEvents() {
                    @Override
                    public void onProcess() throws DataConstraintException {
                        personneService.deletePersonne(utilisateurModel.getObject());
                    }

                    @Override
                    public void onSuccess() {
                        successNextPage(ManageUtilisateurPage.class, ACTION_DELETE);
                        redirect();
                    }
                });
        deleteButton.setVisibilityAllowed(personneService.updateOrDeletePersonneEnabled(utilisateurModel.getObject(),
                getSession().getUtilisateur()));
        deleteButton.add(new JSConfirmationBehavior(getStringModel("Confirm")));
        formView.add(deleteButton);
        add(formView);
    }

    /**
     * Redirection vers une autre page
     */
    private void redirect() {
        // On passe la personne associée à cette page, en paramètre de la prochaine page, pour lui permettre de
        // l'exploiter si besoin
        callerPage.addPageParameter(Personne.class.getSimpleName(), utilisateurModel.getObject());
        callerPage.responsePage(this);
    }
}
