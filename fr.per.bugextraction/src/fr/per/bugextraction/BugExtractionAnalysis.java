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

	public static String PROJECT_KEY = "XERCESC-";
	private static VerifiedLinksExtractor linkExtractor;
	public static int START_KEY_NB = 0;
	public static int truePositives = 0;
	public static int falsePositives = 0;
	public static int falseNegatives = 0;
	public static int timeRequirement = 7;
	public static float precision = 0;
	public static float recall = 0;
	public static float f_measure = 0;
	
	public BugExtractionAnalysis() {
		super();		
	}

	public BugExtractionAnalysis(AnalysisConfiguration config, Dao dao, Properties properties) {
		super(config, dao, properties);
	}

	@Override
	public void runOn(Source src) throws Exception {
		//extraction(src);
		linkingAnalysis(src);
	}

	public void extraction(Source src) throws MalformedURLException{
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
			keyNumber++;

			System.out.println(keyNumber);
			System.out.println(linkExtractor.getMax());
			Issue i = null;	
			StringBuffer issueKey = new StringBuffer(PROJECT_KEY);
            issueKey.append(keyNumber);
			try {
				i = jira.getIssue(issueKey.toString());
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
		}
		
		for(IssueEntity ie : issueList)
			dao.saveData(getPersitenceUnitName(), ie, src);
		
		System.out.println(issueList.size() + " Found");
		System.out.println("Extraction Successful");
	}

	@SuppressWarnings("deprecation")
	public void linkingAnalysis(Source src){
		
		int nbCommit = 0;
		int nbLink = 0;
		Map<String, Date> bugReport  = fillBugReport(src);
		linkExtractor = new VerifiedLinksExtractor(PROJECT_KEY);

		ArrayList<String> links = new ArrayList<String>();
		System.out.println("ANALYSIS STARTING:");
		LinkMap foundLinks = new LinkMap();
		LinkMap rejectedLinks = new LinkMap();

		for (Author auth : src.getAuthors()) {
			for (int i=0; i<auth.getEvents().size(); i++){

				String commitLog = auth.getEvents().get(i).getMetadata().get("commit_message");
				Date commitDate = new Date(auth.getEvents().get(i).getTimestamp());
				String commitID = auth.getEvents().get(i).getNativeId();
				Date limitDate = linkExtractor.getDatesMap().get(PROJECT_KEY);
				
				if(commitDate.before(limitDate)){
					nbCommit++;
					ArrayList<String> link = compareLogToBugReport(commitLog, bugReport, PROJECT_KEY, commitDate, commitID, rejectedLinks);

					if(link != null){
						for(String l: link){;
							foundLinks.put(l, commitID);
							String linkDisplay = "Commit " + commitID
									+" linked to bug " + l;
							links.add(linkDisplay);
						}			
					}
				}
			}
		}
		
		checkLinks(foundLinks, links.size(), linkExtractor.getLinksMap());
		nbLink = links.size();

		//for(String s: links)
			//System.out.println(s);
		System.out.println();
		
		System.out.println("Time Requirement: " + timeRequirement);
		System.out.println("Nombre de links du benchmark: " + linkExtractor.getNbLines());
		System.out.println("Nombre de links validés: " + nbLink);
		System.out.println("Nombre de commits: " + nbCommit);
		
		System.out.println();
		System.out.println("Nombre de truePositives: " + truePositives);
		falseNegatives = linkExtractor.getNbLines() - truePositives;
		System.out.println("Nombre de falseNegative: " + falseNegatives);
		System.out.println("Nombre de falsePositive: " + falsePositives);
		CsvWriter.exportLinkResults(PROJECT_KEY, truePositives, falseNegatives, falsePositives, "linkResults.csv");
		
		System.out.println("Nombre de false negative2: " + (linkExtractor.getNbLines() - truePositives));
		float false_negatives = linkExtractor.getNbLines() - truePositives;
		precision = (float)truePositives/((float)truePositives+(float)falsePositives);
		recall = (float)truePositives/((float)truePositives+(float)false_negatives);
		f_measure = ((float)2*(float)precision*(float)recall)/((float)precision+(float)recall);

		
		System.out.println();
		System.out.println("Precision : " + precision);
		System.out.println("Recall2 : " + recall);
		System.out.println("F-Measure : " + f_measure);
		System.out.println();
		CsvWriter.exportStatsResults(PROJECT_KEY, precision, recall, f_measure, "statsResults.csv");
	}

	
	
	
	public HashMap<String, Date> fillBugReport(Source src){
		List<IssueEntity> issueList = null;
		Map<String, Date> br = new HashMap<String, Date>();
		

		issueList = dao.getData(getPersitenceUnitName(), IssueEntity.class, src);

		//System.out.println("NUMBER OF BUGS IN THE DATABASE: " + issueList.size());
		for (IssueEntity i : issueList) {
			br.put(i.getIssue_key(), i.getDate());
		}	

		Set<Entry<String,Date>> set = br.entrySet();
		//for(Entry<String,Date> ent : set)
		//	System.out.println("Issue key: "+ent.getKey());

		return (HashMap<String, Date>) br;
	}

	
	
	
	public ArrayList<String> compareLogToBugReport(String commitLog, Map<String, Date> bugReport, 
			String pk, Date commitDate, String commitID, LinkMap rejectedLinks){
		
		ArrayList<String> linkReport = new ArrayList<String>();
		Pattern pattern = Pattern.compile("(\\w)+" + "-" + "(\\d)+");
		Matcher matcher = pattern.matcher(commitLog);

		while (matcher.find()) {
			String foundID = matcher.group().toUpperCase();
			//System.out.println("found ID : " + foundID);
			if (bugReport.containsKey(foundID)){
				
				//Date resolutionDate = bugReport.get(foundID);
		       // int diffInDays = (int) ((commitDate.getTime() - resolutionDate.getTime()) / (1000 * 60 * 60 * 24));
		        
		      //  if(Math.abs(diffInDays) <= timeRequirement){
		        //	System.out.println();
		        	//System.out.println("Found Bug ID : " + foundID);
		        	linkReport.add(foundID);
		      //  }
		        	//}

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

	
	
	
	public static void checkLinks(LinkMap links, int nbLinks, LinkMap verifiedLinksMap) {
		
		LinkMap verifiedLinks = verifiedLinksMap;
		
		for (Map.Entry<String, Set<String>> entry : links.getLinksMap().entrySet())
		{
			if(verifiedLinks.containsKey(entry.getKey())) {
				for(String commit : entry.getValue())
					if(verifiedLinks.valueContains(entry.getKey(), commit))
						truePositives++;
					else
						System.out.println("Bad Link between bug : " + entry.getKey() + "and commit : " + commit);
			}
			else
				System.out.println("Bad Link between bug : " + entry.getKey() + "and commit : " + entry.getValue());
		}
		
//		for (Map.Entry<String, List<String>> entry : rejectedLinks.getLinksMap().entrySet())
//		{
//			if(verifiedLinks.containsKey(entry.getKey()))
//				for(String commit : entry.getValue())
//					if(verifiedLinks.valueContains(entry.getKey(), commit))
//						falseNegatives++;
//		}
//		
		falsePositives = nbLinks - truePositives;
		
	}
}

