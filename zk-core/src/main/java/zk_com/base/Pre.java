package zk_com.base;

import mpc.html.EHtml5;

import java.io.IOException;
import java.nio.file.Path;

public class Pre extends Xml {
	public Pre(String html) {
		super(html);
	}


	public static Pre buildComponentFromFile(Path file) throws IOException {
		return new Pre(loadDataFrom(file, EHtml5.pre));
	}

	public static Pre ofRsrc(String file) throws IOException {
		return new Pre(loadDataFromRsrc(file, EHtml5.pre));
	}

}
