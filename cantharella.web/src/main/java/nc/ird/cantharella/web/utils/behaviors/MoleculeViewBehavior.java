/*
 * #%L
 * Cantharella :: Web
 * $Id: MoleculeViewBehavior.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.web/src/main/java/nc/ird/cantharella/web/utils/behaviors/MoleculeViewBehavior.java $
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
package nc.ird.cantharella.web.utils.behaviors;

import org.apache.wicket.Application;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.core.util.string.JavaScriptUtils;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.Response;

/**
 * Permet d'ajouter la formule dans un attribut formula. L'attribut est ensuite utilise en javascript pour ajouter
 * l'editeur de formule
 * 
 * @author poussin
 * @version $Revision: 269 $
 * 
 *          Last update: $Date: 2014-05-07 19:14:00 +1100 (Wed, 07 May 2014) $ by : $Author: echatellier $
 */
public class MoleculeViewBehavior extends AttributeModifier {

    /** Full view, with cliquable canvas and mol download support. */
    protected boolean fullView;

    /**
     * Constructor with default canvas size.
     * 
     * @param replaceModel replace model
     */
    public MoleculeViewBehavior(IModel<?> replaceModel) {
        this(replaceModel, false);
    }

    /**
     * Constructor.
     * 
     * @param replaceModel replace model
     * @param fullView full view, with cliquable canvas and mol download support
     */
    public MoleculeViewBehavior(IModel<?> replaceModel, boolean fullView) {
        super("formula", replaceModel);
        this.fullView = fullView;
    }

    /** {@inheritDoc} */
    @Override
    protected String newValue(String currentValue, String replacementValue) {
        // on ajoute toujours un premier caractere pour oblige l'existance de
        // l'attribut car sinon si formula est vide le l'attribut n'est pas ajoute
        String result = ".";
        if (replacementValue != null) {
            result += replacementValue;
        }
        return result;
    }

    /** {@inheritDoc} */
    @Override
    public void renderHead(Component component, IHeaderResponse response) {
        response.render(JavaScriptHeaderItem.forReference(Application.get().getJavaScriptLibrarySettings()
                .getJQueryReference()));
        response.render(CssHeaderItem.forUrl("ChemDoodleWeb/css/ChemDoodleWeb.css"));
        response.render(JavaScriptHeaderItem.forUrl("ChemDoodleWeb/js/ChemDoodleWeb-libs.js"));
        response.render(JavaScriptHeaderItem.forUrl("ChemDoodleWeb/js/ChemDoodleWeb.js"));
        response.render(JavaScriptHeaderItem.forUrl("js/molviewer.js"));
    }

    /** {@inheritDoc} */
    @Override
    public void beforeRender(Component component) {
        // il faut que l'element HTML est forcement un identifiant pour pouvoir
        // travailler avec
        component.setOutputMarkupId(true);
    }

    /** {@inheritDoc} */
    @Override
    public void afterRender(Component component) {
        Response response = component.getResponse();
        String id = component.getMarkupId();

        response.write(JavaScriptUtils.SCRIPT_OPEN_TAG);
        if (fullView) {
            response.write("addFullViewerMolecule('" + id + "');");
        } else {
            response.write("addViewerMolecule('" + id + "');");
        }
        response.write(JavaScriptUtils.SCRIPT_CLOSE_TAG);
    }

}
