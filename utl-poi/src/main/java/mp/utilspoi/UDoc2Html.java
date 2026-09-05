package mp.utilspoi;

import mpe.core.P;
import mpu.Sys;
import mpu.core.RW;
import org.zwobble.mammoth.DocumentConverter;
import org.zwobble.mammoth.Result;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Set;

//https://github.com/mwilliamson/java-mammoth
public class UDoc2Html {

	public static void main(String[] args) throws IOException {
		DocumentConverter converter = new DocumentConverter();
		Result<String> result = converter.convertToHtml(new File("........_25-05-2022.docx"));
		String html = result.getValue(); // The generated HTML
		Set<String> warnings = result.getWarnings(); // Any warnings during conversion
		P.p(warnings);
		RW.write_(Paths.get("/tmp/form.html"), html);
	}

	//https://github.com/mwilliamson/java-mammoth
	public static String buildHtml(File file) throws IOException {
		DocumentConverter converter = new DocumentConverter();
		Result<String> result = converter.convertToHtml(file);
		String html = result.getValue(); // The generated HTML
//		Set<String> warnings = result.getWarnings(); // Any warnings during conversion
		return html;
	}


}
