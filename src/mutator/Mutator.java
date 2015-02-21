package mutator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import us.codecraft.xsoup.Xsoup;

public class Mutator {

	static String inputHTML;
	static String mutant; 
	static String type;
	static ArrayList<String> mutants = new ArrayList<String>();

	public Mutator(String filePath, String baseElementXPath,
			String targetElementXpath, ArrayList<String> args)
			throws IOException {
	}

	public static void saveMutant(String attributeName, String styleName)
			throws IOException {
		File file;
		/*
		 * if (styleName != "") { String path = "/var/www/html/mutator/" +
		 * MutatorManager.domain + "/style/" + styleName + "/" +
		 * MutatorManager.count; file = new File(path); } else if (attributeName
		 * != "") { String path = "/var/www/html/mutator/" +
		 * MutatorManager.domain + "/attribute/" + attributeName + "/" +
		 * MutatorManager.count; file = new File(path); } else { String path =
		 * "/var/www/html/mutator/" + MutatorManager.domain + "/" + type + "/" +
		 * MutatorManager.count; file = new File(path); } PrintWriter pw = new
		 * PrintWriter(new FileWriter(file)); pw.print(json); pw.close();
		 */
		
		String json = "{\"input\":\"" + MutatorManager.input.replaceAll("\n", "").replace("\"", "\\\"").replaceAll("\\s+<", "<").replaceAll(">\\s+", ">")
				+ "\",\"output\":\"" + MutatorManager.output.replaceAll("\n", "").replace("\"", "\\\"").replaceAll("\\s+<", "<").replaceAll(">\\s+", ">")
				+ "\",\"xpath\":\"" + MutatorManager.xpath
				+ "\",\"line\":\"" + MutatorManager.lineNumber
				+ "\",\"type\":\"" + MutatorManager.type
				+ "\"}";
		
		/*
		String json1 = "{\"input\":\"" + MutatorManager.input1.replaceAll("\n", "").replace("\"", "\\\"").replaceAll("\\s+<", "<").replaceAll(">\\s+", "")
				//+ "\",\"mutant\":\"" + MutatorManager.output1.replaceAll("\n", "").replace("\"", "\\\"").replaceAll("\\s+<", "<").replaceAll(">\\s+", "")
				+ "\",\"attributeName\":\"" + attributeName.replace("\"", "\\\"")
				+ "\",\"styleName\":\"" + styleName.replace("\"", "\\\"")
				+ "\",\"type\":\"" + type.replace("\"", "\\\"")
				+ "\"}";
		*/
		
		if(mutants.contains(json)) {
			return;
		} else {
			mutants.add(json);
			ArrayList<String> typeMutants = MutatorManager.mutants.get(MutatorManager.type);
			if (typeMutants == null) {
				typeMutants = new ArrayList<String>();
			}
			typeMutants.add(Integer.valueOf(MutatorManager.count).toString());
			MutatorManager.mutants.put(MutatorManager.type, typeMutants);
			MutatorManager.count++;
		}
		System.out.println(MutatorManager.count);
		PrintWriter pw = new PrintWriter(new FileWriter(
				"/var/www/html/mutator/mutants.js", true));
		pw.println("\"" + MutatorManager.count + "\":" + json + ",");
		pw.close();
	}

	public static void saveHtml(String appDir, Element element)
			throws IOException {
		String htmlFilePath = appDir + "/dom/html/html" + MutatorManager.count;
		BufferedWriter bw = new BufferedWriter(new FileWriter(htmlFilePath));
		String html = element.outerHtml();

		Pattern p = Pattern.compile("\\d");
		Matcher m = p.matcher(html);
		html = m.replaceAll("");
		inputHTML = html;

		bw.write(html);
		bw.close();
	}

	/*
	 * public static void saveHtml(String appDir, String text) throws
	 * IOException{ String htmlFilePath = appDir + "/dom/html/html" +
	 * MutatorManager.count; BufferedWriter bw = new BufferedWriter(new
	 * FileWriter(htmlFilePath)); bw.write(text); bw.close(); }
	 */

	public static void saveMutant(String appDir, Element element, String type,
			String attributeName, String styleName) throws IOException {
		saveMutant(attributeName, styleName);
	}

	public static void saveMutant(String appDir, String text, String type)
			throws IOException {
		String htmlFilePath = appDir + "/dom/html/html" + MutatorManager.count;
		String mutantFilePath = appDir + "/dom/html_css_mutants/mutant"
				+ MutatorManager.count;
		String diffFilePath = appDir + "/dom/html_css_diffs/mutant"
				+ MutatorManager.count + ".diff";

		BufferedWriter bw = new BufferedWriter(new FileWriter(mutantFilePath));
		bw.write(text);
		bw.close();

		ProcessBuilder pb = new ProcessBuilder("diff", "-w", htmlFilePath,
				mutantFilePath);
		File diffFile = new File(diffFilePath);
		pb.redirectOutput(diffFile);
		try {
			pb.start().waitFor();
			if (diffFile.length() == 0) {
				new File(htmlFilePath).delete();
				new File(mutantFilePath).delete();
				new File(diffFilePath).delete();
				return;
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		ArrayList<String> typeMutants = MutatorManager.mutants.get(type);
		if (typeMutants == null) {
			typeMutants = new ArrayList<String>();
		}
		typeMutants.add(Integer.valueOf(MutatorManager.count).toString());
		MutatorManager.mutants.put(type, typeMutants);

		MutatorManager.count++;
	}

	public static Element selectElementByLDXPath(Document document,
			Element targetElement, Elements candidateElements) {
		String sourceElementXPath = null;
		int minLD = Integer.MAX_VALUE;
		String targetElementXPath = getXPath(targetElement);
		for (Element candidateElement : candidateElements) {
			String candidateElementXPath = getXPath(candidateElement);

			if (getLD(candidateElementXPath, targetElementXPath) <= 0) {
				continue;
			}

			if (sourceElementXPath == null) {
				sourceElementXPath = candidateElementXPath;
				minLD = getLD(candidateElementXPath, targetElementXPath);
			} else if (getLD(candidateElementXPath, targetElementXPath) < minLD) {
				sourceElementXPath = candidateElementXPath;
				minLD = getLD(candidateElementXPath, targetElementXPath);
			}
		}

		return Xsoup.compile(sourceElementXPath).evaluate(document)
				.getElements().get(0);
	}

	public static String getXPath(Element element) {
		String xpath = "";
		ArrayList<String> paths = new ArrayList<String>();
		for (; element.parent() != null; element = element.parent()) {
			int index = 0;
			for (Node sibling = element.previousSibling(); sibling != null; sibling = sibling
					.previousSibling()) {
				try {
					Element elementSibling = (Element) sibling;
					if (elementSibling.tagName().equals(element.tagName())) {
						++index;
					}
				} catch (ClassCastException e) {

				}
			}

			String pathIndex = index > 1 ? "[" + (index + 1) + "]" : "";
			paths.add(element.tagName() + pathIndex);
		}

		for (int i = paths.size() - 1; i >= 0; i--) {
			xpath += "/" + paths.get(i);
		}
		return xpath;
	}

	public static int getLD(String s0, String s1) {
		return new LevenshteinDistance(s0, s1).get();
	}
}
