package mp.utl_odb.netapp.usr;

import mpc.arr.Arr;
import mpc.types.tks.FID;
import mpt.IUserNet;
import mpt.IaUser;

import java.util.List;

//NetAbstractUsewr
public interface InaUser extends IaUser {
	@Override
	default List<IUserNet> getUserNets() {
		return Arr.as(new IUserNet() {
			@Override
			public String getNt() {
				return InaUser.this.getNt();
			}

			@Override
			public String getNid() {
				return InaUser.this.getUserNID();
			}
			@Override
			public String getUserUid() {
				return String.valueOf(InaUser.this.getUserUid());
			}
		});
	}

	@Override
	default FID getFID(String NET, FID... defRq) {
		return IaUser.super.getFID(NET, defRq);
	}

	String getNt();

	String getUserNID();

	default String toStringId() {
		return toStringId(this);
	}

	public static String toStringId(InaUser inaUser) {
		return toStringId(inaUser.getNt(), inaUser.getUserNID());
	}

	static String toStringId(String net, String user_nid) {
		return net + "*" + user_nid;
	}

	String getUserName(int... length);

}
