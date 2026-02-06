package mp.utl_odb.netapp.usr;

import mpu.core.ARR;
import mpc.types.tks.FID;
import mpt.INetUser;
import mpt.IaUser;

import java.util.List;

//NetAbstractUsewr
public interface InaUser extends IaUser {
	@Override
	default List<INetUser> getUserNets() {
		return ARR.as(new INetUser() {
			@Override
			public String getNt() {
				return InaUser.this.getNt();
			}

			@Override
			public String getNid() {
				return InaUser.this.getUserNID();
			}

			@Override
			public String getUserSid() {
				return String.valueOf(InaUser.this.getUserSid());
			}
		});
	}


	String getUserName(int... length);

	default boolean hasNetNid() {
		return getNt() != null && getUserNID() != null;
	}

	@Override
	default FID getFID(String NET, FID... defRq) {
		return IaUser.super.getFID(NET, defRq);
	}

	String getNt();

	String getUserNID();

//	default String toStringNetId() {
//		return toStringNetId(this);
//	}

	default String toStringSidNamed() {
		return getUserSid() + "#" + getUserName();
	}

	static String toStringNetId(InaUser inaUser) {
		return toStringNetId(inaUser.getNt(), inaUser.getUserNID());
	}

	static String toStringNetId(String net, String user_nid) {
		return net + "*" + user_nid;
	}


}
