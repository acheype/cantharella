/*
 * #%L
 * Cantharella :: Web
 * $Id: MapValuePropertyColumn.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.web/src/main/java/nc/ird/cantharella/web/utils/columns/MapValuePropertyColumn.java $
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

import java.util.Map;

import nc.ird.cantharella.web.utils.models.DisplayMapValuePropertyModel;

import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.export.IExportableColumn;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;

/**
 * PropertyColumn which display string resulting values of applying model value on a map.
 * 
 * @author Adrien Cheype
 * @param <T> Type of the row model
 * @param <S> the type of the sort property
 * @param <U> Type of the map key. The map is thus parametred by <U, String>
 */
public class MapValuePropertyColumn<T, S, U> extends AbstractColumn<T, S> implements IExportableColumn<T, S, Object> {

    /** wicket property expression */
    private final String propertyExpression;

    /** map */
    private final Map<U, String> map;

    /**
     * Constructor
     * 
     * @param displayModel DisplayModel
     * @param sortProperty SortProperty
     * @param propertyExpression Wicket property expression
     * @param map Applying map
     */
    public MapValuePropertyColumn(IModel<String> displayModel, S sortProperty, String propertyExpression,
            Map<U, String> map) {
        super(displayModel, sortProperty);
        this.propertyExpression = propertyExpression;
        this.map = map;
    }

    /** {@inheritDoc} */
    @Override
    public void populateItem(Item<ICellPopulator<T>> cellItem, String componentId, IModel<T> rowModel) {
        cellItem.add(new Label(componentId, getDataModel(rowModel)));
    }

    /** {@inheritDoc} */
    @Override
    public IModel<Object> getDataModel(IModel<T> rowModel) {
        return new DisplayMapValuePropertyModel<U>(rowModel.getObject(), propertyExpression, map);
    }
}
