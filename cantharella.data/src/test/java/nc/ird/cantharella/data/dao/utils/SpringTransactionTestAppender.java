/*
 * #%L
 * Cantharella :: Data
 * $Id: SpringTransactionTestAppender.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.data/src/test/java/nc/ird/cantharella/data/dao/utils/SpringTransactionTestAppender.java $
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
package nc.ird.cantharella.data.dao.utils;

import org.springframework.transaction.interceptor.TransactionInterceptor;

/**
 * Log4J appender optimalized to check Spring transaction operations being processed.
 * 
 * @author Jan Novotný, FG Forrest a.s. (c) 2007
 */
public class SpringTransactionTestAppender extends TestAppender {

    /**
     * Constructor
     */
    public SpringTransactionTestAppender() {
        super(TransactionInterceptor.class);
    }

    /**
     * @param forClass -
     * @param forMethod -
     * @return -
     */
    public boolean isTransactionOpened(@SuppressWarnings("rawtypes") Class forClass, String forMethod) {
        return containsExactSingleLogRecord("Getting transaction for [" + forClass.getName() + "." + forMethod + "]");
    }

    /**
     * @param forClass -
     * @param forMethod -
     * @return -
     */
    public boolean isTransactionCompleted(@SuppressWarnings("rawtypes") Class forClass, String forMethod) {
        return containsExactSingleLogRecord("Completing transaction for [" + forClass.getName() + "." + forMethod + "]");
    }

    /**
     * @param forClass -
     * @param forMethod -
     * @return -
     */
    public boolean isTransactionRollbacked(@SuppressWarnings("rawtypes") Class forClass, String forMethod) {
        return containsSingleLogRecord("Completing transaction for [" + forClass.getName() + "." + forMethod
                + "] after exception");
    }

}