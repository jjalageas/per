package jpaDatabase;

import jpaDatabase.persistence.Issue;


public interface DatabaseApi {

	void setNewIssue(String key, String status);
	
	void setIssueKey(int issue_id, String key);
	
	void setIssueStatus(int issue_id, String status);
	
	Issue getIssue(int issue_id);
	
	Issue getIssue(String key);

	String getIssuekey(int issue_id);
	
	String getIssueStatus(int issue_id);
	
	int getIssueId(String key);
	
	boolean isIssue(String key);
	
	boolean isIssue(Issue issue);
	
	public void deleteIssue(Issue issue);

}
