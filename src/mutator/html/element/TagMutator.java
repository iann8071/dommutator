package mutator.html.element;

import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import us.codecraft.xsoup.Xsoup;
import mutator.Candidates;
import mutator.Mutator;
import mutator.MutatorManager;

public class TagMutator {

	String appDir;
	Element targetElement;
	Candidates candidates;

	public TagMutator(String appDir, Element targetElement,
			Candidates candidates) throws IOException {
		this.appDir = appDir;
		this.targetElement = targetElement;
		this.candidates = candidates;
	}

	public void replace(String tag) throws IOException {
		MutatorManager.input = targetElement.outerHtml();
		if(tag.equals("*")) {
			MutatorManager.output = "";
		} else { 
			targetElement.tagName(candidates.getTagCandidate(tag));
			MutatorManager.output = targetElement.outerHtml();
		}
		Mutator.saveMutant(appDir, targetElement, "tag", "", "");
	}
}
