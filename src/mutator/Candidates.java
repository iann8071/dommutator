package mutator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Candidates {

	String candidateSourceHtmlFilePath;
	Document document;
	ArrayList<String> allAttributes = new ArrayList<String>();
	HashMap<String, ArrayList<String>> allAttributeCandidates = new HashMap<String, ArrayList<String>>();
	HashMap<String, ArrayList<String>> allStyleCandidates = new HashMap<String, ArrayList<String>>();
	ArrayList<String> allTextCandidates = new ArrayList<String>();
	ArrayList<String> allSelectorCandidates = new ArrayList<String>();
	ArrayList<String> metaContent = new ArrayList<String>();
	{
		metaContent.add("base");
		metaContent.add("link");
		metaContent.add("meta");
		metaContent.add("noscript");
		metaContent.add("script");
		metaContent.add("style");
		metaContent.add("templatw");
		metaContent.add("title");
	}
	ArrayList<String> phrasingContent = new ArrayList<String>();
	{
		phrasingContent.add("a");
		phrasingContent.add("abbr");
		phrasingContent.add("area");
		phrasingContent.add("audio");
		phrasingContent.add("b");
		phrasingContent.add("bdi");
		phrasingContent.add("bdo");
		phrasingContent.add("br");
		phrasingContent.add("button");
		phrasingContent.add("canvas");
		phrasingContent.add("cite");
		phrasingContent.add("code");
		phrasingContent.add("data");
		phrasingContent.add("datalist");
		phrasingContent.add("del");
		phrasingContent.add("dfn");
		phrasingContent.add("em");
		phrasingContent.add("embed");
		phrasingContent.add("i");
		phrasingContent.add("iframe");
		phrasingContent.add("img");
		phrasingContent.add("input");
		phrasingContent.add("ins");
		phrasingContent.add("kbd");
		phrasingContent.add("keygen");
		phrasingContent.add("label");
		phrasingContent.add("link");
		phrasingContent.add("map");
		phrasingContent.add("mark");
		phrasingContent.add("math");
		phrasingContent.add("meta");
		phrasingContent.add("meter");
		phrasingContent.add("noscript");
		phrasingContent.add("object");
		phrasingContent.add("output");
		phrasingContent.add("progress");
		phrasingContent.add("q");
		phrasingContent.add("ruby");
		phrasingContent.add("s");
		phrasingContent.add("samp");
		phrasingContent.add("script");
		phrasingContent.add("select");
		phrasingContent.add("small");
		phrasingContent.add("span");
		phrasingContent.add("strong");
		phrasingContent.add("sub");
		phrasingContent.add("sup");
		phrasingContent.add("svg");
		phrasingContent.add("template");
		phrasingContent.add("textarea");
		phrasingContent.add("time");
		phrasingContent.add("u");
		phrasingContent.add("var");
		phrasingContent.add("video");
		phrasingContent.add("wbr");
		phrasingContent.add("text");
	}
	ArrayList<String> embeddedContent = new ArrayList<String>();
	{
		embeddedContent.add("audio");
		embeddedContent.add("canvas");
		embeddedContent.add("embed");
		embeddedContent.add("iframe");
		embeddedContent.add("img");
		embeddedContent.add("math");
		embeddedContent.add("object");
		embeddedContent.add("svg");
		embeddedContent.add("video");
	}
	ArrayList<String> interactiveContent = new ArrayList<String>();
	{
		interactiveContent.add("a");
		interactiveContent.add("audio");
		interactiveContent.add("button");
		interactiveContent.add("detail");
		interactiveContent.add("s");
		interactiveContent.add("embed");
		interactiveContent.add("iframe");
		interactiveContent.add("img");
		interactiveContent.add("input");
		interactiveContent.add("keygen");
		interactiveContent.add("label");
		interactiveContent.add("object");
		interactiveContent.add("select");
		interactiveContent.add("textarea");
		interactiveContent.add("th");
		interactiveContent.add("video");
	}
	ArrayList<String> headingContent = new ArrayList<String>();
	{
		headingContent.add("h1");
		headingContent.add("h2");
		headingContent.add("h3");
		headingContent.add("h4");
		headingContent.add("h5");
		headingContent.add("h6");
		headingContent.add("hgroup");
	}
	ArrayList<String> sectioningContent = new ArrayList<String>();
	{
		sectioningContent.add("article");
		sectioningContent.add("aside");
		sectioningContent.add("nav");
		sectioningContent.add("section");
	}
	ArrayList<String> flowContent = new ArrayList<String>();
	{
		flowContent.add("address");
		flowContent.add("blockquote");
		flowContent.add("dialog");
		flowContent.add("div");
		flowContent.add("dl");
		flowContent.add("fieldset");
		flowContent.add("figure");
		flowContent.add("footer");
		flowContent.add("form");
		flowContent.add("header");
		flowContent.add("hr");
		flowContent.add("main");
		flowContent.add("menu");
		flowContent.add("ol");
		flowContent.add("output");
		flowContent.add("p");
		flowContent.add("pre");
		flowContent.add("section");
		flowContent.add("style");
		flowContent.add("table");
		flowContent.add("ul");
	}

	public Candidates(String candidateSourceHtmlFilePath) throws IOException {
		this.candidateSourceHtmlFilePath = candidateSourceHtmlFilePath;
		document = Jsoup.parse(new File(candidateSourceHtmlFilePath), "UTF-8");
		setCandidates();
	}

	private void setCandidates() {
		setAllAttributes();
		setAttributeCandidates();
		setStyleCandidates();
		setTextCandidates();
		setSelectorCandidates();
	}

	private void setAllAttributes() {
		Elements allElements = document.select("*");
		for (Element element : allElements) {
			Attributes attributes = element.attributes();
			Iterator<Attribute> iterator = attributes.iterator();
			while (iterator.hasNext()) {
				Attribute attribute = iterator.next();
				String attributeName = attribute.getKey();
				if (!allAttributes.contains(attributeName)) {
					allAttributes.add(attributeName);
				}
			}
		}
	}

	private void setAttributeCandidates() {
		for (String attributeName : allAttributes) {
			Elements elements = document.select("*[" + attributeName + "]");
			ArrayList<String> attributeCandidates = allAttributeCandidates
					.get(attributeName);
			if (attributeCandidates == null) {
				attributeCandidates = new ArrayList<String>();
			}

			if (attributeName.equals("class")) {
				for (Element element : elements) {
					String[] values = element.attr("class").split(" ");
					for (String value : values) {
						if (!attributeCandidates.contains(value)) {
							attributeCandidates.add(value);
						}
					}

					String value = element.attr("class");
					if (!attributeCandidates.contains(value)) {
						attributeCandidates.add(value);
					}
				}
			} else {
				for (Element element : elements) {
					String value = element.attr(attributeName);
					if (!attributeCandidates.contains(value)) {
						attributeCandidates.add(value);
					}
				}
			}
			allAttributeCandidates.put(attributeName, attributeCandidates);
		}
	}

	private void setStyleCandidates() {
		Elements elements = document.select("*");
		for (Element element : elements) {
			if(element.attr("style").equals("")) {
				continue;
			}
			String[] styles = element.attr("style").split(";");

			for (String style : styles) {
				if (!style.equals("")) {
					String styleName = style.split(":")[0];
					String styleValue = style.split(":")[1];
					ArrayList<String> styleCandidates = allStyleCandidates
							.get(styleName);
					if (styleCandidates == null) {
						styleCandidates = new ArrayList<String>();
					}

					if (!styleCandidates.contains(styleValue)) {
						styleCandidates.add(styleValue);
					}

					allStyleCandidates.put(styleName, styleCandidates);
				}
			}
		}
	}

	private void setTextCandidates() {
		Elements elements = document.select("*");
		for (Element element : elements) {
			String text = element.ownText();
			if (!text.trim().equals("") && !allTextCandidates.contains(text)) {
				allTextCandidates.add(text);
			}
		}
	}

	private void setSelectorCandidates() {
		Elements elements = document.select("*");
		for (Element element : elements) {
			Element clone = element.clone();
			clone.empty();
			String html = clone.outerHtml();
			if (!allSelectorCandidates.contains(html)) {
				allSelectorCandidates.add(html);
			}
		}
	}

	public String getCandidateValue(ArrayList<String> candidates, String value) {
		String result = "";
		int minLD = Integer.MAX_VALUE;
		if (candidates != null) {
			for (String candidate : candidates) {
				if (getLD(candidate, value) <= 0) {
					continue;
				}

				if (getLD(candidate, value) < minLD) {
					result = candidate;
					minLD = getLD(candidate, value);
				}
			}
		}

		if (!result.contains("<")) {
			if (result.equals(value)) {
				if (result.equals("")) {
					result = "test";
				} else {
					result = "test";
				}
			}
		}
		return result;
	}

	private int getLD(String s0, String s1) {
		return new LevenshteinDistance(s0, s1).get();
	}

	public String getAttributeCandidate(String name, String value) {
		ArrayList<String> attributeCandidates = allAttributeCandidates
				.get(name);
		return getCandidateValue(attributeCandidates, value);
	}

	public String getStyleCandidate(String name, String value) {
		ArrayList<String> styleCandidates = allStyleCandidates.get(name);
		String result  = getCandidateValue(styleCandidates, value);
		if(result.equals("")) {
			return "inherit";
		} 
		return getCandidateValue(styleCandidates, value);
	}

	public String getTextCandidate(String value) {
		return getCandidateValue(allTextCandidates, value);
	}

	public String getTagCandidate(String name) {
		String result = "div";
		if (metaContent.contains(name)) {
			while (true) {
				int rand = (int) (Math.random() * metaContent.size());
				result = metaContent.get(rand);
				if (!result.equals(name)) {
					break;
				}
			}
		} else if (embeddedContent.contains(name)) {
			while (true) {
				int rand = (int) (Math.random() * embeddedContent.size());
				result = embeddedContent.get(rand);
				if (!result.equals(name)) {
					break;
				}
			}
		} else if (headingContent.contains(name)) {
			while (true) {
				int rand = (int) (Math.random() * headingContent.size());
				result = headingContent.get(rand);
				if (!result.equals(name)) {
					break;
				}
			}
		} else if (sectioningContent.contains(name)) {
			while (true) {
				int rand = (int) (Math.random() * sectioningContent.size());
				result = sectioningContent.get(rand);
				if (!result.equals(name)) {
					break;
				}
			}
		} else if (interactiveContent.contains(name)) {
			while (true) {
				int rand = (int) (Math.random() * interactiveContent.size());
				result = interactiveContent.get(rand);
				if (!result.equals(name)) {
					break;
				}
			}
		} else if (phrasingContent.contains(name)) {
			while (true) {
				int rand = (int) (Math.random() * phrasingContent.size());
				result = phrasingContent.get(rand);
				if (!result.equals(name)) {
					break;
				}
			}
		} else if (flowContent.contains(name)) {
			while (true) {
				int rand = (int) (Math.random() * flowContent.size());
				result = flowContent.get(rand);
				if (!result.equals(name)) {
					break;
				}
			}
		}
		return result;
	}

	public String getSelectorCandidate(String html) {
		return getCandidateValue(allSelectorCandidates, html);
	}
}