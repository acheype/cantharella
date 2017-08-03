/*
 * #%L
 * Cantharella :: Web
 * $Id: ManageCampagnePage.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.web/src/main/java/nc/ird/cantharella/web/pages/domain/campagne/ManageCampagnePage.java $
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
import java.util.Date;
import java.util.List;

import nc.ird.cantharella.data.exceptions.DataConstraintException;
import nc.ird.cantharella.data.exceptions.DataNotFoundException;
import nc.ird.cantharella.data.exceptions.UnexpectedException;
import nc.ird.cantharella.data.model.Campagne;
import nc.ird.cantharella.data.model.CampagnePersonneParticipant;
import nc.ird.cantharella.data.model.Personne;
import nc.ird.cantharella.data.model.Station;
import nc.ird.cantharella.data.validation.utils.ModelValidator;
import nc.ird.cantharella.service.services.CampagneService;
import nc.ird.cantharella.service.services.PersonneService;
import nc.ird.cantharella.service.services.StationService;
import nc.ird.cantharella.web.config.WebContext;
import nc.ird.cantharella.web.pages.TemplatePage;
import nc.ird.cantharella.web.pages.domain.document.panel.ManageListDocumentsPanel;
import nc.ird.cantharella.web.pages.domain.personne.ManagePersonnePage;
import nc.ird.cantharella.web.pages.domain.station.ManageStationPage;
import nc.ird.cantharella.web.pages.renderers.PersonneRenderer;
import nc.ird.cantharella.web.utils.CallerPage;
import nc.ird.cantharella.web.utils.behaviors.JSConfirmationBehavior;
import nc.ird.cantharella.web.utils.behaviors.ReplaceEmptyLabelBehavior;
import nc.ird.cantharella.web.utils.forms.AutoCompleteTextFieldString;
import nc.ird.cantharella.web.utils.forms.AutoCompleteTextFieldString.ComparisonMode;
import nc.ird.cantharella.web.utils.forms.SubmittableButton;
import nc.ird.cantharella.web.utils.forms.SubmittableButtonEvents;
import nc.ird.cantharella.web.utils.models.DisplayMapValuePropertyModel;
import nc.ird.cantharella.web.utils.panels.SimpleTooltipPanel;
import nc.ird.cantharella.web.utils.renderers.MapChoiceRenderer;
import nc.ird.cantharella.web.utils.security.AuthRole;
import nc.ird.cantharella.web.utils.security.AuthRoles;
import nc.ird.cantharella.utils.CollectionTools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxFallbackButton;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.extensions.markup.html.form.DateTextField;
import org.apache.wicket.extensions.yui.calendar.DatePicker;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.AbstractSingleSelectChoice;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.string.StringValueConversionException;

/**
 * Gestion d'une campagne
 * 
 * @author Mickael Tricot
 * @author Adrien Cheype
 */
@AuthRoles({ AuthRole.ADMIN, AuthRole.USER })
public final class ManageCampagnePage extends TemplatePage {

    /** Action : create */
    private static final String ACTION_CREATE = "Create";

    /** Action : delete */
    private static final String ACTION_DELETE = "Delete";

    /** Action : update */
    private static final String ACTION_UPDATE = "Update";

    /** Logger */
    private static final Logger LOG = LoggerFactory.getLogger(ManageCampagnePage.class);

    /** Bouton d'ajout d'un participant */
    private Button addParticipant;

    /** Bouton d'ajout d'une station */
    private Button addStation;

    /** Choix d'un participant */
    private AbstractSingleSelectChoice<Personne> availablePersonnes;

    /** Choix d'une station prospectée */
    private AbstractSingleSelectChoice<Station> availableStations;

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

    /** Modèle : participant */
    private final IModel<CampagnePersonneParticipant> participantModel;

    /** Modèle : station */
    private final IModel<Station> stationModel;

    /** Liste des personnes existantes */
    private final List<Personne> personnes;

    /** Liste des stations existantes */
    private final List<Station> stations;

    /** Service : personnes */
    @SpringBean
    private PersonneService personneService;

    /** Validateur modèle */
    @SpringBean(name = "webModelValidator")
    private ModelValidator validator;

    /** ComplementView */
    FormComponent<String> complementView;

    /** Saisie multiple */
    private boolean multipleEntry;

    /**
     * Constructeur (mode création)
     * 
     * @param callerPage Page appelante
     * @param multipleEntry Saisie multiple
     */
    public ManageCampagnePage(CallerPage callerPage, boolean multipleEntry) {
        this(null, null, callerPage, multipleEntry);
    }

    /**
     * Constructeur (mode édition)
     * 
     * @param idCampagne ID campagne
     * @param callerPage Page appelante
     */
    public ManageCampagnePage(Integer idCampagne, CallerPage callerPage) {
        this(idCampagne, null, callerPage, false);
    }

    /**
     * Constructeur (mode saisie de la campagne suivante)
     * 
     * @param campagne campagne
     * @param callerPage Page appelante
     */
    public ManageCampagnePage(Campagne campagne, CallerPage callerPage) {
        this(null, campagne, callerPage, true);
    }

    /**
     * Constructeur. Si idCampagne et campagne sont null, on créée une nouvelle campagne. Si idCampagne est renseigné,
     * on édite la campagne correspondante. Si campagne est renseignée, on créée une nouvelle campagne à partir des
     * informations qu'elle contient.
     * 
     * @param idCampagne ID campagne
     * @param campagne Lot
     * @param callerPage Page appelante
     * @param multipleEntry Saisie multiple
     */
    private ManageCampagnePage(Integer idCampagne, Campagne campagne, final CallerPage callerPage, boolean multipleEntry) {
        super(ManageCampagnePage.class);
        assert idCampagne == null || campagne == null;
        this.callerPage = callerPage;
        final CallerPage currentPage = new CallerPage(this);
        this.multipleEntry = multipleEntry;

        // Initialisation des modèles
        try {
            campagneModel = new Model<Campagne>(idCampagne == null && campagne == null ? new Campagne()
                    : campagne != null ? campagne : campagneService.loadCampagne(idCampagne));
        } catch (DataNotFoundException e) {
            LOG.error(e.getMessage(), e);
            throw new UnexpectedException(e);
        }
        boolean createMode = idCampagne == null;
        if (createMode) {
            campagneModel.getObject().setCreateur(getSession().getUtilisateur());
        }
        participantModel = new Model<CampagnePersonneParticipant>(new CampagnePersonneParticipant());
        participantModel.getObject().getId().setPk1(campagneModel.getObject());
        stationModel = new Model<Station>(new Station());

        // Initialisation des listes
        List<String> programmes = campagneService.listCampagneProgrammes();
        personnes = personneService.listPersonnes();
        stations = stationService.listStations(getSession().getUtilisateur());

        final Form<Void> formView = new Form<Void>("Form");

        formView.add(new TextField<String>("Campagne.nom", new PropertyModel<String>(campagneModel, "nom")));

        // Choix du code pays
        formView.add(new DropDownChoice<String>("Campagne.codePays", new PropertyModel<String>(campagneModel,
                "codePays"), WebContext.COUNTRY_CODES.get(getSession().getLocale()),
                new MapChoiceRenderer<String, String>(WebContext.COUNTRIES.get(getSession().getLocale()))));
        if (campagneModel.getObject().getCodePays() == null) {
            campagneModel.getObject().setCodePays(
                    WebContext.COUNTRIES.get(getSession().getLocale()).entrySet().iterator().next().getKey());
        }

        // Champs de type Date
        formView.add(new DateTextField("Campagne.dateDeb", new PropertyModel<Date>(campagneModel, "dateDeb"))
                .add(new DatePicker()));

        formView.add(new DateTextField("Campagne.dateFin", new PropertyModel<Date>(campagneModel, "dateFin"))
                .add(new DatePicker()));

        formView.add(new TextArea<String>("Campagne.mentionLegale", new PropertyModel<String>(campagneModel,
                "mentionLegale")));
        formView.add(new SimpleTooltipPanel("Campagne.mentionLegale.info",
                getStringModel("Campagne.mentionLegale.info")));

        formView.add(new TextArea<String>("Campagne.complement", new PropertyModel<String>(campagneModel, "complement")));
        formView.add(new SimpleTooltipPanel("Campagne.complement.info", getStringModel("Campagne.complement.info")));

        formView.add(new TextField<String>("Campagne.createur", new PropertyModel<String>(campagneModel, "createur"))
                .setEnabled(false));

        // Champs en lecture seule
        formView.add(new AutoCompleteTextFieldString("Campagne.programme", new PropertyModel<String>(campagneModel,
                "programme"), programmes, ComparisonMode.CONTAINS));

        initParticipantsFields(formView);
        initStationsFields(formView);

        // add list document panel
        ManageListDocumentsPanel manageListDocumentsPanel = new ManageListDocumentsPanel("ManageListDocumentsPanel",
                campagneModel, currentPage);
        manageListDocumentsPanel.setUpdateOrDeleteEnabled(createMode
                || campagneService.updateOrdeleteCampagneEnabled(campagneModel.getObject(), getSession()
                        .getUtilisateur()));
        formView.add(manageListDocumentsPanel);

        // Action : création de la campagne
        Button createButton = new SubmittableButton(ACTION_CREATE, new SubmittableButtonEvents() {
            @Override
            public void onProcess() throws DataConstraintException {
                campagneService.createCampagne(campagneModel.getObject());
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

        // Action : mise à jour de la campagne
        Button updateButton = new SubmittableButton(ACTION_UPDATE, new SubmittableButtonEvents() {
            @Override
            public void onProcess() throws DataConstraintException {
                campagneService.updateCampagne(campagneModel.getObject());
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
        updateButton.setVisibilityAllowed(!createMode
                && campagneService.updateOrdeleteCampagneEnabled(campagneModel.getObject(), getSession()
                        .getUtilisateur()));
        formView.add(updateButton);

        // Action : suppression de la campagne
        Button deleteButton = new SubmittableButton(ACTION_DELETE, new SubmittableButtonEvents() {
            @Override
            public void onProcess() throws DataConstraintException {
                campagneService.deleteCampagne(campagneModel.getObject());
            }

            @Override
            public void onSuccess() {
                successNextPage(ACTION_DELETE);
                redirect();
            }
        });
        deleteButton.setVisibilityAllowed(!createMode
                && campagneService.updateOrdeleteCampagneEnabled(campagneModel.getObject(), getSession()
                        .getUtilisateur()));
        deleteButton.add(new JSConfirmationBehavior(getStringModel("Confirm")));
        deleteButton.setDefaultFormProcessing(false);
        formView.add(deleteButton);

        // Action : annulation (lien)
        formView.add(new Link<Void>("Cancel") {
            // Cas où le formulaire est annulé
            @Override
            public void onClick() {
                callerPage.responsePage((TemplatePage) this.getPage());
            }
        });

        cleanPersonnesChoice();
        cleanStationsChoice();
        add(formView);
    }

    /**
     * Initialize participants section
     * 
     * @param formView The form view
     */
    private void initParticipantsFields(final Form<Void> formView) {
        // Tableau des participants
        final MarkupContainer participantsTable = new WebMarkupContainer("Campagne.participants.Table");
        participantsTable.setOutputMarkupId(true);

        // Liste des participants ajoutés
        participantsTable.add(new ListView<CampagnePersonneParticipant>("Campagne.participants.List",
                new PropertyModel<List<CampagnePersonneParticipant>>(campagneModel, "participants")) {
            @Override
            protected void populateItem(ListItem<CampagnePersonneParticipant> item) {
                if (item.getIndex() % 2 == 1) {
                    item.add(new AttributeModifier("class", item.getIndex() % 2 == 0 ? "even" : "odd"));
                }

                final CampagnePersonneParticipant participant = item.getModelObject();
                // Colonnes
                item.add(new Label("Campagne.participants.List.personne", new PropertyModel<String>(participant,
                        "id.pk2")));
                item.add(new Label("Campagne.participants.List.complement", new PropertyModel<String>(participant,
                        "complement")));

                // Action : suppression d'un participant
                Button deleteButton = new AjaxFallbackButton("Campagne.participants.Delete", formView) {
                    @Override
                    protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                        // Suppression
                        campagneModel.getObject().getParticipants().remove(participant);
                        cleanPersonnesChoice();

                        // Mise à jour des listes
                        refreshModel();
                        if (target != null) {
                            target.add(participantsTable, availablePersonnes);
                        }
                    }

                    @Override
                    protected void onError(AjaxRequestTarget target, Form<?> form) {
                        // never called
                    }
                };
                deleteButton.setDefaultFormProcessing(false);
                item.add(deleteButton);
            }
        });

        // Liste des personnes "disponibles" (non encore ajoutées dans la liste des participants)
        availablePersonnes = new DropDownChoice<Personne>("Campagne.participant", new PropertyModel<Personne>(
                participantModel, "id.pk2"), new ArrayList<Personne>(), new PersonneRenderer());
        availablePersonnes.setNullValid(false);
        availablePersonnes.setOutputMarkupId(true);

        participantsTable.add(availablePersonnes);

        complementView = new TextField<String>("CampagnePersonneParticipant.complement", new PropertyModel<String>(
                participantModel, "complement"));
        participantsTable.add(complementView);

        // Bouton AJAX pour ajouter un participant
        addParticipant = new AjaxFallbackButton("Campagne.participants.Add", formView) {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                if (participantModel.getObject().getId().getPk2() != null) {
                    try {
                        // Ajout du participant
                        campagneModel.getObject().getParticipants().add(participantModel.getObject().clone());
                        cleanPersonnesChoice();
                        // réinit de la ligne d'ajout
                        participantModel.getObject().getId().setPk2(null);
                        participantModel.getObject().setComplement(null);
                    } catch (CloneNotSupportedException e) {
                        LOG.error(e.getMessage(), e);
                        throw new UnexpectedException(e);
                    }

                    // Mise à jour des listes
                    refreshModel();
                    if (target != null) {
                        target.add(participantsTable, availablePersonnes);
                    }
                }
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                refreshFeedbackPage(target);
            }

        };
        participantsTable.add(addParticipant);

        formView.add(participantsTable);

        // Action : création d'une nouvelle personne
        // ajaxSubmitLink permet de sauvegarder l'état du formulaire
        participantsTable.add(new AjaxSubmitLink("NewPersonne") {
            @Override
            protected void onSubmit(AjaxRequestTarget arg0, Form<?> arg1) {
                setResponsePage(new ManagePersonnePage(new CallerPage((TemplatePage) getPage()), false));
            }

            // si erreur, le formulaire est également enregistré puis la redirection effectuée
            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                setResponsePage(new ManagePersonnePage(new CallerPage((TemplatePage) getPage()), false));
            }
        });
    }

    /**
     * Initialize stations section
     * 
     * @param formView The form view
     */
    private void initStationsFields(final Form<Void> formView) {
        // Tableau des participants
        final MarkupContainer stationsTable = new WebMarkupContainer("Campagne.stations.Table");
        stationsTable.setOutputMarkupId(true);

        // Liste des stations ajoutées
        stationsTable.add(new ListView<Station>("Campagne.stations.List", new PropertyModel<List<Station>>(
                campagneModel, "sortedStations")) {
            @Override
            protected void populateItem(ListItem<Station> item) {
                if (item.getIndex() % 2 == 1) {
                    item.add(new AttributeModifier("class", item.getIndex() % 2 == 0 ? "even" : "odd"));
                }

                final Station station = item.getModelObject();
                // Colonnes
                item.add(new Label("Campagne.stations.List.station", new PropertyModel<String>(station, "nom"))
                        .add(new ReplaceEmptyLabelBehavior()));
                item.add(new Label("Campagne.stations.List.codePays", new DisplayMapValuePropertyModel<String>(station,
                        "codePays", WebContext.COUNTRIES.get(getSession().getLocale())))
                        .add(new ReplaceEmptyLabelBehavior()));
                item.add(new Label("Campagne.stations.List.localite", new PropertyModel<String>(station, "localite"))
                        .add(new ReplaceEmptyLabelBehavior()));
                item.add(new Label("Campagne.stations.List.latitude", new PropertyModel<String>(station, "latitude"))
                        .add(new ReplaceEmptyLabelBehavior()));
                item.add(new Label("Campagne.stations.List.longitude", new PropertyModel<String>(station, "longitude"))
                        .add(new ReplaceEmptyLabelBehavior()));

                // Action : suppression d'une station
                Button deleteButton = new AjaxFallbackButton("Campagne.stations.Delete", formView) {
                    @Override
                    protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                        // Suppression
                        campagneModel.getObject().getStations().remove(station);
                        cleanStationsChoice();

                        // Mise à jour des listes
                        refreshModel();
                        if (target != null) {
                            target.add(stationsTable, availableStations);
                        }
                    }

                    @Override
                    protected void onError(AjaxRequestTarget target, Form<?> form) {
                        // never called
                    }

                };
                deleteButton.setDefaultFormProcessing(false);
                item.add(deleteButton);
            }
        });

        // Liste des stations "disponibles" (non encore ajoutées dans la liste des stations)
        availableStations = new DropDownChoice<Station>("Campagne.station", stationModel, new ArrayList<Station>());
        availableStations.setNullValid(false);
        availableStations.setOutputMarkupId(true);

        stationsTable.add(availableStations);

        final Label codePaysLabel = new Label("Campagne.station.codePays", new DisplayMapValuePropertyModel<String>(
                stationModel, "codePays", WebContext.COUNTRIES.get(getSession().getLocale())));
        codePaysLabel.setOutputMarkupId(true);
        stationsTable.add(codePaysLabel);

        final Label localiteLabel = new Label("Campagne.station.localite", new PropertyModel<String>(stationModel,
                "localite"));
        localiteLabel.setOutputMarkupId(true);
        stationsTable.add(localiteLabel);

        final Label latitudeLabel = new Label("Campagne.station.latitude", new PropertyModel<String>(stationModel,
                "latitude"));
        latitudeLabel.setOutputMarkupId(true);
        stationsTable.add(latitudeLabel);

        final Label longitudeLabel = new Label("Campagne.station.longitude", new PropertyModel<String>(stationModel,
                "longitude"));
        longitudeLabel.setOutputMarkupId(true);
        stationsTable.add(longitudeLabel);

        availableStations.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            protected void onUpdate(AjaxRequestTarget target) {
                target.add(codePaysLabel, localiteLabel, latitudeLabel, longitudeLabel);
            }
        });

        // Bouton AJAX pour ajouter une station
        addStation = new AjaxFallbackButton("Campagne.stations.Add", formView) {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                if (stationModel.getObject() != null) {
                    try {
                        // Ajout du participant
                        participantModel.getObject().setComplement(complementView.getValue());
                        campagneModel.getObject().getStations().add(stationModel.getObject().clone());
                        cleanStationsChoice();
                        // réinit de la ligne d'ajout
                        stationModel.setObject(new Station());
                    } catch (CloneNotSupportedException e) {
                        LOG.error(e.getMessage(), e);
                        throw new UnexpectedException(e);
                    }

                    // Mise à jour des listes
                    refreshModel();
                    if (target != null) {
                        target.add(stationsTable, availableStations);
                    }
                }
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                refreshFeedbackPage(target);
            }

        };
        stationsTable.add(addStation);

        formView.add(stationsTable);

        // Action : création d'une nouvelle station
        // ajaxSubmitLink permet de sauvegarder l'état du formulaire
        stationsTable.add(new AjaxSubmitLink("NewStation") {
            @Override
            protected void onSubmit(AjaxRequestTarget arg0, Form<?> arg1) {
                setResponsePage(new ManageStationPage(new CallerPage((TemplatePage) getPage()), false));
            }

            // si erreur, le formulaire est également enregistré puis la redirection effectuée
            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                setResponsePage(new ManageStationPage(new CallerPage((TemplatePage) getPage()), false));
            }
        });
    }

    /** {@inheritDoc} */
    @Override
    protected void onBeforeRender() {
        // On rafraichit le modèle lorsque la page est rechargée (par exemple après l'ajout d'une nouvelle entité
        // Station, Spécimen ou Campagne)
        refreshModel();
        super.onBeforeRender();
    }

    /**
     * Redirection vers une autre page. Cas où le formulaire est validé
     */
    private void redirect() {
        if (multipleEntry) {
            // Redirection vers l'écran de saisie d'une nouvelle campagne, en fournissant déjà quelques données
            Campagne nextCampagne = new Campagne();

            nextCampagne.setCodePays(campagneModel.getObject().getCodePays());
            nextCampagne.setProgramme(campagneModel.getObject().getProgramme());
            nextCampagne.setMentionLegale(campagneModel.getObject().getMentionLegale());
            setResponsePage(new ManageCampagnePage(nextCampagne, callerPage));
        } else if (callerPage != null) {
            // On passe la campagne associée à cette page, en paramètre de la prochaine page, pour lui permettre de
            // l'exploiter si besoin
            callerPage.addPageParameter(Campagne.class.getSimpleName(), campagneModel.getObject().getIdCampagne());
            callerPage.responsePage(this);
        }
    }

    /**
     * Mets à jour les personnes disponibles (non encore ajoutées)
     */
    private void refreshModel() {
        // Récupère (et supprime) les éventuels nouveaux objets créés dans les paramètres de la page.
        String key = Personne.class.getSimpleName();
        if (getPageParameters().getNamedKeys().contains(key)) {
            CollectionTools.setter(personnes, personneService.listPersonnes());
            cleanPersonnesChoice();
            try {
                Personne createdPersonne = personneService.loadPersonne(getPageParameters().get(key).toInt());
                participantModel.getObject().getId().setPk2(createdPersonne);

            } catch (StringValueConversionException e) {
                LOG.error(e.getMessage(), e);
                throw new UnexpectedException(e);
            } catch (DataNotFoundException e) {
                LOG.error(e.getMessage(), e);
                throw new UnexpectedException(e);
            }
            getPageParameters().remove(key);
        }

        key = Station.class.getSimpleName();
        if (getPageParameters().getNamedKeys().contains(key)) {
            CollectionTools.setter(stations, stationService.listStations(getSession().getUtilisateur()));
            cleanStationsChoice();
            try {
                Station createdStation = stationService.loadStation(getPageParameters().get(key).toInt());
                stationModel.setObject(createdStation);
            } catch (StringValueConversionException e) {
                LOG.error(e.getMessage(), e);
                throw new UnexpectedException(e);
            } catch (DataNotFoundException e) {
                LOG.error(e.getMessage(), e);
                throw new UnexpectedException(e);
            }
            getPageParameters().remove(key);
        }

    }

    /**
     * Suprimme de la liste des personnes proposées, les personnes qui sont déjà ajoutées comme participant
     */
    private void cleanPersonnesChoice() {
        List<Personne> choices = new ArrayList<Personne>(personnes);
        for (CampagnePersonneParticipant participant : campagneModel.getObject().getParticipants()) {
            for (Personne personne : personnes) {
                if (participant.getId().getPk2().getIdPersonne() == personne.getIdPersonne()) {
                    choices.remove(personne);
                }
            }
        }
        availablePersonnes.setChoices(choices);
        availablePersonnes.setEnabled(!choices.isEmpty());
        addParticipant.setEnabled(!choices.isEmpty());
        complementView.setEnabled(!choices.isEmpty());

    }

    /**
     * Suprimme de la liste des stations proposées, les stations qui sont déjà ajoutées comme station prospectée
     */
    private void cleanStationsChoice() {
        List<Station> choices = new ArrayList<Station>(stations);
        for (Station curStationAdded : campagneModel.getObject().getStations()) {
            for (Station curStation : stations) {
                if (curStationAdded.getIdStation() == curStation.getIdStation()) {
                    choices.remove(curStation);
                }
            }
        }
        availableStations.setChoices(choices);
        availableStations.setEnabled(!choices.isEmpty());
        addStation.setEnabled(!choices.isEmpty());
        availableStations.setEnabled(!choices.isEmpty());
    }

    /**
     * Validate the campagne model (for update & create)
     */
    private void validateModel() {
        if (campagneModel.getObject().getCreateur() == null) {
            campagneModel.getObject().setCreateur(getSession().getUtilisateur());
        }
        addValidationErrors(validator.validate(campagneModel.getObject(), getSession().getLocale(), "nom", "codePays",
                "programme", "complement", "createur"));
        List<String> dateErrors = validator.validate(campagneModel.getObject(), getSession().getLocale(), "dateDeb",
                "dateFin");
        addValidationErrors(dateErrors);
        if (dateErrors.isEmpty()
                && (campagneModel.getObject().getDateDeb() == null || campagneModel.getObject().getDateFin() == null || !campagneModel
                        .getObject().getDateDeb().before(campagneModel.getObject().getDateFin()))) {
            getPage().error(getStringModel("Campagne.dates.KO"));
        }
    }
}