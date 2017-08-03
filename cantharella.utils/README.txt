Module.utils 
============

This is the readme file for the utils module of the Cantharella projet.
This module gathers tools usable in common Java projects.

The information system (IS) Cantharella: Pharmacochemical database of natural substances, designed and developed by 
IRD (www.ird.fr), share and sustain pharmacochemical data of all organisms collected for the study of their natural
substances, with a controlled access via internet. 
The IS provides access to harvest and taxonomic data, monitor various chemical processes of extraction and 
purification, and finally centralize all biological activities. The database is progressive according to the extraction
and purification methods needed and the biological tests performed.

Cantharella is an open source information system based on Java components and a PostgreSQL database.  

License
-------

Cantharella is distributed under the terms of the under the terms of the GNU General Public License, version 3.0. The 
text is included in the file LICENSE is in the root of the project.

Dependencies
------------
Module.utils is dependent to some librairies from another open source projects. This is the list of the dependencies and
their licenses.
  - Apache Commons (http://commons.apache.org), under the Apache License 2.0 
  - JUnit (www.junit.org), under the Common Public License 1.0
  - ICU4J (http://www.icu-project.org), under the ICU License (compatible with GNU GPL)

You can refer to the licenses folder of the project to see each license conditions.
The dependency libraries are managed by the the Maven build tool (http://maven.apache.org). The Maven's dependency 
mechanism will download all the necessary libraries automatically. You can see the dependencies declared in the 
pom.xml files to known each library version.