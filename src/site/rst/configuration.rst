.. -
.. * #%L
.. * Cantharella
.. * $Id: configuration.rst 213 2013-04-30 10:52:22Z acheype $
.. * $HeadURL: https://svn.codelutin.com/cantharella/trunk/src/site/rst/configuration.rst $
.. * %%
.. * Copyright (C) 2009 - 2012 IRD (Institut de Recherche pour le Developpement) and by respective authors (see below)
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

Configuration
=============

Configuration file
~~~~~~~~~~~~~~~~~~

All configuration variables can be put in file **cantharella.conf**. This file can be in:

- classpath
- system configuration directory
- user configuration directory
- current directory
- JVM variables

Configuration files and variables are read in this order, last file found
overwrite variables from previous file.

Classpath
---------

This file contains default value and is found in cantharella WAR archive.

System configuration directory
------------------------------

- Linux : */etc/cantharella.conf*
- Windows : *C:\\Windows\\System32\\cantharella.conf*
- Mac OS X : */etc/cantharella.conf*

User configuration directory
----------------------------

- Linux : *${user.home}/.config/cantharella.conf*
- Windows : *${user.home}\\Application Data\\cantharella.conf*
- Mac OS X : *${user.home}/Library/Application Support/cantharella.conf*

Current directory
-----------------

The directory used to launch application.

JVM variables
-------------

You can add parameter to JVM command line to set variable.

Example::

  java -Ddb.user=$USER -Dmail.host=localhost ...

Adapt the variables
~~~~~~~~~~~~~~~~~~~~~~~

The default *cantharella.conf* file is listed below. You have to tune the variables available in 
order to adapt it to your need.
To launch the application in your environment, the first need is to set **db.url**, **db.user**,
**db.password**, **mail.from** and **mail.host**. ::

  # 
  # DATABASE CONFIGURATION
  #
  db.url=jdbc:postgresql:cantharella
  db.user=cantharella
  db.password=cantharella
  db.debug=false
  db.hbm2ddl=validate
  
  # Hibernate search lucene index location on filesystem.
  hibernate.search.indexBase=/tmp/cantharella
  # Hibernate search analyzer
  hibernate.search.analyzer=org.apache.lucene.analysis.fr.FrenchAnalyzer
  
  # 
  # SERVICE CONFIGURATION
  #
  mail.debug=false
  mail.from=no-reply@ird.fr
  mail.host=smtp
  
  # default administration login/password 
  admin.courriel=ISlog@ird.fr
  admin.password=password

  # Document's allowed extensions
  document.extension.allowed=jpeg,jpg,gif,png,xls,doc,pdf,cdx,mol

  #
  # WEB CONFIGURATION
  #
  app.debug=false
  app.optimize=true
  wicket.configuration=deployment
  log4j.config=classpath:log4j_prod.xml

  #document file max upload size (Mb)
  document.maxUploadSize=1