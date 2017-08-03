/*
 * #%L
 * Cantharella :: Data
 * $Id: CantharellaAnalyzerTest.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.data/src/test/java/nc/ird/cantharella/data/model/search/CantharellaAnalyzerTest.java $
 * %%
 * Copyright (C) 2013 IRD (Institut de Recherche pour le Developpement) and by respective authors (see below)
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
import java.io.StringReader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.util.Version;
import org.junit.Assert;
import org.junit.Test;

public class CantharellaAnalyzerTest {

    /**
     * Test que les caractères dupliqués ne sont pas perdu (supprimé) par l'analyzer.
     * 
     * @throws IOException
     */
    @Test
    public void testDuplicated() throws IOException {
        Analyzer analyzer = new CantharellaAnalyzer(Version.LUCENE_36);

        // without *
        StringReader reader = new StringReader("R3044");
        TokenStream stream = analyzer.tokenStream("label", reader);
        stream.incrementToken();
        String term = stream.getAttribute(CharTermAttribute.class).toString();
        Assert.assertEquals("r3044", term.toString());

        // with *
        reader = new StringReader("*3044");
        stream = analyzer.tokenStream("label", reader);
        stream.incrementToken();
        term = stream.getAttribute(CharTermAttribute.class).toString();
        Assert.assertEquals("3044", term.toString());

        analyzer.close();
    }
}
