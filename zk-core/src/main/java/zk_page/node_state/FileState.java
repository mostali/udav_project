package zk_page.node_state;

import mpc.exception.RequiredRuntimeException;
import mpc.fs.UFS;
import mpc.fs.path.IPath;
import mpc.json.GsonMap;
import mpc.str.ObjTo;
import mpu.IT;
import mpu.X;
import mpu.core.ARG;
import mpu.core.RW;
import mpu.pare.Pare;
import mpu.str.SPLIT;
import mpu.str.STR;
import mpu.str.UST;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.HtmlBasedComponent;
import zk_page.ZKS;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class FileState<PATH> extends EntityState implements IPath {
	protected final String pathFcStr;
	protected transient Path pathFc;

	public Path pathFc() {
		return pathFc == null ? pathFc = Paths.get(pathFcStr) : pathFc;
	}

	public FileState(Pare sdn, String pathFcStr, boolean isForm) {
		super(sdn, isForm);
		this.pathFcStr = pathFcStr;
	}

	public Path pathPropsNodeDir() {
		return pathProps().getParent();
	}

	public boolean isJson() {
		return isForm ? false : true;
	}

	public boolean isForm() {
		return isForm;
	}

	@Override
	public Path fPath() {
		return pathPropsNodeDir();
	}

	public PATH getPropsOrCreate() {
		return (PATH) readGsonMapOrCreate(pathFc(), isForm);
	}

	public PATH getProps() {
		return (PATH) readGsonMap(pathFc(), isForm);
	}

	//
	public EntityState writeFcDataWithClean(String data) {
		return writeFcData(STR.trim(data));
	}

	public EntityState writeFcData(String data) {
		RW.write(pathFc(), data, true);
		return this;
	}

//
	//
	// PROP > STATE

	public void updateProp_STATE(Object state) {
		updatePropSingle(PK_STATE, state);
	}

	public void updateProp_TITLE(Object state) {
		updatePropSingle(PK_TITLE, state);
	}

	public String getProp_STATE(String... defRq) {
		return getAs(PK_STATE, String.class, defRq);
	}

	public String getProp_USER(String... defRq) {
		return getAs(PK_USER, String.class, defRq);
	}

	public String getProp_TITLE(String... defRq) {
		return getAs(PK_TITLE, String.class, defRq);
	}

	public String getProp_VIEW(String... defRq) {
		return getAs(PK_VIEW, String.class, defRq);
	}

	public <T> T getPropAs_STATE(Class<T> asType, T... defRq) {
		return getAs(PK_STATE, asType, defRq);
	}

	//
	//
	// PROP'S ENTITY IMPL

	public boolean existProps() {
		return existProps(pathFc(), isForm);
	}

	public String readFcData(String... defRq) {
		return RW.readContent(pathFc(), defRq);
	}

	public List<String> readFcDataAsLines(List<String>... defRq) {
		return RW.readLines(pathFc(), defRq);
	}

	public GsonMap getPropsJson(GsonMap... defRq) {
		Path fileCom = pathFc();
		try {
			return readGsonMap(fileCom, isForm);
		} catch (Exception ex) {
			return ARG.toDefThrow(() -> new RequiredRuntimeException(ex, "Except state json from file '%s'", fileCom), defRq);
		}
	}

	public Path pathProps() {
		return propsPath(pathFc(), isForm);
	}

	//
	//
	// CONTRACT impl

	@Override
	public void updatePropSingle(String prop, Object value) {
		updatePropSingle(pathFc(), isForm, prop, value);
	}

	@Override
	public String get(String prop, String... defRq) {
		return getAs(prop, String.class, defRq);
	}

	@Override
	public <T> T getAs(String prop, Class<T> asType, T... defRq) {
		return getAs(pathFc(), isForm, prop, asType, defRq);
	}

	@Override
	public Boolean hasPropEnable(String prop, Boolean... defRq) {
		return hasEnable(pathFc(), isForm, prop, defRq);
	}

	//
	//

	public void updatePropsFromCom(HtmlBasedComponent com, String... props) {
		updatePropsFromCom(com, pathFc(), isForm, props);
	}

	//
	//
	// APPLY

	static boolean apply(HtmlBasedComponent com, Path fileCom, boolean isForm, String... props) {
		IT.notEmpty(props, "set style applied props");
		GsonMap applyGsonMap = readGsonMap(fileCom, isForm, null);
		if (X.empty(applyGsonMap)) {
			return false;
		}
		boolean change = false;
		for (String prop : props) {
			boolean rslt = ZKS.applyProp(com, applyGsonMap, prop);
			change = rslt ? true : change;
		}
		return change;
	}

	//
	//
	// GET

	public static Boolean has(Path fileCom, boolean isForm, String key, Boolean... defRq) {
		GsonMap applyGsonMap = readGsonMap(fileCom, isForm, null);
		if (applyGsonMap != null) {
			return applyGsonMap.containsKey(key);
		}
		return ARG.toDefThrow(() -> new RequiredRuntimeException("Com '%s' GsonMap by key '%s' is required has", fileCom, key), defRq);
	}

	public static Boolean hasEnable(Path fileCom, boolean isForm, String key, Boolean... defRq) {
		Object o = get0(fileCom, isForm, key, defRq);
		if (o != null) {
			Boolean b = ObjTo.objTo(o, Boolean.class, null);
			return b != null ? b : ARG.toDefThrow(() -> new RequiredRuntimeException("Com '%s' GsonMap by key '%s' is required boolean", fileCom, key), defRq);
		}
		return ARG.toDefThrow(() -> new RequiredRuntimeException("Com '%s' GsonMap by key '%s' is required boolean(not null)", fileCom, key), defRq);
	}

	public static Integer getPX(Path fileCom, boolean isForm, String key, Integer... defRq) {
		String vl = get(fileCom, isForm, key, null);
		if (ZKS.isPx(vl)) {
			return UST.INT(STR.substr(vl, -2));
		}
		return ARG.toDefThrow(() -> new RequiredRuntimeException("Com '%s' GsonMap by key '%s' is required px value", fileCom, key), defRq);
	}

	public static String get(Path fileCom, boolean isForm, String key, String... defRq) {
		return getAs(fileCom, isForm, key, String.class, defRq);
	}

	public static <T> T getAs(Path fileCom, boolean isForm, String key, Class<T> asType, T... defRq) {
		Object propVl = get0(fileCom, isForm, key, null);
		if (propVl != null) {
			return ObjTo.objTo(propVl, asType, defRq);
		}
		return ARG.toDefThrow(() -> new RequiredRuntimeException("Com '%s' GsonMap by key '%s' is required not nul object", fileCom, key), defRq);
	}

	public static Object get0(Path fileCom, boolean isForm, String key, Object... defRq) {
		GsonMap applyGsonMap = readGsonMap(fileCom, isForm, null);
		if (applyGsonMap != null) {
			return applyGsonMap.get(key);
		}
		return ARG.toDefThrow(() -> new RequiredRuntimeException("Com '%s' GsonMap by key '%s' is required", fileCom, key), defRq);
	}


	//
	// UPDATE/WRITE

	private static void updatePropSingle(Path pathCom, boolean isForm, String prop, Object value) {
		GsonMap applyGsonMap = readGsonMapOrCreate(pathCom, isForm);
		if (value == null) {
			Object remove = applyGsonMap.remove(prop);
			if (L.isDebugEnabled()) {
				L.debug("SormState key '{}' removed : {}", prop, remove);
			}
		} else {
			Object prev = applyGsonMap.put(prop, value);
			if (L.isDebugEnabled()) {
				L.debug("SormState key '{}' put : {}. Prev: {} ", prop, value, prev);
			}
		}
		write(pathCom, isForm, applyGsonMap);
	}

	private static void updatePropsFromCom(HtmlBasedComponent com, Path pathCom, boolean isForm, String... props) {
		IT.notEmpty(props, "set style updatable props");
		GsonMap applyGsonMap = readGsonMapOrCreate(pathCom, isForm);
		for (String prop : props) {
			ZKS.putProp(com, applyGsonMap, prop);
		}
		write(pathCom, isForm, applyGsonMap);
	}

	public static void write(Path pathCom, boolean isForm, GsonMap applyGsonMap) {
		GsonMap.write(propsPath(pathCom, isForm), applyGsonMap);
	}

	//
	// READ

	public static boolean existProps(Path pathCom, boolean isForm) {
		return UFS.existFile(propsPath(pathCom, isForm));
	}

	public static GsonMap readGsonMap(Path fileCom, boolean isForm, GsonMap... defRq) {
		return GsonMap.of(propsPath(fileCom, isForm), defRq);
	}

	public static GsonMap readGsonMapOrCreate(Path fileCom, boolean isForm) {
		return GsonMap.of(propsPath(fileCom, isForm), true);
	}

	public static Path propsPath(Path fileCom, boolean isForm) {
		return isForm ? fileCom.getParent().resolve(fileCom.getFileName() + "..") : fileCom;
	}


}
