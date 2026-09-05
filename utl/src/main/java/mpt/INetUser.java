package mpt;


import mpc.types.tks.FID;

import java.io.Serializable;

public interface INetUser extends Serializable {
	String getNt();

	String getNid();

	String getUserSid();

	default FID FID() {
		return FID.of(getNt(), getNid());
	}
}
