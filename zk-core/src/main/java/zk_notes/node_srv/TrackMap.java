package zk_notes.node_srv;

import lombok.RequiredArgsConstructor;
import mpc.exception.NI;
import mpe.wthttp.HttpCallMsg;
import mpu.X;
import mpu.core.ARG;
import zk_os.core.NodeData;
import zk_page.core.SpVM;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class TrackMap {

	private final static ConcurrentMap<String, Map> trackContextMap = new ConcurrentHashMap();

//	public static String getTrackIdManually(Map context) {
//		String trackId = UUID.randomUUID().toString();
//		trackContextMap.put(trackId, IT.NN(context));
//		return trackId;
//	}

	public static String getTrackIdNewContext(Map trackContext) {
		String trackId = UUID.randomUUID().toString();
		trackContextMap.put(trackId, trackContext);
		return trackId;
	}

	@Deprecated
	public static String getTrackIdFromQuery() {
		NI.stop("who tid?");
		return SpVM.get().getQuery().getFirstAsStr(HttpCallMsg.TID, null);
	}

	public static Map getContext(String trackId, Map... defRq) {
		Map map = trackId == null ? null : trackContextMap.get(trackId);
		return map != null ? map : ARG.toDefThrowMsg(() -> X.f("TrackContext '%s' not found", trackId), defRq);
	}

	@RequiredArgsConstructor
	public static class TrackId {
		public final String uuid;
		public final NodeData nodeData;

//		public static TrackId of(String uuid, NodeData nodeData) {
//			return new TrackId(uuid, nodeData);
//		}

		public Map getContext() {
			return TrackMap.getContext(uuid);
		}

		public static TrackId get(TrackId... defRq) {
			TrackId trackId = TRACK_TL.get();
			return trackId != null ? trackId : ARG.toDefThrowMsg(() -> X.f("Not found TL context for trackId"), defRq);
		}
	}

	private static final ThreadLocal<TrackId> TRACK_TL = new ThreadLocal<>();

	public static abstract class EvalTrack<T> {
		private NodeData nodeData;

		public EvalTrack<T> withNode(NodeData nodeData) {
			this.nodeData = nodeData;
			return this;
		}

		private Map trackContext;

		public EvalTrack<T> trackContext(Map trackContext) {
			this.trackContext = trackContext;
			return this;
		}

		public T doEval() {

			String trackIdCnewContext = getTrackIdNewContext(trackContext);
			try {
				TrackId value = new TrackId(trackIdCnewContext, nodeData);
				on_add(value);
				return doEvalImpl(value);
			} finally {
				on_clear(trackIdCnewContext);
			}
		}

		public static void on_clear(String trackId) {
			trackContextMap.remove(trackId);
//			TaskPanel.stopTask(Thread.currentThread());
			TRACK_TL.remove();
		}

		private void on_add(TrackId value) {
//			TaskPanel.addTask(Thread.currentThread(), value);
			TRACK_TL.set(value);
		}

		abstract T doEvalImpl(TrackMap.TrackId track);


	}
}
