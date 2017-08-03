.. -
.. * #%L
.. * Cantharella :: Web
.. * $Id: integration-chemdoodle.rst 134 2013-02-19 17:45:15Z echatellier $
.. * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.web/src/site/rst/devel/integration-chemdoodle.rst $
.. * %%
.. * Copyright (C) 2009 - 2013 IRD (Institut de Recherche pour le Developpement) and by respective authors (see below)
.. * %%
.. * This program is free software: you can redistribute it and/or modify
.. * it under the terms of the GNU Affero General Public License as published by
.. * the Free Software Foundation, either version 3 of the License, or
.. * (at your option) any later version.
.. * 
.. * This program is distributed in the hope that it will be useful,
.. * but WITHOUT ANY WARRANTY; without even the implied warranty of
.. * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
.. * GNU General Public License for more details.
.. * 
.. * You should have received a copy of the GNU Affero General Public License
.. * along with this program.  If not, see <http://www.gnu.org/licenses/>.
.. * #L%
.. -

Intégration de ChemDoodle
=========================

Principe
--------

Création de deux scripts qui ajout le rendu ou l'editeur de molecule via les
methodes: addViewerMolecule et addEditorMolecule.

Création de deux behaviors, un pour la visualisation et un pour l'édition.

Les behaviors ajoutent toutes les CSS et Script necessaire dans le Header

Les behaviors force le positionnement de l'attribut id sur les elements qui
portent la formule et ajout d'un attribut formula sur l'élement qui contient
la formule.

Ajout d'un script qui appelle soit addViewerMolecule et addEditorMolecule
avec l'id de l'element.

Visulisation
------------

Le canvas de rendu prend la place de l'element precedent en gardant le meme
id

Edition
-------

Le canvas d'edition ce position apres l'element de la formule. Cet element
peut-etre un input de type hidden.

Le script apres avoir mis en place l'editeur, ce met listener du submit de
la form pour pouvoir pousser dans le champs Hidden la valeur de la formule
edite sous le format Mol.

Dans Wicket
-----------

Très peu de modification, seulement l'ajout du behavior sur les champs qui
represente la formule brut. Et utilisation d'un champs Hidden dans la page
web d'edition.

ChemDoodle
----------

Tous les fichiers specifiques a ChemDoodle ont ete mis dans un seul
repertoire pour facilement faire un changement de librairie si le besoin
arrivait.
