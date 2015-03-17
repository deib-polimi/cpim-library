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
package it.polimi.modaclouds.cpimlibrary.exception;

/**
 * Unchecked exception thrown by NoSQL service when some problems
 * occurs when reading persistence metadata.
 *
 * @author Fabio Arcidiacono.
 */
public class PersistenceMetadataException extends RuntimeException {

    public PersistenceMetadataException(String msg) {
        super(msg);
    }

    public PersistenceMetadataException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
