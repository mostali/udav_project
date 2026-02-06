package zk_os.sec;

import zk_page.ZkCookie;

import java.util.UUID;

public class ZKUser {


	public static final String ANOUS = "anous";

	public static UUID getUserUUID() {
		UUID uuid = ZkCookie.getCookieValueAs(ANOUS, UUID.class, null);
		if (uuid != null) {
			return uuid;
		}
		ZkCookie.setCookie(ANOUS, uuid = UUID.randomUUID(),false);
		return uuid;
	}
}
