package fr.per.buglinking;

import moduleDatabase.DatabaseApiSql;


public class Main {
	public static void main(String[] args){
		DatabaseApiSql db = new DatabaseApiSql();
		db.setNewIssue("AMQ-3832", "5");
		db.setNewIssue("AMQ-3805", "5");
		db.setNewIssue("AMQ-3801", "5");
		db.setNewIssue("AMQ-3684", "5");
		db.setNewIssue("AMQ-2985", "5");
		if(db.isIssue("AMQ-2985"))
			System.out.println("good");
		if(db.isIssue("AMQ-56362765371"))
			System.out.println("wrong");
		if(db.isIssue("AMQ-fezfjnzkrg"))
			System.out.println("wrong");
		if(db.isIssue("AMQ-3832"))
			System.out.println("good");
		if(db.isIssue("AMQ-3805"))
			System.out.println("good");
		if(db.isIssue("AMQ-4985"))
			System.out.println("wrong");
		if(db.isIssue("AMQ-1985"))
			System.out.println("wrong");
		if(db.isIssue("AMQ-3684"))
			System.out.println("good");
		
	}

}
