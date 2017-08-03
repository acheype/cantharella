/*
 * #%L
 * Cantharella :: Web
 * $Id: PropertyLabelLinkProduitPanel.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.web/src/main/java/nc/ird/cantharella/web/utils/panels/PropertyLabelLinkProduitPanel.java $
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
package nc.ird.cantharella.web.utils.panels;

import nc.ird.cantharella.data.model.Extrait;
import nc.ird.cantharella.data.model.Fraction;
import nc.ird.cantharella.data.model.Produit;
import nc.ird.cantharella.web.pages.TemplatePage;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

/**
 * Panel which display a "produit" link with inside the correspondig value of the model. If the produit is an Extrait,
 * the link is on the corresponding Extraction page, otherwise it's an Fraction and the link is on the corresponding
 * Purification page.
 * 
 * @author Adrien Cheype
 */
public abstract class PropertyLabelLinkProduitPanel extends Panel {

    /** generated link */
    private Link<Produit> link;

    /**
     * Constructor
     * 
     * @param id panel id
     * @param produitModel model used to generate the link
     */
    public PropertyLabelLinkProduitPanel(String id, final IModel<Produit> produitModel) {
        super(id, produitModel);
        link = new Link<Produit>("link") {

            @Override
            public void onClick() {
                if (produitModel.getObject().isExtrait()) {
                    Extrait extrait = (Extrait) produitModel.getObject();
                    onClickIfExtrait(extrait);
                } else {
                    // le produit est une fraction
                    Fraction fraction = (Fraction) produitModel.getObject();
                    onClickIfFraction(fraction);
                }
            }
        };
        add(link);

        link.add(new Label("label", produitModel));
    }

    /**
     * Constructor
     * 
     * @param id panel id
     * @param produitModel model used to generate the link
     * @param page page used to get messages
     */
    public PropertyLabelLinkProduitPanel(String id, IModel<Produit> produitModel, TemplatePage page) {
        this(id, produitModel);
        if (produitModel.getObject() != null) {
            IModel<String> linkTitle;
            // title différent suivant si le produit provient d'une extraction ou d'une purification
            if (produitModel.getObject().isExtrait()) {
                linkTitle = page.getStringModel("ReadExtraction");
            } else {
                linkTitle = page.getStringModel("ReadPurification");
            }
            link.add(new AttributeModifier("title", linkTitle));
        }
    }

    /**
     * Get the model
     * 
     * @return model
     */
    @SuppressWarnings("unchecked")
    public final IModel<Produit> getModel() {
        return (IModel<Produit>) getDefaultModel();
    }

    /**
     * Get the model object
     * 
     * @return model object
     */
    public final Produit getModelObject() {
        return (Produit) getDefaultModelObject();
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
