package zk_page.node;

import api_kafka37.ApiKafka;
import com.jayway.jsonpath.JsonPath;
import lombok.SneakyThrows;
import mp.utl_ndb.Db;
import mp.utl_ndb.IJdbcUrl;
import mpc.exception.NI;
import mpc.exception.WhatIsTypeException;
import mpc.types.abstype.AbsType;
import mpe.core.ERR;
import mpe.rt.ObjThread;
import mpe.str.URx;
import mpe.wthttp.HttpCallMsg;
import mpu.IT;
import mpu.X;
import mpu.core.ARG;
import mpu.str.JOIN;
import mpu.str.STR;
import mpu.str.UST;
import org.apache.kafka.clients.producer.Callback;
import org.apache.regexp.RE;
import utl_jack.UJack;
import zk_com.core.IZCom;
import zk_form.notify.ZKI;
import zk_page.ZKR;
import zk_page.node_state.FormState;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class NodeDirCallService {

	public static class InjectNode {

		public static String inject(NodeDir node, String nodeData) {
			List<String> allGroup = URx.findAllGroup(nodeData, URx.PT_PLH_DOUBLE_ROUND);
			if (X.empty(allGroup)) {
				return nodeData;
			}
			Map map = new LinkedHashMap<>();
			for (String injectPl : allGroup) {
				String name = STR.substr(STR.substr(injectPl, 2), -2);
				String vl = NodeDirCallService.doHttpCall(node.cloneWithItem(name), false, true);
				map.put(injectPl, vl);
			}
			String s = URx.fillAllGroup(nodeData, URx.PT_PLH_DOUBLE_ROUND, map, true);
			return s;
		}
	}

	public static String doHttpCall(NodeDir node, boolean skipInnerJp, boolean... RETURN) {

		boolean isRETURN = ARG.isDefEqTrue(RETURN);

		HttpCallMsg httpCallMsg;
		try {
			FormState state = node.state();

			String nodeData = state.nodeData();

			nodeData = InjectNode.inject(node, nodeData);

			httpCallMsg = HttpCallMsg.of(nodeData);

			//

			state.deletePathFc(1);
			state.deletePathFc(2);

			//

			String rsp = httpCallMsg.sendHttpCall(true).trim();

			boolean isJson = UST.GSON(rsp, null) != null;
			if (isJson) {
				rsp = UJack.toStringScientific(rsp, true);
			}

			//

			state.writeFcDataOk(rsp);
			if (httpCallMsg.hasErrors()) {
				state.writeFcDataError(httpCallMsg.getErrsAsMsg());
			}

			//

			if (!skipInnerJp) {
				String jsonPath = httpCallMsg.getJsonPath(null);
				if (jsonPath != null) {
					Object read = JsonPath.read(rsp, jsonPath);
					if (!isRETURN) {
						ZKI.infoEditorBw(jsonPath + "\n" + read);
					}
					return read + "";
				}
			}

			//

			if (isRETURN) {
				return rsp;
			}

			if (node.upd().isSizable()) {//if enable multi rows
				ZKR.restartPage();
			} else { //web-editor + bottomHistory
				ZKI.infoEditorBw(rsp);
//				BottomHistoryPanel.addItemAsData(rsp, true);
			}
			return rsp;
		} catch (Throwable ex) {
			if (isRETURN) {
				return X.throwException(ex);
			}
			IZCom.L.error("Call error on node (mb data is change on moment send?):" + node, ex);
			String stackTrace = ERR.getStackTrace(ex);
			if (node.upd().isSizable()) {
				node.state().writeFcDataError(stackTrace);
			}
			ZKI.errorEditorBw(stackTrace);
			return null;
		}
	}


	@SneakyThrows
	public static List<List<AbsType>> doSqlCall(NodeDir node, boolean... RETURN) {
		List<String>[] headersAndBodyLines = HttpCallMsg.getHeadersAndBodyLines(node.state().nodeDataCached(true));
		String sql = JOIN.allByNL(headersAndBodyLines[1]);
//		List<Map<String, AbsType>> maps = Db.queryMap_(IJdbcUrl.ofULP(headersAndBodyLines[0]), sql);
		List<List<AbsType>> maps = Db.queryList_(IJdbcUrl.ofULP(headersAndBodyLines[0]), sql);
//		if (ARG.isDefEqTrue(RETURN)) {
//			return maps;
//		}
		return maps;
	}

	public static String doKafkaCall(NodeDir node, boolean... RETURN) {

		boolean isRETURN = ARG.isDefEqTrue(RETURN);

		HttpCallMsg httpCallMsg;

		try {
			FormState state = node.state();

			httpCallMsg = HttpCallMsg.of(state.nodeData());

			IT.state(httpCallMsg.isKafkaCall());

			switch (httpCallMsg.http_method) {
				case PUT: {

					state.deletePathFc(1);
					state.deletePathFc(2);

					ObjThread objThread = new ObjThread("sendKafkaMsg", true) {
						@Override
						public void run() {
//							NI.stop("ni kafka");
							Callback sendCallback = (metadata, err) -> {
								if (err != null) {
									set_result_error(err);
								} else {
									String rsp = X.f("%s:%s*%s", metadata.topic(), metadata.partition(), metadata.offset());
									set_result_object(rsp);
								}
							};
							ApiKafka.produceMsg(httpCallMsg, sendCallback);
						}
					};

					Object resultObject = objThread.getAndWaitResult(30_000, true);

//					boolean isSizable = state.upd().isSizable();

					Boolean hasResult = objThread.hasResult();
					if (hasResult) {
						String rslt = resultObject.toString();
						state.writeFcDataOk(rslt);
						if (isRETURN) {
							return rslt;
						}
//						if (isSizable) {
//							ZKR.restartPage();
//						} else {
						ZKI.infoEditorBw(rslt);
						//BottomHistoryPanel.
//						}
					}
					if (objThread.hasErrors()) {
						String errorMessage = objThread.getErrorMessage();
						objThread.getErrors().forEach(t -> IZCom.L.info(ERR.getStackTrace((Throwable) t)));
						state.writeFcDataError(errorMessage);

						if (isRETURN) {
							return null;
						}

//						if (isSizable) {
//							ZKR.restartPage();
//						} else {
						ZKI.alert(errorMessage);
//						}
					}
					return null;
				}
				case GET: {
//							ApiKafka.consumeMsg(httpCallMsg,);

					throw NI.stop();
				}
				default:
					throw new WhatIsTypeException(httpCallMsg.http_method);

			}
		} catch (Throwable ex) {
			IZCom.L.error("Call error on node (mb data is change on moment send?):" + node, ex);
			node.state().writeFcDataError(ERR.getStackTrace(ex));
			if (isRETURN) {
				return X.throwException(ex);
			}
			ZKI.alert(ERR.getStackTrace(ex));
			return null;
		}
	}
}
