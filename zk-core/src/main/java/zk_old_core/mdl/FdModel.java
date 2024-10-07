package zk_old_core.mdl;

import lombok.SneakyThrows;
import mpu.core.ARG;
import mpu.IT;
import mpc.exception.RequiredRuntimeException;
import mpc.exception.WhatIsTypeException;
import mpc.fs.ext.EXT;
import mpu.core.RW;
import mpc.types.ruprops.RuProps;
import mpc.json.GsonMap;
import mpc.map.UMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.Component;
import zk_page.ZKC;

import java.io.Serializable;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public abstract class FdModel implements Serializable {

	public static final Logger L = LoggerFactory.getLogger(FdModel.class);
	public static final String SKIP_PFX = "--";

	public void setAttributeTo(Component com) {
		setAttributeTo(com, getClass().getSimpleName());
	}

	public void setAttributeTo(Component com, String key) {
		ZKC.setAttributeToCom(com, key, rootFd);
	}

	public static FdModel getAny(String name, FdModel... defRq) {
		PageDirModel pageDirModel = PageDirModel.get(null);
		if (pageDirModel != null) {
			FdModel frm = pageDirModel.findFormModel(name, null);
			if (frm != null) {
				return frm;
			}
		}
		if (ARG.isDef(defRq)) {
			return ARG.toDef(defRq);
		}
		throw new RequiredRuntimeException("Form '%s' not found from page '%s'", name, pageDirModel == null ? null : pageDirModel.name());
	}

	public static <F extends FdModel> F get(Class<F> class_form, String name, F... defRq) {
		FdModel fdm = FdModel.getAny(name, null);
		if (fdm != null) {
			if (class_form == FormDirModel.class) {
				return (F) fdm;
			} else if (class_form == FormFileModel.class) {
				return (F) fdm;
			} else {
				throw new WhatIsTypeException(class_form);
			}
		}
		if (ARG.isDef(defRq)) {
			return ARG.toDef(defRq);
		}
		PageDirModel pageDirModel = PageDirModel.get(null);
		throw new RequiredRuntimeException("Form '%s:%s' not found from page '%s'", class_form.getClass().getSimpleName(), name, pageDirModel == null ? null : pageDirModel.name());
	}

	public String name() {
		return path().getFileName().toString();
	}

	private final String rootFd;
	private transient Path pathFd;

	public Path path() {
		return pathFd == null ? pathFd = Paths.get(rootFd) : pathFd;
	}

	public Path path(String child) {
		return path().resolve(child);
	}

	public String getFileData() {
		return RW.readContent(path());
	}

	public String rootFd() {
		return rootFd;
	}

	public FdModel(Path rootFilePath) {
		this.rootFd = rootFilePath.toString();
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "{" + "rootFd=" + rootFd + '}';
	}

	private Map rootProps;

	public Map<String, Object> getRootProps() {
		return rootProps != null ? rootProps : (rootProps = loadMap());
	}

	public Map<String, Object> getRootPropsChildOrCreate(String rootChildJson) {
		Map<String, Object> map = getRootProps();
		Object childJson = map.get(rootChildJson);
		if (childJson == null) {
			map.put(rootChildJson, childJson = new HashMap());
		} else {
			IT.isType0(childJson, Map.class);
		}
		return getRootPropsChild(rootChildJson);
	}

	public Map<String, Object> getRootPropsChild(String rootChildJson, Map<String, Object>... defRq) {
		return UMap.getAs(getRootProps(), rootChildJson, Map.class, defRq);
	}

	@SneakyThrows
	public void map_write(Boolean mkdirs_mkdir_orNot) {
		Path path = getFileRootProps();
		EXT of = EXT.of(path, EXT.$$$UND_EXT$$$);
		switch (of) {
			case JSON:
				GsonMap.write(path, (GsonMap) getRootProps(), true, false);
				break;
			case PROPERTIES:
				//return (Map) RW.readProperties(path);
			case PROPS:
				RuProps.writeMap(path, getRootProps(), mkdirs_mkdir_orNot);
				break;
			default:
				throw new WhatIsTypeException("What is type '%s' from path '%s'?", of, path);
		}
	}

	@SneakyThrows
	public Map<String, Object> loadMap() {
		Path path = getFileRootProps();
		EXT of = EXT.of(path, EXT.$$$UND_EXT$$$);
		switch (of) {
			case JSON:
				return GsonMap.read(path, true);
			case PROPERTIES:
//				return (Map) RW.readProperties(path);
			case PROPS:
				return (Map) RuProps.of(path).readMap_(true, false);
			default:
				throw new WhatIsTypeException("What is type '%s' from path '%s'?", of, path);
		}
	}

	public abstract Path getFileRootProps();

	public boolean isFile() {
		return this instanceof FormFileModel;
	}

	/**
	 * *************************************************************
	 * ----------------------------- SET ----------------------------
	 * *************************************************************
	 */

	private void set(Map<String, Object> props, String key, Object value, boolean write) {
		props.put(key, value);
		if (write) {
			map_write(true);
		}
	}

	public void setRootProperty(String key, Object value, boolean write) {
		set(getRootProps(), key, value, write);
	}

	public void setRootPropertyIn(String rootChildJson, String key, Object value, boolean write) {
		set(getRootPropsChildOrCreate(rootChildJson), key, value, write);
	}

	/**
	 * *************************************************************
	 * ----------------------------- GET ----------------------------
	 * *************************************************************
	 */

	public String getRootProperty(String key, String... defRq) {
		return UMap.get((Map) getRootProps(), key, defRq);
	}

	public String getRootPropertyIn(String rootChildJson, String key, String... defRq) {
		Map rootPropsChild = getRootPropsChild(rootChildJson, null);
		if (rootPropsChild != null) {
			return UMap.get(rootPropsChild, key, defRq);
		}
		return ARG.toDefThrow(() -> new RequiredRuntimeException("Root Child Json '%s' not found", rootPropsChild), defRq);
	}

	/**
	 * *************************************************************
	 * ----------------------------- GET TYPE ----------------------------
	 * *************************************************************
	 */

	public <T> T getRootPropertyAs(String key, Class<T> type, T... defRq) {
		return UMap.getAs(getRootProps(), key, type, defRq);
	}

	public <T> T getRootPropertyInAs(String rootChildJson, String key, Class<T> type, String... defRq) {
		return UMap.getAs(getRootPropsChild(rootChildJson), key, type, defRq);
	}
}
