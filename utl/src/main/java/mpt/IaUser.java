package mpt;

import mpu.X;
import mpu.core.ARR;
import mpu.core.ARG;
import mpc.exception.RequiredRuntimeException;
import mpc.types.tks.FID;

import java.util.List;
import java.util.Optional;

//AbstractUser
public interface IaUser extends IUser {

	default List<INetUser> getUserNets() {
		return ARR.as();
	}

	static String toString(IaUser usr) {
		return usr == null ? null : usr.getClass().getSimpleName() + "&" + usr.getUserSid();
	}

	static IaUser def() {
		return fake(0L);
	}

	static IaUser fake(long sid) {
		return () -> sid;
	}

	static IaUser fake(long sid, String net, String nid) {
		return new IaUser() {
			@Override
			public Long getUserSid() {
				return sid;
			}

			@Override
			public List<INetUser> getUserNets() {
				return ARR.as(new INetUser() {
					@Override
					public String getNt() {
						return net;
					}

					@Override
					public String getNid() {
						return nid;
					}

					@Override
					public String getUserSid() {
						return sid + "";
					}
				});
			}
		};
	}

	default FID firstFID(FID... defRq) {
		List<INetUser> userNets = getUserNets();
		Optional<INetUser> first = userNets.stream().filter(un -> X.NE(un.getNt())).findFirst();
		if (first.isPresent()) {
			return first.get().FID();
		}
		return ARG.toDefThrow(() -> new RequiredRuntimeException("User '%s' not found from any nets*%s", getUserSid(), X.sizeOf(userNets)), defRq);
	}

	default FID getFID(String NET, FID... defRq) {
		try {
			return getNetOf(NET).FID();
		} catch (Exception ex) {
			return ARG.toDefThrow(ex, defRq);
		}
	}

	default INetUser getNetOf(String NET, INetUser... defRq) {
		Optional<INetUser> first = getUserNets().stream().filter(n -> n.getNt().equals(NET)).findFirst();
		return ARG.toDefThrowOpt(() -> new RequiredRuntimeException("User '%s' Net '%s' not found", getUserSid(), NET), first, defRq);
	}

}
