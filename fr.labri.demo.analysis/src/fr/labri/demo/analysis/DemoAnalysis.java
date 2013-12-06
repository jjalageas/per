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
		
		for (Author auth : src.getAuthors()) {
			
			for (int i=0; i<auth.getEvents().size(); i++){
				
				String commitLog = auth.getEvents().get(i).getMetadata().get("commit_message");
				
				//link search
				ArrayList<String> link = compareLogToReport(commitLog, bugReport);
				if(link.size() > 0){
					for(String l: link){
						String linkDisplay = "Commit " + auth.getEvents().get(i).getNativeId() + " linked to bug " + l;
						links.add(linkDisplay);
					}			
				}

			}
		}
		
		for(String s: links)
			System.out.println(s);
		System.out.println();
		
	}
	
	
	public ArrayList<String> compareLogToReport(String commitLog, ArrayList<String> bugReportIds){
		
		String delims = "[ , #]+";	
		String[] logTokens = commitLog.split(delims);
		ArrayList<String> linkReport = new ArrayList<String>();
		
		for(String log: logTokens)
			for(String report: bugReportIds)
				if(log.equals(report))
					linkReport.add(report);
	
		return linkReport;
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
	


