.. -
.. * #%L
.. * Cantharella
.. * $Id:
.. * $HeadURL:
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

Installation
============

This section describes how to install on the Tomcat application server in a linux system.

Software requirements
---------------------

You will first need to install the following prerequisites:

- Java Development Kit 1.7 or higher
- Tomcat 7.0 or higher
- PostgreSQL 9.0 or higher

Get the war and the database script
-----------------------------------

You can download the last release version files from here_, that is the WAR archive and the SQL
script to create the database schema. ::

  cd /tmp
  wget http://forge.codelutin.com/attachments/download/686/cantharella.web-1.1-rc-1.war
  wget http://forge.codelutin.com/attachments/download/700/cantharella_schema_1.1.sql

.. _here: http://forge.codelutin.com/projects/cantharella/files

Install the database
--------------------

To begin, start the SQL command with the postgres user. ::

  su postgres
  psql

Then, in the SQL command, add the user (don't forget to change the password!), the database and
its rights. ::

  CREATE ROLE cantharella LOGIN PASSWORD 'cantharella';
  CREATE DATABASE cantharella WITH ENCODING='UTF8' OWNER=cantharella;
  GRANT ALL ON DATABASE cantharella TO cantharella;
  \connect cantharella
  ALTER SCHEMA public OWNER TO cantharella;
  GRANT ALL ON SCHEMA public TO cantharella;
  \q

And still with the postgres user, import the database creation script. ::

  psql -d cantharella -f /tmp/cantharella_schema_1.1.sql
  exit

Deploy the WAR in Tomcat
------------------------

By default, Tomcat deploy automatically the WAR file in its webapps directory. Assuming that Tomcat
path is */opt/tomcat*, then do::

  cp /tmp/cantharella.web-1.1-rc-1.war /opt/tomcat/webapps

Then, you have to configure your Cantharella instance as described in this section_.

.. _section: configuration.rst

After the configuration step, you can restart your tomcat server, read the logs in the */opt/tomcat/logs* directory and verify that they
don't contain any errors. By default, the application logs will be in the *cantharella.log* file.