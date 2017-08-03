/*
 * #%L
 * Cantharella :: Web
 * $Id: ManageStationPage.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.web/src/main/java/nc/ird/cantharella/web/pages/domain/station/ManageStationPage.java $
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

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import nc.ird.cantharella.data.config.DataContext;
import nc.ird.cantharella.data.exceptions.DataConstraintException;
import nc.ird.cantharella.data.exceptions.DataNotFoundException;
import nc.ird.cantharella.data.exceptions.UnexpectedException;
import nc.ird.cantharella.data.model.Station;
import nc.ird.cantharella.data.validation.utils.ModelValidator;
import nc.ird.cantharella.service.services.StationService;
import nc.ird.cantharella.web.config.WebContext;
import nc.ird.cantharella.web.pages.TemplatePage;
import nc.ird.cantharella.web.pages.domain.document.panel.ManageListDocumentsPanel;
import nc.ird.cantharella.web.pages.model.ManageStationModel;
import nc.ird.cantharella.web.utils.CallerPage;
import nc.ird.cantharella.web.utils.behaviors.JSConfirmationBehavior;
import nc.ird.cantharella.web.utils.forms.AutoCompleteTextFieldString;
import nc.ird.cantharella.web.utils.forms.AutoCompleteTextFieldString.ComparisonMode;
import nc.ird.cantharella.web.utils.forms.SubmittableButton;
import nc.ird.cantharella.web.utils.forms.SubmittableButtonEvents;
import nc.ird.cantharella.web.utils.renderers.MapChoiceRenderer;
import nc.ird.cantharella.web.utils.security.AuthRole;
import nc.ird.cantharella.web.utils.security.AuthRoles;
import nc.ird.cantharella.utils.CoordTools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * Gestion d'une station
 * 
 * @author Mickael Tricot
 * @author Adrien Cheype
 */
@AuthRoles({ AuthRole.ADMIN, AuthRole.USER })
public final class ManageStationPage extends TemplatePage {

    /** Action : create */
    private static final String ACTION_CREATE = "Create";

    /** Action : delete */
    private static final String ACTION_DELETE = "Delete";

    /** Action : update */
    private static final String ACTION_UPDATE = "Update";

    /** Logger */
    private static final Logger LOG = LoggerFactory.getLogger(ManageStationPage.class);

    /** Page appelante */
    private final CallerPage callerPage;

    /** Saisie multiple */
    private boolean multipleEntry;

    /** Modèle : manage station */
    private final IModel<ManageStationModel> manageStationModel;

    /** Modèle : station */
    private final IModel<Station> stationModel;

    /** Service : station */
    @SpringBean
    private StationService stationService;

    /** Validateur modèle */
    @SpringBean(name = "webModelValidator")
    private ModelValidator validator;

    /**
     * Constructor (mode création)
     * 
     * @param callerPage Page appelante
     * @param multipleEntry Saisie de stations multiples
     */
    public ManageStationPage(CallerPage callerPage, boolean multipleEntry) {
        this(null, null, callerPage, multipleEntry);
    }

    /**
     * Constructeur (mode édition)
     * 
     * @param idStation ID station
     * @param callerPage Page appelante
     */
    public ManageStationPage(Integer idStation, CallerPage callerPage) {
        this(idStation, null, callerPage, false);
    }

    /**
     * Constructeur (mode saisie de la station suivante)
     * 
     * @param station Station
     * @param callerPage Page appelante
     */
    public ManageStationPage(Station station, CallerPage callerPage) {
        this(null, station, callerPage, true);
    }

    /**
     * Constructeur. Si idStation et station sont null, on créée une nouvelle Station. Si idStation est renseigné, on
     * édite la station correspondante. Si station est renseigné, on créée une nouvelle station à partir des
     * informations qu'il contient.
     * 
     * @param idStation ID station
     * @param station Station
     * @param callerPage Page appelante
     * @param multipleEntry Saisie de stations multiples
     */
    private ManageStationPage(Integer idStation, Station station, final CallerPage callerPage,
            final boolean multipleEntry) {
        super(ManageStationPage.class);
        assert idStation == null || station == null;
        this.callerPage = callerPage;
        final CallerPage currentPage = new CallerPage(this);
        this.multipleEntry = multipleEntry;

        // Initialisation du modèle
        manageStationModel = new Model<ManageStationModel>(new ManageStationModel());
        try {
            stationModel = new Model<Station>(idStation == null && station == null ? new Station()
                    : station != null ? station : stationService.loadStation(idStation));
        } catch (DataNotFoundException e) {
            LOG.error(e.getMessage(), e);
            throw new UnexpectedException(e);
        }
        boolean createMode = idStation == null;
        if (createMode) {
            stationModel.getObject().setCreateur(getSession().getUtilisateur());
            if (station != null) {
                // cas où édition d'une station à partir de champs préremplies de l'ancienne station
                // on met à jour le modèle intermédiaire pour les coordonnées (pas les minutes de longitude et latitude
                // par contre)
                manageStationModel.getObject().setLatitudeDegrees(
                        CoordTools.latitudeDegrees(stationModel.getObject().getLatitude()));
                manageStationModel.getObject().setLatitudeOrientation(
                        CoordTools.latitudeOrientation(stationModel.getObject().getLatitude()));
                manageStationModel.getObject().setLongitudeDegrees(
                        CoordTools.longitudeDegrees(stationModel.getObject().getLongitude()));
                manageStationModel.getObject().setLongitudeOrientation(
                        CoordTools.longitudeOrientation(stationModel.getObject().getLongitude()));
            }
        } else {
            // mode édition, mise à jour du modèle intermédiaire pour les coordonnées
            manageStationModel.getObject().setLatitudeDegrees(
                    CoordTools.latitudeDegrees(stationModel.getObject().getLatitude()));
            manageStationModel.getObject().setLatitudeMinutes(
                    CoordTools.latitudeMinutes(stationModel.getObject().getLatitude()));
            manageStationModel.getObject().setLatitudeOrientation(
                    CoordTools.latitudeOrientation(stationModel.getObject().getLatitude()));
            manageStationModel.getObject().setLongitudeDegrees(
                    CoordTools.longitudeDegrees(stationModel.getObject().getLongitude()));
            manageStationModel.getObject().setLongitudeMinutes(
                    CoordTools.longitudeMinutes(stationModel.getObject().getLongitude()));
            manageStationModel.getObject().setLongitudeOrientation(
                    CoordTools.longitudeOrientation(stationModel.getObject().getLongitude()));
        }

        // Initialisation des listes
        List<String> localites = stationService.listStationLocalites();
        final Form<Void> formView = new Form<Void>("Form");
        formView.add(new TextField<String>("Station.nom", new PropertyModel<String>(stationModel, "nom")));
        formView.add(new DropDownChoice<String>("Station.codePays",
                new PropertyModel<String>(stationModel, "codePays"), WebContext.COUNTRY_CODES.get(getSession()
                        .getLocale()), new MapChoiceRenderer<String, String>(WebContext.COUNTRIES.get(getSession()
                        .getLocale()))));
        formView.add(new AutoCompleteTextFieldString("Station.localite", new PropertyModel<String>(stationModel,
                "localite"), localites, ComparisonMode.CONTAINS));
        formView.add(new TextArea<String>("Station.complement", new PropertyModel<String>(stationModel, "complement")));

        // Créateur en lecture seule
        formView.add(new TextField<String>("Station.createur", new PropertyModel<String>(stationModel, "createur"))
                .setEnabled(false));

        formView.add(new TextField<Integer>("ManageStationModel.latitudeDegrees", new PropertyModel<Integer>(
                manageStationModel, "latitudeDegrees")));
        formView.add(new Label("Coordonnate.degrees.latitude", String.valueOf(CoordTools.DEGREES)));
        formView.add(new TextField<BigDecimal>("ManageStationModel.latitudeMinutes", new PropertyModel<BigDecimal>(
                manageStationModel, "latitudeMinutes")));
        formView.add(new Label("Coordonnate.minutes.latitude", String.valueOf(CoordTools.MINUTES)));
        formView.add(new DropDownChoice<Character>("ManageStationModel.latitudeOrientation",
                new PropertyModel<Character>(manageStationModel, "latitudeOrientation"), Arrays
                        .asList(CoordTools.LATITUDE_ORIENTATIONS)));
        formView.add(new TextField<Integer>("ManageStationModel.longitudeDegrees", new PropertyModel<Integer>(
                manageStationModel, "longitudeDegrees")));
        formView.add(new Label("Coordonnate.degrees.longitude", String.valueOf(CoordTools.DEGREES)));
        formView.add(new TextField<BigDecimal>("ManageStationModel.longitudeMinutes", new PropertyModel<BigDecimal>(
                manageStationModel, "longitudeMinutes")));
        formView.add(new Label("Coordonnate.minutes.longitude", String.valueOf(CoordTools.MINUTES)));
        formView.add(new DropDownChoice<Character>("ManageStationModel.longitudeOrientation",
                new PropertyModel<Character>(manageStationModel, "longitudeOrientation"), Arrays
                        .asList(CoordTools.LONGITUDE_ORIENTATIONS)));

        formView.add(new DropDownChoice<Integer>("Station.referentiel", new PropertyModel<Integer>(stationModel,
                "referentiel"), WebContext.REFERENTIEL_CODES, new MapChoiceRenderer<Integer, String>(
                DataContext.REFERENTIELS)));

        // add list document panel
        ManageListDocumentsPanel manageListDocumentsPanel = new ManageListDocumentsPanel("ManageListDocumentsPanel",
                stationModel, currentPage);
        manageListDocumentsPanel
                .setUpdateOrDeleteEnabled(createMode
                        || stationService.updateOrdeleteStationEnabled(stationModel.getObject(), getSession()
                                .getUtilisateur()));
        formView.add(manageListDocumentsPanel);

        Button createButton = new SubmittableButton(ACTION_CREATE, new SubmittableButtonEvents() {
            @Override
            public void onProcess() throws DataConstraintException {
                if (manageStationModel.getObject().getLatitudeDegrees() == null) {
                    stationModel.getObject().setReferentiel(null);
                }
                stationService.createStation(stationModel.getObject());
            }

            @Override
            public void onSuccess() {
                successNextPage(ACTION_CREATE);
                redirect();
            }

            @Override
            public void onValidate() {
                validateModel();
            }
        });
        createButton.setVisibilityAllowed(createMode);
        formView.add(createButton);

        Button updateButton = new SubmittableButton(ACTION_UPDATE, new SubmittableButtonEvents() {
            @Override
            public void onProcess() throws DataConstraintException {
                if (manageStationModel.getObject().getLatitudeDegrees() == null) {
                    stationModel.getObject().setReferentiel(null);
                }
                stationService.updateStation(stationModel.getObject());
            }

            @Override
            public void onSuccess() {
                successNextPage(ACTION_UPDATE);
                redirect();
            }

            @Override
            public void onValidate() {
                validateModel();
            }
        });
        updateButton
                .setVisibilityAllowed(!createMode
                        && stationService.updateOrdeleteStationEnabled(stationModel.getObject(), getSession()
                                .getUtilisateur()));
        formView.add(updateButton);

        Button deleteButton = new SubmittableButton(ACTION_DELETE, new SubmittableButtonEvents() {
            @Override
            public void onProcess() throws DataConstraintException {
                stationService.deleteStation(stationModel.getObject());
            }

            @Override
            public void onSuccess() {
                successNextPage(ACTION_DELETE);
                redirect();
            }
        });
        deleteButton
                .setVisibilityAllowed(!createMode
                        && stationService.updateOrdeleteStationEnabled(stationModel.getObject(), getSession()
                                .getUtilisateur()));
        deleteButton.add(new JSConfirmationBehavior(getStringModel("Confirm")));
        deleteButton.setDefaultFormProcessing(false);
        formView.add(deleteButton);

        formView.add(new Link<Void>("Cancel") {
            // Cas où le formulaire est annulé
            @Override
            public void onClick() {
                callerPage.responsePage((TemplatePage) this.getPage());
            }
        });

        if (createMode) {
            // Données par défaut du modèle
            if (stationModel.getObject().getCodePays() == null) {
                stationModel.getObject().setCodePays(
                        WebContext.COUNTRIES.get(getSession().getLocale()).entrySet().iterator().next().getKey());
            }
            if (stationModel.getObject().getReferentiel() == null) {
                stationModel.getObject().setReferentiel(WebContext.REFERENTIEL_CODES.get(0));
            }
            // Données par défaut du modèle intermédiaire
            if (manageStationModel.getObject().getLatitudeOrientation() == null) {
                manageStationModel.getObject().setLatitudeOrientation(CoordTools.LATITUDE_ORIENTATIONS[1]);
            }
            if (manageStationModel.getObject().getLongitudeOrientation() == null) {
                manageStationModel.getObject().setLongitudeOrientation(CoordTools.LONGITUDE_ORIENTATIONS[0]);
            }
        }

        add(formView);
    }

    /**
     * Page redirection. Cas où le formulaire est validé
     */
    private void redirect() {
        if (multipleEntry) {
            // Redirection vers l'écran de saisie d'une nouvelle station, en fournissant déjà quelques données
            Station nextStation = new Station();
            nextStation.setCodePays(stationModel.getObject().getCodePays());
            if (manageStationModel.getObject().getLatitudeDegrees() != null) {
                nextStation.setLatitude(stationModel.getObject().getLatitude());
                nextStation.setLongitude(stationModel.getObject().getLongitude());
                nextStation.setReferentiel(stationModel.getObject().getReferentiel());
            }
            setResponsePage(new ManageStationPage(nextStation, callerPage));
        } else {
            // On passe l'id de la station associée à cette page, en paramètre de la prochaine page, pour lui permettre
            // de
            // l'exploiter si besoin
            callerPage.addPageParameter(Station.class.getSimpleName(), stationModel.getObject().getIdStation());
            callerPage.responsePage(this);
        }
    }

    /**
     * Validate the campagne model (for update & create)
     */
    private void validateModel() {
        if (stationModel.getObject().getCreateur() == null) {
            stationModel.getObject().setCreateur(getSession().getUtilisateur());
        }

        List<String> referentielErrors = validator.validate(stationModel.getObject(), getSession().getLocale(),
                "referentiel");
        List<String> coordErrors = validator.validate(manageStationModel.getObject(), getSession().getLocale());

        // On ne considère les coordonnées que si au moins un des champs a été renseigné
        if (manageStationModel.getObject().getLatitudeDegrees() != null
                || manageStationModel.getObject().getLatitudeMinutes() != null
                || manageStationModel.getObject().getLongitudeDegrees() != null
                || manageStationModel.getObject().getLongitudeMinutes() != null) {
            // Tous les champs doivent avoir été renseignées pour que les coordonnées soient valides
            if (manageStationModel.getObject().getLatitudeDegrees() != null
                    && manageStationModel.getObject().getLatitudeMinutes() != null
                    && manageStationModel.getObject().getLatitudeOrientation() != null
                    && manageStationModel.getObject().getLongitudeDegrees() != null
                    && manageStationModel.getObject().getLongitudeMinutes() != null
                    && manageStationModel.getObject().getLongitudeOrientation() != null
                    && stationModel.getObject().getReferentiel() != null) {
                // Construction des coordonnées
                if (referentielErrors.isEmpty() && coordErrors.isEmpty()) {
                    stationModel.getObject().setLatitude(
                            CoordTools.latitude(manageStationModel.getObject().getLatitudeDegrees(), manageStationModel
                                    .getObject().getLatitudeMinutes(), manageStationModel.getObject()
                                    .getLatitudeOrientation()));
                    stationModel.getObject().setLongitude(
                            CoordTools.longitude(manageStationModel.getObject().getLongitudeDegrees(),
                                    manageStationModel.getObject().getLongitudeMinutes(), manageStationModel
                                            .getObject().getLongitudeOrientation()));
                }
            } else {
                getPage().error(getString("Station.coordonnees.KO"));
            }
        } else {
            // si tous les champs sont vide, on met à null les coordonnées
            if (manageStationModel.getObject().getLatitudeDegrees() == null
                    && manageStationModel.getObject().getLatitudeMinutes() == null
                    && manageStationModel.getObject().getLongitudeDegrees() == null
                    && manageStationModel.getObject().getLongitudeMinutes() == null) {
                stationModel.getObject().setLatitude(null);
                stationModel.getObject().setLongitude(null);
            }
        }

        addValidationErrors(validator.validate(stationModel.getObject(), getSession().getLocale(), "nom", "codePays",
                "localite", "complement", "createur", "latitude", "longitude"));
        addValidationErrors(referentielErrors);
        addValidationErrors(coordErrors);
    }
}
