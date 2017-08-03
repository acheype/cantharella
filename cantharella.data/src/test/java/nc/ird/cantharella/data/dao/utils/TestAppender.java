/*
 * #%L
 * Cantharella :: Data
 * $Id: TestAppender.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.data/src/test/java/nc/ird/cantharella/data/dao/utils/TestAppender.java $
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

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;
import org.slf4j.LoggerFactory;

/**
 * Base test appender used to capture logging events for test purposes.
 * 
 * @author Jan Novotný
 * @version $Id: TestAppender.java 269 2014-05-07 08:14:00Z echatellier $
 */
public class TestAppender extends AppenderSkeleton {
    /** Logger */
    private static org.slf4j.Logger log = LoggerFactory.getLogger(TestAppender.class);

    /**  */
    @SuppressWarnings("rawtypes")
    private final Class[] monitoredClasses;

    /**  */
    private final LoggerInfo[] backedUpLoggers;

    /**  */
    private final List<LoggingEvent> events = new ArrayList<LoggingEvent>();

    /**
     * Constructor
     * 
     * @param monitoredClass -
     */
    public TestAppender(@SuppressWarnings("rawtypes") Class monitoredClass) {
        this(new Class[] { monitoredClass });
    }

    /**
     * Constructor
     * 
     * @param monitoredClasses -
     */
    public TestAppender(@SuppressWarnings("rawtypes") Class[] monitoredClasses) {
        super();
        this.monitoredClasses = monitoredClasses;
        if (log.isInfoEnabled()) {
            log.info("***************************************************************");
            log.info("        APPENDING TEST CONTROLLED LOGGING ENVIRONMENT          ");
            log.info("***************************************************************");
        }
        backedUpLoggers = new LoggerInfo[monitoredClasses.length];
        for (int i = 0; i < monitoredClasses.length; i++) {
            @SuppressWarnings("rawtypes")
            Class monitoredClass = monitoredClasses[i];
            Logger logger = LogManager.getLogger(monitoredClass);
            backedUpLoggers[i] = new LoggerInfo(logger.getLevel(), logger.getAdditivity());
            logger.setLevel(Level.TRACE);
            logger.addAppender(this);
            logger.setAdditivity(true);
        }
    }

    /**
     * 
     */
    public void clearLogChanges() {
        for (int i = 0; i < monitoredClasses.length; i++) {
            @SuppressWarnings("rawtypes")
            Class monitoredClass = monitoredClasses[i];
            Logger logger = LogManager.getLogger(monitoredClass);
            logger.setLevel(backedUpLoggers[i].getOriginalLevel());
            logger.setAdditivity(backedUpLoggers[i].isOriginalAdditivity());
            logger.removeAppender(this);
        }
        if (log.isInfoEnabled()) {
            log.info("***************************************************************");
            log.info("         REMOVED TEST CONTROLLED LOGGING ENVIRONMENT           ");
            log.info("***************************************************************");
        }
    }

    /** {@inheritDoc} */
    @Override
    protected void append(LoggingEvent event) {
        synchronized (events) {
            events.add(event);
            System.out.println(">>> Capturing : " + event.getMessage());
        }
    }

    /**
     * @param messagePart -
     * @return -
     */
    public int countLogRecord(String messagePart) {
        int counter = 0;
        for (LoggingEvent event : events) {
            String message = (String) event.getMessage();
            if (message != null && message.indexOf(messagePart) > -1) {
                counter++;
            }
        }
        return counter;
    }

    /**
     * @param comparedMessage -
     * @return -
     */
    public int countExactLogRecord(String comparedMessage) {
        int counter = 0;
        for (LoggingEvent event : events) {
            String message = (String) event.getMessage();
            if (message != null && message.equals(comparedMessage)) {
                counter++;
            }
        }
        return counter;
    }

    /**
     * @param messagePart -
     * @return -
     */
    public boolean containsLogRecord(String messagePart) {
        return countLogRecord(messagePart) > 0;
    }

    /**
     * @param messagePart -
     * @return -
     */
    public boolean containsSingleLogRecord(String messagePart) {
        return countLogRecord(messagePart) == 1;
    }

    /**
     * @param completeMessage -
     * @return -
     */
    public boolean containsExactLogRecord(String completeMessage) {
        return countExactLogRecord(completeMessage) > 0;
    }

    /**
     * @param completeMessage -
     * @return -
     */
    public boolean containsExactSingleLogRecord(String completeMessage) {
        return countExactLogRecord(completeMessage) == 1;
    }

    /** {@inheritDoc} */
    @Override
    public void close() {
        events.clear();
        clearLogChanges();
    }

    /** {@inheritDoc} */
    @Override
    public boolean requiresLayout() {
        return false;
    }

    /**
     * @author Jan Novotný
     */
    private class LoggerInfo {
        /**  */
        Level originalLevel;

        /**  */
        boolean originalAdditivity;

        /**
         * Constructor
         * 
         * @param originalLevel -
         * @param originalAdditivity -
         */
        public LoggerInfo(Level originalLevel, boolean originalAdditivity) {
            this.originalLevel = originalLevel;
            this.originalAdditivity = originalAdditivity;
        }

        /**
         * @return -
         */
        public Level getOriginalLevel() {
            return originalLevel;
        }

        /**
         * @return -
         */
        public boolean isOriginalAdditivity() {
            return originalAdditivity;
        }
    }

}