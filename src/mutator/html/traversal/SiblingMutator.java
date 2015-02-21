package mutator.html.traversal;

import java.io.IOException;

import mutator.Candidates;
import mutator.Mutator;
import mutator.MutatorManager;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.parser.Parser;

public class SiblingMutator {
	String appDir;
	Element targetElement;
	Candidates candidates;

	public SiblingMutator(String appDir, Element targetElement,
			Candidates candidates) throws IOException {
		this.appDir = appDir;
		this.targetElement = targetElement;
		this.candidates = candidates;
	}

	public void replace() throws IOException {
		//Mutator.saveHtml(appDir, targetElement);
		MutatorManager.input =  targetElement.outerHtml();
		MutatorManager.output =  "";
		Mutator.saveMutant(appDir, null, "sibling", "", "");
	}
}
