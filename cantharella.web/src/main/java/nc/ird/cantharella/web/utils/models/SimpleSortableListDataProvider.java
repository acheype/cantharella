/*
 * #%L
 * Cantharella :: Web
 * $Id: SimpleSortableListDataProvider.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.web/src/main/java/nc/ird/cantharella/web/utils/models/SimpleSortableListDataProvider.java $
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
package nc.ird.cantharella.web.utils.models;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import nc.ird.cantharella.data.config.DataContext;
import nc.ird.cantharella.data.exceptions.UnexpectedException;
import nc.ird.cantharella.web.config.WebContext;
import nc.ird.cantharella.utils.AssertTools;
import nc.ird.cantharella.utils.BeanTools;
import nc.ird.cantharella.utils.BeanTools.AccessType;
import nc.ird.cantharella.utils.GenericsTools;
import org.apache.commons.collections.comparators.NullComparator;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.IModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Same as {@link LoadableDetachableSortableListDataProvider} but use directly object in list (without reload from data
 * base)
 * 
 * 
 * @author Mickael Tricot
 * @author Adrien Cheype
 * @param <M> Model object type
 */
public class SimpleSortableListDataProvider<M> extends SortableDataProvider<M, String> {

    private static final long serialVersionUID = 1L;

    /** Logger */
    private static final Logger LOG = LoggerFactory.getLogger(SimpleSortableListDataProvider.class);

    /** Comparator */
    private Comparator<Object> comparator;

    /** Data list */
    private List<M> list;

    /** Locale */
    private Locale locale;

    /** To recognize a special sort by codePays */
    final static String CODE_PAYS_PROPERTY = "codePays";

    /** Current filter to select results */
    // private FilterMapHomeMade filter;
    /**
     * Constructor
     * 
     * @param list List
     * @param locale Locale
     */
    @SuppressWarnings("unchecked")
    public SimpleSortableListDataProvider(List<M> list, Locale locale) {
        AssertTools.assertNotNull(list);
        AssertTools.assertIn(locale, DataContext.LOCALES);
        this.list = list;
        this.locale = locale;
        comparator = new NullComparator(true);
    }

    /** {@inheritDoc} */
    @Override
    public Iterator<M> iterator(long first, long count) {

        if (getSort() != null && !StringUtils.isEmpty(getSort().getProperty())) {
            Collections.sort(list, new Comparator<M>() {
                @Override
                public int compare(M o1, M o2) {
                    try {
                        Comparable<? extends Object> c1;
                        Object c2;
                        c1 = GenericsTools.cast(BeanTools.getValueFromPath(o1, AccessType.GETTER, getSort()
                                .getProperty()));

                        c2 = GenericsTools.cast(BeanTools.getValueFromPath(o2, AccessType.GETTER, getSort()
                                .getProperty()));

                        // Exceptions
                        // Countries are sorted by country name, not by country code
                        if (getSort().getProperty().endsWith(CODE_PAYS_PROPERTY)) {
                            if (c1 != null) {
                                c1 = WebContext.COUNTRIES.get(locale).get(c1);
                            }
                            if (c2 != null) {
                                c2 = WebContext.COUNTRIES.get(locale).get(c2);
                            }
                        }

                        return (getSort().isAscending() ? 1 : -1) * comparator.compare(c1, c2);
                    } catch (Exception e) {
                        LOG.error(e.getMessage(), e);
                        throw new UnexpectedException(e);
                    }
                }
            });
        }

        return list.subList((int) first, (int) Math.min(first + count, size())).iterator();
    }

    /** {@inheritDoc} */
    @Override
    public IModel<M> model(final M object) {
        // return new Model<M>(object);
        return new IModel<M>() {
            public M getObject() {
                return object;
            }

            public void setObject(M t) {
            }

            public void detach() {
            }
        };
    }

    /** {@inheritDoc} */
    @Override
    public long size() {
        return list.size();
    }

    /**
     * list getter
     * 
     * @return list
     */
    public List<M> getList() {
        return list;
    }

    /**
     * list setter
     * 
     * @param list list
     */
    public void setList(List<M> list) {
        this.list = list;
    }

}