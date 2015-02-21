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

import it.polimi.modaclouds.cpimlibrary.exception.CloudException;

/**
 * @author Fabio Arcidiacono.
 */
public interface SeqNumberDispenser {

    /**
     * Returns the table the dispenser is responsible for.
     *
     * @return the string table name
     */
    public String getTable();

    /**
     * A setter to modify the default offset at runtime.
     * The default one is the one specified in the <i>migration.xml</i> .
     *
     * @param offset the new offset
     */
    public void setOffset(int offset);

    /**
     * Get the current offset value.
     */
    public int geOffset();

    /**
     * Gives the next sequence number assigned by migration system.
     *
     * @return the next sequence number
     */
    public int nextSequenceNumber();

    /**
     * Returns a representation of the current dispenser state.
     *
     * @return the current state representation.
     */
    public byte[] save();

    /**
     * Restore the dispenser state.
     *
     * @param content a state representation obtained through {@link SeqNumberDispenser#save()}.
     *
     * @return true if success, false otherwise
     *
     * @throws it.polimi.modaclouds.cpimlibrary.exception.CloudException of something goes wrong during restore.
     */
    public boolean restore(byte[] content) throws CloudException;
}
