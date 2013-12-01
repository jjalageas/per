package fr.labri.demo.analysis;

import java.util.ArrayList;
import java.util.Properties;

import fr.labri.harmony.core.analysis.AbstractAnalysis;
import fr.labri.harmony.core.config.model.AnalysisConfiguration;
import fr.labri.harmony.core.dao.Dao;
import fr.labri.harmony.core.log.HarmonyLogger;
import fr.labri.harmony.core.model.Author;
import fr.labri.harmony.core.model.Source;


public class DemoAnalysis extends AbstractAnalysis{
    
	public DemoAnalysis() {
		super();		
	}

	public DemoAnalysis(AnalysisConfiguration config, Dao dao, Properties properties) {
		super(config, dao, properties);
	}

	@Override
	public void runOn(Source src) {
		
		ArrayList<String> bugReport = new ArrayList<String>();
		ArrayList<String> links = new ArrayList<String>();
		bugReport.add("Merged");
		
		//Liste les classes contenues dans le dépôt
		for(Object o: src.getItems().toArray())
			System.out.println(o.toString());
		
		//Affiche le nom du workspace
		System.out.println(src.getWorkspace().toString());
			
		for (Author auth : src.getAuthors()) {
			
			//Liste le nombre de commits par auteurs
			HarmonyLogger.info(auth.getName()+" made "+auth.getEvents().size()+" commits to the projects: "+src.getUrl());
			
			for (int i=0; i<auth.getEvents().size(); i++){
				//Affiche la date et l'heure du commit
				System.out.println("Commit Timestamp");
				System.out.println(auth.getEvents().get(i).getTimestampAsString());
				
				//Affiche le message du commit
				System.out.println("Commit Log");
				String commitLog = auth.getEvents().get(i).getMetadata().get("commit_message");
				System.out.println(commitLog);
				
				//Recherche d'un lien avec un bug
				String link = compareLogToReport(commitLog, bugReport);
				if(link != null){
					String linkDisplay = "Commit " + auth.getEvents().get(i).getId() + " linked to bug " + link;
					links.add(linkDisplay);
					}			
				}
			
			for(String s: links)
				System.out.println(s);

			}
		}
	
	public String compareLogToReport(String commitLog, ArrayList<String> bugReportIds){
		
		String delims = "\\s+";
		String[] logTokens = commitLog.split(delims);

		for(String log: logTokens)
			for(String report: bugReportIds)
				if(log.equals(report))
					return report;
		
		return null;		
	}
	

}
	


