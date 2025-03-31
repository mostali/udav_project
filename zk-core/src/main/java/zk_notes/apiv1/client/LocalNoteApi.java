package zk_notes.apiv1.client;

import mpc.env.APP;
import mpu.X;
import udav_net.apis.zznote.NodeID;
import udav_net.apis.zznote.NoteApi;
import zk_os.AppZosConfig;

public class LocalNoteApi extends NoteApi {

	public LocalNoteApi() {
		super(APP.getAppHost("q.com:8080"), AppZosConfig.SUPER_KEY);
	}
//	public LocalNoteApi(String url,String key) {
//		super(url, AppZosConfig.SUPER_KEY);
//	}

	public static void main(String[] args) {
		String s = new LocalNoteApi().GET_event(NodeID.of("test/test-qztask/qzEval"), "put", "k", "v");
		X.exit(s);
	}
}
