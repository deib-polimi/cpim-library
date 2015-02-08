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
package it.polimi.modaclouds.cpimlibrary.entitymng.migration.hegira;

import it.polimi.hegira.zkWrapper.rest.RestClient;
import it.polimi.modaclouds.cpimlibrary.exception.MigrationException;

/**
 * @author Fabio Arcidiacono.
 */
public class ZKHttp implements ZKAdapter {

    private final RestClient restClient;

    public ZKHttp(String basePath) {
        this.restClient = new RestClient(basePath);
    }

    @Override
    public int[] assignSeqNrRange(String tableName, int offset) throws Exception {
        return restClient.assignSeqNrRange(tableName, offset);
    }

    @Override
    public int assignSeqNr(String tableName) throws Exception {
        return restClient.assignSeqNr(tableName);
    }

    @Override
    public void setSynchronizing(boolean status) {
        /* do nothing */
    }

    @Override
    public boolean isSynchronizing() {
        try {
            return restClient.isSynchronizing();
        } catch (Exception e) {
            throw new MigrationException("Some error occurred", e);
        }
    }
}
