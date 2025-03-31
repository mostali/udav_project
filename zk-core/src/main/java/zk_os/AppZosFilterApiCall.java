package zk_os;

import mpc.exception.WhatIsTypeException;
import mpc.net.CON;
import mpc.net.query.QueryUrl;
import mpu.IT;
import mpu.X;
import mpu.pare.Pare;
import mpu.pare.Pare3;
import mpu.str.TKN;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import udav_net.apis.zznote.ItemPath;
import udav_net.apis.zznote.NodeID;
import udav_net.apis.zznote.NoteApi;
import utl_web.URsp;
import zk_notes.apiv1.ApiCase;
import zk_notes.apiv1.NodeApiChars;
import zk_notes.apiv1.treenode.TreeRestCall;
import zk_notes.node.NodeDir;
import zk_notes.node_srv.fsman.NodeFileTransferMan;
import zk_os.core.Sdn;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class AppZosFilterApiCall {

	private static final Logger L = LoggerFactory.getLogger(AppZosFilterApiCall.class);

	static boolean extractApiPostDeleteRequest(ServletRequest request, HttpServletResponse response, String servletPath, HttpServletRequest httpRequest) throws IOException {

		CON.Method method = CON.Method.valueOf(((HttpServletRequest) request).getMethod());

		switch (method) {
			case PUT:
			case POST:
			case DELETE:
				break;
			default:
				return false;
		}

		ApiCase apiCase = ApiCase.ofServletPath(servletPath);

		if (apiCase == null) {
			return false;
		}

		String callPath = servletPath.substring(apiCase.apiName().length() + 2);

		String queryString = ((HttpServletRequest) request).getQueryString();

		QueryUrl qUrl = QueryUrl.of(queryString);

		String firstAsStr = qUrl.getFirstAsStr(NoteApi.SKA, null);

		if (!AppZosConfig.SUPER_KEY.equals(firstAsStr)) {
			URsp.sendResponseAndClose(response, 401, "no auth");
			return true;
		}

		String sd3 = AppZosConfig.getSd3(httpRequest);

		String sd3Part = ItemPath.wrapSd3(sd3);

		Path nodePath;

		String ctrlOperSym = null;

		switch (method) {

			case PUT:
			case POST: {

				switch (apiCase) {

					case _api: {

						boolean hasSymUD = !NodeApiChars.isCallPathStartWithUpDownPart(callPath);
						if (!hasSymUD) {
							callPath = ItemPath.PAGE_INDEX_ALIAS + "/" + callPath;
						}
						String[] pageArgs = TKN.two(callPath, "/");

						String pagenamePart = IT.NE(pageArgs[0]);

						String[] two = TKN.two(pageArgs[1], "/");

						ctrlOperSym = two[0];

						IT.state(NodeApiChars.isCallPathStartWithUpDown(ctrlOperSym), "illegal %s", ctrlOperSym);

						String itemPart = IT.NE(two)[1];

						nodePath = Paths.get(sd3Part).resolve(pagenamePart).resolve(itemPart);

						break;
					}
					case _ati: {

						String pagename = TKN.first(callPath, "/", 0);
						String nodename = TKN.first(callPath, "/", 1);

						nodePath = Paths.get(sd3Part).resolve(pagename).resolve(nodename);

						break;
					}
					default:
						throw new WhatIsTypeException(apiCase);

				}

				break;
			}
			case DELETE: {

				switch (apiCase) {

					case _api: {

						nodePath = Paths.get(sd3Part).resolve(callPath);
						ctrlOperSym = null;

						break;
					}
//					case _ati: {
//
//						NI.stop("ni");
//
////						String pagename = USToken.first(callPath, "/", 0);
////						String nodename = USToken.first(callPath, "/", 1);
////						nodePath = Paths.get(sd3Part).resolve(pagename).resolve(nodename);
//
//						break;
//					}
					default:
						throw new WhatIsTypeException(apiCase);

				}

				break;
			}
			default:
				return false;
		}

		if (nodePath.getNameCount() == 2) {
			NodeID nodeID = NodeID.of(nodePath + "/");
			if (handleDeletePageResponse(response, method, nodeID)) {
				return true;
			}
		}
		ItemPath itemPath = ItemPath.of(nodePath);

		itemPath.throwIsNotWhole();

		NodeDir nodeDir = NodeDir.ofNodeName(Pare.of(sd3, itemPath.pageName()), itemPath.nodeName());

		switch (method) {
			case DELETE: {
				boolean statusDeleted = false;
				if (nodeDir.fdExist()) {
					NodeFileTransferMan.deleteItem(nodeDir);
					statusDeleted = true;
					L.info("Delete item '" + itemPath + "' from '" + nodeDir.state().pathFc() + "'");
				} else {
					L.info("Node item not exist '" + itemPath + "'");
				}
				URsp.sendResponseAndClose(response, 200, "Delete " + itemPath.nodeName() + " item " + (statusDeleted ? " successfully" : "NO"));
				return true;
			}
			case PUT:
			case POST: {

				String rspData200;

				switch (apiCase) {
					case _api:

						switch (ctrlOperSym) {
							case NodeApiChars.UP: {
								if (nodeDir.fdExist()) {
									rspData200 = nodeDir.state().readFcData();
								} else {
									URsp.sendResponseAndClose(response, 404, X.f(NoteApi.MSG_404_ITEM_NOTE_FOUND, nodeDir.nodeName()));
									return true;
								}
								break;
							}
							case NodeApiChars.DOWN: {
								String body = X.toStringFrom(request.getInputStream(), null);
								if (X.empty(body)) {
									body = qUrl.getFirstAsStr("v", null);
									if (body == null) {
										URsp.sendResponseAndClose(response, 400, NoteApi.MSG_400_SET_BODY);
										return true;
									}
								}
								nodeDir.state().writeFcData(body);
								rspData200 = X.f(NoteApi.MSG_200_ITEM_ADDED, itemPath.nodeName());
								break;
							}
							default:
								throw new WhatIsTypeException("What is pattern for POST call? " + ctrlOperSym);
						}

						break;

					case _ati:

						String body = X.toStringFrom(request.getInputStream(), null);
						if (X.empty(body)) {
							body = qUrl.getFirstAsStr("v", null);
							if (body == null) {
								URsp.sendResponseAndClose(response, 400, NoteApi.MSG_400_SET_BODY);
								return true;
							}
						}

						String item2 = TKN.first(callPath, "/", 2);
						String k = qUrl.getFirstAsStr("k");

						Pare3<String, String, String> oper = Pare3.of(item2, k, body);

						Pare<Integer, String> rspPare = new TreeRestCall(nodeDir.nodeID(), oper).apply();
						URsp.sendResponseAndClose(response, rspPare);
						return true;

					default:
						throw new WhatIsTypeException(apiCase);
				}


				URsp.sendResponseAndClose(response, 200, rspData200);

				return true;
			}

			default:
				throw new WhatIsTypeException(method);

		}

	}

	private static boolean handleDeletePageResponse(HttpServletResponse response, CON.Method method, NodeID node) {
		switch (method) {
			case DELETE: {
				boolean statusDeleted = false;
				if (Sdn.existPage(node.sdn())) {
					NodeFileTransferMan.deletePage(Sdn.getPageDir(node.sdn()));
					statusDeleted = true;
					L.info("Delete page '" + node + "' from '" + node + "'");
				} else {
					L.info("Node page not exist '" + node + "'");
				}
				URsp.sendResponseAndClose(response, 200, "Delete page '" + node.pageRq() + "' " + (statusDeleted ? " successfully" : "NO"));
				return true;
			}
			default:
				throw new WhatIsTypeException(method);

		}
	}
}
