package zk_notes.apiv1.client;

import mpc.env.APP;
import mpu.X;
import udav_net.apis.zznote.NoteApi;
import zk_os.AppZosConfig;

import java.util.List;

public class NoteApi0 extends NoteApi {

	@Deprecated
	public NoteApi0() {
		super(APP.HOST.getAppHost0("q.com:8080"), AppZosConfig.SUPER_KEY);
	}

	public NoteApi0(String domainWithPort, String ska) {
		super(domainWithPort, ska);
	}

	public static NoteApi0 ofZZNoteRu0() {
		return new NoteApi0(APP.HOST.getAppHost0("zznote.ru"), APP.APK_SUPER_KEY_DEGAULT);
	}
	public static NoteApi0 ofXNodeRu0() {
		return new NoteApi0(APP.HOST.getAppHost0("xnode.ru"), APP.APK_SUPER_KEY_DEGAULT);
	}

	public static NoteApi0 of(String host, String ska) {
		return new NoteApi0(host, ska);
	}

	public static void main(String[] args) {

		List<String> s2 = of("q.com:8080", "go").GET_itemsList((String) null);
//		String s2 = of("q.com:8080", "go").GET_item(NodeID.of(""));
//		String s2 = of("a.com:7001", "zz").GET_items("nginx");
		X.exit(s2);
		String s = ofZZNoteRu0().GET_items((String) null);
//		String s = ofZZN().GET_items((String) null);
//		String s = new NoteApi0().GET_event(NodeID.of("test/test-qztask/qzEval"), "put", "k", "v");
		X.exit(s);
	}
}
