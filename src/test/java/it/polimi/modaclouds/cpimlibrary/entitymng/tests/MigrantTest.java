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

import it.polimi.modaclouds.cpimlibrary.entitymng.migration.MigrationManager;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Fabio Arcidiacono.
 */
public class MigrantTest {

    @Test
    public void testNormalState() {
        MigrationManager migrant = MigrationManager.getInstance();

        Assert.assertFalse(migrant.isMigrating());
        try {
            migrant.stopMigration();
        } catch (IllegalStateException e) {
            // that's fine
        }
        try {
            migrant.propagate(null, null);
        } catch (IllegalStateException e) {
            // that's fine
        }
        try {
            migrant.propagate(null);
        } catch (IllegalStateException e) {
            // that's fine
        }
    }

    @Test
    public void testMigrationState() {
        MigrationManager migrant = MigrationManager.getInstance();

        migrant.startMigration();
        Assert.assertTrue(migrant.isMigrating());
        try {
            migrant.startMigration();
        } catch (IllegalStateException e) {
            // that's fine
        }
        migrant.stopMigration();
        Assert.assertFalse(migrant.isMigrating());
    }
}
