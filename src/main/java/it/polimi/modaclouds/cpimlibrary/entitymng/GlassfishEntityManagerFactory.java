package it.polimi.modaclouds.cpimlibrary.entitymng;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class GlassfishEntityManagerFactory extends CloudEntityManagerFactory {

	private EntityManagerFactory factory = null;
	private String persistenceUnit = null;

	public GlassfishEntityManagerFactory(String persistenceUnit) {
		this.persistenceUnit= persistenceUnit;
		factory = Persistence.createEntityManagerFactory(persistenceUnit);
	}

	@Override
	public void close() {
		if(persistenceUnit !=null){
		super.removeMF(persistenceUnit);
		factory.close();
		}
	}

	@Override
	public CloudEntityManager createCloudEntityManager() {
		return new GlassfishEntityManager(factory.createEntityManager());

	}

}
