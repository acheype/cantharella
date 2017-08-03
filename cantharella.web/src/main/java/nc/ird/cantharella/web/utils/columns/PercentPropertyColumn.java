/*
 * #%L
 * Cantharella :: Web
 * $Id: PercentPropertyColumn.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.web/src/main/java/nc/ird/cantharella/web/utils/columns/PercentPropertyColumn.java $
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
package nc.ird.cantharella.web.utils.columns;

import java.util.Locale;

import nc.ird.cantharella.web.utils.models.DisplayPercentPropertyModel;

import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;

/**
 * PropertyColumns which display numbers with percent format
 * 
 * @author Adrien Cheype
 * @param <T> Type of the row model
 * @param <S> the type of the sort property
 */
public class PercentPropertyColumn<T, S> extends AbstractColumn<T, S> {

    /** wicket property expression */
    private final String propertyExpression;

    /** locale property */
    private final Locale locale;

    /**
     * Constructor
     * 
     * @param displayModel DisplayModel
     * @param sortProperty SortProperty
     * @param propertyExpression Wicket property expression
     * @param locale Locale used to format decimal
     */
    public PercentPropertyColumn(IModel<String> displayModel, S sortProperty, String propertyExpression, Locale locale) {
        super(displayModel, sortProperty);
        this.propertyExpression = propertyExpression;
        this.locale = locale;
    }

    /** {@inheritDoc} */
    @Override
    public void populateItem(Item<ICellPopulator<T>> cellItem, String componentId, IModel<T> rowModel) {
        cellItem.add(new Label(componentId, new DisplayPercentPropertyModel(rowModel.getObject(), propertyExpression,
                locale)));
    }

}
