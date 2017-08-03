/*
 * #%L
 * Cantharella :: Web
 * $Id: WebMessages.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.web/src/main/java/nc/ird/cantharella/web/utils/resources/WebMessages.java $
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
package nc.ird.cantharella.web.utils.resources;

import java.util.Locale;

import nc.ird.cantharella.utils.AssertTools;

import org.apache.wicket.Component;
import org.apache.wicket.resource.loader.IStringResourceLoader;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Service;

/**
 * Internationalization messages (works with Spring MessageSourceAccessor)
 * 
 * @author Mickael Tricot
 */
@Service
public final class WebMessages implements IStringResourceLoader {

    /** Message source */
    private final MessageSourceAccessor source;

    /**
     * Constructor
     * 
     * @param source Message source
     */
    public WebMessages(MessageSourceAccessor source) {
        AssertTools.assertNotNull(source);
        this.source = source;
    }

    /** {@inheritDoc} */
    @Override
    public String loadStringResource(Class<?> clazz, String key, Locale locale, String style, String variation) {
        try {
            return source.getMessage(key, locale);
        } catch (NoSuchMessageException e) {
            return null;
        }
    }

    /** {@inheritDoc} */
    @Override
    public String loadStringResource(Component component, String key, Locale locale, String style, String variation) {
        return component != null ? loadStringResource(component.getClass(), key, locale, null, null) : null;
    }

}