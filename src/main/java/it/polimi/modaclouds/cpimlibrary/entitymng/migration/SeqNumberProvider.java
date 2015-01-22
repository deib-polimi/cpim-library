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

import it.polimi.modaclouds.cpimlibrary.entitymng.PersistenceMetadata;
import it.polimi.modaclouds.cpimlibrary.exception.MigrationException;

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

    private SeqNumberProvider() {
        this.dispenser = new HashMap<>();
        Set<String> persistedTables = PersistenceMetadata.getInstance().getPersistedTables();
        for (String table : persistedTables) {
            this.addTable(table);
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
     *
     * @param tableName the table name
     */
    public void addTable(String tableName) {
        this.dispenser.put(tableName, new SeqNumberDispenserImpl(tableName));
    }

    /**
     * Gives the next sequence number assigned by migration system for the given table.
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
        return tableDispenser.nextSequenceNumber();
    }
}
