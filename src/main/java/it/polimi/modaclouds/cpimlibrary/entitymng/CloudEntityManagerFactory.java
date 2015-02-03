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
package it.polimi.modaclouds.cpimlibrary.entitymng;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * @author Fabio Arcidiacono.
 */
public class CloudEntityManagerFactory {

    private EntityManagerFactory factory = null;
    private String persistenceUnit = null;

    public CloudEntityManagerFactory(String persistenceUnit) {
        this.persistenceUnit = persistenceUnit;
    }

    /**
     * @return a new instance of {@link it.polimi.modaclouds.cpimlibrary.entitymng.CloudEntityManager}
     */
    public CloudEntityManager createCloudEntityManager() {
        if (factory == null || !factory.isOpen()) {
            this.factory = Persistence.createEntityManagerFactory(persistenceUnit);
        }
        return new CloudEntityManager(factory.createEntityManager());
    }

    public void close() {
        factory.close();
    }
}