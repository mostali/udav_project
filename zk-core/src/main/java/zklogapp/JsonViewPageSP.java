package zklogapp;

import com.jayway.jsonpath.JsonPath;
import lombok.SneakyThrows;
import mpc.json.UGson;
import mpc.str.sym.SYMJ;
import mpe.core.ERR;
import mpu.IT;
import mpu.X;
import mpu.str.Sb;
import org.zkoss.zul.Window;
import zk_com.base.Bt;
import zk_com.base.Tbx;
import zk_com.base.Tbxm;
import zk_com.base.Xml;
import zk_com.base_ctr.Div0Next;
import zk_com.sun_editor.IPerPage;
import zk_form.WithLogo;
import zk_form.events.IBoolEvent;
import zk_form.head.StdHeadLib;
import zk_notes.coms.PrettyCodeXml;
import zk_notes.control.NoteLogo;
import zk_os.sec.ROLE;
import zk_page.*;
import zk_page.core.PageRoute;
import zk_page.core.PageSP;
import zk_page.core.SpVM;
import zk_page.with_com.WithSearch;
import zk_page.panels.BottomHistoryPanel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@PageRoute(sd3 = "view", pagename = "json", role = ROLE.ANONIM)
public class JsonViewPageSP extends PageSP implements IPerPage, WithLogo, WithSearch {//, WithLogo

	public JsonViewPageSP(Window window, SpVM spVM) {
		super(window, spVM);
	}

//	Charset utf8 = AppZosConfig.getCharset();

	Tbx tbxJsonPath = (Tbx) new Tbx().placeholder("json path $.key").width(40.0).height(30);

	Tbxm tbxmJson = (Tbxm) new Tbxm().placeholder("set json data").bgcolor(ZKColor.WHITE.nextColor()).inlineBlock().width(49.0).height(770);
	//	Tbxm xsdPan = (Tbxm) new Tbxm().placeholder("set xsd").width(50.0).height(770);
	Div0Next outNext = (Div0Next) new Div0Next().accumulateSize(10).revert().inlineBlock().width(40.0);

	Tbxm outErrPan = (Tbxm) new Tbxm().width(100.0).height(0);

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


		ZKPage.renderHeadRsrcs(window, PrettyCodeXml.HEAD_RSCS);

		IBoolEvent.initNewAndAppend(window);
//		window.appendChild(new LogPageHeader());

		window.appendChild(new BottomHistoryPanel());

		//

		Bt showPretty = (Bt) new Bt("Do Pretty Json", "gray");
		Bt showDocs = (Bt) new Bt(SYMJ.LINK + " Show Git Documentation", "gray").onCLICK(e -> {
			ZKR.openWindow800_1200("https://github.com/json-path/JsonPath");

		}).padding(3);
		Bt showNative = (Bt) new Bt(SYMJ.LINK + " Show Online Editor", "gray").onCLICK(e -> {
//			ZKM.showModal("Docs", Xml.ofFrame("https://github.com/json-path/JsonPath"));
			ZKR.openWindow800_1200("http://jsonpath.com/");
		}).padding(3);
		;
		window.appendChild(showPretty);
		window.appendChild(showDocs);
		window.appendChild(showNative);
		window.appendChild(Xml.BR());
		window.appendChild(Xml.BR());

		//

		Bt btApplyJsonPath = new Bt("Apply | Json Path").onCLICK(e -> update(true));
		window.appendChild(btApplyJsonPath);
		window.appendChild(tbxJsonPath);

		window.appendChild(outErrPan);

		tbxJsonPath.onOK(e -> update(true));
		window.appendChild(Xml.BR());

		//

		window.appendChild(tbxmJson);
		window.appendChild(outNext);

		//


		showPretty.onCLICK(e -> tbxmJson.setValue(UGson.toStringPretty(tbxmJson.getValue())));

	}

	private void update(boolean... force) {
		int headRslt = 3;
		int headObj = 4;
		try {

			String jsonVal = tbxmJson.getValue();
			String jsonPath = tbxJsonPath.getValue();

			IT.isJsonOrArray(jsonVal);

			class Rslt {

				List<String> list = new ArrayList<>();

				public void addJsonMap(Map read) {
					String stringJson = UGson.toStringJson((Map) read);
					stringJson = UGson.toStringPretty(stringJson);
					list.add(stringJson);
				}

				public void addJsonList(List objList) {
					for (int i = 0; i < objList.size(); i++) {

						Object o = objList.get(i);

						if (o instanceof Map) {
							list.add("Map#" + i);
							addJsonMap((Map) o);
							continue;
						}

						list.add("#" + i);
						String data = o + "";
						list.add(data);
					}
				}

				public void addJsonStr(String code) {
					list.add(UGson.toStringPretty(code));
				}

				public void addJsonArrayStr(String code) {
					list.add(UGson.toStringPrettyArrayWrap(SYMJ.FIX + "-PRETTY-ARRAY", code));
				}
			}

			Rslt rslt = new Rslt();

			Object read = X.empty(jsonPath) ? jsonVal : JsonPath.read(jsonVal, jsonPath);

			outNext().appendChildNext(Xml.H(headRslt, X.f("JsonPath | %s", jsonPath)));

			if (read instanceof Map) {
				outNext().appendChildNext(Xml.H(headObj, X.f("Map*%s", X.sizeOf((Map) read))));
				rslt.addJsonMap((Map) read);
			} else if (read instanceof List) {
				outNext().appendChildNext(Xml.H(headObj, X.f("List Size: %s", X.sizeOf((List) read))));
				rslt.addJsonList((List) read);
			} else {
				outNext().appendChildNext(Xml.H(headObj, X.f("Object")));
				String code = read.toString();
				if (UGson.isGson(code)) {
					rslt.addJsonStr(code);
				} else if (UGson.isGsonArray(code)) {
					rslt.addJsonArrayStr(code);
				} else if (UGson.isGsonLinent(code)) {
					code = UGson.toStringPrettyLinent(code);
					rslt.addJsonStr(code);
				}
			}

			for (String rslt1 : rslt.list) {
				outNext().appendChildNext(PrettyCodeXml.of(rslt1));
			}

			{
				Sb hist = new Sb();
				for (int i = 0; i < rslt.list.size(); i++) {
					hist.NL("=====" + i + "=====", rslt.list.get(i));
				}

				BottomHistoryPanel.addItemAsData(
						"\n----------JSONPATH---------\n" +
								jsonPath + "\n----------OUT---------\n" +
								hist + "\n----------ORIGINAL---------\n" + jsonVal, false);
			}


			outErrPan.setValue("Ok");
			outErrPan.addSTYLE("background-color:" + ZKColor.GREEN.nextColor());
			outErrPan.height(20);
			outErrPan.setVisible(false);

		} catch (Exception ex) {
			L.info("unhandled error json", ex);
			outErrPan.setVisible(true);
			outErrPan.setValue(ERR.getMessagesAsStringWithHead(ex, "Errors", true));
			outErrPan.addSTYLE("background-color:pink");
			outErrPan.height(100);
			outNext().appendChildNext(Xml.H(headRslt, X.f("JsonPath | ERR | %s", ex.getMessage())));

		} finally {
//				start = QDate.now();
			outNext().replaceNextDiv();

		}
//		}
	}

	private Div0Next outNext() {
		return outNext;
	}


}
