/*
 * #%L
 * Cantharella :: Web
 * $Id: TableExportToolbar.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.web/src/main/java/nc/ird/cantharella/web/utils/data/TableExportToolbar.java $
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
package nc.ird.cantharella.web.utils.data;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.apache.wicket.Component;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.export.CSVDataExporter;
import org.apache.wicket.extensions.markup.html.repeater.data.table.export.ExportToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.table.export.IDataExporter;
import org.apache.wicket.markup.html.WebComponent;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.link.ResourceLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.resource.ContextRelativeResource;
import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.request.resource.ResourceStreamResource;
import org.apache.wicket.util.resource.IResourceStream;

/**
 * Cantharella customisation of wicket {@link ExportToolbar}.
 * 
 * @author Eric Chatellier
 */
public class TableExportToolbar extends ExportToolbar {

    /**
     * Constructor adding csv export configuration.
     * 
     * @param table table to add export to
     * @param fileName export filename
     * @param locale locale
     */
    public TableExportToolbar(DataTable<?, ?> table, String fileName, Locale locale) {
        super(table);

        // set message model
        setMessageModel(new StringResourceModel("ExportTo", this, null));

        // file name model
        DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd-HH_mm-");
        String headerFilename = dateFormat.format(new Date());
        setFileNameModel(new Model<String>(headerFilename + fileName));

        CSVDataExporter csvDataExporter = new CSVDataExporter();
        // configuration du separateur suivant la locale
        // fr : ;
        // en : ,
        if (Locale.FRENCH.equals(locale)) {
            csvDataExporter.setDelimiter(';');
        } else {
            csvDataExporter.setDelimiter(',');
        }
        addDataExporter(csvDataExporter);
    }

    /**
     * Creates a new link to the exported data for the provided {@link IDataExporter}.
     * 
     * @param componentId The component of the link.
     * @param dataExporter The data exporter to use to export the data.
     * @return a new link to the exported data for the provided {@link IDataExporter}.
     */
    protected Component createExportLink(String componentId, final IDataExporter dataExporter) {
        IResource resource = new ResourceStreamResource() {
            @Override
            protected IResourceStream getResourceStream() {
                return new DataExportResourceStreamWriter(dataExporter, getTable());
            }
        }.setFileName(getFileNameModel().getObject() + "." + dataExporter.getFileNameExtension());

        return new ImageResourceLink(componentId, resource, dataExporter);
    }

    /**
     * Panel which include a image. Used with the LinkableImagePropertyColumn$LinkablePanel.html file
     */
    public class ImageResourceLink extends Panel {

        /**
         * Constructor
         * 
         * @param id Component id
         * @param resource link resource
         * @param dataExporter data exporter
         */
        public ImageResourceLink(String id, IResource resource, IDataExporter dataExporter) {
            super(id);

            ResourceLink<Void> link = new ResourceLink<Void>("link", resource);
            link.setBody(dataExporter.getDataFormatNameModel());
            add(link);

            // add a link on <type>_text.png image
            // for CSV : csv_text.png image
            String type = dataExporter.getDataFormatNameModel().getObject().toLowerCase();
            WebComponent img = new Image("img", new ContextRelativeResource("images/" + type + "_text.png"));
            add(img);
        }
    }
}
