package zk_com.base;

import mpc.html.EHtml5;

import java.io.IOException;
import java.nio.file.Path;

public class Js extends Xml {
	public Js(String html) {
		super(html);
	}


	public static Js buildComponentFromFile(Path file) throws IOException {
		return new Js(loadDataFrom(file, EHtml5.script));
	}

	public static Js ofRsrc(String file) throws IOException {
		return new Js(loadDataFromRsrc(file, EHtml5.script));
	}

}
