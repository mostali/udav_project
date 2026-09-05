package zk_com.base;

import mpc.html.EHtml5;

import java.io.IOException;
import java.nio.file.Path;

public class Css extends Xml {
	public Css(String html) {
		super(html);
	}


	public static Css buildComponentFromFile(Path file) throws IOException {
		return new Css(loadDataFrom(file, EHtml5.style));
	}

	public static Css ofJsRsrc(String file) throws IOException {
		return new Css(loadDataFromRsrc(file, EHtml5.style));
	}

}
