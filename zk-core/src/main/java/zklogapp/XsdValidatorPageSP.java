package zklogapp;

import lombok.SneakyThrows;
import mpe.core.ERR;
import mpu.core.ARG;
import mpu.core.QDate;
import org.zkoss.zul.Window;
import zk_com.base.Bt;
import zk_com.base.Tbxm;
import zk_com.base_ctr.Div0;
import zk_com.sun_editor.IPerPage;
import zk_form.WithLogo;
import zk_form.events.IBoolEvent;
import zk_form.head.StdHeadLib;
import zk_notes.control.NoteLogo;
import zk_os.AppZosConfig;
import zk_os.sec.ROLE;
import zk_page.ZKS;
import zk_page.ZKPage;
import zk_page.core.PageRoute;
import zk_page.core.PageSP;
import zk_page.core.SpVM;
import zk_page.core.WithSearch;
import zklogapp.header.BottomHistoryPanel;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.ByteArrayInputStream;
import java.nio.charset.Charset;

@PageRoute(pagename = "xsdv", role = ROLE.ANONIM)
public class XsdValidatorPageSP extends PageSP implements IPerPage, WithLogo, WithSearch {//, WithLogo

	private AppLogSettingsPanel pageHeader = null;

	public XsdValidatorPageSP(Window window, SpVM spVM) {
		super(window, spVM);
	}

	Charset utf8 = AppZosConfig.getCharset();

	Tbxm xmlPan = (Tbxm) new Tbxm().placeholder("set xml").width(50.0).height(770);
	Tbxm xsdPan = (Tbxm) new Tbxm().placeholder("set xsd").width(50.0).height(770);
	Tbxm outPan = (Tbxm) new Tbxm().width(100.0).height(70);

	@Override
	public LogoCom getLogoDefault() {
		LogoCom first = LogoCom.findFirst(null);
		if (first != null) {
			return first;
		}
		NoteLogo noteLogo = new NoteLogo(null, "/img/xnr16.png");
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

		IBoolEvent.initNewAndAppend(window);
//		window.appendChild(new LogPageHeader());

		window.appendChild(new BottomHistoryPanel());

		window.appendChild(outPan);
		window.appendChild(xmlPan);
		window.appendChild(xsdPan);
		window.appendChild(Div0.of(new Bt("Validate").onCLICK(e -> update(true))).addSTYLE("text-align:center"));

		xmlPan.onCHANGED(e -> update());
		xsdPan.onCHANGED(e -> update());
//
//		xsdPan.addEventListener(Events.ON_BLUR, e -> {
//////			Clients.evalJavaScript(" editor1.getTextArea().value=editor1.getValue(); p('swap>>') ");
////			Clients.evalJavaScript(X.f_(" el('%s').value=editor1.getValue(); p('updated-blur'); p('swap>>') ", "#" + xmlPan.getUuid()));
////			Clients.evalJavaScript(X.f_(" tEl.value=editor1.getValue(); p('updated-blur'); p('swap>>') ", "#" + xmlPan.getUuid()));
//		});

		String sel = "#" + xmlPan.getUuid();
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

	QDate start = new QDate().now();

	private void update(boolean... force) {
		if (ARG.isDefEqTrue(force) || start.diff_WRONG_MINUS() > 5000) {
			try {
				String xmlVal = xmlPan.getValue();
				String xsdVl = xsdPan.getValue();
				validateProfileXMLwithXSD(xmlVal.getBytes(utf8), xsdVl.getBytes(utf8));
				outPan.setValue("ok");
				outPan.addSTYLE("background-color:green");
			} catch (Exception ex) {
				outPan.setValue(ERR.getStackTrace(ex));
				outPan.addSTYLE("background-color:pink");
			} finally {
				start = QDate.now();
			}
		}
	}

	@SneakyThrows
	public static void validateProfileXMLwithXSD(byte[] profileXml, byte[] profileXsd) {
		ByteArrayInputStream profileXsdIS = new ByteArrayInputStream(profileXsd);
		SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		Schema schema = factory.newSchema(new StreamSource(profileXsdIS));
		Validator validator = schema.newValidator();
		validator.validate(new StreamSource(new ByteArrayInputStream(profileXml)));
	}

	//experimental


}
