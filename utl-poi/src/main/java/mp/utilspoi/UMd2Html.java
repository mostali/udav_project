package mp.utilspoi;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.MutableDataSet;
import mpe.core.P;
import mpu.core.RW;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

//https://gist.github.com/Jekins/2bf2d0638163f1294637
public class UMd2Html {

	public static void main(String[] args) throws IOException {
		String md2html = buildHtml(Paths.get("Readme.md"));
		P.exit(md2html);
	}

	public static String buildHtml(Path data_md) throws IOException {
		return buildHtml(RW.readContent_(data_md));
	}

	public static String buildHtml(String data_md) {
		MutableDataSet options = new MutableDataSet();
		Parser parser = Parser.builder(options).build();
		HtmlRenderer renderer = HtmlRenderer.builder(options).build();
		Node document = parser.parse(data_md);
		String html = renderer.render(document);
		return html;
	}
}
