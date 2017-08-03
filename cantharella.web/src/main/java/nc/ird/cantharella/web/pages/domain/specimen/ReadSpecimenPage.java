/*
 * #%L
 * Cantharella :: Web
 * $Id: ReadSpecimenPage.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.web/src/main/java/nc/ird/cantharella/web/pages/domain/specimen/ReadSpecimenPage.java $
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
package nc.ird.cantharella.web.pages.domain.specimen;

import java.util.Date;

import nc.ird.cantharella.data.exceptions.DataConstraintException;
import nc.ird.cantharella.data.model.Personne;
import nc.ird.cantharella.data.model.Specimen;
import nc.ird.cantharella.data.model.Station;
import nc.ird.cantharella.service.services.SpecimenService;
import nc.ird.cantharella.web.pages.TemplatePage;
import nc.ird.cantharella.web.pages.domain.document.panel.ReadListDocumentsPanel;
import nc.ird.cantharella.web.pages.domain.personne.ReadPersonnePage;
import nc.ird.cantharella.web.pages.domain.station.ReadStationPage;
import nc.ird.cantharella.web.utils.CallerPage;
import nc.ird.cantharella.web.utils.behaviors.JSConfirmationBehavior;
import nc.ird.cantharella.web.utils.behaviors.ReplaceEmptyLabelBehavior;
import nc.ird.cantharella.web.utils.forms.SubmittableButton;
import nc.ird.cantharella.web.utils.forms.SubmittableButtonEvents;
import nc.ird.cantharella.web.utils.models.DisplayEnumPropertyModel;
import nc.ird.cantharella.web.utils.models.GenericLoadableDetachableModel;
import nc.ird.cantharella.web.utils.panels.PropertyLabelLinkPanel;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * Consultation d'un spécimen
 * 
 * @author Alban Diguer
 */
public final class ReadSpecimenPage extends TemplatePage {

    /** Action : delete */
    public static final String ACTION_DELETE = "Delete";

    /** Logger */
    //private static final Logger LOG = LoggerFactory.getLogger(ReadSpecimenPage.class);
    /** Modèle : specimen */
    private final IModel<Specimen> specimenModel;

    /** Service : specimen */
    @SpringBean
    private SpecimenService specimenService;

    /** Page appelante */
    private final CallerPage callerPage;

    /**
     * Constructeur
     * 
     * @param idSpecimen identifiant du spécimen
     * @param callerPage Page appelante
     */
    public ReadSpecimenPage(Integer idSpecimen, final CallerPage callerPage) {
        super(ReadSpecimenPage.class);
        final CallerPage currentPage = new CallerPage((TemplatePage) getPage());
        this.callerPage = callerPage;

        // Initialisation du modèle
        specimenModel = new GenericLoadableDetachableModel<Specimen>(Specimen.class, idSpecimen);

        boolean createMode = idSpecimen == null;
        if (createMode) {
            specimenModel.getObject().setCreateur(getSession().getUtilisateur());
        }
        final Specimen specimen = specimenModel.getObject();

        // Mapping
        add(new Label("Specimen.ref", new PropertyModel<String>(specimenModel, "ref"))
                .add(new ReplaceEmptyLabelBehavior()));
        add(new Label("Specimen.embranchement", new PropertyModel<String>(specimenModel, "embranchement"))
                .add(new ReplaceEmptyLabelBehavior()));
        add(new Label("Specimen.famille", new PropertyModel<String>(specimenModel, "famille"))
                .add(new ReplaceEmptyLabelBehavior()));
        add(new Label("Specimen.genre", new PropertyModel<String>(specimenModel, "genre"))
                .add(new ReplaceEmptyLabelBehavior()));
        add(new Label("Specimen.espece", new PropertyModel<String>(specimenModel, "espece"))
                .add(new ReplaceEmptyLabelBehavior()));
        add(new Label("Specimen.sousEspece", new PropertyModel<String>(specimenModel, "sousEspece"))
                .add(new ReplaceEmptyLabelBehavior()));
        add(new Label("Specimen.variete", new PropertyModel<String>(specimenModel, "variete"))
                .add(new ReplaceEmptyLabelBehavior()));
        add(new Label("Specimen.typeOrganisme", new DisplayEnumPropertyModel(specimenModel, "typeOrganisme", this)));

        add(new PropertyLabelLinkPanel<Personne>("Specimen.identificateur", new PropertyModel<Personne>(specimenModel,
                "identificateur"), getStringModel("Read")) {
            @Override
            public void onClick() {
                setResponsePage(new ReadPersonnePage(getModelObject().getIdPersonne(), currentPage));
            }
        });

        add(new PropertyLabelLinkPanel<Station>("Specimen.station",
                new PropertyModel<Station>(specimenModel, "station"), getStringModel("Read")) {
            @Override
            public void onClick() {
                setResponsePage(new ReadStationPage(getModelObject().getIdStation(), currentPage));
            }
        }.add(new ReplaceEmptyLabelBehavior()));

        add(new MultiLineLabel("Specimen.complement", new PropertyModel<String>(specimenModel, "complement"))
                .add(new ReplaceEmptyLabelBehavior()));

        add(new PropertyLabelLinkPanel<Personne>("Specimen.createur", new PropertyModel<Personne>(specimenModel,
                "createur"), getStringModel("Read")) {
            @Override
            public void onClick() {
                setResponsePage(new ReadPersonnePage(getModelObject().getIdPersonne(), currentPage));
            }
        });

        // champs dépôt
        add(new Label("Specimen.numDepot", new PropertyModel<String>(specimenModel, "numDepot"))
                .add(new ReplaceEmptyLabelBehavior()));
        add(new Label("Specimen.dateDepot", new PropertyModel<Date>(specimenModel, "dateDepot"))
                .add(new ReplaceEmptyLabelBehavior()));
        add(new Label("Specimen.lieuDepot", new PropertyModel<String>(specimenModel, "lieuDepot"))
                .add(new ReplaceEmptyLabelBehavior()));

        // add list document panel
        ReadListDocumentsPanel readListDocumentsPanel = new ReadListDocumentsPanel("ReadListDocumentsPanel",
                specimenModel, currentPage);
        add(readListDocumentsPanel);

        final Form<Void> formView = new Form<Void>("Form");

        // Action : mise à jour
        Link<Specimen> updateLink = new Link<Specimen>(getResource() + ".Specimen.Update",
                new Model<Specimen>(specimen)) {
            @Override
            public void onClick() {
                setResponsePage(new ManageSpecimenPage(getModelObject().getIdSpecimen(), currentPage));
            }
        };
        updateLink.setVisibilityAllowed(specimenService.updateOrdeleteSpecimenEnabled(specimen, getSession()
                .getUtilisateur()));
        formView.add(updateLink);

        // Action : suppression du specimen
        Button deleteButton = new SubmittableButton(ACTION_DELETE, ManageSpecimenPage.class,
                new SubmittableButtonEvents() {
                    @Override
                    public void onProcess() throws DataConstraintException {
                        specimenService.deleteSpecimen(specimenModel.getObject());
                    }

                    @Override
                    public void onSuccess() {
                        successNextPage(ManageSpecimenPage.class, ACTION_DELETE);
                        redirect();
                    }
                });
        deleteButton.setVisibilityAllowed(!createMode
                && specimenService.updateOrdeleteSpecimenEnabled(specimenModel.getObject(), getSession()
                        .getUtilisateur()));
        deleteButton.add(new JSConfirmationBehavior(getStringModel("Confirm")));
        deleteButton.setDefaultFormProcessing(false);
        formView.add(deleteButton);

        // Action : retour
        formView.add(new Link<Void>(getResource() + ".Specimen.Back") {
            @Override
            public void onClick() {
                callerPage.responsePage((TemplatePage) getPage());
            }
        });

        add(formView);
    }

    /**
     * Redirection vers une autre page
     */
    private void redirect() {
        callerPage.responsePage(this);
    }
}
