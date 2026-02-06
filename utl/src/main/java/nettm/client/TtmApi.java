package nettm.client;

import com.google.gson.internal.LinkedTreeMap;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import mpc.env.EnvTlp;
import mpc.exception.DRQ;
import mpc.exception.WhatIsTypeException;
import mpc.json.GsonMap;
import mpc.map.MapTableContract;
import mpc.net.IllegalHttpStatusException;
import mpc.net.JHttp;
import mpe.str.CN;
import mpf.contract.IContract;
import mpu.IT;
import mpu.X;
import mpu.core.ARG;
import mpu.core.ARR;
import mpu.core.ARRi;
import mpu.core.QDate;
import mpu.pare.Pare;
import mpu.str.JOIN;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class TtmApi {

	public static void printChatNames(TtmApi ttmApi) {
		List<Long> chatsIds = ttmApi.GET_Chats_Ids();
		Map<Long, String> titles = chatsIds.stream().map(chat -> {
			String title = ttmApi.GET_Chat(chat + "").getAsString("title");
			X.p(chat + ":" + title);
			return Pare.<Long, String>of(chat, title);
		}).collect(Collectors.toMap(k -> k.key(), v -> v.val()));
	}

	public static final String UP_API_PARTURL = "api";
	public static final String _UP_API_PARTURL = "/" + UP_API_PARTURL;

	public static Function<String, String> funcFindHostByAlias = null;

	private final @Getter ZApiUrl zApiUrl;

	public TtmApi() {
		this(EnvTlp.ofHlpTtm("def"));
	}

	public TtmApi(EnvTlp envTlp) {
		this(envTlp.readHostWithPort(), envTlp.readPass());
	}

	public TtmApi(String domainWithPort, String ska) {
		zApiUrl = new ZApiUrl(domainWithPort, ska);
	}

	@SneakyThrows
	public GsonMap GET_Chats() throws IllegalHttpStatusException {
		String url = zApiUrl.GET_Chats();
		return JHttp.GET_BODY(url, null, GsonMap.class, 200);
	}

	//
	@SneakyThrows
	public List<Long> GET_Chats_Ids() throws IllegalHttpStatusException {
		GsonMap rspMap = GET_Chats();
		List<LinkedTreeMap> vls = (List<LinkedTreeMap>) rspMap.values().stream().map(o -> (LinkedTreeMap) o).collect(Collectors.toList());
		List<Long> chatIds = vls.stream().map(m -> ((Double) m.get("chatId")).longValue()).collect(Collectors.toList());
		return chatIds;
	}

	@SneakyThrows
	public GsonMap GET_Chat(String chatId) throws IllegalHttpStatusException {
		String url = zApiUrl.GET_Chat(chatId);
		return JHttp.GET_BODY(url, null, GsonMap.class, 200);
	}

	public List<ITPost> GET_MsgsList(String chatId, QDate day) throws IllegalHttpStatusException {
		return GET_MsgsList(chatId, m -> QDate.ofEpoch(m.getAsInt("date")).day == day.day).stream().map(ITPost::of).collect(Collectors.toList());
	}

	public interface ITPost extends IContract {

		Integer getDate();

		default QDate getQDate() {
			return QDate.ofEpoch(getDate());
		}

		Long getId();

		Long getChatId();

		GsonMap getContent();

		default String getContentCaption(GsonMap... defRq) {
			return getContent().getAsGsonMap("caption").getAsString("text");
		}

		default GsonMap getContentMedia(GsonMap... defRq) {
			String contentTypeName = ARG.isDef(defRq) ? getContentTypeName(null) : getContentTypeName();
			if (contentTypeName == null) {
				return ARG.toDef(defRq);
			}
			return (GsonMap) getContent().getAs(contentTypeName, GsonMap.class, defRq);
//			return ARG.toDefThrow(() -> new DRQ("Except content photo or video"), defRq);
		}

		default String getContentTypeName(String... defRq) {
			GsonMap content = getContent();
			if (content.containsKey("video")) {
				return "video";
			} else if (content.containsKey("photo")) {
				return "photo";
			}
			return ARG.toDefThrow(() -> new DRQ("Except content photo or video"), defRq);

		}

		default Integer getContentMediaFileId() {
			switch (getContentTypeName()) {
				case "photo": {
					List<GsonMap> sizes = getContentMedia().getAsArrayGsonMap("sizes");
					GsonMap last = ARRi.last(sizes);
					return last.getAsGsonMap("photo").getAsInt("id");
				}
				case "video": {
					return getContentMedia().getAsGsonMap("video").getAsInt("id");
				}
				default:
					throw new WhatIsTypeException(getContentTypeName());
			}
		}

		public static ITPost of(Map data) {
			return MapTableContract.buildContract_DefRq(data, ITPost.class);
		}

		default String toStringSimple() {
			return X.f("%s_%s(%s)>>>%s_%s", getChatId(), getId(), getQDate(), getContentTypeName(), getContentMediaFileId());
		}

	}

	public List<GsonMap> GET_MsgsList(String chatId, Predicate<GsonMap> filter) throws IllegalHttpStatusException {
		GsonMap gm = GET_Msgs(chatId);
		Integer totalCount = gm.getAsInt("totalCount");
		if (totalCount < 1) {
			return ARR.EMPTY_LIST;
		}
//		List<GsonMap> l = new ArrayList();
		List<Map> messages = gm.getAsArrayGsonMap("messages");
		return messages.stream().map(GsonMap::of).filter(filter).collect(Collectors.toList());
	}

	@SneakyThrows
	public GsonMap GET_Msgs(String chatId) throws IllegalHttpStatusException {
		String url = zApiUrl.GET_Msgs(chatId);
		return JHttp.GET_BODY(url, null, GsonMap.class, 200);
	}

	@SneakyThrows
	public GsonMap GET_Msg(String chatId, String msgId) throws IllegalHttpStatusException {
		String url = zApiUrl.GET_Msg(chatId, msgId);
		return JHttp.GET_BODY(url, null, GsonMap.class, 200);
	}

	@SneakyThrows
	public InputStream DLD_File(String fileId) throws IllegalHttpStatusException {
		String url = zApiUrl.GET_DldFile(fileId);
		return JHttp.GET_BODY(url, null, InputStream.class, 200);
	}

	@SneakyThrows
	public GsonMap GET_MsgReact(String chatId, String msgId) throws IllegalHttpStatusException {
		String url = zApiUrl.urlToMessage_react(chatId, msgId);
		return JHttp.GET_BODY(url, null, GsonMap.class, 200);
	}

	public static class ZApiUrl {

		private static final String QP_TOKEN = "t";
		final String domainNameWithPort;
		final String ctx = "";
		final boolean https = false;
		final String t;

		public ZApiUrl(String domainNameWithPort, String t) {
			this.domainNameWithPort = domainNameWithPort;
			this.t = t;
		}


		private Object http() {
			return https ? "https://" : "http://";
		}


		public String urlToChats() {
			String sdPart = "";
			String opPart = "/get";
			String namemPart = "/chats";
			String query = queryPart();
			return http() + sdPart + domainNameWithPort + ctxPart() + _UP_API_PARTURL //
					+ opPart + namemPart + query;
		}

		public String urlToChat(String chatId) {
			String sdPart = "";
			String opPart = "/get";
			String namemPart = "/chat";
			String query = queryPart();
			return http() + sdPart + domainNameWithPort + ctxPart() + _UP_API_PARTURL //
					+ opPart + namemPart + "/" + IT.NE(chatId) + query;
		}

		public String urlToMsgs(String chatId) {
			String sdPart = "";
			String opPart = "/get";
			String namemPart = "/msgs";
			String query = queryPart();
			return http() + sdPart + domainNameWithPort + ctxPart() + _UP_API_PARTURL //
					+ opPart + namemPart + "/" + IT.NE(chatId) + query;
		}

		public String urlToMsg(String chatId, String msgId) {
			String sdPart = "";
			String opPart = "/get";
			String namemPart = "/msg";
			String query = queryPart();
			return http() + sdPart + domainNameWithPort + ctxPart() + _UP_API_PARTURL //
					+ opPart + namemPart + "/" + IT.NE(chatId) + "/" + IT.NE(msgId) + query;
		}


		public String urlToDldFile(String fileId) {
			String sdPart = "";
			String opPart = "/dld";
			String namemPart = "/file";
			String query = queryPart();
			return http() + sdPart + domainNameWithPort + ctxPart() + _UP_API_PARTURL //
					+ opPart + namemPart + "/" + IT.NE(fileId) + query;
		}

		public String urlToMessage_react(String chatId, String msgId) {
			String sdPart = "";
			String opPart = "/get";
			String namemPart = "/msg_react";
			String query = queryPart();
			return http() + sdPart + domainNameWithPort + ctxPart() + _UP_API_PARTURL //
					+ opPart + namemPart + "/" + IT.NE(chatId) + "/" + IT.NE(msgId) + query;
		}


		private String ctxPart() {
			return X.empty(ctx) ? "" : "/" + ctx;
		}

		private static @NotNull String opSymbolPart(boolean isGetOrPut) {
			return "/" + (isGetOrPut ? "*" : "!");
		}

		private String queryPart() {
			return queryPart(null);
		}

		private String queryPart(String v) {
			String skaArg = X.empty(t) ? "" : QP_TOKEN + "=" + t;
			String vArg = X.empty(v) ? "" : CN.V + "=" + v;
			List<String> ql = ARR.as(skaArg, vArg).stream().filter(X::notEmpty).collect(Collectors.toList());
			if (ql.isEmpty()) {
				return "";
			}
			return JOIN.allBy(ql, "&", "?", "");
		}


		//
		//

		public String GET_Chats() {
			return urlToChats();
		}

		public String GET_Chat(String chatId) {
			return urlToChat(chatId);
		}

		public String GET_Msgs(String chatId) {
			return urlToMsgs(chatId);
		}

		public String GET_Msg(String chatId, String msgId) {
			return urlToMsg(chatId, msgId);
		}

		public String GET_DldFile(String chatId) {
			return urlToDldFile(chatId);
		}

	}
}
