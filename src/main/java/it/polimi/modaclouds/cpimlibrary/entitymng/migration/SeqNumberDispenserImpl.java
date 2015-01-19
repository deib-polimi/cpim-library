package it.polimi.modaclouds.cpimlibrary.entitymng.migration;

import it.polimi.hegira.zkWrapper.ZKclient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.ArrayUtils;

import java.util.Arrays;

/**
 * An implementation of {@link it.polimi.modaclouds.cpimlibrary.entitymng.migration.SeqNumberDispenser}.
 * <p/>
 * Provides the next sequence generated sequence number through the method {@code nextSequenceNumber}.
 * <p/>
 * This implementation maintains an array of {@code int} which is gradually populated with more
 * sequence numbers retrieved from ZooKeeper when no sequence number is ready to be served.
 *
 * @author Fabio Arcidiacono.
 * @see it.polimi.modaclouds.cpimlibrary.entitymng.migration.SeqNumberDispenser
 */
@Slf4j
public class SeqNumberDispenserImpl implements SeqNumberDispenser {

    private static final int OFFSET = 10; // TODO goes in migration.xml
    private String tableName;
    private int[] sequenceNumbers;
    private int current;
    private ZKclient zKclient;

    public SeqNumberDispenserImpl(String tableName) {
        this.tableName = tableName;
        this.sequenceNumbers = new int[0];
        this.current = 0;
        this.zKclient = MigrationManager.getInstance().getZKclient();
    }

    private void getAssignedSequenceNumbers() {
        try {
            int[] more = zKclient.assignSeqNrRange(this.tableName, OFFSET);
            this.sequenceNumbers = ArrayUtils.addAll(this.sequenceNumbers, more);
        } catch (Exception e) {
            throw new RuntimeException("Some error occurred while retrieving a sequence number range", e);
        }
    }

    @Override
    public int nextSequenceNumber() {
        if (this.current == this.sequenceNumbers.length) {
            getAssignedSequenceNumbers();
        }
        int next = sequenceNumbers[current];
        log.info("TABLE: " + this.tableName + ", RANGE: " + Arrays.toString(sequenceNumbers) + ", NEXT: " + next);
        current++;
        return next;
    }
}
