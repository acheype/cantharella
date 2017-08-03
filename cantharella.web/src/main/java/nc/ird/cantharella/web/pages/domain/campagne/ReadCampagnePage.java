/*
 * #%L
 * Cantharella :: Web
 * $Id: ReadCampagnePage.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.web/src/main/java/nc/ird/cantharella/web/pages/domain/campagne/ReadCampagnePage.java $
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
package nc.ird.cantharella.web.pages.domain.campagne;

import java.util.ArrayList;
import java.util.List;

import nc.ird.cantharella.data.exceptions.DataConstraintException;
import nc.ird.cantharella.data.model.Campagne;
import nc.ird.cantharella.data.model.CampagnePersonneParticipant;
import nc.ird.cantharella.data.model.Personne;
import nc.ird.cantharella.data.model.Station;
import nc.ird.cantharella.service.services.CampagneService;
import nc.ird.cantharella.service.services.StationService;
import nc.ird.cantharella.web.config.WebContext;
import nc.ird.cantharella.web.pages.TemplatePage;
import nc.ird.cantharella.web.pages.domain.document.panel.ReadListDocumentsPanel;
import nc.ird.cantharella.web.pages.domain.personne.ReadPersonnePage;
import nc.ird.cantharella.web.pages.domain.station.ReadStationPage;
import nc.ird.cantharella.web.utils.CallerPage;
import nc.ird.cantharella.web.utils.behaviors.JSConfirmationBehavior;
import nc.ird.cantharella.web.utils.behaviors.ReplaceEmptyLabelBehavior;
import nc.ird.cantharella.web.utils.forms.SubmittableButton;
import nc.ird.cantharella.web.utils.forms.SubmittableButtonEvents;
import nc.ird.cantharella.web.utils.models.DisplayMapValuePropertyModel;
import nc.ird.cantharella.web.utils.models.GenericLoadableDetachableModel;
import nc.ird.cantharella.web.utils.panels.PropertyLabelLinkPanel;
import nc.ird.cantharella.web.utils.panels.SimpleTooltipPanel;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * Consultation d'une campagne
 * 
 * @author Alban Diguer
 */
public final class ReadCampagnePage extends TemplatePage {

    /** Logger */
    //private static final Logger LOG = LoggerFactory.getLogger(ReadCampagnePage.class);
    /** Action : delete */
    public static final String ACTION_DELETE = "Delete";

    /** Page appelante */
    private final CallerPage callerPage;

    /** Modèle : campagne */
    private final IModel<Campagne> campagneModel;

    /** Service : campagne */
    @SpringBean
    private CampagneService campagneService;

    /** Service : station */
    @SpringBean
    private StationService stationService;

    /**
     * Constructeur
     * 
     * @param idCampagne ID campagne
     * @param callerPage Page appelante
     */
    public ReadCampagnePage(Integer idCampagne, final CallerPage callerPage) {

        super(ReadCampagnePage.class);
        final CallerPage currentPage = new CallerPage((TemplatePage) getPage());
        this.callerPage = callerPage;

        // Initialisation des modèles
        campagneModel = new GenericLoadableDetachableModel<Campagne>(Campagne.class, idCampagne);

        final Campagne campagne = campagneModel.getObject();

        // Mapping des champs du modèle
        add(new Label("Campagne.nom", new PropertyModel<String>(campagneModel, "nom"))
                .add(new ReplaceEmptyLabelBehavior()));

        // Affichage du pays
        add(new Label("Campagne.codePays", new DisplayMapValuePropertyModel<String>(campagneModel, "codePays",
                WebContext.COUNTRIES.get(getSession().getLocale()))).add(new ReplaceEmptyLabelBehavior()));
        add(new Label("Campagne.dateDeb", new PropertyModel<String>(campagneModel, "dateDeb"))
                .add(new ReplaceEmptyLabelBehavior()));
        add(new Label("Campagne.dateFin", new PropertyModel<String>(campagneModel, "dateFin"))
                .add(new ReplaceEmptyLabelBehavior()));
        add(new Label("Campagne.programme", new PropertyModel<String>(campagneModel, "programme"))
                .add(new ReplaceEmptyLabelBehavior()));
        add(new MultiLineLabel("Campagne.mentionLegale", new PropertyModel<String>(campagneModel, "mentionLegale"))
                .add(new ReplaceEmptyLabelBehavior()));
        add(new SimpleTooltipPanel("Campagne.mentionLegale.info", getStringModel("Campagne.mentionLegale.info2")));
        add(new MultiLineLabel("Campagne.complement", new PropertyModel<String>(campagneModel, "complement"))
                .add(new ReplaceEmptyLabelBehavior()));

        add(new PropertyLabelLinkPanel<Personne>("Campagne.createur", new PropertyModel<Personne>(campagneModel,
                "createur"), getStringModel("Read")) {
            @Override
            public void onClick() {
                setResponsePage(new ReadPersonnePage(getModelObject().getIdPersonne(), currentPage));
            }
        });

        initParticipantsFields(currentPage);
        initStationsFields(currentPage);

        // add list document panel
        ReadListDocumentsPanel readListDocumentsPanel = new ReadListDocumentsPanel("ReadListDocumentsPanel",
                campagneModel, currentPage);
        add(readListDocumentsPanel);

        // Ajout du formulaire pour les actions
        final Form<Void> formView = new Form<Void>("Form");

        // Action : mise à jour (redirection vers le formulaire)
        Link<Campagne> updateLink = new Link<Campagne>(getResource() + ".Campagne.Update",
                new Model<Campagne>(campagne)) {
            @Override
            public void onClick() {
                setResponsePage(new ManageCampagnePage(getModelObject().getIdCampagne(), currentPage));
            }
        };
        updateLink.setVisibilityAllowed(campagneService.updateOrdeleteCampagneEnabled(campagne, getSession()
                .getUtilisateur()));
        formView.add(updateLink);

        // Action : retour à la page précédente
        formView.add(new Link<Void>(getResource() + ".Campagne.Back") {
            @Override
            public void onClick() {
                redirect();
            }
        });

        // Action : suppression de la campagne
        Button deleteButton = new SubmittableButton(ACTION_DELETE, ManageCampagnePage.class,
                new SubmittableButtonEvents() {
                    @Override
                    public void onProcess() throws DataConstraintException {
                        campagneService.deleteCampagne(campagne);
                    }

                    @Override
                    public void onSuccess() {
                        successNextPage(ManageCampagnePage.class, ACTION_DELETE);
                        redirect();
                    }
                });
        deleteButton.setVisibilityAllowed(campagneService.updateOrdeleteCampagneEnabled(campagne, getSession()
                .getUtilisateur()));
        deleteButton.add(new JSConfirmationBehavior(getStringModel("Confirm")));
        formView.add(deleteButton);
        add(formView);
    }

    /**
     * Initialize participants section
     * 
     * @param currentPage The current page
     */
    private void initParticipantsFields(final CallerPage currentPage) {
        ListView<CampagnePersonneParticipant> campagneParticipants = new ListView<CampagnePersonneParticipant>(
                "Campagne.participants.List", new PropertyModel<List<CampagnePersonneParticipant>>(campagneModel,
                        "participants")) {
            @Override
            protected void populateItem(ListItem<CampagnePersonneParticipant> item) {
                if (item.getIndex() % 2 == 1) {
                    item.add(new AttributeModifier("class", item.getIndex() % 2 == 0 ? "even" : "odd"));
                }

                CampagnePersonneParticipant participant = item.getModelObject();
                // affichage + lien vers la fiche
                item.add(new PropertyLabelLinkPanel<Personne>("Campagne.participants.List.personne",
                        new Model<Personne>(participant.getId().getPk2()), getStringModel("Read")) {
                    @Override
                    public void onClick() {
                        setResponsePage(new ReadPersonnePage(getModelObject().getIdPersonne(), currentPage));
                    }
                });
                item.add(new Label("Campagne.participants.List.complement", new PropertyModel<String>(participant,
                        "complement")));
            }
        };

        // Selon l'existence d'elements dans la liste on affiche la table
        MarkupContainer tableParticipants = new WebMarkupContainer("Campagne.participants.Table") {
            @Override
            public boolean isVisible() {
                return campagneModel.getObject().getParticipants().size() != 0;
            }
        };
        tableParticipants.add(campagneParticipants);
        add(tableParticipants);

        // Selon la non existence d'elements dans la liste on affiche le span
        MarkupContainer noTableParticipants = new WebMarkupContainer("Campagne.participants.NoTable") {
            @Override
            public boolean isVisible() {
                return campagneModel.getObject().getParticipants().size() == 0;
            }
        };
        add(noTableParticipants);
    }

    /**
     * Initialize stations section
     * 
     * @param currentPage The current page
     */
    private void initStationsFields(final CallerPage currentPage) {

        // Selon l'existence d'elements dans la liste on affiche la table
        MarkupContainer tableStations = new WebMarkupContainer("Campagne.stations.Table") {
            @Override
            public boolean isVisible() {
                return !campagneModel.getObject().getStations().isEmpty();
            }
        };
        add(tableStations);

        final WebMarkupContainer stationsNotAccessiblesCont = new WebMarkupContainer(
                "Campagne.stations.stationsNotAccessibles");
        stationsNotAccessiblesCont.setOutputMarkupPlaceholderTag(true);
        tableStations.add(stationsNotAccessiblesCont);

        // Model de liste des stations
        final LoadableDetachableModel<List<Station>> listStationsModel = new LoadableDetachableModel<List<Station>>() {
            @Override
            protected List<Station> load() {
                boolean isOneResultNotAccessible = false;
                final List<Station> accessiblesStations = stationService.listStations(getSession().getUtilisateur());

                List<Station> listStations = new ArrayList<Station>();

                for (Station st : campagneModel.getObject().getSortedStations()) {

                    if (accessiblesStations.contains(st)) {
                        listStations.add(st);
                    } else {
                        isOneResultNotAccessible = true;
                    }
                }
                // si une des stations non accessibles, on rend visible le message d'avertissement
                stationsNotAccessiblesCont.setVisibilityAllowed(isOneResultNotAccessible);
                return listStations;
            }
        };

        ListView<Station> stationsListView = new ListView<Station>("Campagne.stations.List", listStationsModel) {
            @Override
            protected void populateItem(ListItem<Station> item) {
                if (item.getIndex() % 2 == 1) {
                    item.add(new AttributeModifier("class", item.getIndex() % 2 == 0 ? "even" : "odd"));
                }

                IModel<Station> stationModel = item.getModel();
                // affichage + lien vers la fiche
                item.add(new PropertyLabelLinkPanel<Station>("Campagne.stations.List.station", item.getModel(),
                        getStringModel("Read")) {
                    @Override
                    public void onClick() {
                        setResponsePage(new ReadStationPage(getModelObject().getIdStation(), currentPage));
                    }
                });

                item.add(new Label("Campagne.stations.List.codePays", new DisplayMapValuePropertyModel<String>(
                        stationModel, "codePays", WebContext.COUNTRIES.get(getSession().getLocale())))
                        .add(new ReplaceEmptyLabelBehavior()));
                item.add(new Label("Campagne.stations.List.localite", new PropertyModel<String>(stationModel,
                        "localite")).add(new ReplaceEmptyLabelBehavior()));
                item.add(new Label("Campagne.stations.List.latitude", new PropertyModel<String>(stationModel,
                        "latitude")).add(new ReplaceEmptyLabelBehavior()));
                item.add(new Label("Campagne.stations.List.longitude", new PropertyModel<String>(stationModel,
                        "longitude")).add(new ReplaceEmptyLabelBehavior()));
            }
        };
        tableStations.add(stationsListView);

        // Selon la non existence d'elements dans la liste on affiche le span
        MarkupContainer noTableStations = new WebMarkupContainer("Campagne.stations.NoTable") {
            @Override
            public boolean isVisible() {
                return campagneModel.getObject().getStations().isEmpty();
            }
        };
        add(noTableStations);
    }

    /**
     * Redirection vers une autre page
     */
    private void redirect() {
        callerPage.responsePage(this);
    }

}
