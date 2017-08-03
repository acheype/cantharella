/*
 * #%L
 * Cantharella :: Web
 * $Id: ExtraitsColumn.java 148 2013-02-21 14:47:28Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.web/src/main/java/nc/ird/cantharella/web/pages/domain/extraction/ExtraitsColumn.java $
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
package nc.ird.cantharella.web.pages.domain.extraction;

import java.io.Serializable;
import java.util.List;
import java.util.Locale;

import nc.ird.cantharella.data.model.Extraction;
import nc.ird.cantharella.data.model.Extrait;
import nc.ird.cantharella.web.utils.behaviors.ReplaceEmptyLabelBehavior;
import nc.ird.cantharella.web.utils.models.DisplayDecimalPropertyModel;
import nc.ird.cantharella.web.utils.models.DisplayDecimalPropertyModel.DecimalDisplFormat;

import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.export.IExportableColumn;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

/**
 * Extraits exportable column displaying 4 first extracts of current extractions.
 * 
 * @author Eric Chatellier
 */
public class ExtraitsColumn extends AbstractColumn<Extraction, String> implements
        IExportableColumn<Extraction, String, Serializable> {

    /** Nombre d'extraits affichés dans la colonne de résumés des extraits. */
    private static final int MAX_EXTRAITS_DISPLAY = 4;

    /** Locale. */
    protected Locale locale;

    /**
     * Constructor.
     * 
     * @param displayModel model used to generate header text
     * @param locale locale
     */
    public ExtraitsColumn(IModel<String> displayModel, Locale locale) {
        super(displayModel);
        this.locale = locale;
    }

    /** {@inheritDoc} */
    @Override
    public void populateItem(Item<ICellPopulator<Extraction>> cellItem, String componentId, IModel<Extraction> rowModel) {
        // affiche un résumé des types extraits et des masses, allant jusqu'à MAX_EXTRAITS_DISPLAY extraits
        Label extraitsLabel = new Label(componentId, getDataModel(rowModel, true));
        extraitsLabel.setEscapeModelStrings(false);
        extraitsLabel.add(new ReplaceEmptyLabelBehavior());
        cellItem.add(extraitsLabel);
    }

    /** {@inheritDoc} */
    @Override
    public IModel<Serializable> getDataModel(IModel<Extraction> rowModel) {
        return getDataModel(rowModel, false);
    }

    /**
     * Improve data model to support optionnal html formatting.
     * 
     * @param rowModel current row to format
     * @param html enable html formatting (must be false for csv)
     * @return data model
     */
    protected IModel<Serializable> getDataModel(IModel<Extraction> rowModel, final boolean html) {
        return new Model<Serializable>(rowModel) {
            /** {@inheritDoc} */
            @Override
            public String getObject() {
                StringBuilder extraitsResume = new StringBuilder();
                List<Extrait> extraits = new PropertyModel<List<Extrait>>(super.getObject(), "sortedExtraits")
                        .getObject();
                for (int i = 0; i < MAX_EXTRAITS_DISPLAY && i < extraits.size(); i++) {
                    Extrait curExtrait = extraits.get(i);
                    if (html) {
                        extraitsResume.append("<b>");
                    }
                    extraitsResume.append(curExtrait.getTypeExtrait().getInitiales());
                    if (html) {
                        extraitsResume.append("</b>");
                    }
                    extraitsResume.append(" : ");
                    extraitsResume
                            .append(curExtrait.getMasseObtenue() == null ? ReplaceEmptyLabelBehavior.NULL_PROPERTY
                                    : new DisplayDecimalPropertyModel(curExtrait, "masseObtenue",
                                            DecimalDisplFormat.SMALL, locale).getObject());
                    if (i < MAX_EXTRAITS_DISPLAY - 1 && i < extraits.size() - 1) {
                        extraitsResume.append(", ");
                    }
                    if (i == MAX_EXTRAITS_DISPLAY - 1 && i < extraits.size() - 1) {
                        extraitsResume.append(", ...");
                    }
                }
                return extraitsResume.toString();
            }
        };
    }
}
