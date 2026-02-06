package mpf.contract;

import mpc.map.MapTableContract;

import java.io.Serializable;
import java.lang.reflect.Proxy;
import java.util.Map;

public interface IContract extends Serializable {
	Map<String, Object> mapc();//return clean map with properties

	Map<String, Object> mapdb();//return original map

	default MapTableContract.GetAndDefMapInvocationHandler proxy() {
		MapTableContract.GetAndDefMapInvocationHandler h = (MapTableContract.GetAndDefMapInvocationHandler) Proxy.getInvocationHandler(this);
		return h;
	}

}
