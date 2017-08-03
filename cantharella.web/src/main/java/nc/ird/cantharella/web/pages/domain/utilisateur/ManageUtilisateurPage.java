/*
 * #%L
 * Cantharella :: Web
 * $Id: ManageUtilisateurPage.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.web/src/main/java/nc/ird/cantharella/web/pages/domain/utilisateur/ManageUtilisateurPage.java $
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
package nc.ird.cantharella.web.pages.domain.utilisateur;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import nc.ird.cantharella.data.exceptions.DataConstraintException;
import nc.ird.cantharella.data.exceptions.DataNotFoundException;
import nc.ird.cantharella.data.exceptions.UnexpectedException;
import nc.ird.cantharella.data.model.Campagne;
import nc.ird.cantharella.data.model.CampagnePersonneDroits;
import nc.ird.cantharella.data.model.Lot;
import nc.ird.cantharella.data.model.LotPersonneDroits;
import nc.ird.cantharella.data.model.Utilisateur;
import nc.ird.cantharella.data.model.Utilisateur.TypeDroit;
import nc.ird.cantharella.data.validation.utils.ModelValidator;
import nc.ird.cantharella.service.exceptions.EmailException;
import nc.ird.cantharella.service.services.CampagneService;
import nc.ird.cantharella.service.services.LotService;
import nc.ird.cantharella.service.services.PersonneService;
import nc.ird.cantharella.utils.BeanTools;
import nc.ird.cantharella.utils.BeanTools.AccessType;
import nc.ird.cantharella.utils.CollectionTools;
import nc.ird.cantharella.web.pages.TemplatePage;
import nc.ird.cantharella.web.pages.domain.document.panel.ManageListDocumentsPanel;
import nc.ird.cantharella.web.pages.domain.personne.panels.ManagePersonnePanel;
import nc.ird.cantharella.web.pages.model.ManageUtilisateurModel;
import nc.ird.cantharella.web.utils.CallerPage;
import nc.ird.cantharella.web.utils.behaviors.JSConfirmationBehavior;
import nc.ird.cantharella.web.utils.forms.SubmittableButton;
import nc.ird.cantharella.web.utils.forms.SubmittableButtonEvents;
import nc.ird.cantharella.web.utils.renderers.EnumChoiceRenderer;
import nc.ird.cantharella.web.utils.security.AuthRole;
import nc.ird.cantharella.web.utils.security.AuthRoles;

import org.apache.commons.collections.comparators.ComparatorChain;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxFallbackButton;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.AbstractSingleSelectChoice;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RadioChoice;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Gestion d'un utilisateur par un admin
 * 
 * @author Mickael Tricot
 * @author Adrien Cheype
 */
@AuthRoles({ AuthRole.ADMIN })
public final class ManageUtilisateurPage extends TemplatePage {

    /** Action : delete */
    private static final String ACTION_DELETE = "Delete";

    /** Action : reject */
    private static final String ACTION_REJECT = "Reject";

    /** Action : update */
    private static final String ACTION_UPDATE = "Update";

    /** Action : validate */
    private static final String ACTION_VALID = "Valid";

    /** Logger */
    private static final Logger LOG = LoggerFactory.getLogger(ManageUtilisateurPage.class);

    /** Ajout d'une autorisation sur une campagne ou un lot */
    private final Button addAuthorization;

    /** Campagnes disponibles pour ajouter des droits */
    private final AbstractSingleSelectChoice<Campagne> availableCampagnes;

    /** Lots disponibles pour ajouter des droits */
    private final AbstractSingleSelectChoice<Lot> availableLots;

    /** Modèle : droits sur une campagne, en cours de saisie */
    private final IModel<CampagnePersonneDroits> campagnePersonneDroitsModel;

    /** Campagnes */
    private final List<Campagne> campagnes;

    /** Service : campagne */
    @SpringBean
    private CampagneService campagneService;

    /** Service : lot */
    @SpringBean
    private LotService lotService;

    /** Modèle : droits sur un lot, en cours de saisie */
    private final IModel<LotPersonneDroits> lotsPersonneDroitsModel;

    /** Modèle : campagne en cours de sélection */
    private final IModel<ManageUtilisateurModel> manageUtilisateurModel;

    /** Service : personne */
    @SpringBean
    private PersonneService personneService;

    /** Modèle : utilisateur */
    private final IModel<Utilisateur> utilisateurModel;

    /** Validateur modèle */
    @SpringBean(name = "webModelValidator")
    private ModelValidator validator;

    /**
     * Constructeur
     * 
     * @param idPersonne Identifiant utilisateur
     * @param callerPage Page appelante (pour redirection)
     */
    public ManageUtilisateurPage(Integer idPersonne, final CallerPage callerPage) {
        super(ManageUtilisateurPage.class);
        final CallerPage currentPage = new CallerPage(this);

        // Initialisation des modèles
        try {
            utilisateurModel = new Model<Utilisateur>(personneService.loadUtilisateur(idPersonne));
        } catch (DataNotFoundException e) {
            LOG.error(e.getMessage(), e);
            throw new UnexpectedException(e);
        }
        manageUtilisateurModel = new Model<ManageUtilisateurModel>(new ManageUtilisateurModel());

        manageUtilisateurModel.getObject().setCampagnesDroits(utilisateurModel.getObject().getCampagnesDroits());
        manageUtilisateurModel.getObject().getCampagnes()
                .addAll(utilisateurModel.getObject().getCampagnesDroits().keySet());
        manageUtilisateurModel.getObject().setLotsDroits(utilisateurModel.getObject().getLotsDroits());
        manageUtilisateurModel.getObject().getLots().addAll(utilisateurModel.getObject().getLotsDroits().keySet());

        campagnePersonneDroitsModel = new Model<CampagnePersonneDroits>(new CampagnePersonneDroits());
        campagnePersonneDroitsModel.getObject().getId().setPk2(utilisateurModel.getObject());
        campagnePersonneDroitsModel.getObject().getDroits().setDroitExtrait(Boolean.TRUE);
        campagnePersonneDroitsModel.getObject().getDroits().setDroitPuri(Boolean.TRUE);
        campagnePersonneDroitsModel.getObject().getDroits().setDroitRecolte(Boolean.TRUE);
        campagnePersonneDroitsModel.getObject().getDroits().setDroitTestBio(Boolean.TRUE);
        lotsPersonneDroitsModel = new Model<LotPersonneDroits>(new LotPersonneDroits());
        lotsPersonneDroitsModel.getObject().getId().setPk2(utilisateurModel.getObject());
        lotsPersonneDroitsModel.getObject().getDroits().setDroitExtrait(Boolean.TRUE);
        lotsPersonneDroitsModel.getObject().getDroits().setDroitPuri(Boolean.TRUE);
        lotsPersonneDroitsModel.getObject().getDroits().setDroitRecolte(Boolean.TRUE);
        lotsPersonneDroitsModel.getObject().getDroits().setDroitTestBio(Boolean.TRUE);

        // Initialisation des listes
        campagnes = Collections.unmodifiableList(campagneService.listCampagnes(getSession().getUtilisateur()));

        final Form<Void> formView = new Form<Void>("Form");

        final ManagePersonnePanel personnePanel = new ManagePersonnePanel("ManagePersonnePanel", utilisateurModel);
        formView.add(personnePanel);

        formView.add(new RadioChoice<TypeDroit>("Utilisateur.typeDroit", new PropertyModel<TypeDroit>(utilisateurModel,
                "typeDroit"), Arrays.asList(TypeDroit.values()), new EnumChoiceRenderer<TypeDroit>(this)));

        // Gestion des campagnes et lots autorisés
        final MarkupContainer campagnesContainer = new WebMarkupContainer(getResource()
                + ".Authorizations.Campagnes.Table");
        campagnesContainer.setOutputMarkupId(true);

        // Liste des campagnes autorisées
        campagnesContainer.add(new ListView<Campagne>(getResource() + ".Authorizations.Campagnes.List",
                manageUtilisateurModel.getObject().getCampagnes()) {
            @Override
            protected void populateItem(final ListItem<Campagne> item) {
                if (item.getIndex() % 2 == 1) {
                    item.add(new AttributeModifier("class", item.getIndex() % 2 == 0 ? "even" : "odd"));
                }

                final Campagne campagne = item.getModelObject();
                item.add(new Label(getResource() + ".Authorizations.Campagnes.Campagne.nom", campagne.getNom()));
                // Bouton de suppression des droits sur une campagne
                AjaxFallbackButton deleteAuthorization = new AjaxFallbackButton(
                        "ManageUtilisateurPage.Authorizations.Campagnes.Delete", formView) {
                    @Override
                    protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                        // Suppression
                        manageUtilisateurModel.getObject().getCampagnesDroits().remove(campagne);
                        // Mise à jour des listes
                        updateAvailableCampagnes();
                        if (target != null) {
                            target.add(campagnesContainer);
                        }
                    }

                    @Override
                    protected void onError(AjaxRequestTarget target, Form<?> form) {
                        // never called
                    }

                };
                deleteAuthorization.setDefaultFormProcessing(false);
                item.add(deleteAuthorization);
            }
        });

        // Liste des lots autorisés
        campagnesContainer.add(new ListView<Lot>(getResource() + ".Authorizations.Lots.List", manageUtilisateurModel
                .getObject().getLots()) {
            @Override
            protected void populateItem(ListItem<Lot> item) {
                if (item.getIndex() % 2 == 1) {
                    item.add(new AttributeModifier("class", item.getIndex() % 2 == 0 ? "even" : "odd"));
                }

                final Lot lot = item.getModelObject();
                item.add(new Label(getResource() + ".Authorizations.Lots.Campagne.nom", lot.getCampagne().getNom()));
                item.add(new Label(getResource() + ".Authorizations.Lots.Lot.ref", lot.getRef()));
                // Bouton de suppression des droits sur un lot
                AjaxFallbackButton deleteAuthorization = new AjaxFallbackButton(
                        "ManageUtilisateurPage.Authorizations.Lots.Delete", formView) {
                    @Override
                    protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                        // Suppression
                        manageUtilisateurModel.getObject().getLotsDroits().remove(lot);
                        // Mise à jour des listes
                        updateAvailableCampagnes();
                        if (target != null) {
                            target.add(campagnesContainer);
                        }
                    }

                    @Override
                    protected void onError(AjaxRequestTarget target, Form<?> form) {
                        // never called
                    }

                };
                deleteAuthorization.setDefaultFormProcessing(false);
                item.add(deleteAuthorization);
            }
        });

        // Liste des campagnes disponibles (pour ajouter les droits)
        availableCampagnes = new DropDownChoice<Campagne>(getResource() + ".Authorizations.Campagnes.Campagne",
                new PropertyModel<Campagne>(campagnePersonneDroitsModel, "id.pk1"), new ArrayList<Campagne>());
        availableCampagnes.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                // Mise à jour des listes lorsqu'une campagne a été sélectionnée
                updateAvailableCampagnes();
                target.add(availableLots);
            }
        });
        campagnesContainer.add(availableCampagnes);

        // Liste des lots disponibles (pour ajouter les droits)
        availableLots = new DropDownChoice<Lot>(getResource() + ".Authorizations.Campagnes.Lot",
                new PropertyModel<Lot>(lotsPersonneDroitsModel, "id.pk1"), new ArrayList<Lot>());
        availableLots.setOutputMarkupId(true);
        campagnesContainer.add(availableLots);

        // Ajout de droits sur une campagne ou un lot
        addAuthorization = new AjaxFallbackButton(getResource() + ".Authorizations.Campagnes.Add", formView) {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                if (campagnePersonneDroitsModel.getObject().getId().getPk1() != null) {
                    if (lotsPersonneDroitsModel.getObject().getId().getPk1() == null) {
                        // Ajout de droits sur une campagne
                        CampagnePersonneDroits droits;
                        try {
                            droits = campagnePersonneDroitsModel.getObject().clone();
                        } catch (CloneNotSupportedException e) {
                            LOG.error(e.getMessage(), e);
                            throw new UnexpectedException(e);
                        }
                        // Recharge la campagne pour éviter une LazyLoadingException
                        Campagne campagne = droits.getId().getPk1();
                        campagneService.refreshCampagne(campagne);
                        manageUtilisateurModel.getObject().getCampagnesDroits().put(campagne, droits);
                        campagne.getPersonnesDroits().add(droits);

                        // puisque droits sur Campagne-Tous, suppression des Campagne-Lot de cette campagne
                        // Se base sur le réf de lot plutôt que l'objet pour être indépendant du contexte de persistance
                        // d'où provient l'entité
                        CollectionTools.removeAllWithValue(manageUtilisateurModel.getObject().getLotsDroits().keySet(),
                                "ref", AccessType.GETTER, CollectionTools.valuesFromList(droits.getId().getPk1()
                                        .getLots(), "ref", AccessType.GETTER));

                    } else {
                        // Ajout de droits sur un lot
                        LotPersonneDroits droits;
                        try {
                            droits = lotsPersonneDroitsModel.getObject().clone();
                        } catch (CloneNotSupportedException e) {
                            LOG.error(e.getMessage(), e);
                            throw new UnexpectedException(e);
                        }
                        Lot lot = droits.getId().getPk1();
                        // Evite une LazyLoadingException car la campagne n'est plus dans la session
                        lotService.refreshLot(lot);
                        manageUtilisateurModel.getObject().getLotsDroits().put(lot, droits);
                        lot.getPersonnesDroits().add(droits);
                    }
                    // Mise à jour des listes
                    updateAvailableCampagnes();
                    if (target != null) {
                        target.add(campagnesContainer);
                    }
                }
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                refreshFeedbackPage(target);
            }

        };
        campagnesContainer.add(addAuthorization);

        updateAvailableCampagnes();
        formView.add(campagnesContainer);

        // add list document panel
        ManageListDocumentsPanel manageListDocumentsPanel = new ManageListDocumentsPanel("ManageListDocumentsPanel",
                utilisateurModel, currentPage);
        formView.add(manageListDocumentsPanel);

        // Action : mise à jour de l'utilisateur
        Button updateButton = new SubmittableButton(ACTION_UPDATE, new SubmittableButtonEvents() {
            @Override
            public void onProcess() throws DataConstraintException {
                personneService.updateUtilisateur(utilisateurModel.getObject(), true);
            }

            @Override
            public void onSuccess() {
                successNextPage(ACTION_UPDATE);
                callerPage.responsePage((TemplatePage) getPage());
            }

            @Override
            public void onValidate() {
                utilisateurModel.getObject()
                        .setCampagnesDroits(manageUtilisateurModel.getObject().getCampagnesDroits());
                utilisateurModel.getObject().setLotsDroits(manageUtilisateurModel.getObject().getLotsDroits());
                personnePanel.validate();
                validator.validate(utilisateurModel.getObject(), getSession().getLocale(), "typeDroit");
            }
        });
        updateButton.setVisibilityAllowed(utilisateurEstValide());
        formView.add(updateButton);

        // Action validation de l'utilisateur
        Button validButton = new SubmittableButton(ACTION_VALID, new SubmittableButtonEvents() {
            @Override
            public void onProcess() throws DataConstraintException {
                personneService.validAndUpdateUtilisateur(utilisateurModel.getObject());
            }

            @Override
            public void onSuccess() {
                successNextPage(ACTION_VALID);
                callerPage.responsePage((TemplatePage) getPage());
            }

            @Override
            public void onValidate() {
                utilisateurModel.getObject()
                        .setCampagnesDroits(manageUtilisateurModel.getObject().getCampagnesDroits());
                utilisateurModel.getObject().setLotsDroits(manageUtilisateurModel.getObject().getLotsDroits());
                personnePanel.validate();
                validator.validate(utilisateurModel.getObject(), getSession().getLocale(), "typeDroit");
            }
        });
        validButton.setVisibilityAllowed(!utilisateurEstValide());
        formView.add(validButton);

        // Action : suppression de l'utilisateur
        Button deleteButton = new SubmittableButton(ACTION_DELETE, new SubmittableButtonEvents() {
            @Override
            public void onProcess() throws DataConstraintException, EmailException {
                personneService.deleteUtilisateur(utilisateurModel.getObject());
            }

            @Override
            public void onSuccess() {
                successNextPage(ACTION_DELETE);
                callerPage.responsePage((TemplatePage) getPage());
            }
        });
        // impossible de supprimer son propre utilisateur
        deleteButton.setVisibilityAllowed(utilisateurEstValide()
                && getSession().getUtilisateur().getIdPersonne() != utilisateurModel.getObject().getIdPersonne());
        deleteButton.setDefaultFormProcessing(false);
        deleteButton.add(new JSConfirmationBehavior(getStringModel("Confirm")));
        formView.add(deleteButton);

        // Action : rejet de l'utilisateur
        Button rejectButton = new SubmittableButton(ACTION_REJECT, new SubmittableButtonEvents() {
            @Override
            public void onProcess() throws DataConstraintException {
                personneService.rejectUtilisateur(utilisateurModel.getObject());
            }

            @Override
            public void onSuccess() {
                successNextPage(ACTION_REJECT);
                callerPage.responsePage((TemplatePage) getPage());
            }
        });
        rejectButton.setVisibilityAllowed(!utilisateurEstValide());
        rejectButton.setDefaultFormProcessing(false);
        rejectButton.add(new JSConfirmationBehavior(getStringModel("Confirm")));
        formView.add(rejectButton);

        // Action : annulation
        formView.add(new Link<Void>("Cancel") {
            @Override
            public void onClick() {
                callerPage.responsePage((TemplatePage) getPage());
            }
        });

        add(formView);
    }

    /**
     * Mets à jour les campagnes et lots disponibles
     */
    @SuppressWarnings("unchecked")
    private void updateAvailableCampagnes() {
        CollectionTools.setter(manageUtilisateurModel.getObject().getCampagnes(), manageUtilisateurModel.getObject()
                .getCampagnesDroits().keySet());
        // tri pour affichage
        Collections.sort(manageUtilisateurModel.getObject().getCampagnes(), BeanTools.createPropertyComparator("nom"));

        CollectionTools.setter(manageUtilisateurModel.getObject().getLots(), manageUtilisateurModel.getObject()
                .getLotsDroits().keySet());
        // tri pour affichage
        ComparatorChain campagneComp = new ComparatorChain(BeanTools.createPropertyComparator("campagne.nom"));
        campagneComp.addComparator(BeanTools.createPropertyComparator("ref"));
        Collections.sort(manageUtilisateurModel.getObject().getLots(), campagneComp);

        // Campagnes
        List<Campagne> campagnesList = new ArrayList<Campagne>(campagnes);
        // Supprime les campagnes dont l'utilisateur a déjà les droits
        // Se base sur le nom de campagne plutôt que l'objet pour être indépendant du contexte de persistance d'où
        // provient l'entité
        CollectionTools.removeAllWithValue(campagnesList, "nom", AccessType.GETTER, CollectionTools.valuesFromList(
                manageUtilisateurModel.getObject().getCampagnes(), "nom", AccessType.GETTER));
        // campagnesList.removeAll(manageUtilisateurModel.getObject().getCampagnes());
        availableCampagnes.setChoices(campagnesList);
        campagnePersonneDroitsModel.getObject().getId()
                .setPk1(campagnesList.isEmpty() ? null : availableCampagnes.getModelObject());
        availableCampagnes.setEnabled(!campagnesList.isEmpty());

        // Lots
        List<Lot> lotsList = new ArrayList<Lot>();
        availableLots.setEnabled(campagnePersonneDroitsModel.getObject().getId().getPk1() != null);
        if (availableLots.isEnabled()) {
            // Evite une LazyLoadingException car la campagne n'est plus dans la session
            campagneService.refreshCampagne(campagnePersonneDroitsModel.getObject().getId().getPk1());
            lotsList.addAll(campagnePersonneDroitsModel.getObject().getId().getPk1().getLots());
            // Supprime les lots pour lequels l'utilisateur a déjà les droits
            // Se base sur le réf de lot plutôt que l'objet pour être indépendant du contexte de persistance d'où
            // provient l'entité
            CollectionTools.removeAllWithValue(lotsList, "ref", AccessType.GETTER, CollectionTools.valuesFromList(
                    manageUtilisateurModel.getObject().getLots(), "ref", AccessType.GETTER));

            availableLots.setChoices(lotsList);
        }
        lotsPersonneDroitsModel.getObject().getId().setPk1(null);

        addAuthorization.setEnabled(!campagnesList.isEmpty());
    }

    /**
     * L'utilisateur est-il valide?
     * 
     * @return Validité
     */
    private boolean utilisateurEstValide() {
        return utilisateurModel.getObject().isValide();
    }
}
