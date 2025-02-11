package zk_notes.apiv1.client;

import mpc.env.APP;
import udav_net.apis.zznote.NoteApi;
import zk_os.AppZosConfig;

public class LocalNoteApi extends NoteApi {

	public LocalNoteApi() {
		super(APP.getAppDomain(), AppZosConfig.SUPER_KEY);
	}
}
