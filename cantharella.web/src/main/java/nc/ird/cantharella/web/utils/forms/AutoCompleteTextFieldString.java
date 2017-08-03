/*
 * #%L
 * Cantharella :: Web
 * $Id: AutoCompleteTextFieldString.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.web/src/main/java/nc/ird/cantharella/web/utils/forms/AutoCompleteTextFieldString.java $
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
package nc.ird.cantharella.web.utils.forms;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import nc.ird.cantharella.utils.AssertTools;
import nc.ird.cantharella.utils.Pair;
import nc.ird.cantharella.utils.StringTools;
import nc.ird.cantharella.utils.StringTransformer;

import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.StringAutoCompleteRenderer;
import org.apache.wicket.model.IModel;

/**
 * AutoCompleteTextField for strings
 * <p>
 * Warning: if you get troubles with Tomcat, you need to adjust the server.xml file (<Connector URIEncoding="UTF-8" />),
 * because the input is sent inside the URL.
 * </p>
 * 
 * @author Mickael Tricot
 * @author Adrien Cheype
 */
public class AutoCompleteTextFieldString extends AutoCompleteTextField<String> {

    /**
     * Comparison mode
     */
    public enum ComparisonMode {
        /** choice.contains(input) */
        CONTAINS,
        /** choice.endsWith(input) */
        ENDS_WITH,
        /** choice.equals(input) */
        EQUALS,
        /** choice.startsWith(input) */
        STARTS_WITH;
    }

    /**
     * String purge for comparison
     * 
     * @param string String
     * @return Purged string (trim, lower case, no accent)
     */
    private static String purgeString(String string) {
        return new StringTransformer(string).trimToNull().replaceAccents().toLowerCase().toString();
    }

    /** Choices (pairs of purge choice & choice) */
    private final List<Pair<String, String>> choices;

    /** Max number of choices */
    private final int maxChoices;

    /** Min processing length */
    private final int minLength;

    /** Comparison mode */
    private final ComparisonMode mode;

    /**
     * Constructor
     * 
     * @param id ID
     * @param model Model
     * @param choices Choices
     * @param mode Comparison mode
     */
    public AutoCompleteTextFieldString(String id, IModel<String> model, List<String> choices, ComparisonMode mode) {
        this(id, model, choices, mode, 0, Integer.MAX_VALUE);
    }

    /**
     * Constructor
     * 
     * @param id ID
     * @param model Model
     * @param choices Choices
     * @param mode Comparison mode
     * @param minLength Min processing length (default 0)
     * @param maxChoices Max choices (default Integer.MAX_VALUE)
     */
    @SuppressWarnings("unchecked")
    public AutoCompleteTextFieldString(String id, IModel<String> model, List<String> choices, ComparisonMode mode,
            int minLength, int maxChoices) {
        super(id, model, StringAutoCompleteRenderer.INSTANCE);
        AssertTools.assertNotNull(choices);
        AssertTools.assertNotNull(mode);
        AssertTools.assertNotNegative(minLength);
        AssertTools.assertPositive(maxChoices);
        this.choices = new ArrayList<Pair<String, String>>();
        for (String choice : choices) {
            String purgeChoice = purgeString(choice);
            if (purgeChoice != null && purgeChoice.length() >= minLength) {
                this.choices.add(new Pair<String, String>(purgeChoice, choice));
            }
        }
        this.mode = mode;
        this.minLength = minLength;
        this.maxChoices = maxChoices;
    }

    /** {@inheritDoc} */
    @Override
    protected Iterator<String> getChoices(String input) {
        TreeSet<String> choicesList = new TreeSet<String>();
        String purgeInput = purgeString(input);
        if (StringTools.length(purgeInput) >= minLength) {
            Iterator<Pair<String, String>> i = choices.iterator();
            while (i.hasNext() && choicesList.size() < maxChoices) {
                Pair<String, String> choice = i.next();
                if (mode == ComparisonMode.STARTS_WITH && choice.getKey().startsWith(purgeInput)
                        || mode == ComparisonMode.CONTAINS && choice.getKey().contains(purgeInput)
                        || mode == ComparisonMode.ENDS_WITH && choice.getKey().endsWith(purgeInput)
                        || mode == ComparisonMode.EQUALS && choice.getKey().equals(purgeInput)) {
                    choicesList.add(choice.getValue());
                }
            }
        }
        return choicesList.iterator();
    }

    /**
     * Add a choice at the suggestion list
     * 
     * @param choice The choice to add
     */
    public void addChoice(String choice) {
        String purgeChoice = purgeString(choice);
        if (purgeChoice != null && purgeChoice.length() >= minLength) {
            this.choices.add(new Pair<String, String>(purgeChoice, choice));
        }
    }

}
