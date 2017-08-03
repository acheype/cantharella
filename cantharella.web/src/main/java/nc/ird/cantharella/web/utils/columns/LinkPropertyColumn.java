/*
 * #%L
 * Cantharella :: Web
 * $Id: LinkPropertyColumn.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.web/src/main/java/nc/ird/cantharella/web/utils/columns/LinkPropertyColumn.java $
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

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.link.PopupSettings;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;

/**
 * Column embeded in a datatable which represent a link code from
 * https://cwiki.apache.org/WICKET/adding-links-in-a-defaultdatatable.html
 * 
 * @param <T> Generic type
 * @param <S> the type of the sort property
 */
abstract public class LinkPropertyColumn<T, S> extends PropertyColumn<T, S> {

    /** Popup settings */
    private PopupSettings popupSettings;

    /** Label model */
    private IModel<String> labelModel;

    /** Title displayed for the link */
    protected IModel<String> linkTitle;

    /**
     * Constructor
     * 
     * @param displayModel Display model
     * @param sortProperty Sort property
     * @param propertyExpression Property expression
     * @param popupSettings Popup setting
     */
    public LinkPropertyColumn(IModel<String> displayModel, S sortProperty, String propertyExpression,
            PopupSettings popupSettings) {
        this(displayModel, sortProperty, propertyExpression);
        this.popupSettings = popupSettings;
    }

    /**
     * Constructor
     * 
     * @param displayModel Display model
     * @param sortProperty Sort property
     * @param propertyExpression Property expression
     * @param linkTitle Title displayed for the link
     */
    public LinkPropertyColumn(IModel<String> displayModel, S sortProperty, String propertyExpression,
            IModel<String> linkTitle) {
        this(displayModel, sortProperty, propertyExpression);
        this.linkTitle = linkTitle;
    }

    /**
     * Constructor
     * 
     * @param displayModel Display model
     * @param sortProperty Sort property
     * @param propertyExpression Property expression
     * @param linkTitle Title displayed for the link
     * @param popupSettings Popup setting
     */
    public LinkPropertyColumn(IModel<String> displayModel, S sortProperty, String propertyExpression,
            IModel<String> linkTitle, PopupSettings popupSettings) {
        this(displayModel, sortProperty, propertyExpression);
        this.linkTitle = linkTitle;
        this.popupSettings = popupSettings;
    }

    /**
     * Constructor
     * 
     * @param displayModel Display model
     * @param labelModel Label model
     */
    public LinkPropertyColumn(IModel<String> displayModel, IModel<String> labelModel) {
        super(displayModel, null);
        this.labelModel = labelModel;
    }

    /**
     * Constructor
     * 
     * @param displayModel Display model
     * @param sortProperty Sort property
     * @param propertyExpression Property expression
     */
    public LinkPropertyColumn(IModel<String> displayModel, S sortProperty, String propertyExpression) {
        super(displayModel, sortProperty, propertyExpression);
    }

    /**
     * Constructor
     * 
     * @param displayModel Display model
     * @param propertyExpression Property expression
     */
    public LinkPropertyColumn(IModel<String> displayModel, String propertyExpression) {
        super(displayModel, propertyExpression);
    }

    /** {@inheritDoc} */
    @Override
    public void populateItem(Item<ICellPopulator<T>> item, String componentId, IModel<T> model) {
        item.add(new LinkPanel(item, componentId, model));
    }

    /**
     * Override this method to react to link clicks. Your own/internal row id will most likely be inside the model.
     * 
     * @param item Item
     * @param componentId Component id
     * @param model Model
     */
    public abstract void onClick(Item<ICellPopulator<T>> item, String componentId, IModel<T> model);

    /**
     * Panel which include a link. Used with the LinkPropertyColumn$LinkPanel.html file
     */
    public class LinkPanel extends Panel {

        /**
         * Constructor
         * 
         * @param item Item
         * @param componentId Component id
         * @param model Model
         */
        public LinkPanel(final Item<ICellPopulator<T>> item, final String componentId, final IModel<T> model) {
            super(componentId);

            Link<T> link = new Link<T>("link") {

                @Override
                public void onClick() {
                    LinkPropertyColumn.this.onClick(item, componentId, model);
                }
            };
            link.setPopupSettings(popupSettings);
            if (linkTitle != null) {
                link.add(new AttributeModifier("title", linkTitle));
            }

            add(link);

            IModel<?> tmpLabelModel = labelModel;

            if (labelModel == null) {
                tmpLabelModel = getDataModel(model);
            }

            link.add(new Label("label", tmpLabelModel));
        }
    }
}