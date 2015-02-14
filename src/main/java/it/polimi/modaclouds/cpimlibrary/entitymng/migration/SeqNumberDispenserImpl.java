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

import it.polimi.modaclouds.cpimlibrary.entitymng.migration.hegira.HegiraConnector;
import it.polimi.modaclouds.cpimlibrary.exception.CloudException;
import it.polimi.modaclouds.cpimlibrary.exception.MigrationException;
import it.polimi.modaclouds.cpimlibrary.mffactory.MF;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;
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

    private final String CHARSET = "UTF-8";
    private String tableName;
    private int[] range;
    private int next;
    private int offset;
    private HegiraConnector hegiraConnector;

    public SeqNumberDispenserImpl(String tableName) {
        if (tableName == null) {
            throw new NullPointerException("Table name cannot be null");
        }
        this.tableName = tableName;
        this.hegiraConnector = HegiraConnector.getInstance();
        this.offset = MF.getFactory().getCloudMetadata().getSeqNumberRange();
        this.range = getAssignedSequenceNumbers();
        this.next = range[0];
    }

    private int[] getAssignedSequenceNumbers() {
        try {
            return hegiraConnector.assignSeqNrRange(this.tableName, offset);
        } catch (Exception e) {
            throw new MigrationException("Some error occurred while retrieving sequence number range for table [" + this.tableName + "]", e);
        }
    }

    @Override
    public String getTable() {
        return this.tableName;
    }

    @Override
    public void setOffset(int offset) {
        this.offset = offset;
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

    @Override
    public byte[] save() {
        String state = Arrays.toString(this.range) + ":" + this.next;
        return state.getBytes(Charset.forName(CHARSET));
    }

    @Override
    public boolean restore(byte[] content) throws CloudException {
        String state = new String(content, Charset.forName(CHARSET));
        String[] parts = state.split(":");
        if (parts.length == 1 || parts.length > 2) {
            throw new CloudException("state is malformed and cannot be restored. " + state);
        }
        String[] elements = parts[0].replace("[", "").replace("]", "").split(",");
        if (elements.length != 2) {
            throw new CloudException("range is malformed and cannot be restored. " + parts[0]);
        }

        int[] range;
        try {
            range = new int[]{Integer.parseInt(elements[0].trim()), Integer.parseInt(elements[1].trim())};
        } catch (NumberFormatException e) {
            throw new CloudException(e);
        }
        int next;
        try {
            next = Integer.parseInt(parts[1].trim());
        } catch (NumberFormatException e) {
            throw new CloudException(e);
        }

        if (next < range[0] || next > range[1]) {
            throw new CloudException("next sequence number (" + next + ") is outside range (" + Arrays.toString(range) + ")");
        }

        this.range = range;
        this.next = next;
        return true;
    }
}
