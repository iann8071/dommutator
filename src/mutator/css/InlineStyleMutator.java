package mutator.css;

import java.io.IOException;
import java.util.Arrays;

import mutator.Candidates;
import mutator.Mutator;
import mutator.MutatorManager;

import org.jsoup.nodes.Element;

public class InlineStyleMutator {
	String appDir;
	Element targetElement;
	Candidates candidates;

	public InlineStyleMutator(String appDir, Element targetElement,
			Candidates candidates) throws IOException {
		this.appDir = appDir;
		this.targetElement = targetElement;
		this.candidates = candidates;
	}

	public void replace(String name, String value) throws IOException {
		//Mutator.saveHtml(appDir, targetElement);
		String attr = targetElement.attr("style");
		MutatorManager.input = targetElement.outerHtml();
		String[] styles = attr.split(";");
		for(int i = 0; i < styles.length; i++) {
			if(styles[i].contains(name)) {
				styles[i] = styles[i].replace(value, candidates.getStyleCandidate(name, value));
			}
		}
		
		attr = "";
		for(int i = 0; i < styles.length; i++) {
			if(styles[i].contains("inherit")) {
				continue;
			}
			attr += (styles[i] + ";");
		}
		targetElement.attr("style", attr);
		MutatorManager.output = targetElement.outerHtml();
		Mutator.saveMutant(appDir, targetElement, "style", name, "");
	}
}
