package mutator.html.element;

import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import us.codecraft.xsoup.Xsoup;
import mutator.Candidates;
import mutator.LevenshteinDistance;
import mutator.Mutator;
import mutator.MutatorManager;

public class NameMutator {

	String appDir;
	Element targetElement;
	Candidates candidates;

	public NameMutator(String appDir, Element targetElement,
			Candidates candidates) throws IOException {
		this.appDir = appDir;
		this.targetElement = targetElement;
		this.candidates = candidates;
	}

	public void replace(String value) throws IOException {
		//Mutator.saveHtml(appDir, targetElement);
		String attr = targetElement.attr("name");
		MutatorManager.input =  targetElement.outerHtml();
		attr = attr.replace(value, candidates.getAttributeCandidate("name", value));
		targetElement.attr("name", attr);
		MutatorManager.output =  targetElement.outerHtml();
		Mutator.saveMutant(appDir, targetElement, "name", "", "");
	}
}
