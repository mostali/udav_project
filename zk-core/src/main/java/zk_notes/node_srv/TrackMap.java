package zk_notes.node_srv;

import mpc.net.query.QueryUrl;
import mpe.wthttp.HttpCallMsg;
import mpu.IT;
import mpu.X;
import mpu.core.ARG;
import zk_page.core.SpVM;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class TrackMap {

	private final static ConcurrentMap<String, Map> track = new ConcurrentHashMap();

	public static String getTrackId() {
		return getTrackId(false);
	}

	public static String getTrackIdManually(Map context) {
		String trackId = UUID.randomUUID().toString();
		track.put(trackId, IT.NN(context));
		return trackId;
	}

	public static String getTrackId(boolean create) {
		QueryUrl query = SpVM.get().getQuery();
		if (create) {
			Map trackContext = query.getMapWithKeyPfx("$$", true);
			String trackId = UUID.randomUUID().toString();
//				track.put(trackId, FirstValueMap.of(trackContext));
			track.put(trackId, trackContext);
			return trackId;
		}
		return query.getFirstAsStr(HttpCallMsg.TID, null);
	}

	public static Map getContext(String trackId, Map... defRq) {
		Map map = trackId == null ? null : track.get(trackId);
		return map != null ? map : ARG.toDefThrowMsg(() -> X.f("TrackContext '%s' not found", trackId), defRq);
	}

	public static void clear(String trackId) {
		track.remove(trackId);
	}
}
