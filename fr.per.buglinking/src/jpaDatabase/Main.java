package jpaDatabase;

import jpaDatabase.persistence.Issue;


public class Main
{
	public static void main(String[] args)
	{

		DatabaseApi db = new IssueDao();
		db.setNewIssue("AMQ-3832", "5");
		db.setNewIssue("AMQ-3805", "5");
		db.setNewIssue("AMQ-3801", "5");
		db.setNewIssue("AMQ-3684", "5");
		db.setNewIssue("AMQ-2985", "5");
//		Issue issue = db.getIssue(1);
//		Issue issue2 = db.getIssue("AMQ-222");
//		db.deleteIssue(issue2);
//		DatabaseApi db2 = new IssueDao();
//		db2.setNewIssue("AMQ-611", "5");
//		db2.setNewIssue("AMQ-612", "5");
//		if (db.isIssue("AMQ-611"))
//			db2.setNewIssue("AMQ-999", "5");
//		if (!(db.isIssue(issue2))){
//			db.setIssueStatus(1, "1");
//			db.setIssueKey(1, "AMQ-998");
//		}
//		db2.setNewIssue("AMQ-888", "5");

		
		

	
	}
}