package zk_notes;

import groovy.lang.GroovyShell;
import groovy.lang.MetaClass;
import mpu.X;

import java.util.Map;

public class GroovyLang {
	public static String test() {
		return "1223";
	}

	public static void main(String[] args) {

		String processingCode = "def hello_world() { return \"Hello, ${fff} world!\" }; hello_world();";
		GroovyShell shell = new GroovyShell();

		shell.setProperty("fff", 123);

//		shell.setVariable("fff", "wtf");

		Map variables = shell.getContext().getVariables();
		Object evaluate = shell.evaluate(processingCode);
		X.p("GRooooovy:" + evaluate);
		X.p("GRooooovy2:" + variables);
	}
}
