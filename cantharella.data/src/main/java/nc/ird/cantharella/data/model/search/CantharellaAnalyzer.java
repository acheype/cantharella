/*
 * #%L
 * Cantharella :: Data
 * $Id: CantharellaAnalyzer.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.data/src/main/java/nc/ird/cantharella/data/model/search/CantharellaAnalyzer.java $
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
package nc.ird.cantharella.data.model.search;

import java.io.IOException;
import java.io.Reader;
import java.util.Set;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.KeywordMarkerFilter;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.StopwordAnalyzerBase;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.WordlistLoader;
import org.apache.lucene.analysis.fr.ElisionFilter;
import org.apache.lucene.analysis.fr.FrenchLightStemFilter;
import org.apache.lucene.analysis.snowball.SnowballFilter;
import org.apache.lucene.analysis.standard.StandardAnalyzer; // for javadoc
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.util.IOUtils;
import org.apache.lucene.util.Version;

/**
 * {@link Analyzer} for French language.
 * <p>
 * Supports an external list of stopwords (words that will not be indexed at all) and an external list of exclusions
 * (word that will not be stemmed, but indexed). A default set of stopwords is used unless an alternative list is
 * specified, but the exclusion list is empty by default.
 * </p>
 * 
 * <a name="version"/>
 * <p>
 * You must specify the required {@link Version} compatibility when creating FrenchAnalyzer:
 * <ul>
 * <li>As of 3.6, FrenchLightStemFilter is used for less aggressive stemming.
 * <li>As of 3.1, Snowball stemming is done with SnowballFilter, LowerCaseFilter is used prior to StopFilter, and
 * ElisionFilter and Snowball stopwords are used by default.
 * <li>As of 2.9, StopFilter preserves position increments
 * </ul>
 * 
 * <p>
 * <b>NOTE</b>: This class uses the same {@link Version} dependent settings as {@link StandardAnalyzer}.
 * </p>
 */
public final class CantharellaAnalyzer extends StopwordAnalyzerBase {

    /** File containing default French stopwords. */
    public final static String DEFAULT_STOPWORD_FILE = "french_stop.txt";

    /**
     * Contains words that should be indexed but not stemmed.
     */
    private Set<?> excltable = CharArraySet.EMPTY_SET;

    /**
     * Returns an unmodifiable instance of the default stop-words set.
     * 
     * @return an unmodifiable instance of the default stop-words set.
     */
    public static Set<?> getDefaultStopSet() {
        return DefaultSetHolder.DEFAULT_STOP_SET;
    }

    @SuppressWarnings("deprecation")
    private static class DefaultSetHolder {

        static final Set<?> DEFAULT_STOP_SET;
        static {
            try {
                DEFAULT_STOP_SET = WordlistLoader.getSnowballWordSet(
                        IOUtils.getDecodingReader(SnowballFilter.class, DEFAULT_STOPWORD_FILE, IOUtils.CHARSET_UTF_8),
                        Version.LUCENE_CURRENT);
            } catch (IOException ex) {
                // default set should always be present as it is part of the
                // distribution (JAR)
                throw new RuntimeException("Unable to load default stopword set");
            }
        }
    }

    /**
     * Builds an analyzer with the default stop words ({@link #getDefaultStopSet}).
     * 
     * @param matchVersion lucene match version
     */
    public CantharellaAnalyzer(Version matchVersion) {
        this(matchVersion, DefaultSetHolder.DEFAULT_STOP_SET);
    }

    /**
     * Builds an analyzer with the given stop words
     * 
     * @param matchVersion lucene compatibility version
     * @param stopwords a stopword set
     */
    public CantharellaAnalyzer(Version matchVersion, Set<?> stopwords) {
        this(matchVersion, stopwords, CharArraySet.EMPTY_SET);
    }

    /**
     * Builds an analyzer with the given stop words
     * 
     * @param matchVersion lucene compatibility version
     * @param stopwords a stopword set
     * @param stemExclutionSet a stemming exclusion set
     */
    public CantharellaAnalyzer(Version matchVersion, Set<?> stopwords, Set<?> stemExclutionSet) {
        super(matchVersion, stopwords);
        this.excltable = CharArraySet.unmodifiableSet(CharArraySet.copy(matchVersion, stemExclutionSet));
    }

    /**
     * Creates {@link org.apache.lucene.analysis.ReusableAnalyzerBase.TokenStreamComponents} used to tokenize all the
     * text in the provided {@link Reader}.
     * 
     * @return {@link org.apache.lucene.analysis.ReusableAnalyzerBase.TokenStreamComponents} built from a
     *         {@link StandardTokenizer} filtered with {@link StandardFilter}, {@link ElisionFilter},
     *         {@link LowerCaseFilter}, {@link StopFilter}, {@link KeywordMarkerFilter} if a stem exclusion set is
     *         provided, and {@link FrenchLightStemFilter}
     */
    @Override
    protected TokenStreamComponents createComponents(String fieldName, Reader reader) {
        final Tokenizer source = new StandardTokenizer(matchVersion, reader);
        TokenStream result = new StandardFilter(matchVersion, source);
        result = new ElisionFilter(matchVersion, result);
        result = new LowerCaseFilter(matchVersion, result);
        result = new StopFilter(matchVersion, result, stopwords);
        if (!excltable.isEmpty())
            result = new KeywordMarkerFilter(result, excltable);
        result = new CantharellaStemFilter(result);
        return new TokenStreamComponents(source, result);
    }
}
