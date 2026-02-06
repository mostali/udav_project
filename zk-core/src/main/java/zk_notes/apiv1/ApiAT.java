package zk_notes.apiv1;

import mpu.X;
import mpu.pare.Pare;
import mpe.call_msg.core.NodeID;
import udav_net.apis.zznote.NoteApi;
import zk_notes.apiv1.client.NoteApi0;

public class ApiAT {
	public static NoteApi0 noteApi;

	public static void main(String[] args) {
		noteApi = new NoteApi0();

//		X.p(test_get_items());
		X.p(test_get_event());


	}

	private static Pare<String, Object> test_get_event() {
		String rslt = noteApi.GET_event(NodeID.of("/mypage/mynote1"), "get", NoteApi.PK_K, NoteApi.PK_V);
		return Pare.of("test_get_event", rslt);
	}

	private static Pare<String, Object> test_get_items() {
		String rslt = noteApi.GET_items(".index", "mypage");
		return Pare.of("test_get_items", rslt);
	}
}
