---
-- #%L
-- Cantharella :: Data
-- $Id: create_db.sql 133 2013-02-19 11:02:25Z acheype $
-- $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.data/src/main/sql/create_db.sql $
-- %%
-- Copyright (C) 2009 - 2012 IRD (Institut de Recherche pour le Developpement) and by respective authors (see below)
-- %%
-- This program is free software: you can redistribute it and/or modify
-- it under the terms of the GNU Affero General Public License as published by
-- the Free Software Foundation, either version 3 of the License, or
-- (at your option) any later version.
-- 
-- This program is distributed in the hope that it will be useful,
-- but WITHOUT ANY WARRANTY; without even the implied warranty of
-- MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
-- GNU General Public License for more details.
-- 
-- You should have received a copy of the GNU Affero General Public License
-- along with this program.  If not, see <http://www.gnu.org/licenses/>.
-- #L%
---
CREATE ROLE cantharella LOGIN PASSWORD 'ctrl4';
CREATE DATABASE cantharella WITH ENCODING='UTF8' OWNER=cantharella;
GRANT ALL ON DATABASE cantharella TO cantharella;
\connect cantharella
ALTER SCHEMA public OWNER TO cantharella;
GRANT ALL ON SCHEMA public TO cantharella;
\q
