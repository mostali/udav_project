package zk_notes.node_srv;

import com.jayway.jsonpath.JsonPath;
import lombok.SneakyThrows;
import mpc.fs.UF;
import mpc.html.UXPath;
import mpc.net.IllegalHttpStatusException;
import mpe.core.ERR;
import mpe.wthttp.HttpCallMsg;
import mpu.X;
import mpu.core.ARG;
import mpu.core.QDate;
import mpu.core.RW;
import mpu.str.UST;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import udav_net.UJsoup;
import utl_jack.UJack;
import zk_com.core.IZCom;
import zk_form.notify.ZKI;
import zk_notes.node.NodeDir;
import zk_os.AppZosProps;
import zk_page.ZKR;
import zk_notes.node_state.FormState;

import java.nio.file.Path;
import java.nio.file.Paths;

public class HttpCallService {
	public static final Logger L = LoggerFactory.getLogger(HttpCallService.class);

	public static String doHttpCall_VALUE(String trackId, NodeDir node) {
		return doHttpCall(trackId, node, false, false, true);
	}

	public static String doHttpCall(String trackId, NodeDir node, boolean RETURN) {
		return doHttpCall(trackId, node, false, false, ARG.isDefEqTrue(RETURN));
	}

	public static String doHttpCall(String trackId, NodeDir node, boolean skipInnerJp, boolean skipInnerXp, boolean RETURN) {
		try {

			return doHttpCallImpl(trackId, node, skipInnerJp, skipInnerXp, RETURN);

		} catch (Throwable ex) {
			if (RETURN) {
				return X.throwException(ex);
			}
			IZCom.L.error("Call error on node (mb data is change on moment send?):" + node, ex);
			String stackTrace = ERR.getStackTrace(ex);
			if (node.upd().isSizable()) {
				node.state().writeFcDataErr(stackTrace);
			}
			if (ex instanceof IllegalHttpStatusException) {
				IllegalHttpStatusException statusEx = (IllegalHttpStatusException) ex;
//				Object msg = statusEx.getMsgWithCode();
				Object msg = statusEx.getMessage();
				if (msg != null && msg instanceof String) {
					String string = ((String) msg).trim();
					if (string.startsWith("<") && UJsoup.isXml(string)) {
						String titleCapCom = statusEx.code() + " - Response error";
						ZKI.infoEditorHtmlView(titleCapCom, string);
					} else if (string.startsWith("{") && UST.isJson(string)) {
						ZKI.infoEditorJson(string, false);
					} else if (string.startsWith("[") && UST.isJsonArray(string)) {
						ZKI.infoEditorJson(string, false);
					} else {
						ZKI.errorEditorBw(string);
					}
				} else {
					ZKI.errorEditorBw(statusEx.code() + ":" + null);
				}
			} else {
				ZKI.errorEditorBw(stackTrace);
			}
			return null;
		}
	}

	@SneakyThrows
	private static String doHttpCallImpl(String trackId, NodeDir node, boolean skipInnerJp, boolean skipInnerXp, boolean RETURN) {

		FormState state = node.state();

		String nodeData = state.nodeData();

		nodeData = InjectNode.inject(node, nodeData, trackId);

		if (AppZosProps.APD_IS_TRACE_HTTP_CALL.getValueOrDefault(false)) {
			Path file = Paths.get("./tmp/" + QDate.now_ms() + "_" + node.nodeName() + ".json");
			RW.write(file, nodeData);
			X.p(UF.ln(file.getParent()));
			X.p(UF.ln(file));
		}

		HttpCallMsg httpCallMsg = HttpCallMsg.of(nodeData);

		//

		state.deletePathFc_OkErr();

		//

		String rsp = httpCallMsg.sendHttpCall_200_204(true).trim();

		boolean isJson = UST.JSON(rsp, null) != null;
		if (isJson) {
			rsp = UJack.toStringScientific(rsp, true);
		}

		//

		state.writeFcDataOk(rsp);
		if (httpCallMsg.hasErrors()) {
			state.writeFcDataErr(httpCallMsg.getErrsAsMsg("Http Call Errors:", true));
		}

		//

		if (!skipInnerJp) {
			String jsonPath = httpCallMsg.getJsonPath(null);
			if (jsonPath != null) {
				if (UST.JSON(rsp, null) == null) {
					state.writeFcDataErr("Http Call except format JSON for apply JsonPath, but response contains is not valid JSON:", rsp);
				} else {
					Object read = JsonPath.read(rsp, jsonPath);
					if (!RETURN) {
						ZKI.infoEditorBw(jsonPath + "\n" + read);
					}
					return read + "";
				}
			}
		}

		if (!skipInnerXp) {
			String xPath = httpCallMsg.getXPath(null);
			if (xPath != null) {
				if (UST.XML_STRICT(rsp, null) == null) {
					state.writeFcDataErr("Http Call except format XML for apply XPath, but response contains is not valid XML:", rsp);
				} else {
					Object read = UXPath.parseString(rsp, xPath);
					if (!RETURN) {
						ZKI.infoEditorBw(xPath + "\n" + read);
					}
					return read + "";
				}
			}
		}

		//

		if (RETURN) {
			return rsp;
		}

		if (node.upd().isSizable()) {//if enable multi rows
			ZKR.restartPage();
		} else { //web-editor + bottomHistory
			if (rsp.startsWith("<") && UJsoup.isXml(rsp)) {
				ZKI.infoEditorHtmlView("Response", rsp);
			} else {
				ZKI.infoEditorBw(rsp);
			}
//				BottomHistoryPanel.addItemAsData(rsp, true);
		}
		return rsp;

	}

	//
	//
	//

}
