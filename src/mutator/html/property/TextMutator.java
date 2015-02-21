package mutator.html.property;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import mutator.Candidates;
import mutator.Mutator;
import mutator.MutatorManager;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

public class TextMutator {

	String appDir;
	Element targetElement;
	Candidates candidates;

	public TextMutator(String appDir, Element targetElement,
			Candidates candidates) throws IOException {
		this.appDir = appDir;
		this.targetElement = targetElement;
		this.candidates = candidates;
	}

	public void replace(String value) throws IOException {
		//Mutator.saveHtml(appDir, targetElement);
		MutatorManager.input =  targetElement.outerHtml();
		targetElement.text(candidates.getTextCandidate(value));
		MutatorManager.output =  targetElement.outerHtml();
		Mutator.saveMutant(appDir, targetElement, "text", "", "");

	}
}
