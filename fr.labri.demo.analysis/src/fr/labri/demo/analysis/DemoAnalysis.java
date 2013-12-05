package fr.labri.demo.analysis;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import de.lightningbug.api.BugzillaClient;
import de.lightningbug.api.domain.Bug;
import de.lightningbug.api.service.BugService;
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
	public void runOn(Source src) throws MalformedURLException {
		
		ArrayList<String> bugReport = bugzillaReportExtractor("https://issues.apache.org/bugzilla/", "jjalageas@yahoo.com", "pepsi718");
		ArrayList<String> links = new ArrayList<String>();

		//Liste les classes contenues dans le dépôt
		for(Object o: src.getItems().toArray())
			System.out.println(o.toString());
		
		//Affiche le nom du workspace
		System.out.println(src.getWorkspace().toString());
			
		for (Author auth : src.getAuthors()) {
			
			//Liste le nombre de commits par auteurs
			HarmonyLogger.info(auth.getName()+" made "+auth.getEvents().size()+" commits to the projects: "+src.getUrl());
			
			for (int i=0; i<auth.getEvents().size(); i++){
				
				//Affiche le commit ID
				System.out.println("Commit ID");
				System.out.println(auth.getEvents().get(i).getNativeId());
				
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
					String linkDisplay = "Commit " + auth.getEvents().get(i).getNativeId() + " linked to bug " + link;
					links.add(linkDisplay);
					}			
				}

			}
		
		for(String s: links)
			System.out.println(s);
		
		}
	
	
	public String compareLogToReport(String commitLog, ArrayList<String> bugReportIds){
		
		String delims = "[ , #]+";
		String[] logTokens = commitLog.split(delims);

		for(String log: logTokens)
			for(String report: bugReportIds)
				if(log.equals(report))
					return report;
		
		return null;
	}
	
	
	public ArrayList<String> bugzillaReportExtractor(String bugzillaAddress, String username, String password) throws MalformedURLException{
		
		ArrayList<String> bugReport = new ArrayList<String>();
		final BugzillaClient client = new BugzillaClient(new URL(bugzillaAddress), username, password);
		client.login();
		final BugService bugService = new BugService(client);
		final Map<String, Object[]> searchParams = new HashMap<String, Object[]>();
		searchParams.put("summary", new Object[]{"ant"});
		final List<Bug> bugs = bugService.search(searchParams);
		for(Bug b: bugs)
			bugReport.add(b.getId().toString());
		return bugReport;
	}
	

}
	


