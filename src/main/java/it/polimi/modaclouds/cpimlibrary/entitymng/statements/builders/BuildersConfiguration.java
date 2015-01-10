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
package it.polimi.modaclouds.cpimlibrary.entitymng.statements.builders;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * Maintains configurations for statement builders, provides methods to set and read configuration.
 * <p/>
 * By default cascade types are not followed, behavior can be changed at runtime by calling
 * {@link it.polimi.modaclouds.cpimlibrary.entitymng.statements.builders.BuildersConfiguration#setFollowCascades(boolean)}.
 *
 * @author Fabio Arcidiacono.
 * @see it.polimi.modaclouds.cpimlibrary.entitymng.statements.builders.StatementBuilder
 * @see it.polimi.modaclouds.cpimlibrary.entitymng.statements.Statement
 */
@Slf4j
public class BuildersConfiguration {

    private static BuildersConfiguration instance = null;
    @Setter private boolean followCascades;

    private BuildersConfiguration() {
        this.followCascades = false;
    }

    public static synchronized BuildersConfiguration getInstance() {
        if (instance == null) {
            instance = new BuildersConfiguration();
        }
        return instance;
    }

    public boolean followCascades() {
        return this.followCascades;
    }
}
