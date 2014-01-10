package fr.per.bugextraction;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.codehaus.swizzle.jira.Issue;
import org.codehaus.swizzle.jira.Jira;

import fr.labri.harmony.core.analysis.AbstractAnalysis;
import fr.labri.harmony.core.config.model.AnalysisConfiguration;
import fr.labri.harmony.core.dao.Dao;
import fr.labri.harmony.core.model.Author;
import fr.labri.harmony.core.model.Source;

public class BugExtractionAnalysis extends AbstractAnalysis{

	public static String PROJECT_KEY = "AMQ-";   

	public BugExtractionAnalysis() {
		super();		
	}

	public BugExtractionAnalysis(AnalysisConfiguration config, Dao dao, Properties properties) {
		super(config, dao, properties);
	}

	@Override
	public void runOn(Source src) throws Exception {
		extraction(src);
		linkingAnalysis(src);
	}

	public void extraction(Source src){
		Jira jira = null;
		List<IssueEntity> issueList = null;

		try {
			jira = new Jira("https://issues.apache.org/jira/rpc/xmlrpc");

		} catch (MalformedURLException e1) {
			System.err.println("Mauvaise addresse de repository");
			e1.printStackTrace();
		}
		try {
			jira.login("gmeral", "harmony");
		} catch (Exception e) {
			System.err.println("Mauvais login");			
			e.printStackTrace();
		}
		issueList = new ArrayList<IssueEntity>();
		int issuecount = 1109;
		while(issuecount < 1114) {
			Issue i = null;
			issuecount ++;
			StringBuffer issueKey = new StringBuffer(PROJECT_KEY);
			issueKey.append(issuecount);
			try {
				i = jira.getIssue(issueKey.toString());
				if(i != null)
				{
					IssueEntity ie = new IssueEntity(i.getKey(), i.getStatus().toString());
					issueList.add(ie);
				}
			}catch(Exception e) {
				e.printStackTrace();
				continue;
			}
			System.out.println(issuecount);
		}
		for(IssueEntity ie : issueList)
			dao.saveData(getPersitenceUnitName(), ie, src);
		System.out.println("Nombre de bugs trouvés : " + issuecount);
		System.out.println("Extraction Reussie");
	}

	public void linkingAnalysis(Source src){
		int nbCommit = 0;
		int nbLink = 0;
		Map<String, String> bugReport  = fillBugReport(src);

		ArrayList<String> links = new ArrayList<String>();
		System.out.println("DEBUT DE LANALYSE:");

		for (Author auth : src.getAuthors()) {
			for (int i=0; i<auth.getEvents().size(); i++){
				nbCommit++;
				String commitLog = auth.getEvents().get(i).getMetadata().get("commit_message");
				//System.out.println(commitLog);
				//link search
				ArrayList<String> link = compareLogToBugReport(commitLog, bugReport, PROJECT_KEY);
				if(link.size() > 0){
					for(String l: link){
						String linkDisplay = "Commit " + auth.getEvents().get(i).getNativeId() 
								+" linked to bug " + l;
						links.add(linkDisplay);
					}			
				}
			}
		}

		nbLink = links.size();
		System.out.println("Nombre de links : " + nbLink);
		System.out.println("Nombre de commits : " + nbCommit);

		for(String s: links)
			System.out.println(s);
		System.out.println();

	}

	public HashMap<String, String> fillBugReport(Source src){
		List<IssueEntity> issueList = null;
		Map<String, String> br = new HashMap<String, String>();

		issueList = dao.getData(getPersitenceUnitName(), IssueEntity.class, src);

		System.out.println("NOMBRE DE BUGS DANS LA DB: " + issueList.size());
		for (IssueEntity i : issueList) {
			br.put(i.getIssue_key(), i.getStatus());
		}	

		Set<Entry<String,String>> set = br.entrySet();
		for(Entry<String,String> ent : set)
			System.out.println("Issue key: "+ent.getKey());

		return (HashMap<String, String>) br;
	}

	public ArrayList<String> compareLogToBugReport(String commitLog, Map<String, String> bugReport, 
			String pk){

		ArrayList<String> linkReport = new ArrayList<String>();
		Pattern pattern = Pattern.compile(pk + "(\\d)*");
		Matcher matcher = pattern.matcher(commitLog);

		while (matcher.find()) {
			String foundID = matcher.group();
			if (bugReport.containsKey(foundID)){
				System.out.println("Id bug trouve : " + foundID);
				linkReport.add(foundID);
			}
		}
		return linkReport;
	}
}

