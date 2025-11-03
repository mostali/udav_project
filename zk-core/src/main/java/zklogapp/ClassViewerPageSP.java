package zklogapp;

import lombok.Getter;
import lombok.SneakyThrows;
import mpc.exception.WhatIsTypeException;
import mpc.fs.fd.RES;
import mpe.core.ERR;
import mpe.str.URx;
import mpf.ClassGen;
import mpu.X;
import mpu.core.ARG;
import mpu.core.ARR;
import mpu.core.QDate;
import mpu.str.STR;
import mpu.str.TKN;
import org.zkoss.zul.Window;
import zk_com.base.Bt;
import zk_com.base.Cb;
import zk_com.base.Tbxm;
import zk_com.base_ctr.Div0;
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

@PageRoute(sd3 = "view", pagename = "class", role = ROLE.ANONIM)
public class ClassViewerPageSP extends PageSP implements IPerPage, WithLogo, WithSearch {//, WithLogo

	public ClassViewerPageSP(Window window, SpVM spVM) {
		super(window, spVM);
	}

	Charset utf8 = AppZosConfig.getCharset();

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
//		window.appendChild(new LogPageHeader());

//		window.appendChild(new BottomHistoryPanel());

		window.appendChild(inputPan);
		window.appendChild(outPan);
		window.appendChild(Div0.of(new Bt("Do").onCLICK(e -> update(true))).addSTYLE("text-align:center"));
		window.appendChild(Div0.of(new Cb("arg").onCLICK(e -> updateArg((Cb) e.getTarget()))).addSTYLE("text-align:center"));
		window.appendChild(errPan);

//		PrettyCodeXml prettyClass = PrettyCodeXml.of("123");
//		ZKPage.renderHeadRsrc(prettyClass);

		inputPan.onCHANGED(e -> update());

		inputPan.setValue("TestBean bigserial longField VARCHAR stringField");
//

		String sel = "#" + inputPan.getUuid();
		String ondBind = "tEl=document.querySelector('%s');  editor1 = CodeMirror.fromTextArea(tEl, {    lineNumbers: true,    mode: 'text/x-perl',    theme: 'abbott' });var style=tEl.nextSibling.style; style['width']='50%';style['display']='inline-block';style['height']='500px';"
//				"editor1.onblur=function(){ editor1.getTextArea().value=editor1.getValue(); }";
//		"editor1.onBlur=function(){ tEl.value=editor1.getValue(); }";
//				"editor1.onBlur=function(){ el('%s').value=editor1.getValue(); p('updated-blur') }"
				+ "tEl.nextElementSibling.addEventListener('mouseout', function (event) {  updateXmlPan(); } );"
				+ "console.log('%s');"
				+ "function updateXmlPan(){ console.log('Произошло событие updateXmlPan') ; document.querySelector('%s').value = editor1.getValue(); };"
				+ "";

//		xmlPan.setWidgetListener("onBind", X.f_(ondBind, sel, sel,sel)); //initialize client side paste listener

	}

	boolean argMode = false;

	private void updateArg(Cb target) {
		argMode = target.isChecked();
	}

	QDate start = new QDate().now();

	private void update(boolean... force) {
		if (ARG.isDefEqTrue(force) || start.diffabs() > 5000) {
			try {
				String inputVal = inputPan.getValue();

				outPan.detachAll();

				updateImpl();

				errPan.addSTYLE("background-color:lightgreen");

//				UST.XML_STRICT(inputVal);

			} catch (Exception ex) {
				L.error("generate error", ex);
				errPan.setValue(ERR.getMessagesAsStringWithHead(ex, "Illegal HTML", true));
				errPan.addSTYLE("background-color:pink");
			} finally {
				start = QDate.now();
			}
		}
	}

	private @Getter String string;

	public Object string(String... string) {
		this.string = ARG.toDefOr(null, string);
		return this;
	}

	private void updateImpl() {

		CharSequence sb;
		if (argMode) {
			String[] two = TKN.twoGreedy(inputPan.getValue(), " ", ARR.of(inputPan.getValue(), ""));
			String type = two[0];
			String valName = X.notEmpty(two[1]) ? two[1] : (STR.capitalizeNo(TKN.last(type, ".", type)));
			String cat = RES.ofRoot(ClassViewerPageSP.class, "etc/classgen/method.arg.txt").cat();
			String rslt = URx.PlaceholderRegex.SIMPLE.findAndReplaceAll(cat, (k) -> {
				switch (k) {
					case "TYPE":
						return type;
					case "NAME":
						return valName;
					default:
						throw new WhatIsTypeException(k);
				}
			});
			sb = rslt;
		} else {
			ClassGen.GenBeanClass gen = ClassGen.GenBeanClass.of(inputPan.getValue());
			sb = gen.generateClass();
		}


		Tbxm dims = (Tbxm) Tbxm.of("class type filedname", sb.toString()).dims(Tbxm.DIMS.WH100);
//		outPan.setValue(sb.toString());

		PrettyCodeXml prettyClass = PrettyCodeXml.of(sb.toString());
//		ZKM.showModal("Generated", prettyClass);
		outPan.appendChild(prettyClass);

	}


}
