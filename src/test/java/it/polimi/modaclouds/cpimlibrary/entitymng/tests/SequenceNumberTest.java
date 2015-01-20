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
package it.polimi.modaclouds.cpimlibrary.entitymng.tests;

import it.polimi.modaclouds.cpimlibrary.entitymng.PersistenceMetadata;
import it.polimi.modaclouds.cpimlibrary.entitymng.migration.MigrationManager;
import it.polimi.modaclouds.cpimlibrary.entitymng.migration.SeqNumberProvider;
import it.polimi.modaclouds.cpimlibrary.mffactory.MF;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * @author Fabio Arcidiacono.
 */
public class SequenceNumberTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testSequenceNumber() {
        MigrationManager migrant = MigrationManager.getInstance();
        Assert.assertNotNull(migrant.getZKclient());

        SeqNumberProvider seqNumberProvider = SeqNumberProvider.getInstance();

        int range = MF.getFactory().getCloudMetadata().getSeqNumberRange();
        for (String table : PersistenceMetadata.getInstance().getPersistedTables()) {
            int[] receivedIds = new int[range * 2];
            for (int i = 0; i < range * 2; i++) {
                int id = seqNumberProvider.getNextSequenceNumber(table);
                Assert.assertNotNull(id);
                receivedIds[i] = id;
            }
            // System.out.println(table + " " + Arrays.toString(receivedIds));
            for (int i = 1; i < receivedIds.length - 1; i++) {
                Assert.assertFalse(receivedIds[i - 1] == receivedIds[i]);
            }
        }

        thrown.expect(RuntimeException.class);
        seqNumberProvider.getNextSequenceNumber("Non-Existent-Table");
    }
}
