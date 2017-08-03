/*
 * #%L
 * Cantharella :: Utils
 * $Id: GenerateCsv.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.utils/src/main/java/nc/ird/cantharella/utils/GenerateCsv.java $
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
package nc.ird.cantharella.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

import au.com.bytecode.opencsv.CSVWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Classe permettant de générer un fichier csv
 * 
 * @author Adrien Cheype
 */
public class GenerateCsv {

    /**
     * Logger
     */
    private static final Logger LOG = LoggerFactory.getLogger(GenerateCsv.class);

    /** delimiter de colonne */
    private char columnsSeparator;

    /** Copyright, pour Ird par défaut */
    private String copyright;

    /** Encoding utilisé pour le CSV */
    private String encoding;

    /** Copyright utilisé par défaut, pour l'IRD en français */
    private final static String DEFAULT_COPYRIGHT = "©IRD tous droits réservés";

    /** static varable for UTF-8 encoding */
    public static final String CHARSET_UTF_8 = "UTF-8";

    /** static varable for ISO_8859_1 encoding */
    public static final String CHARSET_ISO_8859_1 = "ISO-8859-1";

    /**
     * Constructeur par défaut
     */
    public GenerateCsv() {
        this(';', CHARSET_UTF_8);
    }

    /**
     * Constructeur
     * 
     * @param encoding utilisé pour le CSV
     */
    public GenerateCsv(String encoding) {
        this(';', encoding);
    }

    /**
     * Constructeur
     * 
     * @param columnsSeparator caractère utilisé comme délimiteur de colonne
     * @param encoding utilisé pour le CSV
     */
    public GenerateCsv(char columnsSeparator, String encoding) {
        setColumnsSeparator(columnsSeparator);
        setEncoding(encoding);
        setCopyright(DEFAULT_COPYRIGHT);
    }

    /**
     * Rend un flux qui décrit un ensemble de données en respectant le format CSV
     * 
     * @param data données à écrire
     * @param withCopyright si vrai, un copyright est ajouté à la dernière ligne
     * @return le flux de donnée en sortie
     * @throws IOException exception parvenue dans les traitements entrée/sortie du fichier
     */
    public ByteArrayOutputStream writeCSV(List<String[]> data, boolean withCopyright) throws IOException {

        ByteArrayOutputStream csvStream = new ByteArrayOutputStream();
        OutputStreamWriter outWriter = new OutputStreamWriter(csvStream, encoding);

        CSVWriter csvWriter = new CSVWriter(outWriter, this.getColumnsSeparator());
        // paramétrage de l'écriture du CSV

        //écriture des données
        csvWriter.writeAll(data);
        if (withCopyright) {
            //écriture du copyright
            csvWriter.writeNext(new String[] { getCopyright() });
        }
        csvWriter.close();
        LOG.debug("data written in the CSV stream");

        return csvStream;
    }

    /**
     * copyright getter
     * 
     * @return copyright
     */
    public String getCopyright() {
        return copyright;
    }

    /**
     * copyright setter
     * 
     * @param copyright copyright
     */
    public void setCopyright(String copyright) {
        this.copyright = copyright;
    }

    /**
     * columnsSeparator getter
     * 
     * @return columnsSeparator
     */
    public char getColumnsSeparator() {
        return columnsSeparator;
    }

    /**
     * columnsSeparator setter
     * 
     * @param columnsSeparator columnsSeparator
     */
    public void setColumnsSeparator(char columnsSeparator) {
        this.columnsSeparator = columnsSeparator;
    }

    /**
     * encoding getter
     * 
     * @return encoding
     */
    public String getEncoding() {
        return encoding;
    }

    /**
     * encoding setter
     * 
     * @param encoding encoding
     */
    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

}
