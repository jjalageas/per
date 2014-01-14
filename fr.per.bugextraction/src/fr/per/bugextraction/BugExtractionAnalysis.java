package fr.per.bugextraction;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Date;
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

	public static String PROJECT_KEY = "OPENNLP-";
	private static VerifiedLinksExtractor linkExtractor;
	public static int START_KEY_NB = 1;
	public static int truePositives = 0;
	public static int falsePositives = 0;
	public static int falseNegatives = 0;
	public static int trueNegatives = 0;
	
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
			System.err.println("Invalid repository address");
			e1.printStackTrace();
		}
		try {
			jira.login("gmeral", "harmony");
		} catch (Exception e) {
			System.err.println("Invalid login");			
			e.printStackTrace();
		}
		issueList = new ArrayList<IssueEntity>();
		int keyNumber = START_KEY_NB;

		while(keyNumber < linkExtractor.getMax()){
			Issue i = null;	
			try {
				i = jira.getIssue(PROJECT_KEY + keyNumber);
				String status = i.getStatus().toString();

				
				if(i != null && (status.equals("Resolved") || status.equals("Closed") || status.equals("Reopened")))
				{
					IssueEntity ie = new IssueEntity(i.getKey(), i.getStatus().toString(), i.getUpdated());
					issueList.add(ie);
	
				}
			}catch(Exception e) {
				e.printStackTrace();
				continue;
			}
			System.out.println(keyNumber);
			keyNumber++;
		}
		
		for(IssueEntity ie : issueList)
			dao.saveData(getPersitenceUnitName(), ie, src);
		
		System.out.println(issueList.size() + "Found");
		System.out.println("Extraction Successful");
	}

	public void linkingAnalysis(Source src){
		
		int nbCommit = 0;
		int nbLink = 0;
		Map<String, Date> bugReport  = fillBugReport(src);

		ArrayList<String> links = new ArrayList<String>();
		System.out.println("ANALYSIS STARTING:");
		LinkMap foundLinks = new LinkMap();
		
		for (Author auth : src.getAuthors()) {
			for (int i=0; i<auth.getEvents().size(); i++){
				
				nbCommit++;
				String commitLog = auth.getEvents().get(i).getMetadata().get("commit_message");
				Date commitDate = new Date(auth.getEvents().get(i).getTimestamp());
				ArrayList<String> link = compareLogToBugReport(commitLog, bugReport, PROJECT_KEY, commitDate);
				
				if(link != null){
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
		System.out.println("Nombre de trueNegative : " + trueNegatives);

	}

	
	
	
	public HashMap<String, Date> fillBugReport(Source src){
		List<IssueEntity> issueList = null;
		Map<String, Date> br = new HashMap<String, Date>();
		

		issueList = dao.getData(getPersitenceUnitName(), IssueEntity.class, src);

		System.out.println("NUMBER OF BUGS IN THE DATABASE: " + issueList.size());
		for (IssueEntity i : issueList) {
			if(i.getDate()!=null)
				System.out.println(i.getDate());
			br.put(i.getIssue_key(), i.getDate());
		}	

		Set<Entry<String,Date>> set = br.entrySet();
		for(Entry<String,Date> ent : set)
			System.out.println("Issue key: "+ent.getKey());

		return (HashMap<String, Date>) br;
	}

	
	
	
	public ArrayList<String> compareLogToBugReport(String commitLog, Map<String, Date> bugReport, 
			String pk, Date commitDate){
		
		ArrayList<String> linkReport = new ArrayList<String>();
		Pattern pattern = Pattern.compile(pk + "(\\d)*");
		Matcher matcher = pattern.matcher(commitLog);

		while (matcher.find()) {
			String foundID = matcher.group();
			if (bugReport.containsKey(foundID)){
				
				Date resolutionDate = bugReport.get(foundID);
		        int diffInDays = (int) ((commitDate.getTime() - resolutionDate.getTime()) / (1000 * 60 * 60 * 24));
		        System.out.println(diffInDays);
		        
		        if(Math.abs(diffInDays) <= 7){
		        	System.out.println();
		        	System.out.println("Found Bug ID : " + foundID);
		        	linkReport.add(foundID);
		        }
		        else
		        	trueNegatives++;
			}			
		}
		return linkReport;
	}

	
	
	
	@SuppressWarnings("unused")
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

	
	
	
	@SuppressWarnings("unchecked")
	public static void checkLinks(LinkMap links, int nbLinks, LinkMap verifiedLinksMap) {
		
		LinkMap verifiedLinks = verifiedLinksMap;
		
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
		trueNegatives -= falseNegatives;


	}
}

