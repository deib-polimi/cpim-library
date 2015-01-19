package it.polimi.modaclouds.cpimlibrary.entitymng.migration;

import it.polimi.hegira.zkWrapper.ZKclient;
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
        MigrationManager migrant = MigrationManager.getInstance();
        boolean isSynchronizing = ZKclient.toBoolean(newCount);
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
