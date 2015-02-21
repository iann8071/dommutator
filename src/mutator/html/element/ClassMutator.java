package mutator.html.element;

import java.io.IOException;
import java.util.ArrayList;

import mutator.Candidates;
import mutator.Mutator;
import mutator.MutatorManager;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ClassMutator {

	String appDir;
	Element targetElement;
	Candidates candidates;

	public ClassMutator(String appDir, Element targetElement,
			Candidates candidates) throws IOException {
		this.appDir = appDir;
		this.targetElement = targetElement;
		this.candidates = candidates;
	}

	public void replace(String value) throws IOException {
		//Mutator.saveHtml(appDir, targetElement);
		String attr = targetElement.attr("class");
		MutatorManager.input = targetElement.outerHtml();
		attr = attr.replace(value, candidates.getAttributeCandidate("class", value));
		targetElement.attr("class", attr);
		MutatorManager.output = targetElement.outerHtml();
		Mutator.saveMutant(appDir, targetElement, "class", "", "");
	}
}
