/*
 * #%L
 * Cantharella :: Web
 * $Id: TemplatePage.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.web/src/main/java/nc/ird/cantharella/web/pages/TemplatePage.java $
 * %%
 * Copyright (C) 2009 - 2013 IRD (Institut de Recherche pour le Developpement) and by respective authors (see below)
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
package nc.ird.cantharella.web.pages;

import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;

import nc.ird.cantharella.service.model.SearchBean;
import nc.ird.cantharella.utils.AssertTools;
import nc.ird.cantharella.utils.Pair;
import nc.ird.cantharella.web.pages.domain.campagne.ListCampagnesPage;
import nc.ird.cantharella.web.pages.domain.config.ListConfigurationPage;
import nc.ird.cantharella.web.pages.domain.extraction.ListExtractionsPage;
import nc.ird.cantharella.web.pages.domain.lot.ListLotsPage;
import nc.ird.cantharella.web.pages.domain.molecule.ListMoleculesPage;
import nc.ird.cantharella.web.pages.domain.personne.ListPersonnesPage;
import nc.ird.cantharella.web.pages.domain.purification.ListPurificationsPage;
import nc.ird.cantharella.web.pages.domain.search.SearchPage;
import nc.ird.cantharella.web.pages.domain.specimen.ListSpecimensPage;
import nc.ird.cantharella.web.pages.domain.station.ListStationsPage;
import nc.ird.cantharella.web.pages.domain.testBio.ListTestsBioPage;
import nc.ird.cantharella.web.pages.domain.utilisateur.UpdateUtilisateurPage;
import nc.ird.cantharella.web.utils.security.AuthContainer;
import nc.ird.cantharella.web.utils.security.AuthRole;
import nc.ird.cantharella.web.utils.security.AuthSession;

import org.apache.wicket.Application;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.IAjaxIndicatorAware;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.IHeaderContributor;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.value.ValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Template page
 * 
 * @author Mickael Tricot
 * @author Adrien Cheype
 */
public abstract class TemplatePage extends WebPage implements IAjaxIndicatorAware, IHeaderContributor {

    /** Action: logout */
    private static final String ACTION_LOGOUT = "Logout";

    /** Ajax indicator ID */
    private static final String ID_AJAX_INDICATOR = "AjaxIndicator";

    /** Logger */
    private static final Logger LOG = LoggerFactory.getLogger(TemplatePage.class);

    /** Pattern: enum value label (simpleClassName.valueString) */
    public static final String PATTERN_ENUM_VALUE_LABEL = "%s.%s";

    /** Pattern: exception error on action (resource.action.simpleClassName) */
    private static final String PATTERN_ERROR_ACTION_EXCEPTION = "%s.%s.%s";

    /** Pattern: message error (resource.KO) */
    private static final String PATTERN_ERROR_MESSAGE = "%s.KO";

    /** Pattern error property: "property message" */
    private static final String PATTERN_ERROR_MESSAGE_VALIDATOR = "%s - %s";

    /** Pattern: message success (resource.action.OK) */
    private static final String PATTERN_SUCCESS_ACTION_MESSAGE = "%s.%s.OK";

    /** Year */
    private static final String YEAR = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));

    /** Feedback panel container */
    private final MarkupContainer feedbackPanelContainer;

    /** Resource key */
    private final String resource;

    /**
     * Constructor
     * 
     * @param page Page (permet de construire la "resource" qui servira d'identifiant à la page)
     */
    protected TemplatePage(Class<? extends TemplatePage> page) {
        super(new PageParameters());
        AssertTools.assertNotNull(page);
        resource = page.getSimpleName();
        addPageTitles();
        add(new BookmarkablePageLink<Void>(HomePage.class.getSimpleName(), getApplication().getHomePage()));
        add(createUserSatellite());
        add(createUserSearch());
        add(createVisitorMenu());
        add(createUserMenu());
        createLanguageLinks();
        feedbackPanelContainer = new WebMarkupContainer("FeedbackPageContainer");
        feedbackPanelContainer.add(new FeedbackPanel("FeedbackPage"));
        feedbackPanelContainer.setOutputMarkupId(true);
        add(feedbackPanelContainer);
        add(new Label("Year", YEAR));
        add(new BookmarkablePageLink<Void>(ContactPage.class.getSimpleName(), ContactPage.class));
        add(new BookmarkablePageLink<Void>(ImprintPage.class.getSimpleName(), ImprintPage.class));
        setVersioned(true);

        // dev banner
        WebMarkupContainer bannerContainer = new WebMarkupContainer("TemplatePage.Banner");
        bannerContainer.setVisibilityAllowed(getApplication().getDebugSettings().isDevelopmentUtilitiesEnabled());
        add(bannerContainer);
    }

    /** {@inheritDoc} */
    public void renderHead(IHeaderResponse response) {
        // dynamic declaration of the menu Css due to the internationalization
        response.render(CssHeaderItem.forUrl(getString("TemplatePage.Css.Menu")));

        // scroll up to the anchor if any feedback message
        if (!getSession().getFeedbackMessages().isEmpty()) {
            response.render(OnDomReadyHeaderItem.forScript("location.hash='header'"));
            // LOG.debug("session FeedbackMessages is not empty");
            // for (FeedbackMessage msg : getSession().getFeedbackMessages()) {
            // LOG.debug(msg.getLevelAsString() + " : " + msg.getMessage() + ", from : " + msg.getReporter());
            // }
        }

        // colorbox (on doit le faire ici car sinon, jquery est
        // inclut après dans la page et ca ne fonctionne pas)
        response.render(JavaScriptHeaderItem.forReference(Application.get().getJavaScriptLibrarySettings()
                .getJQueryReference()));
        response.render(CssHeaderItem.forUrl("colorbox/colorbox.css"));
        response.render(JavaScriptHeaderItem.forUrl("colorbox/jquery.colorbox-min.js"));
        response.render(JavaScriptHeaderItem.forUrl("js/imagebox.js"));
    }

    /**
     * Création des liens vers les sélections de langue
     */
    private void createLanguageLinks() {
        this.add(new Link<Void>("SelectFrenchLang") {
            // Cas où on clique sur le drapeau français
            @Override
            public void onClick() {
                this.getSession().setLocale(Locale.FRENCH);
            }
        });
        this.add(new Link<Void>("SelectEnglishLang") {
            // Cas où on clique sur le drapeau anglais
            @Override
            public void onClick() {
                this.getSession().setLocale(Locale.ENGLISH);
            }
        });
    }

    /**
     * Création de la partie satellite spécifique à un utilisateur
     * 
     * @return Le conteneur
     */
    private AuthContainer createUserSatellite() {
        AuthContainer userSatellite = new AuthContainer("Satellite.USER", AuthRole.USER, AuthRole.ADMIN);
        if (userSatellite.isAuthorized()) {
            Link<Void> link = new BookmarkablePageLink<Void>(UpdateUtilisateurPage.class.getSimpleName(),
                    UpdateUtilisateurPage.class);

            link.add(new Label("Utilisateur", new PropertyModel<String>(this, "session.utilisateur")));
            userSatellite.add(link);
            userSatellite.add(new Link<Void>(TemplatePage.class.getSimpleName() + "." + ACTION_LOGOUT) {
                @Override
                public void onClick() {
                    ((AuthSession) getSession()).logout();
                    successNextPage(TemplatePage.class, ACTION_LOGOUT);
                    setResponsePage(getApplication().getHomePage());
                }
            });
        }
        return userSatellite;
    }

    /**
     * Création de la partie recherche spécifique à un utilisateur
     * 
     * @return Le conteneur
     */
    private AuthContainer createUserSearch() {
        AuthContainer userSatellite = new AuthContainer("Search.USER", AuthRole.USER, AuthRole.ADMIN);

        final Model<SearchBean> searchModel = Model.of(new SearchBean());
        Form<ValueMap> searchForm = new Form<ValueMap>("Search.FORM") {
            @Override
            protected void onSubmit() {
                setResponsePage(new SearchPage(searchModel.getObject()));
            }
        };
        searchForm.add(new TextField<String>("Search.query", new PropertyModel<String>(searchModel, "query")));

        userSatellite.add(searchForm);
        return userSatellite;
    }

    /**
     * Création du menu visiteur
     * 
     * @return Le conteneur
     */
    private AuthContainer createVisitorMenu() {
        AuthContainer visitorMenu = new AuthContainer("Menu.VISITOR", AuthRole.VISITOR);
        return visitorMenu;
    }

    /**
     * Création du menu utilisateur
     * 
     * @return Le conteneur
     */
    private AuthContainer createUserMenu() {
        AuthContainer userMenu = new AuthContainer("Menu.USER", AuthRole.USER, AuthRole.ADMIN);
        if (userMenu.isAuthorized()) {

            userMenu.add(createAdminMenu());

            userMenu.add(new BookmarkablePageLink<Void>(ListPersonnesPage.class.getSimpleName(),
                    ListPersonnesPage.class));
            userMenu.add(new BookmarkablePageLink<Void>(ListCampagnesPage.class.getSimpleName(),
                    ListCampagnesPage.class));
            userMenu.add(new BookmarkablePageLink<Void>(ListStationsPage.class.getSimpleName(), ListStationsPage.class));
            userMenu.add(new BookmarkablePageLink<Void>(ListLotsPage.class.getSimpleName(), ListLotsPage.class));
            userMenu.add(new BookmarkablePageLink<Void>(ListSpecimensPage.class.getSimpleName(),
                    ListSpecimensPage.class));
            userMenu.add(new BookmarkablePageLink<Void>(ListExtractionsPage.class.getSimpleName(),
                    ListExtractionsPage.class));
            userMenu.add(new BookmarkablePageLink<Void>(ListPurificationsPage.class.getSimpleName(),
                    ListPurificationsPage.class));
            userMenu.add(new BookmarkablePageLink<Void>(ListMoleculesPage.class.getSimpleName(),
                    ListMoleculesPage.class));
            userMenu.add(new BookmarkablePageLink<Void>(ListTestsBioPage.class.getSimpleName(), ListTestsBioPage.class));

            // userMenu.add(new BookmarkablePageLink<Void>(SandboxPage.class.getSimpleName(), SandboxPage.class));
        }
        return userMenu;
    }

    /**
     * Création du menu administrateur
     * 
     * @return Le conteneur
     */
    private AuthContainer createAdminMenu() {
        AuthContainer adminMenu = new AuthContainer("Menu.ADMIN", AuthRole.ADMIN);
        if (adminMenu.isAuthorized()) {
            adminMenu.add(new BookmarkablePageLink<Void>(ListConfigurationPage.class.getSimpleName(),
                    ListConfigurationPage.class));
        }
        return adminMenu;
    }

    /**
     * Add page titles (in head and body)
     */
    private void addPageTitles() {
        IModel<String> page = getStringModel(getResource());

        add(new Label("PageTitleHead", page));
        add(new Label("PageTitleBody", page));
    }

    /**
     * Convert validation errors (violations) into readable message, and display them
     * 
     * @param violations Violations
     */
    public final void addValidationErrors(Collection<String> violations) {
        for (String violation : violations) {
            error(violation);
        }
    }

    /**
     * Convert validation errors (violations) into readable message, and display them
     * 
     * @param violations Violations
     */
    public final void addValidationErrors(List<Pair<String, String>> violations) {
        for (Pair<String, String> violation : violations) {
            String label = violation.getKey();
            try {
                label = getString(label);
            } catch (MissingResourceException e) {
                //
            }
            error(String.format(PATTERN_ERROR_MESSAGE_VALIDATOR, label, violation.getValue()));
        }
    }

    /**
     * Display an error message (pageClass.action.Exception) on the current page
     * 
     * @param pageClass Page class
     * @param action Action
     * @param exception Exception
     */
    public final void errorCurrentPage(Class<? extends TemplatePage> pageClass, String action, Exception exception) {
        AssertTools.assertNotNull(pageClass);
        AssertTools.assertNotEmpty(action);
        AssertTools.assertNotNull(exception);
        String errorMessage = getString(String.format(PATTERN_ERROR_ACTION_EXCEPTION, pageClass.getSimpleName(),
                action, exception.getClass().getSimpleName()));
        LOG.warn(errorMessage);
        error(errorMessage);
    }

    /**
     * Display an error message (componentId.KO) on the current page
     * 
     * @param component Component
     */
    public final void errorCurrentPage(Component component) {
        AssertTools.assertNotNull(component);
        String errorMessage = String.format(PATTERN_ERROR_MESSAGE, component.getId());
        try {
            errorMessage = getString(errorMessage);
        } catch (MissingResourceException e) {
            //
        }
        LOG.warn(errorMessage);
        error(errorMessage);
    }

    /**
     * Display an error message (resource.action.Exception) on the current page
     * 
     * @param action Action
     * @param exception Exception
     */
    public final void errorCurrentPage(String action, Exception exception) {
        AssertTools.assertNotEmpty(action);
        AssertTools.assertNotNull(exception);
        String errorMessage = getString(String.format(PATTERN_ERROR_ACTION_EXCEPTION, getResource(), action, exception
                .getClass().getSimpleName()));
        LOG.warn(errorMessage);
        error(errorMessage);
    }

    /** {@inheritDoc} */
    @Override
    public final String getAjaxIndicatorMarkupId() {
        return ID_AJAX_INDICATOR;
    }

    /**
     * resource getter
     * 
     * @return resource
     */
    protected final String getResource() {
        return resource;
    }

    /** {@inheritDoc} */
    @Override
    public final AuthSession getSession() {
        return (AuthSession) super.getSession();
    }

    /**
     * Refresh feedback page and scroll up to the anchor if any feedback message
     * 
     * @param target Ajax target
     */
    public final void refreshFeedbackPage(AjaxRequestTarget target) {
        if (target != null) {
            target.add(feedbackPanelContainer);
            // scroll up to the anchor if any feedback message
            if (!getSession().getFeedbackMessages().isEmpty()) {
                LOG.debug("REFRESH FEEDBACK BY AJAX");
                target.appendJavaScript("window.location.hash = '#header';");
                // unless go back to button after scrolling up to the anchor
                target.focusComponent(feedbackPanelContainer);
            }
        }
    }

    /**
     * Display a success message on the current page (pageClass.action.OK)
     * 
     * @param pageClass Page class
     * @param action Action
     */
    public final void successCurrentPage(Class<? extends TemplatePage> pageClass, String action) {
        info(successMessage(pageClass, action));
    }

    /**
     * Display a success message on the current page (resource.action.OK)
     * 
     * @param action Action
     */
    public final void successCurrentPage(String action) {
        info(successMessage(action));
    }

    /**
     * Retrieve a success message (page.action.OK)
     * 
     * @param pageClass Page class
     * @param action Action
     * @return Success message
     */
    private String successMessage(Class<? extends TemplatePage> pageClass, String action) {
        AssertTools.assertNotNull(pageClass);
        AssertTools.assertNotEmpty(action);
        return getString(String.format(PATTERN_SUCCESS_ACTION_MESSAGE, pageClass.getSimpleName(), action));
    }

    /**
     * Retrieve a success message (resource.action.OK)
     * 
     * @param action Action
     * @return Success message
     */
    private String successMessage(String action) {
        AssertTools.assertNotEmpty(action);
        return getString(String.format(PATTERN_SUCCESS_ACTION_MESSAGE, getResource(), action));
    }

    /**
     * Display a success message on the next page (pageClass.action.OK)
     * 
     * @param pageClass Page class
     * @param action Action
     */
    public final void successNextPage(Class<? extends TemplatePage> pageClass, String action) {
        getSession().info(successMessage(pageClass, action));
    }

    /**
     * Display a success message on the next page (resource.action.OK)
     * 
     * @param action Action
     */
    public final void successNextPage(String action) {
        getSession().info(successMessage(action));
    }

    /**
     * Retrieve an enum value message (class.value). Null-safe methode
     * 
     * @param <E> Enum type
     * @param enumValue Enum value, null is the enumValue is null
     * @return Enum value message
     */
    public final <E extends Enum<?>> String enumValueMessage(E enumValue) {
        if (enumValue == null) {
            return null;
        }
        String enumValueMessage = String.format(PATTERN_ENUM_VALUE_LABEL, enumValue.getClass().getSimpleName(),
                enumValue.toString());

        return getString(enumValueMessage);
    }

    /**
     * Translation method, similar to {@link #getString(String)}, but return a model instead of a static String to fix
     * page reload in case of locale change.
     * 
     * @param key key to translate
     * @param parameters translation parameter
     * @return a model containing translation of {@code key}
     */
    public IModel<String> getStringModel(String key, Object... parameters) {
        return new StringResourceModel(key, this, null, parameters);
    }
}
