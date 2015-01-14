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
package it.polimi.modaclouds.cpimlibrary.entitymng.tests;

import it.polimi.modaclouds.cpimlibrary.entitymng.CloudEntityManager;
import it.polimi.modaclouds.cpimlibrary.entitymng.CloudEntityManagerFactory;
import it.polimi.modaclouds.cpimlibrary.entitymng.CloudQuery;
import it.polimi.modaclouds.cpimlibrary.entitymng.TypedCloudQuery;
import it.polimi.modaclouds.cpimlibrary.entitymng.entities.Phone;
import it.polimi.modaclouds.cpimlibrary.mffactory.MF;
import org.junit.Assert;
import org.junit.Test;

import javax.persistence.Query;
import javax.persistence.TypedQuery;

/**
 * @author Fabio Arcidiacono.
 */
public class QuerySupportTest {

    // CPIM stuff
    protected CloudEntityManagerFactory emf = MF.getFactory().getEntityManagerFactory();
    protected CloudEntityManager em = emf.createCloudEntityManager();

    @Test
    public void testNamedQueries() {
        Query jpaQuery = em.createNamedQuery("allPhones");
        Assert.assertTrue(jpaQuery instanceof CloudQuery);
        String queryString = ((CloudQuery) jpaQuery).getQueryString();
        Assert.assertEquals("SELECT p FROM Phone p", queryString);

        TypedQuery<Phone> jpaTypedQuery = em.createNamedQuery("allPhones", Phone.class);
        Assert.assertTrue(jpaTypedQuery instanceof TypedCloudQuery);
        queryString = ((TypedCloudQuery) jpaTypedQuery).getQueryString();
        Assert.assertEquals("SELECT p FROM Phone p", queryString);
    }

    @Test
    public void testJpqlQueries() {
        Query jpaQuery = em.createQuery("SELECT p FROM Phone p");
        Assert.assertTrue(jpaQuery instanceof CloudQuery);
        String queryString = ((CloudQuery) jpaQuery).getQueryString();
        Assert.assertEquals("SELECT p FROM Phone p", queryString);

        TypedQuery<Phone> jpaTypedQuery = em.createQuery("SELECT p FROM Phone p", Phone.class);
        Assert.assertTrue(jpaTypedQuery instanceof TypedCloudQuery);
        queryString = ((TypedCloudQuery) jpaTypedQuery).getQueryString();
        Assert.assertEquals("SELECT p FROM Phone p", queryString);
    }

    @Test
    public void testUnsupported() {
        try {
            em.createNativeQuery("");
        } catch (UnsupportedOperationException e) {
            // that's fine
        }
        try {
            em.createNativeQuery("", Phone.class);
        } catch (UnsupportedOperationException e) {
            // that's fine
        }
        try {
            em.createNativeQuery("", "");
        } catch (UnsupportedOperationException e) {
            // that's fine
        }
    }
}
