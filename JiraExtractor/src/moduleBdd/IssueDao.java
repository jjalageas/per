package moduleBdd;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;

import moduleBdd.persistence.Issue;

public class IssueDao implements DatabaseApi{

	private static EntityManager em;


	public IssueDao(){
		em = MyEntityManager.getMyEntityManager().getEntityManager();
	}

	private void persist(Issue issue){
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		try
		{
			em.persist(issue);
			tx.commit();
		}
		catch ( Exception e )
		{
			System.out.println( "Erreur :" + e.getMessage() );
			tx.rollback();
		}
	}

	public void setNewIssue(String key, String status) {
		Issue issue = new Issue();
		issue.setIssue_key(key);
		issue.setStatus(status);
		persist(issue);	
	}

	public void setIssueKey(int issue_id, String key) {
		Issue foundIssue = getIssue(issue_id);
		foundIssue.setIssue_key(key);
		persist(foundIssue);
	}

	public void setIssueStatus(int issue_id, String status) {
		Issue foundIssue = getIssue(issue_id);
		foundIssue.setStatus(status);
		persist(foundIssue);

	}

	public Issue getIssue(int issue_id) {
		Issue foundIssue = em.find(Issue.class, issue_id);
		return foundIssue;
	}

	public Issue getIssue(String key){
		String sql = "SELECT i FROM Issue i WHERE i.issue_key = :k";
		Query query = em.createQuery(sql);
		query.setParameter("k", key);
		return (Issue) query.getSingleResult();

	}

	public String getIssuekey(int issue_id) {
		String key = getIssue(issue_id).getIssue_key();
		return key;
	}

	public String getIssueStatus(int issue_id) {
		String status = getIssue(issue_id).getStatus();
		return status;
	}



	public int getIssueId(String key) {
		Issue foundIssue = getIssue(key);
		return foundIssue.getId();

	}

	public boolean isIssue(String key) {
		Issue foundIssue = getIssue(key);
		return (foundIssue != null);
	}

	public boolean isIssue(Issue issue) {
		Issue foundIssue = getIssue(issue.getId());
		return (foundIssue != null);
	}

	public void deleteIssue(Issue issue){
		EntityTransaction tx = em.getTransaction();
		try{
			tx.begin();
			em.remove(issue);
			tx.commit();
		}catch(Exception re)
		{
			System.out.println( "Erreur :" + re.getMessage() );
			tx.rollback();
		}
	}
}
