/*
 * #%L
 * Cantharella :: Web
 * $Id: ReadStationPage.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.web/src/main/java/nc/ird/cantharella/web/pages/domain/station/ReadStationPage.java $
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
package nc.ird.cantharella.web.pages.domain.station;

import nc.ird.cantharella.data.config.DataContext;
import nc.ird.cantharella.data.exceptions.DataConstraintException;
import nc.ird.cantharella.data.model.Personne;
import nc.ird.cantharella.data.model.Station;
import nc.ird.cantharella.service.services.StationService;
import nc.ird.cantharella.web.config.WebContext;
import nc.ird.cantharella.web.pages.TemplatePage;
import nc.ird.cantharella.web.pages.domain.document.panel.ReadListDocumentsPanel;
import nc.ird.cantharella.web.pages.domain.personne.ReadPersonnePage;
import nc.ird.cantharella.web.utils.CallerPage;
import nc.ird.cantharella.web.utils.behaviors.JSConfirmationBehavior;
import nc.ird.cantharella.web.utils.behaviors.ReplaceEmptyLabelBehavior;
import nc.ird.cantharella.web.utils.forms.SubmittableButton;
import nc.ird.cantharella.web.utils.forms.SubmittableButtonEvents;
import nc.ird.cantharella.web.utils.models.DisplayMapValuePropertyModel;
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
 * Consultation d'une station
 * 
 * @author Alban Diguer
 */
public final class ReadStationPage extends TemplatePage {

    /** Logger */
    //private static final Logger LOG = LoggerFactory.getLogger(ReadStationPage.class);
    /** Action : delete */
    public static final String ACTION_DELETE = "Delete";

    /** Page appelante */
    private final CallerPage callerPage;

    /** Modèle : campagne */
    private final IModel<Station> stationModel;

    /** Service : station */
    @SpringBean
    private StationService stationService;

    /**
     * Constructeur
     * 
     * @param idStation ID station
     * @param callerPage Page appelante
     */
    public ReadStationPage(Integer idStation, CallerPage callerPage) {
        super(ReadStationPage.class);
        final CallerPage currentPage = new CallerPage((TemplatePage) getPage());
        this.callerPage = callerPage;

        // Initialisation des modèles
        stationModel = new GenericLoadableDetachableModel<Station>(Station.class, idStation);

        final Station station = stationModel.getObject();

        // Mapping des champs du modèle
        add(new Label("Station.nom", new PropertyModel<String>(stationModel, "nom"))
                .add(new ReplaceEmptyLabelBehavior()));
        add(new Label("Station.codePays", new DisplayMapValuePropertyModel<String>(stationModel, "codePays",
                WebContext.COUNTRIES.get(getSession().getLocale()))).add(new ReplaceEmptyLabelBehavior()));
        add(new Label("Station.localite", new PropertyModel<String>(stationModel, "localite"))
                .add(new ReplaceEmptyLabelBehavior()));
        add(new PropertyLabelLinkPanel<Personne>("Station.createur", new PropertyModel<Personne>(stationModel,
                "createur"), getStringModel("Read")) {
            @Override
            public void onClick() {
                setResponsePage(new ReadPersonnePage(getModelObject().getIdPersonne(), currentPage));
            }
        });
        add(new MultiLineLabel("Station.complement", new PropertyModel<String>(stationModel, "complement"))
                .add(new ReplaceEmptyLabelBehavior()));
        add(new Label("Station.latitude", new PropertyModel<String>(stationModel, "latitude"))
                .add(new ReplaceEmptyLabelBehavior()));
        add(new Label("Station.longitude", new PropertyModel<String>(stationModel, "longitude"))
                .add(new ReplaceEmptyLabelBehavior()));
        add(new Label("Station.referentiel", new DisplayMapValuePropertyModel<Integer>(stationModel, "referentiel",
                DataContext.REFERENTIELS)).add(new ReplaceEmptyLabelBehavior()));

        // add list document panel
        ReadListDocumentsPanel readListDocumentsPanel = new ReadListDocumentsPanel("ReadListDocumentsPanel",
                stationModel, currentPage);
        add(readListDocumentsPanel);

        // Ajout du formulaire pour les actions
        Form<Void> formView = new Form<Void>("Form");

        // Action : mise à jour (redirection vers le formulaire)
        Link<Station> updateLink = new Link<Station>(getResource() + ".Station.Update", new Model<Station>(station)) {
            @Override
            public void onClick() {
                setResponsePage(new ManageStationPage(getModelObject().getIdStation(), currentPage));
            }
        };
        updateLink.setVisibilityAllowed(stationService.updateOrdeleteStationEnabled(station, getSession()
                .getUtilisateur()));
        formView.add(updateLink);

        // Action : retour à la page précédente
        formView.add(new Link<Void>(getResource() + ".Station.Back") {
            @Override
            public void onClick() {
                redirect();
            }
        });

        // Action : suppression de la station
        Button deleteButton = new SubmittableButton(ACTION_DELETE, ManageStationPage.class,
                new SubmittableButtonEvents() {

                    @Override
                    public void onProcess() throws DataConstraintException {
                        stationService.deleteStation(station);
                    }

                    @Override
                    public void onSuccess() {
                        successNextPage(ManageStationPage.class, ACTION_DELETE);
                        redirect();
                    }
                });
        deleteButton.setVisibilityAllowed(stationService.updateOrdeleteStationEnabled(station, getSession()
                .getUtilisateur()));
        deleteButton.add(new JSConfirmationBehavior(getStringModel("Confirm")));
        formView.add(deleteButton);
        add(formView);
    }

    /**
     * Redirection vers une autre page
     */
    private void redirect() {
        callerPage.responsePage(this);
    }

}
