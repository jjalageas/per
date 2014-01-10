package moduleBdd;

import moduleBdd.persistence.Issue;


public interface DatabaseApi {

	void setNewIssue(String key, String status);
	
	void setIssueKey(int issue_id, String issue_key);
	
	void setIssueStatus(int issue_id, String status);
	
	Issue getIssue(int issue_id);
	
	Issue getIssue(String issue_key);

	String getIssuekey(int issue_id);
	
	String getIssueStatus(int issue_id);
	
	int getIssueId(String issue_key);
	
	boolean isIssue(String issue_key);
	
	boolean isIssue(Issue issue);
	
	public void deleteIssue(Issue issue);

}
