/*
 * #%L
 * Cantharella :: Web
 * $Id: LinkProduitPropertyColumn.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.web/src/main/java/nc/ird/cantharella/web/pages/columns/LinkProduitPropertyColumn.java $
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

import nc.ird.cantharella.data.model.Extrait;
import nc.ird.cantharella.data.model.Fraction;
import nc.ird.cantharella.data.model.Produit;
import nc.ird.cantharella.web.pages.TemplatePage;
import nc.ird.cantharella.web.utils.columns.LinkPropertyColumn;
import nc.ird.cantharella.utils.BeanTools;
import nc.ird.cantharella.utils.BeanTools.AccessType;

import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;

/**
 * LinkPropertyColumn used to link toward an Extraction or Purification depending on the produit type (extrait or
 * fraction). T MUST HAVE a property named "produit" which give a Produit.
 * 
 * @author Adrien Cheype
 * @param <T> Row type
 * @param <S> the type of the sort property
 */
public abstract class LinkProduitPropertyColumn<T, S> extends LinkPropertyColumn<T, S> {

    /** page used to get messages */
    private final TemplatePage page;

    /**
     * Constructor
     * 
     * @param displayModel displayModel
     * @param sortProperty sortProperty
     * @param propertyExpression propertyExpression
     * @param page page used to get messages
     */
    public LinkProduitPropertyColumn(IModel<String> displayModel, S sortProperty, String propertyExpression,
            TemplatePage page) {
        super(displayModel, sortProperty, propertyExpression);
        this.page = page;
    }

    /** {@inheritDoc} */
    @Override
    public void onClick(Item<ICellPopulator<T>> item, String componentId, IModel<T> model) {
        if (model.getObject() != null) {
            Produit prod = (Produit) BeanTools.getValue(model.getObject(), AccessType.GETTER, "produit");

            if (prod.isExtrait()) {
                Extrait extrait = (Extrait) prod;
                onClickIfExtrait(extrait);
            } else {
                // le produit est une fraction
                Fraction fraction = (Fraction) prod;
                onClickIfFraction(fraction);
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public void populateItem(Item<ICellPopulator<T>> item, String componentId, IModel<T> model) {
        if (model.getObject() != null) {
            // title différent suivant si le produit provient d'une extraction ou d'une purification
            Produit prod = (Produit) BeanTools.getValue(model.getObject(), AccessType.GETTER, "produit");
            if (prod.isExtrait()) {
                linkTitle = page.getStringModel("ReadExtraction");
            } else {
                linkTitle = page.getStringModel("ReadPurification");
            }
        }
        super.populateItem(item, componentId, model);
    }

    /**
     * Executed on click evenement when the produis is an extrait
     * 
     * @param extrait extrait
     */
    public abstract void onClickIfExtrait(Extrait extrait);

    /**
     * Executed on click evenement when the produis is a fraction
     * 
     * @param fraction fraction
     */
    public abstract void onClickIfFraction(Fraction fraction);

}
