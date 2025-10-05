package zklogapp.docker;

import lombok.SneakyThrows;
import org.zkoss.zul.Window;
import zk_com.base.Tbx;
import zk_com.base.Tbxm;
import zk_com.base_ctr.Span0;
import zk_com.sun_editor.IPerPage;
import zk_form.WithLogo;
import zk_form.events.IBoolEvent;
import zk_form.head.StdHeadLib;
import zk_notes.control.NoteLogo;
import zk_os.AppZosConfig;
import zk_os.sec.ROLE;
import zk_page.ZKPage;
import zk_page.ZKS;
import zk_page.core.PageRoute;
import zk_page.core.PageSP;
import zk_page.core.SpVM;
import zk_page.with_com.WithSearch;
import zk_notes.coms.PrettyCodeXml;

import java.nio.charset.Charset;

@PageRoute(pagename = "@@docker", role = ROLE.OWNER)
public class DockerPageSP extends PageSP implements IPerPage, WithLogo, WithSearch {//, WithLogo

	public DockerPageSP(Window window, SpVM spVM) {
		super(window, spVM);
	}

	Charset utf8 = AppZosConfig.getCharset();

	Tbx inputUserPan = (Tbx) new Tbx().placeholder("set user").block();
	Tbxm inputPan = (Tbxm) new Tbxm().placeholder("set html").width(50.0).height(770);
	Span0 outPan = (Span0) new Span0().contentEditable().width(50.0).height(770).borderSilver().inlineBlock();
	//	Tbxm xsdPan = (Tbxm) new Tbxm().placeholder("set xsd").width(50.0).height(770);
	Tbxm errPan = (Tbxm) new Tbxm().width(100.0).height(70);

	@Override
	public LogoCom getLogoDefault() {
		LogoCom first = LogoCom.findFirst(null);
		if (first != null) {
			return first;
		}
		NoteLogo noteLogo = new NoteLogo();
		return noteLogo;
	}

	@SneakyThrows
	public void buildPageImpl() {

		ZKS.PADDING0(window);
		ZKS.MARGIN(window, "30px 0 0 0");

		window.appendChild(getLogoOrAdd());

		ZKPage.renderHeadRsrcs(window, StdHeadLib.JS_CODEMIRROR_6_65_7);
		ZKPage.renderHeadRsrcs(window, StdHeadLib.JS_CODEMIRROR_6_65_7_PERL);
		ZKPage.renderHeadRsrcs(window, StdHeadLib.CSS_CODEMIRROR_6_65_7);
		ZKPage.renderHeadRsrcs(window, StdHeadLib.CSS_CODEMIRROR_6_65_7_THEME_ABBOTT);

		ZKPage.renderHeadRsrcs(window, StdHeadLib.PRETTYFY_JS);

		IBoolEvent.initNewAndAppend(window);

		window.appendChild(PrettyCodeXml.BR());
		window.appendChild(PrettyCodeXml.BR());


		window.appendChild(new DockerImages());
		window.appendChild(new DockerCtrs());


	}

}
