package zk_os.sec;

import zk_page.ZKR;

import java.util.UUID;

public class ZKUser {


	public static final String ANOUS = "anous";

	public static UUID getUserUUID() {
		UUID uuid = ZKR.getCookieValueAs(ANOUS, UUID.class, null);
		if (uuid != null) {
			return uuid;
		}
		ZKR.setCookie(ANOUS, uuid = UUID.randomUUID());
		return uuid;
	}
}
