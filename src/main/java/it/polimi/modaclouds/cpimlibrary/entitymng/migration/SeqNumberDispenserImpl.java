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

import it.polimi.hegira.zkWrapper.ZKclient;
import it.polimi.modaclouds.cpimlibrary.blobmng.CloudDownloadBlob;
import it.polimi.modaclouds.cpimlibrary.exception.MigrationException;
import it.polimi.modaclouds.cpimlibrary.mffactory.MF;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
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

    private String tableName;
    private int[] range;
    private int next;
    private int offset;
    private ZKclient zKclient;

    public SeqNumberDispenserImpl(String tableName) {
        this.tableName = tableName;
        this.zKclient = MigrationManager.getInstance().getZKclient();
        this.offset = MF.getFactory().getCloudMetadata().getSeqNumberRange();
        this.range = getAssignedSequenceNumbers();
        this.next = range[0];
    }

    private int[] getAssignedSequenceNumbers() {
        try {
            return zKclient.assignSeqNrRange(this.tableName, offset);
        } catch (Exception e) {
            throw new MigrationException("Some error occurred while retrieving a sequence number range", e);
        }
    }

    @Override
    public int nextSequenceNumber() {
        int current = next;
        if (this.next == this.range[this.range.length - 1]) {
            this.range = getAssignedSequenceNumbers();
            this.next = this.range[0];
        } else {
            this.next++;
        }
        log.debug("TABLE: " + this.tableName + ", RANGE: " + Arrays.toString(range) + ", CURRENT: " + current + ", NEXT: " + next);
        return current;
    }
}
