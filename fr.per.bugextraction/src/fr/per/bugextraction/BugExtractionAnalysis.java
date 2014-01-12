package fr.per.bugextraction;

import java.io.IOException;
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

	public static String PROJECT_KEY = "XALANC-";
	private static VerifiedLinksExtractor linkExtractor;
	public static int truePositives = 0;
	public static int falsePositives = 0;
	public static int falseNegatives = 0;
	
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
		linkExtractor = new VerifiedLinksExtractor(PROJECT_KEY);

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
		//TODO remplacer le test par les min et max de VerifiedLinksExtractor
		ArrayList<String> bugIds= linkExtractor.getBugIds();
		for(String id: bugIds) {	
			Issue i = null;	
			try {
				i = jira.getIssue(id);
				if(i != null)
				{
					IssueEntity ie = new IssueEntity(i.getKey(), i.getStatus().toString());
					issueList.add(ie);
				}
			}catch(Exception e) {
				e.printStackTrace();
				continue;
			}
			System.out.println(id);

		}
		for(IssueEntity ie : issueList)
			dao.saveData(getPersitenceUnitName(), ie, src);
		System.out.println("Nombre de bugs trouvés : " + bugIds.size());
		System.out.println("Extraction Reussie");
	}

	public void linkingAnalysis(Source src){
		int nbCommit = 0;
		int nbLink = 0;
		Map<String, String> bugReport  = fillBugReport(src);

		ArrayList<String> links = new ArrayList<String>();
		System.out.println("DEBUT DE LANALYSE:");
		LinkMap foundLinks = new LinkMap();
		for (Author auth : src.getAuthors()) {
			for (int i=0; i<auth.getEvents().size(); i++){
				nbCommit++;
				String commitLog = auth.getEvents().get(i).getMetadata().get("commit_message");
				//System.out.println(commitLog);
				//link search
				ArrayList<String> link = compareLogToBugReport(commitLog, bugReport, PROJECT_KEY);
				if(link.size() > 0){
					for(String l: link){
						String commit = auth.getEvents().get(i).getNativeId();
						foundLinks.put(l, commit);
						String linkDisplay = "Commit " + commit
								+" linked to bug " + l;
						links.add(linkDisplay);
					}			
				}
			}
		}
		checkLinks(foundLinks, links.size(), linkExtractor.getLinksMap());
		nbLink = links.size();
		
		for(String s: links)
			System.out.println(s);
		System.out.println();
		
		
		System.out.println("Nombre de links : " + nbLink);
		System.out.println("Nombre de commits : " + nbCommit);
		System.out.println("Nombre de truePositives : " + truePositives);
		System.out.println("Nombre de falseNegative : " + falseNegatives);
		System.out.println("Nombre de falsePositive : " + falsePositives);

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

	public static void dumpDatabase(String db) {
		//TODO fix le path relatif du dump
		Runtime r = Runtime.getRuntime();
		Process p;
		String fileName = PROJECT_KEY + "Dump.sql";
		java.io.File linksFile = new java.io.File("resources/" + fileName);
		//ProcessBuilder pb = new ProcessBuilder("mysqldump --opt -h localhost -u root -p pchanson > cleanActiveMQ.sql");
		try {
			p = r.exec("mysqldump --opt -h localhost -u root --password=pepsi718" + db + " > " + linksFile.getAbsolutePath());
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Dump Terminé : fichier dump accessible à : \n" + linksFile.getAbsolutePath());
	}

	public static void checkLinks(LinkMap links, int nbLinks, LinkMap verifiedLinksMap) {
		LinkMap verifiedLinks = verifiedLinksMap;
		System.out.println(linkExtractor.getNbLines());
		for (Map.Entry<String, List<String>> entry : links.getLinksMap().entrySet())
		{
			String bugKey = entry.getKey();
			if(verifiedLinks.containsKey(entry.getKey())) {
				for(String commit : entry.getValue())
					if(verifiedLinks.valueContains(entry.getKey(), commit))
						truePositives++;
					else
						falsePositives++;
				List<String> verifiedCommits = (List<String>) verifiedLinks.get(bugKey);
				int nbVerifiedCommits = verifiedCommits.size();
				int nbCommits = entry.getValue().size();
				falseNegatives += nbVerifiedCommits > nbCommits ? nbVerifiedCommits - nbCommits : 0;
			}
			else
				falsePositives++;
		}



	}
}

