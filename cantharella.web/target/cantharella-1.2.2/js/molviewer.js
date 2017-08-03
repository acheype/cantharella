/*
 * #%L
 * Cantharella :: Web
 * $Id: molviewer.js 198 2013-04-15 10:31:40Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.web/src/main/webapp/js/molviewer.js $
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
function addChemCanvas(id, formulaString, width, heigth) {
	//initialize component and set visual specifications
    var viewerCanvas = new ChemDoodle.ViewerCanvas(id, width, heigth);
    viewerCanvas.specs.bonds_width_2D = .6;
    viewerCanvas.specs.bonds_saturationWidth_2D = .18;
    viewerCanvas.specs.bonds_hashSpacing_2D = 2.5;
    viewerCanvas.specs.atoms_font_size_2D = 10;
    viewerCanvas.specs.atoms_font_families_2D = ["Helvetica", "Arial", "sans-serif"];
    viewerCanvas.specs.atoms_displayTerminalCarbonLabels_2D = true;

    var formula = ChemDoodle.readMOL(formulaString);

    // function to scale molecul to canvas
    var size = formula.getDimension();
    var scale = Math.min(viewerCanvas.width/size.x, viewerCanvas.height/size.y);
    viewerCanvas.loadMolecule(formula);
    viewerCanvas.specs.scale = scale*.7;
    viewerCanvas.repaint();
}

function addViewerMolecule(tagId) {
    if (tagId) {
        tagId = '#' + tagId.replace( /(:|\.)/g, "\\$1" );
    } else {
        tagId = '[formula]';
    }

    $(tagId).each(function(i, formulaTag) {
        // il faut faire un substring de 1 car on a ajoute un caractere pour
        // forcer l'existance du tag'
        var formulaString = $(formulaTag).attr('formula').substring(1);
        var id = $(formulaTag).attr('id');
        $(formulaTag).replaceWith("<a download='molecule'><canvas id='"+id+"'/></a><canvas id='"+id+"-big' style='display:none' />");

        addChemCanvas(id, formulaString, 100, 100);
        addChemCanvas(id + '-big', formulaString, 500, 300);

        // add download link
        var dataUrl = document.getElementById(id + '-big').toDataURL("image/png");
        $('#' + id).parent().attr('href', dataUrl);

        // link to colorbox
        $('#' + id).parent().colorbox({
        	photo: true
        });
    });
}

function addFullViewerMolecule(tagId) {
    if (tagId) {
        tagId = '#' + tagId.replace( /(:|\.)/g, "\\$1" );
    } else {
        tagId = '[formula]';
    }

    $(tagId).each(function(i, formulaTag) {
        // il faut faire un substring de 1 car on a ajoute un caractere pour
        // forcer l'existance du tag'
        var formulaString = $(formulaTag).attr('formula').substring(1);
        var id = $(formulaTag).attr('id');
        $(formulaTag).replaceWith("<a download='molecule'><canvas id='"+id+"'/></a>");

        addChemCanvas(id, formulaString, 500, 300);

        // add download link
        var dataUrl = document.getElementById(id).toDataURL("image/png");
        $('#' + id).parent().attr('href', dataUrl);
    });
}
