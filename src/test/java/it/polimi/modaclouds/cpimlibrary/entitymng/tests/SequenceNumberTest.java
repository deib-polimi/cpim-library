package it.polimi.modaclouds.cpimlibrary.entitymng.tests;

import it.polimi.modaclouds.cpimlibrary.entitymng.PersistenceMetadata;
import it.polimi.modaclouds.cpimlibrary.entitymng.migration.MigrationManager;
import it.polimi.modaclouds.cpimlibrary.entitymng.migration.SeqNumberProvider;
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
        seqNumberProvider.addTable("Goofy");

        int id = seqNumberProvider.getNextSequenceNumber("Goofy");
        int id2 = seqNumberProvider.getNextSequenceNumber("Goofy");
        int id3 = seqNumberProvider.getNextSequenceNumber("Goofy");
        Assert.assertFalse(id2 == id);
        Assert.assertFalse(id3 == id2);
        Assert.assertFalse(id3 == id);

        for (String table : PersistenceMetadata.getInstance().getPersistedTables()) {
            Assert.assertNotNull(seqNumberProvider.getNextSequenceNumber(table));
        }

        thrown.expect(RuntimeException.class);
        seqNumberProvider.getNextSequenceNumber("Non-Existent-Table");
    }
}
