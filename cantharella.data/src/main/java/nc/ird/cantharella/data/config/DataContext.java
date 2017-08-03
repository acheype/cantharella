/*
 * #%L
 * Cantharella :: Data
 * $Id: DataContext.java 268 2014-05-06 15:45:40Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.data/src/main/java/nc/ird/cantharella/data/config/DataContext.java $
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
package nc.ird.cantharella.data.config;

import java.beans.PropertyVetoException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;

import nc.ird.cantharella.data.exceptions.UnexpectedException;
import nc.ird.cantharella.data.model.search.CantharellaAnalyzer;
import nc.ird.cantharella.data.validation.utils.ModelValidator;
import nc.ird.cantharella.data.validation.utils.ModelValidatorImpl;
import nc.ird.cantharella.utils.CantharellaConfig;

import org.hibernate.SessionFactory;
import org.hibernate.cache.ehcache.SingletonEhCacheRegionFactory;
import org.hibernate.cfg.Environment;
import org.hibernate.dialect.PostgreSQL82Dialect;
import org.nuiton.config.ArgumentsParserException;
import org.postgresql.Driver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.orm.hibernate4.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.mchange.v2.c3p0.impl.DefaultConnectionTester;

/**
 * Spring context for the data layer
 * <p>
 * DB_* properties can be placed in the properties file, if they vary depending on the environment.
 * </p>
 * 
 * @author Mickael Tricot
 * @author Adrien Cheype
 */
// Import the spring context file
@ImportResource(value = "classpath:/dataContext.xml")
// Scans for @Repository, @Service and @Component
@ComponentScan(basePackages = { "nc.ird.cantharella.data.dao", "nc.ird.cantharella.data.validation.utils" })
// Enable @Transactional support -> not work with <aop:aspectj-autoproxy /> in xml context file
@EnableTransactionManagement
// This is a configuration class
@Configuration
public abstract class DataContext {

    /**
     * Logger
     */
    private static final Logger LOG = LoggerFactory.getLogger(DataContext.class);

    /**
     * Country codes
     */
    public static final List<String> COUNTRY_CODES = Collections.unmodifiableList(Arrays.asList(Locale
            .getISOCountries()));

    /**
     * Language codes
     */
    public static final List<String> LANGUAGE_CODES = Collections.unmodifiableList(Arrays.asList(Locale
            .getISOLanguages()));

    /**
     * DB connection: acquire retry attemps
     */
    private static final int DB_CONNECTION_ACQUIRE_RETRY_ATTEMPS = 30;

    /**
     * DB connexion: acquire retry delay
     */
    private static final int DB_CONNECTION_ACQUIRE_RETRY_DELAY = 1000;

    /**
     * DB pool: min size
     */
    private static final int DB_POOL_MIN_SIZE = 3;

    /**
     * Encoding
     */
    public static final String ENCODING = Charset.forName("UTF-8").name();

    /**
     * Availables locales for the application
     */
    public static final List<Locale> LOCALES;

    /**
     * Precision is the total number of digits
     */
    public static final int DECIMAL_PRECISION = 9;

    /**
     * Scale is the number of digits to the right of the decimal point in a number. For example, the number 123.45 has a
     * precision of 5 and a scale of 2
     * 
     */
    public static final int DECIMAL_SCALE = 4;

    /**
     * According to DECIMAL_PRECISION and DECIMAL_SCALE, number max allowed for decimals. Attention to ensure that is
     * equal to 10^(DataContext.DECIMAL_PRECISION - DataContext.DECIMAL_SCALE) -1
     */
    public static final int DECIMAL_MAX = 99999;

    /**
     * Référentiels
     */
    public static final Map<Integer, String> REFERENTIELS;

    /**
     * DB debug
     */
    @Value("${db.debug}")
    protected boolean dbDebugProperty;

    /**
     * DB password
     */
    @Value("${db.password}")
    protected String dbPasswordProperty;

    /**
     * DB URL
     */
    @Value("${db.url}")
    protected String dbUrlProperty;

    /**
     * DB user
     */
    @Value("${db.user}")
    protected String dbUserProperty;

    /**
     * Hibernate schema validation property.
     */
    @Value("${db.hbm2ddl}")
    protected String hbm2ddl;

    /**
     * Hibernate search lucene index location on filesystem.
     */
    @Value("${hibernate.search.indexBase}")
    protected String hibernateSearchIndexBase;

    static {
        // The first Locale in the list is the default one
        List<Locale> locales = new ArrayList<Locale>();
        locales.add(Locale.FRENCH);
        locales.add(Locale.ENGLISH);

        LOCALES = Collections.unmodifiableList(locales);

        Map<Integer, String> referentiels = new HashMap<Integer, String>();
        referentiels.put(4326, "WGS84");
        REFERENTIELS = Collections.unmodifiableMap(referentiels);
    }

    /**
     * @return Internationalization messages for data layer
     */
    @Bean
    public MessageSourceAccessor dataMessageSource() {
        // The ResourceBundleMessageSource does not handle UTF-8, so we use the Reloadable one
        ReloadableResourceBundleMessageSource dataMessageSource = new ReloadableResourceBundleMessageSource();
        dataMessageSource.setBasename("data");
        dataMessageSource.setDefaultEncoding(ENCODING);
        dataMessageSource.setCacheSeconds(-1);
        return new MessageSourceAccessor(dataMessageSource);
    }

    /**
     * @return Data source (for DB connection)
     */
    @Bean
    public DataSource dataSource() {
        ComboPooledDataSource dataSource = new ComboPooledDataSource();
        // Connexion
        try {
            dataSource.setDriverClass(Driver.class.getName());
        } catch (PropertyVetoException e) {
            LOG.error(e.getMessage(), e);
            throw new UnexpectedException(e);
        }
        dataSource.setJdbcUrl(dbUrlProperty);
        dataSource.setPassword(dbPasswordProperty);
        dataSource.setUser(dbUserProperty);
        // Pool: basic configuration
        dataSource.setInitialPoolSize(DB_POOL_MIN_SIZE);
        // Pool: size and connection age
        dataSource.setMaxConnectionAge(0);
        dataSource.setMaxIdleTimeExcessConnections(0);
        // Pool: connection testing
        dataSource.setAutomaticTestTable(null);
        try {
            dataSource.setConnectionTesterClassName(DefaultConnectionTester.class.getName());
        } catch (PropertyVetoException e) {
            LOG.error(e.getMessage(), e);
            throw new UnexpectedException(e);
        }
        dataSource.setPreferredTestQuery(null);
        dataSource.setTestConnectionOnCheckin(false);
        dataSource.setTestConnectionOnCheckout(false);
        // Pool: statement pooling
        dataSource.setMaxStatementsPerConnection(0);
        // Pool: recovery from database outages
        dataSource.setAcquireRetryAttempts(DB_CONNECTION_ACQUIRE_RETRY_ATTEMPS);
        dataSource.setAcquireRetryDelay(DB_CONNECTION_ACQUIRE_RETRY_DELAY);
        dataSource.setBreakAfterAcquireFailure(false);
        // Pool: connection lifecycles with connection customizer
        dataSource.setConnectionCustomizerClassName(null);
        // Pool: unresolved transaction handling
        dataSource.setAutoCommitOnClose(false);
        dataSource.setForceIgnoreUnresolvedTransactions(false);
        // Pool: debug and workaround broken client applications
        dataSource.setDebugUnreturnedConnectionStackTraces(false);
        dataSource.setUnreturnedConnectionTimeout(0);
        // Pool: dataSource
        dataSource.setCheckoutTimeout(0);
        dataSource.setFactoryClassLocation(null);
        dataSource.setMaxAdministrativeTaskTime(0);
        dataSource.setUsesTraditionalReflectiveProxies(false);
        return dataSource;
    }

    /**
     * @return Session factory (for DB connections)
     */
    @Bean
    public SessionFactory sessionFactory() {
        LocalSessionFactoryBean sessionFactoryBean = new LocalSessionFactoryBean();
        sessionFactoryBean.setDataSource(dataSource());
        sessionFactoryBean.setPackagesToScan(new String[] { "nc.ird.cantharella.data.model" });
        Properties hibernateProperties = new Properties();
        // Hibernate: basic
        hibernateProperties.setProperty(Environment.DIALECT, PostgreSQL82Dialect.class.getName());
        //System.setProperty(Environment.BYTECODE_PROVIDER, "cglib");
        hibernateProperties.setProperty(Environment.HBM2DDL_AUTO, hbm2ddl);
        // hibernateProperties.setProperty("hibernate.hbm2ddl.auto", "create-drop");
        hibernateProperties.setProperty("hibernate.generate_statistics", String.valueOf(dbDebugProperty));
        hibernateProperties.setProperty("hibernate.cache.use_structured_entries", String.valueOf(dbDebugProperty));
        hibernateProperties.setProperty("hibernate.show_sql", String.valueOf(false));
        hibernateProperties.setProperty("hibernate.format_sql", String.valueOf(false));
        hibernateProperties.setProperty("hibernate.use_sql_comments", String.valueOf(dbDebugProperty));
        // Hibernate: cache
        hibernateProperties.setProperty("hibernate.cache.region.factory_class",
                SingletonEhCacheRegionFactory.class.getName());
        hibernateProperties.setProperty("hibernate.cache.use_query_cache", String.valueOf(true));
        hibernateProperties.setProperty("hibernate.cache.use_second_level_cache", String.valueOf(true));
        hibernateProperties.setProperty("hibernate.cache.use_structured_entries", String.valueOf(dbDebugProperty));
        // Hibernate: connection
        hibernateProperties.setProperty("hibernate.connection.autocommit", String.valueOf(false));
        hibernateProperties.setProperty("hibernate.connection.release_mode", "on_close");
        // Pool: c3p0 properties overrided by Hibernate
        // echatellier 20121129 : désactivé car la datasource est construite
        // à partir de c3p0, il ne faut donc pas que hibernate la gère
        // sur c3p0 en plus
        //hibernateProperties.setProperty(Environment.CONNECTION_PROVIDER, "org.hibernate.service.jdbc.connections.internal.C3P0ConnectionProvider");
        //hibernateProperties.setProperty("hibernate.c3p0.acquire_increment", String.valueOf(DB_POOL_ACQUIRE_INCREMENT)); // acquireIncrement
        //hibernateProperties.setProperty("hibernate.c3p0.idle_test_period", String.valueOf(0)); // idleConnectionTestPeriod
        //hibernateProperties.setProperty("hibernate.c3p0.timeout", String.valueOf(0)); // maxIdleTime
        //hibernateProperties.setProperty("hibernate.c3p0.max_size", String.valueOf(DB_POOL_MAX_SIZE)); // maxPoolSize
        //hibernateProperties.setProperty("hibernate.c3p0.max_statements", String.valueOf(0)); // maxStatements
        //hibernateProperties.setProperty("hibernate.c3p0.min_size", String.valueOf(DB_POOL_MIN_SIZE)); // minPoolSize
        // Hibernate search
        hibernateProperties.setProperty("hibernate.search.default.directory_provider", "filesystem");
        hibernateProperties.setProperty("hibernate.search.default.indexBase", hibernateSearchIndexBase);
        hibernateProperties.setProperty(org.hibernate.search.Environment.ANALYZER_CLASS,
                CantharellaAnalyzer.class.getName());
        hibernateProperties.setProperty(org.hibernate.search.Environment.ENABLE_DIRTY_CHECK, "false");
        hibernateProperties.setProperty(org.hibernate.search.Environment.LUCENE_MATCH_VERSION, "LUCENE_36");
        // Hibernate: Session
        //hibernateProperties.setProperty("hibernate.current_session_context_class", "thread");
        //hibernateProperties.setProperty(Environment.JTA_PLATFORM, "hibernate.transaction.factory_class", SpringTransactionFactory.class.getName());
        // TODO batch_size à ajuster
        hibernateProperties.setProperty("hibernate.default_batch_fetch_size", String.valueOf(20));

        sessionFactoryBean.setHibernateProperties(hibernateProperties);

        //Map<String, Object> eventListeners = new HashMap<String, Object>();
        //ModelValidatorEventListener validationListener = new ModelValidatorEventListener(validatorFactory());
        //eventListeners.put("pre-insert", validationListener);
        //eventListeners.put("pre-update", validationListener);
        //sessionFactoryBean.setEventListeners(eventListeners);

        try {
            sessionFactoryBean.afterPropertiesSet();
        } catch (Exception e) {
            throw new RuntimeException("SessionFactory misconfiguration", e);
        }
        return sessionFactoryBean.getObject();
    }

    /**
     * @return Transaction manager (for DB)
     */
    @Bean
    public HibernateTransactionManager transactionManager() {
        return new HibernateTransactionManager(sessionFactory());
    }

    /**
     * @return Validator factory
     */
    @Bean
    public ValidatorFactory validatorFactory() {
        // LocalValidatorFactoryBean validatorFactory = new LocalValidatorFactoryBean();
        // return validatorFactory;
        return Validation.buildDefaultValidatorFactory();
    }

    /**
     * @return A Validator for models of the data layer
     */
    @Bean
    public ModelValidator dataModelValidator() {
        return new ModelValidatorImpl(validatorFactory(), dataMessageSource());
    }

    /**
     * Set the data layer properties for the cantharella configuration
     * 
     * @return The placeholder configurer which get the data layer properties
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
}
