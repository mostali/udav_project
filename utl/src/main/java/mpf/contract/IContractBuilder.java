package mpf.contract;

import mpc.exception.FIllegalStateException;
import mpc.map.IMap;
import mpc.map.MapTableContract;
import mpu.core.ARG;

import java.util.Map;

public interface IContractBuilder {

	default <T extends IContract> T buildContract(Class<T> contract, boolean... markNoRq) {
		return ARG.isDefEqTrue(markNoRq) ? MapTableContract.buildContract_MarkNotRq(getContractDataMap(), contract) : MapTableContract.buildContract_DefRq(getContractDataMap(), contract);
	}

	default Map getContractDataMap() {
		if (this instanceof Map) {
			return (Map) this;
		} else if (this instanceof IMap) {
			return ((IMap) this).toMap();
		}
		throw new FIllegalStateException("Except Object implements Map, but it:%s", this);
	}

}
