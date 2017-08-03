/*
 * #%L
 * Cantharella :: Web
 * $Id: ManageLotPage.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.web/src/main/java/nc/ird/cantharella/web/pages/domain/lot/ManageLotPage.java $
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
package nc.ird.cantharella.web.pages.domain.lot;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import nc.ird.cantharella.data.exceptions.DataConstraintException;
import nc.ird.cantharella.data.exceptions.DataNotFoundException;
import nc.ird.cantharella.data.exceptions.UnexpectedException;
import nc.ird.cantharella.data.model.Campagne;
import nc.ird.cantharella.data.model.Lot;
import nc.ird.cantharella.data.model.Partie;
import nc.ird.cantharella.data.model.Specimen;
import nc.ird.cantharella.data.model.Station;
import nc.ird.cantharella.data.validation.utils.ModelValidator;
import nc.ird.cantharella.service.services.CampagneService;
import nc.ird.cantharella.service.services.LotService;
import nc.ird.cantharella.service.services.SpecimenService;
import nc.ird.cantharella.web.pages.TemplatePage;
import nc.ird.cantharella.web.pages.domain.campagne.ManageCampagnePage;
import nc.ird.cantharella.web.pages.domain.document.panel.ManageListDocumentsPanel;
import nc.ird.cantharella.web.pages.domain.specimen.ManageSpecimenPage;
import nc.ird.cantharella.web.utils.CallerPage;
import nc.ird.cantharella.web.utils.behaviors.JSConfirmationBehavior;
import nc.ird.cantharella.web.utils.forms.SubmittableButton;
import nc.ird.cantharella.web.utils.forms.SubmittableButtonEvents;
import nc.ird.cantharella.web.utils.panels.SimpleTooltipPanel;
import nc.ird.cantharella.web.utils.security.AuthRole;
import nc.ird.cantharella.web.utils.security.AuthRoles;
import nc.ird.cantharella.utils.BeanTools.AccessType;
import nc.ird.cantharella.utils.CollectionTools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.extensions.markup.html.form.DateTextField;
import org.apache.wicket.extensions.yui.calendar.DatePicker;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.CheckBox;
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
 * Gestion des lots
 * 
 * @author Mickael Tricot
 * @author Adrien Cheype
 */
@AuthRoles({ AuthRole.USER, AuthRole.ADMIN })
public final class ManageLotPage extends TemplatePage {

    /** Action : create */
    private static final String ACTION_CREATE = "Create";

    /** Action : delete */
    private static final String ACTION_DELETE = "Delete";

    /** Action : update */
    private static final String ACTION_UPDATE = "Update";

    /** Logger */
    private static final Logger LOG = LoggerFactory.getLogger(ManageLotPage.class);

    /** Campagnes */
    private final List<Campagne> campagnes;

    /** Service : campagne */
    @SpringBean
    private CampagneService campagneService;

    /** Modèle : lot */
    private final IModel<Lot> lotModel;

    /** Service : lot */
    @SpringBean
    private LotService lotService;

    /** Spécimens */
    private final List<Specimen> specimens;

    /** Service : specimen */
    @SpringBean
    private SpecimenService specimenService;

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
    public ManageLotPage(CallerPage callerPage, boolean multipleEntry) {
        this(null, null, callerPage, multipleEntry);
    }

    /**
     * Constructeur (mode édition)
     * 
     * @param idLot ID lot
     * @param callerPage Page appelante
     */
    public ManageLotPage(Integer idLot, CallerPage callerPage) {
        this(idLot, null, callerPage, false);
    }

    /**
     * Constructeur (mode saisie du lot suivante)
     * 
     * @param lot Lot
     * @param callerPage Page appelante
     */
    public ManageLotPage(Lot lot, CallerPage callerPage) {
        this(null, lot, callerPage, true);
    }

    /**
     * Constructeur. Si idLot et lot sont null, on créée un nouveau Lot. Si idLot est renseigné, on édite le lot
     * correspondant. Si lot est renseigné, on créée un nouveau lot à partir des informations qu'il contient.
     * 
     * @param idLot ID lot
     * @param lot Lot
     * @param callerPage Page appelante
     * @param multipleEntry Saisie multiple
     */
    private ManageLotPage(Integer idLot, Lot lot, final CallerPage callerPage, boolean multipleEntry) {
        super(ManageLotPage.class);
        assert idLot == null || lot == null;
        this.callerPage = callerPage;
        this.multipleEntry = multipleEntry;

        final CallerPage currentPage = new CallerPage(this);

        // Initialisation du modèle
        try {
            lotModel = new Model<Lot>(idLot == null && lot == null ? new Lot() : lot != null ? lot
                    : lotService.loadLot(idLot));
        } catch (DataNotFoundException e) {
            LOG.error(e.getMessage(), e);
            throw new UnexpectedException(e);
        }

        boolean createMode = idLot == null;
        if (createMode) {
            lotModel.getObject().setCreateur(getSession().getUtilisateur());
        }

        // Initialisation des listes
        campagnes = campagneService.listCampagnes(getSession().getUtilisateur());
        specimens = specimenService.listSpecimens(getSession().getUtilisateur());

        List<Partie> parties = lotService.listParties();

        if (lot != null) {
            // qd saisie multiple avec préremplissage, hack nécessaire afin d'avoir dans le model le même objet que
            // celui de la liste de choix (sinon comme les objets viennent de sessions hibernate différentes, on n'a pas
            // l'égalité entre les objets)
            lotModel.getObject().setCampagne(
                    CollectionTools.findWithValue(campagnes, "idCampagne", AccessType.GETTER, lotModel.getObject()
                            .getCampagne().getIdCampagne()));
            // normalement pas nul car un bean campagne (le modèle donné en l'occurence) bien formé comporte une
            // campagne de renseigné
            if (lotModel.getObject().getCampagne() != null) {
                List<Station> stations = lotModel.getObject().getCampagne().getStations();
                lotModel.getObject().setStation(
                        CollectionTools.findWithValue(stations, "idStation", AccessType.GETTER, lotModel.getObject()
                                .getStation().getIdStation()));
            }
            if (lotModel.getObject().getPartie() != null) {
                lotModel.getObject().setPartie(
                        CollectionTools.findWithValue(parties, "idPartie", AccessType.GETTER, lotModel.getObject()
                                .getPartie().getIdPartie()));
            }
        } else if (idLot != null) {

        }

        final Form<Void> formView = new Form<Void>("Form");
        DropDownChoice<Campagne> campagnesInput = new DropDownChoice<Campagne>("Lot.campagne",
                new PropertyModel<Campagne>(lotModel, "campagne"), campagnes);
        campagnesInput.setNullValid(false);
        formView.add(campagnesInput);

        // Action : création d'une nouvelle campagne
        // ajaxSubmitLink permet de sauvegarder l'état du formulaire
        formView.add(new AjaxSubmitLink("NewCampagne") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                setResponsePage(new ManageCampagnePage(currentPage, false));
            }

            // si erreur, le formulaire est également enregistré puis la redirection effectuée
            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                setResponsePage(new ManageCampagnePage(currentPage, false));
            }
        });

        final DropDownChoice<Station> stationsInput = new DropDownChoice<Station>("Lot.station",
                new PropertyModel<Station>(lotModel, "station"),
                lotModel.getObject().getCampagne() == null ? new ArrayList<Station>() : lotModel.getObject()
                        .getCampagne().getStations());
        stationsInput.setOutputMarkupId(true);
        stationsInput.setNullValid(false);
        stationsInput.setEnabled(lotModel.getObject().getCampagne() != null);
        formView.add(stationsInput);
        formView.add(new SimpleTooltipPanel("Lot.station.info", getStringModel("Lot.station.info")));

        campagnesInput.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            protected void onUpdate(AjaxRequestTarget target) {
                List<Station> stations = new ArrayList<Station>();
                if (lotModel.getObject().getCampagne() != null) {
                    campagneService.refreshCampagne(lotModel.getObject().getCampagne());
                    stations = lotModel.getObject().getCampagne().getSortedStations();
                }
                stationsInput.setChoices(stations);
                stationsInput.setEnabled(lotModel.getObject().getCampagne() != null);
                lotModel.getObject().setStation(null);
                // refresh the station choices component
                target.add(stationsInput);
            }
        });

        formView.add(new DateTextField("Lot.dateRecolte", new PropertyModel<Date>(lotModel, "dateRecolte"))
                .add(new DatePicker()));
        formView.add(new TextField<String>("Lot.ref", new PropertyModel<String>(lotModel, "ref")));
        formView.add(new DropDownChoice<Specimen>("Lot.specimenRef", new PropertyModel<Specimen>(lotModel,
                "specimenRef"), specimens).setNullValid(false));

        // Action : création d'un nouveau spécimen
        // ajaxSubmitLink permet de sauvegarder l'état du formulaire
        formView.add(new AjaxSubmitLink("NewSpecimen") {
            @Override
            protected void onSubmit(AjaxRequestTarget arg0, Form<?> arg1) {
                setResponsePage(new ManageSpecimenPage(currentPage, false));
            }

            // si erreur, le formulaire est également enregistré puis la redirection effectuée
            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                setResponsePage(new ManageSpecimenPage(currentPage, false));
            }
        });
        formView.add(new DropDownChoice<Partie>("Lot.partie", new PropertyModel<Partie>(lotModel, "partie"), parties)
                .setNullValid(true));
        formView.add(new TextField<BigDecimal>("Lot.masseFraiche", new PropertyModel<BigDecimal>(lotModel,
                "masseFraiche")));
        formView.add(new TextField<BigDecimal>("Lot.masseSeche", new PropertyModel<BigDecimal>(lotModel, "masseSeche")));
        formView.add(new CheckBox("Lot.echantillonColl", new PropertyModel<Boolean>(lotModel, "echantillonColl")));
        formView.add(new CheckBox("Lot.echantillonIdent", new PropertyModel<Boolean>(lotModel, "echantillonIdent")));
        formView.add(new CheckBox("Lot.echantillonPhylo", new PropertyModel<Boolean>(lotModel, "echantillonPhylo")));
        formView.add(new TextArea<String>("Lot.complement", new PropertyModel<String>(lotModel, "complement")));

        // Créateur en lecture seule
        formView.add(new TextField<String>("Lot.createur", new PropertyModel<String>(lotModel, "createur"))
                .setEnabled(false));

        // add list document panel
        ManageListDocumentsPanel manageListDocumentsPanel = new ManageListDocumentsPanel("ManageListDocumentsPanel",
                lotModel, currentPage);
        manageListDocumentsPanel.setUpdateOrDeleteEnabled(createMode
                || lotService.updateOrdeleteLotEnabled(lotModel.getObject(), getSession().getUtilisateur()));
        formView.add(manageListDocumentsPanel);

        // Action : création du lot
        Button createButton = new SubmittableButton(ACTION_CREATE, new SubmittableButtonEvents() {
            @Override
            public void onProcess() throws DataConstraintException {
                lotService.createLot(lotModel.getObject());
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

        // Action : mise à jour du lot
        Button updateButton = new SubmittableButton(ACTION_UPDATE, new SubmittableButtonEvents() {
            @Override
            public void onProcess() throws DataConstraintException {
                lotService.updateLot(lotModel.getObject());
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
                && lotService.updateOrdeleteLotEnabled(lotModel.getObject(), getSession().getUtilisateur()));
        formView.add(updateButton);

        // Action : suppression du lot
        Button deleteButton = new SubmittableButton(ACTION_DELETE, new SubmittableButtonEvents() {
            @Override
            public void onProcess() throws DataConstraintException {
                lotService.deleteLot(lotModel.getObject());
            }

            @Override
            public void onSuccess() {
                successNextPage(ACTION_DELETE);
                callerPage.responsePage((TemplatePage) getPage());
            }
        });
        deleteButton.setVisibilityAllowed(!createMode
                && lotService.updateOrdeleteLotEnabled(lotModel.getObject(), getSession().getUtilisateur()));
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
            // Redirection vers l'écran de saisie d'un nouveau lot, en fournissant déjà quelques données
            Lot nextLot = new Lot();
            nextLot.setCampagne(lotModel.getObject().getCampagne());
            nextLot.setStation(lotModel.getObject().getStation());
            nextLot.setDateRecolte(lotModel.getObject().getDateRecolte());
            nextLot.setPartie(lotModel.getObject().getPartie());
            setResponsePage(new ManageLotPage(nextLot, callerPage));
        } else if (callerPage != null) {
            // On passe l'id du lot associé à cette page, en paramètre de la prochaine page, pour lui permettre de
            // l'exploiter si besoin
            callerPage.addPageParameter(Lot.class.getSimpleName(), lotModel.getObject().getIdLot());
            callerPage.responsePage(this);
        }
    }

    /**
     * Refresh model
     */
    private void refreshModel() {

        // Récupère (et supprime) les éventuels nouveaux objets créés dans les paramètres de la page.
        String key = Campagne.class.getSimpleName();
        if (getPageParameters().getNamedKeys().contains(key)) {
            CollectionTools.setter(campagnes, campagneService.listCampagnes(getSession().getUtilisateur()));
            try {
                Campagne createdCampagne = campagneService.loadCampagne(getPageParameters().get(key).toInt());
                lotModel.getObject().setCampagne(createdCampagne);
            } catch (StringValueConversionException e) {
                LOG.error(e.getMessage(), e);
                throw new UnexpectedException(e);
            } catch (DataNotFoundException e) {
                LOG.error(e.getMessage(), e);
                throw new UnexpectedException(e);
            }
            getPageParameters().remove(key);
        }

        key = Specimen.class.getSimpleName();
        if (getPageParameters().getNamedKeys().contains(key)) {
            CollectionTools.setter(specimens, specimenService.listSpecimens(getSession().getUtilisateur()));
            try {
                Specimen createdSpecimen = specimenService.loadSpecimen(getPageParameters().get(key).toInt());
                lotModel.getObject().setSpecimenRef(createdSpecimen);
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
        if (lotModel.getObject().getCreateur() == null) {
            lotModel.getObject().setCreateur(getSession().getUtilisateur());
        }
        addValidationErrors(validator.validate(lotModel.getObject(), getSession().getLocale()));
    }
}
