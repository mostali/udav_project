package mpe.call_msg.injector;

import lombok.RequiredArgsConstructor;
import mpe.call_msg.core.INode;
import mpu.X;
import mpu.core.ARG;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class TrackMap {

	private final static ConcurrentMap<String, Map> trackContextMap = new ConcurrentHashMap();

	public static String getTrackIdNewContext(Map trackContext) {
		String trackId = UUID.randomUUID().toString();
		trackContextMap.put(trackId, trackContext);
		return trackId;
	}

	public static Map getContext(String trackId, Map... defRq) {
		Map map = trackId == null ? null : trackContextMap.get(trackId);
		return map != null ? map : ARG.toDefThrowMsg(() -> X.f("TrackContext '%s' not found", trackId), defRq);
	}

//	@RequiredArgsConstructor
	public static class TrackId {
		public final String uuid;
		public final INode iNode;//nodeData

		public TrackId(String uuid, INode iNode) {
			this.uuid = uuid;
			this.iNode = iNode;
		}

		public Map getContext() {
			return TrackMap.getContext(uuid);
		}

	}

	private static final ThreadLocal<TrackId> TRACK_TL = new ThreadLocal<>();

	public static abstract class EvalTrack<T> {
		private INode nodeData;

		public EvalTrack<T> withNode(INode nodeData) {
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
			TRACK_TL.remove();
		}

		private void on_add(TrackId value) {
			TRACK_TL.set(value);
		}

		protected abstract T doEvalImpl(TrackMap.TrackId track);

	}
}
