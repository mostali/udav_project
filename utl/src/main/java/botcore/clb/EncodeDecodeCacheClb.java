package botcore.clb;

import mpu.Sys;
import mpc.arr.QUEUE;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class EncodeDecodeCacheClb {

	public static final String PFX_ED_TG = "?_";
	public static final String PFX_ED_VK = "202301";
	private final static Map<String, String> cacheClb = QUEUE.cache_map_sync_FILO(10_000);

	public static String decode(String data) {
		return cacheClb.get(data);
	}

	public static String encode(String data) {
		String code = PFX_ED_TG + UUID.randomUUID().toString().substring(10);
		cacheClb.put(code, data);
		return code;
	}

	public static AtomicLong CTR = new AtomicLong();

	//max 19 num's & 263 chars
	public static String encodeNum(String data) {
//		if (data.length() <= 255) {
			//if (data.length() <= 263) {
//			return data;//it length is ok
//		}
		String next = CTR.incrementAndGet() + "";
		if (next.length() > 12) {
			CTR.set(0);
			next = CTR.incrementAndGet() + "";
		}
		String code = PFX_ED_VK + next;
		if (code.length() > 18) {
			throw new IllegalStateException("Sequence os num's max !!19!!, your already 19 ( the next step will be error)");
		}
		Sys.p(data + " >>>>>>>>>>> " + code);
		cacheClb.put(code, data);
		return code;
	}

	public static String decodeIfNum(String payload) {
		return payload != null && payload.startsWith(PFX_ED_VK) ? decode(payload) : payload;
	}


}
