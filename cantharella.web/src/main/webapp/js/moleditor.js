/*
 * #%L
 * Cantharella :: Web
 * $Id: moleditor.js 133 2013-02-19 11:02:25Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.web/src/main/webapp/js/moleditor.js $
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
function addEditorMolecule(tagId) {
    if (tagId) {
        tagId = '#' + tagId.replace( /(:|\.)/g, "\\$1" );
    } else {
        tagId = '[formula]';
    }

    $(tagId).each(function(i, formulaTag) {
    	
        // il faut faire un substring de 1 car on a ajoute un caractere pour
        // forcer l'existance du tag'
        var formulaString = $(formulaTag).attr('formula').substring(1);
        var id = $(formulaTag).attr('id').replace(/\./g, '_') + '_editor';
        $(formulaTag).after("<div><canvas id='"+id+"'/><div>");

        // changes the default JMol color of hydrogen to black so it appears on white backgrounds
        ChemDoodle.ELEMENT['H'].jmolColor = 'black';
        // darkens the default JMol color of sulfur so it appears on white backgrounds
        ChemDoodle.ELEMENT['S'].jmolColor = '#B9A130';
        var sketcher = new ChemDoodle.SketcherCanvas(id, 500, 300,{oneMolecule:true});
        sketcher.specs.atoms_displayTerminalCarbonLabels_2D = true;
        sketcher.specs.atoms_useJMOLColors = true;
        sketcher.specs.bonds_clearOverlaps_2D = true;
        sketcher.repaint();

        // on se met listener du submit de la form pour pousser la molecule
        // editer dans le champs de sauvegarde
        $(formulaTag).parents('form').submit(function() {
            var mol = sketcher.getMolecule();
            var molString = ChemDoodle.writeMOL(mol);
            $(formulaTag).val(molString);
            return true;
        });
        if (formulaString.length != 0) {
        	var formula = ChemDoodle.readMOL(formulaString);
        	sketcher.loadMolecule(formula);
        }
    });

}
