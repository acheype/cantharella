/*
 * #%L
 * Cantharella :: Web
 * $Id: TypeDroitColumn.java 235 2013-05-29 08:49:38Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.web/src/main/java/nc/ird/cantharella/web/pages/domain/personne/TypeDroitColumn.java $
 * %%
 * Copyright (C) 2009 - 2013 IRD (Institut de Recherche pour le Developpement) and by respective authors (see below)
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
package nc.ird.cantharella.web.pages.domain.personne;

import java.io.Serializable;

import nc.ird.cantharella.data.model.Personne;
import nc.ird.cantharella.data.model.Utilisateur;
import nc.ird.cantharella.web.pages.TemplatePage;

import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.export.IExportableColumn;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

/**
 * Define TypeDroit column as exportable column.
 * 
 * @author Eric Chatellier
 */
public class TypeDroitColumn extends AbstractColumn<Personne, String> implements
        IExportableColumn<Personne, String, Serializable> {

    /** Template page. */
    protected TemplatePage templatePage;

    /**
     * Constructor.
     * 
     * @param displayModel model used to generate header text
     * @param templatePage templatePage
     */
    public TypeDroitColumn(IModel<String> displayModel, TemplatePage templatePage) {
        super(displayModel);
        this.templatePage = templatePage;
    }

    /** {@inheritDoc} */
    @Override
    public void populateItem(Item<ICellPopulator<Personne>> item, String componentId, IModel<Personne> rowModel) {
        item.add(new Label(componentId, getDataModel(rowModel)));
    }

    /** {@inheritDoc} */
    @Override
    public IModel<Serializable> getDataModel(final IModel<Personne> rowModel) {
        return new Model<Serializable>(rowModel) {
            /** {@inheritDoc} */
            @Override
            public String getObject() {
                String typeDroit;
                if (rowModel.getObject() instanceof Utilisateur) {
                    Utilisateur util = (Utilisateur) rowModel.getObject();
                    typeDroit = templatePage.enumValueMessage(util.getTypeDroit())
                            + (util.isValide() ? "" : " "
                                    + templatePage.getString(templatePage.getClass().getSimpleName() + ".IsNotValid"));
                } else {
                    typeDroit = templatePage.getString(Personne.class.getSimpleName());
                }
                return typeDroit;
            }
        };
    }
}
