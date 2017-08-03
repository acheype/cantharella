/*
 * #%L
 * Cantharella :: Data
 * $Id: CantharellaStemFilter.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.data/src/main/java/nc/ird/cantharella/data/model/search/CantharellaStemFilter.java $
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

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.KeywordAttribute;

/**
 * Custom stem filter to use {@link CantharellaStemmer} instead of lucene's default French Stemmer.
 * 
 * @author Eric Chatellier
 */
public class CantharellaStemFilter extends TokenFilter {
    /** Character sequence stemmer. */
    private final CantharellaStemmer stemmer = new CantharellaStemmer();

    /** Char term attribute. */
    private final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);

    /** Keyword attribute. */
    private final KeywordAttribute keywordAttr = addAttribute(KeywordAttribute.class);

    /**
     * Constructor.
     * 
     * @param input token stream.
     */
    public CantharellaStemFilter(TokenStream input) {
        super(input);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean incrementToken() throws IOException {
        if (input.incrementToken()) {
            if (!keywordAttr.isKeyword()) {
                final int newlen = stemmer.stem(termAtt.buffer(), termAtt.length());
                termAtt.setLength(newlen);
            }
            return true;
        } else {
            return false;
        }
    }
}