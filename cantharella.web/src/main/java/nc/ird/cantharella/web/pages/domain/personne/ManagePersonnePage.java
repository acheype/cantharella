/*
 * #%L
 * Cantharella :: Web
 * $Id: ManagePersonnePage.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.web/src/main/java/nc/ird/cantharella/web/pages/domain/personne/ManagePersonnePage.java $
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
package nc.ird.cantharella.web.pages.domain.personne;

import nc.ird.cantharella.data.exceptions.DataConstraintException;
import nc.ird.cantharella.data.exceptions.DataNotFoundException;
import nc.ird.cantharella.data.exceptions.UnexpectedException;
import nc.ird.cantharella.data.model.Personne;
import nc.ird.cantharella.data.model.Utilisateur.TypeDroit;
import nc.ird.cantharella.service.exceptions.EmailException;
import nc.ird.cantharella.service.services.PersonneService;
import nc.ird.cantharella.web.pages.TemplatePage;
import nc.ird.cantharella.web.pages.domain.document.panel.ManageListDocumentsPanel;
import nc.ird.cantharella.web.pages.domain.personne.panels.ManagePersonnePanel;
import nc.ird.cantharella.web.utils.CallerPage;
import nc.ird.cantharella.web.utils.behaviors.JSConfirmationBehavior;
import nc.ird.cantharella.web.utils.forms.SubmittableButton;
import nc.ird.cantharella.web.utils.forms.SubmittableButtonEvents;
import nc.ird.cantharella.web.utils.security.AuthRole;
import nc.ird.cantharella.web.utils.security.AuthRoles;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * Gestion d'une personne par un admin
 * 
 * @author Mickael Tricot
 * @author Adrien Cheype
 */
@AuthRoles({ AuthRole.ADMIN, AuthRole.USER })
public final class ManagePersonnePage extends TemplatePage {

    /** Action : create */
    private static final String ACTION_CREATE = "Create";

    /** Action : create utilisateur */
    private static final String ACTION_CREATE_UTILISATEUR = "CreateUtilisateur";

    /** Action : delete */
    private static final String ACTION_DELETE = "Delete";

    /** Action : update */
    private static final String ACTION_UPDATE = "Update";

    /** Logger */
    private static final Logger LOG = LoggerFactory.getLogger(ManagePersonnePage.class);

    /** Page appelante */
    private final CallerPage callerPage;

    /** Saisie multiple */
    private boolean multipleEntry;

    /** Modèle : personne */
    private final IModel<Personne> personneModel;

    /** Service : personne */
    @SpringBean
    private PersonneService personneService;

    /**
     * Constructeur (mode création)
     * 
     * @param callerPage Page appelante
     * @param multipleEntry Saisie multiple
     */
    public ManagePersonnePage(CallerPage callerPage, boolean multipleEntry) {
        this(null, null, callerPage, multipleEntry);
    }

    /**
     * Constructeur (mode édition)
     * 
     * @param idPersonne ID lot
     * @param callerPage Page appelante
     */
    public ManagePersonnePage(Integer idPersonne, CallerPage callerPage) {
        this(idPersonne, null, callerPage, false);
    }

    /**
     * Constructeur (mode saisie de la personne suivante)
     * 
     * @param personne Personne
     * @param callerPage Page appelante
     */
    public ManagePersonnePage(Personne personne, CallerPage callerPage) {
        this(null, personne, callerPage, true);
    }

    /**
     * Constructeur. Si idPersonne et personne sont null, on créée une nouveau personne. Si idPersonne est renseigné, on
     * édite la personne correspondante. Si personne est renseignée, on créée une nouvelle personne à partir des
     * informations qu'elle contient.
     * 
     * @param idPersonne ID personne
     * @param personne Personne
     * @param callerPage Page appelante
     * @param multipleEntry Saisie multiple
     */
    private ManagePersonnePage(Integer idPersonne, Personne personne, final CallerPage callerPage, boolean multipleEntry) {
        super(ManagePersonnePage.class);
        assert idPersonne == null || personne == null;
        this.callerPage = callerPage;
        final CallerPage currentPage = new CallerPage(this);
        this.multipleEntry = multipleEntry;

        // Initialisation du modèle
        try {
            personneModel = new Model<Personne>(idPersonne == null && personne == null ? new Personne()
                    : personne != null ? personne : personneService.loadPersonne(idPersonne));
        } catch (DataNotFoundException e) {
            LOG.error(e.getMessage(), e);
            throw new UnexpectedException(e);
        }

        boolean createMode = idPersonne == null;
        final Form<Personne> formView = new Form<Personne>("Form", personneModel);

        final ManagePersonnePanel personnePanel = new ManagePersonnePanel("ManagePersonnePanel", personneModel);
        formView.add(personnePanel);

        // add list document panel
        ManageListDocumentsPanel manageListDocumentsPanel = new ManageListDocumentsPanel("ManageListDocumentsPanel",
                personneModel, currentPage);
        manageListDocumentsPanel.setUpdateOrDeleteEnabled(createMode
                || getSession().getUtilisateur().getTypeDroit() == TypeDroit.ADMINISTRATEUR);
        formView.add(manageListDocumentsPanel);

        // Action : création de la personne
        Button createButton = new SubmittableButton(ACTION_CREATE, new SubmittableButtonEvents() {
            @Override
            public void onProcess() throws DataConstraintException {
                personneService.createPersonne(personneModel.getObject());
            }

            @Override
            public void onSuccess() {
                successNextPage(ACTION_CREATE);
                redirect();
            }

            @Override
            public void onValidate() {
                personnePanel.validate();
            }
        });
        createButton.setVisibilityAllowed(createMode);
        formView.add(createButton);

        // Action : mise à jour de la personne
        Button updateButton = new SubmittableButton(ACTION_UPDATE, new SubmittableButtonEvents() {
            @Override
            public void onProcess() throws DataConstraintException {
                personneService.updatePersonne(personneModel.getObject());
            }

            @Override
            public void onSuccess() {
                successNextPage(ACTION_UPDATE);
                redirect();
            }

            @Override
            public void onValidate() {
                personnePanel.validate();
            }
        });
        updateButton.setVisibilityAllowed(!createMode);
        formView.add(updateButton);

        // Action : transformation de la personne en utilisateur
        Button createUtilisateurButton = new SubmittableButton(ACTION_CREATE_UTILISATEUR,
                new SubmittableButtonEvents() {
                    @Override
                    public void onProcess() throws EmailException, DataConstraintException {
                        personneService.updateAndCreateUtilisateur(personneModel.getObject());
                    }

                    @Override
                    public void onSuccess() {
                        successNextPage(ACTION_CREATE_UTILISATEUR);
                        // redirect all the time to ReadUtilisateur
                    }

                    @Override
                    public void onValidate() {
                        personnePanel.validate();
                    }
                });
        createUtilisateurButton.setVisibilityAllowed(!createMode
                && getSession().getUtilisateur().getTypeDroit() == TypeDroit.ADMINISTRATEUR);
        formView.add(createUtilisateurButton);

        // Action : suppression de la personne
        Button deleteButton = new SubmittableButton(ACTION_DELETE, new SubmittableButtonEvents() {
            @Override
            public void onProcess() throws DataConstraintException {
                personneService.deletePersonne(personneModel.getObject());
            }

            @Override
            public void onSuccess() {
                successNextPage(ACTION_DELETE);
                redirect();
            }
        });
        deleteButton.setVisibilityAllowed(!createMode);
        deleteButton.setDefaultFormProcessing(false);
        deleteButton.add(new JSConfirmationBehavior(getStringModel("Confirm")));
        formView.add(deleteButton);

        formView.add(new Link<Void>("Cancel") {
            // Cas où le formulaire est annulé
            @Override
            public void onClick() {
                if (callerPage != null) {
                    callerPage.responsePage((TemplatePage) this.getPage());
                }
            }
        });

        add(formView);
    }

    /**
     * Redirection vers une autre page. Cas où le formulaire est validé
     */
    private void redirect() {
        if (multipleEntry) {
            // Redirection vers l'écran de saisie d'un nouveau lot, en fournissant déjà quelques données
            Personne nextPersonne = new Personne();
            nextPersonne.setOrganisme(personneModel.getObject().getOrganisme());
            nextPersonne.setAdressePostale(personneModel.getObject().getAdressePostale());
            nextPersonne.setCodePostal(personneModel.getObject().getCodePostal());
            nextPersonne.setVille(personneModel.getObject().getVille());
            nextPersonne.setCodePays(personneModel.getObject().getCodePays());
            setResponsePage(new ManagePersonnePage(nextPersonne, callerPage));
        } else if (callerPage != null) {
            // On passe l'id de la personne associée à cette page, en paramètre de la prochaine page, pour lui permettre
            // de
            // l'exploiter si besoin
            callerPage.addPageParameter(Personne.class.getSimpleName(), personneModel.getObject().getIdPersonne());
            callerPage.responsePage(this);
        }
    }
}
