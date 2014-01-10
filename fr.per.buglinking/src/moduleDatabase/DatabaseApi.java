package moduleDatabase;

import java.util.ArrayList;


public interface DatabaseApi {

	void setNewIssue(String key, String status);
	
	void setIssueKey(int issue_id, String key);
	
	void setIssueStatus(int issue_id, String status);
	
	boolean isIssue(String issue_id);
	
	ArrayList<String> getIssue(int issue_id);

	String getIssuekey(int issue_id);
	
	String getIssueStatus(int issue_id);

}
