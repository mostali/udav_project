package mp.utl_odb.tree;

import mp.utl_odb.tree.trees.UTreeL;
import mpc.fs.RW;
import mpc.ERR;
import mpc.X;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Objects;

public abstract class CachedValueCtx<T extends Serializable> {

	public static final Logger L = LoggerFactory.getLogger(CachedValueCtx.class);

	final CtxtlDb.TimeAccess timeAccess;
	final String key;

	public CachedValueCtx(String key, CtxtlDb.TimeAccess timeAccess) {
		this.key = ERR.notEmpty(key);
		this.timeAccess = timeAccess;
	}

	public abstract T loadFreshData() throws Exception;

	public T getCachedValue() throws Exception {
		try {

			UTreeL.getL_TLA(key, timeAccess);

			String str = getFreshValueAsString();
			T decodedValue = (T) RW.Serializable2String.deserializable(str);
			if (L.isDebugEnabled()) {
				L.debug("Cache-Value is found. cache-key '{}', size {}:", key, X.sizeOf(Objects.toString(decodedValue, null)));
			}
			return decodedValue;

		} catch (CtxtlDb.UtreeDelayException e) {
			if (L.isDebugEnabled()) {
				if (L.isTraceEnabled()) {
					L.trace("Read&Load cached value:" + e.getMessage(), e);
				} else {
					L.debug("Read&Load cached value:" + e.getMessage());
				}
			}
			String val = UTreeL.getL(key);
			T type = (T) RW.Serializable2String.deserializable(val);
			return type;
		}
	}

	protected String getFreshValueAsString() throws Exception {
		if (L.isTraceEnabled()) {
			L.trace("Load fresh value, cache-key '{}':", key);
		}
		T freshVal = ERR.NN(loadFreshData(), "getValueImpl=null");
		if (L.isDebugEnabled()) {
			L.debug("Load fresh value is SUCCESS , cache-key '{}', size {}:", key, X.sizeOf(Objects.toString(freshVal, null)));
		}
		String coded = RW.Serializable2String.serializable(freshVal);
		UTreeL.setL(key, coded);
		if (L.isTraceEnabled()) {
			L.trace("Store loaded fresh value is success, cache-key '{}':", key);
		}
		return coded;
	}
}
