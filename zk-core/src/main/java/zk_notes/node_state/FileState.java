package zk_notes.node_state;

import com.google.common.collect.Multimap;
import lombok.SneakyThrows;
import mpc.exception.RequiredRuntimeException;
import mpc.fs.UFS;
import mpc.fs.path.IPath;
import mpc.json.GsonMap;
import mpc.str.ObjTo;
import mpc.types.ruprops.RuProps;
import mpc.types.ruprops.URuProps;
import mpe.core.ERR;
import mpu.IT;
import mpu.X;
import mpu.core.ARG;
import mpu.core.ARGn;
import mpu.core.RW;
import mpu.pare.Pare;
import mpu.str.STR;
import mpu.str.UST;
import org.zkoss.zk.ui.HtmlBasedComponent;
import zk_notes.factory.NFStyle;
import zk_os.coms.AFCC;
import zk_os.sec.SecApp;
import zk_page.ZKS;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

public abstract class FileState<PATH> extends EntityState implements IPath {

	private static final int FILE_OUT_OK = 1;
	private static final int FILE_OUT_ERR = 2;


	protected final String pathFcStr;

	protected transient Path pathFc;

	public static Path getPartPathFor(Path file, int... ind) {
		String fn = getPartNameFor(file.getFileName().toString(), ind);
		return file.getParent().resolve(fn);
	}

	public static String getPartNameFor(String fn, int... ind) {
		return fn + "." + ARGn.toDefOr(1, ind) + ".$$";
	}

	public Path pathFc(int ind) {
		Path pathFc = pathFc();
		String fn = pathFc.getFileName().toString();
		return pathFc.getParent().resolve(getPartNameFor(fn, ind));
	}

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

	public Path toPathDir() {
		return toPath();
	}

	@Deprecated //use toPathDir
	@Override
	public Path toPath() {
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
		return writeFcData(STR.trimSimple(data));
	}

	public EntityState writeFcData(String data) {
		RW.write(pathFc(), data, true);
		return this;
	}

	public EntityState writeFcDataOk(String data, Object... args) {
		return writeFcData(X.f(data, args), FILE_OUT_OK);
	}

	public EntityState writeFcDataErr(Throwable err) {
		writeFcDataErr(ERR.getStackTrace(err));
		return this;
	}

	public EntityState writeFcDataErr(String data, Object... args) {
		return writeFcData(X.f(data, args), FILE_OUT_ERR);
	}

	@SneakyThrows
	public EntityState appendFcData(String data, int index) {
		RW.write_AppendOrCreateNew_(pathFc(index), data);
		return this;
	}

	@SneakyThrows
	public EntityState appendFcDataOk(String data) {
		return appendFcData(data, FILE_OUT_OK);
	}

	@SneakyThrows
	public EntityState appendFcDataErr(String msg, Throwable err) {
		appendFcDataErr(msg);
		appendFcDataErr(ERR.getMessagesAsStringWithHead(err, msg));
		return this;
	}

	@SneakyThrows
	public EntityState appendFcDataErr(String data) {
		return appendFcData(data, FILE_OUT_ERR);
	}

	public EntityState writeFcData(String data, int index) {
		RW.write(pathFc(index), data, true);
		return this;
	}

	public void deletePathFc_OkErr() {
		deletePathFc(FILE_OUT_OK);
		deletePathFc(FILE_OUT_ERR);
	}

	public void deletePathFc(int index) {
		RW.deleteFile(pathFc(index));
	}

	public void deletePathFc() {
		RW.deleteFile(pathFc());
	}

	//
	//
	// PROP > STATE

	public void set_STATE(Object state) {
		set(PK_STATE, state);
	}

	public void set_TITLE(Object state) {
		set(PK_TITLE, state);
	}

	public String get_STATE(String... defRq) {
		return getAs(PK_STATE, String.class, defRq);
	}

	public String get_USER(String... defRq) {
		return getAs(SecApp.USER, String.class, defRq);
	}

	public String get_TITLE(String... defRq) {
		return getAs(PK_TITLE, String.class, defRq);
	}

	public String get_VIEW(String... defRq) {
		return getAs(PK_VIEW, String.class, defRq);
	}

	public <T> T getAs_STATE(Class<T> asType, T... defRq) {
		return getAs(PK_STATE, asType, defRq);
	}

	//
	//
	// PROP'S ENTITY IMPL

	public boolean existPropsFile() {
		return existPropsFile(pathFc(), isForm);
	}

	public boolean emptyDataProps() {
		return X.empty(readPropsData(null));
	}

	public boolean emptyData() {
		return X.empty(readFcData(null));
	}

	public boolean emptyData(int index) {
		return X.empty(readFcData(index, null));
	}

	public String readFcDataLine(int lineNum, String... defRq) {
		return RW.readLineOpt(pathFc(), lineNum, defRq);
	}

	public String readFcDataOk(String... defRq) {
		return readFcData(FILE_OUT_OK, defRq);
	}

	public String readFcDataErr(String... defRq) {
		return readFcData(FILE_OUT_ERR, defRq);
	}

	public String readFcData(int index, String... defRq) {
		return RW.readString(pathFc(index), defRq);
	}

	public String readFcData(String... defRq) {
		return RW.readString(pathFc(), defRq);
	}

	public String readPropsData(String... defRq) {
		return RW.readString(pathProps(), defRq);
	}

	public List<String> readFcDataAsLines(List<String>... defRq) {
		return RW.readLines(pathFc(), defRq);
	}

	public RuProps readFcDataAsRuProps() {
		return RW.readRuProps(pathFc());
	}

	public Multimap readFcDataAsMmap() {
		return URuProps.getRuPropertiesMultiMap(pathFc());
	}

	public GsonMap readFcDataAsGsonMap(GsonMap... defRq) {
		Path fileCom = pathFc();
		try {
			return readGsonMap(fileCom, isForm);
		} catch (Exception ex) {
			return ARG.toDefThrow(() -> new RequiredRuntimeException(ex, "Except state json from file '%s'", fileCom), defRq);
		}
	}

	public Path pathProps() {
		return toPathPropsFromPathFc(pathFc(), isForm);
	}

	//
	//
	// CONTRACT impl

	@Override
	public void set(String prop, Object value) {
		setPropSingle(pathFc(), isForm, prop, value);
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

	public void setFromCom(HtmlBasedComponent com, String... props) {
		setFromCom(com, pathFc(), isForm, props);
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

			boolean rslt = NFStyle.applyProp(com, applyGsonMap, prop);

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
			return UST.INT(STR.substrCount(vl, -2));
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

	private static void setPropSingle(Path pathCom, boolean isForm, String prop, Object value) {
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

	private static void setFromCom(HtmlBasedComponent com, Path pathCom, boolean isForm, String... props) {
		IT.notEmpty(props, "set style updatable props");
		GsonMap applyGsonMap = readGsonMapOrCreate(pathCom, isForm);
		for (String prop : props) {
			ZKS.putProp(com, applyGsonMap, prop);
		}
		write(pathCom, isForm, applyGsonMap);
	}

	public static void write(Path pathCom, boolean isForm, GsonMap applyGsonMap) {
		GsonMap.write(toPathPropsFromPathFc(pathCom, isForm), applyGsonMap);
	}

	//
	// READ

	public static boolean existPropsFile(Path pathCom, boolean isForm) {
		return UFS.existFile(toPathPropsFromPathFc(pathCom, isForm));
	}

	public static GsonMap readGsonMap(Path fileCom, boolean isForm, GsonMap... defRq) {
		return GsonMap.of(toPathPropsFromPathFc(fileCom, isForm), defRq);
	}

	public static GsonMap readGsonMapOrCreate(Path fileCom, boolean isForm) {
		return GsonMap.of(toPathPropsFromPathFc(fileCom, isForm), true);
	}

	public static Path toPathPropsFromPathFc(Path fileCom, boolean isForm) {
		return isForm ? fileCom.getParent().resolve(fileCom.getFileName() + AFCC.PROPS_FILE_EXT) : fileCom;
	}

	private Optional<String> nodeDataCached;

	public String nodeDataCached(boolean... fresh) {
		if (ARG.isDefEqTrue(fresh)) {
			nodeDataCached = null;
		}
		if (nodeDataCached != null) {
			return nodeDataCached.orElse(null);
		}
		Optional<String> opt = nodeDataCached = Optional.ofNullable(nodeData(null));
		return opt.orElse(null);
	}

	public String nodeData(String... defRq) {
		return readFcData(defRq);
	}

	public String nodeLine(int lineNum, String... defRq) {
		return readFcDataLine(lineNum, defRq);
	}


}
