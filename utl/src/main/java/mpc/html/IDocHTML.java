package mpc.html;

import mpu.X;
import mpu.core.ARG;
import mpu.str.Sb;

public interface IDocHTML {

	Sb sb();

	default IDocHTML addH1(String str, Object... args) {
//			sb().NL(SEP.DOG.__str3__(str, false, args));
		String html = EHtml5.h1.with(str, args);
		sb().NL(html);
		return this;
	}

	default IDocHTML addH2(String str, Object... args) {
//			sb().NL(SEP.DOG.__str2__(str, false, args));
		String html = EHtml5.h2.with(str, args);
		sb().NL(html);
		return this;
	}

	default IDocHTML addH3(String str, Object... args) {
//			sb().NL(SEP.DOG.__str1__(str, false, args));
		String html = EHtml5.h3.with(str, args);
		sb().NL(html);
		return this;
	}

	default IDocHTML addH4(String str, Object... args) {
		String html = EHtml5.h4.with(str, args);
		sb().NL(html);
		return this;
	}

	default IDocHTML addH5(String str, Object... args) {
		String html = EHtml5.h5.with(str, args);
		sb().NL(html);
		return this;
	}

	default IDocHTML addH6(String str, Object... args) {
		String html = EHtml5.h6.with(str, args);
		sb().NL(html);
		return this;
	}

	default IDocHTML addLink(String label, String link, String linkName, boolean openInNewWindow) {
		addLink(label, link, linkName, false, openInNewWindow);
		return this;
	}

	default IDocHTML addLinkLine(String label, String link, String linkName, boolean openInNewWindow) {
		addLink(label, link, linkName, true, openInNewWindow);
		return this;
	}

	default IDocHTML addLink(String label, String link, String linkName, boolean newLine, boolean openInNewWindow) {
//			sb().append(label);
//			sb().append(" >>> ");
//			sb().NLF(link, args);

		String targetAttr = ARG.isDef(openInNewWindow) ? "target='_blank'" : "";
		String html = label + EHtml5.a.withTag(linkName, X.f("href='%s'", link), targetAttr);
		if (newLine) {
			html = EHtml5.div.with(html);
		}
		sb().NL(html);
		return this;
	}

	default IDocHTML addLabelBold(String str, Object... args) {
		String div = EHtml5.b.with(str, args);
		sb().append(div);
		return this;
	}

	default IDocHTML addLabelCursive(String str, Object... args) {
		String div = EHtml5.i.with(str, args);
		sb().append(div);
		return this;
	}

	default IDocHTML addLabel(String str, Object... args) {
		sb().append(X.f(str, args));
		return this;
	}

	default IDocHTML addLabelClass(String str, String clazz) {
		sb().append(EHtml5.span.withClass(str, clazz));
		return this;
	}

	default IDocHTML addLineBold(String str, Object... args) {
//			sb().NLF(str, args);
		String div = EHtml5.div.with(EHtml5.b.with(str, args));
		sb().NL(div);
		return this;
	}

	default IDocHTML addLineCursive(String str, Object... args) {
		String div = EHtml5.div.with(EHtml5.i.with(str, args));
		sb().NL(div);
		return this;
	}

	default IDocHTML addLine(String str, Object... args) {
//			sb().NLF(str, args);
		String div = EHtml5.div.with(str, args);
		sb().NL(div);
		return this;
	}

	default IDocHTML addStyle(String style) {
		sb().NL(EHtml5Head.style.wrap(style));
		return this;
	}

	default IDocHTML addNewLineBr() {
		sb().NL(EHtml5.br.with());
		return this;
	}

	default IDocHTML addSection(CharSequence report) {
		sb().NL(EHtml5.section.with(report));
		return this;
	}

	default IDocHTML addSection(CharSequence report, int fontSize) {
		sb().NL(EHtml5.section.withStyle(report, "font-size", fontSize + "px"));
		return this;
	}

	default IDocHTML addHR() {
		sb().NL(EHtml5.hr.with());
		return this;
	}


	;
}
