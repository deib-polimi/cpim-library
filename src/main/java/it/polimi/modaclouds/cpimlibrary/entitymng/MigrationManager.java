/**
 * Copyright 2013 deib-polimi
 * Contact: deib-polimi <marco.miglierina@polimi.it>
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package it.polimi.modaclouds.cpimlibrary.entitymng;

import javax.persistence.Query;

/**
 * @author Fabio Arcidiacono.
 */
public class MigrationManager {

    private static MigrationManager instance = null;

    private MigrationManager() {
    }

    public static synchronized MigrationManager getInstance() {
        if (instance == null) {
            instance = new MigrationManager();
        }
        return instance;
    }

    public boolean isMigrating() {
        // TODO
        return false;
    }

    public void propagate(String statement) {
        // TODO
        // send statement to migration system
    }

    public String generateInsertStatement(Object entity) {
        //TODO
        // generate insert statement using reflections
        // need to check also CascadeType and generate inner object INSERT statement accordingly
        return null;
    }

    public boolean isUpdate(Query query) {
        //TODO
        return true;
    }

    public String generateUpdateStatement(Query query) {
        //TODO
        return null;
    }

    public String generateDeleteStatement(Query query) {
        //TODO
        return null;
    }
}
