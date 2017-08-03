package nc.ird.cantharella.data;

/*
 * #%L
 * Cantharella :: Data
 * $Id: SchemaExporter.java 267 2014-05-06 15:39:05Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.data/src/test/java/nc/ird/cantharella/data/SchemaExporter.java $
 * %%
 * Copyright (C) 2012 - 2013 IRD (Institut de Recherche pour le Developpement) and by respective authors (see below)
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

import java.io.IOException;
import java.util.Properties;

import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.dialect.PostgreSQL9Dialect;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.junit.Test;
import org.springframework.orm.hibernate4.LocalSessionFactoryBean;

/**
 * Schema export test.
 * 
 * @author Eric Chatellier
 */
public class SchemaExporter {

    /**
     * Export schema creation script.
     * 
     * @throws IOException
     */
    @Test
    public void exportSchema() throws IOException {
        LocalSessionFactoryBean sessionFactoryBean = new LocalSessionFactoryBean();
        sessionFactoryBean.setPackagesToScan(new String[] { "nc.ird.cantharella.data.model" });
        Properties hibernateProperties = new Properties();
        // Hibernate: basic
        hibernateProperties.setProperty(Environment.DIALECT, PostgreSQL9Dialect.class.getName());
        hibernateProperties.setProperty(Environment.SHOW_SQL, "false");
        sessionFactoryBean.setHibernateProperties(hibernateProperties);
        sessionFactoryBean.afterPropertiesSet();
        Configuration configuration = sessionFactoryBean.getConfiguration();
        SchemaExport schemaExport = new SchemaExport(configuration);
        schemaExport.create(true, false);
    }
}
