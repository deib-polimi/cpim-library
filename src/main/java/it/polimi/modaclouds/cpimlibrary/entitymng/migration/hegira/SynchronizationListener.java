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

import it.polimi.hegira.zkWrapper.ZKclient;
import it.polimi.modaclouds.cpimlibrary.entitymng.migration.MigrationManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.shared.SharedCountListener;
import org.apache.curator.framework.recipes.shared.SharedCountReader;
import org.apache.curator.framework.state.ConnectionState;

/**
 * An implementation of {@link org.apache.curator.framework.recipes.shared.SharedCountListener}
 * that listen and reacts to synchronization state changes.
 *
 * @author Fabio Arcidiacono.
 * @see org.apache.curator.framework.recipes.shared.SharedCountListener
 * @see it.polimi.modaclouds.cpimlibrary.entitymng.migration.MigrationManager
 */
@Slf4j
public class SynchronizationListener implements SharedCountListener {

    @Override
    public void countHasChanged(SharedCountReader sharedCount, int newCount) throws Exception {
        log.info("Shared counter has changed to: " + newCount);
        boolean isSynchronizing = ZKclient.toBoolean(newCount);
        HegiraConnector.getInstance().setSynchronizing(isSynchronizing);
        MigrationManager migrant = MigrationManager.getInstance();
        if (isSynchronizing && !migrant.isMigrating()) {
            migrant.startMigration();
        } else if (!isSynchronizing && migrant.isMigrating()) {
            migrant.stopMigration();
        }
    }

    @Override
    public void stateChanged(CuratorFramework client, ConnectionState newState) {
        // TODO handle in some ways?
        log.warn("ZooKeeper connection changed: " + newState.toString());
    }
}
