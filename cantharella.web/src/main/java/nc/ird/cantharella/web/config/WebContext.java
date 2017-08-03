/*
 * #%L
 * Cantharella :: Web
 * $Id: WebContext.java 268 2014-05-06 15:45:40Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.web/src/main/java/nc/ird/cantharella/web/config/WebContext.java $
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

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.validation.ValidatorFactory;

import nc.ird.cantharella.data.config.DataContext;
import nc.ird.cantharella.data.exceptions.UnexpectedException;
import nc.ird.cantharella.data.validation.utils.ModelValidator;
import nc.ird.cantharella.data.validation.utils.ModelValidatorImpl;
import nc.ird.cantharella.service.config.ServiceContext;
import nc.ird.cantharella.utils.CantharellaConfig;
import nc.ird.cantharella.utils.Pair;
import nc.ird.cantharella.utils.StringTools;
import nc.ird.cantharella.web.utils.resources.WebMessages;

import org.apache.wicket.RuntimeConfigurationType;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.util.lang.Bytes;
import org.apache.wicket.util.time.Duration;
import org.nuiton.config.ArgumentsParserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.util.Log4jConfigurer;

/**
 * Spring context for the web layer
 * 
 * @author Mickael Tricot
 * @author Adrien Cheype
 */
// Load the service-layer configuration
@Import(ServiceContext.class)
// This is a configuration class
@Configuration
public abstract class WebContext {

    /**
     * Logger
     */
    private static final Logger LOG = LoggerFactory.getLogger(WebContext.class);

    /**
     * Cookie key for authentification
     */
    public static final String AUTH_COOKIE_KEY = "LoggedIn";

    /**
     * Cookie max age for authentification
     */
    public static final int AUTH_COOKIE_MAX_AGE = (int) Duration.days(1).seconds();

    /**
     * Countries: Locale -> country code + country name (sorted by country name)
     */
    public static final Map<Locale, Map<String, String>> COUNTRIES;

    /**
     * Country codes: Locale -> country code (sorted by country name)
     */
    public static final Map<Locale, List<String>> COUNTRY_CODES;

    /**
     * Countries: Locale -> language code + language name (sorted by language name)
     */
    public static final Map<Locale, Map<String, String>> LANGUAGES;

    /**
     * Country codes: Locale -> language code (sorted by country name)
     */
    public static final Map<Locale, List<String>> LANGUAGE_CODES;

    /**
     * HTTP cache duration
     */
    public static final Duration HTTP_CACHE_DURATION = Duration.days(1);

    /**
     * HTTP maximum upload
     */
    public static final Bytes HTTP_MAXIMUM_UPLOAD = Bytes.megabytes(1);

    /**
     * HTTP request logger window size
     */
    public static final int HTTP_REQUEST_LOGGER_WINDOW_SIZE = 2000;

    /**
     * HTTP session max page maps
     */
    public static final int HTTP_SESSION_MAX_PAGE_MAPS = 5;

    /**
     * HTTP time out
     */
    public static final Duration HTTP_TIME_OUT = Duration.seconds(30);

    /**
     * Référentiels codes
     */
    public static final List<Integer> REFERENTIEL_CODES = new ArrayList<Integer>(DataContext.REFERENTIELS.keySet());

    /**
     * Percent format precision
     */
    public static final int PERCENT_PRECISION = 2;

    /**
     * The maximum number of digits allowed in the fraction portion of doubles *
     */
    public static final int DOUBLE_MAX_FRACTION_DIGIT = 3;

    /**
     * The maximum rows number displayed for a list page
     */
    public static final int ROWS_PER_PAGE = 20;

    /**
     * Message source for data layer
     */
    @Resource(name = "dataMessageSource")
    private MessageSourceAccessor dataMessageSource;

    /**
     * Validator factory of the data layer
     */
    @Resource(name = "validatorFactory")
    private ValidatorFactory validatorFactory;

    /**
     * Debug mode
     */
    @Value("${app.debug}")
    protected boolean appDebugProperty;

    /**
     * Optimize mode
     */
    @Value("${app.optimize}")
    protected boolean appOptimizeProperty;

    /**
     * Wicket configuration string ("development" or "deployment")
     */
    @Value("${wicket.configuration}")
    protected String wicketConfiguration;

    /**
     * Log4j configuration filepath
     */
    @Value("${log4j.config}")
    protected String log4jConfig;

    static {
        Map<Locale, Map<String, String>> countries = new HashMap<Locale, Map<String, String>>(
                DataContext.LOCALES.size());
        Map<Locale, Map<String, String>> languages = new HashMap<Locale, Map<String, String>>(
                DataContext.LOCALES.size());
        Map<Locale, List<String>> countryCodes = new HashMap<Locale, List<String>>(DataContext.LOCALES.size());
        Map<Locale, List<String>> languageCodes = new HashMap<Locale, List<String>>(DataContext.LOCALES.size());
        for (Locale locale : DataContext.LOCALES) {
            // Build (country code + country name) list
            List<Pair<String, Pair<String, String>>> sortedCountryList = new ArrayList<Pair<String, Pair<String, String>>>(
                    DataContext.COUNTRY_CODES.size());
            for (String countryCode : DataContext.COUNTRY_CODES) {
                String countryName = new Locale(locale.getLanguage(), countryCode).getDisplayCountry(locale);
                sortedCountryList.add(new Pair<String, Pair<String, String>>(countryCode, new Pair<String, String>(
                        StringTools.replaceAccents(countryName), countryName)));
            }
            // Build (country code + language name) list
            List<Pair<String, Pair<String, String>>> sortedLanguageList = new ArrayList<Pair<String, Pair<String, String>>>(
                    DataContext.LANGUAGE_CODES.size());
            for (String languageCode : DataContext.LANGUAGE_CODES) {
                String languageName = new Locale(languageCode, locale.getCountry()).getDisplayLanguage(locale);
                sortedLanguageList.add(new Pair<String, Pair<String, String>>(languageCode, new Pair<String, String>(
                        StringTools.replaceAccents(languageName), languageName)));
            }
            // Sort by name (country code + country name) list
            Collections.sort(sortedCountryList, new Comparator<Pair<String, Pair<String, String>>>() {
                @Override
                public int compare(Pair<String, Pair<String, String>> pair1, Pair<String, Pair<String, String>> pair2) {
                    return String.CASE_INSENSITIVE_ORDER.compare(pair1.getValue().getKey(), pair2.getValue().getKey());
                }
            });
            Collections.sort(sortedLanguageList, new Comparator<Pair<String, Pair<String, String>>>() {
                @Override
                public int compare(Pair<String, Pair<String, String>> pair1, Pair<String, Pair<String, String>> pair2) {
                    return String.CASE_INSENSITIVE_ORDER.compare(pair1.getValue().getKey(), pair2.getValue().getKey());
                }
            });
            // Build (locale -> (country code + country name)) map (sorted by name)
            // Build (locale -> (country code)) map (sorted by name)
            Map<String, String> sortedCountryMap = new LinkedHashMap<String, String>(sortedCountryList.size());
            List<String> sortedCountryCodesList = new ArrayList<String>(sortedCountryList.size());
            for (Pair<String, Pair<String, String>> countryPair : sortedCountryList) {
                sortedCountryMap.put(countryPair.getKey(), countryPair.getValue().getValue());
                sortedCountryCodesList.add(countryPair.getKey());
            }
            countries.put(locale, Collections.unmodifiableMap(sortedCountryMap));
            countryCodes.put(locale, Collections.unmodifiableList(sortedCountryCodesList));
            // Build (locale -> (language code + language name)) map (sorted by name)
            // Build (locale -> (language code)) map (sorted by name)
            Map<String, String> sortedLanguageMap = new LinkedHashMap<String, String>(sortedCountryList.size());
            List<String> sortedLanguageCodesList = new ArrayList<String>(sortedCountryList.size());
            for (Pair<String, Pair<String, String>> languagePair : sortedLanguageList) {
                sortedLanguageMap.put(languagePair.getKey(), languagePair.getValue().getValue());
                sortedLanguageCodesList.add(languagePair.getKey());
            }
            languages.put(locale, Collections.unmodifiableMap(sortedLanguageMap));
            languageCodes.put(locale, Collections.unmodifiableList(sortedLanguageCodesList));
        }
        COUNTRIES = Collections.unmodifiableMap(countries);
        COUNTRY_CODES = Collections.unmodifiableMap(countryCodes);
        LANGUAGES = Collections.unmodifiableMap(languages);
        LANGUAGE_CODES = Collections.unmodifiableMap(languageCodes);
    }

    /**
     * @return Internationalization messages for web layer
     */
    @Bean
    public MessageSourceAccessor webMessageSource() {
        // The ResourceBundleMessageSource does not handle UTF-8, so we use the
        // Reloadable one
        ReloadableResourceBundleMessageSource source = new ReloadableResourceBundleMessageSource();
        source.setBasename("web");
        source.setDefaultEncoding(DataContext.ENCODING);
        source.setCacheSeconds(-1);
        return new MessageSourceAccessor(source);
    }

    /**
     * @return A Validator for models of the data & web layers
     */
    @Bean
    public ModelValidator webModelValidator() {
        return new ModelValidatorImpl(validatorFactory, webMessageSource(), dataMessageSource);
    }

    /**
     * @return Wicket application
     */
    @Bean
    public WebApplication webApplication() {
        RuntimeConfigurationType runtimeWicketConf = RuntimeConfigurationType
                .valueOf(wicketConfiguration.toUpperCase());
        return new WebApplicationImpl(appDebugProperty, appOptimizeProperty, runtimeWicketConf, new WebMessages(
                dataMessageSource), new WebMessages(webMessageSource()));
    }

    /**
     * Set the web layer properties for the cantharella configuration
     * 
     * @return The placeholder configurer which get the web layer properties
     */
    @Bean
    public static PropertySourcesPlaceholderConfigurer properties() {
        PropertySourcesPlaceholderConfigurer pspc = new PropertySourcesPlaceholderConfigurer();
        try {
            pspc.setProperties(CantharellaConfig.getProperties());
        } catch (ArgumentsParserException e) {
            LOG.error(e.getMessage(), e);
            throw new UnexpectedException(e);
        }
        pspc.setIgnoreUnresolvablePlaceholders(true);
        return pspc;
    }

    /**
     * Init the log4j configuration filepath
     * 
     * @throws FileNotFoundException If the config file is not found
     */
    @PostConstruct
    public void initLog4j() throws FileNotFoundException {
        Log4jConfigurer.initLogging(log4jConfig);
    }
}
