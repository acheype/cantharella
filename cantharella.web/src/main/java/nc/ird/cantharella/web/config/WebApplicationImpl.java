/*
 * #%L
 * Cantharella :: Web
 * $Id: WebApplicationImpl.java 268 2014-05-06 15:45:40Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.web/src/main/java/nc/ird/cantharella/web/config/WebApplicationImpl.java $
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
package nc.ird.cantharella.web.config;

import java.math.BigDecimal;

import nc.ird.cantharella.data.config.DataContext;
import nc.ird.cantharella.data.exceptions.DataConstraintException;
import nc.ird.cantharella.data.exceptions.UnexpectedException;
import nc.ird.cantharella.service.services.PersonneService;
import nc.ird.cantharella.utils.AssertTools;
import nc.ird.cantharella.web.pages.ContactPage;
import nc.ird.cantharella.web.pages.HomePage;
import nc.ird.cantharella.web.pages.ImprintPage;
import nc.ird.cantharella.web.pages.domain.campagne.ListCampagnesPage;
import nc.ird.cantharella.web.pages.domain.campagne.ManageCampagnePage;
import nc.ird.cantharella.web.pages.domain.campagne.ReadCampagnePage;
import nc.ird.cantharella.web.pages.domain.config.ListConfigurationPage;
import nc.ird.cantharella.web.pages.domain.document.ManageDocumentPage;
import nc.ird.cantharella.web.pages.domain.document.ReadDocumentPage;
import nc.ird.cantharella.web.pages.domain.extraction.ListExtractionsPage;
import nc.ird.cantharella.web.pages.domain.extraction.ManageExtractionPage;
import nc.ird.cantharella.web.pages.domain.extraction.ReadExtractionPage;
import nc.ird.cantharella.web.pages.domain.lot.ListLotsPage;
import nc.ird.cantharella.web.pages.domain.lot.ManageLotPage;
import nc.ird.cantharella.web.pages.domain.lot.ReadLotPage;
import nc.ird.cantharella.web.pages.domain.molecule.ListMoleculesPage;
import nc.ird.cantharella.web.pages.domain.molecule.ManageMoleculePage;
import nc.ird.cantharella.web.pages.domain.molecule.ReadMoleculePage;
import nc.ird.cantharella.web.pages.domain.personne.ListPersonnesPage;
import nc.ird.cantharella.web.pages.domain.personne.ManagePersonnePage;
import nc.ird.cantharella.web.pages.domain.personne.ReadPersonnePage;
import nc.ird.cantharella.web.pages.domain.purification.ListPurificationsPage;
import nc.ird.cantharella.web.pages.domain.purification.ManagePurificationPage;
import nc.ird.cantharella.web.pages.domain.purification.ReadPurificationPage;
import nc.ird.cantharella.web.pages.domain.search.SearchPage;
import nc.ird.cantharella.web.pages.domain.specimen.ListSpecimensPage;
import nc.ird.cantharella.web.pages.domain.specimen.ManageSpecimenPage;
import nc.ird.cantharella.web.pages.domain.specimen.ReadSpecimenPage;
import nc.ird.cantharella.web.pages.domain.station.ListStationsPage;
import nc.ird.cantharella.web.pages.domain.station.ManageStationPage;
import nc.ird.cantharella.web.pages.domain.station.ReadStationPage;
import nc.ird.cantharella.web.pages.domain.testBio.ListTestsBioPage;
import nc.ird.cantharella.web.pages.domain.testBio.ManageTestBioPage;
import nc.ird.cantharella.web.pages.domain.testBio.ReadTestBioPage;
import nc.ird.cantharella.web.pages.domain.utilisateur.ManageUtilisateurPage;
import nc.ird.cantharella.web.pages.domain.utilisateur.RegisterPage;
import nc.ird.cantharella.web.pages.domain.utilisateur.ResetPasswordPage;
import nc.ird.cantharella.web.pages.domain.utilisateur.UpdateUtilisateurPage;
import nc.ird.cantharella.web.pages.errors.AccessDeniedPage;
import nc.ird.cantharella.web.pages.errors.InternalErrorPage;
import nc.ird.cantharella.web.pages.errors.PageExpiredErrorPage;
import nc.ird.cantharella.web.utils.converters.BigDecimalConverterImpl;
import nc.ird.cantharella.web.utils.converters.DoubleConverterImpl;
import nc.ird.cantharella.web.utils.security.AuthSession;
import nc.ird.cantharella.web.utils.security.AuthStrategy;

import org.apache.wicket.ConverterLocator;
import org.apache.wicket.IConverterLocator;
import org.apache.wicket.RuntimeConfigurationType;
import org.apache.wicket.Session;
import org.apache.wicket.authentication.IAuthenticationStrategy;
import org.apache.wicket.authentication.strategy.DefaultAuthenticationStrategy;
import org.apache.wicket.core.request.mapper.MountedMapper;
import org.apache.wicket.core.request.mapper.PackageMapper;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.javascript.DefaultJavaScriptCompressor;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.Response;
import org.apache.wicket.request.mapper.mount.MountMapper;
import org.apache.wicket.resource.NoOpTextCompressor;
import org.apache.wicket.resource.loader.IStringResourceLoader;
import org.apache.wicket.settings.IExceptionSettings;
import org.apache.wicket.settings.IRequestCycleSettings.RenderStrategy;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.apache.wicket.util.cookies.CookieUtils;
import org.apache.wicket.util.lang.PackageName;
import org.apache.wicket.util.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

/**
 * Web application
 * 
 * @author Mickael Tricot
 * @author Adrien Cheype
 */
public final class WebApplicationImpl extends WebApplication {

    /** Logger */
    private static final Logger LOG = LoggerFactory.getLogger(WebApplicationImpl.class);

    /**
     * SpringBeans injector (some objects such as Pages are automatically injected, by not all of them)
     * 
     * @param object Object to be injected
     */
    public static void injectSpringBeans(Object object) {
        Injector.get().inject(object);
    }

    /** Debug mode */
    private final boolean debug;

    /** Internationalization messages */
    private final IStringResourceLoader[] messages;

    /** Optimize mode */
    private final boolean optimize;

    /** Service: personne */
    @SpringBean
    private PersonneService personneService;

    /** Wicket configuration (Application.DEVELOPMENT or Application.DEPLOYMENT) */
    private final RuntimeConfigurationType wicketConfiguration;

    /** Configuration document max upload size. */
    @Value("${document.maxUploadSize}")
    protected long documentMaxUploadSize;

    /** Configuration document's allowed extensions. */
    @Value("${document.extension.allowed}")
    protected String documentExtensionAllowed;

    /**
     * Constructor
     * 
     * @param debug Debug mode
     * @param optimize Optimize mode
     * @param wicketConfiguration Wicket configuration (DEVELOPMENT or DEPLOYMENT)
     * @param messages Internationalization messages
     */
    public WebApplicationImpl(boolean debug, boolean optimize, RuntimeConfigurationType wicketConfiguration,
            IStringResourceLoader... messages) {
        AssertTools.assertArrayNotNull(messages);
        this.debug = debug;
        this.optimize = optimize;
        this.wicketConfiguration = wicketConfiguration;
        this.messages = messages;
    }

    /** {@inheritDoc} */
    @Override
    public RuntimeConfigurationType getConfigurationType() {
        return wicketConfiguration;
    }

    /** {@inheritDoc} */
    @Override
    public Class<HomePage> getHomePage() {
        return HomePage.class;
    }

    /** {@inheritDoc} */
    @Override
    protected void init() {
        super.init();
        getComponentInstantiationListeners().add(new SpringComponentInjector(this));
        injectSpringBeans(this);
        try {
            personneService.checkOrCreateAdmin();
        } catch (DataConstraintException e) {
            LOG.error(e.getMessage(), e);
            throw new UnexpectedException(e);
        }
        setApplicationSettings();
        setDebugSettings();
        setExceptionSettings();
        setFrameworkSettings();
        setMarkupSettings();
        setPageSettings();
        setRequestCycleSettings();
        setRequestLoggerSettings();
        setResourceSettings();
        setSecuritySettings();

        mountUrls();
    }

    /** {@inheritDoc} */
    @Override
    protected IConverterLocator newConverterLocator() {
        ConverterLocator converterLocator = (ConverterLocator) super.newConverterLocator();
        converterLocator.set(Double.class, DoubleConverterImpl.INSTANCE);
        converterLocator.set(BigDecimal.class, BigDecimalConverterImpl.INSTANCE);
        return converterLocator;
    }

    /** {@inheritDoc} */
    @Override
    public Session newSession(Request request, Response response) {
        return new AuthSession(request);
    }

    /**
     * Application settings
     */
    private void setApplicationSettings() {
        getApplicationSettings().setAccessDeniedPage(AccessDeniedPage.class);
        getApplicationSettings().setDefaultMaximumUploadSize(WebContext.HTTP_MAXIMUM_UPLOAD);
        getApplicationSettings().setInternalErrorPage(InternalErrorPage.class);
        getApplicationSettings().setPageExpiredErrorPage(PageExpiredErrorPage.class);
    }

    /**
     * Debug settings
     */
    private void setDebugSettings() {
        getDebugSettings().setAjaxDebugModeEnabled(debug);
        getDebugSettings().setComponentUseCheck(debug);
        getDebugSettings().setDevelopmentUtilitiesEnabled(debug);
        getDebugSettings().setLinePreciseReportingOnAddComponentEnabled(debug);
        getDebugSettings().setLinePreciseReportingOnNewComponentEnabled(debug);
        getDebugSettings().setOutputComponentPath(debug);
        getDebugSettings().setOutputMarkupContainerClassName(debug);
    }

    /**
     * Exception settings
     */
    private void setExceptionSettings() {
        getExceptionSettings().setUnexpectedExceptionDisplay(
                debug ? IExceptionSettings.SHOW_EXCEPTION_PAGE : IExceptionSettings.SHOW_INTERNAL_ERROR_PAGE);
    }

    /**
     * Framework settings
     */
    private void setFrameworkSettings() {
        getFrameworkSettings().setDetachListener(null);
    }

    /**
     * Markup settings
     */
    private void setMarkupSettings() {
        getMarkupSettings().setAutomaticLinking(false);
        getMarkupSettings().setCompressWhitespace(optimize);
        getMarkupSettings().setDefaultAfterDisabledLink("</strike");
        getMarkupSettings().setDefaultBeforeDisabledLink("<strike>");
        getMarkupSettings().setDefaultMarkupEncoding(DataContext.ENCODING);
        // getMarkupSettings().setMarkupCache(???);
        // getMarkupSettings().setMarkupParserFactory(???);
        getMarkupSettings().setStripComments(optimize);
        getMarkupSettings().setStripWicketTags(true);

        getMarkupSettings().setThrowExceptionOnMissingXmlDeclaration(false);
    }

    /**
     * Page settings
     */
    private void setPageSettings() {
        // getPageSettings().addComponentResolver(???);
        getPageSettings().setVersionPagesByDefault(true);
    }

    /**
     * Request cycle settings
     */
    private void setRequestCycleSettings() {
        // getRequestCycleSettings().addResponseFilter(???);
        getRequestCycleSettings().setBufferResponse(true);
        getRequestCycleSettings().setGatherExtendedBrowserInfo(false);
        getRequestCycleSettings().setRenderStrategy(RenderStrategy.REDIRECT_TO_BUFFER);
        getRequestCycleSettings().setResponseRequestEncoding(DataContext.ENCODING);
        getRequestCycleSettings().setTimeout(WebContext.HTTP_TIME_OUT);
    }

    /**
     * Request logger settings
     */
    private void setRequestLoggerSettings() {
        getRequestLoggerSettings().setRecordSessionSize(debug);
        getRequestLoggerSettings().setRequestLoggerEnabled(debug);
        getRequestLoggerSettings().setRequestsWindowSize(WebContext.HTTP_REQUEST_LOGGER_WINDOW_SIZE);
    }

    /**
     * Resource settings
     */
    private void setResourceSettings() {
        for (IStringResourceLoader message : messages) {
            getResourceSettings().getStringResourceLoaders().add(message);
        }
        getResourceSettings().setDefaultCacheDuration(optimize ? WebContext.HTTP_CACHE_DURATION : Duration.NONE);

        getResourceSettings().setJavaScriptCompressor(optimize ? new DefaultJavaScriptCompressor() : null);

        getResourceSettings().setCssCompressor(optimize ? new NoOpTextCompressor() : null);

        getResourceSettings().setParentFolderPlaceholder(null);
        getResourceSettings().setResourcePollFrequency(null);

        getResourceSettings().setThrowExceptionOnMissingResource(true);
        getResourceSettings().setUseDefaultOnMissingResource(!debug);
    }

    /**
     * Security settings
     */
    private void setSecuritySettings() {
        getSecuritySettings().setAuthorizationStrategy(new AuthStrategy());

        // Customize the cookie age for the authentification stategy
        IAuthenticationStrategy authStrategy = new DefaultAuthenticationStrategy(WebContext.AUTH_COOKIE_KEY) {

            /** Cookie utils with custom settings */
            private CookieUtils cookieUtils;

            protected CookieUtils getCookieUtils() {
                if (cookieUtils == null) {
                    cookieUtils = new CookieUtils();
                    cookieUtils.getSettings().setMaxAge(WebContext.AUTH_COOKIE_MAX_AGE);
                }
                return cookieUtils;
            }
        };
        getSecuritySettings().setAuthenticationStrategy(authStrategy);

        getSecuritySettings().setEnforceMounts(false);
    }

    /**
     * Configure the mapping of urls
     */
    private void mountUrls() {

        getRootRequestMapperAsCompound().add(new MountedMapper("/campagne/list", ListCampagnesPage.class));
        mountPage("/campagne/list", ListCampagnesPage.class);
        getRootRequestMapperAsCompound().add(new MountedMapper("/campagne/edit", ManageCampagnePage.class));
        mountPage("/campagne/edit", ManageCampagnePage.class);
        getRootRequestMapperAsCompound().add(new MountedMapper("/campagne/view", ReadCampagnePage.class));
        mountPage("/campagne/view", ReadCampagnePage.class);

        getRootRequestMapperAsCompound().add(
                new MountMapper("/config", new PackageMapper(PackageName.forClass(ListConfigurationPage.class))));

        getRootRequestMapperAsCompound().add(new MountedMapper("/extraction/list", ListExtractionsPage.class));
        mountPage("/extraction/list", ListExtractionsPage.class);
        getRootRequestMapperAsCompound().add(new MountedMapper("/extraction/edit", ManageExtractionPage.class));
        mountPage("/extraction/edit", ManageExtractionPage.class);
        getRootRequestMapperAsCompound().add(new MountedMapper("/extraction/view", ReadExtractionPage.class));
        mountPage("/extraction/view", ReadExtractionPage.class);

        getRootRequestMapperAsCompound().add(new MountedMapper("/lot/list", ListLotsPage.class));
        mountPage("/lot/list", ListLotsPage.class);
        getRootRequestMapperAsCompound().add(new MountedMapper("/lot/edit", ManageLotPage.class));
        mountPage("/lot/edit", ManageLotPage.class);
        getRootRequestMapperAsCompound().add(new MountedMapper("/lot/view", ReadLotPage.class));
        mountPage("/lot/view", ReadLotPage.class);

        getRootRequestMapperAsCompound().add(new MountedMapper("/personne/list", ListPersonnesPage.class));
        mountPage("/personne/list", ListPersonnesPage.class);
        getRootRequestMapperAsCompound().add(new MountedMapper("/personne/edit", ManagePersonnePage.class));
        mountPage("/personne/edit", ManagePersonnePage.class);
        getRootRequestMapperAsCompound().add(new MountedMapper("/personne/view", ReadPersonnePage.class));
        mountPage("/personne/view", ReadPersonnePage.class);

        getRootRequestMapperAsCompound().add(new MountedMapper("/purification/list", ListPurificationsPage.class));
        mountPage("/purification/list", ListPurificationsPage.class);
        getRootRequestMapperAsCompound().add(new MountedMapper("/purification/edit", ManagePurificationPage.class));
        mountPage("/purification/edit", ManagePurificationPage.class);
        getRootRequestMapperAsCompound().add(new MountedMapper("/purification/view", ReadPurificationPage.class));
        mountPage("/purification/view", ReadPurificationPage.class);

        getRootRequestMapperAsCompound().add(new MountedMapper("/specimen/list", ListSpecimensPage.class));
        mountPage("/specimen/list", ListSpecimensPage.class);
        getRootRequestMapperAsCompound().add(new MountedMapper("/specimen/edit", ManageSpecimenPage.class));
        mountPage("/specimen/edit", ManageSpecimenPage.class);
        getRootRequestMapperAsCompound().add(new MountedMapper("/specimen/view", ReadSpecimenPage.class));
        mountPage("/specimen/view", ReadSpecimenPage.class);

        getRootRequestMapperAsCompound().add(new MountedMapper("/station/list", ListStationsPage.class));
        mountPage("/station/list", ListStationsPage.class);
        getRootRequestMapperAsCompound().add(new MountedMapper("/station/edit", ManageStationPage.class));
        mountPage("/station/edit", ManageStationPage.class);
        getRootRequestMapperAsCompound().add(new MountedMapper("/station/view", ReadStationPage.class));
        mountPage("/station/view", ReadStationPage.class);

        getRootRequestMapperAsCompound().add(new MountedMapper("/molecule/list", ListMoleculesPage.class));
        mountPage("/molecule/list", ListMoleculesPage.class);
        getRootRequestMapperAsCompound().add(new MountedMapper("/molecule/edit", ManageMoleculePage.class));
        mountPage("/molecule/edit", ManageMoleculePage.class);
        getRootRequestMapperAsCompound().add(new MountedMapper("/molecule/view", ReadMoleculePage.class));
        mountPage("/molecule/view", ReadMoleculePage.class);

        getRootRequestMapperAsCompound().add(new MountedMapper("/testBio/list", ListTestsBioPage.class));
        mountPage("/testBio/list", ListTestsBioPage.class);
        getRootRequestMapperAsCompound().add(new MountedMapper("/testBio/edit", ManageTestBioPage.class));
        mountPage("/testBio/edit", ManageTestBioPage.class);
        getRootRequestMapperAsCompound().add(new MountedMapper("/testBio/view", ReadTestBioPage.class));
        mountPage("/testBio/view", ReadTestBioPage.class);

        getRootRequestMapperAsCompound().add(new MountedMapper("/document/edit", ManageDocumentPage.class));
        mountPage("/document/edit", ManageDocumentPage.class);
        getRootRequestMapperAsCompound().add(new MountedMapper("/document/view", ReadDocumentPage.class));
        mountPage("/document/view", ReadDocumentPage.class);

        getRootRequestMapperAsCompound().add(new MountedMapper("/utilisateur/edit", ManageUtilisateurPage.class));
        mountPage("/utilisateur/edit", ManageUtilisateurPage.class);
        getRootRequestMapperAsCompound().add(new MountedMapper("/utilisateur/update", UpdateUtilisateurPage.class));
        mountPage("/utilisateur/update", UpdateUtilisateurPage.class);

        getRootRequestMapperAsCompound().add(new MountedMapper("/contact", ContactPage.class));
        mountPage("/contact", ContactPage.class);
        getRootRequestMapperAsCompound().add(new MountedMapper("/imprint", ImprintPage.class));
        mountPage("/imprint", ImprintPage.class);
        getRootRequestMapperAsCompound().add(new MountedMapper("/register", RegisterPage.class));
        mountPage("/register", RegisterPage.class);
        getRootRequestMapperAsCompound().add(new MountedMapper("/passwordLost", ResetPasswordPage.class));
        mountPage("/passwordLost", ResetPasswordPage.class);

        getRootRequestMapperAsCompound().add(new MountedMapper("/search", SearchPage.class));
        mountPage("/search", SearchPage.class);
    }

    /**
     * Get document max upload size.
     * 
     * @return document max upload size
     */
    public long getDocumentMaxUploadSize() {
        return documentMaxUploadSize;
    }

    /**
     * Get document allowed extension.
     * 
     * @return document allowed extension
     */
    public String getDocumentExtensionAllowed() {
        return documentExtensionAllowed;
    }
}