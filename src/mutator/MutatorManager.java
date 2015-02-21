package mutator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mutator.css.InlineStyleMutator;
import mutator.html.element.ClassMutator;
import mutator.html.element.IdMutator;
import mutator.html.element.NameMutator;
import mutator.html.element.SelectorAllMutator;
import mutator.html.element.TagMutator;
import mutator.html.property.AttributeMutator;
import mutator.html.property.TextMutator;
import mutator.html.traversal.ChildMutator;
import mutator.html.traversal.ParentMutator;
import mutator.html.traversal.SiblingMutator;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import us.codecraft.xsoup.Xsoup;

public class MutatorManager {

	private Document document;
	public static int count = 0, idCount = 0, classCount = 0, tagCount = 0,
			nameCount = 0, parentCount = 0, firstChildCount = 0,
			previousSiblingCount = 0, attributeCount = 0, tagNameCount = 0,
			inlineStyleCount = 0, selectorAllCount = 0;
	public static String domAccessFilePath, app, appDir, input = "", input1 = "", output = "",type = "",
			output1 = "", line="", htmlFilePath="";
	public static String xpath, lineNumber;
	public static HashMap<String, ArrayList<String>> mutants = new HashMap<String, ArrayList<String>>();
	public ArrayList<String> targets = new ArrayList<String>();
	public ArrayList<String> xpaths = new ArrayList<String>();

	public static void main(String[] args) {
		try {
			app = "marctv";
			appDir = "/home/ubuntu/" + app + "/domaccess";
			MutatorManager mm = new MutatorManager(appDir + "/log.txt");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public MutatorManager(String domAccessFilePath)
			throws IOException {
		File maxSizeFile = null;
		for(File html: new File(appDir + "/htmls").listFiles()) {
			if(html.getName().endsWith(".modified")) {
				if(maxSizeFile != null) {
					if(maxSizeFile.length() < html.length()){
						maxSizeFile = html;
					}
				} else {
					maxSizeFile = html;
				}
			}
		}
		this.domAccessFilePath = "/home/ubuntu/" + app + "/domaccess/log.txt";
		this.htmlFilePath = maxSizeFile.getAbsolutePath();
		String path = "/var/www/html/mutator/mutants.js";
		PrintWriter pw;

		// FileUtils.cleanDirectory(new File(appDir + "/dom/html"));

		pw = new PrintWriter(new FileWriter(path));
		pw.println("var mutants = {");
		pw.close();
		mutate();
		pw = new PrintWriter(new FileWriter(path, true));
		pw.println("};");
		pw.close();
	}

	private void mutate() throws IOException {
		BufferedReader br = new BufferedReader(
				new FileReader(domAccessFilePath));
		JsonFactory factory = new JsonFactory();
		factory.enable(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS);
		factory.enable(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES);
		HashMap<String, Integer> lines = new HashMap<String, Integer>();
		String line;
		ArrayList<String> exclusiveLines = new ArrayList<String>();
		int lineCount = 0;

		br = new BufferedReader(new FileReader(domAccessFilePath));
		while ((line = br.readLine()) != null) {
			if (!line.startsWith("{") || exclusiveLines.contains(line)) {
				continue;
			}
			
			exclusiveLines.add(line);
			JsonParser parser = null;
			try {
				parser = factory.createJsonParser(line);
				String html = "", lineNumber = "", xpath = "", value = "", js = "", type = "", name = "";
				while (parser.nextToken() != JsonToken.END_OBJECT) {
					String key = parser.getCurrentName();
					if (key != null) {
						parser.nextToken();
						if (key.equals("sub_type")) {
							type = parser.getText();
							MutatorManager.type = type;
						} else if (key.equals("name")) {
							name = parser.getText();
						} else if (key.equals("value")) {
							value = parser.getText();
						} else if (key.equals("js")) {
							js = parser.getText();
						} else if (key.equals("line")) {
							MutatorManager.lineNumber = parser.getText();
						} else if (key.equals("targetElementXPath")) {
							MutatorManager.xpath = parser.getText().replace("\"", "\\\"");
						} else if (key.equals("targetElementFullHTML")) {
							html = parser.getText();
						}
					}
				}

				if(html.equals("null")) {
					continue;
				}
				// System.out.println(stack);
				Candidates candidates = new Candidates(htmlFilePath);
				
				if (type.equals("absolute_id")) {
					Element element = Jsoup.parse(html, "",
					 		Parser.xmlParser()).child(0);
					new IdMutator(appDir, element).remove();
				} else if (type.equals("absolute_class")) {
					Element element = Jsoup.parse(html, "",
							Parser.xmlParser()).child(0);
					new ClassMutator(appDir, element, candidates)
							.replace(value);
				} else if (type.equals("absolute_name")) {
					Element element = Jsoup.parse(html, "",
							Parser.xmlParser()).child(0);
					new NameMutator(appDir, element, candidates)
							.replace(value);
				} else if (type.equals("absolute_tag")) {
					Element element = Jsoup.parse(html, "",
							Parser.xmlParser()).child(0);
					new TagMutator(appDir, element, candidates)
							.replace(value);
				} else if (type.equals("absolute_selector")) {
					Element element = Jsoup.parse(html, "",
							Parser.xmlParser()).child(0);
					if (!element.tagName().equals("body")
							&& !element.tagName().equals("html")) {
						new SelectorAllMutator(appDir, element, candidates)
								.replace();
					}
				} else if (type.contains("firstChild")) {
					Element element = Jsoup.parse(html, "",
							Parser.xmlParser()).child(0);
					if (!element.tagName().equals("body")
							&& !element.tagName().equals("html")) {
						new ChildMutator(appDir, element, candidates).replace();
					}
				} else if (type.contains("parentNode")) {
					Element element = Jsoup.parse(html, "",
							Parser.xmlParser()).child(0);
					if (!element.tagName().equals("body")
							&& !element.tagName().equals("html")) {
						new ParentMutator(appDir, element, candidates)
								.replace();
					}
				} else if (type.contains("previousSibling")) {
					Element element = Jsoup.parse(html, "",
							Parser.xmlParser()).child(0);
					if (!element.tagName().equals("body")
							&& !element.tagName().equals("html")) {
						new SiblingMutator(appDir, element, candidates)
								.replace();
					}
				} else if (type.contains("attribute")) {
					//if (allAttributes.contains(attributeName)) {
						Element element = Jsoup.parse(html, "",
								Parser.xmlParser()).child(0);
						new AttributeMutator(appDir, element, candidates)
								.replace(name, value);
					//}
				} else if (type.contains("text")) {
					Element element = Jsoup.parse(html, "",
							Parser.xmlParser()).child(0);
					new TextMutator(appDir, element, candidates)
							.replace(value);
				} else if (type.contains("style")) {
					Element element = Jsoup.parse(html, "",
							Parser.xmlParser()).child(0);
					new InlineStyleMutator(appDir, element, candidates)
								.replace(name, value);

				} 
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println(line);
				continue;
			}
		}

		// log
		HashMap<String, String> map = new HashMap<String, String>();
		for (String key : mutants.keySet()) {
			for (String num : mutants.get(key)) {
				map.put(num, key);
			}
		}

		PrintWriter pw = new PrintWriter(new FileWriter(appDir
				+ "/html_css_mutants.csv"));
		for (int i = 0; i < map.keySet().size(); i++) {
			String type = map.get(Integer.valueOf(i).toString());
			pw.println(i + "," + type);
			System.out.println(i + "," + type);
		}
		pw.close();
	}
}
