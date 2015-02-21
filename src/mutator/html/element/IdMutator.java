package mutator.html.element;

import java.io.IOException;
import java.util.ArrayList;

import mutator.Mutator;
import mutator.MutatorManager;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import us.codecraft.xsoup.Xsoup;

public class IdMutator {

	String appDir;
	Element targetElement;

	public IdMutator(String appDir, Element targetElement) throws IOException {
		this.appDir = appDir;
		this.targetElement = targetElement;
	}

	public void remove() throws IOException {
		//Mutator.saveHtml(appDir, targetElement);
		MutatorManager.input = targetElement.outerHtml();
		targetElement.removeAttr("id");
		MutatorManager.output = targetElement.outerHtml();
		Mutator.saveMutant(appDir, targetElement, "id", "", "");
	}
}
