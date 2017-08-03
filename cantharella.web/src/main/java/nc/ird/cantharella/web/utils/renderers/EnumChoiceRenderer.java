/*
 * #%L
 * Cantharella :: Web
 * $Id: EnumChoiceRenderer.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.web/src/main/java/nc/ird/cantharella/web/utils/renderers/EnumChoiceRenderer.java $
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
package nc.ird.cantharella.web.utils.renderers;

import java.io.Serializable;

import nc.ird.cantharella.web.pages.TemplatePage;

import org.apache.wicket.markup.html.form.IChoiceRenderer;

/**
 * IChoiceRenderer for an Enum
 * 
 * @author Mickael Tricot
 * @param <E> Enum
 */
public final class EnumChoiceRenderer<E extends Enum<?>> implements IChoiceRenderer<E>, Serializable {

    /** Page */
    private final TemplatePage page;

    /**
     * Constructor
     * 
     * @param page Page
     */
    public EnumChoiceRenderer(TemplatePage page) {
        this.page = page;
    }

    /** {@inheritDoc} */
    @Override
    public String getDisplayValue(E enumValue) {
        return page.enumValueMessage(enumValue);
    }

    /** {@inheritDoc} */
    @Override
    public String getIdValue(E enumValue, int index) {
        return enumValue.toString();
    }
}