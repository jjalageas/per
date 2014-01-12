package fr.per.bugextraction;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;



public class VerifiedLinksExtractor {

	private static final int BUG_COLUMN = 0;
	private static final int COMMIT_COLUMN = 1;
	private int minBugId;
	private int maxBugId;
	private String projectKey;
	private LinkMap linksMap;
	private int nbLines;
	private ArrayList<String> bugIds;

	public VerifiedLinksExtractor(String key) {
		projectKey = key;
		maxBugId = -1;
		minBugId = Integer.MAX_VALUE;
		nbLines = 0;
		extract();
	}


	private void extract() {
		LinkMap links = new LinkMap();
		bugIds = new ArrayList<String>();
		String fileName = projectKey + "Links";
		java.io.File linksFile = new java.io.File("resources/" + fileName);
		//TODO Fix le chemin relatif du link
		try {
			//InputStream ips=new FileInputStream(linksFile.getAbsolutePath());
			InputStream ips=new FileInputStream("/home/juliannos/per/fr.per.bugextraction/resources/XALANC-Links");
			InputStreamReader ipsr=new InputStreamReader(ips);
			BufferedReader br=new BufferedReader(ipsr);
			String ligne;
			while ((ligne=br.readLine())!=null){
				nbLines++;
				String[] splitLine = ligne.split("\\s");
				String bug = splitLine[BUG_COLUMN];
				String commit = splitLine[COMMIT_COLUMN];
				
				links.put(bug, commit);
				updateMinMaxBugId(splitLine[BUG_COLUMN]);
				bugIds.add(bug);
			}
			br.close(); 
		}catch(Exception e){
			e.printStackTrace();
		}
		linksMap = links;
		Collections.reverse(bugIds);
	}

	public void updateMinMaxBugId(String bugKey) {
		int newId = Integer.parseInt(bugKey.split("-")[1]);
		if (newId > maxBugId)
			maxBugId = newId;
		if (newId < minBugId)
			minBugId = newId;
	}

	public int getMinBugId() {
		return minBugId;
	}

	public int getMaxBugId() {
		return maxBugId;
	}

	public LinkMap getLinksMap() {
		return linksMap;
	}
	
	public int getNbLines(){
		return nbLines;
	}
	
	public ArrayList<String> getBugIds(){
		return bugIds;
	}
	
}
