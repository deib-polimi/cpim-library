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

import it.polimi.modaclouds.cpimlibrary.CloudMetadata;
import it.polimi.modaclouds.cpimlibrary.exception.MigrationException;
import it.polimi.modaclouds.cpimlibrary.mffactory.MF;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Fabio Arcidiacono.
 */
@Slf4j
public class HegiraConnector {

    private static HegiraConnector instance = null;
    private ZKAdapter zkClient = null;

    private HegiraConnector() {
        CloudMetadata cloudMetadata = MF.getFactory().getCloudMetadata();
        String type = cloudMetadata.getZooKeeperType();
        if (type.equalsIgnoreCase("thread")) {
            log.info("Instantiating THREAD type ZKClient");
            zkClient = new ZKThread(cloudMetadata.getZookeeperConnectionString());
        } else if (type.equalsIgnoreCase("http")) {
            log.info("Instantiating HTTP type ZKClient");
            zkClient = new ZKHttp(cloudMetadata.getZookeeperConnectionString());
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

    public int[] assignSeqNrRange(String tableName, int offset) throws Exception {
        return zkClient.assignSeqNrRange(tableName, offset);
    }

    public int assignSeqNr(String tableName) throws Exception {
        return zkClient.assignSeqNr(tableName);
    }

    public void setSynchronizing(boolean status) {
        zkClient.setSynchronizing(status);
    }

    public boolean isSynchronizing() {
        return zkClient.isSynchronizing();
    }
}
