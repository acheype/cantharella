/*
 * #%L
 * Cantharella :: Web
 * $Id: ReadPersonnePage.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.web/src/main/java/nc/ird/cantharella/web/pages/domain/personne/ReadPersonnePage.java $
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
import nc.ird.cantharella.data.model.Personne;
import nc.ird.cantharella.service.services.PersonneService;
import nc.ird.cantharella.web.pages.TemplatePage;
import nc.ird.cantharella.web.pages.domain.document.panel.ReadListDocumentsPanel;
import nc.ird.cantharella.web.pages.domain.personne.panels.ReadPersonnePanel;
import nc.ird.cantharella.web.utils.CallerPage;
import nc.ird.cantharella.web.utils.behaviors.JSConfirmationBehavior;
import nc.ird.cantharella.web.utils.forms.SubmittableButton;
import nc.ird.cantharella.web.utils.forms.SubmittableButtonEvents;
import nc.ird.cantharella.web.utils.models.GenericLoadableDetachableModel;

import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * Consultation d'une personne
 * 
 * @author Alban Diguer
 */
public final class ReadPersonnePage extends TemplatePage {

    /** Action : delete */
    public static final String ACTION_DELETE = "Delete";

    /** Logger */
    //private static final Logger LOG = LoggerFactory.getLogger(ReadPersonnePage.class);
    /** Page appelante */
    private final CallerPage callerPage;

    /** Modèle : personne */
    private final IModel<Personne> personneModel;

    /** Service : personne */
    @SpringBean
    private PersonneService personneService;

    /**
     * Constructeur
     * 
     * @param idPersonne ID personne
     * @param callerPage Page appelante
     */
    public ReadPersonnePage(Integer idPersonne, CallerPage callerPage) {
        super(ReadPersonnePage.class);

        final CallerPage currentPage = new CallerPage((TemplatePage) getPage());
        this.callerPage = callerPage;

        // Initialisation du modèle
        personneModel = new GenericLoadableDetachableModel<Personne>(Personne.class, idPersonne);

        final Personne personne = personneModel.getObject();

        // Ajout du formulaire pour les actions
        final Form<Personne> formView = new Form<Personne>("Form", personneModel);

        // Ajout du panel ReadPersonnePanel
        final ReadPersonnePanel personnePanel = new ReadPersonnePanel("ReadPersonnePanel", personneModel);
        add(personnePanel);

        // add list document panel
        ReadListDocumentsPanel readListDocumentsPanel = new ReadListDocumentsPanel("ReadListDocumentsPanel",
                personneModel, currentPage);
        add(readListDocumentsPanel);

        // Action : mise à jour (redirection vers le formulaire)
        Link<Personne> updateLink = new Link<Personne>(getResource() + ".Personne.Update",
                new Model<Personne>(personne)) {
            @Override
            public void onClick() {
                setResponsePage(new ManagePersonnePage(getModelObject().getIdPersonne(), currentPage));
            }
        };
        updateLink.setVisibilityAllowed(personneService.updateOrDeletePersonneEnabled(personne, getSession()
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
        Button deleteButton = new SubmittableButton(ACTION_DELETE, ManagePersonnePage.class,
                new SubmittableButtonEvents() {
                    @Override
                    public void onProcess() throws DataConstraintException {
                        personneService.deletePersonne(personne);
                    }

                    @Override
                    public void onSuccess() {
                        successNextPage(ManagePersonnePage.class, ACTION_DELETE);
                        redirect();
                    }
                });
        deleteButton.setVisibilityAllowed(personneService.updateOrDeletePersonneEnabled(personne, getSession()
                .getUtilisateur()));
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
        callerPage.addPageParameter(Personne.class.getSimpleName(), personneModel.getObject());
        callerPage.responsePage(this);
    }
}
