/*
 * #%L
 * Cantharella :: Web
 * $Id: ReadLotPage.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.web/src/main/java/nc/ird/cantharella/web/pages/domain/lot/ReadLotPage.java $
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

import nc.ird.cantharella.data.exceptions.DataConstraintException;
import nc.ird.cantharella.data.model.Campagne;
import nc.ird.cantharella.data.model.Lot;
import nc.ird.cantharella.data.model.Personne;
import nc.ird.cantharella.data.model.Specimen;
import nc.ird.cantharella.data.model.Station;
import nc.ird.cantharella.service.services.LotService;
import nc.ird.cantharella.web.pages.TemplatePage;
import nc.ird.cantharella.web.pages.domain.campagne.ReadCampagnePage;
import nc.ird.cantharella.web.pages.domain.document.panel.ReadListDocumentsPanel;
import nc.ird.cantharella.web.pages.domain.personne.ReadPersonnePage;
import nc.ird.cantharella.web.pages.domain.specimen.ReadSpecimenPage;
import nc.ird.cantharella.web.pages.domain.station.ReadStationPage;
import nc.ird.cantharella.web.utils.CallerPage;
import nc.ird.cantharella.web.utils.behaviors.JSConfirmationBehavior;
import nc.ird.cantharella.web.utils.behaviors.ReplaceEmptyLabelBehavior;
import nc.ird.cantharella.web.utils.forms.SubmittableButton;
import nc.ird.cantharella.web.utils.forms.SubmittableButtonEvents;
import nc.ird.cantharella.web.utils.models.DisplayBooleanPropertyModel;
import nc.ird.cantharella.web.utils.models.DisplayDecimalPropertyModel;
import nc.ird.cantharella.web.utils.models.DisplayDecimalPropertyModel.DecimalDisplFormat;
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
 * Consultation d'un lot
 * 
 * @author Alban Diguer
 */
public final class ReadLotPage extends TemplatePage {

    /** Action : delete */
    public static final String ACTION_DELETE = "Delete";

    /** Logger */
    //private static final Logger LOG = LoggerFactory.getLogger(ReadLotPage.class);
    /** Modèle : lot */
    private final IModel<Lot> lotModel;

    /** Service : lot */
    @SpringBean
    private LotService lotService;

    /** Page appelante */
    private final CallerPage callerPage;

    /**
     * Constructeur
     * 
     * @param idLot ID lot
     * @param callerPage Page appelante
     */
    public ReadLotPage(Integer idLot, final CallerPage callerPage) {
        super(ReadLotPage.class);
        this.callerPage = callerPage;
        final CallerPage currentPage = new CallerPage(this);

        // Initialisation du modèle
        lotModel = new GenericLoadableDetachableModel<Lot>(Lot.class, idLot);

        add(new PropertyLabelLinkPanel<Campagne>("Lot.campagne", new PropertyModel<Campagne>(lotModel, "campagne"),
                getStringModel("Read")) {
            @Override
            public void onClick() {
                setResponsePage(new ReadCampagnePage(getModelObject().getIdCampagne(), currentPage));
            }
        });

        add(new PropertyLabelLinkPanel<Station>("Lot.station", new PropertyModel<Station>(lotModel, "station"),
                getStringModel("Read")) {
            @Override
            public void onClick() {
                setResponsePage(new ReadStationPage(getModelObject().getIdStation(), currentPage));
            }
        });

        add(new Label("Lot.dateRecolte", new PropertyModel<String>(lotModel, "dateRecolte")) // formatShortDateValue(lotModel.getDateRecolte()))
                .add(new ReplaceEmptyLabelBehavior()));
        add(new Label("Lot.ref", new PropertyModel<String>(lotModel, "ref")).add(new ReplaceEmptyLabelBehavior()));

        add(new PropertyLabelLinkPanel<Specimen>("Lot.specimenRef",
                new PropertyModel<Specimen>(lotModel, "specimenRef"), getStringModel("Read")) {
            @Override
            public void onClick() {
                setResponsePage(new ReadSpecimenPage(getModelObject().getIdSpecimen(), currentPage));
            }
        });

        add(new Label("Lot.partie", new PropertyModel<String>(lotModel, "partie")).add(new ReplaceEmptyLabelBehavior()));
        add(new Label("Lot.masseFraiche", new DisplayDecimalPropertyModel(lotModel, "masseFraiche",
                DecimalDisplFormat.SMALL, getLocale())).add(new ReplaceEmptyLabelBehavior()));
        add(new Label("Lot.masseSeche", new DisplayDecimalPropertyModel(lotModel, "masseSeche",
                DecimalDisplFormat.SMALL, getLocale())).add(new ReplaceEmptyLabelBehavior()));
        add(new Label("Lot.echantillonColl", new DisplayBooleanPropertyModel(lotModel, "echantillonColl", this))
                .add(new ReplaceEmptyLabelBehavior()));
        add(new Label("Lot.echantillonIdent", new DisplayBooleanPropertyModel(lotModel, "echantillonIdent", this))
                .add(new ReplaceEmptyLabelBehavior()));
        add(new Label("Lot.echantillonPhylo", new DisplayBooleanPropertyModel(lotModel, "echantillonPhylo", this))
                .add(new ReplaceEmptyLabelBehavior()));
        add(new MultiLineLabel("Lot.complement", new PropertyModel<String>(lotModel, "complement"))
                .add(new ReplaceEmptyLabelBehavior()));

        add(new PropertyLabelLinkPanel<Personne>("Lot.createur", new PropertyModel<Personne>(lotModel, "createur"),
                getStringModel("Read")) {
            @Override
            public void onClick() {
                setResponsePage(new ReadPersonnePage(getModelObject().getIdPersonne(), currentPage));
            }
        });

        // add list document panel
        ReadListDocumentsPanel readListDocumentsPanel = new ReadListDocumentsPanel("ReadListDocumentsPanel", lotModel,
                currentPage);
        add(readListDocumentsPanel);

        // Formulaire des actions
        final Form<Void> formView = new Form<Void>("Form");

        // Action : mise à jour (redirection vers le formulaire)
        Link<Lot> updateLink = new Link<Lot>(getResource() + ".Lot.Update", new Model<Lot>(lotModel.getObject())) {
            @Override
            public void onClick() {
                setResponsePage(new ManageLotPage(getModelObject().getIdLot(), currentPage));
            }
        };
        updateLink.setVisibilityAllowed(lotService.updateOrdeleteLotEnabled(lotModel.getObject(), getSession()
                .getUtilisateur()));
        formView.add(updateLink);

        // Action : suppression
        Button deleteButton = new SubmittableButton(ACTION_DELETE, ManageLotPage.class, new SubmittableButtonEvents() {
            @Override
            public void onProcess() throws DataConstraintException {
                lotService.deleteLot(lotModel.getObject());
            }

            @Override
            public void onSuccess() {
                successNextPage(ManageLotPage.class, ACTION_DELETE);
                redirect();
            }
        });
        deleteButton.setVisibilityAllowed(lotService.updateOrdeleteLotEnabled(lotModel.getObject(), getSession()
                .getUtilisateur()));
        deleteButton.add(new JSConfirmationBehavior(getStringModel("Confirm")));
        deleteButton.setDefaultFormProcessing(false);
        formView.add(deleteButton);

        // Action : retour
        formView.add(new Link<Void>(getResource() + ".Lot.Back") {
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
