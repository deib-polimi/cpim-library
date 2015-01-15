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

import it.polimi.modaclouds.cpimlibrary.entitymng.PersistenceMetadata;
import it.polimi.modaclouds.cpimlibrary.entitymng.entities.Department;
import it.polimi.modaclouds.cpimlibrary.entitymng.entities.EmployeeMTO;
import it.polimi.modaclouds.cpimlibrary.entitymng.entities.EmployeeOTO;
import it.polimi.modaclouds.cpimlibrary.entitymng.entities.Phone;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Fabio Arcidiacono.
 */
public class MetadataTest {

    @Test
    public void testPersistedClass() {
        Assert.assertEquals(Department.class.getCanonicalName(), PersistenceMetadata.getInstance().getMappedClass("Department"));

        Assert.assertEquals(EmployeeMTO.class.getCanonicalName(), PersistenceMetadata.getInstance().getMappedClass("EmployeeMTO"));

        Assert.assertEquals(EmployeeOTO.class.getCanonicalName(), PersistenceMetadata.getInstance().getMappedClass("EmployeeOTO"));
        Assert.assertEquals(EmployeeOTO.class.getCanonicalName(), PersistenceMetadata.getInstance().getMappedClass("EmployeeOTOne"));

        Assert.assertEquals(Phone.class.getCanonicalName(), PersistenceMetadata.getInstance().getMappedClass("Phone"));
    }

    @Test
    public void testNamedQueries() {
        // verify named query map is correctly filled
        String query = PersistenceMetadata.getInstance().getNamedQuery("allPhones");
        Assert.assertEquals("SELECT p FROM Phone p", query);

        query = PersistenceMetadata.getInstance().getNamedQuery("allEmployees");
        Assert.assertEquals("SELECT e FROM EmployeeOTOne e", query);
        System.out.println(query);

        query = PersistenceMetadata.getInstance().getNamedQuery("updateSalary");
        Assert.assertEquals("UPDATE EmployeeOTOne e SET e.salary = :s WHERE e.name = :n", query);
    }
}
