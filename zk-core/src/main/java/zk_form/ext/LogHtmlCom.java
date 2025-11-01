package zk_form.ext;

import mpu.X;
import mpc.log.Log2HtmlConverter;
import mpu.str.Hu;
import mpc.str.condition.LogGetterDate;
import mpc.ui.ColorTheme;
import org.zkoss.zul.Html;
import zk_page.ZKC;

import java.nio.file.Path;
import java.util.List;

public class LogHtmlCom extends Html {

	public static LogHtmlCom removeMeFirst(LogHtmlCom... defRq) {
		return ZKC.removeMeFirst(LogHtmlCom.class, true, defRq);
	}

	public LogHtmlCom() {
	}

	public LogHtmlCom(String content) {
		super(content);
	}

	public static LogHtmlCom fromLines(LogGetterDate logGetterDate, List<String> lines, Object... tagAttrs) {
		String content = Log2HtmlConverter.fromLines(logGetterDate, lines, tagAttrs);
		LogHtmlCom logHtml = new LogHtmlCom(content);
		logHtml.setWidth("100%");
		return logHtml;
	}

	public static boolean reverse = true;

	public static LogHtmlCom fromFileMaxMb(LogGetterDate logGetterDate, Path file, int maxMb, boolean withHeader, Object... tagAttrs) {

		String html = Log2HtmlConverter.fromFile(file, -Integer.MAX_VALUE, reverse, maxMb, logGetterDate, tagAttrs);
		String head = "";
		if (withHeader) {
			head = "<hr/>";
			head += "<h3 style='text-align:center'>" + file.getFileName() + " / " + Hu.KB_TB(file) + "</h3>";
		}
		LogHtmlCom logHtml = new LogHtmlCom(head + html);
		logHtml.setWidth("100%");
		return logHtml;
	}

	public static String getBgColorAttr() {
		String bgColorAttr = X.f("style=\"background-color:%s\"", ColorTheme.WHITE[0]);
		return bgColorAttr;
	}
}
