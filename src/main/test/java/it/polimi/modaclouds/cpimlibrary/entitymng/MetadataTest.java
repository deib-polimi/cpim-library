package it.polimi.modaclouds.cpimlibrary.entitymng;

import it.polimi.modaclouds.cpimlibrary.entitymng.entities.EmployeeOTO;
import it.polimi.modaclouds.cpimlibrary.entitymng.entities.Phone;
import org.junit.Assert;
import org.junit.Test;

import javax.persistence.Query;

/**
 * @author Fabio Arcidiacono.
 */
public class MetadataTest {

    @Test
    public void testPersistedClass() {
        String employeeOTO = PersistenceMetadata.getInstance().getMappedClass("EmployeeOTO");
        String employeeOTOne = PersistenceMetadata.getInstance().getMappedClass("EmployeeOTOne");

        Assert.assertEquals(EmployeeOTO.class.getCanonicalName(), employeeOTO);
        Assert.assertEquals(EmployeeOTO.class.getCanonicalName(), employeeOTOne);

        String phone = PersistenceMetadata.getInstance().getMappedClass("Phone");
        Assert.assertEquals(phone, Phone.class.getCanonicalName());
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

        // verify entity manager wrapping is working
        CloudEntityManagerFactory emf = new CloudEntityManagerFactory("pu");
        Query jpaQuery = emf.createCloudEntityManager().createNamedQuery("allPhones");
        Assert.assertTrue(jpaQuery instanceof CloudQuery);
        String queryString = ((CloudQuery) jpaQuery).getQueryString();
        Assert.assertEquals("SELECT p FROM Phone p", queryString);
    }
}
