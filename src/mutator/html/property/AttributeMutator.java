package mutator.html.property;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import mutator.Candidates;
import mutator.Mutator;
import mutator.MutatorManager;

import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

public class AttributeMutator {

	String appDir;
	Element targetElement;
	Candidates candidates;

	public AttributeMutator(String appDir, Element targetElement,
			Candidates candidates) throws IOException {
		this.appDir = appDir;
		this.targetElement = targetElement;
		this.candidates = candidates;
	}

	public void replace(String name, String value) throws IOException {
		
		//Mutator.saveHtml(appDir, targetElement);
		String attr = targetElement.attr(name);
		MutatorManager.input = targetElement.outerHtml();
		attr = candidates.getAttributeCandidate(name, value);
		targetElement.attr(name, attr);
		MutatorManager.output = targetElement.outerHtml();
		Mutator.saveMutant(appDir, targetElement, "attribute", name, "");
	}

	public void replace(String name, String value, Element element) throws IOException {
		
		//Mutator.saveHtml(appDir, targetElement);
		String attr = element.attr(name);
		//Element abstractElement = getAbstractElement(targetElement.clone());
		//abstractElement.attr(name, value);
		//MutatorManager.input = targetElement.outerHtml().replaceAll("\\d", "");
		MutatorManager.input = targetElement.outerHtml();
		String newAttr = attr.replace(value, candidates.getAttributeCandidate(name, value));
		element.attr(name, newAttr);
		MutatorManager.output = targetElement.outerHtml();
		//MutatorManager.output1 = abstractElement.outerHtml();
		Mutator.saveMutant(appDir, targetElement, "attribute", name, "");
		element.attr(name, attr);
	}
}
