package mp.utl_odb.netapp.mdl;

import lombok.Getter;
import lombok.SneakyThrows;
import mpu.core.ARG;
import mpu.IT;
import mpc.env.APP;
import mpc.exception.FIllegalStateException;
import mpc.rfl.R;
import mpc.types.tks.FID;
import mpe.NT;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;

public class NetSrcId {

	public static final Logger L = LoggerFactory.getLogger(NetSrcId.class);

	public static NetSrcId of(Long src_uid, boolean... checkExist) {
		NetSrcId src = new NetSrcId(src_uid, (String) null, null);
		return ARG.isDefEqTrue(checkExist) ? checkExist(src) : src;
	}

	public static NetSrcId of(NT nt, long src_nid, boolean... checkExist) {
		return of(nt, String.valueOf(src_nid), checkExist);
	}

	public static NetSrcId of(NT nt, String src_nid, boolean... checkExist) {
		NetSrcId src = new NetSrcId(null, nt.uidStr(), src_nid);
		return ARG.isDefEqTrue(checkExist) ? checkExist(src) : src;
	}

	public static NetSrcId checkExist(NetSrcId usr) {
		IT.NE(usr.getNid());
		return usr;
	}

	@Getter
	protected String name;

	@Getter
	protected Long sid;

	@Getter
	protected String guid;

	protected String net;

	@Getter
	protected String nid;

	private static <U> Constructor<U> getDefConstructor(Class<U> clazz) throws NoSuchMethodException {
		return clazz.getDeclaredConstructor(Long.class, String.class, String.class);
	}

	@SneakyThrows
	@NotNull
	public static <U extends NetSrcId> U newId(Class<U> clazz, String net_uid, String user_nid, boolean... checkExist) {
		U u = getDefConstructor(clazz).newInstance(null, net_uid, user_nid);
		if (ARG.isDefEqTrue(checkExist)) {
			checkExist(u);
		}
		return u;
	}

	@SneakyThrows
	@NotNull
	public static <U extends NetSrcId> U newId(Class<U> clazz, Long user_uid, boolean... checkExist) {
		U u = getDefConstructor(clazz).newInstance(user_uid, null, null);
		if (ARG.isDefEqTrue(checkExist)) {
			checkExist(u);
		}
		return u;
	}

	public NetSrcId(Long sid, String net, String nid) {
		this.sid = sid;
		this.nid = nid;
		this.net = net;
		IT.notNullAny(sid, nid);
	}

	public String getNetUid() {
		return net;
	}

	public void checkDonorNet(NT net) {
		IT.isEq(net.uid, getNetUid());
	}

	public NT getNetType() {
		return NT.ofUid(getNetUid());
	}

	public NT getAppType() {
		String appName = APP.getAppName(null);
		if (appName != null) {
			return NT.valueOf(appName.toUpperCase());
		}
		throw new FIllegalStateException("Override method NetSrcId#getAppType or set 'app.name' in application.properties");
	}

	public String getNetFidString() {
		return FID.to(getNetUid(), getNid());
	}

	public FID getMainAppFid() {
		return FID.of(getMainNetAppName(), getSid());
	}


	public String getMainNetAppName() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String toString() {
		return R.sn(getClass()) + "{" +
				"name='" + name + '\'' +
				", nt=" + net +
				", sid=" + sid +
				", nid='" + nid + '\'' +
				'}';
	}

	public boolean equals(long nt_uid, String nid) {
		return getNetFidString().equals(FID.to(nt_uid, nid));
	}
}
