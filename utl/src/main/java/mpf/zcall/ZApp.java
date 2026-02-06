package mpf.zcall;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import mpu.X;
import mpu.core.ARG;

import java.util.List;

@RequiredArgsConstructor
public class ZApp {

	private @Getter
	final List<ZType> zTypes;

	public static ZApp of(List<ZType> zType) {
		return new ZApp(zType);
	}

	@Override
	public String toString() {
		return "ZApp{" +
				"zType=" + zTypes +
				'}';
	}

	public Object invokeWithArgs(String methodName, Object... args) {
		List<ZType> zTypes = getZTypes();
		ZType zType = X.sizeOf0(zTypes) == 1 ? zTypes.get(0) : getLastVersionZType();
		return zType.invokeWithArgs(methodName, args);
	}

	public Object invokeWithArgs1(String methodName, Object arg1) {
		List<ZType> zTypes = getZTypes();
		ZType zType = X.sizeOf0(zTypes) == 1 ? zTypes.get(0) : getLastVersionZType();
		return zType.invokeWithArgs1(methodName, arg1);
	}

	public ZType getLastVersionZType(String... version) {
		List<ZType> zTypes = getZTypes();
		String lastVersionFom = ZType.findLastVersion(zTypes);
		String v0 = ARG.toDefOr(lastVersionFom, version);
		return ZType.getZTypeByVersion(zTypes, v0);
	}


}
