package mpt;

import mpc.arr.Arr;
import mpc.args.ARG;
import mpc.exception.RequiredRuntimeException;
import mpc.types.tks.FID;

import java.util.List;
import java.util.Optional;

//AbstractUser
public interface IaUser extends IUser {

	default List<IUserNet> getUserNets() {
		return Arr.as();
	}

	static String toString(IaUser usr) {
		return usr == null ? null : usr.getClass().getSimpleName() + "&" + usr.getUserUid();
	}

	static IaUser def() {
		return fake(0L);
	}

	static IaUser fake(long uid) {
		return () -> uid;
	}

	static IaUser fake(long uid, String net, String nid) {
		return new IaUser() {
			@Override
			public Long getUserUid() {
				return uid;
			}

			@Override
			public List<IUserNet> getUserNets() {
				return Arr.as(new IUserNet() {
					@Override
					public String getNt() {
						return net;
					}

					@Override
					public String getNid() {
						return nid;
					}

					@Override
					public String getUserUid() {
						return uid + "";
					}
				});
			}
		};
	}

	default FID getFID(String NET, FID... defRq) {
		try {
			return getNetOf(NET).FID();
		} catch (Exception ex) {
			return ARG.toDefThrow(ex, defRq);
		}

	}

	default IUserNet getNetOf(String NET, IUserNet... defRq) {
		Optional<IUserNet> first = getUserNets().stream().filter(n -> n.getNt().equals(NET)).findFirst();
		return ARG.toDefThrow(() -> new RequiredRuntimeException("User '%s' Net '%s' not found", getUserUid(), NET), first);
	}

}
