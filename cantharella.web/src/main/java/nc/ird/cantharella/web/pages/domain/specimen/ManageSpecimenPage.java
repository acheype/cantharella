/*
 * #%L
 * Cantharella :: Web
 * $Id: ManageSpecimenPage.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.web/src/main/java/nc/ird/cantharella/web/pages/domain/specimen/ManageSpecimenPage.java $
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

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import nc.ird.cantharella.data.exceptions.DataConstraintException;
import nc.ird.cantharella.data.exceptions.DataNotFoundException;
import nc.ird.cantharella.data.exceptions.UnexpectedException;
import nc.ird.cantharella.data.model.Personne;
import nc.ird.cantharella.data.model.Specimen;
import nc.ird.cantharella.data.model.Specimen.TypeOrganisme;
import nc.ird.cantharella.data.model.Station;
import nc.ird.cantharella.data.validation.utils.ModelValidator;
import nc.ird.cantharella.service.services.PersonneService;
import nc.ird.cantharella.service.services.SpecimenService;
import nc.ird.cantharella.service.services.StationService;
import nc.ird.cantharella.web.pages.TemplatePage;
import nc.ird.cantharella.web.pages.domain.document.panel.ManageListDocumentsPanel;
import nc.ird.cantharella.web.pages.domain.personne.ManagePersonnePage;
import nc.ird.cantharella.web.pages.domain.station.ManageStationPage;
import nc.ird.cantharella.web.pages.renderers.PersonneRenderer;
import nc.ird.cantharella.web.utils.CallerPage;
import nc.ird.cantharella.web.utils.behaviors.JSConfirmationBehavior;
import nc.ird.cantharella.web.utils.forms.AutoCompleteTextFieldString;
import nc.ird.cantharella.web.utils.forms.AutoCompleteTextFieldString.ComparisonMode;
import nc.ird.cantharella.web.utils.forms.SubmittableButton;
import nc.ird.cantharella.web.utils.forms.SubmittableButtonEvents;
import nc.ird.cantharella.web.utils.renderers.EnumChoiceRenderer;
import nc.ird.cantharella.web.utils.security.AuthRole;
import nc.ird.cantharella.web.utils.security.AuthRoles;
import nc.ird.cantharella.utils.BeanTools.AccessType;
import nc.ird.cantharella.utils.CollectionTools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.extensions.markup.html.form.DateTextField;
import org.apache.wicket.extensions.yui.calendar.DatePicker;
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
import org.apache.wicket.util.string.StringValueConversionException;

/**
 * Gestion des specimens
 * 
 * @author Mickael Tricot
 * @author Adrien Cheype
 */
@AuthRoles({ AuthRole.USER, AuthRole.ADMIN })
public final class ManageSpecimenPage extends TemplatePage {

    /** Action : create */
    private static final String ACTION_CREATE = "Create";

    /** Action : delete */
    private static final String ACTION_DELETE = "Delete";

    /** Action : update */
    private static final String ACTION_UPDATE = "Update";

    /** Logger */
    private static final Logger LOG = LoggerFactory.getLogger(ManageSpecimenPage.class);

    /** Modèle : specimen */
    private final IModel<Specimen> specimenModel;

    /** Service : specimen */
    @SpringBean
    private SpecimenService specimenService;

    /** Liste des personnes existantes */
    private final List<Personne> personnes;

    /** Liste des stations existantes */
    private final List<Station> stations;

    /** Service : personne */
    @SpringBean
    private PersonneService personneService;

    /** Service : station */
    @SpringBean
    private StationService stationService;

    /** Model validateur */
    @SpringBean(name = "webModelValidator")
    private ModelValidator validator;

    /** Page appelante */
    private final CallerPage callerPage;

    /** Saisie multiple */
    private boolean multipleEntry;

    /**
     * Constructeur (mode création)
     * 
     * @param callerPage Page appelante
     * @param multipleEntry Saisie multiple
     */
    public ManageSpecimenPage(CallerPage callerPage, boolean multipleEntry) {
        this(null, null, callerPage, multipleEntry);
    }

    /**
     * Constructeur (mode édition)
     * 
     * @param idSpecimen Id du specimen
     * @param callerPage Page appelante
     */
    public ManageSpecimenPage(Integer idSpecimen, CallerPage callerPage) {
        this(idSpecimen, null, callerPage, false);
    }

    /**
     * Constructeur (mode saisie du specimen suivant)
     * 
     * @param specimen Specimen
     * @param callerPage Page appelante
     */
    public ManageSpecimenPage(Specimen specimen, CallerPage callerPage) {
        this(null, specimen, callerPage, true);
    }

    /**
     * Constructeur. Si refSpecimen et specimen sont nuls, on créée un nouveau Specimen. Si refSpecimen est renseigné,
     * on édite le specimen correspondant. Si specimen est renseigné, on créée un nouveau specimen à partir des
     * informations qu'il contient.
     * 
     * @param idSpecimen Id du specimen
     * @param specimen Specimen
     * @param callerPage Page appelante
     * @param multipleEntry Saisie multiple
     */
    private ManageSpecimenPage(Integer idSpecimen, Specimen specimen, final CallerPage callerPage, boolean multipleEntry) {
        super(ManageSpecimenPage.class);
        assert idSpecimen == null || specimen == null;
        this.callerPage = callerPage;
        this.multipleEntry = multipleEntry;

        final CallerPage currentPage = new CallerPage(this);

        // Initialisation du modèle
        try {
            specimenModel = new Model<Specimen>(idSpecimen == null && specimen == null ? new Specimen()
                    : specimen != null ? specimen : specimenService.loadSpecimen(idSpecimen));
        } catch (DataNotFoundException e) {
            LOG.error(e.getMessage(), e);
            throw new UnexpectedException(e);
        }
        boolean createMode = idSpecimen == null;
        if (createMode) {
            specimenModel.getObject().setCreateur(getSession().getUtilisateur());
        }

        // Initialisation des listes (pour le dropDownChoice)
        personnes = personneService.listPersonnes();
        stations = stationService.listStations(getSession().getUtilisateur());

        // Initialisation des listes (pour le AutoCompleteTextField)
        // liste des valeurs déjà existantes pour la propriété correspondante
        List<String> embranchements = specimenService.listSpecimenEmbranchements();
        List<String> familles = specimenService.listSpecimenFamilles();
        List<String> genres = specimenService.listSpecimenGenres();
        List<String> especes = specimenService.listSpecimenEspeces();
        List<String> sousEspeces = specimenService.listSpecimenSousEspeces();
        List<String> varietes = specimenService.listSpecimenVarietes();
        List<String> lieuxDepot = specimenService.listLieuxDepot();

        if (specimen != null) {
            // qd saisie multiple avec préremplissage, hack nécessaire afin d'avoir dans le model le même objet que
            // celui de la liste de choix (sinon comme les objets viennent de sessions hibernate différentes, on n'a pas
            // l'égalité entre les objets)
            if (specimenModel.getObject().getIdentificateur() != null) {
                specimenModel.getObject().setIdentificateur(
                        CollectionTools.findWithValue(personnes, "idPersonne", AccessType.GETTER, specimenModel
                                .getObject().getIdentificateur().getIdPersonne()));
            }
            if (specimenModel.getObject().getStation() != null) {
                specimenModel.getObject().setStation(
                        CollectionTools.findWithValue(stations, "idStation", AccessType.GETTER, specimenModel
                                .getObject().getStation().getIdStation()));
            }
        }

        final Form<Void> formView = new Form<Void>("Form");

        formView.add(new TextField<String>("Specimen.ref", new PropertyModel<String>(specimenModel, "ref")));
        formView.add(new AutoCompleteTextFieldString("Specimen.embranchement", new PropertyModel<String>(specimenModel,
                "embranchement"), embranchements, ComparisonMode.CONTAINS));
        formView.add(new AutoCompleteTextFieldString("Specimen.famille", new PropertyModel<String>(specimenModel,
                "famille"), familles, ComparisonMode.CONTAINS));
        formView.add(new AutoCompleteTextFieldString("Specimen.genre",
                new PropertyModel<String>(specimenModel, "genre"), genres, ComparisonMode.CONTAINS));
        formView.add(new AutoCompleteTextFieldString("Specimen.espece", new PropertyModel<String>(specimenModel,
                "espece"), especes, ComparisonMode.CONTAINS));
        formView.add(new AutoCompleteTextFieldString("Specimen.sousEspece", new PropertyModel<String>(specimenModel,
                "sousEspece"), sousEspeces, ComparisonMode.CONTAINS));
        formView.add(new AutoCompleteTextFieldString("Specimen.variete", new PropertyModel<String>(specimenModel,
                "variete"), varietes, ComparisonMode.CONTAINS));

        DropDownChoice<TypeOrganisme> typeOrganismeInput = new DropDownChoice<TypeOrganisme>("Specimen.typeOrganisme",
                new PropertyModel<TypeOrganisme>(specimenModel, "typeOrganisme"),
                Arrays.asList(TypeOrganisme.values()), new EnumChoiceRenderer<TypeOrganisme>(this));
        typeOrganismeInput.setNullValid(false);
        formView.add(typeOrganismeInput);

        formView.add(new DropDownChoice<Personne>("Specimen.identificateur", new PropertyModel<Personne>(specimenModel,
                "identificateur"), personnes, new PersonneRenderer()).setNullValid(true));
        // Action : création d'une nouvelle personne
        // ajaxSubmitLink permet de sauvegarder l'état du formulaire
        formView.add(new AjaxSubmitLink("NewPersonne") {
            @Override
            protected void onSubmit(AjaxRequestTarget arg0, Form<?> arg1) {
                setResponsePage(new ManagePersonnePage(currentPage, false));
            }

            // si erreur, le formulaire est également enregistré puis la redirection effectuée
            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                setResponsePage(new ManagePersonnePage(currentPage, false));
            }
        });

        formView.add(new DropDownChoice<Station>("Specimen.station", new PropertyModel<Station>(specimenModel,
                "station"), stations).setNullValid(true));

        // Action : création d'une nouvelle station
        // ajaxSubmitLink permet de sauvegarder l'état du formulaire
        formView.add(new AjaxSubmitLink("NewStation") {
            @Override
            protected void onSubmit(AjaxRequestTarget arg0, Form<?> arg1) {
                setResponsePage(new ManageStationPage(currentPage, false));
            }

            // si erreur, le formulaire est également enregistré puis la redirection effectuée
            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                setResponsePage(new ManageStationPage(currentPage, false));
            }
        });

        formView.add(new TextArea<String>("Specimen.complement", new PropertyModel<String>(specimenModel, "complement")));
        // Créateur en lecture seule
        formView.add(new TextField<String>("Specimen.createur", new PropertyModel<String>(specimenModel, "createur"))
                .setEnabled(false));

        // champs dépôt
        formView.add(new TextField<String>("Specimen.numDepot", new PropertyModel<String>(specimenModel, "numDepot")));
        formView.add(new DateTextField("Specimen.dateDepot", new PropertyModel<Date>(specimenModel, "dateDepot"))
                .add(new DatePicker()));
        formView.add(new AutoCompleteTextFieldString("Specimen.lieuDepot", new PropertyModel<String>(specimenModel,
                "lieuDepot"), lieuxDepot, ComparisonMode.CONTAINS));

        // add list document panel
        ManageListDocumentsPanel manageListDocumentsPanel = new ManageListDocumentsPanel("ManageListDocumentsPanel",
                specimenModel, currentPage);
        manageListDocumentsPanel.setUpdateOrDeleteEnabled(createMode
                || specimenService.updateOrdeleteSpecimenEnabled(specimenModel.getObject(), getSession()
                        .getUtilisateur()));
        formView.add(manageListDocumentsPanel);

        // Action : création du specimen
        Button createButton = new SubmittableButton(ACTION_CREATE, new SubmittableButtonEvents() {
            @Override
            public void onProcess() throws DataConstraintException {
                specimenService.createSpecimen(specimenModel.getObject());
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

        // Action : mise à jour du specimen
        Button updateButton = new SubmittableButton(ACTION_UPDATE, new SubmittableButtonEvents() {
            @Override
            public void onProcess() throws DataConstraintException {
                specimenService.updateSpecimen(specimenModel.getObject());
            }

            @Override
            public void onSuccess() {
                successNextPage(ACTION_UPDATE);
                callerPage.responsePage((TemplatePage) getPage());
            }

            @Override
            public void onValidate() {
                validateModel();
            }
        });
        updateButton.setVisibilityAllowed(!createMode
                && specimenService.updateOrdeleteSpecimenEnabled(specimenModel.getObject(), getSession()
                        .getUtilisateur()));
        formView.add(updateButton);

        // Action : suppression du specimen
        Button deleteButton = new SubmittableButton(ACTION_DELETE, new SubmittableButtonEvents() {
            @Override
            public void onProcess() throws DataConstraintException {
                specimenService.deleteSpecimen(specimenModel.getObject());
            }

            @Override
            public void onSuccess() {
                successNextPage(ACTION_DELETE);
                callerPage.responsePage((TemplatePage) getPage());
            }
        });
        deleteButton.setVisibilityAllowed(!createMode
                && specimenService.updateOrdeleteSpecimenEnabled(specimenModel.getObject(), getSession()
                        .getUtilisateur()));
        deleteButton.add(new JSConfirmationBehavior(getStringModel("Confirm")));
        deleteButton.setDefaultFormProcessing(false);
        formView.add(deleteButton);

        formView.add(new Link<Void>("Cancel") {
            // Cas où le formulaire est annulé
            @Override
            public void onClick() {
                callerPage.responsePage((TemplatePage) getPage());
            }
        });

        add(formView);
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
            // Redirection de nouveau vers l'écran de saisie d'un nouveau spécimen
            Specimen nextSpecimen = new Specimen();
            nextSpecimen.setTypeOrganisme(specimenModel.getObject().getTypeOrganisme());
            nextSpecimen.setEmbranchement(specimenModel.getObject().getEmbranchement());
            nextSpecimen.setIdentificateur(specimenModel.getObject().getIdentificateur());
            nextSpecimen.setStation(specimenModel.getObject().getStation());
            nextSpecimen.setLieuDepot(specimenModel.getObject().getLieuDepot());

            setResponsePage(new ManageSpecimenPage(nextSpecimen, callerPage));
        } else if (callerPage != null) {
            // On passe l'id du specimen associée à cette page, en paramètre de la prochaine page, pour lui permettre de
            // l'exploiter si besoin
            callerPage.addPageParameter(Specimen.class.getSimpleName(), specimenModel.getObject().getIdSpecimen());
            callerPage.responsePage(this);
        }
    }

    /**
     * Refresh model
     */
    private void refreshModel() {

        // Récupère (et supprime) les éventuels nouveaux objets créés dans les paramètres de la page.
        String key = Personne.class.getSimpleName();
        if (getPageParameters().getNamedKeys().contains(key)) {
            CollectionTools.setter(personnes, personneService.listPersonnes());
            try {
                Personne createdPersonne = personneService.loadPersonne(getPageParameters().get(key).toInt());
                specimenModel.getObject().setIdentificateur(createdPersonne);
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
            try {
                Station createdStation = stationService.loadStation(getPageParameters().get(key).toInt());
                specimenModel.getObject().setStation(createdStation);
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
     * Validate model
     */
    private void validateModel() {
        addValidationErrors(validator.validate(specimenModel.getObject(), getSession().getLocale()));
    }
}
