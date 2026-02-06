package nettm.client;

import com.jayway.jsonpath.JsonPath;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import mpc.json.GsonMap;
import mpu.IT;
import mpu.core.RW;
import mpu.str.UST;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class TChat {
	final TtmApi ttmApi;

	List<Long> chats;

	public List<Long> chats() {
		return chats != null ? chats : (chats = ttmApi.GET_Chats_Ids());
	}

	Long chatId = null;

	public Long chatId() {
		return IT.NN(chatId);
	}

	public TChat choice(int i) {
		IT.isIndex(i, chats());
		chatId = chats.get(i);
		return this;
	}

	public TChat choice(String chatId) {
		Long chatId0 = UST.LONG(chatId);
		IT.state(chats().contains(chatId0), "alone chat:" + chatId);
		this.chatId = chatId0;
		return this;
	}

	//		String msgId = null;
	public GsonMap msg() {
		return IT.NN(msgGm, "set msgId");
	}

	@SneakyThrows
	public Path msg_video_file_dld_to(String msgId, Path storeDir) {
		InputStream data = msg_video_file_dld(msgId);
		Path file = storeDir.resolve(msg_video_filename());
		RW.write_(file, data);
		return file;
	}

	public InputStream msg_video_file_dld(String msgId) {
		Number fileId = JsonPath.read(msg(msgId).toStringJson().toString(), "$.content.video.video.id");
		return ttmApi.DLD_File(msg_video_file_id(msgId).toString());
	}

	public Integer msg_video_file_id(String msgId) {
		Number fileId = JsonPath.read(msg(msgId).toStringJson().toString(), "$.content.video.video.id");
		return fileId.intValue();
	}

	public String msg_video_filename() {
		String fn = JsonPath.read(msgGm.toStringJson().toString(), "$.content.video.fileName");
		return fn;
	}

	GsonMap msgGm = null;

	public GsonMap msg(String msgId) {
		if (msgGm != null) {
			return msgGm;
		}
		return msgGm = ttmApi.GET_Msg(chatId() + "", msgId);
	}

	public List<GsonMap> msgs() {
		IT.state(chatId != null, "before choice chat");
		GsonMap gsonMap = ttmApi.GET_Msgs(chatId + "");
		Integer count = gsonMap.getAsInt("totalCount");
		List l = new ArrayList();
		if (count < 1) {
			return l;
		}
		List messages = gsonMap.getAsArrayGsonMap("messages");
		return messages;
	}

//		public List<GsonMap> msgs(QDate day) {
//
//			return gsonMap.getAsArrayGsonMap("messages");
//		}
}
