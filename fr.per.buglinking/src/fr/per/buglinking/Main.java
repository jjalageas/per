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
			System.out.println("trouvé !");
		else
			System.out.println("pas trouvé !");
	}

}
