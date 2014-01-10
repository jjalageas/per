package jpaDatabase;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class MyEntityManager {

	private static final String PERSISTENCE_UNIT_NAME = "Issues";
	private static MyEntityManager instance;

	private static EntityManagerFactory emf;
	private static  EntityManager em;

	private MyEntityManager(){
		System.out.println("ok3");
		
		emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
		em = emf.createEntityManager();
	}

	public static MyEntityManager getMyEntityManager(){
		if (instance == null)
			instance = new MyEntityManager();
		return instance;
	}

	public EntityManager getEntityManager(){
		return em;
	}

	public EntityManagerFactory getEntityManagerFactory(){
		return emf;
	}

	public void close(){
		em.close();
		emf.close();
	}

}
