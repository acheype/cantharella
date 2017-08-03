#!/bin/bash

function onexit(){
    echo "An error occured - init_cantharella_db script exiting !"
}

SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

set -e
trap onexit EXIT

echo "--- Init Cantharella DB ---"
echo "$SCRIPT_DIR"
echo "No 'cantharella' database found. The database is going to be created."
psql --username "$POSTGRES_USER" -f "$SCRIPT_DIR/sql/create_db.sql"
echo "* create_db.sql imported *"
psql --username cantharella -d cantharella -f $SCRIPT_DIR/sql/cantharella_schema_1.2.sql
echo "* cantharella_schema_1.2.sql imported *"
echo "--- End of Init Cantharella DB ---"
echo -e "\n"
echo "**** You can login with an admin account with : ****"
echo "****   - email : islog@ird.fr                   ****"
echo "****   - password : password                    ****"
echo -e "\n"


