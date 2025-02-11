package mpe.wthttp;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import mpc.fs.UF;
import mpc.fs.UFS;
import mpc.fs.ext.EXT;
import mpc.fs.path.IPath;
import mpc.map.MAP;
import mpc.rfl.RFL;
import mpe.rt.Thread0;
import mpu.X;
import mpu.core.ARG;
import mpu.core.RW;
import mpu.pare.Pare;
import mpu.str.STR;
import mpu.str.USToken;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroovyCallMsg extends CallMsg {

	public static final String KEY = "//groovy";

//	public final Map<String, Object> headersParams;
//	public final @Getter Multimap context;

	public static boolean isValidKeyFirstLine(String data) {
		return STR.startsWith(data, KEY);
	}

	public static boolean isValid(String data) {
		return GroovyCallMsg.ofQk(data).isValid();
	}

	@Override
	public String type() {
		return KEY;
	}


	public GroovyCallMsg(String fullMsg, boolean... lazyValid) {
		super(fullMsg, true);

		switch (state) {
			case EMPTY:
//				headersParams = null;
//				context = null;
				addError("Empty msg");
				return;

			case LINE:
//				headersParams = new HashMap<>();
//				context = ArrayListMultimap.create();
				break;

			default:
			case BODY:
//				headersParams = getHeadersAsMap_All();
//				context = getBodyAsPropertiesMultimap();
				break;
		}


	}


	@Override
	public String toString() {
		return "GroovyCallMsg{" +
//				"msg='" + fullMsg + '\'' +
//				", class='" + className + '\'' + ", method='" + classMethodName + '\'' + ", headers=" + headersParams + ", context=" + context +
				", errs=" + X.sizeOf0(getErrors()) + '}';
	}


	public static GroovyCallMsg of(IPath file, boolean... lazyValid) {
		return (GroovyCallMsg) ofQk(file, lazyValid).throwIsErr();
	}

	public static GroovyCallMsg ofQk(Path file, boolean... lazyValid) {
		return ofQk(IPath.of(file), lazyValid);
	}

	public static GroovyCallMsg ofQk(IPath file, boolean... lazyValid) {
		return (GroovyCallMsg) of(file.fCat(), lazyValid).setFromSrc(file);
	}

	public static GroovyCallMsg of(Path file, boolean... lazyValid) {
		return of(RW.readContent(file), lazyValid);
	}

	public static GroovyCallMsg of(String msg, boolean... lazyValid) {
		return (GroovyCallMsg) ofQk(msg, lazyValid).throwIsErr();
	}

	public static GroovyCallMsg ofQk(String msg, boolean... lazyValid) {
		return new GroovyCallMsg(msg, lazyValid);
	}


}
