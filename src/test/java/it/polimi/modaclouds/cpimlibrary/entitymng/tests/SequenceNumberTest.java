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
import it.polimi.modaclouds.cpimlibrary.entitymng.migration.SeqNumberDispenserImpl;
import it.polimi.modaclouds.cpimlibrary.entitymng.migration.SeqNumberProvider;
import it.polimi.modaclouds.cpimlibrary.exception.CloudException;
import it.polimi.modaclouds.cpimlibrary.mffactory.MF;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.nio.charset.Charset;

/**
 * @author Fabio Arcidiacono.
 */
public class SequenceNumberTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testSequenceNumber() {
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
    }

    @Test
    public void testOffsetConfiguration() {
        SeqNumberProvider seqNumberProvider = SeqNumberProvider.getInstance();

        int defaultOffset = MF.getFactory().getCloudMetadata().getSeqNumberRange();
        Assert.assertEquals(defaultOffset, seqNumberProvider.getOffset("Department"));

        seqNumberProvider.setOffset("Department", 50);
        Assert.assertEquals(50, seqNumberProvider.getOffset("Department"));
    }

    @Test
    public void testNonExistentTable() {
        try {
            SeqNumberProvider.getInstance().getOffset("pippo");
        } catch (IllegalArgumentException e) {
            /* that's fine */
        }
        try {
            SeqNumberProvider.getInstance().setOffset("pippo", 50);
        } catch (IllegalArgumentException e) {
            /* that's fine */
        }
        try {
            SeqNumberProvider.getInstance().getNextSequenceNumber("pippo");
        } catch (IllegalArgumentException e) {
            /* that's fine */
        }
    }

    @Test
    public void testSaveAndRestore() {
        SeqNumberDispenserImpl dispenser = new SeqNumberDispenserImpl("Test");
        byte[] state;

        try {
            state = dispenser.save();
            Assert.assertNotNull(state);
            dispenser.restore(state);
        } catch (CloudException e) {
            Assert.fail(e.getMessage());
        }

        try {
            state = "".getBytes(Charset.forName("UTF-8"));
            dispenser.restore(state);
        } catch (CloudException e) {
            // that's fine
        }

        try {
            state = "[11]:11".getBytes(Charset.forName("UTF-8"));
            dispenser.restore(state);
        } catch (CloudException e) {
            // that's fine
        }
        try {
            state = "[11,e]:11".getBytes(Charset.forName("UTF-8"));
            dispenser.restore(state);
        } catch (CloudException e) {
            // that's fine
        }
        try {
            state = "[e,11]:11".getBytes(Charset.forName("UTF-8"));
            dispenser.restore(state);
        } catch (CloudException e) {
            // that's fine
        }
        try {
            state = "[10,20]:f".getBytes(Charset.forName("UTF-8"));
            dispenser.restore(state);
        } catch (CloudException e) {
            // that's fine
        }
        try {
            state = "[10,20]:8".getBytes(Charset.forName("UTF-8"));
            dispenser.restore(state);
        } catch (CloudException e) {
            // that's fine
        }
        try {
            state = "[10,20]:33".getBytes(Charset.forName("UTF-8"));
            dispenser.restore(state);
        } catch (CloudException e) {
            // that's fine
        }
        try {
            state = "[10,20]:15".getBytes(Charset.forName("UTF-8"));
            dispenser.restore(state);
        } catch (CloudException e) {
            // that's fine
        }
    }
}
