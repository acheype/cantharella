/*
 * #%L
 * Cantharella :: Web
 * $Id: ListConfigurationPage.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.web/src/main/java/nc/ird/cantharella/web/pages/domain/config/ListConfigurationPage.java $
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
package nc.ird.cantharella.web.pages.domain.config;

import nc.ird.cantharella.web.pages.TemplatePage;
import nc.ird.cantharella.web.pages.domain.config.panels.ListErreurTestBioPanel;
import nc.ird.cantharella.web.pages.domain.config.panels.ListMethodeExtractionPanel;
import nc.ird.cantharella.web.pages.domain.config.panels.ListMethodePurificationPanel;
import nc.ird.cantharella.web.pages.domain.config.panels.ListMethodeTestBioPanel;
import nc.ird.cantharella.web.pages.domain.config.panels.ListPartiePanel;
import nc.ird.cantharella.web.pages.domain.config.panels.ListTypeDocumentPanel;
import nc.ird.cantharella.web.pages.domain.config.panels.RebuildLuceneIndexPanel;
import nc.ird.cantharella.web.utils.panels.CollapsiblePanel;
import nc.ird.cantharella.web.utils.security.AuthRole;
import nc.ird.cantharella.web.utils.security.AuthRoles;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 * Consulting of the Information System configuration
 * 
 * @author Adrien Cheype
 */
@AuthRoles({ AuthRole.ADMIN })
public final class ListConfigurationPage extends TemplatePage {

    /** The opened param value */
    private static final String OPENED_PARAM_VALUE = "opened";

    /**
     * Constructor
     * 
     * @param params The page parameters
     */
    public ListConfigurationPage(PageParameters params) {
        super(ListConfigurationPage.class);

        // check the parameters to know which panel must be opened
        boolean partieOpened = checkIfPanelOpened(params, "partie");
        boolean methExtrOpened = checkIfPanelOpened(params, "methodeExtraction");
        boolean methPuriOpened = checkIfPanelOpened(params, "methodePurification");
        boolean methTestOpened = checkIfPanelOpened(params, "methodeTestBio");
        boolean errTestOpened = checkIfPanelOpened(params, "erreurTestBio");
        boolean typeDocOpened = checkIfPanelOpened(params, "typeDocument");
        boolean rebIndexOpened = checkIfPanelOpened(params, "rebuildLuceneIndex");

        if (params.get("openedTab").equals("partie")) {
            partieOpened = true;
        } else if (params.get("openedTab").equals("methodeExtraction")) {
            methExtrOpened = true;
        } else {

            // initialize the Partie panel
            add(new CollapsiblePanel(getResource() + ".ListPartiePanel",
                    getStringModel("ListConfigurationPage.Parties"), partieOpened) {

                @Override
                protected Panel getInnerPanel(String markupId) {
                    return new ListPartiePanel(markupId);
                }
            });
        }

        // initialize the MethodeExtraction panel
        add(new CollapsiblePanel(getResource() + ".ListMethodeExtractionPanel",
                getStringModel("ListConfigurationPage.MethodesExtraction"), methExtrOpened) {

            @Override
            protected Panel getInnerPanel(String markupId) {
                return new ListMethodeExtractionPanel(markupId);
            }
        });

        // initialize the MethodePurification panel
        add(new CollapsiblePanel(getResource() + ".ListMethodePurificationPanel",
                getStringModel("ListConfigurationPage.MethodesPurification"), methPuriOpened) {

            @Override
            protected Panel getInnerPanel(String markupId) {
                return new ListMethodePurificationPanel(markupId);
            }
        });

        // initialize the MethodeTest panel
        add(new CollapsiblePanel(getResource() + ".ListMethodeTestBioPanel",
                getStringModel("ListConfigurationPage.MethodesTestBio"), methTestOpened) {

            @Override
            protected Panel getInnerPanel(String markupId) {
                return new ListMethodeTestBioPanel(markupId);
            }
        });

        // initialize the TestErreur panel
        add(new CollapsiblePanel(getResource() + ".ListErreurTestBioPanel",
                getStringModel("ListConfigurationPage.ErreursTestBio"), errTestOpened) {

            @Override
            protected Panel getInnerPanel(String markupId) {
                return new ListErreurTestBioPanel(markupId);
            }
        });

        // initialize the TypeDocument panel
        add(new CollapsiblePanel(getResource() + ".ListTypeDocumentPanel",
                getStringModel("ListConfigurationPage.TypesDocument"), typeDocOpened) {

            @Override
            protected Panel getInnerPanel(String markupId) {
                return new ListTypeDocumentPanel(markupId);
            }
        });

        // initialize the RebuidLuceneIndex panel
        add(new CollapsiblePanel(getResource() + ".RebuildLuceneIndexPanel",
                getStringModel("ListConfigurationPage.RebuildLuceneIndex"), rebIndexOpened) {
            @Override
            protected Panel getInnerPanel(String markupId) {
                return new RebuildLuceneIndexPanel(markupId);
            }
        });
    }

    /**
     * Check in the request parameters if a panel should be opened
     * 
     * @param params The parameters
     * @param paramName The parameter name to check
     * @return the answer
     */
    private boolean checkIfPanelOpened(PageParameters params, String paramName) {
        return params.get(paramName) != null && OPENED_PARAM_VALUE.equals(params.get(paramName).toString());
    }
}
