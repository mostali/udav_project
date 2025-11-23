package zk_os.core;

import lombok.RequiredArgsConstructor;
import mpc.exception.WhatIsTypeException;
import mpc.fs.UFS;
import mpe.call_msg.core.NodeID;
import mpu.core.ARG;
import mpu.pare.Pare;
import udav_net.apis.zznote.ItemPath;
import zk_os.coms.AFC;
import zk_os.db.net.AnonimAppWebUsr;
import zk_os.db.net.WebUsr;
import zk_page.ZKR;
import zk_page.core.PagePathInfo;
import zk_page.core.SpVM;
import zk_page.index.RSPath;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public class Sdnu {
	private WebUsr webUsr;
	public final Long userSid;
	public final Sdn sdn;

	public static Sdnu of(Long userSid, String plane, String page) {
		return new Sdnu(userSid, Sdn.of(plane, page));
	}

	public static Sdnu of(long userSid, Sdn sdn) {
		return new Sdnu(userSid, sdn);
	}

	public static Sdnu of(WebUsr usr, Sdn sdn) {
		return new Sdnu(usr, sdn);
	}

	public Sdnu(WebUsr user, Sdn sdn) {
		this.webUsr = user;
		this.userSid = webUsr.getSid();
		this.sdn = sdn;
	}

	public Sdnu(Long userSid, Sdn sdn) {
		this.userSid = userSid;
		if (userSid.equals(AnonimAppWebUsr.SID)) {
			webUsr = AnonimAppWebUsr.ANONIM_USER;
		}
		this.sdn = sdn;
	}

	public WebUsr getWebUsr() {
		if (webUsr != null) {
			return webUsr;
		}
		return webUsr = WebUsr.loadBySid(userSid);
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Sdnu)) {
			return false;
		}
		Sdnu sdnu = (Sdnu) o;
		return Objects.equals(userSid, sdnu.userSid) && Objects.equals(sdn, sdnu.sdn);
	}

	@Override
	public int hashCode() {
		return Objects.hash(userSid, sdn);
	}

	@Override
	public String toString() {
		return "Sdnu#" +
				+userSid +
				"|" + sdn.key() + "/" + sdn.val();
	}
}
