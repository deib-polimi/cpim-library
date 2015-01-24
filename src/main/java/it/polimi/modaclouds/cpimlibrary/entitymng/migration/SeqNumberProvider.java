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

import it.polimi.modaclouds.cpimlibrary.blobmng.CloudBlobManager;
import it.polimi.modaclouds.cpimlibrary.entitymng.PersistenceMetadata;
import it.polimi.modaclouds.cpimlibrary.exception.CloudException;
import it.polimi.modaclouds.cpimlibrary.exception.MigrationException;
import it.polimi.modaclouds.cpimlibrary.mffactory.MF;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Manage a {@link it.polimi.modaclouds.cpimlibrary.entitymng.migration.SeqNumberDispenserImpl}
 * foreach table is registered.
 * <p/>
 * A table can be registered at runtime using the {@code addTable(tableName)} method.
 * All the persisted tables stated in persistence.xml are automatically registered in construction.
 * <p/>
 * The class is managed as a singleton instance so to get the next generated sequence number for a table
 * simply call {@code SeqNumberProvider.getInstance().getNextSequenceNumber(tableName)}.
 *
 * @author Fabio Arcidiacono.
 * @see it.polimi.modaclouds.cpimlibrary.entitymng.migration.SeqNumberDispenser
 * @see it.polimi.modaclouds.cpimlibrary.entitymng.migration.SeqNumberDispenserImpl
 */
public class SeqNumberProvider {

    private static SeqNumberProvider instance = null;
    private Map<String, SeqNumberDispenser> dispenser;
    private final boolean backupToBlob;
    private String blobPrefix;
    private CloudBlobManager blobManager;

    private SeqNumberProvider() {
        this.dispenser = new HashMap<>();
        Set<String> persistedTables = PersistenceMetadata.getInstance().getPersistedTables();
        for (String table : persistedTables) {
            this.addTable(table);
        }
        this.backupToBlob = MF.getFactory().getCloudMetadata().getBackupToBlob();
        if (backupToBlob) {
            this.blobPrefix = MF.getFactory().getCloudMetadata().getBackupPrefix();
            this.blobManager = MF.getFactory().getBlobManagerFactory().createCloudBlobManager();
        }
    }

    public static synchronized SeqNumberProvider getInstance() {
        if (instance == null) {
            instance = new SeqNumberProvider();
        }
        return instance;
    }

    /**
     * Register a table so its ids can be managed through the migration system.
     * <p/>
     * Before registering the table, check if a blob with previous backup exists
     * in this case restore the state of the created table dispenser.
     *
     * @param tableName the table name
     */
    public void addTable(String tableName) {
        SeqNumberDispenserImpl tableDispenser = new SeqNumberDispenserImpl(tableName);
        if (backupToBlob) {
            restoreDispenserState(tableDispenser);
        }
        this.dispenser.put(tableName, tableDispenser);
    }

    /**
     * Gives the next sequence number assigned by migration system for the given table
     * and backup to a blob the new state of the table dispenser.
     *
     * @param tableName the table name
     *
     * @return the sequence number
     *
     * @throws java.lang.RuntimeException if {@code tableName} was not registered
     */
    public int getNextSequenceNumber(String tableName) {
        SeqNumberDispenser tableDispenser = this.dispenser.get(tableName);
        if (tableDispenser == null) {
            throw new MigrationException("Table [" + tableName + "] was not registered");
        }
        int next = tableDispenser.nextSequenceNumber();
        if (backupToBlob) {
            backupDispenserState(tableDispenser);
        }
        return next;
    }

    private String getFileName(SeqNumberDispenser tableDispenser) {
        return blobPrefix + tableDispenser.getTable();
    }

    private void backupDispenserState(SeqNumberDispenser tableDispenser) {
        byte[] newState = tableDispenser.save();
        blobManager.uploadBlob(newState, getFileName(tableDispenser));
    }

    private void restoreDispenserState(SeqNumberDispenser tableDispenser) {
        String blobFileName = getFileName(tableDispenser);
        if (blobManager.fileExists(blobFileName)) {
            try {
                byte[] savedState = blobManager.downloadBlob(blobFileName).getContent();
                tableDispenser.restore(savedState);
            } catch (IOException | CloudException e) {
                throw new MigrationException("Some problem occurred while restoring the previous state for table [" + tableDispenser.getTable() + "]", e);
            }
        }
    }
}
