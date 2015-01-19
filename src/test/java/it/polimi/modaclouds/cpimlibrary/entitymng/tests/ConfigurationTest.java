package it.polimi.modaclouds.cpimlibrary.entitymng.tests;

import it.polimi.modaclouds.cpimlibrary.CloudMetadata;
import it.polimi.modaclouds.cpimlibrary.exception.ParserConfigurationFileException;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Fabio Arcidiacono.
 */
public class ConfigurationTest {

    @Test
    public void configurationTest() throws ParserConfigurationFileException {
        CloudMetadata cloudMetadata = CloudMetadata.getCloudMetadata();

        Assert.assertFalse(cloudMetadata.getFollowCascades());
        Assert.assertEquals("localhost:2181", cloudMetadata.getZookeeperConnectionString());
        Assert.assertEquals(5, cloudMetadata.getSeqNumberRange());
    }
}
