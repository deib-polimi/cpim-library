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
package it.polimi.modaclouds.cpimlibrary.entitymng.migration;

import it.polimi.hegira.zkWrapper.ZKclient;
import it.polimi.hegira.zkWrapper.rest.RestClient;
import it.polimi.modaclouds.cpimlibrary.CloudMetadata;
import it.polimi.modaclouds.cpimlibrary.exception.MigrationException;
import it.polimi.modaclouds.cpimlibrary.mffactory.MF;
import lombok.Setter;

/**
 * @author Fabio Arcidiacono.
 */
public class HegiraConnector {

    private static HegiraConnector instance = null;
    private ZKclient zkClient = null;
    private RestClient restClient = null;
    @Setter private boolean isSynchronizing;
    private boolean useRestClient;

    private HegiraConnector() {
        CloudMetadata cloudMetadata = MF.getFactory().getCloudMetadata();
        String type = cloudMetadata.getZooKeeperType();
        if (type.equalsIgnoreCase("thread")) {
            this.useRestClient = false;
            initializeZKClient(cloudMetadata.getZookeeperConnectionString());
        } else if (type.equalsIgnoreCase("http")) {
            this.useRestClient = true;
            initializeRestClient(cloudMetadata.getZookeeperConnectionString());
        } else {
            throw new MigrationException("Unrecognized type '" + type + "' for ZooKeeper client");
        }
    }

    public static HegiraConnector getInstance() {
        if (instance == null) {
            instance = new HegiraConnector();
        }
        return instance;
    }

    private void initializeZKClient(String connectionString) {
        this.zkClient = new ZKclient(connectionString);
        try {
            this.isSynchronizing = zkClient.isSynchronizing(new SynchronizationListener());
        } catch (Exception e) {
            throw new MigrationException("Cannot connect to ZooKeeper [" + connectionString + "]", e);
        }
    }

    private void initializeRestClient(String basePath) {
        this.restClient = new RestClient(basePath);
    }

    public int[] assignSeqNrRange(String tableName, int offset) throws Exception {
        if (useRestClient) {
            return restClient.assignSeqNrRange(tableName, offset);
        }
        return zkClient.assignSeqNrRange(tableName, offset);
    }

    public int assignSeqNr(String tableName) throws Exception {
        if (useRestClient) {
            return restClient.assignSeqNr(tableName);
        }
        return zkClient.assignSeqNr(tableName);
    }

    public boolean isSynchronizing() {
        if (useRestClient) {
            try {
                return restClient.isSynchronizing();
            } catch (Exception e) {
                throw new MigrationException("Some error occurred", e);
            }
        }
        return isSynchronizing;
    }
}
