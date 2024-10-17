package mpt;


import mpc.types.tks.FID;

import java.io.Serializable;

public interface IUserNet extends Serializable {
	String getNt();

	String getNid();

	String getUserUid();

	default FID FID() {
		return FID.of(getNt(), getNid());
	}
}
