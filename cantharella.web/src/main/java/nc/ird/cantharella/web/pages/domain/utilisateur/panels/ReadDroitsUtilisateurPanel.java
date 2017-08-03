/*
 * #%L
 * Cantharella :: Web
 * $Id: ReadDroitsUtilisateurPanel.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.web/src/main/java/nc/ird/cantharella/web/pages/domain/utilisateur/panels/ReadDroitsUtilisateurPanel.java $
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
package nc.ird.cantharella.web.pages.domain.utilisateur.panels;

import java.util.ArrayList;
import java.util.List;

import nc.ird.cantharella.data.model.Campagne;
import nc.ird.cantharella.data.model.CampagnePersonneDroits;
import nc.ird.cantharella.data.model.Lot;
import nc.ird.cantharella.data.model.LotPersonneDroits;
import nc.ird.cantharella.data.model.Utilisateur;
import nc.ird.cantharella.data.model.comparators.CampagnePersonneDroitsComp;
import nc.ird.cantharella.data.model.comparators.LotPersonneDroitsComp;
import nc.ird.cantharella.web.pages.TemplatePage;
import nc.ird.cantharella.web.pages.domain.campagne.ReadCampagnePage;
import nc.ird.cantharella.web.pages.domain.lot.ReadLotPage;
import nc.ird.cantharella.web.utils.CallerPage;
import nc.ird.cantharella.web.utils.panels.PropertyLabelLinkPanel;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;

import edu.emory.mathcs.backport.java.util.Collections;

/**
 * Panneau de consultation des droits d'un utilisateur
 * 
 * @author Adrien Cheype
 */
public final class ReadDroitsUtilisateurPanel extends Panel {

    /**
     * Constructeur
     * 
     * @param id Id du composant
     * @param utilisateurModel Modèle de l'utilisateur concerné
     */
    public ReadDroitsUtilisateurPanel(String id, final IModel<? extends Utilisateur> utilisateurModel) {
        super(id, utilisateurModel);

        // Gestion des campagnes et lots autorisés
        final MarkupContainer autorisationsContainer = new WebMarkupContainer("Authorizations.Table") {
            @Override
            public boolean isVisible() {
                return utilisateurModel.getObject().getCampagnesDroits().size() != 0
                        || utilisateurModel.getObject().getLotsDroits().size() != 0;
            }
        };
        autorisationsContainer.setOutputMarkupId(true);

        final LoadableDetachableModel<List<CampagnePersonneDroits>> campagnesModel = new LoadableDetachableModel<List<CampagnePersonneDroits>>() {
            @Override
            protected List<CampagnePersonneDroits> load() {
                List<CampagnePersonneDroits> listCampagnesDroits = new ArrayList<CampagnePersonneDroits>(
                        utilisateurModel.getObject().getCampagnesDroits().values());

                // tri pour affichage
                Collections.sort(listCampagnesDroits, new CampagnePersonneDroitsComp());
                return listCampagnesDroits;
            }
        };

        // Liste des campagnes autorisées
        autorisationsContainer
                .add(new ListView<CampagnePersonneDroits>("Authorizations.Campagnes.List", campagnesModel) {
                    @Override
                    protected void populateItem(final ListItem<CampagnePersonneDroits> item) {
                        if (item.getIndex() % 2 == 1) {
                            item.add(new AttributeModifier("class", item.getIndex() % 2 == 0 ? "even" : "odd"));
                        }

                        item.add(new PropertyLabelLinkPanel<Campagne>("Authorizations.Campagnes.Campagne.nom",
                                new PropertyModel<Campagne>(item.getModel(), "id.pk1"), new StringResourceModel("Read",
                                        this, null)) {
                            @Override
                            public void onClick() {
                                setResponsePage(new ReadCampagnePage(getModelObject().getIdCampagne(), new CallerPage(
                                        (TemplatePage) getPage())));
                            }
                        });
                    }
                });

        // Liste des lots autorisés
        final LoadableDetachableModel<List<LotPersonneDroits>> lotsModel = new LoadableDetachableModel<List<LotPersonneDroits>>() {
            protected List<LotPersonneDroits> load() {
                List<LotPersonneDroits> listLotsDroits = new ArrayList<LotPersonneDroits>(utilisateurModel.getObject()
                        .getLotsDroits().values());
                // tri pour affichage
                Collections.sort(listLotsDroits, new LotPersonneDroitsComp());
                return listLotsDroits;
            }
        };
        autorisationsContainer.add(new ListView<LotPersonneDroits>("Authorizations.Lots.List", lotsModel) {
            @Override
            protected void populateItem(ListItem<LotPersonneDroits> item) {
                if (item.getIndex() % 2 == 1) {
                    item.add(new AttributeModifier("class", item.getIndex() % 2 == 0 ? "even" : "odd"));
                }

                item.add(new PropertyLabelLinkPanel<Campagne>("Authorizations.Lots.Campagne.nom",
                        new PropertyModel<Campagne>(item.getModel(), "id.pk1.campagne"), new StringResourceModel(
                                "Read", this, null)) {
                    @Override
                    public void onClick() {
                        setResponsePage(new ReadCampagnePage(getModelObject().getIdCampagne(), new CallerPage(
                                (TemplatePage) getPage())));
                    }
                });

                item.add(new PropertyLabelLinkPanel<Lot>("Authorizations.Lots.Lot.ref", new PropertyModel<Lot>(item
                        .getModel(), "id.pk1"), new StringResourceModel("Read", this, null)) {
                    @Override
                    public void onClick() {
                        setResponsePage(new ReadLotPage(getModelObject().getIdLot(), new CallerPage(
                                (TemplatePage) getPage())));
                    }
                });
            }
        });
        add(autorisationsContainer);

        // Selon la non existence d'elements dans la table on affiche le span pour remplacer
        MarkupContainer emptyCampagnesContainer = new WebMarkupContainer("Authorizations.emptyTable") {
            @Override
            public boolean isVisible() {
                return !autorisationsContainer.isVisible();
            }
        };
        add(emptyCampagnesContainer);
    }
}
