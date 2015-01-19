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
package it.polimi.modaclouds.cpimlibrary.mffactory;

import it.polimi.modaclouds.cpimlibrary.CloudMetadata;
import it.polimi.modaclouds.cpimlibrary.blobmng.CloudBlobManagerFactory;
import it.polimi.modaclouds.cpimlibrary.entitymng.CloudEntityManagerFactory;
import it.polimi.modaclouds.cpimlibrary.exception.ParserConfigurationFileException;
import it.polimi.modaclouds.cpimlibrary.mailservice.CloudMailManager;
import it.polimi.modaclouds.cpimlibrary.memcache.CloudMemcache;
import it.polimi.modaclouds.cpimlibrary.msgqueuemng.CloudMessageQueueFactory;
import it.polimi.modaclouds.cpimlibrary.sqlservice.CloudSqlService;
import it.polimi.modaclouds.cpimlibrary.taskqueuemng.CloudTaskQueueFactory;

import java.sql.Connection;
import java.util.Map;

/**
 * This class allows to invoke all the services in a platform-independent way.
 */
public class MF {

    private static MF _instance = null;
    private CloudMetadata metadata = null;
    private CloudEntityManagerFactory emfInstance = null;
    private CloudBlobManagerFactory bmfInstance = null;
    private CloudMessageQueueFactory mqfInstance = null;
    private CloudTaskQueueFactory tqfInstance = null;

    private MF() {
        try {
            metadata = CloudMetadata.getCloudMetadata();
        } catch (ParserConfigurationFileException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method is used to create a general ManagerFactory, with the
     * singleton technique. In the {@code MF} object are present all the methods
     * used to instantiate all the objects of the available services on the
     * Cloud.
     *
     * @return the factory of all available services
     */
    public static MF getFactory() {
        if (_instance == null) {
            _instance = new MF();
        }
        return _instance;
    }

    /**
     * Convenient method to get parsed metadata from configuration files.
     *
     * @return a {@link it.polimi.modaclouds.cpimlibrary.CloudMetadata} instance
     */
    public CloudMetadata getCloudMetadata() {
        return this.metadata;
    }

    /**
     * Returns a {@code CloudMailManager} object that can then be used to manage
     * the mail service.
     *
     * @return the manager of the mail service
     *
     * @see CloudMailManager
     */
    public CloudMailManager getMailManager() {
        return CloudMailManager.getCloudMailManager(metadata);
    }

    /**
     * Returns a {@code CloudEntityManagerFactory} object used to instantiate
     * the {@code CloudEntityManager}. This method is used to create the manager
     * of the NoSQL service.
     *
     * @return the factory of the {@code CloudEntityManager}
     *
     * @see CloudEntityManagerFactory
     */
    public CloudEntityManagerFactory getEntityManagerFactory() {
        if (emfInstance == null) {
            emfInstance = new CloudEntityManagerFactory(metadata.getPersistenceUnit());
        }
        return emfInstance;
    }

    /**
     * Returns a Map<String, String> containing the information saved into <i>persistence.xml</i>.
     *
     * @return the map returned by {@link it.polimi.modaclouds.cpimlibrary.CloudMetadata#getPersistenceInfo()}
     *
     * @see it.polimi.modaclouds.cpimlibrary.CloudMetadata
     */
    public Map<String, String> getPersistenceUnitInfo() {
        return metadata.getPersistenceInfo();
    }

    /**
     * Returns a {@code CloudTaskQueueFactory} object used to instantiate a
     * {@code CloudTaskQueue}. This factory is used to create a queue of the
     * TaskQueue service.
     *
     * @return the factory of the {@code CloudTaskQueue}
     *
     * @see CloudTaskQueueFactory
     */
    public CloudTaskQueueFactory getTaskQueueFactory() {
        if (tqfInstance == null) {
            tqfInstance = CloudTaskQueueFactory
                    .getCloudTaskQueueFactory(metadata);
        }
        return tqfInstance;
    }

    /**
     * Returns a {@code CloudMessageQueueFactory} object used to instantiate a
     * {@code CloudMessageQueue}.
     *
     * @return the factory of the CloudMessageQueue
     *
     * @see CloudMessageQueueFactory
     */
    public CloudMessageQueueFactory getMessageQueueFactory() {
        if (mqfInstance == null) {
            mqfInstance = CloudMessageQueueFactory
                    .getCloudMessageQueueFactory(metadata);
        }
        return mqfInstance;
    }

    /**
     * Returns a {@code CloudBlobManagerFactory} object used to instantiate a
     * {@code CloudBlobManager}. This method is used to manage the upload and
     * the download of files on the Cloud.
     *
     * @return the factory of the {@code CloudBlobManager}
     *
     * @see CloudBlobManagerFactory
     */
    public CloudBlobManagerFactory getBlobManagerFactory() {
        if (bmfInstance == null) {
            bmfInstance = CloudBlobManagerFactory
                    .getCloudBlobManagerFactory(metadata);
        }
        return bmfInstance;
    }

    /**
     * Returns a {@link java.sql.Connection} object that contains the method to
     * use the SQL service.
     *
     * @return {@code Connection} object of java.sql package
     *
     * @see Connection
     */
    public CloudSqlService getSQLService() {
        return CloudSqlService.getCloudSqlService(metadata);
    }

    /**
     * Returns a {@code CloudMemcache} object. This object is used to manage the
     * Memcache service.
     *
     * @return the manager of the Memcache service.
     *
     * @see CloudMemcache
     */
    public CloudMemcache getCloudMemcache() {
        return CloudMemcache.getCloudMemcache(metadata);
    }
}
