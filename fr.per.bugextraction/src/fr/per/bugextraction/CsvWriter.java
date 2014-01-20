package fr.per.bugextraction;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class CsvWriter {
	public static void exportLinkResults(String project, int truePositives,int falseNegatives, int falsePositives, String outPath) {
		try {
			File f = new File("out/projectStats/" + outPath);
			boolean fileExists = f.exists();
			FileWriter fw = new FileWriter(f.getAbsolutePath(), true);
			System.out.println(f.getAbsolutePath());
			if (!fileExists)
				fw.write("Project TruePositives FalseNegatives FalsePositives\n");
			fw.write(project + " " + truePositives + " " + falseNegatives + " " + falsePositives + "\n");
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Ecriture du fichier terminée");
	}
	
	public static void exportStatsResults(String project, float precision, float recall, float fMeasure, String outPath) {
		try {
			
			File f = new File("out/projectStats/" + outPath);
			boolean fileExists = f.exists();
			FileWriter fw = new FileWriter(f.getAbsolutePath(), true);
			System.out.println(f.getAbsolutePath());
			if (!fileExists)
				fw.write("Project Precision Recall F-Measure\n");
			fw.write(project + " " + precision + " " + recall + " " + fMeasure + "\n");
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Ecriture du fichier terminée");
	}
}
