package zk_notes;

import groovy.lang.GroovyShell;
import mpu.X;

public class GroovyLang {
	public static void main(String[] args) {

		String processingCode = "def hello_world() { return 'Hello, world!' }; hello_world();";
		GroovyShell shell = new GroovyShell();
		Object evaluate = shell.evaluate(processingCode);
		X.p("GRooooovy:" + evaluate);
	}
}
