/*
 * #%L
 * Cantharella :: Web
 * $Id: TaxonomyPropertyColumn.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.web/src/main/java/nc/ird/cantharella/web/pages/columns/TaxonomyPropertyColumn.java $
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
package nc.ird.cantharella.web.pages.columns;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

/**
 * Column which display taxonomy names. Put "taxonomy" at attribute class of the html column.
 * 
 * @author Adrien Cheype
 * @param <T> Type of the row model
 * @param <S> Type of sort property
 */
public class TaxonomyPropertyColumn<T, S> extends PropertyColumn<T, S> {

    /**
     * Constructor
     * 
     * @param displayModel Display model
     * @param sortProperty Sort property
     * @param propertyExpression Property expression
     */
    public TaxonomyPropertyColumn(IModel<String> displayModel, S sortProperty, String propertyExpression) {
        super(displayModel, sortProperty, propertyExpression);
    }

    /**
     * Constructor
     * 
     * @param displayModel Display model
     * @param propertyExpression Property expression
     */
    public TaxonomyPropertyColumn(IModel<String> displayModel, String propertyExpression) {
        super(displayModel, propertyExpression);
    }

    /** {@inheritDoc} */
    @Override
    public void populateItem(Item<ICellPopulator<T>> item, String componentId, IModel<T> rowModel) {
        Label label = new Label(componentId, getDataModel(rowModel));
        label.add(new AttributeModifier("class", new Model<String>("taxonomy")));
        item.add(label);
    }
}
