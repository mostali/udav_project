package mp.utl_odb.tree.ctxdb;

import mpu.X;
import mpu.core.ARG;
import mpu.core.IEnum;

public enum OCols implements IEnum {
	key, value, ext, o1, o2, o3, o4, o5, o6, o7, o8, o9, o10;

	public <T> T getFieldValueAs(Ctx5Db.CtxModel m, Class<T> asType, T[] defRq) {
		boolean def = ARG.isDef(defRq);
		String nameField = name();
		Object o;
		if (def) {
			o = m.getAs(nameField, asType, null);
		} else {
			o = m.getAs(nameField, asType);
		}
		return !def ? (T) o : ARG.throwMsg(() -> X.f("Except key [%s] value", nameField), defRq);
	}
}
