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
package it.polimi.modaclouds.cpimlibrary.entitymng.migration.hegira;

/**
 * @author Fabio Arcidiacono.
 */
public interface ZKAdapter {

    public int[] assignSeqNrRange(String tableName, int offset) throws Exception;

    public int assignSeqNr(String tableName) throws Exception;

    public void setSynchronizing(boolean status);

    public boolean isSynchronizing();
}
