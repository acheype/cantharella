Cantharella 
===========

This is the readme file for the Cantharella projet.

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
text is included in the file LICENSE iq in the root of the project.

Java and database servers requirements
-------------------------------------------------

Cantharella requires at least Java 1.6. The application server for running your web application should adhere to the 
servlet specification version 2.5 or newer. Tomcat 6 or newer is recommended.
For the database, the IS requires a PostgreSQL database (version 8.0 or newer).

Dependencies
------------
Cantharella is dependent to some librairies from another open source projects. This is the list of the dependencies and
their licenses.
  - Apache Commons (http://commons.apache.org), under the Apache License 2.0 
  - JUnit (www.junit.org), under the Common Public License 1.0
  - ICU4J (http://www.icu-project.org), under the ICU License (compatible with GNU GPL)
  - Log4j (http://logging.apache.org/log4j), under the Apache License 2.0
  - SFL4J (http://www.slf4j.org), under the MIT license
  - AspectJ (http://www.eclipse.org/aspectj), under the Common Public License 1.0
  - Spring (http://www.springsource.org), under the Apache License 2.0
  - Javassist (http://www.jboss.org/javassist), under the GNU Lesser General Public License 2.1
  - Cglib (http://cglib.sourceforge.net), under the Apache License 1.1
  - Hibernate (http://www.hibernate.org), under the GNU Lesser General Public License 2.1
  - POI (http://poi.apache.org), under the Apache License 2.0
  - Wicket (http://wicket.apache.org), under the Apache License 2.0
  - PostgreSQL JDBC Driver (http://jdbc.postgresql.org), under the BSD License (same as the server)

You can refer to the licenses folder of the project to see each license conditions.
The dependency libraries are managed by the the Maven build tool (http://maven.apache.org). The Maven's dependency 
mechanism will download all the necessary libraries automatically. You can see the dependencies declared in the 
pom.xml files to known each library version.